package com.benewake.saleordersystem.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.saleordersystem.annotation.TrackingTime;
import com.benewake.saleordersystem.entity.Customer;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.Item;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.VO.FilterCriteria;
import com.benewake.saleordersystem.excel.InquiryExcelListener;
import com.benewake.saleordersystem.excel.model.InquiryModel;
import com.benewake.saleordersystem.mapper.InquiryCodeMapper;
import com.benewake.saleordersystem.mapper.InquiryMapper;
import com.benewake.saleordersystem.mapper.Vo.SalesOrderVoMapper;
import com.benewake.saleordersystem.service.*;
import com.benewake.saleordersystem.utils.BenewakeConstants;
import com.benewake.saleordersystem.utils.CommonUtils;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Lcs
 * @since 2023年07月05 16:19
 * 描 述： TODO
 */
@Service
@Slf4j
public class InquiryServiceImpl extends ServiceImpl<InquiryMapper,Inquiry> implements InquiryService, BenewakeConstants {

    @Autowired
    private InquiryCodeMapper inquiryCodeMapper;
    @Autowired
    private InquiryMapper inquiryMapper;
    @Autowired
    private SalesOrderVoMapper salesOrderVoMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerTypeService customerTypeService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    @TrackingTime
    public Map<String, Object> saveDataByExcel(MultipartFile file) {
        // 获取当前用户所有有效订单
        LambdaQueryWrapper<Inquiry> lqw = new LambdaQueryWrapper<>();
        //设置查询条件，获取当前用户的有效订单。查询条件包括订单状态大于等于 0，
        lqw.and(a->a.ge(Inquiry::getState,0));
        //执行查询操作，并将得到的数据存入existList中
        List<Inquiry> existList = inquiryMapper.selectList(lqw);
        // 读取Excel
        //创建一个监听实例
        InquiryExcelListener listener = new InquiryExcelListener(this,deliveryService,existList);
        Map<String,Object> map;
        try{
            //使用 EasyExcel 库读取上传文件的数据
            EasyExcel.read(file.getInputStream(), InquiryModel.class,listener).sheet().headRowNumber(1).doRead();
            map = listener.getMap();
        }catch (Exception e) {
            //将监听器中收集的结果赋给 map 变量，以便在异常情况下也能获取处理结果。
            map = listener.getMap();
            e.printStackTrace();
            log.error((String) map.get("error"));
        }
        return map;
    }

    @Override
    public int insertLists(List<Inquiry> inquiries) {
        return inquiryMapper.insertInquiries(inquiries);
    }

