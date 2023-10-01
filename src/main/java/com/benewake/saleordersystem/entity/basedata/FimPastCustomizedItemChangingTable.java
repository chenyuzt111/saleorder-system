package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_past_customized_item_changing_table
 */
@TableName(value ="fim_past_customized_item_changing_table")
@Data
public class FimPastCustomizedItemChangingTable implements Serializable {
    private String customerName;

    private String itemCodeOld;

    private String itemCodeNew;

    private static final long serialVersionUID = 1L;
}