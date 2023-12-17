package com.benewake.saleordersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.saleordersystem.entity.VO.DevideInquiryVo;
import com.benewake.saleordersystem.entity.VO.FilterCriteria;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.excel.model.InquiryModel;
import com.benewake.saleordersystem.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 订单应用接口
 * @author Lcs
 * @return
 *
 *
 */
public interface InquiryService extends IService<Inquiry> {

    /**
     * 将Excel数据导入到数据库
     * @param file
     * @return
     */
    Map<String, Object> saveDataByExcel(MultipartFile file);

    /**
     * 批量加入数据库
     * @param inquiries
     * @return
     */
    int insertLists(List<Inquiry> inquiries);

    /**
     * 根据条件筛选获得数据 若果filters==null 返回全部数据
     * @param filters
     * @return
     */
    List<Map<String,Object>> selectSalesOrderVoList(List<FilterCriteria> filters,String username);

    /**
     * 历史订单  根据条件筛选获得数据 若果filters==null 返回全部数据
     * @param filters
     * @return
     */
    List<Map<String,Object>> selectPastOrders(List<FilterCriteria> filters,String username);

    /**
     * 根据订单类型生成相应的单据编号
     * @param type
     * @param length
     * @return
     */
    List<String> getDocumentNumberFormat(long type,int length);

    /**
     * 新增询单时判断参数是否合法
     * @param inquiry
     * @return
     */
    Map<String,Object> addValid(Inquiry inquiry);

    /**
     * 修改时判断参数是否合法
     * @param inquiry
     * @return
     */
    Map<String,Object> updateValid(Inquiry inquiry);

    /**
     * 根据询单id获得对应的询单
     * @param orderId
     * @return
     */
    Inquiry getInquiryById(Long orderId);

    /**
     * 删除对应订单号的订单（逻辑删除)
     * @param orderId
     * @return
     */
    boolean deleteOrder(Long orderId);

    /**
     * 判断订单类型是否合法
     * @param inquiryType
     * @return
     */
    boolean isValidType(int inquiryType);

    /**
     * 将字符串形式的订单类型转换为数据库中的int形式      -1表示非法
     * @param inquiryType
     * @return
     */
    Integer transferType(String inquiryType);

    /**
     * 使用excel导入时进行参数检查
     * @param inquiryModel
     * @param rowIndex
     * @return
     */
    Map<String,Object> checkAddByExcel(InquiryModel inquiryModel, int rowIndex);

    /**
     * 判断单据编号是否存在
     * @param inquiryCode
     * @return
     */
    boolean containsCode(String inquiryCode);

    /**
     * 更新state 和 arrangeTime
     * @param success
     * @return
     */
    Integer updateByInquiry(List<Inquiry> success);

    /**
     * 获取单据编号模糊匹配结果
     *
     * @param key
     * @return
     */
    List<Inquiry> getInquiryCodeLikeList(String key);

    /**
     * 获取订单状态模糊匹配结果
     * @param key
     * @return
     */
    List<String> getInquiryTypeList(String key);

    /**
     * 获取所有订单中存在的state集合
     * @return
     */
    List<String> getStateList();

    /**
     * 更新单个订单的state
     * @param inquiryId
     * @param i
     * @return
     */
    int updateState(String inquiryCode, int i);

    /**
     * 允许某订单询单（为allowinquiry赋值）
     * @param inquiryId
     * @return
     */
    Result update_InquiryAllowInquiry(Long inquiryId);


    /**
     * 根据单据编号找到订单
     * @param inquiryCode
     * @return
     */
    Inquiry getInquiriesByCode(String inquiryCode);

    /**
     * 根据单据编号找到订单
     * @param devideInquiryVo
     * @return
     */
    List<Inquiry> splitInquiry(DevideInquiryVo devideInquiryVo);


    /**
     * 根据单据编号恢复删除订单
     * @param inquiryCodes
     * @return
     */
    int restoreOrders(List<String> inquiryCodes);
}
