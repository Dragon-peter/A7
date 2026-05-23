package com.example.A7.Controller;

import com.example.A7.DO.LearningRecordDO;
import com.example.A7.Model.Result;
import com.example.A7.Service.LearningRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
public class LearningRecordController {

    @Autowired
    private LearningRecordService learningRecordService;

    /**
     * 记录学习行为
     */
    @PostMapping("/record")
    @ResponseBody
    public Result<LearningRecordDO> recordLearning(@RequestBody LearningRecordDO learningRecord) {
        return learningRecordService.recordLearning(learningRecord);
    }

    /**
     * 更新学习进度
     */
    @PutMapping("/progress/{id}")
    @ResponseBody
    public Result<LearningRecordDO> updateLearningProgress(
            @PathVariable Long id,
            @RequestParam(required = false) Integer progress,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Integer lastPosition) {
        return learningRecordService.updateLearningProgress(id, progress, duration, lastPosition);
    }

    /**
     * 获取学生的所有学习记录
     */
    @GetMapping("/student/{studentId}")
    @ResponseBody
    public Result<List<LearningRecordDO>> getStudentLearningRecords(@PathVariable Long studentId) {
        return learningRecordService.getStudentLearningRecords(studentId);
    }

    /**
     * 获取学生对特定资源的学习记录
     */
    @GetMapping("/student/{studentId}/resource/{resourceId}")
    @ResponseBody
    public Result<LearningRecordDO> getStudentResourceRecord(
            @PathVariable Long studentId,
            @PathVariable Long resourceId) {
        return learningRecordService.getStudentResourceRecord(studentId, resourceId);
    }

    /**
     * 获取学生在特定课程的学习记录
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    @ResponseBody
    public Result<List<LearningRecordDO>> getStudentCourseRecords(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return learningRecordService.getStudentCourseRecords(studentId, courseId);
    }

    /**
     * 获取资源的学习统计
     */
    @GetMapping("/statistics/resource/{resourceId}")
    @ResponseBody
    public Result<Map<String, Object>> getResourceStatistics(@PathVariable Long resourceId) {
        return learningRecordService.getResourceStatistics(resourceId);
    }

    /**
     * 获取学生的课程进度统计
     */
    @GetMapping("/progress/student/{studentId}/course/{courseId}")
    @ResponseBody
    public Result<Map<String, Object>> getStudentCourseProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return learningRecordService.getStudentCourseProgress(studentId, courseId);
    }

    /**
     * 获取课程的整体学习统计
     */
    @GetMapping("/statistics/course/{courseId}")
    @ResponseBody
    public Result<Map<String, Object>> getCourseStatistics(@PathVariable Long courseId) {
        return learningRecordService.getCourseStatistics(courseId);
    }
} 