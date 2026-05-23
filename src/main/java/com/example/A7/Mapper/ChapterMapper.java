package com.example.A7.Mapper;

import com.example.A7.DO.ChapterDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【chapter(章节表)】的数据库操作Mapper
 */
@Mapper
public interface ChapterMapper {
    /**
     * 添加章节
     */
    int add(ChapterDO chapterDO);
    
  
int countByCourseId(@Param("courseId") Long courseId);

    /**
     * 批量添加章节
     */
    int batchAdd(List<ChapterDO> chapterList);

    /**
     * 更新章节信息
     */
    int update(ChapterDO chapterDO);

    /**
     * 删除章节
     */
    int delete(@Param("id") long id);
    int deleteByCourseId(@Param("courseId") long courseId);
    /**
     * 查询所有章节
     */
    List<ChapterDO> findAll();
    
    /**
     * 根据ID查询章节
     */
    ChapterDO findById(@Param("id") Long id);
    
    /**
     * 根据课程ID查询章节
     */
    List<ChapterDO> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据课程ID查询章节，按排序号排序
     */
    List<ChapterDO> findByCourseIdOrdered(@Param("courseId") Long courseId);
    
    /**
     * 根据标题查询章节
     */
    List<ChapterDO> findByTitle(@Param("title") String title);
    
    /**
     * 更新章节排序号
     */
    int updateOrderNum(@Param("id") Long id, @Param("orderNum") Integer orderNum);
} 