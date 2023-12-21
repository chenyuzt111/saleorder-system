package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName fim_past_choose_item_table
 */
@TableName(value ="fim_past_choose_item_table")
@Data
public class FimPastChooseItemTable implements Serializable {
    private String itemCode;

    private String itemName;

    private Date startMonth;

    private static final long serialVersionUID = 1L;
}