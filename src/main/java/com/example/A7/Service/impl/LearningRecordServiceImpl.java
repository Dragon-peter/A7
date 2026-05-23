package com.example.A7.Service.impl;

import com.example.A7.DO.LearningRecordDO;
import com.example.A7.DO.ResourceDO;
import com.example.A7.Mapper.ChapterMapper;
import com.example.A7.Mapper.CourseMapper;
import com.example.A7.Mapper.LearningRecordMapper;
import com.example.A7.Mapper.ResourceMapper;
import com.example.A7.Model.Result;
import com.example.A7.Service.LearningRecordService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class LearningRecordServiceImpl implements LearningRecordService {
    private static final Logger log = LoggerFactory.getLogger(LearningRecordServiceImpl.class);
    @Autowired
    private LearningRecordMapper learningRecordMapper;
    
    @Autowired
    private ResourceMapper resourceMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Result<LearningRecordDO> recordLearning(LearningRecordDO learningRecord) {
        Result<LearningRecordDO> result = new Result<>();
        try {
            // 检查是否已有该学生对该资源的学习记录
            LearningRecordDO existingRecord = learningRecordMapper.findByStudentIdAndResourceId(
                    learningRecord.getStudentId(), learningRecord.getResourceId());
            
            if (existingRecord != null) {
                // 更新已有记录
                existingRecord.setProgress(learningRecord.getProgress());
                existingRecord.setDuration(existingRecord.getDuration() + learningRecord.getDuration());
                existingRecord.setLastPosition(learningRecord.getLastPosition());
                existingRecord.setUpdateTime(LocalDateTime.now());
                
                int rows = learningRecordMapper.update(existingRecord);
                if (rows > 0) {
                    return result.success(existingRecord);
                }
                return result.error("400", "更新学习记录失败");
            } else {
                // 创建新记录
                learningRecord.setCreateTime(LocalDateTime.now());
                learningRecord.setUpdateTime(LocalDateTime.now());
                
                int rows = learningRecordMapper.add(learningRecord);
                if (rows > 0) {
                    return result.success(learningRecord);
                }
                return result.error("400", "创建学习记录失败");
            }
        } catch (Exception e) {
            log.error("记录学习行为失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<LearningRecordDO> updateLearningProgress(Long id, Integer progress, Integer duration, Integer lastPosition) {
        Result<LearningRecordDO> result = new Result<>();
        try {
            // 获取当前记录
            LearningRecordDO record = learningRecordMapper.findById(id);
            if (record == null) {
                return result.error("404", "学习记录不存在");
            }
            
            // 更新记录
            if (progress != null) {
                record.setProgress(progress);
            }
            if (duration != null) {
                record.setDuration(record.getDuration() + duration);
            }
            if (lastPosition != null) {
                record.setLastPosition(lastPosition);
            }
            record.setUpdateTime(LocalDateTime.now());
            
            int rows = learningRecordMapper.update(record);
            if (rows > 0) {
                return result.success(record);
            }
            return result.error("400", "更新学习进度失败");
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<LearningRecordDO>> getStudentLearningRecords(Long studentId) {
        Result<List<LearningRecordDO>> result = new Result<>();
        try {
            List<LearningRecordDO> records = learningRecordMapper.findByStudentId(studentId);
            return result.success(records);
        } catch (Exception e) {
            log.error("获取学生学习记录失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<LearningRecordDO> getStudentResourceRecord(Long studentId, Long resourceId) {
        Result<LearningRecordDO> result = new Result<>();
        try {
            LearningRecordDO record = learningRecordMapper.findByStudentIdAndResourceId(studentId, resourceId);
            if (record != null) {
                return result.success(record);
            }
            return result.error("404", "未找到该资源的学习记录");
        } catch (Exception e) {
            log.error("获取学生资源学习记录失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<LearningRecordDO>> getStudentCourseRecords(Long studentId, Long courseId) {
        Result<List<LearningRecordDO>> result = new Result<>();
        try {
            // 获取课程下的所有章节ID
            List<Long> chapterIds = chapterMapper.findByCourseId(courseId)
                    .stream()
                    .map(chapter -> chapter.getId())
                    .collect(Collectors.toList());
            
            if (chapterIds.isEmpty()) {
                return result.success(new ArrayList<>());
            }
            
            // 获取章节下的所有资源ID
            List<Long> resourceIds = resourceMapper.findByChapterIds(chapterIds)
                    .stream()
                    .map(resource-> resource.getId())
                    .collect(Collectors.toList());
            
            if (resourceIds.isEmpty()) {
                return result.success(new ArrayList<>());
            }
            
            // 获取学生对这些资源的学习记录
            List<LearningRecordDO> records = learningRecordMapper.findByStudentIdAndResourceIds(studentId, resourceIds);
            return result.success(records);
        } catch (Exception e) {
            log.error("获取学生课程学习记录失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getResourceStatistics(Long resourceId) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            // 获取资源信息
            ResourceDO resource = resourceMapper.findById(resourceId);
            if (resource == null) {
                return result.error("404", "资源不存在");
            }
            
            // 统计数据
            int learnerCount = learningRecordMapper.countLearnersByResourceId(resourceId);
            int completedCount = learningRecordMapper.countCompletedByResourceId(resourceId);
            double averageProgress = learningRecordMapper.getAverageProgressByResourceId(resourceId);
            double averageDuration = learningRecordMapper.getAverageDurationByResourceId(resourceId);
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("resourceId", resourceId);
            statistics.put("title", resource.getTitle());
            statistics.put("learnerCount", learnerCount);
            statistics.put("completedCount", completedCount);
            statistics.put("completionRate", learnerCount > 0 ? (double) completedCount / learnerCount : 0);
            statistics.put("averageProgress", averageProgress);
            statistics.put("averageDuration", averageDuration);
            
            return result.success(statistics);
        } catch (Exception e) {
            log.error("获取资源统计失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getStudentCourseProgress(Long studentId, Long courseId) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            // 获取课程下的所有章节
            List<Map<String, Object>> chapterProgress = new ArrayList<>();
            List<Map<String, Object>> resourceProgress = new ArrayList<>();
            final int[] totalResources = {0};
            final int[] completedResources = {0};
            final int[] totalDuration = {0};
            
            // 遍历章节
            chapterMapper.findByCourseId(courseId).forEach(chapter -> {
                Map<String, Object> chapterData = new HashMap<>();
                chapterData.put("chapterId", chapter.getId());
                chapterData.put("title", chapter.getTitle());
                
                List<ResourceDO> resources = resourceMapper.findByChapterId(chapter.getId());
                int chapterTotalResources = resources.size();
                int chapterCompletedResources = 0;
                
                // 遍历章节中的资源
                for (ResourceDO resource : resources) {
                    LearningRecordDO record = learningRecordMapper.findByStudentIdAndResourceId(
                            studentId, resource.getId());
                    
                    Map<String, Object> resourceData = new HashMap<>();
                    resourceData.put("resourceId", resource.getId());
                    resourceData.put("title", resource.getTitle());
                    resourceData.put("type", resource.getType());
                    
                    if (record != null) {
                        resourceData.put("progress", record.getProgress());
                        resourceData.put("duration", record.getDuration());
                        resourceData.put("lastPosition", record.getLastPosition());
                        totalDuration[0] += record.getDuration();
                        
                        if (record.getProgress() >= 100) {
                            chapterCompletedResources++;
                            completedResources[0]++;
                        }
                    } else {
                        resourceData.put("progress", 0);
                        resourceData.put("duration", 0);
                        resourceData.put("lastPosition", 0);
                    }
                    
                    resourceProgress.add(resourceData);
                }
                
                totalResources[0] += chapterTotalResources;
                chapterData.put("totalResources", chapterTotalResources);
                chapterData.put("completedResources", chapterCompletedResources);
                chapterData.put("progress", chapterTotalResources > 0 
                        ? (double) chapterCompletedResources / chapterTotalResources * 100 : 0);
                
                chapterProgress.add(chapterData);
            });
            
            // 整体课程进度
            Map<String, Object> courseProgress = new HashMap<>();
            courseProgress.put("courseId", courseId);
            courseProgress.put("studentId", studentId);
            courseProgress.put("totalResources", totalResources[0]);
            courseProgress.put("completedResources", completedResources[0]);
            courseProgress.put("progress", totalResources[0] > 0 
                    ? (double) completedResources[0] / totalResources[0] * 100 : 0);
            courseProgress.put("totalDuration", totalDuration[0]);
            courseProgress.put("chapterProgress", chapterProgress);
            courseProgress.put("resourceProgress", resourceProgress);
            
            return result.success(courseProgress);
        } catch (Exception e) {
            log.error("获取学生课程进度失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCourseStatistics(Long courseId) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            // 获取课程信息
            if (courseMapper.findById(courseId) == null) {
                return result.error("404", "课程不存在");
            }
            
            // 课程统计数据
            int learnerCount = learningRecordMapper.countLearnersByCourseId(courseId);
            int completedCount = learningRecordMapper.countCompletedStudentsByCourseId(courseId);
            double averageProgress = learningRecordMapper.getAverageProgressByCourseId(courseId);
            
            // 资源统计
            List<Map<String, Object>> resourceStatistics = new ArrayList<>();
            // 获取课程下的所有章节
            List<Long> chapterIds = chapterMapper.findByCourseId(courseId)
                    .stream()
                    .map(chapter -> chapter.getId())
                    .collect(Collectors.toList());
            
            if (!chapterIds.isEmpty()) {
                // 获取章节下的所有资源
                List<ResourceDO> resources = resourceMapper.findByChapterIds(chapterIds);
                
                // 计算每个资源的统计信息
                for (ResourceDO resource : resources) {
                    int resourceLearnerCount = learningRecordMapper.countLearnersByResourceId(resource.getId());
                    int resourceCompletedCount = learningRecordMapper.countCompletedByResourceId(resource.getId());
                    double resourceAverageProgress = learningRecordMapper.getAverageProgressByResourceId(resource.getId());
                    
                    Map<String, Object> resourceData = new HashMap<>();
                    resourceData.put("resourceId", resource.getId());
                    resourceData.put("title", resource.getTitle());
                    resourceData.put("type", resource.getType());
                    resourceData.put("learnerCount", resourceLearnerCount);
                    resourceData.put("completedCount", resourceCompletedCount);
                    resourceData.put("completionRate", resourceLearnerCount > 0 
                            ? (double) resourceCompletedCount / resourceLearnerCount : 0);
                    resourceData.put("averageProgress", resourceAverageProgress);
                    
                    resourceStatistics.add(resourceData);
                }
            }
            
            // 组装结果
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("courseId", courseId);
            statistics.put("learnerCount", learnerCount);
            statistics.put("completedCount", completedCount);
            statistics.put("completionRate", learnerCount > 0 ? (double) completedCount / learnerCount : 0);
            statistics.put("averageProgress", averageProgress);
            statistics.put("resourceStatistics", resourceStatistics);
            
            return result.success(statistics);
        } catch (Exception e) {
            log.error("获取课程统计失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }
} 