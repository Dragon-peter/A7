package com.example.A7.Service;

import com.example.A7.DO.CourseDO;
import com.example.A7.DO.ChapterDO;
import com.example.A7.DO.ResourceDO;
import com.example.A7.DO.StudentCourseDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

import java.util.List;
import java.util.Map;

public interface CourseService {
    /**
     * 创建课程
     */
    Result<CourseDO> createCourse(CourseDO courseDO);
    
    /**
     * 更新课程
     */
    Result<CourseDO> updateCourse(CourseDO courseDO);
    
    /**
     * 删除课程
     */
    Result<Boolean> deleteCourse(Long id);
    public Result<List<StudentCourseDO>> getCoursesByStudentId(Long studentId);
    /**
     * 获取课程详情
     */
    Result<CourseDO> getCourseById(Long id);
    
    /**
     * 获取所有课程（分页）
     */
    Result<Paging<CourseDO>> findAllCourses(Integer pageNum, Integer pageSize);
    
    /**
     * 按教师ID获取课程列表
     */
    Result<List<CourseDO>> getCoursesByTeacherId(Long teacherId);
    
    /**
     * 添加章节
     */
    Result<ChapterDO> addChapter(ChapterDO chapterDO);
    
    /**
     * 更新章节
     */
    Result<ChapterDO> updateChapter(ChapterDO chapterDO);
    
    /**
     * 删除章节
     */
    Result<Boolean> deleteChapter(Long id);
    
    /**
     * 获取课程下的所有章节
     */
    Result<List<ChapterDO>> getChaptersByCourseId(Long courseId);
    
    /**
     * 上传资源
     */
    Result<ResourceDO> addResource(ResourceDO resourceDO);
    
    /**
     * 更新资源
     */
    Result<ResourceDO> updateResource(ResourceDO resourceDO);
    
    /**
     * 删除资源
     */
    Result<Boolean> deleteResource(Long id);
    
    /**
     * 获取章节下的所有资源
     */
    Result<List<ResourceDO>> getResourcesByChapterId(Long chapterId);
    
    /**
     * 学生选课
     */
    Result<StudentCourseDO> studentEnrollCourse(Long studentId, Long courseId);
    
    /**
     * 学生退课
     */
    Result<Boolean> studentDropCourse(Long id);
    

    
    /**
     * 获取课程统计信息
     */
    Result<Map<String, Object>> getCourseStatistics(Long courseId);
} 