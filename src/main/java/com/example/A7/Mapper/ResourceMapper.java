package com.example.A7.Mapper;

import com.example.A7.DO.ResourceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【resource(资源表)】的数据库操作Mapper
 */
@Mapper
public interface ResourceMapper {
    /**
     * 添加资源
     */
    int add(ResourceDO resourceDO);
    
    /**
     * 批量添加资源
     */
    int batchAdd(List<ResourceDO> resourceList);
List<ResourceDO> findByChapterIds(@Param("chapterIds") List<Long> chapterIds);
    /**
     * 更新资源信息
     */
    int update(ResourceDO resourceDO);



int countByCourseId(@Param("courseId") Long courseId);

    /**
     * 删除资源
     */
    int delete(@Param("id") long id);
    int deleteByChapterId(@Param("chapterId") long chapterId);
    /**
     * 查询所有资源
     */
    List<ResourceDO> findAll();
    
    /**
     * 分页查询资源
     */
    List<ResourceDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据ID查询资源
     */
    ResourceDO findById(@Param("id") Long id);
    
    /**
     * 根据章节ID查询资源
     */
    List<ResourceDO> findByChapterId(@Param("chapterId") Long chapterId);
    
    /**
     * 根据标题查询资源
     */
    List<ResourceDO> findByTitle(@Param("title") String title);
    
    /**
     * 根据类型查询资源
     */
    List<ResourceDO> findByType(@Param("type") String type);
} 