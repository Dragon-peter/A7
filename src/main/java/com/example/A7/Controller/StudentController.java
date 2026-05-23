package com.example.A7.Controller;

import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // 获取所有用户（分页）
    @GetMapping("/page")
    @ResponseBody
    public Result<Paging<StudentDO>> findAllStudents(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return studentService.findAllStudents(pageNum, pageSize);
    }

    // 根据用户名查询
    @GetMapping("/username/{username}")
    @ResponseBody
    public Result<StudentDO> findByRealName(@PathVariable String realName) {
        return studentService.findByRealName(realName);
    }

    // 添加用户
    @PostMapping("/add")
    @ResponseBody
    public Result<StudentDO> add(@RequestBody StudentDO studentDO) {
        return studentService.addStudent(studentDO);
    }

    // 更新用户
    @PutMapping("/update/{id}")
    @ResponseBody
    public Result<StudentDO> updateUser(@PathVariable Long id,
                                     @RequestBody StudentDO studentDO) {
        studentDO.setId(id);
        return studentService.updateStudent(studentDO);
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

    @GetMapping("/getById")
    @ResponseBody
    public StudentDO getById(@RequestParam("id") Long id) {
        return studentService.getStudentById(id);
    }
}
