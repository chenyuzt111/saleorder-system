package com.benewake.saleordersystem.mapper;

import com.benewake.saleordersystem.SaleOrderSystemApplication;
import com.benewake.saleordersystem.entity.Past_analysis.Past_orders_analysis_15_customer_type_monthly_back;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年06月30 10:08
 * 描 述： TODO
 */
@SpringBootTest
@ContextConfiguration(classes = SaleOrderSystemApplication.class)
public class StoredProceduresTest {
    @Autowired
    StoredProceduresMapper storedProceduresMapper;

    @Test
    public void doGet(){
        List<Past_orders_analysis_15_customer_type_monthly_back> resultList = new ArrayList<>();
        List<Map<String,Object>> list = storedProceduresMapper.doPastOrdersAnalysis15CustomerTypeMonthlyBack(1,1,1,1,1,1);
        for(Map<String,Object> t : list){
            Past_orders_analysis_15_customer_type_monthly_back entity  = new Past_orders_analysis_15_customer_type_monthly_back();
            entity.setSerialNum((BigInteger) t.get("serial_number")); // Assuming the primary key is "id"
            entity.setItemCode((String) t.get("item_code"));
            entity.setItemName((String) t.get("item_name"));
            entity.setMonthAvg((BigDecimal) t.get("month_avg"));
            entity.setTotalItem((BigDecimal) t.get("total_item"));
            entity.setTotalMonths((Integer) t.get("total_months"));
            entity.setMax((BigDecimal) t.get("max_"));


            t.forEach((k,v)-> System.out.print(k+"："+v+","));
        }
        System.out.println(resultList);

    }
}
