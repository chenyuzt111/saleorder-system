package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Map;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：主要客户现况
 */
@Data
@TableName("past_orders_analysis_8_major_customers_situation")
public class Past_orders_analysis_8_major_customers_situation {
    @TableField("serial_num")
    private Long serialNum;

    @TableField("is_yellow") // 注意字段名称不符合Java标识符规范，使用引号包围
    private int isYellow;

    @TableField("salesman_name")
    private String salesmanName;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("customer_name")
    private String customerName;

    @TableField("sale_times")
    private Integer saleTimes;

    @TableField("total_customer_item_num")
    private Long totalCustomerItemNum;

    @TableField("month_avg")
    private Float monthAvg;

    @TableField("total_item")
    private Long totalItem;

    @TableField("customer_proporation")
    private Float customerProporation;

    @TableField("month_avg_proportion")
    private Float monthAvgProportion;

    // 还需要添加一个字段来表示 "2021/4"
//    private Long year2021Month4;

    @TableField("april2021")
    private Long April2021;

    @TableField("may2021")
    private Long May2021;

    @TableField("june2021")
    private Long June2021;

    @TableField("july2021")
    private Long July2021;

    @TableField("august2021")
    private Long August2021;

    @TableField("september2021")
    private Long September2021;

    @TableField("october2021")
    private Long October2021;

    @TableField("november2021")
    private Long November2021;

    @TableField("december2021")
    private Long December2021;

    @TableField("january2022")
    private Long January2022;

    @TableField("february2022")
    private Long February2022;

    @TableField("march2022")
    private Long March2022;

    @TableField("april2022")
    private Long April2022;

    @TableField("may2022")
    private Long May2022;

    @TableField("june2022")
    private Long June2022;

    @TableField("july2022")
    private Long July2022;

    @TableField("august2022")
    private Long August2022;

    @TableField("september2022")
    private Long September2022;

    @TableField("october2022")
    private Long October2022;

    @TableField("november2022")
    private Long November2022;

    @TableField("december2022")
    private Long December2022;

    @TableField("january2023")
    private Long January2023;

    @TableField("february2023")
    private Long February2023;

    @TableField("march2023")
    private Long March2023;

    @TableField("april2023")
    private Long April2023;

    @TableField("may2023")
    private Long May2023;

    @TableField("june2023")
    private Long June2023;

    @TableField("july2023")
    private Long July2023;

    @TableField("august2023")
    private Long August2023;

}
