package com.benewake.saleordersystem.entity.Past_analysis;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("past_orders_analysis_4_temp2_eligible_orders")
public class Past_orders_analysis_4_temp2_eligible_orders {
    @TableField("serial_num")
    private Long serialNum;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("sale_num")
    private Long saleNum;

    @TableField("customer_name")
    private String customerName;

    @TableField("salesman_name")
    private String salesmanName;

    @TableField("sale_time")
    private Date saleTime;

    @TableField("order_code")
    private String orderCode;

}
