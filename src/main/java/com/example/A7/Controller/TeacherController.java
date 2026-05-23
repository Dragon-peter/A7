package com.example.A7.Controller;

import com.example.A7.DO.TeacherDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    // 获取所有用户（分页）
    @GetMapping("/page")
    @ResponseBody
    public Result<Paging<TeacherDO>> findAllTeachers(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teacherService.findAllTeachers(pageNum, pageSize);
    }

    // 根据用户名查询
    @GetMapping("/realName/{realName}")
    @ResponseBody
    public Result<TeacherDO> findByRealName(@PathVariable String realName) {
        return teacherService.findByRealName(realName);
    }

    // 添加用户
    @PostMapping("/add")
    @ResponseBody
    public Result<TeacherDO> add(@RequestBody TeacherDO teacherDO) {
        return teacherService.addTeachers(teacherDO);
    }

    // 更新用户
    @PutMapping("/update/{id}")
    @ResponseBody
    public Result<TeacherDO> update(@PathVariable Long id,
                                     @RequestBody TeacherDO teacherDO) {
        teacherDO.setId(id);
        return teacherService.updateTeachers(teacherDO);
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> delete(@PathVariable Long id) {
        return teacherService.deleteTeachers(id);
    }

    @GetMapping("/getById")
    @ResponseBody
    public TeacherDO getById(@RequestParam("id") Long id) {
        return teacherService.getTeacherById(id);
    }
}
