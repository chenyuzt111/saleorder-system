package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value ="fim_item_table")
@Data
public class FimItemTable {
    private int itemId;

    private String itemCode;

    private String itemName;

    private int itemType;

    private int Quantitative;
}
