package com.benewake.saleordersystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_12_customer_type_orders;
import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_1_salesman_selling_condition;
import com.benewake.saleordersystem.service.PastAnalysisService;
import com.benewake.saleordersystem.utils.Result;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Result getAllData(@PathVariable String methodName, @RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "10") int pageSize) {
        Object result = null;

        switch (methodName) {
            case "getAllPastAnalysis":
                result = pastAnalysisService.getAllPastAnalysis(pageNum, pageSize);
                break;
            case "getAllQuarterlySellingCondition":
                result = pastAnalysisService.getAllQuarterlySellingCondition(pageNum, pageSize);
                break;
            case "getAllMonthAvgProportion":
                result = pastAnalysisService.getAllMonthAvgProportion(pageNum, pageSize);
                break;
            case "getAllEligibleOrders":
                result = pastAnalysisService.getAllEligibleOrders(pageNum, pageSize);
                break;
            case "getAllNotEligibleOrders":
                result = pastAnalysisService.getAllNotEligibleOrders(pageNum, pageSize);
                break;
            case "getAllSellingConditionReplaced":
                result = pastAnalysisService.getAllSellingConditionOrders(pageNum, pageSize);
                break;
            case "getAllQuarterlySellingConditionReplaced":
                result = pastAnalysisService.getAllQuarterlySellingConditionReplaced(pageNum, pageSize);
                break;
            case "getAllMajorCustomersSituation":
                result = pastAnalysisService.getAllMajorCustomersSituation(pageNum, pageSize);
                break;
            case "getAllProductDimensionSituation":
                result = pastAnalysisService.getAllProductDimensionSituation(pageNum, pageSize);
                break;
            case "getAllRetailCondition":
                result = pastAnalysisService.getAllRetailCondition(pageNum, pageSize);
                break;
            case "getAllRetailQuarterlySellingCondition":
                result = pastAnalysisService.getAllRetailQuarterlySellingCondition(pageNum, pageSize);
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





    @GetMapping("/getAllCustomerTypeOrdersReplaced")
    public Result getAllCustomerTypeOrdersReplaced(@RequestParam("yearly")int yearly,
                                                   @RequestParam("monthly")int monthly,@RequestParam("agent")int agent,
                                                   @RequestParam("newCustomer")int newCustomer,@RequestParam("temporaryCustomer")int temporaryCustomer,
                                                   @RequestParam("daily")int daily, @RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        Object result = null;
        result = pastAnalysisService.getAllCustomerTypeordersReplaced(yearly,monthly,agent,newCustomer,temporaryCustomer,daily,pageNum, pageSize);
        if (result != null) {
            return Result.success(200, "Query successful", result);
        } else {
            return Result.fail(500, "Error in query execution");
        }
    }


    @GetMapping("/getAllCustomerTypeOrdersBack")
    public Result getAllCustomerTypeOrdersBack(@RequestParam("yearly")int yearly,
                                               @RequestParam("monthly")int monthly,@RequestParam("agent")int agent,
                                               @RequestParam("newCustomer")int newCustomer,@RequestParam("temporaryCustomer")int temporaryCustomer,
                                               @RequestParam("daily")int daily, @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        Object result = null;
        result = pastAnalysisService.getAllCustomerTypeOrdersBack(yearly,monthly,agent,newCustomer,temporaryCustomer,daily,pageNum, pageSize);
        if (result != null) {
            return Result.success(200, "Query successful", result);
        } else {
            return Result.fail(500, "Error in query execution");
        }
    }


    @GetMapping("/getAllCustomerTypeordersMonthlyReplaced")
    public Result getAllCustomerTypeordersMonthlyReplaced(@RequestParam("yearly")int yearly,
                                                   @RequestParam("monthly")int monthly,@RequestParam("agent")int agent,
                                                   @RequestParam("newCustomer")int newCustomer,@RequestParam("temporaryCustomer")int temporaryCustomer,
                                                   @RequestParam("daily")int daily, @RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        Object result = null;
        result = pastAnalysisService.getAllCustomerTypeordersMonthlyReplaced(yearly,monthly,agent,newCustomer,temporaryCustomer,daily,pageNum, pageSize);
        if (result != null) {
            return Result.success(200, "Query successful", result);
        } else {
            return Result.fail(500, "Error in query execution");
        }
    }


    @GetMapping("/getAllCustomerTypeordersMonthlyBack")
    public Result getAllCustomerTypeordersMonthlyBack(@RequestParam("yearly")int yearly,
                                               @RequestParam("monthly")int monthly,@RequestParam("agent")int agent,
                                               @RequestParam("newCustomer")int newCustomer,@RequestParam("temporaryCustomer")int temporaryCustomer,
                                               @RequestParam("daily")int daily, @RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        Object result = null;
        result = pastAnalysisService.getAllCustomerTypeordersMonthlyBack(yearly,monthly,agent,newCustomer,temporaryCustomer,daily,pageNum, pageSize);
        if (result != null) {
            return Result.success(200, "Query successful", result);
        } else {
            return Result.fail(500, "Error in query execution");
        }
    }
}
