package com.benewake.saleordersystem.entity.VO;

import com.benewake.saleordersystem.entity.Inquiry;
import lombok.Data;

import java.util.List;
@Data
public class DevideInquiryVo {
    private List<Inquiry> inquiryList;
    private Integer devideNum;
}
