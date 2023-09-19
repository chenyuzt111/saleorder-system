package com.benewake.saleordersystem.entity.Past_analysis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zt
 * @since 2023年09月13日 11:17
 * * 描 述：月度平均比例现况
 */
@Data
@TableName("past_orders_analysis_3_month_avg_proportion")
public class Past_orders_analysis_3_month_avg_proportion {
    @TableField("serial_num")
    private Long serialNum;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("over_line_percentage")
    private Integer overLinePercentage;

    @TableField("over_line_num")
    private Integer overLineNum;

    @TableField("rest_percentage")
    private Long restPercentage;

    @TableField("rest_num")
    private Float restNum;

    @TableField("month_avg_proportion")
    private Float monthAvgProportion;




}
