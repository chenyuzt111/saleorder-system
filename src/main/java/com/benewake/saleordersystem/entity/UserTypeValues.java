package com.benewake.saleordersystem.entity;

/**
 * 用于查询用户的YC, XD, PR字段值
 */
public class UserTypeValues {
    private Long useryc;  // 用户YC字段值
    private Long userxd;  // 用户XD字段值
    private Long userpr;  // 用户PR字段值

    // 获取用户YC字段值
    public Long getUseryc() {
        return useryc;
    }

    // 设置用户YC字段值
    public void setUseryc(Long useryc) {
        this.useryc = useryc;
    }

    // 获取用户XD字段值
    public Long getUserxd() {
        return userxd;
    }

    // 设置用户XD字段值
    public void setUserxd(Long userxd) {
        this.userxd = userxd;
    }

    // 获取用户PR字段值
    public Long getUserpr() {
        return userpr;
    }

    // 设置用户PR字段值
    public void setUserpr(Long userpr) {
        this.userpr = userpr;
    }
}
