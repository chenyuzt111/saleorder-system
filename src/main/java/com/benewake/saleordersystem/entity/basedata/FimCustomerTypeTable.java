package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_customer_type_table
 */
@TableName(value ="fim_customer_type_table")
@Data
public class FimCustomerTypeTable implements Serializable {
    private String customerName;

    private String itemCode;

    private String customerType;

    private static final long serialVersionUID = 1L;
}