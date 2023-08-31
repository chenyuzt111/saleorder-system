package com.benewake.saleordersystem.excel.model;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

@Data
public class Kingdee {
    @ExcelProperty("单据编号")
    private String order_id;
    @ExcelProperty("单据状态")
    private String order_code;
    @ExcelProperty("销售员")
    private String item_code;
    @ExcelProperty("创建人")
    private String customer_name;
    @ExcelProperty("客户")
    private String saleman_name;
    @ExcelProperty("审核人")
    private String sale_num;

    @ExcelProperty("审核人")
    private String sale_time;
}
