package com.benewake.saleordersystem.annotation;


//枚举了不同的元素类型
import java.lang.annotation.ElementType;
//指定注解的保留策略
import java.lang.annotation.Retention;
//导入RetentionPolicy枚举，定义注解的保留策略
import java.lang.annotation.RetentionPolicy;
//指定了注解可以应用于哪些元素类型
import java.lang.annotation.Target;

/**
 * @author Lcs
 */
//LoginRequired注解可以应用于方法，只有被@LoginRequired标记的的方法需要登陆才能访问
@Target(ElementType.METHOD)
//注释定义了LoginRequired注解的保留策略为运行时。这个注解将在运行时保留，通过反射来访问
@Retention(RetentionPolicy.RUNTIME)
//该注解中没有定义任何成员，只是一个标注，被标注的方法需要登陆
public @interface LoginRequired {
}
