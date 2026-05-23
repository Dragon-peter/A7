package com.example.A7.AI.AiService;

import com.example.A7.DO.UserDO;
import com.example.A7.Model.Result;

import java.util.Map;

public interface UserAiService {
    /**
     * AI查询用户信息
     */
    Result<UserDO> aiGetUserInfo(Long userId);

    /**
     * AI更新用户信息
     */
    Result<UserDO> aiUpdateUser(Long userId, Map<String, Object> updateFields);

    /**
     * AI删除用户
     */
    Result<Boolean> aiDeleteUser(Long userId);
}
