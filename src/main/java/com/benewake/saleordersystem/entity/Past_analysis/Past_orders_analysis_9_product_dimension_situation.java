package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：销售员用产品维度现况
 */
@Data
@TableName("past_orders_analysis_9_product_dimension_situation")
public class Past_orders_analysis_9_product_dimension_situation {
    @TableField("serial_num")
    private Long serialNum;

    @TableField("salesman_name")
    private String salesmanName;

    @TableField("customer_name")
    private String customerName;

    @TableField("over_proportion_num")
    private Long overProportionNum;

    @TableField("one_is_yellow")
    private Integer oneIsYellow;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("times")
    private Integer times;

    @TableField("customer_item_total")
    private Long customerItemTotal;

    @TableField("month_avg")
    private Float monthAvg;

    @TableField("customer_proporation")
    private Float customerProporation;

    @TableField("month_avg_proportion")
    private Float monthAvgProportion;
}
