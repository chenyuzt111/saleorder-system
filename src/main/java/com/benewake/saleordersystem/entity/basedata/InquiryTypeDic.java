package com.benewake.saleordersystem.entity.basedata;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fim_inquiry_type_dic
 */
@TableName(value ="fim_inquiry_type_dic")
@Data
public class InquiryTypeDic implements Serializable {
    private Long inquiryType;

    private String inquiryTypeName;

    private static final long serialVersionUID = 1L;
}