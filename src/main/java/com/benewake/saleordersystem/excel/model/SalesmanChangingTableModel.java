package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SalesmanChangingTableModel {
    @ExcelProperty(value = "销售员名称（替换前）",index = 0)
    private String salesman_name_old;
    @ExcelProperty(value = "销售员名称（替换后）",index = 1)
    private String salesman_name_new;
}
