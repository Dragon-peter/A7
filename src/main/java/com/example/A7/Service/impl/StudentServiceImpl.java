package com.example.A7.Service.impl;

import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.Mapper.StudentMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.StudentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Result<Paging<StudentDO>> findAllStudents(Integer pageNum, Integer pageSize) {
        Result<Paging<StudentDO>> result = new Result<>();

        // 设置默认值
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }


        Page<StudentDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(() -> studentMapper.findAll());
        result.setSuccess(true);
        result.setData(
                new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), page.getResult()));
        return result;
    }

    @Override
    public Result<StudentDO> addStudent(StudentDO studentDO) {
        try {
            studentDO.setCreateTime(LocalDateTime.now());
            studentDO.setUpdateTime(LocalDateTime.now());

            int affectedRows = studentMapper.add(studentDO);
            if (affectedRows > 0) {
                return new Result<StudentDO>().success(studentDO);
            }
            return new Result<StudentDO>().error("400", "添加用户失败");
        } catch (Exception e) {
            return new Result<StudentDO>().error("500", "添加用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<StudentDO> updateStudent(StudentDO studentDO) {
        try {
            studentDO.setUpdateTime(LocalDateTime.now());

            int affectedRows = studentMapper.update(studentDO);
            if (affectedRows > 0) {
                return new Result<StudentDO>().success(studentDO);
            }
            return new Result<StudentDO>().error("400", "更新用户失败");
        } catch (Exception e) {
            return new Result<StudentDO>().error("500", "更新用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteStudent(Long id) {
        try {
            int affectedRows = studentMapper.delete(id);
            return new Result<Boolean>().success(affectedRows > 0);
        } catch (Exception e) {
            return new Result<Boolean>().error("500", "删除用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<StudentDO> findByRealName(String realName) {
        try {
            StudentDO studentDO = studentMapper.findByRealName(realName);
            if (studentDO != null) {
                return new Result<StudentDO>().success(studentDO);
            }
            return new Result<StudentDO>().error("404", "用户不存在");
        } catch (Exception e) {
            return new Result<StudentDO>().error("500", "查询用户异常: " + e.getMessage());
        }
    }


    @Override
    public StudentDO getStudentById(Long id) {
        return studentMapper.findById(id);
    }

}




