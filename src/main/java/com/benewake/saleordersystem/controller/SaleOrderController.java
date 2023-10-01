package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.annotation.LoginRequired;
import com.benewake.saleordersystem.annotation.SalesmanRequired;
import com.benewake.saleordersystem.annotation.TrackingTime;
import com.benewake.saleordersystem.entity.*;
import com.benewake.saleordersystem.entity.VO.FilterCriteria;
import com.benewake.saleordersystem.entity.VO.FilterVo;
import com.benewake.saleordersystem.entity.VO.StartInquiryVo;
import com.benewake.saleordersystem.service.*;
import com.benewake.saleordersystem.utils.BenewakeConstants;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Lcs
 * @since 2023年07月05 16:18
 * 描 述： TODO
 */
@Controller
@RequestMapping("/order")
@ResponseBody
@Slf4j
public class SaleOrderController implements BenewakeConstants {

    @Autowired
    private InquiryService inquiryService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private ViewColService viewColService;
    @Autowired
    private ViewService viewService;

    @Autowired
    private FeiShuMessageService feiShuMessageService;

    @Autowired
    private UserService userService;






    @GetMapping("/stateList")
    public Result getStateList(){
        List<String> stateList = inquiryService.getStateList();
        return Result.success(stateList);
    }
    @PostMapping("/inquiryTypeList")
    public Result getInquiryTypeList(@RequestBody Map<String,Object> param){
        String key = (String) param.get("inquiryType");
        if(key==null) {
            key = "";
        }
        return Result.success(inquiryService.getInquiryTypeList(key));
    }
    @PostMapping("/inquiryCodeList")
    public Result getInquiryLikeList(@RequestBody Map<String,Object> param){
        String key = (String) param.get("inquiryCode");
        if(key==null) {
            key = "";

        }
        List<Inquiry> res = inquiryService.getInquiryCodeLikeList(key);
        return Result.success(res);
    }

    /**
     * 已登录用户根据tableId获取对应的新增视图，若无新增视图则为空
     */
    @PostMapping("/views")
    //一个自定义注解，需要用户登录才能访问这个接口
    @LoginRequired
    //返回的是一个包含列表的结果，列表的元素类型是view对象
    public Result<List<View>> getViewByTableId(@RequestBody Map<String,Object> param){
        Long tableId = Long.parseLong((String) param.get("tableId"));
        User u = hostHolder.getUser();
        List<View> lists = viewService.getUserView(u.getId(),tableId);

        return Result.success(lists);
    }

    /*获取表的全部列信息*/
    @PostMapping("/cols")
    public Result<Map<String,Object>> getAllCols(@RequestBody Map<String,Object> param){
        Long tableId = Long.parseLong((String) param.get("tableId"));
        Map<String,Object> map = new HashMap<>(16);
        List<Map<String,Object>> maps = viewService.getAllCols(tableId);
        map.put("cols",maps);
        return Result.success(map);
    }

