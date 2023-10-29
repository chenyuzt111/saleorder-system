package com.benewake.saleordersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.saleordersystem.entity.FimOperLog;
import com.benewake.saleordersystem.entity.VO.FimOperLogQueryVo;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.IpUtil;
import com.benewake.saleordersystem.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Lcs
 */
@Service
public interface FimOperLogService extends IService<FimOperLog> {




    /**
     * 条件查找操作日志
     * @param fimOperLogQueryVo
     * @return
     */
    List<FimOperLog> selectOperLogs(FimOperLogQueryVo fimOperLogQueryVo);



}
