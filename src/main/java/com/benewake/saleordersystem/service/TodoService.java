package com.benewake.saleordersystem.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.UserTypeValues;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/*
 * @author Zt
 * @since 2023年08月31日 17:47
 * 描 述： TODO
*/


@Service
public interface TodoService  {
    List<String> getFilteredOrders();

}
