package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.annotation.LoginRequired;
import com.benewake.saleordersystem.entity.Item;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.VO.ItemVo;
import com.benewake.saleordersystem.service.ItemService;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年07月12 11:21
 * 描 述： TODO
 */

@Api(tags = "物料管理接口")
@Controller
@RequestMapping("/item")
@ResponseBody
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private HostHolder hostHolder;

    //获取物料类型模糊匹配列表

    @ApiOperation("物料类型模糊匹配接口")
    @PostMapping("/itemTypeList")
    public Result getItemTypeList(@RequestBody Map<String,Object> param){
        String itemType = (String) param.get("itemType");
        if(itemType==null) {
            itemType = "";
        }
        return Result.success(itemService.getItemTypeList(itemType));
    }

    // 获取物料编码模糊匹配列表
    @ApiOperation("物料编码模糊匹配接口")
    @PostMapping("/likeList")
    public Result getLikeItemList(@RequestBody Map<String,Object> param){
        String itemCode = (String) param.get("itemCode");
        if(itemCode==null) {
            itemCode = "";
        }
        return Result.success(itemService.itemCodeLikeList(itemCode));
    }
    //获取物料名称模糊匹配列表
    @ApiOperation("物料名称模糊匹配接口")
    @PostMapping("/itemNameList")
    public Result getItemNameList(@RequestBody Map<String,Object> param){
        String key = (String) param.get("itemName");
        if(key==null) {
            key = "";
        }
        return Result.success(itemService.getItemNameList(key));
    }

    // 增加物料
    @ApiOperation("添加物料接口")
    @PostMapping("/addItem")
    public Result addItem(@RequestBody Item item) {
        if (StringUtils.isBlank(item.getItemCode()) || StringUtils.isBlank(item.getItemName()) ||
                item.getItemType() < 1 || item.getItemType() > 5) {
            return Result.fail("信息不完全或物料类型不存在，添加失败！");
        }

        itemService.insertItem(item);
        return Result.success("物料添加成功！");
    }

    // 删除物料
    @ApiOperation("删除物料接口")
    @PostMapping("/deleteItem")
    public Result deleteItem(@RequestParam Long itemId) {
        itemService.deleteItemById(itemId);
        return Result.success("物料删除成功！");
    }

    //批量删除物料
    @ApiOperation("批量删除物料")
    @PostMapping("/batchDeleteItems")
    public Result batchDeleteItems(@RequestBody List<Long> itemIds){
        itemService.batchDeleteItemsByIds(itemIds);
        return Result.success("物料删除成功！");
    }

    // 更新物料
    @ApiOperation("更新物料接口")
    @PostMapping("/updateItem")
    public Result updateItem(@RequestBody Item item) {


        if (StringUtils.isBlank(item.getItemCode()) || StringUtils.isBlank(item.getItemName()) ||
                item.getItemType() < 1 || item.getItemType() > 5) {
            return Result.fail("信息不完全或物料类型不存在，添加失败！");
        }


        itemService.updateItem(item);
        return Result.success("物料更新成功！");
    }


    /**
     * 为用户设置物料置顶功能  (还未实现)
     */
    @PostMapping("/follow")
    @LoginRequired
    public Result setTop(@RequestBody ItemVo itemVo){
        User u = hostHolder.getUser();



        return Result.success().message("置顶成功！");
    }


}
