package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomizedItemChangeModel {
    @ExcelProperty(value = "客户名称",index = 0)
    private String customerName;
    @ExcelProperty(value = "物料编码（替换前）",index = 1)
    private String itemCodeOld;
    @ExcelProperty(value = "物料编码（替换后）",index = 2)
    private String itemCodeNew;

}
