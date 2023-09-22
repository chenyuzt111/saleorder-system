package com.benewake.saleordersystem.controller;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.service.ItemService;
import com.benewake.saleordersystem.service.MailService;
import com.benewake.saleordersystem.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private HostHolder hostHolder;



    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody Inquiry inquiry) {

        User u = hostHolder.getUser();
        String name = u.getUsername();
        Long phone = u.getUserphone();
        String mail =u.getUsermail();


        Inquiry inquiry1= inquiry;
        String inquirycode = inquiry1.getInquiryCode();
        String itemname = itemService.findItemById(inquiry1.getItemId()).getItemName();

        // 邮件接收者的邮箱地址
        String to = "wangxiaoxi@benewake.com";
        // 邮件主题
        String subject = "邮件主题";

        // 邮件内容，包括HTML内容
        String htmlContent = "<html><body>" +
                "<p><strong>订单编号:"+inquirycode+"物料名称:"+itemname+"</strong></p>"+
                "<image width='40%'  src='cid:test1'> </image>"+
                "<p><strong><h2 style='color:#007FFF'>"+name+"</h2></strong></p>" +
                "<p><strong>岗位名称 / 计划与物流团队实习生</strong></p>" +
                "<p><strong>"+mail+"</strong></p>" +
                "<p><strong>+86-"+phone+"</strong></p>" +
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



        if (result) {
            return "邮件发送成功";
        } else {
            return "邮件发送失败";
        }
    }
}