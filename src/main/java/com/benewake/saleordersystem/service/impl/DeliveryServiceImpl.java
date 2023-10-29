package com.benewake.saleordersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.saleordersystem.controller.SaleOrderController;
import com.benewake.saleordersystem.entity.Delivery;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.Past.SaleOut;
import com.benewake.saleordersystem.entity.sfexpress.Route;
import com.benewake.saleordersystem.mapper.DeliveryMapper;
import com.benewake.saleordersystem.mapper.InquiryMapper;
import com.benewake.saleordersystem.service.DeliveryService;
import com.benewake.saleordersystem.service.InquiryService;
import com.benewake.saleordersystem.service.KingDeeService;
import com.benewake.saleordersystem.service.SFExpressService;
import com.benewake.saleordersystem.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.benewake.saleordersystem.utils.BenewakeConstants.USER_TYPE_ADMIN;
import static com.benewake.saleordersystem.utils.BenewakeConstants.USER_TYPE_SALESMAN;

@Service
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private InquiryMapper inquiryMapper;

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
        try {
            // 尝试获取没有运输单号或手机号的订单号
            List<Delivery> lists = new ArrayList();
            if (hostHolder.getUser().getUserType() == USER_TYPE_ADMIN) {
                lists = deliveryMapper.selectUnFindDeliveriesByUser1();
            } else if (hostHolder.getUser().getUserType() == USER_TYPE_SALESMAN) {
                lists = deliveryMapper.selectUnFindDeliveriesByUser2(hostHolder.getUser().getId());
            }
            // 获取订单对应的运输单号和手机号
            List<SaleOut> saleOuts = kingDeeService.selectFCarriageNO(lists);
            for (SaleOut s : saleOuts) {

                inquiryMapper.updateInquiryTypeByCode(s.getF_ora_FIMNumber());
            }

            // 在数据库中更新运输单号和手机号
            List<Delivery> nDeliveries = new ArrayList<>();
            saleOuts.forEach(s -> {
                Delivery delivery = new Delivery();
                delivery.setInquiryCode(s.getF_ora_FIMNumber());
                delivery.setDeliveryCode(s.getFCarriageNO());
                delivery.setDeliveryPhone(StringUtils.isBlank(s.getF_ora_Text2()) ? null : s.getF_ora_Text2()
                        .substring(s.getF_ora_Text2().length() - 4));
                nDeliveries.add(delivery);
            });

            // 待添加列表不为空则更新运输单号
            if (!nDeliveries.isEmpty()) {
                deliveryMapper.updateDeliveriesCode(nDeliveries);
            }

            // 获取所有状态未签收的订单信息
            List<Delivery> deliveries = new ArrayList();

            // 如果是管理员获取所有状态未签收的订单信息，销售员只更新自己未签收的订单信息
            if (hostHolder.getUser().getUserType() == USER_TYPE_ADMIN) {
                deliveries = deliveryMapper.selectUnFinisheDeliveriesByUser2();
            } else if (hostHolder.getUser().getUserType() == USER_TYPE_SALESMAN) {
                deliveries = deliveryMapper.selectUnFinisheDeliveriesByUser1(hostHolder.getUser().getId());
            }

            for (Delivery c : deliveries) {
                try {
                    if (!c.getDeliveryCode().equals(" ")) {
                        List<Route> routes = sFExpressService.getLastestRoutesByDelivery(c);

                        if (routes.size() == 1) {
                            Route r = routes.get(0);

                            if (r.getRemark().contains("数字有遗漏，请用户及时修改！")) {
                                c.setDeliveryState(32);
                                c.setFDeliveryIntegrity("不完整");
                            } else {
                                if (r != null) {
                                    if ("80".equals(r.getOpCode())) {
                                        c.setReceiveTime(r.getAcceptTime());
                                        c.setDeliveryState(80);
                                    } else {
                                        c.setDeliveryState(32);
                                    }
                                    c.setFDeliveryIntegrity("完整");
                                }
                            }
                            c.setDeliveryLastestState(r.getRemark());
                            c.setFCountry("国内");
                        } else if (routes.size() > 1) {
                            boolean allOpCode80 = true;
                            boolean allOpCodeNull = true;
                            boolean codeintegrity = true;
                            Date latestAcceptTime = null;
                            String firstRemark = null;

                            for (Route r : routes) {
                                if (r != null) {
                                    if (r.getRemark().contains("数字有遗漏，请用户及时修改！")) {
                                        firstRemark = r.getRemark();
                                        allOpCode80 = false;
                                        allOpCodeNull = false;
                                        codeintegrity = false;
                                        break;
                                    } else {
                                        if (!"80".equals(r.getOpCode())) {
                                            allOpCode80 = false;
                                        }
                                        if (r.getOpCode() != null) {
                                            allOpCodeNull = false;
                                        }
                                        Date acceptTime = r.getAcceptTime();
                                        if (acceptTime != null && (latestAcceptTime == null || acceptTime.compareTo(latestAcceptTime) > 0)) {
                                            latestAcceptTime = acceptTime;
                                        }
                                        if (firstRemark == null && r.getRemark() != null) {
                                            firstRemark = r.getRemark();
                                        }
                                    }
                                }
                            }

                            if (latestAcceptTime != null) {
                                c.setReceiveTime(latestAcceptTime);
                            }

                            if (firstRemark != null) {
                                c.setDeliveryLastestState(firstRemark);
                            }

                            if (allOpCode80) {
                                c.setDeliveryState(80);
                            } else if (allOpCodeNull) {
                                c.setDeliveryState(null);
                            } else {
                                c.setDeliveryState(32);
                            }

                            if (codeintegrity) {
                                c.setFDeliveryIntegrity("完整");
                            } else {
                                c.setFDeliveryIntegrity("不完整");
                            }

                            c.setFCountry("国内");
                        }
                    } else {
                        if (c.getDeliveryPhone() != null) {
                            c.setDeliveryState(32);
                            c.setFDeliveryIntegrity("不完整");
                            c.setDeliveryLastestState("无运输单号，请用户及时修改");
                            c.setFCountry("国内");
                        } else {
                            c.setDeliveryState(40);
                            c.setFDeliveryIntegrity("不完整");
                            c.setDeliveryLastestState("海外订单暂无跟踪");
                            c.setFCountry("海外");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (!deliveries.isEmpty()) {
                // 存入数据库
                deliveryMapper.updateDeliveriesState(deliveries);
                log.info("运输信息更新完成！");
            }
        } catch (Exception e) {
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
        lqw.select(Delivery::getDeliveryCode).like(Delivery::getDeliveryCode, deliveryCode);
        return deliveryMapper.selectList(lqw);
    }

    @Override
    public List<String> getDeliveryStateList(String deliveryState) {
        return deliveryMapper.getDeliveryStateList("%" + deliveryState + "%");
    }

    @Override
    public void updateStatus() {
        List<String> results = deliveryMapper.getNonZeroDeliveryCodes();
        for (String inquirycode : results) {
            Inquiry saleOutInquiries = inquiryService.getInquiriesByCode(inquirycode);
            if (saleOutInquiries == null) {
                if (saleOutInquiries.getInquiryType() != 1) {
                    if (saleOutInquiries == null) {
                        continue;
                    }
                    saleOutInquiries.setInquiryType(1);
                    saleOrderController.updateInquired(saleOutInquiries);
                }
            }
        }
    }
}
