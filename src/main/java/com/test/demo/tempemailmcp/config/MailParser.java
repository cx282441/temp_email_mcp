package com.test.demo.tempemailmcp.config;

/**
 * @author admin
 * @date 2025/12/24
 * @description
 */
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.test.demo.tempemailmcp.entity.EmailRecord;
import com.test.demo.tempemailmcp.entity.MailDto;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MailParser {

    /**
     * 转换大模型返回的json字符串为邮件内容
     * @param jsonObject
     * @return
     */
    public static String convertLlm(JSONObject jsonObject, EmailRecord emailRecord){

        StringBuffer sb= new StringBuffer();
        Integer emailCount = jsonObject.getInt("count");
        if (emailCount == 0 ){
            sb.append("还未收到邮件,请稍后再试!\n");
            return sb.toString();
        }
        sb.append("一共有").append(emailCount).append("条邮件\n");
        sb.append("以下是邮件内容：\n");
        JSONArray emails = jsonObject.getJSONArray("results");
        for (int i = 0; i < emails.size(); i++) {
            JSONObject email = emails.getJSONObject(i);
            MailDto mailDto = parse(email.getStr("raw"));
            sb.append("发件人：").append(email.getStr("source")).append("\n");
            sb.append("收件人：").append(email.getStr("address")).append("\n");
            sb.append("主题：").append(mailDto.getSubject()).append("\n");
            sb.append("内容：").append(mailDto.getContent()).append("\n\n");
        }
        sb.append("更多详情：[点击查看](https://mail.cx22722.top/?jwt=")
                .append(emailRecord.getJwt())
                .append(")\n");

        return sb.toString();
    }
    public static MailDto parse(String raw) {
        try {
            MimeMessage message = parseMessage(raw);
            MailDto mailDto = new MailDto();
            mailDto.setSubject(message.getSubject());
            mailDto.setContent(cleanContent(message.getContent().toString()));
            return mailDto;
        } catch (Exception e) {
            throw new RuntimeException("邮件解析失败", e);
        }
    }

    private static String parseAddress(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        InternetAddress ia = (InternetAddress) addresses[0];
        return StrUtil.format("{} <{}>",
                StrUtil.nullToEmpty(ia.getPersonal()),
                ia.getAddress());
    }

    /** 给大模型前一定要做内容清洗 */
    private static String cleanContent(String content) {
        if (content == null) {
            return null;
        }
        return content
                .replaceAll("\r\n", "\n")
                .trim();
    }


    public static MimeMessage parseMessage(String raw) {
        try {
            Session session = Session.getInstance(new Properties());
            return new MimeMessage(
                    session,
                    new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new RuntimeException("raw 邮件解析失败", e);
        }
    }
}
