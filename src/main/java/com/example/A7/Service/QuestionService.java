package com.example.A7.Service;

import com.example.A7.DO.AnswerDO;
import com.example.A7.DO.QuestionDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

import java.util.List;

public interface QuestionService {
    /**
     * 学生提问
     */
    Result<QuestionDO> addQuestion(QuestionDO questionDO);
    
    /**
     * 回答问题
     */
    Result<AnswerDO> addAnswer(AnswerDO answerDO);
    
    /**
     * 生成AI回答
     */
    Result<AnswerDO> generateAiAnswer(Long questionId);
    
    /**
     * 更新问题
     */
    Result<QuestionDO> updateQuestion(QuestionDO questionDO);
    
    /**
     * 删除问题
     */
    Result<Boolean> deleteQuestion(Long id);
    
    /**
     * 获取问题详情
     */
    Result<QuestionDO> getQuestionById(Long id);
    
    /**
     * 获取问题的所有回答
     */
    Result<List<AnswerDO>> getAnswersByQuestionId(Long questionId);
    
    /**
     * 按课程ID获取问题列表
     */
    Result<List<QuestionDO>> getQuestionsByCourseId(Long courseId);
    
    /**
     * 按章节ID获取问题列表
     */
    Result<List<QuestionDO>> getQuestionsByChapterId(Long chapterId);
    
    /**
     * 按学生ID获取问题列表
     */
    Result<List<QuestionDO>> getQuestionsByStudentId(Long studentId);
    
    /**
     * 分页获取所有问题
     */
    Result<Paging<QuestionDO>> getAllQuestions(Integer pageNum, Integer pageSize);
} 