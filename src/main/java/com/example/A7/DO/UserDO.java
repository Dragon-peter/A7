package com.example.A7.DO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 系统用户表
 * @TableName user
 */
@Data
public class UserDO {
    /**
     * 用户ID（主键）
     */
    private Long id;

    /**
     * 用户名（唯一，用于登录）
     */
    private String username;

    /**
     * 加密后的密码（BCrypt等算法）
     */
    private String password;

    /**
     * 真实姓名（可选）
     */
    private String realName;

    /**
     * 邮箱（可选，用于通知）
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;


    /**
     * 角色：ADMIN（管理员）、TEACHER（教师）、STUDENT（学生）
     */
    private String role;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;



    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 用户ID（主键）
     */
    public Long getId() {
        return id;
    }

    /**
     * 用户ID（主键）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 用户名（唯一，用于登录）
     */
    public String getUsername() {
        return username;
    }

    /**
     * 用户名（唯一，用于登录）
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 加密后的密码（BCrypt等算法）
     */
    public String getPassword() {
        return password;
    }

    /**
     * 加密后的密码（BCrypt等算法）
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 真实姓名（可选）
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 真实姓名（可选）
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 邮箱（可选，用于通知）
     */
    public String getEmail() {
        return email;
    }

    /**
     * 邮箱（可选，用于通知）
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }



    /**
     * 角色：ADMIN（管理员）、TEACHER（教师）、STUDENT（学生）
     */
    public String getRole() {
        return role;
    }

    /**
     * 角色：ADMIN（管理员）、TEACHER（教师）、STUDENT（学生）
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 状态：1-启用，0-禁用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 状态：1-启用，0-禁用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }



    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}