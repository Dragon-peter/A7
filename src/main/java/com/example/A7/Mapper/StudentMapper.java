package com.example.A7.Mapper;

import com.example.A7.DO.StudentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【student(学生信息表)】的数据库操作Mapper
 */
@Mapper
public interface StudentMapper {
    /**
     * 添加学生信息
     */
    int add(StudentDO studentDO);
    
    /**
     * 批量添加学生
     */
    int batchAdd(List<StudentDO> studentList);

    /**
     * 更新学生信息
     */
    int update(StudentDO studentDO);

    /**
     * 删除学生
     */
    int delete(@Param("id") long id);
    
    /**
     * 查询所有学生
     */
    List<StudentDO> findAll();
    
    /**
     * 分页查询学生
     */
    List<StudentDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计学生总数
     */
    int countTotal();

    /**
     * 根据姓名查询学生
     */
    StudentDO findByRealName(@Param("realName") String realName);
    
    /**
     * 根据ID查询学生
     */
    StudentDO findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询学生
     */
    StudentDO findByUserId(@Param("userId") Long userId);
    

    
    /**
     * 根据班级ID查询学生
     */
    List<StudentDO> findByClassId(@Param("classId") Integer classId);
    
    /**
     * 根据专业查询学生
     */
    List<StudentDO> findByMajor(@Param("major") String major);
    
    /**
     * 根据入学年份查询学生
     */
    List<StudentDO> findByAdmissionYear(@Param("admissionYear") Integer admissionYear);
}
