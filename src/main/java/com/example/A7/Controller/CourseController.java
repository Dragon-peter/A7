package com.example.A7.Controller;

import com.example.A7.DO.ChapterDO;
import com.example.A7.DO.CourseDO;
import com.example.A7.DO.ResourceDO;
import com.example.A7.DO.StudentCourseDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 创建课程
     */
    @PostMapping
    @ResponseBody
    public Result<CourseDO> createCourse(@RequestBody CourseDO courseDO) {
        return courseService.createCourse(courseDO);
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    @ResponseBody
    public Result<CourseDO> updateCourse(@PathVariable Long id, @RequestBody CourseDO courseDO) {
        courseDO.setId(id);
        return courseService.updateCourse(courseDO);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result<Boolean> deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Result<CourseDO> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    /**
     * 获取所有课程（分页）
     */
    @GetMapping("/list")
    @ResponseBody
    public Result<Paging<CourseDO>> findAllCourses(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return courseService.findAllCourses(pageNum, pageSize);
    }

    /**
     * 按教师ID获取课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    @ResponseBody
    public Result<List<CourseDO>> getCoursesByTeacherId(@PathVariable Long teacherId) {
        return courseService.getCoursesByTeacherId(teacherId);
    }

    /**
     * 添加章节
     */
    @PostMapping("/chapter")
    @ResponseBody
    public Result<ChapterDO> addChapter(@RequestBody ChapterDO chapterDO) {
        return courseService.addChapter(chapterDO);
    }

    /**
     * 更新章节
     */
    @PutMapping("/chapter/{id}")
    @ResponseBody
    public Result<ChapterDO> updateChapter(@PathVariable Long id, @RequestBody ChapterDO chapterDO) {
        chapterDO.setId(id);
        return courseService.updateChapter(chapterDO);
    }

    /**
     * 删除章节
     */
    @DeleteMapping("/chapter/{id}")
    @ResponseBody
    public Result<Boolean> deleteChapter(@PathVariable Long id) {
        return courseService.deleteChapter(id);
    }

    /**
     * 获取课程下的所有章节
     */
    @GetMapping("/{courseId}/chapters")
    @ResponseBody
    public Result<List<ChapterDO>> getChaptersByCourseId(@PathVariable Long courseId) {
        return courseService.getChaptersByCourseId(courseId);
    }

    /**
     * 上传资源
     */
    @PostMapping("/resource")
    @ResponseBody
    public Result<ResourceDO> addResource(@RequestBody ResourceDO resourceDO) {
        return courseService.addResource(resourceDO);
    }

    /**
     * 更新资源
     */
    @PutMapping("/resource/{id}")
    @ResponseBody
    public Result<ResourceDO> updateResource(@PathVariable Long id, @RequestBody ResourceDO resourceDO) {
        resourceDO.setId(id);
        return courseService.updateResource(resourceDO);
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/resource/{id}")
    @ResponseBody
    public Result<Boolean> deleteResource(@PathVariable Long id) {
        return courseService.deleteResource(id);
    }

    /**
     * 获取章节下的所有资源
     */
    @GetMapping("/chapter/{chapterId}/resources")
    @ResponseBody
    public Result<List<ResourceDO>> getResourcesByChapterId(@PathVariable Long chapterId) {
        return courseService.getResourcesByChapterId(chapterId);
    }

    /**
     * 学生选课
     */
    @PostMapping("/enroll")
    @ResponseBody
    public Result<StudentCourseDO> studentEnrollCourse(
            @RequestParam Long studentId, 
            @RequestParam Long courseId) {
        return courseService.studentEnrollCourse(studentId, courseId);
    }

    /**
     * 学生退课
     */
    @PostMapping("/drop")
    @ResponseBody
    public Result<Boolean> studentDropCourse(
            @RequestParam Long id
            ) {
        return courseService.studentDropCourse(id);
    }

    /**
     * 获取学生选修的课程列表
     */
    @GetMapping("/student/{studentId}")
    @ResponseBody
    public Result<List<StudentCourseDO>> getCoursesByStudentId(@PathVariable Long studentId) {
        return courseService.getCoursesByStudentId(studentId);
    }

    /**
     * 获取课程统计信息
     */
    @GetMapping("/{courseId}/statistics")
    @ResponseBody
    public Result<Map<String, Object>> getCourseStatistics(@PathVariable Long courseId) {
        return courseService.getCourseStatistics(courseId);
    }
} 