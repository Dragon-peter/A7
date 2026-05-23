package com.example.A7.AI.AiService.AiServiceImpl;

import com.example.A7.AI.AiService.StudentAiService;
import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.DO.UserDO;
import com.example.A7.Mapper.UserMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.StudentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;


@Service
public class StudentAiServiceImpl implements StudentAiService {
    private static final Logger log = LoggerFactory.getLogger(StudentAiServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentService studentService;

    @Override
    public Result<Paging<StudentDO>> aiFindAllStudents(Integer pageNum, Integer pageSize) {
        // 参数校验
        if (pageNum == null || pageNum <= 0) {
            return Result.error("400", "页码必须大于0");
        }
        if (pageSize == null || pageSize <= 0 || pageSize > 100) {
            return Result.error("400", "每页数量必须在1-100之间");
        }

        try {
            return studentService.findAllStudents(pageNum, pageSize);
        } catch (Exception e) {
            log.error("AI查询学生列表失败", e);
            return Result.error("500", "查询学生列表失败");
        }
    }

    @Override
    public Result<StudentDO> aiAddStudent(StudentDO studentDO) {
        // 1. 参数校验
        if (studentDO == null) {
            return Result.error("400", "学生信息不能为空");
        }
        if (studentDO.getUserId() == null || studentDO.getUserId() <= 0) {
            return Result.error("400", "关联用户ID不合法");
        }

        // 2. 设置默认值
        studentDO.setCreateTime(LocalDateTime.now());
        studentDO.setUpdateTime(LocalDateTime.now());

        // 3. 验证班级ID
        if (studentDO.getClassId() == null || studentDO.getClassId() <= 0) {
            return Result.error("400", "班级ID不合法");
        }

        // 4. 验证专业
        if (StringUtils.isBlank(studentDO.getMajor())) {
            studentDO.setMajor("未设置"); // 设置默认专业
        }

        // 5. 验证入学年份
        if (studentDO.getAdmissionYear() == null) {
            studentDO.setAdmissionYear(LocalDateTime.now().getYear()); // 默认为当前年份
        }

        // 6. 调用服务添加学生
        try {
            Result<StudentDO> result = studentService.addStudent(studentDO);
            if (result.isSuccess()) {
                UserDO userDO=userMapper.findById(studentDO.getUserId());
                log.info("AI添加学生成功: {}", userDO.getRealName());
            }
            return result;
        } catch (Exception e) {
            log.error("AI添加学生失败", e);
            return Result.error("500", "添加学生失败");
        }
    }

    @Override
    public Result<StudentDO> aiGetStudentInfo(Long studentId) {
        if (studentId == null || studentId <= 0) {
            return Result.error("400", "学生ID不合法");
        }

        try {
            StudentDO studentDO = studentService.getStudentById(studentId);
            if (studentDO == null) {
                return Result.error("404", "学生不存在");
            }
            return Result.success(studentDO);
        } catch (Exception e) {
            log.error("AI查询学生信息失败", e);
            return Result.error("500", "查询学生信息失败");
        }
    }

    @Override
    public Result<StudentDO> aiFindByRealName(String realName) {
        if (StringUtils.isBlank(realName)) {
            return Result.error("400", "学生姓名不能为空");
        }

        try {
            return studentService.findByRealName(realName);
        } catch (Exception e) {
            log.error("AI按关联用户姓名查询学生失败", e);
            return Result.error("500", "按关联用户姓名查询学生失败");
        }
    }

    @Override
    public Result<StudentDO> aiUpdateStudent(Long studentId, Map<String, Object> updateFields) {
        // 1. 参数校验
        if (studentId == null || studentId <= 0) {
            return Result.error("400", "学生ID不合法");
        }
        if (updateFields == null || updateFields.isEmpty()) {
            return Result.error("400", "更新字段不能为空");
        }

        // 2. 获取现有学生信息
        StudentDO student = studentService.getStudentById(studentId);
        if (student == null) {
            return Result.error("404", "学生不存在");
        }

        // 3. 创建更新日志
        StringBuilder updateLog = new StringBuilder();
        updateLog.append("AI更新学生[").append(studentId).append("]:");

        // 4. 只允许更新特定字段（白名单机制）
        try {
            if (updateFields.containsKey("classId")) {
                Integer newClassId = (Integer) updateFields.get("classId");
                if (newClassId != null && newClassId > 0) {
                    updateLog.append(" 班级ID[").append(student.getClassId()).append("->").append(newClassId).append("]");
                    student.setClassId(newClassId);
                }
            }

            if (updateFields.containsKey("major")) {
                String newMajor = (String) updateFields.get("major");
                if (StringUtils.isNotBlank(newMajor)) {
                    updateLog.append(" 专业[").append(student.getMajor()).append("->").append(newMajor).append("]");
                    student.setMajor(newMajor);
                }
            }

            if (updateFields.containsKey("admissionYear")) {
                Integer newAdmissionYear = (Integer) updateFields.get("admissionYear");
                if (newAdmissionYear != null && newAdmissionYear > 0) {
                    updateLog.append(" 入学年份[").append(student.getAdmissionYear()).append("->").append(newAdmissionYear).append("]");
                    student.setAdmissionYear(newAdmissionYear);
                }
            }

            // 5. 如果没有有效更新字段
            if (updateLog.toString().equals("AI更新学生[" + studentId + "]:")) {
                return Result.error("400", "没有提供有效的更新字段");
            }

            // 6. 设置更新时间
            student.setUpdateTime(LocalDateTime.now());

            // 7. 记录更新日志
            log.info(updateLog.toString());

            // 8. 调用原有更新方法
            return studentService.updateStudent(student);
        } catch (ClassCastException e) {
            log.error("AI更新学生字段类型错误", e);
            return Result.error("400", "字段类型不匹配");
        } catch (Exception e) {
            log.error("AI更新学生失败", e);
            return Result.error("500", "更新学生失败");
        }
    }

    @Override
    public Result<Boolean> aiDeleteStudent(Long studentId) {
        if (studentId == null || studentId <= 0) {
            return Result.error("400", "学生ID不合法");
        }

        try {
            Result<Boolean> result = studentService.deleteStudent(studentId);
            if (result.isSuccess()) {
                log.info("AI删除学生成功: {}", studentId);
            }
            return result;
        } catch (Exception e) {
            log.error("AI删除学生失败", e);
            return Result.error("500", "删除学生失败");
        }
    }
}