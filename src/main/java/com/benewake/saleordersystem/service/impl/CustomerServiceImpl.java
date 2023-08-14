package com.benewake.saleordersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.saleordersystem.entity.Customer;
import com.benewake.saleordersystem.mapper.CustomerMapper;
import com.benewake.saleordersystem.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年07月03 17:12
 * 描 述： TODO
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<Customer> getCustomerLikeList(String customerName) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Customer::getFCustId,Customer::getFName);
        queryWrapper.like(StringUtils.isNotBlank(customerName),Customer::getFName,customerName);
        return customerMapper.selectList(queryWrapper);
    }

    @Override
    public Customer findCustomerById(Long customerId) {
        return customerMapper.selectById(customerId);
    }

    @Override
    public Customer findCustomerByName(String customerName) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getFName,customerName);
        return customerMapper.selectOne(queryWrapper);
    }

    @Override
    public List<String> getCustomerTypeLikeList(String type) {
        return customerMapper.getCustomerTypeLikeList("%"+type+"%");
    }
}
