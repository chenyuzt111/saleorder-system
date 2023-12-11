package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：主要客户现况
 */
@Data
@TableName("past_orders_analysis_15_customer_type_monthly_back")
public class Past_orders_analysis_15_customer_type_monthly_back {
    @TableField("serial_num")
    private BigInteger serialNum;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("month_avg")
    private BigDecimal monthAvg;

    @TableField("total_item")
    private BigDecimal totalItem;

    @TableField("total_months")
    private Integer totalMonths;

    @TableField("max_")
    private BigDecimal max;

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

    @TableField("september2023")
    private Long September2023;

    @TableField("october2022")
    private Long October2023;

    @TableField("november2023")
    private Long November2023;

    @TableField("december2023")
    private Long December2023;

}
