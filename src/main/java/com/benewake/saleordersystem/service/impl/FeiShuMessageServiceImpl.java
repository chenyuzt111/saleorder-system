package com.benewake.saleordersystem.service.impl;

import com.benewake.saleordersystem.entity.Item;
import com.benewake.saleordersystem.service.FeiShuMessageService;
import com.benewake.saleordersystem.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class FeiShuMessageServiceImpl implements FeiShuMessageService {

    @Autowired
    private ItemService itemService;
    private  String feishuWebhookUrl ="https://www.feishu.cn/flow/api/trigger-webhook/2ecf907329207869a30febe4befc833b";


    public void sendMessage(String salemanName, String inquiryCode, Long itemId, Long saleNum) {
        try {
            // 创建URL对象
            URL url = new URL(feishuWebhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 创建新的变量 id 并赋值为 itemId
            Long id = itemId;

            Item itemById = itemService.findItemById(id);
            String itemcode=itemById.getItemCode();
            String itemname=itemById.getItemName();

            // 设置请求方法为POST
            connection.setRequestMethod("POST");

            // 启用输入输出流
            connection.setDoOutput(true);

            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            // 构建消息体
            String message = "销售员:" + salemanName + "<br>单据编号:" + inquiryCode +"<br>物料编码:"+itemcode+"<br>物料名称:"+itemname+"<br>数量:"+saleNum+ "<br>已询单，请关注！";
            String payload = "{\"text\": \"" + message + "\"}";
            // 将消息体写入输出流
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);

            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
