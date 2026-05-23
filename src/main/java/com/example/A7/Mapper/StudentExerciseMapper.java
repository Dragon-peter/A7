package com.example.A7.Mapper;

import com.example.A7.DO.StudentExerciseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【student_exercise(学生习题记录表)】的数据库操作Mapper
 */
@Mapper
public interface StudentExerciseMapper {
    /**
     * 添加学生习题记录
     */
    int add(StudentExerciseDO studentExerciseDO);

    /**
     * 更新学生习题记录
     */
    int update(StudentExerciseDO studentExerciseDO);

    /**
     * 删除学生习题记录
     */
    int delete(@Param("id") long id);
    
    /**
     * 根据ID查询学生习题记录
     */
    StudentExerciseDO findById(@Param("id") Long id);
    
    /**
     * 根据学生ID查询习题记录
     */
    List<StudentExerciseDO> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据习题ID查询学生记录
     */
    List<StudentExerciseDO> findByExerciseId(@Param("exerciseId") Long exerciseId);
    
    /**
     * 根据学生ID和习题ID查询记录
     */
    StudentExerciseDO findByStudentIdAndExerciseId(@Param("studentId") Long studentId, @Param("exerciseId") Long exerciseId);
    
    /**
     * 根据是否正确查询记录
     */
    List<StudentExerciseDO> findByIsCorrect(@Param("isCorrect") Integer isCorrect);
    
    /**
     * 查询所有学生习题记录
     */
    List<StudentExerciseDO> findAll();
    
    /**
     * 分页查询学生习题记录
     */
    List<StudentExerciseDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计学生习题记录总数
     */
    int countTotal();
    
    /**
     * 统计学生正确答题数
     */
    int countCorrectByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 统计习题正确率
     */
    double getCorrectRateByExerciseId(@Param("exerciseId") Long exerciseId);
} 