package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_past_item_change_table
 */
@TableName(value ="fim_past_item_change_table")
@Data
public class FimPastItemChangeTable implements Serializable {
    private String itemCodeOld;

    private String itemCodeNew;

    private static final long serialVersionUID = 1L;
}