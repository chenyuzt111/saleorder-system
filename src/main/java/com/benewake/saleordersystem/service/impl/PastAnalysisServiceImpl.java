package com.benewake.saleordersystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.saleordersystem.entity.Past_analysis.*;
import com.benewake.saleordersystem.mapper.PastAnalysisMapper;
import com.benewake.saleordersystem.service.PastAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PastAnalysisServiceImpl implements PastAnalysisService {

    @Autowired
    private PastAnalysisMapper pastAnalysisMapper;


    @Override
    public Page<Past_orders_analysis_1_salesman_selling_condition> getAllPastAnalysis(int pageNum, int pageSize) {
        Page<Past_orders_analysis_1_salesman_selling_condition> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.getAllPastAnalysis1();
        return pastAnalysisMapper.getAllPastAnalysis(page, null);
    }

    @Override
    public Page<Past_orders_analysis_2_quarterly_selling_condition> getAllQuarterlySellingCondition(int pageNum, int pageSize) {
        Page<Past_orders_analysis_2_quarterly_selling_condition> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis2QuarterlySellingCondition();
        return pastAnalysisMapper.getAllQuarterlySellingCondition(page, null);
    }

    @Override
    public Page<Past_orders_analysis_3_month_avg_proportion> getAllMonthAvgProportion(int pageNum, int pageSize) {
        Page<Past_orders_analysis_3_month_avg_proportion> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis3MonthAvgProportion();
        return pastAnalysisMapper.getAllMonthAvgProportion(page, null);
    }

    @Override
    public Page<Past_orders_analysis_4_temp2_eligible_orders> getAllEligibleOrders(int pageNum, int pageSize) {
        Page<Past_orders_analysis_4_temp2_eligible_orders> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis4Temp2EligibleOrders();
        return pastAnalysisMapper.getAllEligibleOrders(page, null);
    }

    @Override
    public Page<Past_orders_analysis_5_temp3_not_eligible_orders> getAllNotEligibleOrders(int pageNum, int pageSize) {
        Page<Past_orders_analysis_5_temp3_not_eligible_orders> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis5Temp3NotEligibleOrders();
        return pastAnalysisMapper.getAllNotEligibleOrders(page, null);
    }

    @Override
    public Page<Past_orders_analysis_6_salesman_selling_condition_replaced> getAllSellingConditionOrders(int pageNum, int pageSize) {
        Page<Past_orders_analysis_6_salesman_selling_condition_replaced> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis6SalesmanSellingConditionReplaced();
        return pastAnalysisMapper.getAllSellingConditionOrders(page, null);
    }

    @Override
    public Page<Past_orders_analysis_7_quarterly_selling_condition_replaced> getAllQuarterlySellingConditionReplaced(int pageNum, int pageSize) {
        Page<Past_orders_analysis_7_quarterly_selling_condition_replaced> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis7QuarterlySellingConditionReplaced();
        return pastAnalysisMapper.getAllQuarterlySellingConditionReplaced(page, null);
    }

    @Override
    public Page<Past_orders_analysis_8_major_customers_situation> getAllMajorCustomersSituation(int pageNum, int pageSize) {
        Page<Past_orders_analysis_8_major_customers_situation> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis8MajorCustomersSituation();
        return pastAnalysisMapper.getAllMajorCustomersSituation(page, null);
    }

    @Override
    public Page<Past_orders_analysis_9_product_dimension_situation> getAllProductDimensionSituation(int pageNum, int pageSize) {
        Page<Past_orders_analysis_9_product_dimension_situation> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis9ProductDismensionSituation();
        return pastAnalysisMapper.getAllProductDimensionSituation(page, null);
    }

    @Override
    public Page<Past_orders_analysis_10_retail_condition> getAllRetailCondition(int pageNum, int pageSize) {
        Page<Past_orders_analysis_10_retail_condition> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis10RetailCondition();
        return pastAnalysisMapper.getAllRetailCondition(page, null);
    }

    @Override
    public Page<Past_orders_analysis_11_retail_quarterly_selling_condition> getAllRetailQuarterlySellingCondition(int pageNum, int pageSize) {
        Page<Past_orders_analysis_11_retail_quarterly_selling_condition> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis11RetailQuarterlySellingCondtion();
        return pastAnalysisMapper.getAllRetailQuarterlySellingCondition(page, null);
    }


    @Override
    public Page<Past_orders_analysis_12_customer_type_orders> getAllCustomerTypeordersReplaced(
            int yearly, int monthly, int agent, int newCustomer, int temporaryCustomer, int daily, int pageNum, int pageSize) {

        Page<Past_orders_analysis_12_customer_type_orders> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis12CustomerTypeOrders(yearly, monthly, agent, newCustomer, temporaryCustomer, daily);
        // 获取分页结果

        // 封装为PageInfo对象，包含分页信息
        return pastAnalysisMapper.getAllCustomerTypeordersReplaced(page, null);
    }

    @Override
    public Page<Past_orders_analysis_13_customer_type_orders_back> getAllCustomerTypeOrdersBack(int yearly, int monthly, int agent, int newCustomer, int temporaryCustomer, int daily, int pageNum, int pageSize) {
        Page<Past_orders_analysis_13_customer_type_orders_back> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.doPastOrdersAnalysis13CustomerTypeOrdersBack(yearly, monthly, agent, newCustomer, temporaryCustomer, daily);
        return pastAnalysisMapper.getAllCustomerTypeOrdersBack(page, null);
    }

    @Override
    public Page<Past_orders_analysis_14_customer_type_monthly> getAllCustomerTypeordersMonthlyReplaced(int yearly, int monthly, int agent, int newCustomer, int temporaryCustomer, int daily, int pageNum, int pageSize) {
        Page<Past_orders_analysis_14_customer_type_monthly> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.past_orders_analysis_14_customer_type_monthly(yearly, monthly, agent, newCustomer, temporaryCustomer, daily);
        return pastAnalysisMapper.getAllCustomerTypeordersMonthlyReplaced(page, null);
    }

    @Override
    public Page<Past_orders_analysis_15_customer_type_monthly_back> getAllCustomerTypeordersMonthlyBack(int yearly, int monthly, int agent, int newCustomer, int temporaryCustomer, int daily, int pageNum, int pageSize) {
        Page<Past_orders_analysis_15_customer_type_monthly_back> page = new Page<>(pageNum, pageSize);
        pastAnalysisMapper.past_orders_analysis_15_customer_type_monthly_back(yearly, monthly, agent, newCustomer, temporaryCustomer, daily);
        return pastAnalysisMapper.getAllCustomerTypeordersMonthlyBack(page, null);
    }
}
