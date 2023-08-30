package com.benewake.saleordersystem.config;

import com.benewake.saleordersystem.utils.BenewakeConstants;
import com.kingdee.bos.webapi.entity.IdentifyInfo;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lcs
 * 金蝶bean配置
 * @since 2023年6月27日 17:26
 */
@Configuration
public class KingdeeConfig implements BenewakeConstants {

    @Bean
    //创建返回一个名为K3CloudApi的Bean对象
    public K3CloudApi k3CloudApi(){
        //新建一个IdentifyInfo对象
        IdentifyInfo iden = new IdentifyInfo();
        //为IdentifyInfo对象设置一系列的属性，用于初始化金蝶API连接的配置参数
        iden.setUserName(X_KDAPI_USERNAME);
        iden.setAppId(X_KDAPI_APPID);
        iden.setdCID(X_KDAPI_ACCTID);
        iden.setAppSecret(X_KDAPI_APPSEC);
        iden.setlCID(X_KDAPI_LCID);
        iden.setServerUrl(X_KDAPI_SERVICEURL);
        iden.setOrgNum("100");
        return new K3CloudApi(iden);
    }
}
