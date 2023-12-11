package com.benewake.saleordersystem.service;

import com.benewake.saleordersystem.entity.Past_analysis.*;

import java.util.List;

public interface PastAnalysisService {
    List<Past_orders_analysis_1_salesman_selling_condition> getAllPastAnalysis();
    List<Past_orders_analysis_2_quarterly_selling_condition> getAllQuarterlySellingCondition();
    List<Past_orders_analysis_3_month_avg_proportion> getAllMonthAvgProportion();
    List<Past_orders_analysis_4_temp2_eligible_orders> getAllEligibleOrders();
    List<Past_orders_analysis_5_temp3_not_eligible_orders> getAllNotEligibleOrders();
    List<Past_orders_analysis_6_salesman_selling_condition_replaced> getAllSellingConditionOrders();
    List<Past_orders_analysis_7_quarterly_selling_condition_replaced> getAllQuarterlySellingConditionReplaced();
    List<Past_orders_analysis_8_major_customers_situation> getAllMajorCustomersSituation();
    List<Past_orders_analysis_9_product_dimension_situation> getAllProductDimensionSituation();
    List<Past_orders_analysis_10_retail_condition> getAllRetailCondition();
    List<Past_orders_analysis_11_retail_quarterly_selling_condition> getAllRetailQuarterlySellingCondition();
    List<Past_orders_analysis_12_customer_type_orders> getAllCustomerTypeordersReplaced(int yearly,int monthly,int agent,int newCustomer,int temporaryCustomer,int daily);
    List<Past_orders_analysis_13_customer_type_orders_back> getAllCustomerTypeOrdersBack(int yearly,int monthly,int agent,int newCustomer,int temporaryCustomer,int daily);

    List<Past_orders_analysis_14_customer_type_monthly> getAllCustomerTypeordersMonthlyReplaced();
    List<Past_orders_analysis_15_customer_type_monthly_back> getAllCustomerTypeordersMonthlyBack();

}
