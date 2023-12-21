package com.benewake.saleordersystem.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.benewake.saleordersystem.entity.basedata.FimCustomerTypeTable;
import com.benewake.saleordersystem.entity.basedata.FimPastCustomizedItemChangingTable;
import com.benewake.saleordersystem.entity.basedata.FimPastSalesmanChangingTable;
import com.benewake.saleordersystem.excel.model.CustomizedItemChangeModel;
import com.benewake.saleordersystem.excel.model.SalesmanChangingTableModel;
import com.benewake.saleordersystem.service.DeliveryService;
import com.benewake.saleordersystem.service.InquiryService;
import com.benewake.saleordersystem.service.UserService;
import com.benewake.saleordersystem.utils.BenewakeConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class CustomizedItemChangeListener extends AnalysisEventListener<CustomizedItemChangeModel> implements BenewakeConstants {
    private List<FimPastCustomizedItemChangingTable> lists = new ArrayList<>();
    private Map<String,Object> map = new HashMap<>();
    private List<CustomizedItemChangeModel> existList;
    private InquiryService inquiryService;
    private UserService userService;
    private DeliveryService deliveryService;

    public CustomizedItemChangeListener(UserService userService, List<FimPastCustomizedItemChangingTable> existList) {
        this.userService = userService;


    }
    private static List<String> head = new ArrayList<>();
    static {
        head.add("客户名称");
        head.add("物料编码（替换前）");
        head.add("物料编码（替换后）");

    }



    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        // 判断列名是否一致
        for(int i=0;i<head.size(); i++){
            String h;
            if((h = headMap.get(i))==null || !h.equals(head.get(i))){
                map.put("error","第"+(i+1)+"列的列名不符合条件，应改为:"+head.get(i));
                throw new ExcelAnalysisStopException();
            }
        }
    }

    @Override
    public void invoke(CustomizedItemChangeModel customizedItemChangeModel, AnalysisContext analysisContext) {
        //log.info("解析到一条数据："+inquiryModel.toString());
        // 获取行号
        ReadRowHolder readRowHolder = analysisContext.readRowHolder();
        Integer rowIndex = readRowHolder.getRowIndex();
        // 检查数据是否有效
        map = userService.checkCustomizedItemChangeByExcel(customizedItemChangeModel,rowIndex);
        if(!map.containsKey("fimPastCustomizedItemChangingTable")){
            // 无效 抛出异常 结束操作
            throw new ExcelAnalysisStopException();
        }

        // 有效 加入集合 等全部解析完后存入数据库
        FimPastCustomizedItemChangingTable fimPastCustomizedItemChangingTable = (FimPastCustomizedItemChangingTable) map.get("fimPastCustomizedItemChangingTable");

        lists.add(fimPastCustomizedItemChangingTable);
        //log.info("第"+rowIndex+"行添加完成: "+inquiry.toString());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        try {
            for (FimPastCustomizedItemChangingTable fimPastCustomizedItemChangingTable:lists){
                String customerName = fimPastCustomizedItemChangingTable.getCustomerName();
                String itemCodeNew = fimPastCustomizedItemChangingTable.getItemCodeNew();
                String itemCodeOld = fimPastCustomizedItemChangingTable.getItemCodeOld();
                userService.addCustomizedItemChange(customerName,itemCodeNew,itemCodeOld);
            }

        }catch (Exception e) {
            e.printStackTrace();
            map.put("error","持久化失败");
            throw new ExcelAnalysisException("持久化失败");
        }
        map.put("success","全部数据导入成功！");
    }




}