    /**
     * 1-6订单列表过滤查询
     * viewId = -1 表示查看我的  viewId = 0 表示查看全部
     * @param filterVo
     * @return
     */
    @PostMapping("/Lists")
    //自定义注解，登录才能访问该方法
    @LoginRequired
    //用于追踪方法的执行时间
    @TrackingTime
    //接收一个请求体包含 FilterVo参数
    public Result selectList(@RequestBody FilterVo filterVo){

        Map<String,Object> res = new HashMap<>(16);
        //检查是否包含过滤条件
        if(filterVo==null || filterVo.getTableId()==null || filterVo.getViewId()==null){
            return Result.fail().message("未选择表或视图！");
        }
        // 当前登录用户
        User loginUser = hostHolder.getUser();
        // 通过viewColService类中getCols方法获取列信息，根据表格 ID、视图 ID 和用户类型
        List<Map<String,Object>> cols = viewColService.getCols(filterVo.getTableId(), filterVo.getViewId(), loginUser.getUserType().equals(1L));
        //如果试图ID小于等于0，表示查看我的视图
        if(filterVo.getViewId() <= 0){
            // 我的视图
            // 查看我的
            List<Map<String,Object>> lists;
            //需要查看全部视图
            if( filterVo.getTableId().equals(1L)&&filterVo.getViewId().equals(-1L)) {
                // 系统全部
                lists = inquiryService.selectSalesOrderVoList(filterVo.getFilterCriterias(), null);
            } else if (loginUser.getUserType().equals(1L) ) {
                //如果用户是管理员
                lists = inquiryService.selectSalesOrderVoList(filterVo.getFilterCriterias(),loginUser.getUsername());
            } else{
                // 普通用户
                lists = inquiryService.selectSalesOrderVoList(filterVo.getFilterCriterias(),loginUser.getUsername());
            }
            //获取到的订单以map形式放入res
            res.put("lists",lists);
        }else{
            // 个人设定的视图
            //筛选存储条件，使用三元运算符如果没有的话初始化为空列表或者使用筛选体中的筛选条件
            List<FilterCriteria> filters = filterVo.getFilterCriterias()==null?new ArrayList<>():filterVo.getFilterCriterias();
            // 遍历前面获取到的cols对象，添加方案默认筛选信息
            for(Map<String,Object> col : cols){
                //首先获取该列的筛选值
                String colValue = (String) col.get("col_value");
                //如果筛选值不为空
                if(!StringUtils.isEmpty(colValue)){
                    //获取键为col_name_ENG的值表示列名，添加到新建的筛选条件中
                    filters.add(new FilterCriteria((String) col.get("col_name_ENG"),
                            //从当前列获取value_operator键对应的值表示列的操作符
                            StringUtils.isEmpty(col.get("value_operator"))?EQUAL: (String) col.get("value_operator"),colValue));
                }
            }
            // 根据个人的筛选条件获取信息
            List<Map<String, Object>> lists;

            if (loginUser.getUserType().equals(1L)) {
                // 如果登录用户是管理员，可以看到所有订单
                lists = inquiryService.selectSalesOrderVoList(filters, null);
            } else {
                // 否则，只能看到自己的订单
                lists = inquiryService.selectSalesOrderVoList(filters, loginUser.getUsername());
            }

            res.put("lists",lists);
        }
        res.put("cols",cols);
        //返回试图
        return Result.success(res);
    }

    /**
     * 保存方案
     * @param filterVo
     * @return
     */
    @PostMapping("/saveView")
    @LoginRequired
    @Transactional(rollbackFor = Exception.class)
    public Result savePlan(@RequestBody FilterVo filterVo){
        // 参数合法性判断
        if(filterVo.getTableId() == null){
            return Result.fail("表id不能为空！",null);
        }
        if(CollectionUtils.isEmpty(filterVo.getCols())){
            return Result.fail("新增方案的列信息不能为空！",null);
        }
        // 获取当前用户
        User u = hostHolder.getUser();
        if(StringUtils.isEmpty(filterVo.getViewName()) || viewService.isExist(filterVo.getTableId(),u.getId(),filterVo.getViewName(),filterVo.getViewId())){
            return Result.fail("该视图名称为空或已存在!",null);
        }
        // 持久化视图
        //创建一个新的视图对象
        View view = new View();
        //设置view属性
        view.setTableId(filterVo.getTableId());
        view.setViewName(filterVo.getViewName());
        view.setUserId(u.getId());
        //如果视图为空执行保存操作
        if(filterVo.getViewId()==null){
            viewService.saveView(view);
        }else{
            //否则的话执行更新操作，获取传入得视图ID
            view.setViewId(filterVo.getViewId());
            viewService.updateView(view);
        }
        //从传入的对象中获取视图列信息列表
        List<ViewCol> cols = filterVo.getCols();
        //遍历视图列，为每个视图列设置视图ID
        for(ViewCol vc : cols){
            vc.setViewId(view.getViewId());
        }
        // 删掉原来的列信息
        viewColService.deleteCols(view.getViewId());
        // 保存新增列信息
        viewColService.saveCols(cols);
        if(filterVo.getViewId()==null) {
            return Result.success("方案添加成功！",null);
        } else {
            return Result.success("方案修改成功！",null);
        }
    }


