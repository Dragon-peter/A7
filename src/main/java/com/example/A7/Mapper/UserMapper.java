package com.example.A7.Mapper;

import com.example.A7.DO.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* @description 针对表【user(系统用户表)】的数据库操作Mapper
*/
@Mapper
public interface UserMapper {

    /**
     * 添加单个用户
     */
    int add(UserDO userDO);

    /**
     * 批量添加用户
     */
    int batchAdd(List<UserDO> userList);

    /**
     * 更新用户信息
     */
    int update(UserDO userDO);

    /**
     * 删除用户
     */
    int delete(@Param("id") long id);
    
    /**
     * 查询所有用户
     */
    List<UserDO> findAll();

    /**
     * 分页查询用户
     */
    List<UserDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计用户总数
     */
    int countTotal();

    /**
     * 根据用户名查询用户
     */
    UserDO findByUserName(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    UserDO findById(@Param("id") Long id);
    
    /**
     * 根据角色查询用户
     */
    List<UserDO> findByRole(@Param("role") String role);
    
    /**
     * 根据状态查询用户
     */
    List<UserDO> findByStatus(@Param("status") Integer status);
    
    /**
     * 根据邮箱查询用户
     */
    UserDO findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户
     */
    UserDO findByPhone(@Param("phone") String phone);
    
    /**
     * 修改用户状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}




