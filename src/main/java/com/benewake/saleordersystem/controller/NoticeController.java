package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.annotation.AdminRequired;
import com.benewake.saleordersystem.entity.Notice;
import com.benewake.saleordersystem.service.NoticeService;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.benewake.saleordersystem.utils.BenewakeConstants.USER_TYPE_ADMIN;

/**
 * @author Lcs
 * @since 2023年08月02 16:33
 * 描 述： TODO
 */
@Api(tags = "通知管理")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    //自动注入一个实例
    private NoticeService noticeService;
    @Autowired
    private HostHolder hostHolder;

    /**
     * 查询通知
     * type 0-普通通知 1-异常通知
     * 销售员查看未隐藏的订单
     *
     */
    @ApiOperation("查询通知接口")
    @PostMapping("/find")
    public Result findNotice(@RequestBody Notice notice){
        if (hostHolder.getUser().getUserType() == USER_TYPE_ADMIN) {
            return Result.success(noticeService.getAllList(notice.getCreateUserId(), notice.getType()));
        }else{
            return Result.success(noticeService.getUnhidenList(notice.getCreateUserId(), notice.getType()));
        }

    }
    @ApiOperation("保存通知接口")
    @PostMapping("/save")
    @AdminRequired
    public Result saveNotice(@RequestBody Notice notice){
        notice.setCreateUserId(hostHolder.getUser().getId());
        noticeService.save(notice);
        return Result.success();
    }
    @ApiOperation("删除通知接口")
    @PostMapping("/delete")
    @AdminRequired
    public Result removeNotice(@RequestBody List<Long> ids){
        noticeService.removeBatchByIds(ids);
        return Result.success();
    }
    @ApiOperation("修改通知接口")
    @PostMapping("/update")
    @AdminRequired
    public Result updateNotice(@RequestBody Notice notice){
        if(notice.getId()==null){
            return Result.fail().message("参数有误！");
        }
        if(notice.getType()!=null && notice.getType() < 0){
            return Result.fail().message("通知类型参数有误！");
        }
        noticeService.updateById(notice);
        return Result.success();
    }


    @PostMapping("/hiden")
    public Result hidenNotice(@RequestParam int id){
        int raw = noticeService.hidenNotice(id);
        if(raw == 0){
            return Result.fail().message("操作失败！");
        }else{
            return Result.success().message("操作成功！");
        }

    }

}
