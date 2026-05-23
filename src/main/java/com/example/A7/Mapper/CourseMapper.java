package com.example.A7.Mapper;

import com.example.A7.DO.CourseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【course(课程表)】的数据库操作Mapper
 */
@Mapper
public interface CourseMapper {
    /**
     * 添加课程
     */
    int add(CourseDO courseDO);
    
    /**
     * 批量添加课程
     */
    int batchAdd(List<CourseDO> courseList);

    /**
     * 更新课程信息
     */
    int update(CourseDO courseDO);

    /**
     * 删除课程
     */
    int delete(@Param("id") long id);
    
    /**
     * 查询所有课程
     */
    List<CourseDO> findAll();
    
    /**
     * 分页查询课程
     */
    List<CourseDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计课程总数
     */
    int countTotal();

    /**
     * 根据名称查询课程
     */
    List<CourseDO> findByName(@Param("name") String name);
    
    /**
     * 根据ID查询课程
     */
    CourseDO findById(@Param("id") Long id);
    
    /**
     * 根据课程代码查询课程
     */
    CourseDO findByCode(@Param("code") String code);
    
    /**
     * 根据教师ID查询课程
     */
    List<CourseDO> findByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据状态查询课程
     */
    List<CourseDO> findByStatus(@Param("status") Integer status);
    
    /**
     * 修改课程状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);


}