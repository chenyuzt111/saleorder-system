package com.benewake.saleordersystem.mapper;

import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_1_salesman_selling_condition;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PastAnalysisMapper {
    @Select("SELECT * FROM past_orders_analysis_1_salesman_selling_condition")
    List<Past_orders_analysis_1_salesman_selling_condition> getAllPastAnalysis();
}
