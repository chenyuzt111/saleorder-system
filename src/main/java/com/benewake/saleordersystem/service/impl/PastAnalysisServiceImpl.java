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
        return pastAnalysisMapper.getAllPastAnalysis();
    }

    @Override
    public List<Past_orders_analysis_2_quarterly_selling_condition> getAllQuarterlySellingCondition() {
        return pastAnalysisMapper.getAllQuarterlySellingCondition();
    }

    @Override
    public List<Past_orders_analysis_3_month_avg_proportion> getAllMonthAvgProportion() {
        return pastAnalysisMapper.getAllMonthAvgProportion();
    }

    @Override
    public List<Past_orders_analysis_4_temp2_eligible_orders> getAllEligibleOrders() {
        return pastAnalysisMapper.getAllEligibleOrders();
    }
    @Override
    public List<Past_orders_analysis_5_temp3_not_eligible_orders> getAllNotEligibleOrders() {
        return pastAnalysisMapper.getAllNotEligibleOrders();
    }
    @Override
    public List<Past_orders_analysis_6_salesman_selling_condition_replaced> getAllSellingConditionOrders() {
        return pastAnalysisMapper.getAllSellingConditionOrders();
    }

    @Override
    public List<Past_orders_analysis_7_quarterly_selling_condition_replaced> getAllQuarterlySellingConditionReplaced() {
        return pastAnalysisMapper.getAllQuarterlySellingConditionReplaced();
    }

    @Override
    public List<Past_orders_analysis_8_major_customers_situation> getAllMajorCustomersSituation() {
        return pastAnalysisMapper.getAllMajorCustomersSituation();
    }

    @Override
    public List<Past_orders_analysis_9_product_dimension_situation> getAllProductDimensionSituation() {
        return pastAnalysisMapper.getAllProductDimensionSituation();
    }

    @Override
    public List<Past_orders_analysis_10_retail_condition> getAllRetailCondition() {
        return pastAnalysisMapper.getAllRetailCondition();
    }

    @Override
    public List<Past_orders_analysis_11_retail_quarterly_selling_condition> getAllRetailQuarterlySellingCondition() {
        return pastAnalysisMapper.getAllRetailQuarterlySellingCondition();
    }

    @Override
    public List<Past_orders_analysis_12_customer_type_orders> getAllCustomerTypeordersReplaced() {
        return pastAnalysisMapper.getAllCustomerTypeordersReplaced();
    }

    @Override
    public List<Past_orders_analysis_13_customer_type_orders_back> getAllCustomerTypeOrdersBack() {
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
