package com.example.A7.Controller;

import com.example.A7.AI.AiService.TeacherAiService;
import com.example.A7.Model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI智能备课控制器
 */
@RestController
@RequestMapping("/api/teaching-ai")
public class TeachingAiController {

    @Autowired
    private TeacherAiService teacherAiService;

    /**
     * 生成课程大纲
     */
    @GetMapping("/course-outline")
    @ResponseBody
    public String generateCourseOutline(
            @RequestParam String subject,
            @RequestParam String level) {
        return teacherAiService.generateCourseOutline(subject, level);
    }

    /**
     * 生成章节内容
     */
    @GetMapping("/chapter-content")
    @ResponseBody
    public String generateChapterContent(
            @RequestParam Long courseId,
            @RequestParam Long chapterId) {
        return teacherAiService.generateChapterContent(courseId, chapterId);
    }

    /**
     * 生成教学计划
     */
    @GetMapping("/teaching-plan")
    @ResponseBody
    public String generateTeachingPlan(@RequestParam Long courseId) {
        return teacherAiService.generateTeachingPlan(courseId);
    }

    /**
     * 生成习题集
     */
    @GetMapping("/exercises")
    @ResponseBody
    public List<Map<String, Object>> generateExercises(
            @RequestParam Long knowledgePointId,
            @RequestParam Integer difficulty,
            @RequestParam Integer count) {
        return teacherAiService.generateExercises(knowledgePointId, difficulty, count);
    }

    /**
     * 评估学生回答
     */
    @PostMapping("/evaluate-answer")
    @ResponseBody
    public Map<String, Object> evaluateStudentAnswer(
            @RequestParam Long exerciseId,
            @RequestParam String studentAnswer) {
        return teacherAiService.evaluateStudentAnswer(exerciseId, studentAnswer);
    }

    /**
     * 生成完整教学内容（包含案例分析和实践练习）
     */
    @PostMapping("/generate-teaching-content")
    @ResponseBody
    public Result<Map<String, Object>> generateTeachingContent(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String courseTitle = (String) request.get("courseTitle");
            String outline = (String) request.get("outline");
            List<String> knowledgePoints = (List<String>) request.get("knowledgePoints");
            Boolean includeCases = (Boolean) request.get("includeCases");
            Boolean includeExercises = (Boolean) request.get("includeExercises");
            Integer teachingHours = (Integer) request.get("teachingHours");

            Map<String, Object> content = teacherAiService.generateCompleteTeachingContent(
                title, courseTitle, outline, knowledgePoints, includeCases, includeExercises, teachingHours
            );

            return new Result<Map<String, Object>>().success(content);
        } catch (Exception e) {
            return new Result<Map<String, Object>>().error("500", "生成教学内容失败: " + e.getMessage());
        }
    }

    /**
     * 获取教学内容详情
     */
    @GetMapping("/teaching-content/{id}")
    @ResponseBody
    public Result<Map<String, Object>> getTeachingContentDetail(@PathVariable Long id) {
        try {
            Map<String, Object> content = teacherAiService.getTeachingContentDetail(id);
            return new Result<Map<String, Object>>().success(content);
        } catch (Exception e) {
            return new Result<Map<String, Object>>().error("500", "获取教学内容失败: " + e.getMessage());
        }
    }

    /**
     * 保存教学内容
     */
    @PostMapping("/save-teaching-content")
    @ResponseBody
    public Result<Long> saveTeachingContent(@RequestBody Map<String, Object> content) {
        try {
            Long contentId = teacherAiService.saveTeachingContent(content);
            return new Result<Long>().success(contentId);
        } catch (Exception e) {
            return new Result<Long>().error("500", "保存教学内容失败: " + e.getMessage());
        }
    }
}