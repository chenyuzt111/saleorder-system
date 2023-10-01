package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_past_customer_rename_table
 */
@TableName(value ="fim_past_customer_rename_table")
@Data
public class FimPastCustomerRenameTable implements Serializable {
    private String customerNameOld;

    private String customerNameNew;

    private static final long serialVersionUID = 1L;
}