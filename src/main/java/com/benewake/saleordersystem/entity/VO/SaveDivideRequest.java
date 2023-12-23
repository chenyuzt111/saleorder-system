package com.benewake.saleordersystem.entity.VO;

import com.benewake.saleordersystem.entity.Inquiry;
import lombok.Data;

import java.util.List;

/*保存拆分订单请求体*/
@Data
public class SaveDivideRequest {
    private List<Inquiry> inquiries;
    private String inquiryCode;
}
