package com.benewake.saleordersystem.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lcs
 * @since 2023年06月30 18:15
 * 描 述： TODO
 */

//这个注解表示这是一个配置类
@Configuration
//这是一个类的名称，表示配置类的名字
public class MyBatisConfig {

    @Bean
    //创建并返回一个名为mybatisPlusInterceptor的bean对象，用于配置 MyBatis-Plus 的分页功能。
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //创建一个拦截器实例，用于配置分页拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //创一个PaginationInnerInterceptor实例，是 MyBatis-Plus 提供的内置分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
