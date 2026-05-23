package com.example.A7.Security;

import com.example.A7.DO.StudentDO;
import com.example.A7.DO.TeacherDO;
import com.example.A7.DO.UserDO;
import com.example.A7.Mapper.StudentMapper;
import com.example.A7.Mapper.TeacherMapper;
import com.example.A7.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDO user = userMapper.findByUserName(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 根据角色加载额外信息
        switch (user.getRole()) {
            case "TEACHER":
                TeacherDO teacher = teacherMapper.findByUserId(user.getId());
                if (teacher != null) {
                    // 可以在这里处理教师特有的信息
                }
                break;
            case "STUDENT":
                StudentDO student = studentMapper.findByUserId(user.getId());
                if (student != null) {
                    // 可以在这里处理学生特有的信息
                }
                break;
            case "ADMIN":
                // 管理员不需要额外信息
                break;
            default:
                // 默认处理
                break;
        }
        
        return new CustomUserDetails(user);
    }
} 