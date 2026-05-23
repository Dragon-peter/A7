package com.example.A7.Mapper;

import com.example.A7.DO.TeacherDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【teacher(教师信息表)】的数据库操作Mapper
 */
@Mapper
public interface TeacherMapper {
    /**
     * 添加教师
     */
    int add(TeacherDO teacherDO);
    
    /**
     * 批量添加教师
     */
    int batchAdd(List<TeacherDO> teacherList);

    /**
     * 更新教师信息
     */
    int update(TeacherDO teacherDO);

    /**
     * 删除教师
     */
    int delete(@Param("id") long id);
    
    /**
     * 查询所有教师
     */
    List<TeacherDO> findAll();
    
    /**
     * 分页查询教师
     */
    List<TeacherDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计教师总数
     */
    int countTotal();

    /**
     * 根据姓名查询教师
     */
    TeacherDO findByRealName(@Param("realName") String realName);
    
    /**
     * 根据ID查询教师
     */
    TeacherDO findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询教师
     */
    TeacherDO findByUserId(@Param("userId") Long userId);
    

    
    /**
     * 根据部门查询教师
     */
    List<TeacherDO> findByDepartment(@Param("department") String department);
    
    /**
     * 根据职称查询教师
     */
    List<TeacherDO> findByTitle(@Param("title") String title);
    
    /**
     * 根据研究领域查询教师
     */
    List<TeacherDO> findByResearchArea(@Param("researchArea") String researchArea);
}
