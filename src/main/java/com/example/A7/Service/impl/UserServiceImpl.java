package com.example.A7.Service.impl;

import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.DO.UserDO;
import com.example.A7.Mapper.StudentMapper;
import com.example.A7.Mapper.TeacherMapper;
import com.example.A7.Mapper.UserMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Result<UserDO> register(UserDO userDO) {
        Result<UserDO> result = new Result<>();
        String username = userDO.getUsername();
        String password = userDO.getPassword();

        if (StringUtils.isBlank(username)) {
            return result.error("600", "用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return result.error("601", "密码不能为空");
        }

        // 检查用户名是否已存在
        UserDO existingUser = userMapper.findByUserName(username);
        if (existingUser != null) {
            return result.error("602", "用户名已存在");
        }
        String bcryptPwd = passwordEncoder.encode(password);
        // 创建新用户
        UserDO newUser = new UserDO();
        newUser.setUsername(username);
        newUser.setPassword(bcryptPwd);// 实际项目中应该加密
        newUser.setRealName(userDO.getRealName());
        newUser.setEmail(userDO.getEmail());
        newUser.setStatus(userDO.getStatus());
        newUser.setRole(userDO.getRole());
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        int affectedRows = userMapper.add(newUser);
      
        if (affectedRows > 0) {
            return result.success(newUser);
        }
        return result.error("605", "用户注册失败");
    }
    
    /**
     * 创建教师记录
     */
    private void createTeacherRecord(Long userId, String realName) {
        TeacherDO teacherDO = new TeacherDO();
        teacherDO.setUserId(userId);
        teacherDO.setDepartment("未分配"); // 默认部门
        teacherDO.setTitle("讲师"); // 默认职称
        teacherDO.setResearchArea("未设置"); // 默认研究领域
        teacherDO.setCreateTime(LocalDateTime.now());
        teacherDO.setUpdateTime(LocalDateTime.now());
        
        teacherMapper.add(teacherDO);
    }
    
    /**
     * 创建学生记录
     */
    private void createStudentRecord(Long userId, String realName) {
        StudentDO studentDO = new StudentDO();
        studentDO.setUserId(userId);
        studentDO.setClassId(1); // 默认班级ID
        studentDO.setMajor("未设置"); // 默认专业
        studentDO.setAdmissionYear(LocalDateTime.now().getYear()); // 默认为当前年份
        studentDO.setCreateTime(LocalDateTime.now());
        studentDO.setUpdateTime(LocalDateTime.now());
        
        studentMapper.add(studentDO);
    }

    @Override
    public Result<Boolean> logout(Long userId) {
        Result<Boolean> result = new Result<>();
        try {
            // 获取用户信息
            UserDO user = userMapper.findById(userId);
            if (user == null) {
                return result.error("404", "用户不存在");
            }
            user.setStatus(0);
            userMapper.update(user);
            // 我们不再将用户状态设为禁用，而是记录最后登出时间
            // 如果需要记录登出时间，可以在UserDO中添加lastLogoutTime字段
            // user.setLastLogoutTime(LocalDateTime.now());
            // userMapper.update(user);
            
            // 这里可以添加其他登出逻辑，比如清除缓存等
            
            return result.success(true);
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }
    @Override
    public Result<UserDO> login(String username,String password) {
        Result<UserDO> result = new Result<>();

        if (StringUtils.isBlank(username)) {
            return result.error("600", "用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return result.error("601", "密码不能为空");
        }

        UserDO user = userMapper.findByUserName(username);
        if (user == null) {
            return result.error("602", "用户名不存在");
        }
        // 用 BCrypt 验证密码（前端传明文，后端加密后比对）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return result.error("603", "密码不正确");
        }
        user.setStatus(1);
        userMapper.update(user);
        return result.success(user);
    }

    @Override
    public Result<Paging<UserDO>> findAllUsers(Integer pageNum, Integer pageSize) {
        Result<Paging<UserDO>> result = new Result<>();

        // 设置默认值
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }


        Page<UserDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(() -> userMapper.findAll());
        result.setSuccess(true);
        result.setData(
                new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), page.getResult()));
        return result;
    }

    @Override
    public Result<UserDO> addUser(UserDO userDO) {
        try {
            userDO.setCreateTime(LocalDateTime.now());
            userDO.setUpdateTime(LocalDateTime.now());

            int affectedRows = userMapper.add(userDO);
            if (affectedRows > 0) {
                return new Result<UserDO>().success(userDO);
            }
            return new Result<UserDO>().error("400", "添加用户失败");
        } catch (Exception e) {
            return new Result<UserDO>().error("500", "添加用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<UserDO> updateUser(UserDO userDO) {
        try {
            userDO.setUpdateTime(LocalDateTime.now());

            int affectedRows = userMapper.update(userDO);
            if (affectedRows > 0) {
                return new Result<UserDO>().success(userDO);
            }
            return new Result<UserDO>().error("400", "更新用户失败");
        } catch (Exception e) {
            return new Result<UserDO>().error("500", "更新用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteUser(Long id) {
        try {
            int affectedRows = userMapper.delete(id);
            return new Result<Boolean>().success(affectedRows > 0);
        } catch (Exception e) {
            return new Result<Boolean>().error("500", "删除用户异常: " + e.getMessage());
        }
    }

    @Override
    public Result<UserDO> findByUsername(String username) {
        try {
            UserDO user = userMapper.findByUserName(username);
            if (user != null) {
                return new Result<UserDO>().success(user);
            }
            return new Result<UserDO>().error("404", "用户不存在");
        } catch (Exception e) {
            return new Result<UserDO>().error("500", "查询用户异常: " + e.getMessage());
        }
    }

    @Override
    public UserDO getById(Long id) {
        return userMapper.findById(id);
    }
}
