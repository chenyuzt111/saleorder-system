package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName customer_type_dic
 */
@TableName(value ="customer_type_dic")
@Data
public class CustomerTypeDic implements Serializable {
    private String customerType;

    private static final long serialVersionUID = 1L;
}