package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerRenameModel {
    @ExcelProperty(value = "客户名称（替换前）",index = 0)
    private String customerNameOld;

    @ExcelProperty(value = "客户名称（替换后）",index = 1)
    private String customerNameNew;
}
