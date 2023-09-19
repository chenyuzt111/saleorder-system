package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：季度销售现况
 */
@Data
@TableName("past_orders_analysis_7_quarterly_selling_condition_replaced")
public class Past_orders_analysis_7_quarterly_selling_condition_replaced {
    @TableField("serial_num")
    private Long serialNum;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("sale_year")
    private Integer saleYear;

    @TableField("sale_quarter")
    private Integer saleQuarter;

    @TableField("quarter_item_sale_num")
    private Long quarterItemSaleNum;

    @TableField("quarter_avg")
    private Float quarterAvg;

    @TableField("month_avg")
    private Float monthAvg;
}
