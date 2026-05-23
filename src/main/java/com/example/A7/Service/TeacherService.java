package com.example.A7.Service;

import com.example.A7.DO.TeacherDO;
import com.example.A7.DO.UserDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

/**
* @author Lenovo
* @description 针对表【teacher(教师信息表)】的数据库操作Service
* @createDate 2025-06-17 15:52:23
*/
public interface TeacherService{

    // 获取所有用户（分页）
    public Result<Paging<TeacherDO>> findAllTeachers(Integer pageNum, Integer pageSize);

    // 添加用户
    public Result<TeacherDO> addTeachers(TeacherDO teacherDO);

    // 更新用户
    public Result<TeacherDO> updateTeachers(TeacherDO teacherDO);

    // 删除用户
    public Result<Boolean> deleteTeachers(Long id);

    // 根据用户名查询
    public Result<TeacherDO> findByRealName(String realName);

    TeacherDO getTeacherById(Long id);

}
