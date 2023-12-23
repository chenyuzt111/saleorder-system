package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.saleordersystem.SaleOrderSystemApplication;
import com.benewake.saleordersystem.entity.Customer;
import com.benewake.saleordersystem.entity.FimOperLog;
import com.benewake.saleordersystem.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年07月03 17:35
 * 描 述： TODO
 */
@SpringBootTest
@ContextConfiguration(classes = SaleOrderSystemApplication.class)
public class CustomerTest {

    @Autowired
    private CustomerService customerService;


    @Test
    public void testUpdateDB(){
       // customerService.updateCustomerDB();
    }

    @Autowired
    private FimOperLogMapper fimOperLogMapper;
    @Test
    public void testLikeList(){
        Page<FimOperLog> fimOperLogPage = new Page<>(1, 10);
        Page<FimOperLog> fimOperLogPage1 = fimOperLogMapper.selectLists(fimOperLogPage, null);
        fimOperLogPage1.getRecords().forEach(System.out::println);
        System.out.println(fimOperLogPage1.getSize());
        System.out.println(fimOperLogPage1.getPages());
        System.out.println(fimOperLogPage1.getCurrent());
        System.out.println(fimOperLogPage1.getTotal());
    }


}
