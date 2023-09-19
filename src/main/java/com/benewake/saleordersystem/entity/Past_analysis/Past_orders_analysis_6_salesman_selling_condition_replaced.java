package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：替换后的销售员销售现况
 */
@Data
@TableName("past_orders_analysis_6_salesman_selling_condition_replaced")
public class Past_orders_analysis_6_salesman_selling_condition_replaced {
    @TableField("serial_num")
    private Long serialNum;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("salesman_name")
    private String salesmanName;

    @TableField("salesman_sale_num")
    private Long salesmanSaleNum;

    @TableField("item_sale_num")
    private Long itemSaleNum;

    @TableField("sales_share")
    private Float salesShare;

    @TableField("month_avg")
    private Float monthAvg;
}
