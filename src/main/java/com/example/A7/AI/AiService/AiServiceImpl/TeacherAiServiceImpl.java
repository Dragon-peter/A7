package com.example.A7.AI.AiService.AiServiceImpl;

import com.example.A7.AI.AiService.TeacherAiService;
import com.example.A7.Config.AiConfig;
import com.example.A7.DO.*;
import com.example.A7.Mapper.*;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.TeacherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TeacherAiServiceImpl implements TeacherAiService {
    private static final Logger log = LoggerFactory.getLogger(TeacherAiServiceImpl.class);
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private KnowledgePointMapper knowledgePointMapper;
    
    @Autowired
    private ExerciseMapper exerciseMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private AiConfig aiConfig;

    @Value("${openai.api.url}")
    private String apiUrl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<Paging<TeacherDO>> aiFindAllTeachers(Integer pageNum, Integer pageSize) {
        // 参数校验
        if (pageNum == null || pageNum <= 0) {
            return Result.error("400", "页码必须大于0");
        }
        if (pageSize == null || pageSize <= 0 || pageSize > 100) {
            return Result.error("400", "每页数量必须在1-100之间");
        }

        try {
            return teacherService.findAllTeachers(pageNum, pageSize);
        } catch (Exception e) {
            log.error("AI查询教师列表失败", e);
            return Result.error("500", "查询教师列表失败");
        }
    }

    @Override
    public Result<TeacherDO> aiAddTeacher(TeacherDO teacherDO) {
        // 1. 参数校验
        if (teacherDO == null) {
            return Result.error("400", "教师信息不能为空");
        }
        if (teacherDO.getUserId() == null || teacherDO.getUserId() <= 0) {
            return Result.error("400", "关联用户ID不合法");
        }

        // 2. 设置默认值
        teacherDO.setCreateTime(LocalDateTime.now());
        teacherDO.setUpdateTime(LocalDateTime.now());

        // 3. 验证职称
        if (StringUtils.isNotBlank(teacherDO.getTitle())) {
            if (!"讲师".equals(teacherDO.getTitle()) &&
                    !"副教授".equals(teacherDO.getTitle()) &&
                    !"教授".equals(teacherDO.getTitle())) {
                return Result.error("400", "职称必须是讲师、副教授或教授");
            }
        } else {
            teacherDO.setTitle("讲师"); // 默认职称
        }

        // 4. 验证所属部门
        if (StringUtils.isBlank(teacherDO.getDepartment())) {
            teacherDO.setDepartment("未分配"); // 默认部门
        }

        // 5. 验证研究领域
        if (StringUtils.isBlank(teacherDO.getResearchArea())) {
            teacherDO.setResearchArea("未设置"); // 默认研究领域
        }

        // 6. 调用服务添加教师
        try {
            Result<TeacherDO> result = teacherService.addTeachers(teacherDO);
            if (result.isSuccess()) {
                UserDO userDO = userMapper.findById(teacherDO.getUserId());
                log.info("AI添加教师成功: {}", userDO.getRealName());
            }
            return result;
        } catch (Exception e) {
            log.error("AI添加教师失败", e);
            return Result.error("500", "添加教师失败");
        }
    }

    @Override
    public Result<TeacherDO> aiGetTeacherInfo(Long teacherId) {
        if (teacherId == null || teacherId <= 0) {
            return Result.error("400", "教师ID不合法");
        }

        try {
            TeacherDO teacher = teacherService.getTeacherById(teacherId);
            if (teacher == null) {
                return Result.error("404", "教师不存在");
            }
            return Result.success(teacher);
        } catch (Exception e) {
            log.error("AI查询教师信息失败", e);
            return Result.error("500", "查询教师信息失败");
        }
    }

    @Override
    public Result<TeacherDO> aiFindByRealName(String realName) {
        if (StringUtils.isBlank(realName)) {
            return Result.error("400", "教师姓名不能为空");
        }

        try {
            return teacherService.findByRealName(realName);
        } catch (Exception e) {
            log.error("AI按姓名查询教师失败", e);
            return Result.error("500", "按姓名查询教师失败");
        }
    }

    @Override
    public Result<TeacherDO> aiUpdateTeacher(Long teacherId, Map<String, Object> updateFields) {
        // 1. 参数校验
        if (teacherId == null || teacherId <= 0) {
            return Result.error("400", "教师ID不合法");
        }
        if (updateFields == null || updateFields.isEmpty()) {
            return Result.error("400", "更新字段不能为空");
        }

        // 2. 获取现有教师信息
        TeacherDO teacher = teacherService.getTeacherById(teacherId);
        if (teacher == null) {
            return Result.error("404", "教师不存在");
        }

        // 3. 创建更新日志
        StringBuilder updateLog = new StringBuilder();
        updateLog.append("AI更新教师[").append(teacherId).append("]:");

        // 4. 只允许更新特定字段（白名单机制）
        try {
           

            if (updateFields.containsKey("department")) {
                String newDepartment = (String) updateFields.get("department");
                if (StringUtils.isNotBlank(newDepartment)) {
                    updateLog.append(" 所属部门[").append(teacher.getDepartment()).append("->").append(newDepartment).append("]");
                    teacher.setDepartment(newDepartment);
                }
            }

            if (updateFields.containsKey("title")) {
                String newTitle = (String) updateFields.get("title");
                if (StringUtils.isNotBlank(newTitle) &&
                        ("讲师".equals(newTitle) || "副教授".equals(newTitle) || "教授".equals(newTitle))) {
                    updateLog.append(" 职称[").append(teacher.getTitle()).append("->").append(newTitle).append("]");
                    teacher.setTitle(newTitle);
                }
            }

            if (updateFields.containsKey("researchArea")) {
                String newResearchArea = (String) updateFields.get("researchArea");
                if (StringUtils.isNotBlank(newResearchArea)) {
                    updateLog.append(" 研究领域[").append(teacher.getResearchArea()).append("->").append(newResearchArea).append("]");
                    teacher.setResearchArea(newResearchArea);
                }
            }

            // 5. 设置更新时间
            teacher.setUpdateTime(LocalDateTime.now());

            // 6. 执行更新
            Result<TeacherDO> result = teacherService.updateTeachers(teacher);
            if (result.isSuccess()) {
                log.info(updateLog.toString());
            }
            return result;
        } catch (Exception e) {
            log.error("AI更新教师信息失败", e);
            return Result.error("500", "更新教师信息失败");
        }
    }

    @Override
    public Result<Boolean> aiDeleteTeacher(Long teacherId) {
        if (teacherId == null || teacherId <= 0) {
            return Result.error("400", "教师ID不合法");
        }

        try {
            Result<Boolean> result = teacherService.deleteTeachers(teacherId);
            if (result.isSuccess() && Boolean.TRUE.equals(result.getData())) {
                log.info("AI删除教师成功: {}", teacherId);
            }
            return result;
        } catch (Exception e) {
            log.error("AI删除教师失败", e);
            return Result.error("500", "删除教师失败");
        }
    }
    
    @Override
    public String generateCourseOutline(String subject, String level) {
        try {
            // 构建请求提示
            String prompt = String.format(
                "请为'%s'学科设计一个%s级别的课程大纲，包含以下内容：\n" +
                "1. 课程简介和教学目标\n" +
                "2. 课程内容组织（章节结构）\n" +
                "3. 每章节的核心知识点\n" +
                "4. 课程教学重点难点\n" +
                "5. 课程所需前置知识",
                subject, level
            );
            
            // 调用AI接口获取回答
            String response = callAiApi(prompt);
            
            return response;
        } catch (Exception e) {
            log.error("生成课程大纲失败", e);
            return "生成课程大纲失败：" + e.getMessage();
        }
    }
    
    @Override
    public String generateChapterContent(Long courseId, Long chapterId) {
        try {
            // 获取课程和章节信息
            CourseDO course = courseMapper.findById(courseId);
            ChapterDO chapter = chapterMapper.findById(chapterId);
            
            if (course == null || chapter == null) {
                return "课程或章节不存在";
            }
            
            // 构建请求提示
            String prompt = String.format(
                "请为课程《%s》的章节《%s》生成详细的教学内容，包含：\n" +
                "1. 章节教学目标\n" +
                "2. 核心概念解释\n" +
                "3. 教学内容详细展开\n" +
                "4. 课程案例和练习\n" +
                "5. 学习难点解析和建议",
                course.getName(), chapter.getTitle()
            );
            
            // 调用AI接口获取回答
            String response = callAiApi(prompt);
            
            return response;
        } catch (Exception e) {
            log.error("生成章节内容失败", e);
            return "生成章节内容失败：" + e.getMessage();
        }
    }
    
    @Override
    public String generateTeachingPlan(Long courseId) {
        try {
            // 获取课程信息
            CourseDO course = courseMapper.findById(courseId);
            
            if (course == null) {
                return "课程不存在";
            }
            
            // 获取课程章节
            List<ChapterDO> chapters = chapterMapper.findByCourseId(courseId);
            
            // 构建章节信息
            StringBuilder chaptersInfo = new StringBuilder();
            for (int i = 0; i < chapters.size(); i++) {
                chaptersInfo.append(i + 1).append(". ")
                    .append(chapters.get(i).getTitle())
                    .append("\n");
            }
            
            // 构建请求提示
            String prompt = String.format(
                "请为课程《%s》制定一个详细的教学计划，该课程包含以下章节：\n%s\n" +
                "教学计划需要包含：\n" +
                "1. 课程整体教学安排（周/课时）\n" +
                "2. 各章节教学时长和内容重点\n" +
                "3. 教学方法和教学资源建议\n" +
                "4. 学生作业和评估方式\n" +
                "5. 教学进度检查点",
                course.getName(), chaptersInfo.toString()
            );
            
            // 调用AI接口获取回答
            String response = callAiApi(prompt);
            
            return response;
        } catch (Exception e) {
            log.error("生成教学计划失败", e);
            return "生成教学计划失败：" + e.getMessage();
        }
    }
    
    @Override
    public List<Map<String, Object>> generateExercises(Long knowledgePointId, Integer difficulty, Integer count) {
        try {
            // 获取知识点信息
            KnowledgePointDO knowledgePoint = knowledgePointMapper.findById(knowledgePointId);
            
            if (knowledgePoint == null) {
                return new ArrayList<>();
            }
            
            // 获取课程和章节信息
            CourseDO course = courseMapper.findById(knowledgePoint.getCourseId());
            ChapterDO chapter = chapterMapper.findById(knowledgePoint.getChapterId());
            
            if (course == null || chapter == null) {
                return new ArrayList<>();
            }
            
            // 构建请求提示
            String prompt = String.format(
                "请为课程《%s》的章节《%s》中的知识点《%s》生成%d道难度为%d级(1-5级)的习题，包含以下题型：\n" +
                "1. 选择题\n" +
                "2. 填空题\n" +
                "3. 简答题\n" +
                "每道题必须包含题干、答案和解析。请以JSON格式返回，格式为：\n" +
                "[{\"type\":\"选择题/填空题/简答题\",\"title\":\"题干内容\",\"options\":[\"选项A\",\"选项B\",\"选项C\",\"选项D\"],\"answer\":\"正确答案\",\"explanation\":\"答案解析\"}]",
                course.getName(), chapter.getTitle(), knowledgePoint.getName(), count, difficulty
            );
            
            // 调用AI接口获取回答
            String response = callAiApi(prompt);
            
            try {
                // 解析JSON响应
                List<Map<String, Object>> exercises = objectMapper.readValue(
                    extractJsonFromText(response), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
                );
                
                return exercises;
            } catch (JsonProcessingException e) {
                log.error("解析习题JSON失败", e);
                List<Map<String, Object>> fallback = new ArrayList<>();
                Map<String, Object> error = new HashMap<>();
                error.put("error", "习题格式解析失败");
                error.put("rawResponse", response);
                fallback.add(error);
                return fallback;
            }
        } catch (Exception e) {
            log.error("生成习题失败", e);
            List<Map<String, Object>> fallback = new ArrayList<>();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "生成习题失败: " + e.getMessage());
            fallback.add(error);
            return fallback;
        }
    }
    
    @Override
    public Map<String, Object> evaluateStudentAnswer(Long exerciseId, String studentAnswer) {
        try {
            // 获取习题信息
            ExerciseDO exercise = exerciseMapper.findById(exerciseId);
            
            if (exercise == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "习题不存在");
                return error;
            }
            
            // 构建请求提示
            String prompt = String.format(
                "以下是一道%s类型的习题：\n" +
                "题目：%s\n" +
                "标准答案：%s\n" +
                "学生回答：%s\n\n" +
                "请评估学生的回答，并提供以下内容：\n" +
                "1. 得分（0-100分）\n" +
                "2. 是否正确（true/false）\n" +
                "3. 评语\n" +
                "4. 改进建议\n" +
                "请以JSON格式返回，格式为：\n" +
                "{\"score\": 分数, \"isCorrect\": true/false, \"comment\": \"评语\", \"suggestion\": \"改进建议\"}",
                exercise.getType(), exercise.getTitle(), exercise.getAnswer(), studentAnswer
            );
            
            // 调用AI接口获取回答
            String response = callAiApi(prompt);
            
            try {
                // 解析JSON响应
                Map<String, Object> evaluation = objectMapper.readValue(
                    extractJsonFromText(response), Map.class
                );
                
                return evaluation;
            } catch (JsonProcessingException e) {
                log.error("解析评估JSON失败", e);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "评估结果解析失败");
                error.put("rawResponse", response);
                return error;
            }
        } catch (Exception e) {
            log.error("评估学生答案失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "评估失败: " + e.getMessage());
            return error;
        }
    }
    
    @Override
    public String generateQuestionAnswer(String questionTitle, String questionContent, Long courseId) {
        try {
            // 获取课程信息
            CourseDO course = null;
            if (courseId != null) {
                course = courseMapper.findById(courseId);
            }
            
            String courseInfo = course != null ? String.format("这个问题与课程《%s》相关。", course.getName()) : "";
            
            // 构建请求提示
            String prompt = String.format(
                "问题标题：%s\n" +
                "问题详情：%s\n" +
                "%s\n" +
                "请提供一个清晰、准确、有教育意义的回答，可以包含例子和相关资源建议。",
                questionTitle, questionContent, courseInfo
            );
            
            // 调用AI接口获取回答
            String response = callAiApi(prompt);
            
            // 查询并更新相关问题的状态为"answered"
            List<QuestionDO> questions = questionMapper.findByTitle(questionTitle);
            if (questions != null && !questions.isEmpty()) {
                for (QuestionDO question : questions) {
                    updateQuestionStatus(question.getId(), "answered");
                }
            }
            
            return response;
        } catch (Exception e) {
            log.error("生成问题回答失败", e);
            return "生成回答失败：" + e.getMessage();
        }
    }
    private void updateQuestionStatus(Long questionId, String status) {
        QuestionDO question = new QuestionDO();
        question.setId(questionId);
        question.setStatus(status);
        questionMapper.update(question);
    }
    /**
     * 调用AI API获取回答
     */
    private String callAiApi(String prompt) {
        try {
            // 检查DashScope API密钥是否配置
            if (!aiConfig.isDashscopeConfigured()) {
                return "错误：未配置有效的DashScope API密钥。请在配置文件中设置 dashscope.api-key";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiConfig.getDashscopeApiKey());

            // 构建DashScope兼容的请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content",
                "You are a professional knowledge learning Q&A and teacher systematic lesson preparation system, " +
                "which can provide complete and efficient knowledge points and assist students in learning and understanding, " +
                "and can also give systematic lesson preparation content about the knowledge points of specific chapters of the course."));
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 使用DashScope兼容模式的URL
            String dashscopeUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
            ResponseEntity<Map> response = restTemplate.postForEntity(dashscopeUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }

            return "无法生成内容，请稍后再试。";
        } catch (Exception e) {
            log.error("AI API调用失败", e);
            return "AI服务暂时不可用，请稍后再试。错误详情：" + e.getMessage();
        }
    }
    
    /**
     * 从文本中提取JSON内容
     */
    private String extractJsonFromText(String text) {
        int start = text.indexOf("[");
        int end = text.lastIndexOf("]") + 1;
        
        if (start >= 0 && end > start) {
            return text.substring(start, end);
        }
        
        start = text.indexOf("{");
        end = text.lastIndexOf("}") + 1;
        
        if (start >= 0 && end > start) {
            return text.substring(start, end);
        }
        
        return "{}";
    }

    @Override
    public Map<String, Object> generateCompleteTeachingContent(
            String title, String courseTitle, String outline,
            List<String> knowledgePoints, Boolean includeCases,
            Boolean includeExercises, Integer teachingHours) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 构建详细的教学内容生成提示
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("请为以下教学内容生成详细的教学材料：\n\n");
            promptBuilder.append("课程名称：").append(courseTitle).append("\n");
            promptBuilder.append("教学内容标题：").append(title).append("\n");
            promptBuilder.append("预计教学课时：").append(teachingHours).append("课时\n\n");

            if (outline != null && !outline.trim().isEmpty()) {
                promptBuilder.append("教学大纲：\n").append(outline).append("\n\n");
            }

            if (knowledgePoints != null && !knowledgePoints.isEmpty()) {
                promptBuilder.append("涉及知识点：\n");
                for (String point : knowledgePoints) {
                    promptBuilder.append("- ").append(point).append("\n");
                }
                promptBuilder.append("\n");
            }

            promptBuilder.append("请生成以下内容：\n");
            promptBuilder.append("1. 教学目标（知识目标、能力目标、素质目标）\n");
            promptBuilder.append("2. 教学重点和难点\n");
            promptBuilder.append("3. 详细的教学内容（分章节展开）\n");
            promptBuilder.append("4. 教学方法和手段\n");

            if (includeCases) {
                promptBuilder.append("5. 案例分析（至少2个实际案例，包含案例背景、分析过程、结论总结）\n");
            }

            if (includeExercises) {
                promptBuilder.append("6. 实践练习（包含练习题目、操作步骤、预期结果）\n");
            }

            promptBuilder.append("\n请以结构化的方式组织内容，确保内容专业、实用、易于理解。");

            // 调用AI生成基础内容
            String baseContent = callAiApi(promptBuilder.toString());
            result.put("baseContent", baseContent);

            // 如果需要案例分析，单独生成
            if (includeCases) {
                String caseContent = generateCaseAnalysis(title, courseTitle, knowledgePoints);
                result.put("caseAnalysis", caseContent);
            }

            // 如果需要实践练习，单独生成
            if (includeExercises) {
                String exerciseContent = generatePracticeExercises(title, courseTitle, knowledgePoints);
                result.put("practiceExercises", exerciseContent);
            }

            // 生成教学建议
            String teachingSuggestions = generateTeachingSuggestions(title, teachingHours);
            result.put("teachingSuggestions", teachingSuggestions);

            return result;

        } catch (Exception e) {
            log.error("生成完整教学内容失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "生成教学内容失败：" + e.getMessage());
            return error;
        }
    }

    /**
     * 生成案例分析内容
     */
    private String generateCaseAnalysis(String title, String courseTitle, List<String> knowledgePoints) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为《").append(courseTitle).append("》课程中的《").append(title).append("》教学内容生成2-3个详细的案例分析。\n\n");

        if (knowledgePoints != null && !knowledgePoints.isEmpty()) {
            prompt.append("案例应涵盖以下知识点：\n");
            for (String point : knowledgePoints) {
                prompt.append("- ").append(point).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("每个案例应包含：\n");
        prompt.append("1. 案例标题\n");
        prompt.append("2. 案例背景（真实的应用场景）\n");
        prompt.append("3. 问题描述\n");
        prompt.append("4. 分析过程（步骤详细，逻辑清晰）\n");
        prompt.append("5. 解决方案\n");
        prompt.append("6. 案例总结（知识点应用、经验教训）\n");
        prompt.append("7. 思考题（供学生讨论）\n\n");
        prompt.append("请确保案例贴近实际，具有教学价值和启发性。");

        return callAiApi(prompt.toString());
    }

    /**
     * 生成实践练习内容
     */
    private String generatePracticeExercises(String title, String courseTitle, List<String> knowledgePoints) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为《").append(courseTitle).append("》课程中的《").append(title).append("》教学内容设计实践练习。\n\n");

        if (knowledgePoints != null && !knowledgePoints.isEmpty()) {
            prompt.append("练习应覆盖以下知识点：\n");
            for (String point : knowledgePoints) {
                prompt.append("- ").append(point).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("请设计3-5个不同难度的练习，包含：\n");
        prompt.append("1. 基础练习（巩固基本概念）\n");
        prompt.append("2. 应用练习（实际操作）\n");
        prompt.append("3. 综合练习（知识点综合运用）\n\n");

        prompt.append("每个练习应包含：\n");
        prompt.append("- 练习标题\n");
        prompt.append("- 练习目标\n");
        prompt.append("- 详细步骤\n");
        prompt.append("- 预期结果\n");
        prompt.append("- 评分标准\n");
        prompt.append("- 常见错误及解决方法\n\n");
        prompt.append("请确保练习具有可操作性和实用性。");

        return callAiApi(prompt.toString());
    }

    /**
     * 生成教学建议
     */
    private String generateTeachingSuggestions(String title, Integer teachingHours) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为《").append(title).append("》教学内容（").append(teachingHours).append("课时）提供详细的教学建议。\n\n");

        prompt.append("请包含以下方面的建议：\n");
        prompt.append("1. 课时分配建议\n");
        prompt.append("2. 教学方法选择\n");
        prompt.append("3. 学生互动设计\n");
        prompt.append("4. 教学工具和资源\n");
        prompt.append("5. 评估方式建议\n");
        prompt.append("6. 常见教学难点及应对策略\n");
        prompt.append("7. 课后作业设计\n");
        prompt.append("8. 教学效果评价方法\n\n");
        prompt.append("请提供实用、具体的建议。");

        return callAiApi(prompt.toString());
    }

    @Override
    public Map<String, Object> getTeachingContentDetail(Long contentId) {
        // 这里应该从数据库获取教学内容详情
        // 暂时返回模拟数据
        Map<String, Object> content = new HashMap<>();
        content.put("id", contentId);
        content.put("title", "示例教学内容");
        content.put("baseContent", "这是基础教学内容...");
        content.put("caseAnalysis", "这是案例分析内容...");
        content.put("practiceExercises", "这是实践练习内容...");
        content.put("teachingSuggestions", "这是教学建议...");
        return content;
    }

    @Override
    public Long saveTeachingContent(Map<String, Object> content) {
        // 这里应该将教学内容保存到数据库
        // 暂时返回模拟ID
        return System.currentTimeMillis();
    }
}