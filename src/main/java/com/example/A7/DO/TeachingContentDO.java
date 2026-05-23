package com.example.A7.DO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学内容数据对象
 */
@Data
public class TeachingContentDO {
    private Long id;
    private String title;
    private String courseTitle;
    private Long courseId;
    private String outline;
    private String knowledgePoints; // JSON格式存储知识点列表
    private Boolean includeCases;
    private Boolean includeExercises;
    private Integer teachingHours;
    private String baseContent; // 基础教学内容
    private String caseAnalysis; // 案例分析内容
    private String practiceExercises; // 实践练习内容
    private String teachingSuggestions; // 教学建议
    private String status; // 状态：draft, published
    private String generatedBy; // 生成方式：ai, teacher, mixed
    private Long teacherId; // 创建教师ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
