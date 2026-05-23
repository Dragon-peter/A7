package com.example.A7.Service.impl;

import com.example.A7.DO.TeacherDO;
import com.example.A7.Mapper.TeacherMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.TeacherService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TeacherServiceImpl implements TeacherService{
    @Autowired
    private TeacherMapper teacherMapper;


    @Override
    public Result<Paging<TeacherDO>> findAllTeachers(Integer pageNum, Integer pageSize) {
        Result<Paging<TeacherDO>> result = new Result<>();

        // 设置默认值
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }


        Page<TeacherDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(() -> teacherMapper.findAll());
        result.setSuccess(true);
        result.setData(
                new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), page.getResult()));
        return result;
    }

    @Override
    public Result<TeacherDO> addTeachers(TeacherDO teacherDO) {
        try {
            teacherDO.setCreateTime(LocalDateTime.now());
            teacherDO.setUpdateTime(LocalDateTime.now());

            int affectedRows = teacherMapper.add(teacherDO);
            if (affectedRows > 0) {
                return new Result<TeacherDO>().success(teacherDO);
            }
            return new Result<TeacherDO>().error("400", "添加用户失败");
        } catch (Exception e) {
            return new Result<TeacherDO>().error("500", "添加用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<TeacherDO> updateTeachers(TeacherDO teacherDO) {
        try {
            teacherDO.setUpdateTime(LocalDateTime.now());

            int affectedRows = teacherMapper.update(teacherDO);
            if (affectedRows > 0) {
                return new Result<TeacherDO>().success(teacherDO);
            }
            return new Result<TeacherDO>().error("400", "更新用户失败");
        } catch (Exception e) {
            return new Result<TeacherDO>().error("500", "更新用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteTeachers(Long id) {
        try {
            int affectedRows = teacherMapper.delete(id);
            return new Result<Boolean>().success(affectedRows > 0);
        } catch (Exception e) {
            return new Result<Boolean>().error("500", "删除用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<TeacherDO> findByRealName(String realName) {
        try {
            TeacherDO teacherDO = teacherMapper.findByRealName(realName);
            if (teacherDO != null) {
                return new Result<TeacherDO>().success(teacherDO);
            }
            return new Result<TeacherDO>().error("404", "用户不存在");
        } catch (Exception e) {
            return new Result<TeacherDO>().error("500", "查询用户异常: " + e.getMessage());
        }
    }

    @Override
    public TeacherDO getTeacherById(Long id) {
        return teacherMapper.findById(id);
    }

}




