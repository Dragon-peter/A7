package com.example.A7.AI.AiService.AiServiceImpl;

import com.example.A7.AI.AiService.UserAiService;
import com.example.A7.DO.UserDO;
import com.example.A7.Model.Result;
import com.example.A7.Service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;


@Service
public class UserAiServiceImpl implements UserAiService {
    private static final Logger log = LoggerFactory.getLogger(UserAiServiceImpl.class);
    @Autowired
    private UserService userService;

    @Override
    public Result<UserDO> aiGetUserInfo(Long userId) {
        UserDO user = userService.getById(userId);
        if (user == null) {
            return Result.error("404", "用户不存在");
        }
        return Result.success(user);
    }

    @Override
    public Result<UserDO> aiUpdateUser(Long userId, Map<String, Object> updateFields) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            return Result.error("400", "用户ID不合法");
        }
        if (updateFields == null || updateFields.isEmpty()) {
            return Result.error("400", "更新字段不能为空");
        }

        // 2. 获取现有用户信息
        UserDO user = userService.getById(userId);
        if (user == null) {
            return Result.error("404", "用户不存在");
        }

        // 3. 创建更新日志（用于审计）
        StringBuilder updateLog = new StringBuilder();
        updateLog.append("AI更新用户[").append(userId).append("]:");

        // 4. 只允许更新特定字段（白名单机制）
        try {
            if (updateFields.containsKey("realName")) {
                String newRealName = (String) updateFields.get("realName");
                if (StringUtils.isNotBlank(newRealName)) {
                    updateLog.append(" 真实姓名[").append(user.getRealName()).append("->").append(newRealName).append("]");
                    user.setRealName(newRealName);
                }
            }

            if (updateFields.containsKey("email")) {
                String newEmail = (String) updateFields.get("email");
                if (StringUtils.isNotBlank(newEmail) && isValidEmail(newEmail)) {
                    updateLog.append(" 邮箱[").append(user.getEmail()).append("->").append(newEmail).append("]");
                    user.setEmail(newEmail);
                }
            }

            if (updateFields.containsKey("status")) {
                Integer newStatus = (Integer) updateFields.get("status");
                if (newStatus != null && (newStatus == 0 || newStatus == 1)) {
                    updateLog.append(" 状态[").append(user.getStatus()).append("->").append(newStatus).append("]");
                    user.setStatus(newStatus);
                }
            }

            if (updateFields.containsKey("role")) {
                String newRole = (String) updateFields.get("role");
                if (StringUtils.isNotBlank(newRole) &&
                        ("管理员".equals(newRole) || "教师".equals(newRole) || "学生".equals(newRole))) {
                    updateLog.append(" 角色[").append(user.getRole()).append("->").append(newRole).append("]");
                    user.setRole(newRole);
                }
            }

            // 5. 如果没有有效更新字段
            if (updateLog.toString().equals("AI更新用户[" + userId + "]:")) {
                return Result.error("400", "没有提供有效的更新字段");
            }

            // 6. 设置更新时间
            user.setUpdateTime(LocalDateTime.now());

            // 7. 记录更新日志
            log.info(updateLog.toString());

            // 8. 调用原有更新方法
            return userService.updateUser(user);
        } catch (ClassCastException e) {
            log.error("AI更新用户字段类型错误", e);
            return Result.error("400", "字段类型不匹配");
        }
    }

    // 简单的邮箱格式验证
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    @Override
    public Result<Boolean> aiDeleteUser(Long userId) {
        return userService.deleteUser(userId);
    }
}
