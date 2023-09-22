package com.benewake.saleordersystem.Email;

import com.benewake.saleordersystem.service.MailService;
import org.junit.Assert;//注意这个包是junit的，不是自带的
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xyp
 * @create 2022-10-07 15:07
 * @describe
 */
@SpringBootTest
public class SpringBootMailTest {

    @Autowired(required = false)
    private MailService mailService;

    @Test
    public void sendSimpleText(){
        String to="zhaotong@benewake.com";
        String title="标题：简单的文本发送测试";
        String content="简单的文本";
        Assert.assertTrue(mailService.sendSimpleText(to,title,content));
    }


    @Test
    public void sendWithImageHtml(){

        // 邮件接收者的邮箱地址
        String to = "wangxiaoxi@benewake.com";
        // 邮件主题
        String subject = "邮件主题";

        // 邮件内容，包括HTML内容
        String htmlContent = "<html><body>" +
                "<image width='40%'  src='cid:test1'> </image>"+
                "<p><strong><h2 style='color:#007FFF'>赵同 / Tong</h2></strong></p>" +
                "<p><strong>岗位名称 / 计划与物流团队实习生</strong></p>" +
                "<p><strong>zhaotong@benewake.com</strong></p>" +
                "<p><strong>+86-15538379765</strong></p>" +
                "<p><strong>北京市海淀区创业路六号自主创新大厦3层3030</strong></p>" +
                "<p><strong>No. 3030, 3rd Floor, Independent Innovation Building, No. 6 Chuangye Road, Haidian District, Beijing</strong></p>" +
                "<p><strong>北醒（北京）光子科技有限公司</strong></p>" +
                "<p><strong>Benewake (Beijing) Co., Ltd.</strong></p>" +
                "<br>" +
                "<a href='http://www.benewake.com/'><image width='50%' src='cid:test2'> </image></a>"+
                "<p><strong>注意：</strong>本电子邮件及所附任何文件都属机密且受到法律保护，仅供特定接收者使用，不允许任何无关第三方使用该信息。如并非本邮件预期的接收者，敬请切勿披露、复制、打印、转发或分发本邮件或其任何内容，亦请勿依本邮件之任何内容而采取任何行动。如有上述情况发生或收到之邮件不清或有缺失，敬请立即通过电子邮件或电话与发件人联系，并将其从您的计算机系统中删除。如接收者违反前述事宜，北醒将保留依法追求其法律责任之权利。</p>" +
                "<p><strong>Note:</strong> The information and any files attached in this e-mail are confidential and legally privileged, and only intended solely for recipient. Access to this message by any irrelevant third party is not permitted. If you are not the intended recipient, please do not disclose, copy, print, forward, diffuse or take any action according to any of the contents of this email. If any of the above situations occur or the email received is unclear or missing, please contact the sender immediately by email or telephone and delete it from your computer system. If the recipient violates the aforementioned, Benewake shall reserve rights to take any legal action necessary against all relevant unlawful information leakage.</p>" +
                "</body></html>";

        // 内联的图片CID数组（如果没有内联图片，可以传递空数组）
        String[] cids = new String[]{"test1","test2"};
        // 内联图片对应的本地文件路径数组（如果没有内联图片，可以传递空数组）
        String[] filePaths = new String[]{
                "E:\\北醒文件\\测试版本\\saleorder-system-main\\src\\main\\java\\com\\benewake\\saleordersystem\\picture\\img.png",
                "E:\\北醒文件\\测试版本\\saleorder-system-main\\src\\main\\java\\com\\benewake\\saleordersystem\\picture\\click.png"
        };

        // 调用邮件服务发送邮件
        boolean result = mailService.sendWithImageHtml(to, subject, htmlContent, cids, filePaths);

        // 断言邮件发送结果
        assert(result);
    }


    }

