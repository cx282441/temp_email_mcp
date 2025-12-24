package com.test.demo.tempemailmcp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "email_records")
public class EmailRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "jwt", nullable = false)
    private String jwt;

    public EmailRecord() {
    }

    public EmailRecord(String userId, String emailAddress, String jwt) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.jwt = jwt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}