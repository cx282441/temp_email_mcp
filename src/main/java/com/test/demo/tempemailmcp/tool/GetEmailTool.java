package com.test.demo.tempemailmcp.tool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.OS;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.test.demo.tempemailmcp.config.MailParser;
import com.test.demo.tempemailmcp.entity.EmailRecord;
import com.test.demo.tempemailmcp.repository.EmailRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author admin
 * @date 2025/12/23
 * @description
 */
@Component
public class GetEmailTool {

    @Autowired
    private EmailRecordRepository emailRecordRepository;

    /**
     * 根据邮箱地址获取最近的邮件
     * @param email
     * @return
     */
    @McpTool(description = "根据邮箱地址获取最近的邮件")
    public String getEmailList(@McpToolParam(description = "邮箱") String email) {

        String userId = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            userId =  request.getHeader("X-DingTalk-User-Id");
        }
        EmailRecord byUserIdAndEmailAddress = emailRecordRepository.findByUserIdAndEmailAddress(userId, email);
        if (byUserIdAndEmailAddress == null) {
            return "请检查邮箱地址: [" + email + "]是否正确";
        }

        HttpRequest get = HttpUtil.createGet("https://apimail.cx22722.top/api/mails?limit=20&offset=0");
        get.header("Content-Type", "application/json");
        get.header("Authorization", "Bearer " + byUserIdAndEmailAddress.getJwt());
        get.header("sec-ch-ua-mobile", "?0");
        get.header("sec-ch-ua-platform", "macOS");
        get.header("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"");
        get.header("sec-fetch-dest", "empty");
        get.header("sec-fetch-mode", "cors");
        get.header("sec-fetch-site", "same-site");
        get.header("user-agent", "Mozilla/5.0 ");
        String getBody = get.execute().body();
        JSONObject jsonObj = JSONUtil.parseObj(getBody);
        return MailParser.convertLlm(jsonObj,byUserIdAndEmailAddress);
    }
}
