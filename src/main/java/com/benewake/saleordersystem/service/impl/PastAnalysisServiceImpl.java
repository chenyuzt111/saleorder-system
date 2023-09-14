package com.benewake.saleordersystem.service.impl;

import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_1_salesman_selling_condition;
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
}
