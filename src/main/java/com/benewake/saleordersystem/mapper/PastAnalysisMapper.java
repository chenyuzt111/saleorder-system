package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.saleordersystem.entity.Past_analysis.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

@Mapper
public interface PastAnalysisMapper extends BaseMapper<Past_orders_analysis_12_customer_type_orders> {
    @Select("SELECT * FROM past_orders_analysis_1_salesman_selling_condition")
    Page<Past_orders_analysis_1_salesman_selling_condition> getAllPastAnalysis(Page<Past_orders_analysis_1_salesman_selling_condition> page, Object object);
    @Select("CALL past_orders_analysis_1_salesman_selling_condition")
    List<Map<String,Object>> getAllPastAnalysis1();

    @Select("SELECT * FROM past_orders_analysis_2_quarterly_selling_condition")
    Page<Past_orders_analysis_2_quarterly_selling_condition> getAllQuarterlySellingCondition(Page<Past_orders_analysis_2_quarterly_selling_condition> page, Object object);
    @Select({"call past_orders_analysis_2_quarterly_selling_condition()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis2QuarterlySellingCondition();

    @Select("SELECT * FROM past_orders_analysis_3_month_avg_proportion")
    Page<Past_orders_analysis_3_month_avg_proportion> getAllMonthAvgProportion(Page<Past_orders_analysis_3_month_avg_proportion> page, Object object);
    @Select({"call past_orders_analysis_3_month_avg_proportion()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis3MonthAvgProportion();

    @Select("SELECT * FROM past_orders_analysis_4_temp2_eligible_orders")
    Page<Past_orders_analysis_4_temp2_eligible_orders> getAllEligibleOrders(Page<Past_orders_analysis_4_temp2_eligible_orders> page, Object object);
    @Select({"call past_orders_analysis_4_temp2_eligible_orders()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis4Temp2EligibleOrders();

    @Select("SELECT * FROM past_orders_analysis_5_temp3_not_eligible_orders")
    Page<Past_orders_analysis_5_temp3_not_eligible_orders> getAllNotEligibleOrders(Page<Past_orders_analysis_5_temp3_not_eligible_orders> page, Object object);
    @Select({"call past_orders_analysis_5_temp3_not_eligible_orders()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis5Temp3NotEligibleOrders();

    @Select("SELECT * FROM past_orders_analysis_6_salesman_selling_condition_replaced")
    Page<Past_orders_analysis_6_salesman_selling_condition_replaced> getAllSellingConditionOrders(Page<Past_orders_analysis_6_salesman_selling_condition_replaced> page, Object object);
    @Select({"call past_orders_analysis_6_salesman_selling_condition_replaced()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis6SalesmanSellingConditionReplaced();

    @Select("SELECT * FROM past_orders_analysis_7_quarterly_selling_condition_replaced")
    Page<Past_orders_analysis_7_quarterly_selling_condition_replaced> getAllQuarterlySellingConditionReplaced(Page<Past_orders_analysis_7_quarterly_selling_condition_replaced> page, Object object);
    @Select({"call past_orders_analysis_7_quarterly_selling_condition_replaced()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis7QuarterlySellingConditionReplaced();

    @Select("SELECT * FROM past_orders_analysis_8_major_customers_situation")
    Page<Past_orders_analysis_8_major_customers_situation> getAllMajorCustomersSituation(Page<Past_orders_analysis_8_major_customers_situation> page, Object object);
    @Select({"call past_orders_analysis_8_major_customers_situation()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis8MajorCustomersSituation();

    @Select("SELECT * FROM past_orders_analysis_9_product_dimension_situation")
    Page<Past_orders_analysis_9_product_dimension_situation> getAllProductDimensionSituation(Page<Past_orders_analysis_9_product_dimension_situation> page, Object object);
    @Select({"call past_orders_analysis_9_product_dimension_situation()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis9ProductDismensionSituation();

    @Select("SELECT * FROM past_orders_analysis_10_retail_condition")
    Page<Past_orders_analysis_10_retail_condition> getAllRetailCondition(Page<Past_orders_analysis_10_retail_condition> page, Object object);
    @Select({"call past_orders_analysis_10_retail_condition()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis10RetailCondition();

    @Select("SELECT * FROM past_orders_analysis_11_retail_quarterly_selling_condition")
    Page<Past_orders_analysis_11_retail_quarterly_selling_condition> getAllRetailQuarterlySellingCondition(Page<Past_orders_analysis_11_retail_quarterly_selling_condition> page, Object object);
    @Select({"call past_orders_analysis_11_retail_quarterly_selling_condition()"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String, Object>> doPastOrdersAnalysis11RetailQuarterlySellingCondtion();

    @Select("SELECT * FROM past_orders_analysis_12_customer_type_orders")
    Page<Past_orders_analysis_12_customer_type_orders> getAllCustomerTypeordersReplaced(Page<Past_orders_analysis_12_customer_type_orders> page, Object object);
    @Select({"call past_orders_analysis_12_customer_type_orders(#{yearly,mode=IN}," +
            "#{monthly,mode=IN},#{agent,mode=IN},#{newCustomer,mode=IN},#{temporaryCustomer,mode=IN},#{daily,mode=IN})"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis12CustomerTypeOrders(@Param("yearly")int yearly,
                                                                      @Param("monthly")int monthly,@Param("agent")int agent,
                                                                      @Param("newCustomer")int newCustomer,@Param("temporaryCustomer")int temporaryCustomer,
                                                                      @Param("daily")int daily);

    @Select("SELECT * FROM past_orders_analysis_13_customer_type_orders_back")
    Page<Past_orders_analysis_13_customer_type_orders_back> getAllCustomerTypeOrdersBack(Page<Past_orders_analysis_13_customer_type_orders_back> page, Object object);
    @Select({"call past_orders_analysis_13_customer_type_orders_back(#{yearly,mode=IN}," +
            "#{monthly,mode=IN},#{agent,mode=IN},#{newCustomer,mode=IN},#{temporaryCustomer,mode=IN},#{daily,mode=IN})"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> doPastOrdersAnalysis13CustomerTypeOrdersBack(@Param("yearly")int yearly,
                                                                          @Param("monthly")int monthly,@Param("agent")int agent,
                                                                          @Param("newCustomer")int newCustomer,@Param("temporaryCustomer")int temporaryCustomer,
                                                                          @Param("daily")int daily);


    @Select("SELECT * FROM past_orders_analysis_14_customer_type_monthly")
    Page<Past_orders_analysis_14_customer_type_monthly> getAllCustomerTypeordersMonthlyReplaced(Page<Past_orders_analysis_14_customer_type_monthly> page, Object object);

    @Select({"call Past_orders_analysis_14_customer_type_monthly(#{yearly,mode=IN}," +
            "#{monthly,mode=IN},#{agent,mode=IN},#{newCustomer,mode=IN},#{temporaryCustomer,mode=IN},#{daily,mode=IN})"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> past_orders_analysis_14_customer_type_monthly(@Param("yearly")int yearly,
                                                                      @Param("monthly")int monthly,@Param("agent")int agent,
                                                                      @Param("newCustomer")int newCustomer,@Param("temporaryCustomer")int temporaryCustomer,
                                                                      @Param("daily")int daily);
    @Select("SELECT * FROM past_orders_analysis_15_customer_type_monthly_back")
    Page<Past_orders_analysis_15_customer_type_monthly_back> getAllCustomerTypeordersMonthlyBack(Page<Past_orders_analysis_15_customer_type_monthly_back> page, Object object);

    @Select({"call past_orders_analysis_15_customer_type_monthly_back(#{yearly,mode=IN}," +
            "#{monthly,mode=IN},#{agent,mode=IN},#{newCustomer,mode=IN},#{temporaryCustomer,mode=IN},#{daily,mode=IN})"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String,Object>> past_orders_analysis_15_customer_type_monthly_back(@Param("yearly")int yearly,
                                                                           @Param("monthly")int monthly,@Param("agent")int agent,
                                                                           @Param("newCustomer")int newCustomer,@Param("temporaryCustomer")int temporaryCustomer,
                                                                           @Param("daily")int daily);



}
