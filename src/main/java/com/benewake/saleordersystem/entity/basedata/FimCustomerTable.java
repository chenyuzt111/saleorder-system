package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_customer_table
 */
@TableName(value ="fim_customer_table")
@Data
public class FimCustomerTable implements Serializable {
    private Long customerId;

    private String customerName;

    private static final long serialVersionUID = 1L;
}