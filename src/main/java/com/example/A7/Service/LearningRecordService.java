package com.example.A7.Service;

import com.example.A7.DO.LearningRecordDO;
import com.example.A7.Model.Result;

import java.util.List;
import java.util.Map;

public interface LearningRecordService {
    /**
     * 记录学习行为
     */
    Result<LearningRecordDO> recordLearning(LearningRecordDO learningRecord);
    
    /**
     * 更新学习进度
     */
    Result<LearningRecordDO> updateLearningProgress(Long id, Integer progress, Integer duration, Integer lastPosition);
    
    /**
     * 获取学生的学习记录
     */
    Result<List<LearningRecordDO>> getStudentLearningRecords(Long studentId);
    
    /**
     * 获取学生在特定资源上的学习记录
     */
    Result<LearningRecordDO> getStudentResourceRecord(Long studentId, Long resourceId);
    
    /**
     * 获取学生在特定课程的学习记录
     */
    Result<List<LearningRecordDO>> getStudentCourseRecords(Long studentId, Long courseId);
    
    /**
     * 获取资源的学习统计
     */
    Result<Map<String, Object>> getResourceStatistics(Long resourceId);
    
    /**
     * 获取学生的课程进度统计
     */
    Result<Map<String, Object>> getStudentCourseProgress(Long studentId, Long courseId);
    
    /**
     * 获取课程的整体学习统计
     */
    Result<Map<String, Object>> getCourseStatistics(Long courseId);
} 