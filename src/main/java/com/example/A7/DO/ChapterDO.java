package com.example.A7.DO;

import lombok.Data;

/**
 * 章节表
 * @TableName chapter
 */
@Data
public class ChapterDO {
    /**
     * 章节ID（主键）
     */
    private Long id;

    /**
     * 课程ID（外键）
     */
    private Long courseId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 排序序号
     */
    private Integer orderNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 章节描述
     */
    private String description;
} 