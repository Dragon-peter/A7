package com.example.A7.Controller;

import com.example.A7.DO.AnswerDO;
import com.example.A7.DO.QuestionDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 学生提问
     */
    @PostMapping
    @ResponseBody
    public Result<QuestionDO> addQuestion(@RequestBody QuestionDO questionDO) {
        return questionService.addQuestion(questionDO);
    }

    /**
     * 回答问题
     */
    @PostMapping("/answer")
    @ResponseBody
    public Result<AnswerDO> addAnswer(@RequestBody AnswerDO answerDO) {
        return questionService.addAnswer(answerDO);
    }

    /**
     * 生成AI回答
     */
    @PostMapping("/{id}/ai-answer")
    @ResponseBody
    public Result<AnswerDO> generateAiAnswer(@PathVariable Long id) {
        return questionService.generateAiAnswer(id);
    }

    /**
     * 更新问题
     */
    @PutMapping("/{id}")
    @ResponseBody
    public Result<QuestionDO> updateQuestion(@PathVariable Long id, @RequestBody QuestionDO questionDO) {
        questionDO.setId(id);
        return questionService.updateQuestion(questionDO);
    }

    /**
     * 删除问题
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result<Boolean> deleteQuestion(@PathVariable Long id) {
        return questionService.deleteQuestion(id);
    }

    /**
     * 获取问题详情
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Result<QuestionDO> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    /**
     * 获取问题的所有回答
     */
    @GetMapping("/{id}/answers")
    @ResponseBody
    public Result<List<AnswerDO>> getAnswersByQuestionId(@PathVariable Long id) {
        return questionService.getAnswersByQuestionId(id);
    }

    /**
     * 按课程ID获取问题列表
     */
    @GetMapping("/course/{courseId}")
    @ResponseBody
    public Result<List<QuestionDO>> getQuestionsByCourseId(@PathVariable Long courseId) {
        return questionService.getQuestionsByCourseId(courseId);
    }

    /**
     * 按章节ID获取问题列表
     */
    @GetMapping("/chapter/{chapterId}")
    @ResponseBody
    public Result<List<QuestionDO>> getQuestionsByChapterId(@PathVariable Long chapterId) {
        return questionService.getQuestionsByChapterId(chapterId);
    }

    /**
     * 按学生ID获取问题列表
     */
    @GetMapping("/student/{studentId}")
    @ResponseBody
    public Result<List<QuestionDO>> getQuestionsByStudentId(@PathVariable Long studentId) {
        return questionService.getQuestionsByStudentId(studentId);
    }

    /**
     * 分页获取所有问题
     */
    @GetMapping("/list")
    @ResponseBody
    public Result<Paging<QuestionDO>> getAllQuestions(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return questionService.getAllQuestions(pageNum, pageSize);
    }
} 