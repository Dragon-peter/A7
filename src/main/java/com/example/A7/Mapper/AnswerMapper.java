package com.example.A7.Mapper;

import com.example.A7.DO.AnswerDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【answer(回答表)】的数据库操作Mapper
 */
@Mapper
public interface AnswerMapper {
    /**
     * 添加回答
     */
    int add(AnswerDO answerDO);

    /**
     * 更新回答信息
     */
    int update(AnswerDO answerDO);

    /**
     * 删除回答
     */
    int delete(@Param("id") long id);

    int deleteByQuestion(@Param("questionId") long questionId);
    /**
     * 根据ID查询回答
     */
    AnswerDO findById(@Param("id") Long id);
    
    /**
     * 根据问题ID查询回答
     */
    List<AnswerDO> findByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 根据作者ID查询回答
     */
    List<AnswerDO> findByAuthorId(@Param("authorId") Long authorId);
    
    /**
     * 根据作者类型查询回答
     */
    List<AnswerDO> findByAuthorType(@Param("authorType") String authorType);
    
    /**
     * 根据问题ID和作者类型查询回答
     */
    List<AnswerDO> findByQuestionIdAndAuthorType(@Param("questionId") Long questionId, @Param("authorType") String authorType);
    
    /**
     * 查询所有回答
     */
    List<AnswerDO> findAll();
    
    /**
     * 分页查询回答
     */
    List<AnswerDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计回答总数
     */
    int countTotal();
} 