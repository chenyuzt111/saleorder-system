package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerModel {
    @ExcelProperty(value = "客户名称",index = 0)
    private String customerName;
}
