package com.example.A7.Mapper;

import com.example.A7.DO.KnowledgePointDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【knowledge_point(知识点表)】的数据库操作Mapper
 */
@Mapper
public interface KnowledgePointMapper {
    /**
     * 添加知识点
     */
    int add(KnowledgePointDO knowledgePointDO);
    
    /**
     * 批量添加知识点
     */
    int batchAdd(List<KnowledgePointDO> knowledgePointList);

    /**
     * 更新知识点信息
     */
    int update(KnowledgePointDO knowledgePointDO);

    /**
     * 删除知识点
     */
    int delete(@Param("id") long id);
    
    /**
     * 根据ID查询知识点
     */
    KnowledgePointDO findById(@Param("id") Long id);
    
    /**
     * 根据课程ID查询知识点
     */
    List<KnowledgePointDO> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据章节ID查询知识点
     */
    List<KnowledgePointDO> findByChapterId(@Param("chapterId") Long chapterId);
    
    /**
     * 根据名称查询知识点
     */
    List<KnowledgePointDO> findByName(@Param("name") String name);
    
    /**
     * 查询所有知识点
     */
    List<KnowledgePointDO> findAll();
} 