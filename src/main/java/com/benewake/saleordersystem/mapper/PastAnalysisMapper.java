package com.benewake.saleordersystem.mapper;

import com.benewake.saleordersystem.entity.Past_analysis.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PastAnalysisMapper {
    @Select("SELECT * FROM past_orders_analysis_1_salesman_selling_condition")
    List<Past_orders_analysis_1_salesman_selling_condition> getAllPastAnalysis();

    @Select("SELECT * FROM past_orders_analysis_2_quarterly_selling_condition")
    List<Past_orders_analysis_2_quarterly_selling_condition> getAllQuarterlySellingCondition();

    @Select("SELECT * FROM past_orders_analysis_3_month_avg_proportion")
    List<Past_orders_analysis_3_month_avg_proportion> getAllMonthAvgProportion();

    @Select("SELECT * FROM past_orders_analysis_4_temp2_eligible_orders")
    List<Past_orders_analysis_4_temp2_eligible_orders> getAllEligibleOrders();

    @Select("SELECT * FROM past_orders_analysis_5_temp3_not_eligible_orders")
    List<Past_orders_analysis_5_temp3_not_eligible_orders> getAllNotEligibleOrders();

    @Select("SELECT * FROM past_orders_analysis_6_salesman_selling_condition_replaced")
    List<Past_orders_analysis_6_salesman_selling_condition_replaced> getAllSellingConditionOrders();

    @Select("SELECT * FROM past_orders_analysis_7_quarterly_selling_condition_replaced")
    List<Past_orders_analysis_7_quarterly_selling_condition_replaced> getAllQuarterlySellingConditionReplaced();

    @Select("SELECT * FROM past_orders_analysis_8_major_customers_situation")
    List<Past_orders_analysis_8_major_customers_situation> getAllMajorCustomersSituation();

    @Select("SELECT * FROM past_orders_analysis_9_product_dimension_situation")
    List<Past_orders_analysis_9_product_dimension_situation> getAllProductDimensionSituation();

    @Select("SELECT * FROM past_orders_analysis_10_retail_condition")
    List<Past_orders_analysis_10_retail_condition> getAllRetailCondition();

    @Select("SELECT * FROM past_orders_analysis_11_retail_quarterly_selling_condition")
    List<Past_orders_analysis_11_retail_quarterly_selling_condition> getAllRetailQuarterlySellingCondition();

    @Select("SELECT * FROM customer_type_orders_replaced")
    List<Past_orders_analysis_12_customer_type_orders> getAllCustomerTypeordersReplaced();

    @Select("SELECT * FROM customer_type_orders_back")
    List<Past_orders_analysis_13_customer_type_orders_back> getAllCustomerTypeOrdersBack();

    @Select("SELECT * FROM past_orders_analysis_14_customer_type_monthly")
    List<Past_orders_analysis_14_customer_type_monthly> getAllCustomerTypeordersMonthlyReplaced();

    @Select("SELECT * FROM past_orders_analysis_15_customer_type_monthly_back")
    List<Past_orders_analysis_15_customer_type_monthly_back> getAllCustomerTypeordersMonthlyBack();





}
