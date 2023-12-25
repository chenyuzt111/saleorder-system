package com.benewake.saleordersystem.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.localdatetime.LocalDateTimeDateConverter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Lcs
 * @since 2023年07月09 10:15
 * 描 述： TODO
 */
@Data
public class InquiryModel {
    @ExcelProperty(value = "单据类型*", index = 0)
    private String inquiryType;
    @ExcelProperty(value = "物料编码*", index = 1)
    private String itemCode;
    @ExcelProperty(value = "数量*", index = 2)
    private String num;
    @ExcelProperty(value = "客户名称*", index = 3)
    private String customerName;
    @ExcelProperty(value = "销售员*", index = 4)
    private String salesmanName;
    @ExcelProperty(value = "期望发货日期（XD订单可为空）", index = 5)
    private String exceptedTime;
    @ExcelProperty(value = "备注", index = 6)
    private String remark;


}
