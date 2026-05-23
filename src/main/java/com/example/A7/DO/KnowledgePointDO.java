package com.example.A7.DO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识点表
 * @TableName knowledge_point
 */
@Data
public class KnowledgePointDO {
    /**
     * 知识点ID（主键）
     */
    private Long id;

    /**
     * 知识点名称
     */
    private String name;

    /**
     * 课程ID（外键）
     */
    private Long courseId;

    /**
     * 章节ID（外键）
     */
    private Long chapterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 知识点描述
     */
    private String description;

    /**
     * 难度等级
     */
    private String difficulty;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
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