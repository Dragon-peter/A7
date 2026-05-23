package com.example.A7.Controller;

import com.example.A7.DO.UserDO;
import com.example.A7.Model.LoginRequest;
import com.example.A7.Model.Result;
import com.example.A7.Security.CustomUserDetails;
import com.example.A7.Security.CustomUserDetailsService;
import com.example.A7.Security.JwtTokenUtil;
import com.example.A7.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private UserService userService;

    /**
     * 用户登录并获取JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            // 先通过自定义服务验证用户名和密码
            Result<UserDO> loginResult = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

            if (!loginResult.isSuccess()) {
                return ResponseEntity.badRequest().body(loginResult);
            }
            
            // 使用Spring Security进行认证
            authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            
            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            
            // 生成JWT令牌
            String token = jwtTokenUtil.generateToken(userDetails);
            
            // 准备响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("tokenType", "Bearer");
            
            if (userDetails instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                responseData.put("userId", customUserDetails.getUserId());
                responseData.put("username", customUserDetails.getUsername());
                responseData.put("role", customUserDetails.getRole());
            }
            
            return ResponseEntity.ok(result.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(result.error("401", "认证失败: " + e.getMessage()));
        }
    }
    
    /**
     * 使用Spring Security进行认证
     */
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("用户已被禁用", e);
        } catch (BadCredentialsException e) {
            throw new Exception("无效的凭证", e);
        }
    }
} 