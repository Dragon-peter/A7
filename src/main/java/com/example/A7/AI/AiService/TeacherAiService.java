package com.example.A7.AI.AiService;

import com.example.A7.DO.TeacherDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

import java.util.List;
import java.util.Map;

public interface TeacherAiService {
    /**
     * 分页查询所有教师
     */
    Result<Paging<TeacherDO>> aiFindAllTeachers(Integer pageNum, Integer pageSize);

    /**
     * 添加教师
     */
    Result<TeacherDO> aiAddTeacher(TeacherDO teacherDO);

    /**
     * 查询教师信息
     */
    Result<TeacherDO> aiGetTeacherInfo(Long teacherId);

    /**
     * 按姓名查询教师
     */
    Result<TeacherDO> aiFindByRealName(String realName);

    /**
     * 更新教师信息
     */
    Result<TeacherDO> aiUpdateTeacher(Long teacherId, Map<String, Object> updateFields);

    /**
     * 删除教师
     */
    Result<Boolean> aiDeleteTeacher(Long teacherId);
    
    /**
     * 生成课程大纲
     * @param subject 学科名称
     * @param level 教学层级（如初级、中级、高级）
     * @return 课程大纲内容
     */
    String generateCourseOutline(String subject, String level);
    
    /**
     * 生成课程章节内容
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 章节详细内容
     */
    String generateChapterContent(Long courseId, Long chapterId);
    
    /**
     * 生成教学计划
     * @param courseId 课程ID
     * @return 教学计划内容
     */
    String generateTeachingPlan(Long courseId);
    
    /**
     * 生成习题集
     * @param knowledgePointId 知识点ID
     * @param difficulty 难度级别(1-5)
     * @param count 习题数量
     * @return 习题集内容
     */
    List<Map<String, Object>> generateExercises(Long knowledgePointId, Integer difficulty, Integer count);
    
    /**
     * 评估学生回答
     * @param exerciseId 习题ID
     * @param studentAnswer 学生回答
     * @return 评估结果
     */
    Map<String, Object> evaluateStudentAnswer(Long exerciseId, String studentAnswer);

    /**
     * 生成完整教学内容（包含案例分析和实践练习）
     * @param title 教学内容标题
     * @param courseTitle 课程名称
     * @param outline 教学大纲
     * @param knowledgePoints 知识点列表
     * @param includeCases 是否包含案例分析
     * @param includeExercises 是否包含实践练习
     * @param teachingHours 教学课时
     * @return 完整的教学内容
     */
    Map<String, Object> generateCompleteTeachingContent(
        String title, String courseTitle, String outline,
        List<String> knowledgePoints, Boolean includeCases,
        Boolean includeExercises, Integer teachingHours
    );

    /**
     * 获取教学内容详情
     * @param contentId 教学内容ID
     * @return 教学内容详情
     */
    Map<String, Object> getTeachingContentDetail(Long contentId);

    /**
     * 保存教学内容
     * @param content 教学内容数据
     * @return 保存后的内容ID
     */
    Long saveTeachingContent(Map<String, Object> content);
    
    /**
     * 生成问题回答
     * @param questionTitle 问题标题
     * @param questionContent 问题内容
     * @param courseId 课程ID
     * @return AI生成的回答内容
     */
    String generateQuestionAnswer(String questionTitle, String questionContent, Long courseId);
}