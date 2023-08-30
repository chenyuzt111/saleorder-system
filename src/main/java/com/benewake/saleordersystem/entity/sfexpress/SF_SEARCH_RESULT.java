package com.benewake.saleordersystem.entity.sfexpress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xsong
 * date:2022/11/8 14:40
 * 描述：顺丰速运查询返回结果
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SF_SEARCH_RESULT {

    //这个字段保存了接口响应的唯一标识。在顺丰的响应中，可能会包含一个用于标识响应的唯一 ID，以便跟踪和区分不同的响应。
    private String apiResponseID;


    //这个字段保存了接口的错误消息。如果在调用接口时发生了错误，顺丰可能会在这里提供相关的错误信息，以便开发者了解问题所在。
    private String apiErrorMsg;



    //这个字段保存了接口的结果码。结果码是一个代表接口调用结果的编码，通常用于标识请求是否成功、失败或其他状态。
    private String apiResultCode;



    //这个字段保存了接口的结果数据。在一些情况下，接口可能会返回一些附加的数据作为结果。这个字段通常用来存储这些数据。
    private String apiResultData;

}