    //接收到筛选条件和一个用户名称
    @Override
    public List<Map<String,Object>> selectSalesOrderVoList(List<FilterCriteria> filters, String username) {
        //如果条件为空创建一个新列表，否则保持原有值
        if(filters==null) {
            filters = new ArrayList<>();
        }
        // 添加筛选条件，创建一个映射表
        Map<String,Integer> map = new HashMap<>(16);
        for(int i=0;i<filters.size();++i){
            //遍历筛选条件，依次加入到map当中
            map.put(filters.get(i).getColName(),i);
        }
        //设置一个查询对象
        QueryWrapper<Inquiry> queryWrapper1 = new QueryWrapper<>();
        // 默认最新和有效，查询对象为非空的bb.inquiry_id
        queryWrapper1.isNotNull("bb.inquiry_id");
        //初始化一个f1列表，存储特定列的筛选条件
        List<FilterCriteria> f1 = new ArrayList<>();
        // inquiry_init_type 需要int表示，定义一个字符串数组str1，其中包含了与f1列表中筛选条件有关的列名。
        String[] str1 = {"inquiry_code","sale_num","expected_time","arranged_time",
                "remark","inquiry_init_type","allow_inquiry"};
        //如果map中包含state键，（map中存的接受的筛选条件），存入f1（专门创建为了存储特定列的筛选条件）
        if(map.containsKey("state")){
            f1.add(filters.get(map.get("state")));
        }else{
            //不包含的话创建新的筛选条件对象，并添加到f1得列表当中
            f1.add(new FilterCriteria("state","ge","0"));
        }
        for(String s : str1){
            //遍历str1中的字段名，如果map中存在对应的键就加入到f1筛选条件中
            if(map.containsKey(s)){
                f1.add(filters.get(map.get(s)));
            }
        }
        //通过调用 CommonUtils.addFilters 方法，将所有筛选条件应用到查询条件包装器中。
        CommonUtils.addFilters(f1,queryWrapper1);

        QueryWrapper<Inquiry> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.isNotNull("item_code");
        f1 = new ArrayList<>();
        String[] str2 = {"item_code","item_name"};
        for(String s : str2){
            if(map.containsKey(s)){
                f1.add(filters.get(map.get(s)));
            }
        }
        CommonUtils.addFilters(f1,queryWrapper2);

        QueryWrapper<Inquiry> queryWrapper3 = new QueryWrapper<>();
        //queryWrapper3.ge("state",0);
        queryWrapper3.isNotNull("item_code");
        f1 = new ArrayList<>();
        String[] str3 = {"inquiry_code","delivery_code","receive_time","delivery_state"};
        for(String s : str3){
            if(map.containsKey(s)){
                FilterCriteria fc = filters.get(map.get(s));
                if("inquiry_code".equals(s)){
                    fc.setColName("a.inquiry_code");
                }
                f1.add(fc);
            }
        }
        CommonUtils.addFilters(f1,queryWrapper3);

        QueryWrapper<Inquiry> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.isNotNull("FIM_user_name");
        f1 = new ArrayList<>();
        String[] str4 = {"salesman_name"};
        for(String s : str4){
            if(map.containsKey(s)){
                FilterCriteria f = filters.get(map.get(s));
                f.setColName("FIM_user_name");
                f1.add(f);
            }
        }
        CommonUtils.addFilters(f1,queryWrapper4);

        QueryWrapper<Inquiry> queryWrapper5 = new QueryWrapper<>();
        queryWrapper5.isNotNull("FIM_user_name");
        f1 = new ArrayList<>();
        String[] str5 = {"created_user_name"};
        for(String s : str5){
            if(map.containsKey(s)){
                FilterCriteria f = filters.get(map.get(s));
                f.setColName("FIM_user_name");
                f1.add(f);
            }
        }
        CommonUtils.addFilters(f1,queryWrapper5);

        QueryWrapper<Inquiry> queryWrapper6 = new QueryWrapper<>();
        queryWrapper6.isNotNull("customer_name");
        f1 = new ArrayList<>();
        String[] str6 = {"customer_name"};
        for(String s : str6){
            if(map.containsKey(s)){
                f1.add(filters.get(map.get(s)));
            }
        }
        CommonUtils.addFilters(f1,queryWrapper6);

        QueryWrapper<Inquiry> queryWrapper7 = new QueryWrapper<>();
        f1 = new ArrayList<>();
        String[] str7 = {"customer_type","item_type","inquiry_type","delay","order_delivery_progress","customize"};
        for(String s : str7){
            if(map.containsKey(s)){
                f1.add(filters.get(map.get(s)));
            }
        }
        CommonUtils.addFilters(f1,queryWrapper7);
        queryWrapper7.isNotNull("customer_name")
                .isNotNull("created_user_name")
                .isNotNull("salesman_name")
                .and(username!=null,o->o.eq(username!=null,"created_user_name",username)
                        .or().eq(username!=null,"salesman_name",username));


        // 重命名 (原默认为ew)
        queryWrapper1.setParamAlias("qw1");
        queryWrapper2.setParamAlias("qw2");
        queryWrapper3.setParamAlias("qw3");
        queryWrapper4.setParamAlias("qw4");
        queryWrapper5.setParamAlias("qw5");
        queryWrapper6.setParamAlias("qw6");
        //queryWrapper7.setParamAlias("qw7");
        return salesOrderVoMapper.selectListByFilter(queryWrapper1,queryWrapper2,
                queryWrapper3,queryWrapper4,queryWrapper5,queryWrapper6,queryWrapper7);
    }


    @Override
    public List<String> getDocumentNumberFormat(long type,int length) {
        List<String> res = new ArrayList<>();
        String s = "";
        if(ORDER_TYPE_XD == type){
            s += "XSXD";
        }else if(ORDER_TYPE_YC == type){
            s += "XSYC";
        }else if(ORDER_TYPE_YG == type){
            s += "XSYG";
        }else {
            return res;
        }
        // 拼接月份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String date = sdf.format(new Date());
        s += date;
        // 获取当月的数量
        ReentrantLock lock = new ReentrantLock();
        Long code = 0L;
        lock.lock();
        try{
            code = inquiryCodeMapper.getMonth(type,date);
            code = code==null?0:code;
            inquiryCodeMapper.updateMaxMonthString(date,code+length,type);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        for(int i=1;i<=length;++i){
            long now = code+i;
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                temp.append(now % 10);
                now/= 10;
            }
            res.add(s+temp.reverse());
        }
        return res;
    }

