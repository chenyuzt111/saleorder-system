package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：客户类型分类-订单版（已还原）
 */
@Data
@TableName("customer_type_orders_back")
public class Past_orders_analysis_13_customer_type_orders_back {
    @TableField("order_id")
    private Long orderId;

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

    @TableField("customer_type")
    private String customerType;
}