    /**
     * 修改订单
     * 将原来订单state设置为-1  再新增一条订单信息
     * @param inquiry
     * @return
     */
    @PostMapping("/update")
    @SalesmanRequired
    @Transactional(rollbackFor = Exception.class)
    public Result updateInquired(@RequestBody Inquiry inquiry){
        User u = hostHolder.getUser();
        if( u.getUserType().equals(USER_TYPE_SALESMAN)&&
                (inquiry.getSalesmanId().equals(u.getId())||inquiry.getCreatedUser().equals(u.getId())) ||
                u.getUserType().equals(USER_TYPE_ADMIN) || u.getUserType().equals(USER_TYPE_SYSTEM)
        ){
            if(inquiry == null){
                return Result.fail().message("请添加选择要修改的订单！");
            }
            if(inquiry.getInquiryType()==null){
                return Result.fail().message("请选择订单类型");
            }
            if(inquiry.getState()==null || inquiry.getState()==-1){
                return Result.fail().message("订单无效！");
            }
            // 订单参数有效判断
            Map<String,Object> res = inquiryService.addValid(inquiry);
            if(res.size()>0){
                return Result.fail((String) res.get("error"),null);
            }
            // 原订单设置无效
            inquiryService.updateState(inquiry.getInquiryId(),-1);
            // 新增修改后的订单
            inquiry.setInquiryId(null);
            inquiry.setCreatedUser(hostHolder.getUser().getId());
            inquiryService.save(inquiry);
            return Result.success("修改成功！",inquiry.getInquiryId());
        }else{
            return Result.fail().message("用户权限不足！");
        }

    }

    /**
     * 管理员为询单失败的订单赋值使其可以询单
     * allowinquiry !=null 表示可以询单
     * @return
     */@ApiOperation("允许询单")
    @PostMapping ("/allowinquiry")
    public List<Result> updateInquiryAllowInquiry(@RequestBody List<Long> inquiryIds) {
        List<Result> results = new ArrayList<>();

        for (Long inquiryId : inquiryIds) {
            Result result = inquiryService.update_InquiryAllowInquiry(inquiryId);
            // 可以根据需要处理每个查询的结果
            // 这里只是简单地将结果添加到总结果中
            results.add(result);
        }

        return results;
    }


