package com.example.A7.Mapper;

import com.example.A7.DO.ExerciseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【exercise(习题表)】的数据库操作Mapper
 */
@Mapper
public interface ExerciseMapper {
    /**
     * 添加习题
     */
    int add(ExerciseDO exerciseDO);
    
    /**
     * 批量添加习题
     */
    int batchAdd(List<ExerciseDO> exerciseList);

    /**
     * 更新习题信息
     */
    int update(ExerciseDO exerciseDO);

    /**
     * 删除习题
     */
    int delete(@Param("id") long id);
    
    /**
     * 根据ID查询习题
     */
    ExerciseDO findById(@Param("id") Long id);
    
    /**
     * 根据章节ID查询习题
     */
    List<ExerciseDO> findByChapterId(@Param("chapterId") Long chapterId);
    
    /**
     * 根据知识点ID查询习题
     */
    List<ExerciseDO> findByKnowledgePointId(@Param("knowledgePointId") Long knowledgePointId);
    
    /**
     * 根据类型查询习题
     */
    List<ExerciseDO> findByType(@Param("type") String type);
    
    /**
     * 根据难度查询习题
     */
    List<ExerciseDO> findByDifficulty(@Param("difficulty") Integer difficulty);
    
    /**
     * 根据标题查询习题
     */
    List<ExerciseDO> findByTitle(@Param("title") String title);
    
    /**
     * 查询所有习题
     */
    List<ExerciseDO> findAll();
    
    /**
     * 分页查询习题
     */
    List<ExerciseDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计习题总数
     */
    int countTotal();
} 