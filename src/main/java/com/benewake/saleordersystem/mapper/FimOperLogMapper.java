package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.saleordersystem.entity.FimOperLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FimOperLogMapper extends BaseMapper<FimOperLog> {
    Page<FimOperLog> selectLists(Page<FimOperLog> fimOperLogPage, Object o);
}
