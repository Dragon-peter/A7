package com.example.A7.Service;

import com.example.A7.DO.KnowledgePointDO;
import com.example.A7.Model.Result;

import java.util.List;

/**
 * 知识点服务接口
 */
public interface KnowledgePointService {

    /**
     * 添加知识点
     * @param knowledgePointDO 知识点信息
     * @return 添加结果
     */
    Result<KnowledgePointDO> addKnowledgePoint(KnowledgePointDO knowledgePointDO);

    /**
     * 更新知识点
     * @param knowledgePointDO 知识点信息
     * @return 更新结果
     */
    Result<KnowledgePointDO> updateKnowledgePoint(KnowledgePointDO knowledgePointDO);

    /**
     * 删除知识点
     * @param id 知识点ID
     * @return 删除结果
     */
    Result<Boolean> deleteKnowledgePoint(Long id);

    /**
     * 根据ID查询知识点
     * @param id 知识点ID
     * @return 知识点信息
     */
    Result<KnowledgePointDO> getKnowledgePointById(Long id);

    /**
     * 根据课程ID查询知识点
     * @param courseId 课程ID
     * @return 知识点列表
     */
    Result<List<KnowledgePointDO>> getKnowledgePointsByCourseId(Long courseId);

    /**
     * 根据章节ID查询知识点
     * @param chapterId 章节ID
     * @return 知识点列表
     */
    Result<List<KnowledgePointDO>> getKnowledgePointsByChapterId(Long chapterId);

    /**
     * 搜索知识点
     * @param keyword 搜索关键词
     * @return 知识点列表
     */
    Result<List<KnowledgePointDO>> searchKnowledgePoints(String keyword);

    /**
     * 获取所有知识点
     * @return 知识点列表
     */
    Result<List<KnowledgePointDO>> getAllKnowledgePoints();
}