    /**
     * 新增询单信息 及 开始询单 （只能新增或询单）
     * startInquiry = 1 表示询单
     * @return
     */
    @PostMapping("/save")
    @SalesmanRequired
    @Transactional(rollbackFor = Exception.class)
    public Result addInquiries(@RequestBody StartInquiryVo param){
        //创建一个新的订单列表用于接收传入的订单
        List<Inquiry> newInquiries = param.getInquiryList();
        //新建一个整数类型用于接收传入的是否开始询单数据
        Integer startInquiry = param.getStartInquiry();
        List<Long> ids = new ArrayList<>();
        // 获取订单编码列表
        List<String> inquiryCodes = new ArrayList<>();
        List<String> message = new ArrayList<>();


        //创建一个新的map,用于存储询单信息
        Map<String,Object> map = new HashMap<>(16);
        //订单为空的话
        if(newInquiries == null){
            return Result.fail("请添加至少一条询单信息",null);
        }

        for(Inquiry inq1 : newInquiries) {
            if (inq1.getInquiryCode() == null) {
                // 保存 或 单据id不存在（开始询单数据为保存状态，或者传入的单据id为0）
                if (startInquiry == 0 || inq1.getInquiryId() == null) {
                    //获取当前用户信息
                    User user = hostHolder.getUser();
                    Date nowTime = new Date();
                    //接收到的订单类型为空
                    if (inq1.getInquiryType() == null) {
                        return Result.fail("请选择订单类型", null);
                    }

                    // 逐条分析询单是否合法，遍历传入的单据列表

                        //使用inquiryservice中addvalid方法判断询单是否合法
                        Map<String, Object> res = inquiryService.addValid(inq1);
                        //res被创建就是为了存储询单不合法的信息，如果里面有内容说明错误
                        if (res.size() > 0) {
                            return Result.fail((String) res.get("error"), null);
                        }
                        // 如果单据合法，设置创建人信息以及单据编号
                    inq1.setCreatedUser(user.getId());
                        //如果单据编码为空，调用方法获取一个单据编号格式，然后获取一个格式化的编号
                        if (inq1.getInquiryCode() == null) {
                            inq1.setInquiryCode(inquiryService.getDocumentNumberFormat(inq1.getInquiryType(), 1).get(0));
                        }

                        //将刚刚获取到的单据编码存入到订单编码列表
                        inquiryCodes.add(inq1.getInquiryCode());
                        //并将状态码设置为0，保存状态
                    inq1.setState(0);

                    //将之前生成的单据编码以键值名为inquiryCode，以便后续使用
                    map.put("inquiryCode", inquiryCodes);


                    List<Inquiry> singleInquiryList = new ArrayList<>();
                    singleInquiryList.add(inq1);

                    // 添加运输信息
                    deliveryService.insertLists(singleInquiryList);
                    // 全部通过加入数据库
                    inquiryService.insertLists(singleInquiryList);

                    //创建一个ids列表

                    //遍历接收到订单信息，获取订单id存入ids

                    ids.add(inq1.getInquiryId());


                    //将ids以键名“ids”存入map
                    map.put("ids", ids);
                    message.add("保存成功!\n");
                    map.put("message",message);
                }
                // 前面是保存，接下来是询单功能
                //如果订单状态不空且不是0的话，可以询单
                if (startInquiry != null && startInquiry != 0) {
                    //创建两个空的arraylist用于存储询单处理结果
                    //fail,success分别存储失败和成功的询单
                    List<Inquiry> fail = new ArrayList<>();
                    List<Inquiry> success = new ArrayList<>();
                    //初始化一个整数变量‘ind’,用于跟踪循环的索引
                    try {

                        Inquiry inquiry = inquiryService.getInquiryById(inq1.getInquiryId());
                            // 检查 allow_inquiry 字段是否为空
                            if (inquiry.getAllowinquiry() == null) {
                                Item item = itemService.findItemById(inquiry.getItemId());
                                if (item.getItemType() == ITEM_TYPE_MATERIALS_AND_SOFTWARE_BESPOKE ||
                                        item.getItemType() == ITEM_TYPE_RAW_MATERIALS_BESPOKE ||
                                        item.getQuantitative() == 0 || inquiry.getSaleNum() > item.getQuantitative()) {
                                    // 询单失败
                                    // 物料类型为 新增原材料+软件定制 或 新增原材料定制 或 物料标准数量为0 或 当前数量大于物料标准数量
                                    fail.add(inquiry);
                                } else {
                                    success.add(inquiry);
                                    //询单成功飞书发送消息
                                    User saleman = userService.findUserById(inquiry.getSalesmanId());
                                    if (saleman != null) {
                                        String salemanName = saleman.getUsername();
                                        feiShuMessageService.sendMessage(salemanName, inquiry.getInquiryCode(), inquiry.getItemId(), inquiry.getSaleNum());
                                    }
                                }
                            } else {
                                // allow_inquiry 不为空，直接添加到成功列表并发送消息
                                success.add(inquiry);
                                User saleman = userService.findUserById(inquiry.getSalesmanId());
                                if (saleman != null) {
                                    String salemanName = saleman.getUsername();
                                    feiShuMessageService.sendMessage(salemanName, inquiry.getInquiryCode(), inquiry.getItemId(), inquiry.getSaleNum());
                                }
                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        throw new RuntimeException("参数有误！");
                    }
                    //创建一个restr列表用于存储最终的处理结果字符串
                    List<String> resStr = new ArrayList<>();
                    //首先遍历失败的订单，添加到刚刚创建的restr中
                    fail.forEach(f -> {
                        if (!message.isEmpty()) {
                            message.remove(message.size() - 1); // Remove the last element (index: size - 1).
                        }
                        message.add("单据编号:" + f.getInquiryCode() + "询单失败，请飞书联系管理员!\n");
                        map.put("message",message);
                    });
                    //如果成功的队列中不为0，进入询单功能
                    if (success.size() > 0) {
                        //map.put("success",success.size()+"个订单开始询单！");

                        // 询单功能（待添加)   异步或消息队列

                        // 设置state+1 （之后可考虑移到异步操作中或使用消息队列）
                        success.forEach(s -> s.setState(s.getState() + 1));
                        // 更新数据库  （之后可考虑移到异步操作中或使用消息队列）
                        inquiryService.updateByInquiry(success);
                        //return Result.success("已开始询单！",map);
//                        resStr.add("APS暂未上线，今日内计划手动反馈日期！");
                        if (!message.isEmpty()) {
                            message.remove(message.size() - 1); // Remove the last element (index: size - 1).
                        }
                        message.add("APS暂未上线，今日内计划手动反馈日期！\n");
                        map.put("message",message);

                    }
                    //返回如果成功的询单数量大于 0，将处理结果添加到 resStr，包含 "APS暂未上线，今日内计划手动反馈日期！"。
                    //使用 String.join 方法将 resStr 中的消息字符串使用换行符连接起来。

                }
            } else {



                    Inquiry inquiriesByCode = inquiryService.getInquiriesByCode(inq1.getInquiryCode());
                    inquiriesByCode.setCustomerId(inq1.getCustomerId());
                    inquiriesByCode.setExpectedTime(inq1.getExpectedTime());
                    inquiriesByCode.setInquiryType(inq1.getInquiryType());
                    inquiriesByCode.setItemId(inq1.getItemId());
                    inquiriesByCode.setRemark(inq1.getRemark());
                    inquiriesByCode.setSaleNum(inq1.getSaleNum());
                    inquiriesByCode.setSalesmanId(inq1.getSalesmanId());
                    //遍历接收到订单信息，获取订单id存入ids


                    //将刚刚获取到的单据编码存入到订单编码列表
                    inquiryCodes.add(inq1.getInquiryCode());
                    //并将状态码设置为0，保存状态
                    inq1.setState(0);

                    //将之前生成的单据编码以键值名为inquiryCode，以便后续使用
                    map.put("inquiryCode", inquiryCodes);
                    updateInquired(inquiriesByCode);



                //将ids以键名“ids”存入map
                    ids.add(inquiriesByCode.getInquiryId());
                    map.put("ids", ids);
                    message.add("保存成功!\n");

                    map.put("message",message);
                if (startInquiry == 1) {
                    List<Inquiry> fail = new ArrayList<>();
                    List<Inquiry> success = new ArrayList<>();
                    //初始化一个整数变量‘ind’,用于跟踪循环的索引
                    try {
                        Inquiry inquiry = inquiryService.getInquiriesByCode(inq1.getInquiryCode());
                        // 检查 allow_inquiry 字段是否为空
                        if (inquiriesByCode.getAllowinquiry() == null) {
                            Item item = itemService.findItemById(inquiry.getItemId());
                            if (item.getItemType() == ITEM_TYPE_MATERIALS_AND_SOFTWARE_BESPOKE ||
                                    item.getItemType() == ITEM_TYPE_RAW_MATERIALS_BESPOKE ||
                                    item.getQuantitative() == 0 || inquiry.getSaleNum() > item.getQuantitative()) {
                                // 询单失败
                                // 物料类型为 新增原材料+软件定制 或 新增原材料定制 或 物料标准数量为0 或 当前数量大于物料标准数量
                                fail.add(inquiry);
                            } else {
                                success.add(inquiry);
                                //询单成功飞书发送消息
                                User saleman = userService.findUserById(inquiry.getSalesmanId());
                                if (saleman != null) {
                                    String salemanName = saleman.getUsername();
                                    feiShuMessageService.sendMessage(salemanName, inquiry.getInquiryCode(), inquiry.getItemId(), inquiry.getSaleNum());
                                }
                            }
                        } else {
                            // allow_inquiry 不为空，直接添加到成功列表并发送消息
                            success.add(inquiry);
                            User saleman = userService.findUserById(inquiry.getSalesmanId());
                            if (saleman != null) {
                                String salemanName = saleman.getUsername();
                                feiShuMessageService.sendMessage(salemanName, inquiry.getInquiryCode(), inquiry.getItemId(), inquiry.getSaleNum());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        throw new RuntimeException("参数有误！");
                    }

                    fail.forEach(f -> {
                        if (!message.isEmpty()) {
                            message.remove(message.size() - 1); // Remove the last element (index: size - 1).
                        }
                        message.add("单据编号:" + f.getInquiryCode() + "询单失败，请飞书联系管理员!\n");
                        map.put("message",message);
                    });
                    //如果成功的队列中不为0，进入询单功能
                    if (success.size() > 0) {
                        //map.put("success",success.size()+"个订单开始询单！");

                        // 询单功能（待添加)   异步或消息队列

                        // 设置state+1 （之后可考虑移到异步操作中或使用消息队列）
                        success.forEach(s -> s.setState(s.getState() + 1));
                        // 更新数据库  （之后可考虑移到异步操作中或使用消息队列）
                        inquiryService.updateByInquiry(success);
                        //return Result.success("已开始询单！",map);
                        if (!message.isEmpty()) {
                            message.remove(message.size() - 1); // Remove the last element (index: size - 1).
                        }
                        message.add("APS暂未上线，今日内计划手动反馈日期！\n");
                        map.put("message",message);

                    }
                    //返回如果成功的询单数量大于 0，将处理结果添加到 resStr，包含 "APS暂未上线，今日内计划手动反馈日期！"。
                    //使用 String.join 方法将 resStr 中的消息字符串使用换行符连接起来。


                }

            }

        }
        String result= message.toString();
        result = result.replace("[", "").replace("]", "").replace(",", "");
        return Result.success(result, map);
    }

    /**
     * 删除接口 销售员只能删除销售员id或创建人id等于自己id的数据
     * @param param
     * @return
     */
    @PostMapping("/delete")
    @SalesmanRequired//调用此接口的必须是销售员
    public Result deleteOrder(@RequestBody Map<String,Long> param){
        //从传入的参数param中获取订单的id
        Long orderId = param.get("orderId");
        //根据订单ID通过inquiryService.getInquiryById获得订单对象
        Inquiry inquiry = inquiryService.getInquiryById(orderId);
        //获取当前用户信息
        User u = hostHolder.getUser();
        //如果用户是销售员并且订单的销售员ID与当前用户ID相同，或者订单的创建人ID与当前用户ID相同，
        //或者用户是管理员或系统用户，那么用户有足够权限进行删除操作。
        if( u.getUserType().equals(USER_TYPE_SALESMAN)&&
                (inquiry.getSalesmanId().equals(u.getId())||inquiry.getCreatedUser().equals(u.getId())) ||
                u.getUserType().equals(USER_TYPE_ADMIN) || u.getUserType().equals(USER_TYPE_SYSTEM)
        ){
            //调用inquiryService.deleteOrder方法传入订单id,进行删除操作res接收返回值
            boolean res = inquiryService.deleteOrder(orderId);
            if(!res){
                return Result.fail().message("订单不存在！");
            }else {
                return Result.success().message("删除成功！");
            }
        }else{
            return Result.fail().message("用户权限不够，只能删除自己的订单！");
        }
    }

    @PostMapping("/importExcel")
    @SalesmanRequired//销售员才能访问该接口
    //接收文件作为参数
    public Result addOrdersByExcel(@RequestParam("file")MultipartFile file){
        //创建一个map，用于存储excel数据处理的结果
        Map<String,Object> map = new HashMap<>(16);
        //对文件进行判空操作
        if(file.isEmpty()){
            return Result.fail("文件为空！",null);
        }
        //这一行代码的目的是获取上传文件的原始文件名，并使用点号（.）作为分隔符将文件名拆分成多个部分。
        // 因为点号在正则表达式中有特殊含义，所以双反斜杠（\\.）用来转义点号，确保按点号进行分割。
        val split = file.getOriginalFilename().split("\\.");
        //检查split中拆分后的文件名的第二部分，也就是文件格式部分，看是否为excel文件
        if(!"xlsx".equals(split[1]) && !"xls".equals(split[1])){
            return Result.fail().message("请提供.xlsx或.xls为后缀的Excel文件");
        }
        //进入try块中保存excel的文件，存入map
        try {
            map = inquiryService.saveDataByExcel(file);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //检查map中是否有error键值，如果有说明处理失败
        if(map.containsKey("error")){
            return Result.fail().message((String) map.get("error"));
        }else{
            return Result.success().message((String) map.get("success"));
        }
    }

    @PostMapping("/deleteView")
    public Result deleteView(@RequestBody View view){
        if(view.getViewId()==null) {
            return Result.fail("viewId不能为空！",null);
        }
        boolean isSuccess = viewService.deleteView(view.getViewId());
        return isSuccess?Result.success("删除成功!",null) : Result.fail("删除失败或视图不存在！",null);
    }
}
