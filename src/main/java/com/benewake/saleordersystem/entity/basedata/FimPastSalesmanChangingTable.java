package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_past_salesman_changing_table
 */
@TableName(value ="fim_past_salesman_changing_table")
@Data
public class FimPastSalesmanChangingTable implements Serializable {
    private String salesmanNameOld;

    private String salesmanNameNew;

    private static final long serialVersionUID = 1L;
}