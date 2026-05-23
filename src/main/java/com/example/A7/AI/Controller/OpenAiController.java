package com.example.A7.AI.Controller;

import com.example.A7.AI.AiService.MemoryService;
import com.example.A7.AI.AiService.StudentAiService;
import com.example.A7.AI.AiService.TeacherAiService;
import com.example.A7.AI.AiService.UserAiService;
import com.example.A7.Config.AiConfig;
import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.DO.UserDO;
import com.example.A7.Mapper.UserMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class OpenAiController {
    private static final Logger log = LoggerFactory.getLogger(OpenAiController.class);
    private  UserMapper userMapper;
    private final StudentAiService studentAiService;
    private final UserAiService userAiService;
    private final MemoryService memoryService;
    private final WebClient webClient;
    private final TeacherAiService teacherAiService;
    private final AiConfig aiConfig;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    // 使用构造函数注入
    public OpenAiController(StudentAiService studentAiService,
                            TeacherAiService teacherAiService,
                            UserAiService userAiService,
                            MemoryService memoryService,
                            AiConfig aiConfig,
                            WebClient.Builder webClientBuilder) {
        this.studentAiService = studentAiService;
        this.teacherAiService = teacherAiService;
        this.userAiService = userAiService;
        this.memoryService = memoryService;
        this.aiConfig = aiConfig;
        this.webClient = webClientBuilder.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1").build();
    }

    //角色预设
    private String getSystemPrompt() {
        return String.format("""
                You are a professional knowledge learning Q&A and teacher systematic lesson preparation system,
                which can provide complete and efficient knowledge points and assist students in learning and understanding,
                and can also give systematic lesson preparation content about the knowledge points of specific chapters of the course.
                当前日期是%s。

                角色设定：
                1. 身份：专业知识学习Q&A和教师系统备课助手
                2. 语言：简体中文
                3. 风格：专业、高效、系统化
                
                新增系统能力：
                1. 用户管理权限：
                - 可查询用户信息（需验证权限）
                - 可更新用户邮箱/姓名（需验证权限）
                - 可禁用用户账户（需二次确认）
             
                操作规范：
                1. 当用户请求管理操作时，必须要求提供安全令牌
                2. 敏感操作需人工确认
                3. 所有操作需记录审计日志
                
                服务准则：
                1. 回答要简洁明了，每段不超过3句话
                2. 涉及个人信息时需验证身份
                3. 课程相关问题需确认课程代码
                
                删除账户确认规则：
                1. 当用户请求删除账户时，必须要求用户明确说出「我确认删除账户[用户ID]」
                2. 收到完整确认语句后，回复「确认删除用户[用户ID]」
                3. 否则回复「删除操作未获确认」
                
                教师管理能力：
                1. 查询教师信息（需教师ID）
                2. 添加新教师（需完整信息）
                3. 更新教师信息（需验证权限）
                4. 删除教师账户（需二次确认）
                
                删除账户确认规则：
                1. 当请求删除教师账户时，必须要求明确说出「我确认删除账户[教师ID]」
                2. 收到完整确认语句后，回复「确认删除教师[教师ID]」
                3. 否则回复「删除操作未获确认」
                
                 学生管理能力：
                1. 查询学生信息（需学生ID）
                2. 添加新学生（需完整信息）
                3. 更新学生信息（需验证权限）
                4. 删除学生账户（需二次确认）
              
                删除账户确认规则：
                1. 当请求删除学生账户时，必须要求明确说出「我确认删除账户[学生ID]」
                2. 收到完整确认语句后，回复「确认删除学生[学生ID]」
                3. 否则回复「删除操作未获确认」
                """, LocalDate.now().format(DATE_FORMATTER));
    }

    @GetMapping(value = "/ai/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> generateStream(
            @RequestParam String message,
            @RequestParam Long userId) {

        Flux<String> responseFlux = generateStreamInternal(message, userId);

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("X-Accel-Buffering", "no")
                .body(responseFlux);
    }

    /**
     * 内部方法：生成AI流式响应
     */
    private Flux<String> generateStreamInternal(String message, Long userId) {

        if (!aiConfig.isDashscopeConfigured()) {
            return Flux.just("data: 错误：未配置有效的DashScope API密钥。请设置环境变量 DASHSCOPE_API_KEY 或在配置文件中设置 dashscope.api-key\n\n");
        }

        // 1. 从记忆服务获取历史对话（增强错误处理）
        List<Map<String, String>> history;
        try {
            history = memoryService.getConversationHistory(userId);
            Collections.reverse(history); // 反转顺序为从旧到新
        } catch (Exception e) {
            log.warn("获取用户对话历史失败，使用空历史记录。用户ID: {}, 错误: {}", userId, e.getMessage());
            history = new ArrayList<>();
        }

        // 2. 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", getSystemPrompt()));
        messages.addAll(history);
        messages.add(Map.of("role", "user", "content", message));

        // 3. 构建请求体（OpenAI兼容格式）
        Map<String, Object> requestBody = Map.of(
                "model", "qwen-plus",
                "messages", messages,
                "stream", false  // 非流式
        );

        Flux<String> responseFlux = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + aiConfig.getDashscopeApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("API返回错误状态码: {}", response.statusCode());
                    return Mono.error(new RuntimeException("API服务异常"));
                })
                .bodyToMono(String.class)  // 改为Mono，因为是单个响应
                .timeout(Duration.ofSeconds(30))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .flatMapMany(response -> {
                    try {
                        // 解析完整响应并模拟流式输出
                        return simulateStreamResponse(response);
                    } catch (Exception e) {
                        log.error("处理AI响应失败", e);
                        return Flux.just("data: 错误：处理响应失败\n\n");
                    }
                })
                .doOnError(e -> log.error("API调用失败", e))
                .onErrorResume(e -> Flux.just("data: 服务暂时不可用，请稍后重试\n\n"));

        return responseFlux;
    }
    // 保存用户消息到历史记录(可在调用此接口前执行)
    @PostMapping("/ai/message")
    public ResponseEntity<Void> saveUserMessage(
            @RequestParam String message,
            @RequestParam Long userId) {
        try {
            memoryService.saveMessage(userId, "user", message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("保存用户消息失败，但不影响AI功能。用户ID: {}, 错误: {}", userId, e.getMessage());
            return ResponseEntity.ok().build(); // 即使保存失败也返回成功，不影响AI功能
        }
    }

    // 格式化SSE响应
    /**
     * 格式化SSE响应，兼容流式（data: {...}）和非流式JSON
     * @param raw 原始数据（可能是 "data: {...}" 或纯JSON）
     * @return 标准SSE格式："data: {text}\n\n" 或错误信息
     */
    /**
     * 模拟流式响应 - 将完整的AI回复分块发送（统一格式）
     */
    private Flux<String> simulateStreamResponse(String response) {
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            String text = null;
            String model = "qwen-turbo";
            String finishReason = "stop";
            int totalTokens = 0;

            // 提取AI回复文本和元数据
            if (root.has("output") && root.get("output").has("text")) {
                text = root.get("output").get("text").asText();
                if (root.get("output").has("finish_reason")) {
                    finishReason = root.get("output").get("finish_reason").asText();
                }
            } else if (root.has("choices")) {
                JsonNode choices = root.get("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode firstChoice = choices.get(0);
                    if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                        text = firstChoice.get("message").get("content").asText();
                    }
                }
            }

            // 提取token使用信息
            if (root.has("usage") && root.get("usage").has("total_tokens")) {
                totalTokens = root.get("usage").get("total_tokens").asInt();
            }

            if (text == null || text.isEmpty()) {
                log.warn("无法从响应中提取文本: {}", response);
                return Flux.just(formatSSEMessage("error", "抱歉，AI服务返回了空的回复", null));
            }

            log.info("开始模拟流式输出，文本长度: {}, tokens: {}", text.length(), totalTokens);

            // 将文本分成小块，模拟流式输出
            List<String> chunks = new ArrayList<>();
            int chunkSize = 8; // 每次发送8个字符
            int chunkIndex = 1;

            for (int i = 0; i < text.length(); i += chunkSize) {
                int end = Math.min(i + chunkSize, text.length());
                String chunk = text.substring(i, end);

                // 创建统一格式的消息
                Map<String, Object> metadata = Map.of(
                    "model", model,
                    "chunk", chunkIndex++,
                    "total_length", text.length()
                );

                chunks.add(formatSSEMessage("text", chunk, metadata));
            }

            // 添加完成标记
            Map<String, Object> doneMetadata = Map.of(
                "model", model,
                "finish_reason", finishReason,
                "total_tokens", totalTokens,
                "total_chunks", chunkIndex - 1
            );
            chunks.add(formatSSEMessage("done", "", doneMetadata));

            // 使用延迟发送每个块，模拟真实的流式效果
            return Flux.fromIterable(chunks)
                    .delayElements(Duration.ofMillis(80)) // 每80ms发送一块
                    .doOnNext(chunk -> log.info("发送SSE块: {}", chunk.replace("\n", "\\n")))
                    .doOnComplete(() -> log.info("SSE流式输出完成"))
                    .doOnError(e -> log.error("SSE流式输出错误", e));

        } catch (Exception e) {
            log.error("模拟流式响应失败", e);
            return Flux.just(formatSSEMessage("error", "处理AI响应时发生错误", null));
        }
    }

    /**
     * 格式化SSE消息为统一格式
     */
    private String formatSSEMessage(String type, String content, Map<String, Object> metadata) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> message = Map.of(
                "type", type,
                "content", content,
                "timestamp", java.time.Instant.now().toString(),
                "metadata", metadata != null ? metadata : Map.of()
            );
            return "data: " + mapper.writeValueAsString(message) + "\n\n";
        } catch (Exception e) {
            log.error("格式化SSE消息失败", e);
            return "data: {\"type\":\"error\",\"content\":\"格式化消息失败\"}\n\n";
        }
    }

    // 在OpenAiController中添加以下方法：

    /**
     * AI查询用户信息
     */
    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<Result<UserDO>>> getUserInfo(
            @PathVariable Long userId) {

        return Mono.fromCallable(() -> userAiService.aiGetUserInfo(userId))
                .map(result -> ResponseEntity.ok().body(result))
                .onErrorResume(e -> {
                    log.error("查询用户失败", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Result.error("500", "查询失败")));
                });
    }

    /**
     * AI更新用户信息（流式响应）
     */
    @PutMapping(value = "/user/{userId}/update", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> updateFields) {

        // 2. 执行更新
        return Flux.fromIterable(updateFields.keySet())
                .concatMap(field -> {
                    Map<String, Object> singleUpdate = Collections.singletonMap(field, updateFields.get(field));
                    Result<UserDO> result = userAiService.aiUpdateUser(userId, singleUpdate);

                    if (result.isSuccess()) {
                        return Flux.just("data: 成功更新字段[" + field + "]\n\n");
                    } else {
                        return Flux.just("data: 更新失败[" + field + "]: " + result.getMessage() + "\n\n");
                    }
                })
                .onErrorResume(e -> {
                    log.error("更新用户异常", e);
                    return Flux.just("data: 更新过程发生异常\n\n");
                });
    }

    /**
     * AI删除用户（与对话整合）
     */
    @DeleteMapping("/user/{userId}/delete")
    public Mono<ResponseEntity<Result<Boolean>>> deleteUser(
            @PathVariable Long userId,
            @RequestParam String confirmMessage) {

        // 1. 通过对话确认
        return generateStreamInternal(confirmMessage, userId)
                .take(10) // 限制响应条数
                .collectList()
                .flatMap(responses -> {
                    String aiResponse = responses.stream()
                            .map(r -> r.replace("data: ", "").replace("\n\n", ""))
                            .collect(Collectors.joining());

                    // 2. 如果AI确认可以删除
                    if (aiResponse.contains("确认删除")) {
                        return Mono.fromCallable(() -> userAiService.aiDeleteUser(userId))
                                .map(result -> ResponseEntity.ok().body(result));
                    }
                    return Mono.just(ResponseEntity
                            .badRequest()
                            .body(Result.error("400", "删除操作未获确认")));
                });
    }


    /**
     * 分页查询所有教师
     */
    @GetMapping("teacher/list")
    public Mono<ResponseEntity<Result<Paging<TeacherDO>>>> findAllTeachers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        return Mono.fromCallable(() -> teacherAiService.aiFindAllTeachers(pageNum, pageSize))
                .map(result -> ResponseEntity.ok().body(result))
                .onErrorResume(e -> {
                    log.error("查询教师列表失败", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Result.error("500", "查询教师列表失败")));
                });
    }



    /**
     * 查询教师信息
     */
    @GetMapping("/teacher/{teacherId}")
    public Mono<ResponseEntity<Result<TeacherDO>>> getTeacherInfo(
            @PathVariable Long teacherId) {

        return Mono.fromCallable(() -> teacherAiService.aiGetTeacherInfo(teacherId))
                .map(result -> {
                    if (result.getData() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                    }
                    return ResponseEntity.ok().body(result);
                })
                .onErrorResume(e -> {
                    log.error("查询教师信息失败", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Result.error("500", "查询教师信息失败")));
                });
    }

    /**
     * 按姓名查询教师
     */
    @GetMapping("/TeacherRealName/{realName}")
    public Result<TeacherDO> findTeacherByRealName(
            @PathVariable String realName) throws UnsupportedEncodingException {
        // 对URL编码的中文进行解码
        String decodedName = URLDecoder.decode(realName, "UTF-8");
        return teacherAiService.aiFindByRealName(decodedName);
    }

    /**
     * 更新教师信息（流式响应）
     */
    @PutMapping(value = "/teacher/{teacherId}/update", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> updateTeacher(
            @PathVariable Long teacherId,
            @RequestBody Map<String, Object> updateFields) {

        return Flux.fromIterable(updateFields.keySet())
                .concatMap(field -> {
                    Map<String, Object> singleUpdate = Collections.singletonMap(field, updateFields.get(field));
                    Result<TeacherDO> result = teacherAiService.aiUpdateTeacher(teacherId, singleUpdate);

                    if (result.isSuccess()) {
                        return Flux.just("data: 成功更新字段[" + field + "]\n\n");
                    } else {
                        return Flux.just("data: 更新失败[" + field + "]: " + result.getMessage() + "\n\n");
                    }
                })
                .onErrorResume(e -> {
                    log.error("更新教师异常", e);
                    return Flux.just("data: 更新过程发生异常\n\n");
                });
    }

    /**
     * 删除教师（与对话整合）
     */
    @DeleteMapping("/teacher/{teacherId}/delete")
    public Mono<ResponseEntity<Result<Boolean>>> deleteTeacher(
            @PathVariable Long teacherId,
            @RequestParam String confirmMessage) {

        return generateStreamInternal(confirmMessage, teacherId)
                .take(10) // 限制响应条数
                .collectList()
                .flatMap(responses -> {
                    String aiResponse = responses.stream()
                            .map(r -> r.replace("data: ", "").replace("\n\n", ""))
                            .collect(Collectors.joining());

                    // 如果AI确认可以删除
                    if (aiResponse.contains("确认删除")) {
                        return Mono.fromCallable(() -> teacherAiService.aiDeleteTeacher(teacherId))
                                .map(result -> ResponseEntity.ok().body(result));
                    }
                    return Mono.just(ResponseEntity
                            .badRequest()
                            .body(Result.error("400", "删除操作未获确认")));
                });
    }

    /**
     * 分页查询所有学生
     */
    @GetMapping("Student/list")
    public Mono<ResponseEntity<Result<Paging<StudentDO>>>> findAllStudents(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        return Mono.fromCallable(() -> studentAiService.aiFindAllStudents(pageNum, pageSize))
                .map(result -> ResponseEntity.ok().body(result))
                .onErrorResume(e -> {
                    log.error("查询学生列表失败", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Result.error("500", "查询学生列表失败")));
                });
    }

    /**
     * 添加新学生
     */
    @PostMapping("Student/add")
    public Mono<ResponseEntity<Result<StudentDO>>> addStudent(@RequestBody StudentDO studentDO) {
        return Mono.fromCallable(() -> studentAiService.aiAddStudent(studentDO))
                .map(result -> {
                    if (result.isSuccess()) {
                        UserDO userDO = userMapper.findById(studentDO.getUserId());
                        log.info("添加学生成功: {}", userDO.getRealName());
                        return ResponseEntity.ok().body(result);
                    }
                    return ResponseEntity.badRequest().body(result);
                })
                .onErrorResume(e -> {
                    log.error("添加学生失败", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Result.error("500", "添加学生失败")));
                });
    }

    /**
     * 查询学生信息
     */
    @GetMapping("Student/{studentId}")
    public Mono<ResponseEntity<Result<StudentDO>>> getStudentInfo(
            @PathVariable Long studentId) {

        return Mono.fromCallable(() -> studentAiService.aiGetStudentInfo(studentId))
                .map(result -> {
                    if (result.getData() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                    }
                    return ResponseEntity.ok().body(result);
                })
                .onErrorResume(e -> {
                    log.error("查询学生信息失败", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Result.error("500", "查询学生信息失败")));
                });
    }

    /**
     * 按姓名查询学生
     */
    @GetMapping("/StudentRealName/{realName}")
    public Result<StudentDO> findStudentByRealName(
            @PathVariable String realName) throws UnsupportedEncodingException {
        // 对URL编码的中文进行解码
        String decodedName = URLDecoder.decode(realName, "UTF-8");
        return studentAiService.aiFindByRealName(decodedName);
    }

    /**
     * 更新学生信息（流式响应）
     */
    @PutMapping(value = "Student/{studentId}/update", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> updateStudent(
            @PathVariable Long studentId,
            @RequestBody Map<String, Object> updateFields) {

        return Flux.fromIterable(updateFields.keySet())
                .concatMap(field -> {
                    Map<String, Object> singleUpdate = Collections.singletonMap(field, updateFields.get(field));
                    Result<StudentDO> result = studentAiService.aiUpdateStudent(studentId, singleUpdate);

                    if (result.isSuccess()) {
                        return Flux.just("data: 成功更新字段[" + field + "]\n\n");
                    } else {
                        return Flux.just("data: 更新失败[" + field + "]: " + result.getMessage() + "\n\n");
                    }
                })
                .onErrorResume(e -> {
                    log.error("更新学生异常", e);
                    return Flux.just("data: 更新过程发生异常\n\n");
                });
    }

    /**
     * 删除学生（与对话整合）
     */
    @DeleteMapping("Student/{studentId}/delete")
    public Mono<ResponseEntity<Result<Boolean>>> deleteStudent(
            @PathVariable Long studentId,
            @RequestParam String confirmMessage) {

        return generateStreamInternal(confirmMessage, studentId)
                .take(10) // 限制响应条数
                .collectList()
                .flatMap(responses -> {
                    String aiResponse = responses.stream()
                            .map(r -> r.replace("data: ", "").replace("\n\n", ""))
                            .collect(Collectors.joining());

                    // 如果AI确认可以删除
                    if (aiResponse.contains("确认删除")) {
                        return Mono.fromCallable(() -> studentAiService.aiDeleteStudent(studentId))
                                .map(result -> ResponseEntity.ok().body(result));
                    }
                    return Mono.just(ResponseEntity
                            .badRequest()
                            .body(Result.error("400", "删除操作未获确认")));
                });
    }
}

