package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_1_salesman_selling_condition;
import com.benewake.saleordersystem.service.PastAnalysisService;
import com.benewake.saleordersystem.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/get-all")
    public Result getAllPastAnalysis() {
        List<Past_orders_analysis_1_salesman_selling_condition> pastAnalysisList = pastAnalysisService.getAllPastAnalysis();

        return Result.success(200, "查询成功", pastAnalysisList);
    }
}
