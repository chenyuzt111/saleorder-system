package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ChooseItemModel {
    @ExcelProperty(value = "物料编码",index = 0)
    private String itemCode;
    @ExcelProperty(value = "物料名称",index = 0)
    private String itemName;
    @ExcelProperty(value = "开始时间",index = 0)
    private Date startMonth;

}
