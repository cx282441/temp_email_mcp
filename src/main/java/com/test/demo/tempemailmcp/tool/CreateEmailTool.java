package com.test.demo.tempemailmcp.tool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.test.demo.tempemailmcp.entity.EmailRecord;
import com.test.demo.tempemailmcp.repository.EmailRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * @author admin
 * @date 2025/12/23
 * @description
 */
@Component
public class CreateEmailTool {

    @Autowired
    private EmailRecordRepository emailRecordRepository;

    @McpTool(description = "创建一个临时邮箱")
    public String createTempEmail() {

        String userId = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            userId =  request.getHeader("X-DingTalk-User-Id");
        }
        //根据用户ID查询邮箱记录
        List<EmailRecord> byUserId = emailRecordRepository.findByUserId(userId);
        if (CollUtil.isNotEmpty(byUserId) && byUserId.size() >= 5) {
            StringBuffer sb = new StringBuffer();
            sb.append("您已创建多个邮箱,暂无法创建更多\n");
            sb.append("分别为:\n");
            for (EmailRecord emailRecord : byUserId) {
                sb.append(emailRecord.getEmailAddress()).append("\n");
            }
            return sb.toString();
        }
        HttpRequest post = HttpUtil.createPost("https://apimail.cx22722.top/api/new_address");
        post.header("accept", "application/json, text/plain, */*");
        post.header("accept-language", "zh,zh-CN;q=0.9");
        post.header("content-type", "application/json");
        post.header("origin", "https://em.bjedu.tech");
        post.header("user-agent", "Mozilla/5.0");
        post.header("x-lang", "zh");

        JSONObject param = new JSONObject();
        param.set("name", getRandomName());
        param.set("domain", "cx22722.top");
        post.body(JSONUtil.toJsonStr(param));
        String body = post.execute().body();
        Console.log("创建邮箱请求参数" + post);
        Console.log("创建邮箱返回" + body);
        JSONObject bodyObj = new JSONObject(body);
        String email = bodyObj.getStr("address");
        String jwt = bodyObj.getStr("jwt");

        EmailRecord emailRecord = new EmailRecord();
        emailRecord.setUserId(userId);
        emailRecord.setEmailAddress(email);
        emailRecord.setJwt(jwt);
        //保存到数据库
        emailRecordRepository.save(emailRecord);

        
        return "✿✿ヽ(°▽°)ノ✿ 临时邮箱创建成功，邮箱地址为：" + email + "，记录ID：" + emailRecord.getId();
    }

    /**
     * 生成一个随机的邮箱用户名 8位数英文加+数字
     * @return
     */
    private String getRandomName() {
        return RandomUtil.randomString(8);
    }
}