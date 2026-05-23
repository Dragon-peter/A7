package com.example.A7.Service;

import com.example.A7.DO.UserDO;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;

public interface UserService {

    // 用户注册（增强版，根据角色创建对应表记录）
    public Result<UserDO> register(UserDO userDO);

    // 用户登录
    public Result<UserDO> login(String username,String password);
    
    // 用户退出登录
    public Result<Boolean> logout(Long userId);
    
    // 获取所有用户（分页）
    public Result<Paging<UserDO>> findAllUsers(Integer pageNum, Integer pageSize);

    // 添加用户
    public Result<UserDO> addUser(UserDO userDO);

    // 更新用户
    public Result<UserDO> updateUser(UserDO userDO);

    // 删除用户
    public Result<Boolean> deleteUser(Long id);

    // 根据用户名查询
    public Result<UserDO> findByUsername(String username);

    UserDO getById(Long id);
}
