package com.example.A7.Mapper;

import com.example.A7.DO.StudentCourseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【student_course(学生课程关系表)】的数据库操作Mapper
 */
@Mapper
public interface StudentCourseMapper {
    /**
     * 添加学生课程关系
     */
    int add(StudentCourseDO studentCourseDO);
    
    /**
     * 批量添加学生课程关系
     */
    int batchAdd(List<StudentCourseDO> studentCourseList);

    /**
     * 更新学生课程关系
     */
    int update(StudentCourseDO studentCourseDO);

    /**
     * 删除学生课程关系
     */
    int delete(@Param("id") long id);
   int deleteByCourseId(@Param("courseId") long courseId);
    /**
     * 根据ID查询学生课程关系
     */
    StudentCourseDO findById(@Param("id") Long id);
    
    /**
     * 根据学生ID查询其选修的课程关系
     */
    List<StudentCourseDO> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据课程ID查询选修该课程的学生关系
     */
    List<StudentCourseDO> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据学生ID和课程ID查询关系
     */
    StudentCourseDO findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    /**
     * 更新学习进度
     */
    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress);
    
    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 根据状态查询学生课程关系
     */
    List<StudentCourseDO> findByStatus(@Param("status") Integer status);

int countStudentByCourseId(@Param("courseId") Long courseId);

int countCompletedByCourseId(@Param("courseId") Long courseId);

} 