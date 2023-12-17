package com.benewake.saleordersystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.saleordersystem.entity.Notice;
import com.benewake.saleordersystem.mapper.NoticeMapper;
import com.benewake.saleordersystem.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月02 16:32
 * 描 述： TODO
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    @Override
    // 根据用户ID和类型获取所有公告列表
    public List<Map<String, Object>> getAllList(Long id,Integer type) {
        return baseMapper.adminFindNoticeByUserId(id,type);
    }

    @Override
    // 根据用户ID和类型获取所有公告列表
    public List<Map<String, Object>> getUnhidenList(Long id,Integer type) {
        return baseMapper.userFindNoticeByUserId(id,type);
    }

    @Override
    public int hidenNotice(int id) {
        return baseMapper.hidenNotice(id);
    }
}
