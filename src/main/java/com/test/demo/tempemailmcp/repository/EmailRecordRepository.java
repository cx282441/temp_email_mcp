package com.test.demo.tempemailmcp.repository;

import com.test.demo.tempemailmcp.entity.EmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRecordRepository extends JpaRepository<EmailRecord, Long> {

    /**
     * 根据用户ID和邮箱地址查询邮件记录
     * @param userId 用户ID
     * @param emailAddress 邮箱地址
     * @return 邮件记录
     */
    EmailRecord findByUserIdAndEmailAddress(String userId, String emailAddress);
    /**
     * 根据用户ID查询所有邮件记录
     * @param userId 用户ID
     * @return 邮件记录列表
     */
    List<EmailRecord> findByUserId(String userId);
}