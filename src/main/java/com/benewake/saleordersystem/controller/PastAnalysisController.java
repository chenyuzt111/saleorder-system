package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_1_salesman_selling_condition;
import com.benewake.saleordersystem.service.PastAnalysisService;
import com.benewake.saleordersystem.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/past-analysis")
public class PastAnalysisController {

    private final PastAnalysisService pastAnalysisService;

    @Autowired
    public PastAnalysisController(PastAnalysisService pastAnalysisService) {
        this.pastAnalysisService = pastAnalysisService;
    }

    @GetMapping("/{methodName}")
    public Result getAllData(@PathVariable String methodName) {
        Object result = null;

        switch (methodName) {
            case "getAllPastAnalysis":
                result = pastAnalysisService.getAllPastAnalysis();
                break;
            case "getAllQuarterlySellingCondition":
                result = pastAnalysisService.getAllQuarterlySellingCondition();
                break;
            case "getAllMonthAvgProportion":
                result = pastAnalysisService.getAllMonthAvgProportion();
                break;
            case "getAllEligibleOrders":
                result = pastAnalysisService.getAllEligibleOrders();
                break;
            case "getAllNotEligibleOrders":
                result = pastAnalysisService.getAllNotEligibleOrders();
                break;
            case "getAllSellingConditionReplaced":
                result = pastAnalysisService.getAllSellingConditionOrders();
                break;
            case "getAllQuarterlySellingConditionReplaced":
                result = pastAnalysisService.getAllQuarterlySellingConditionReplaced();
                break;
            case "getAllMajorCustomersSituation":
                result = pastAnalysisService.getAllMajorCustomersSituation();
                break;
            case "getAllProductDimensionSituation":
                result = pastAnalysisService.getAllProductDimensionSituation();
                break;
            case "getAllRetailCondition":
                result = pastAnalysisService.getAllRetailCondition();
                break;
            case "getAllRetailQuarterlySellingCondition":
                result = pastAnalysisService.getAllRetailQuarterlySellingCondition();
                break;
            case "getAllCustomerTypeOrdersReplaced":
                result = pastAnalysisService.getAllCustomerTypeordersReplaced();
                break;
            case "getAllCustomerTypeOrdersBack":
                result = pastAnalysisService.getAllCustomerTypeOrdersBack();
                break;
            case "getAllCustomerTypeordersMonthlyReplaced":
                result = pastAnalysisService.getAllCustomerTypeordersMonthlyReplaced();
                break;
            case "getAllCustomerTypeordersMonthlyBack":
                result = pastAnalysisService.getAllCustomerTypeordersMonthlyBack();
                break;
            default:
                return Result.fail(400, "Invalid method name");
        }

        if (result != null) {
            return Result.success(200, "Query successful", result);
        } else {
            return Result.fail(500, "Error in query execution");
        }
    }
}
