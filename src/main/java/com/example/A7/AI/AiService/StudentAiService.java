package com.example.A7.AI.AiService;

import com.example.A7.DO.StudentDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

import java.util.Map;

public interface StudentAiService {
    /**
     * 分页查询所有学生
     */
    Result<Paging<StudentDO>> aiFindAllStudents(Integer pageNum, Integer pageSize);

    /**
     * 添加学生
     */
    Result<StudentDO> aiAddStudent(StudentDO studentDO);

    /**
     * 查询学生信息
     */
    Result<StudentDO> aiGetStudentInfo(Long studentId);

    /**
     * 按姓名查询学生
     */
    Result<StudentDO> aiFindByRealName(String realName);

    /**
     * 更新学生信息
     */
    Result<StudentDO> aiUpdateStudent(Long studentId, Map<String, Object> updateFields);

    /**
     * 删除学生
     */
    Result<Boolean> aiDeleteStudent(Long studentId);
}