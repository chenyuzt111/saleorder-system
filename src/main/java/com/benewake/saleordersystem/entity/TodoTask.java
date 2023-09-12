package com.benewake.saleordersystem.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Zt
 * @since 2023年09月09 10:38
 * 描 述： TODO
 */
@Data
@TableName("fim_todotask_table")
public class TodoTask {
    @TableId(value = "inquiry_id",type = IdType.AUTO)
    private Long inquiryId;

    @TableField("salesman_id")
    private Long salesmanId;

    @TableField("inquiry_code")
    private String inquiryCode;


    @TableField("inquiry_type")
    private String inquiryType;

    @TableField("item_name")
    private String itemName;

    @TableField("customer_name")
    private String fName;

    @TableField("message")
    private String message;





    @TableField("sale_num")
    private Long salenum;



    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date expectedTime;


    private Long delayedTime;

    private String POmessage;




}
