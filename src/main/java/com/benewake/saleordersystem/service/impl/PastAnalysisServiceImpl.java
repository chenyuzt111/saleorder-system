package com.benewake.saleordersystem.service.impl;

import com.benewake.saleordersystem.entity.Past_analysis.*;
import com.benewake.saleordersystem.mapper.PastAnalysisMapper;
import com.benewake.saleordersystem.service.PastAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PastAnalysisServiceImpl implements PastAnalysisService {

    @Autowired
    private  PastAnalysisMapper pastAnalysisMapper;



    @Override
    public List<Past_orders_analysis_1_salesman_selling_condition> getAllPastAnalysis() {
        pastAnalysisMapper.getAllPastAnalysis1();
        return pastAnalysisMapper.getAllPastAnalysis();
    }

    @Override
    public List<Past_orders_analysis_2_quarterly_selling_condition> getAllQuarterlySellingCondition() {
        pastAnalysisMapper.doPastOrdersAnalysis2QuarterlySellingCondition();
        return pastAnalysisMapper.getAllQuarterlySellingCondition();
    }

    @Override
    public List<Past_orders_analysis_3_month_avg_proportion> getAllMonthAvgProportion() {
        pastAnalysisMapper.doPastOrdersAnalysis3MonthAvgProportion();
        return pastAnalysisMapper.getAllMonthAvgProportion();
    }

    @Override
    public List<Past_orders_analysis_4_temp2_eligible_orders> getAllEligibleOrders() {
        pastAnalysisMapper.doPastOrdersAnalysis4Temp2EligibleOrders();
        return pastAnalysisMapper.getAllEligibleOrders();
    }
    @Override
    public List<Past_orders_analysis_5_temp3_not_eligible_orders> getAllNotEligibleOrders() {
        pastAnalysisMapper.doPastOrdersAnalysis5Temp3NotEligibleOrders();
        return pastAnalysisMapper.getAllNotEligibleOrders();
    }
    @Override
    public List<Past_orders_analysis_6_salesman_selling_condition_replaced> getAllSellingConditionOrders() {
        pastAnalysisMapper.doPastOrdersAnalysis6SalesmanSellingConditionReplaced();
        return pastAnalysisMapper.getAllSellingConditionOrders();
    }

    @Override
    public List<Past_orders_analysis_7_quarterly_selling_condition_replaced> getAllQuarterlySellingConditionReplaced() {
        pastAnalysisMapper.doPastOrdersAnalysis7QuarterlySellingConditionReplaced();
        return pastAnalysisMapper.getAllQuarterlySellingConditionReplaced();
    }

    @Override
    public List<Past_orders_analysis_8_major_customers_situation> getAllMajorCustomersSituation() {
        pastAnalysisMapper.doPastOrdersAnalysis8MajorCustomersSituation();
        return pastAnalysisMapper.getAllMajorCustomersSituation();
    }

    @Override
    public List<Past_orders_analysis_9_product_dimension_situation> getAllProductDimensionSituation() {
        pastAnalysisMapper.doPastOrdersAnalysis9ProductDismensionSituation();
        return pastAnalysisMapper.getAllProductDimensionSituation();
    }

    @Override
    public List<Past_orders_analysis_10_retail_condition> getAllRetailCondition() {
        pastAnalysisMapper.doPastOrdersAnalysis10RetailCondition();
        return pastAnalysisMapper.getAllRetailCondition();
    }

    @Override
    public List<Past_orders_analysis_11_retail_quarterly_selling_condition> getAllRetailQuarterlySellingCondition() {
        pastAnalysisMapper.doPastOrdersAnalysis11RetailQuarterlySellingCondtion();
        return pastAnalysisMapper.getAllRetailQuarterlySellingCondition();
    }

    @Override
    public List<Past_orders_analysis_12_customer_type_orders> getAllCustomerTypeordersReplaced(int yearly,int monthly,int agent,int newCustomer,int temporaryCustomer,int daily) {
        pastAnalysisMapper.doPastOrdersAnalysis12CustomerTypeOrders(yearly,monthly,agent,newCustomer,temporaryCustomer,daily);
        return pastAnalysisMapper.getAllCustomerTypeordersReplaced();
    }

    @Override
    public List<Past_orders_analysis_13_customer_type_orders_back> getAllCustomerTypeOrdersBack(int yearly,int monthly,int agent,int newCustomer,int temporaryCustomer,int daily) {
        pastAnalysisMapper.doPastOrdersAnalysis13CustomerTypeOrdersBack(yearly,monthly,agent,newCustomer,temporaryCustomer,daily);
        return pastAnalysisMapper.getAllCustomerTypeOrdersBack();
    }

    @Override
    public List<Past_orders_analysis_14_customer_type_monthly> getAllCustomerTypeordersMonthlyReplaced() {
        return pastAnalysisMapper.getAllCustomerTypeordersMonthlyReplaced();
    }

    @Override
    public List<Past_orders_analysis_15_customer_type_monthly_back> getAllCustomerTypeordersMonthlyBack() {
        return pastAnalysisMapper.getAllCustomerTypeordersMonthlyBack();
    }
}
