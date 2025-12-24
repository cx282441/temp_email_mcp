package com.test.demo.tempemailmcp.tool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
public class GetEmailListTool {
    @Autowired
    private EmailRecordRepository emailRecordRepository;

    @McpTool(description = "查看我所有的邮箱,获取我的临时邮箱列表")
    public String getEmailListByUserId() {
        String userId = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            userId =  request.getHeader("X-DingTalk-User-Id");
        }
        if (StrUtil.isBlank(userId)) {
            return "获取用户失败!";
        }
        List<EmailRecord> byUserId = emailRecordRepository.findByUserId(userId);
        if (CollUtil.isEmpty(byUserId)) {
            return "暂无邮箱,告诉我创建一个临时邮箱吧";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= byUserId.size() ; i++) {
            sb.append("邮箱").append(i).append(":  \n").append(byUserId.get(i - 1).getEmailAddress()).append("\n\n");
        }
        sb.append("就是这些了");
        return  sb.toString();
    }
}
