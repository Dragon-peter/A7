package com.example.A7.Service;

import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.DO.UserDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

/**
* @author Lenovo
* @description 针对表【student(学生信息表)】的数据库操作Service
* @createDate 2025-06-17 15:52:35
*/
public interface StudentService {

    // 获取所有用户（分页）
    public Result<Paging<StudentDO>> findAllStudents(Integer pageNum, Integer pageSize);

    // 添加用户
    public Result<StudentDO> addStudent(StudentDO studentDO);

    // 更新用户
    public Result<StudentDO> updateStudent(StudentDO studentDO);

    // 删除用户
    public Result<Boolean> deleteStudent(Long id);

    // 根据用户名查询
    public Result<StudentDO> findByRealName(String realName);

    StudentDO getStudentById(Long id);
}
