package com.example.A7.Controller;

import com.example.A7.DO.UserDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.A7.Security.JwtTokenUtil;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 用户注册
    @PostMapping("/register")
    @ResponseBody
    public Result<UserDO> register(@RequestBody UserDO userDO,
                                   HttpServletRequest request) {

        
        Result<UserDO> result = userService.register(userDO);
        return result;
    }



    // 获取所有用户（分页）
    @GetMapping("/page")
    @ResponseBody
    public Result<Paging<UserDO>> findAllUsers(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.findAllUsers(pageNum, pageSize);
    }

    // 根据用户名查询
    @GetMapping("/username/{username}")
    @ResponseBody
    public Result<UserDO> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    // 添加用户
    @PostMapping("/add")
    @ResponseBody
    public Result<UserDO> addUser(@RequestBody UserDO userDO) {
        return userService.addUser(userDO);
    }

    // 更新用户
    @PutMapping("/update/{id}")
    @ResponseBody
    public Result<UserDO> updateUser(@PathVariable Long id,
                                     @RequestBody UserDO userDO) {
        userDO.setId(id);
        return userService.updateUser(userDO);
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
    @GetMapping("/logout")
    @ResponseBody
    public Result<Void> logout(HttpServletRequest request) {
        Result<Void> result = new Result<>();
        try {
            // 从请求头中获取JWT令牌
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // 从JWT令牌中获取用户ID
                // 这里需要注入JwtTokenUtil
                Long userId = getUserIdFromToken(token);
                if (userId != null) {
                    userService.logout(userId);
                }
            }
            
            // 无论如何，都使会话失效
            request.getSession().invalidate();
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            return result.error("500", "退出登录失败：" + e.getMessage());
        }
    }
    
    // 辅助方法：从JWT令牌中获取用户ID
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    private Long getUserIdFromToken(String token) {
        try {
            Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
            return Long.parseLong(claims.get("userId").toString());
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/findById/{id}")
    @ResponseBody
    public UserDO findById(@PathVariable Long id) {
        return userService.getById(id);
    }

}
