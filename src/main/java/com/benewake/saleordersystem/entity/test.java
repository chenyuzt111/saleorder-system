package com.benewake.saleordersystem.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class test {
    @ExcelProperty("物料编码")
    private String FMaterialId;

    @ExcelProperty("归还日期")
    private String F_ora_BackDate;

    @ExcelProperty("物料ID")
    private String FStatus;
    @ExcelProperty("物料编码")
    private String FPlanFinishDate;


}
