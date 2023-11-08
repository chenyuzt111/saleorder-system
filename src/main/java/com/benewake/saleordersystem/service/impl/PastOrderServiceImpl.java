package com.benewake.saleordersystem.service.impl;

import com.benewake.saleordersystem.entity.Past.PastOrder;
import com.benewake.saleordersystem.entity.Past.SaleOut;
import com.benewake.saleordersystem.entity.Past.Withdraw;
import com.benewake.saleordersystem.excel.model.Kingdee;
import com.benewake.saleordersystem.mapper.PastOrderMapper;
import com.benewake.saleordersystem.mapper.StoredProceduresMapper;
import com.benewake.saleordersystem.service.KingDeeService;
import com.benewake.saleordersystem.service.PastOrderService;
import com.benewake.saleordersystem.utils.CommonUtils;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Lcs
 * @since 2023年07月15 16:20
 * 描 述： TODO
 */
@Service
public class PastOrderServiceImpl implements PastOrderService {
    @Autowired
    private PastOrderMapper pastOrderMapper;
    @Autowired
    private KingDeeService kingDeeService;
    @Autowired
    private StoredProceduresMapper storedProceduresMapper;

    volatile private static Long updateTime = 0L;


    /*
    * 将获取到的销售出库列表数据转化为PastOrder类型对象并存入列表最后返回列表
    * */
    @Override
    public List<PastOrder> transferSaleOutToPastOrder(List<SaleOut> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<PastOrder> pastOrders = new ArrayList<>(list.size());
        list.forEach(l->{
            try {
                pastOrders.add(new PastOrder(l.getFMaterialID(),l.getFRealQty(),
                        l.getFCustomerID(),l.getFSalesManID(),sdf.parse(l.getFDate()),l.getFSoorDerno()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        return pastOrders;
    }


    /*
     * 将获取到的退货出库数据转化为PastOrder类型对象并存入列表最后返回列表
     * */
    @Override
    public List<PastOrder> transferWithdrawToPastOrder(List<Withdraw> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<PastOrder> pastOrders = new ArrayList<>(list.size());
        list.forEach(l->{
            try {
                pastOrders.add(new PastOrder(l.getFMaterialId(),l.getFRealQty(),
                        l.getFRetcustId(),l.getFSalesManId(),sdf.parse(l.getFDate()),l.getFOrderNo()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        return pastOrders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)//定义一个事务，如果发生异常直接回滚
    public int savePastOrder(boolean reloadAll) {
        // 取数限制
        int num = Integer.MAX_VALUE;

        int res;
        // type = 0 重新导入数据
        //创建一个ReentrantLock实例，在多线程环境下保护一段代码的互斥执行
        ReentrantLock lock = new ReentrantLock();
        //创建一个日期格式化对象，用于将日期格式化为指定格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取锁，进去互斥区域
        lock.lock();
        try {
            //如果reloadAll为true或updateTime为0执行重新加载本地数据方法reloadLocalOrders()
            if(reloadAll || updateTime.equals(0L)){
                pastOrderMapper.reloadLocalOrders();
                updateTime = 0L;
            }
            //根据当前的时间获取格式化后的日期字符串
            String d = sdf.format(new Date(updateTime));
            //调用金蝶方法获取销售出库列表1
            val saleOuts1 = kingDeeService.searchSaleOutList1(num,d);//num最大限制，d日期
            //将销售出库列表2添加到出库列表1中
            saleOuts1.addAll(kingDeeService.searchSaleOutList2(num,d));
            //调用金蝶方法获取退货出库列表1
            val withdraws1 = kingDeeService.searcWithdrawList1(num,d);
            //将退货出库列表2添加到退货出库列表1中
            withdraws1.addAll(kingDeeService.searcWithdrawList2(num,d));

            //创建一个新的PastOrder类型列表，用于存储历史订单数据
            List<PastOrder> list = new ArrayList<>();
            //将获取到的列表数据转化为列表对象存入到新创建的列表
            list.addAll(transferSaleOutToPastOrder(saleOuts1));
            list.addAll(transferWithdrawToPastOrder(withdraws1));
            // list是PastOrder类型类型的数据，里面有空值字段，避免出错将空值替换为特定的默认值
            list.forEach(p->{
                if(null == p.getFSalesManID()){
                    p.setFSalesManID("");
                }
                if(null == p.getFMaterialID()){
                    p.setFMaterialID("");
                }
                if(null == p.getFSoorDerno()){
                    p.setFSoorDerno("");
                }
                if(null == p.getFRealQty()){
                    p.setFRealQty("0");
                }
                if(null == p.getFCustomerID()){
                    p.setFCustomerID("");
                }

            });


            //调用pastOrderMapper.insertPastOrders()方法，将刚刚创建的列表插入到数据库中
            res = pastOrderMapper.insertPastOrders(list);
            //将时间设置为当前的时间
            updateTime = System.currentTimeMillis();
            //重新加载过去订单分析的临时表
            storedProceduresMapper.doReloadPastOrdersAnalysisTempTables(365,1.5,2);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            //在finally中无论如何最终都会释放锁
            lock.unlock();
        }
        return res;
    }
}
