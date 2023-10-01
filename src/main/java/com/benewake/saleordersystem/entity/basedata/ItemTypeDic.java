package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName item_type_dic
 */
@TableName(value ="item_type_dic")
@Data
public class ItemTypeDic implements Serializable {
    private Long itemType;

    private String itemTypeName;

    private static final long serialVersionUID = 1L;
}