    @Override
    public boolean isValidType(int inquiryType){
        return inquiryType == ORDER_TYPE_YC || inquiryType == ORDER_TYPE_XD ||
                inquiryType == ORDER_TYPE_PO || inquiryType == ORDER_TYPE_PR || inquiryType == ORDER_TYPE_YG;
    }
    @Override
    //主要就是判断订单是否有效
    public Map<String,Object> addValid(Inquiry inquiry) {
        Map<String,Object> map = new HashMap<>();
        if(inquiry.getItemId() == null || null == itemService.findItemById(inquiry.getItemId())) {
            map.put("error","物料不存在！");
            return map;
        }
        if(inquiry.getSaleNum() == null || inquiry.getSaleNum() < 1){
            map.put("error","销售数量为空或不合法！");
            return map;
        }
        if(inquiry.getCustomerId() == null || null == customerService.findCustomerById(inquiry.getCustomerId())){
            map.put("error","客户为空或不存在！");
            return map;
        }
        if(inquiry.getInquiryType() == null || !isValidType(inquiry.getInquiryType())){
            map.put("error","订单状态为空或不合法！");
            return map;
        }
        if(inquiry.getSalesmanId() == null || userService.findUserById(inquiry.getSalesmanId())==null){
            map.put("error","销售员为空或不存在！");
            return map;
        }

        if (inquiry.getRemark() == null) {
            inquiry.setRemark("");
        }

        if (inquiry.getExpectedTime() == null ) {
            if(inquiry.getInquiryType()== ORDER_TYPE_XD) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(9999, Calendar.DECEMBER, 31); // 设置为9999年12月31日
                inquiry.setExpectedTime(calendar.getTime());

            }else if(inquiry.getInquiryType()==INQUIRY_INIT_TYPE_YC){

                map.put("error","期待发货日期不存在");
                return map;
            }
        }else if (inquiry.getExpectedTime().before(new Date())){
            map.put("error","期待发货日期早于当前时间");
            return map;
        }
        return map;
    }

    @Override
    public Inquiry getInquiryById(Long orderId) {
        LambdaQueryWrapper<Inquiry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Inquiry::getState,-1)
                .eq(Inquiry::getInquiryId,orderId);
        return inquiryMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        //创建一个LambdaUpdateWrapper对象，用于构建更新条件和操作
        LambdaUpdateWrapper<Inquiry> updateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件和更新操作：
        //     1. 确保订单ID与传入的订单ID相匹配。
        //     2. 确保订单状态不等于 -1（-1 可能表示已删除的状态）。
        //     3. 设置订单状态为 -1，以表示订单已删除。
        updateWrapper.eq(Inquiry::getInquiryId,orderId)
                .ne(Inquiry::getState,-1)
                .set(Inquiry::getState,-1);
        //调用update方法，传入设置的查询条件，执行更新
        //返回更新影响的行数是否等于 1，如果等于 1，表示更新成功，返回 true，否则返回 false。
        return inquiryMapper.update(null,updateWrapper)==1;
    }

    @Override
    public Integer transferType(String inquiryType) {
        int type = -1;
        if(StringUtils.isNotBlank(inquiryType)){
            if(inquiryType.contains("YC")){
                type = ORDER_TYPE_YC;
            }else if(inquiryType.contains("XD")){
                type = ORDER_TYPE_XD;
            }
        }
        return type;
    }

    @Override
    public Map<String,Object> checkAddByExcel(InquiryModel inquiryModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        Inquiry inquiry = new Inquiry();
        // 添加物料id
        Item item = itemService.findItemByCode(inquiryModel.getItemCode());
        if(item == null){
            map.put("error","第"+rowIndex+"行的物料编码在数据库中不存在，请核对");
            return map;
        }
        if(!item.getItemName().equals(inquiryModel.getItemName())){
            map.put("error","第"+rowIndex+"行的物料编码和物料名称在数据库中不是对应的，请核对");
            return map;
        }
        if(inquiryModel.getItemType() == null || item.getItemType()!=itemService.transferItemType(inquiryModel.getItemType())){
            map.put("error","第"+rowIndex+"行的物料编码和产品类型在数据库中不是对应的，请核对");
            return map;
        }
        inquiry.setItemId(item.getId());
        // 添加客户id
        Customer c = customerService.findCustomerByName(inquiryModel.getCustomerName());
        if(c==null){
            map.put("error","第"+rowIndex+"行的客户名称在数据库中不存在，请核对");
            return map;
        }
        String ct = customerTypeService.getCustomerTypeByRule(c.getFCustId(),item.getId());
        if(ct==null || inquiryModel.getCustomerType() == null || !ct.equals(inquiryModel.getCustomerType())){
            map.put("error","第"+rowIndex+"行的客户类型与数据库对应关系不匹配或不存在，请核对");
            return map;
        }
        inquiry.setCustomerId(c.getFCustId());
        // 添加销售员id
        User salesman = userService.findSalesmanByName(inquiryModel.getSalesmanName());
        if(salesman == null){
            map.put("error","第"+rowIndex+"行的销售员名称在数据库中不存在，请核对");
            return map;
        }
        inquiry.setSalesmanId(salesman.getId());
        // 设置创建人id
        inquiry.setCreatedUser(hostHolder.getUser().getId());
        // 设置状态为保存
        inquiry.setState(0);
        // 设置订单类型
        int type;
        if((type = transferType(inquiryModel.getInquiryType()))==-1){
            map.put("error","第"+rowIndex+"行的订单类型有误，请修改并重试！");
            return map;
        }
        inquiry.setInquiryType(type);
        // 设置订单编号
        inquiry.setInquiryCode(getDocumentNumberFormat(type,1).get(0));
        // 设置创建时间
        inquiry.setCreatedTime(new Date());

        // 设置备注
        String remark = inquiryModel.getRemark();
        if (remark == null ) {
            remark = ""; // 赋一个“”代替null
        }
        inquiry.setRemark(remark);


        // 设置相关时间
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setLenient(false);
            if(inquiryModel.getArrangedTime()!=null){
                inquiry.setArrangedTime(sdf.parse(inquiryModel.getArrangedTime()));
            }

            if (inquiryModel.getExceptedTime() == null) {
                // 对订单类型进行判断
                if (inquiry.getInquiryType() == ORDER_TYPE_XD) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(9999, Calendar.DECEMBER, 31); // 设置为9999年12月31日
                    inquiry.setExpectedTime(calendar.getTime());
                } else if (inquiry.getInquiryType() == INQUIRY_INIT_TYPE_YC) {
                    map.put("error", "期待发货日期不存在");
                    return map;
                }
            } else {

                inquiry.setExpectedTime(sdf.parse(inquiryModel.getExceptedTime()));
                Date parsedExpectedTime = sdf.parse(inquiryModel.getExceptedTime());

                if (parsedExpectedTime.before(new Date())) {
                    map.put("error", "期待发货日期早于当前时间");
                    return map;
                }
            }

        }catch (Exception e){
            map.put("error","第"+rowIndex+"行的时间数据有误，请检查！");
            return map;
        }
        /*// 设置备注
        inquiry.setRemark(inquiryModel.getRemark());*/
        // 设置数量
        try {
            inquiry.setSaleNum(Long.parseLong(inquiryModel.getNum()));
        }catch (Exception e) {
            map.put("error","第"+rowIndex+"行的数量可能不是数字或长度有误，请检查！");
            return map;
        }
        map.put("inquiry",inquiry);
        return map;
    }

    @Override
    public boolean containsCode(String inquiryCode) {
        if(StringUtils.isBlank(inquiryCode)) {
            return false;
        }
        LambdaQueryWrapper<Inquiry> lqw = new LambdaQueryWrapper<>();
        lqw.select(Inquiry::getInquiryCode).eq(Inquiry::getInquiryCode,inquiryCode)
                .ne(Inquiry::getState,-1);
        return inquiryMapper.selectList(lqw).size()>0;
    }

    @Override
    public Integer updateByInquiry(List<Inquiry> success) {
        return inquiryMapper.ipdateByInquiry(success);
    }

    @Override
    public List<Inquiry> getInquiryCodeLikeList(String key) {
        LambdaQueryWrapper<Inquiry> lqw = new LambdaQueryWrapper<>();
        lqw.select(Inquiry::getInquiryCode).like(Inquiry::getInquiryCode,key);
        return inquiryMapper.selectList(lqw);
    }

    @Override
    public List<String> getInquiryTypeList(String key) {
        return inquiryMapper.getInquiryTypeList("%"+key+"%");
    }

    @Override
    public List<String> getStateList() {
        return inquiryMapper.getStateList();
    }

    @Override
    public int updateState(Long inquiryId, int i) {
        LambdaUpdateWrapper<Inquiry> luw = new LambdaUpdateWrapper<>();
        luw.set(Inquiry::getState,i).eq(Inquiry::getInquiryId,inquiryId);
        return inquiryMapper.update(null,luw);
    }

    public Result update_InquiryAllowInquiry(Long inquiryId) {
        // 获取当前登录用户
        User currentUser = hostHolder.getUser();

        // 检查当前用户是否是管理员
        if (currentUser.getUserType() == USER_TYPE_ADMIN) {
            // 如果是管理员，则更新订单的 allow_inquiry 字段为 1
            inquiryMapper.updateInquiryAllowInquiry(inquiryId);
            return Result.message("允许该订单询单");
        } else {
            return Result.message("仅管理员可进行该操作");
        }
    }

}
