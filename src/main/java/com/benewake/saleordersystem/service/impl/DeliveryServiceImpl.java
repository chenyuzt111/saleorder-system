package com.benewake.saleordersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.saleordersystem.controller.SaleOrderController;
import com.benewake.saleordersystem.entity.Delivery;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.Past.SaleOut;
import com.benewake.saleordersystem.entity.sfexpress.Route;
import com.benewake.saleordersystem.mapper.DeliveryMapper;
import com.benewake.saleordersystem.service.DeliveryService;
import com.benewake.saleordersystem.service.InquiryService;
import com.benewake.saleordersystem.service.KingDeeService;
import com.benewake.saleordersystem.service.SFExpressService;
import com.benewake.saleordersystem.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lcs
 * @since 2023年07月15 09:37
 * 描 述： TODO
 */
@Service
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private KingDeeService kingDeeService;
    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private SaleOrderController saleOrderController;
    @Autowired
    private SFExpressService sFExpressService;
    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean updateDelivery() {
        try{
            // 尝试获取没有运输单号或手机号的订单号
            List<Delivery> lists = deliveryMapper.selectUnFindDeliveriesByUser(hostHolder.getUser().getId());
            // 获取订单对应的运输单号和手机号
            List<SaleOut> saleOuts = kingDeeService.selectFCarriageNO(lists);

//            System.out.println(saleOuts.size());
            // 在数据库中更新运输单号和手机号
            List<Delivery> nDeliveries = new ArrayList<>();
            saleOuts.forEach(s->{
                //System.out.println(s.toString());
                Delivery delivery = new Delivery();
                delivery.setInquiryCode(s.getF_ora_FIMNumber());
                delivery.setDeliveryCode(s.getFCarriageNO());
                delivery.setDeliveryPhone(StringUtils.isBlank(s.getF_ora_Text2())?null:s.getF_ora_Text2()
                        .substring(s.getF_ora_Text2().length()-4));
                nDeliveries.add(delivery);
            });
            // 待添加列表不为空则更新运输单号
            if(!nDeliveries.isEmpty()) {
                deliveryMapper.updateDeliveriesCode(nDeliveries);
            }

            // 获取所有状态未签收的订单信息
            List<Delivery> deliveries = deliveryMapper.selectUnFinisheDeliveriesByUser(hostHolder.getUser().getId());
            deliveries.forEach(System.out::println);
            // 获取最新运输状态 并更新
            deliveries.forEach(c->{
                try {
                    Route r = sFExpressService.getLastestRouteByFCarriageNO(c);
                    System.out.println(r.toString());
                    if(r!=null){
                        if("80".equals(r.getOpCode())){
                            c.setReceiveTime(r.getAcceptTime());
                        }
                        c.setDeliveryState(r.getOpCode()==null?null:Integer.parseInt(r.getOpCode()));
                        c.setDeliveryLastestState(r.getRemark());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            if(!deliveries.isEmpty()){
                // 存入数据库
                deliveryMapper.updateDeliveriesState(deliveries);
                log.info("运输信息更新完成！");
            }
            updateStatus();
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public int insertLists(List<Inquiry> lists) {
        return deliveryMapper.insertLists(lists);
    }

    @Override
    public List<Delivery> getDeliveryCodeList(String deliveryCode) {
        LambdaQueryWrapper<Delivery> lqw = new LambdaQueryWrapper<>();
        lqw.select(Delivery::getDeliveryCode).like(Delivery::getDeliveryCode,deliveryCode);
        return deliveryMapper.selectList(lqw);
    }

    @Override
    public List<String> getDeliveryStateList(String deliveryState) {
        return deliveryMapper.getDeliveryStateList("%"+deliveryState+"%");
    }
    @Override
    public void updateStatus() {
        // 在这里编写您的Service方法的逻辑
        // 可以调用yourMapper中的方法来访问数据库或执行其他操作
        List<String> results = deliveryMapper.getNonZeroDeliveryCodes(); // 替换yourMapperMethod为实际的Mapper方法
        for (String inquirycode : results) {
            // 使用每个SaleOut的code调用getInquiriesByCode方法
            Inquiry saleOutInquiries = inquiryService.getInquiriesByCode(inquirycode);

            // 检查 saleOutInquiries 是否为空，如果为空则跳过当前迭代
            if (saleOutInquiries == null) {
                continue;
            }

            saleOutInquiries.setInquiryType(1);
            saleOrderController.updateInquired(saleOutInquiries);
        }
    }
}
