package com.example.A7.Service.impl;

import com.example.A7.AI.AiService.TeacherAiService;
import com.example.A7.DO.AnswerDO;
import com.example.A7.DO.QuestionDO;
import com.example.A7.Mapper.AnswerMapper;
import com.example.A7.Mapper.QuestionMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.QuestionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private AnswerMapper answerMapper;
    
    @Autowired
    private TeacherAiService teacherAiService;

    @Override
    public Result<QuestionDO> addQuestion(QuestionDO questionDO) {
        Result<QuestionDO> result = new Result<>();
        try {
            // 设置创建时间
            questionDO.setCreateTime(LocalDateTime.now());
            // 设置初始状态为待回答
            questionDO.setStatus("pending");
            
            int rows = questionMapper.add(questionDO);
            if (rows > 0) {
                // 如果标记为使用AI回答，直接生成AI回答
                if (questionDO.getUseAi() != null && questionDO.getUseAi() == 1) {
                    generateAiAnswer(questionDO.getId());
                }
                return result.success(questionDO);
            }
            return result.error("400", "添加问题失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<AnswerDO> addAnswer(AnswerDO answerDO) {
        Result<AnswerDO> result = new Result<>();
        try {
            // 设置创建时间
            answerDO.setCreateTime(LocalDateTime.now());
            
            int rows = answerMapper.add(answerDO);
            if (rows > 0) {
                // 更新问题状态为已回答
                updateQuestionStatus(answerDO.getQuestionId(), "answered");
                return result.success(answerDO);
            }
            return result.error("400", "添加回答失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<AnswerDO> generateAiAnswer(Long questionId) {
        Result<AnswerDO> result = new Result<>();
        try {
            // 获取问题详情
            QuestionDO question = questionMapper.findById(questionId);
            if (question == null) {
                return result.error("404", "问题不存在");
            }
            
            // 使用AI服务生成回答内容
            String aiResponse = teacherAiService.generateQuestionAnswer(
                question.getTitle(), question.getContent(), question.getCourseId());
            
            // 创建回答记录
            AnswerDO answer = new AnswerDO();
            answer.setQuestionId(questionId);
            answer.setContent(aiResponse);
            answer.setAuthorType("ai");
            answer.setCreateTime(LocalDateTime.now());
            
            int rows = answerMapper.add(answer);
            if (rows > 0) {
                // 更新问题状态为已回答
                updateQuestionStatus(questionId, "answered");
                return result.success(answer);
            }
            return result.error("400", "生成AI回答失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<QuestionDO> updateQuestion(QuestionDO questionDO) {
        Result<QuestionDO> result = new Result<>();
        try {
            int rows = questionMapper.update(questionDO);
            if (rows > 0) {
                return result.success(questionDO);
            }
            return result.error("400", "更新问题失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> deleteQuestion(Long id) {
        Result<Boolean> result = new Result<>();
        try {
            // 首先删除问题相关的所有回答
            answerMapper.deleteByQuestion(id);
            // 然后删除问题
            int rows = questionMapper.delete(id);
            return result.success(rows > 0);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<QuestionDO> getQuestionById(Long id) {
        Result<QuestionDO> result = new Result<>();
        try {
            QuestionDO question = questionMapper.findById(id);
            if (question != null) {
                return result.success(question);
            }
            return result.error("404", "问题不存在");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<AnswerDO>> getAnswersByQuestionId(Long questionId) {
        Result<List<AnswerDO>> result = new Result<>();
        try {
            List<AnswerDO> answers = answerMapper.findByQuestionId(questionId);
            return result.success(answers);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<QuestionDO>> getQuestionsByCourseId(Long courseId) {
        Result<List<QuestionDO>> result = new Result<>();
        try {
            List<QuestionDO> questions = questionMapper.findByCourseId(courseId);
            return result.success(questions);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<QuestionDO>> getQuestionsByChapterId(Long chapterId) {
        Result<List<QuestionDO>> result = new Result<>();
        try {
            List<QuestionDO> questions = questionMapper.findByChapterId(chapterId);
            return result.success(questions);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<QuestionDO>> getQuestionsByStudentId(Long studentId) {
        Result<List<QuestionDO>> result = new Result<>();
        try {
            List<QuestionDO> questions = questionMapper.findByStudentId(studentId);
            return result.success(questions);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Paging<QuestionDO>> getAllQuestions(Integer pageNum, Integer pageSize) {
        Result<Paging<QuestionDO>> result = new Result<>();
        try {
            // 设置默认值
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            
            Page<QuestionDO> page = PageHelper.startPage(pageNum, pageSize)
                    .doSelectPage(() -> questionMapper.findAll());
            
            result.setSuccess(true);
            result.setData(new Paging<>(
                    page.getPageNum(), 
                    page.getPageSize(), 
                    page.getPages(), 
                    page.getTotal(), 
                    page.getResult()));
            return result;
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }
    
    /**
     * 更新问题状态
     */
    private void updateQuestionStatus(Long questionId, String status) {
        QuestionDO question = new QuestionDO();
        question.setId(questionId);
        question.setStatus(status);
        questionMapper.update(question);
    }
} 