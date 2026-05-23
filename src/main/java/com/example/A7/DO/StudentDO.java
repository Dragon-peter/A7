package com.example.A7.DO;

import com.example.A7.Mapper.UserMapper;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生信息表
 * @TableName student
 */
@Data
public class StudentDO {

    /**
     * 学生ID（主键）
     */
    private Long id;

    /**
     * 关联用户表ID（user.id）
     */
    private Long userId;

    /**
     * 班级ID
     */
    private Integer classId;

    /**
     * 专业
     */
    private String major;

    /**
     * 入学年份
     */
    private Integer admissionYear;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 学生ID（主键）
     */
    public Long getId() {
        return id;
    }

    /**
     * 学生ID（主键）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 关联用户表ID（user.id）
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 关联用户表ID（user.id）
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 班级ID
     */
    public Integer getClassId() {
        return classId;
    }

    /**
     * 班级ID
     */
    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    /**
     * 专业
     */
    public String getMajor() {
        return major;
    }

    /**
     * 专业
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * 入学年份
     */
    public Integer getAdmissionYear() {
        return admissionYear;
    }

    /**
     * 入学年份
     */
    public void setAdmissionYear(Integer admissionYear) {
        this.admissionYear = admissionYear;
    }

    /**
     * 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}