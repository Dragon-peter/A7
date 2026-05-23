package com.example.A7.DO;

import lombok.Data;

/**
 * 习题表
 * @TableName exercise
 */
@Data
public class ExerciseDO {
    /**
     * 习题ID（主键）
     */
    private Long id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     * 习题标题
     */
    private String title;

    /**
     * 习题内容
     */
    private String content;

    /**
     * 习题类型（单选、多选、判断、简答等）
     */
    private String type;

    /**
     * 难度等级（1-5）
     */
    private Integer difficulty;

    /**
     * 章节ID（外键）
     */
    private Long chapterId;

    /**
     * 知识点ID（外键）
     */
    private Long knowledgePointId;

    /**
     * 参考答案
     */
    private String answer;

    /**
     * 解析说明
     */
    private String explanation;
} 