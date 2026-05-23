package com.example.A7.Mapper;

import com.example.A7.DO.QuestionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【question(问题表)】的数据库操作Mapper
 */
@Mapper
public interface QuestionMapper {
    /**
     * 添加问题
     */
    int add(QuestionDO questionDO);

    /**
     * 更新问题信息
     */
    int update(QuestionDO questionDO);

    /**
     * 删除问题
     */
    int delete(@Param("id") long id);
    
    /**
     * 查询所有问题
     */
    List<QuestionDO> findAll();
    
    /**
     * 分页查询问题
     */
    List<QuestionDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据ID查询问题
     */
    QuestionDO findById(@Param("id") Long id);
    
    /**
     * 根据学生ID查询问题
     */
    List<QuestionDO> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据课程ID查询问题
     */
    List<QuestionDO> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据章节ID查询问题
     */
    List<QuestionDO> findByChapterId(@Param("chapterId") Long chapterId);
    
    /**
     * 根据状态查询问题
     */
    List<QuestionDO> findByStatus(@Param("status") String status);
    
    /**
     * 根据标题查询问题
     */
    List<QuestionDO> findByTitle(@Param("title") String title);
    
    /**
     * 更新问题状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 统计问题总数
     */
    int countTotal();
} 