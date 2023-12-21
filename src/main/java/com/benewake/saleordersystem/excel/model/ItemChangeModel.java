package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ItemChangeModel {
    @ExcelProperty(value = "物料编码（替换前）",index = 0)
    private String itemCodeOld;
    @ExcelProperty(value = "物料编码（替换后）",index = 1)
    private String itemCodeNew;
}
