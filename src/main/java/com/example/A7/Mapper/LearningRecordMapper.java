package com.example.A7.Mapper;

import com.example.A7.DO.LearningRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【learning_record(学习记录表)】的数据库操作Mapper
 */
@Mapper
public interface LearningRecordMapper {
    /**
     * 添加学习记录
     */
    int add(LearningRecordDO learningRecordDO);
List<LearningRecordDO> findByStudentIdAndResourceIds(@Param("studentId") Long studentId, @Param("resourceIds") List<Long> resourceIds);
    /**
     * 更新学习记录
     */
    int update(LearningRecordDO learningRecordDO);

    /**
     * 删除学习记录
     */
    int delete(@Param("id") long id);
    
    /**
     * 根据ID查询学习记录
     */
    LearningRecordDO findById(@Param("id") Long id);
    
    /**
     * 根据学生ID查询学习记录
     */
    List<LearningRecordDO> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据资源ID查询学习记录
     */
    List<LearningRecordDO> findByResourceId(@Param("resourceId") Long resourceId);
    
    /**
     * 根据学生ID和资源ID查询学习记录
     */
    LearningRecordDO findByStudentIdAndResourceId(@Param("studentId") Long studentId, @Param("resourceId") Long resourceId);
    
    /**
     * 更新学习进度
     */
    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress, @Param("lastPosition") Integer lastPosition);
    
    /**
     * 更新学习时长
     */
    int updateDuration(@Param("id") Long id, @Param("duration") Integer duration);
    
    /**
     * 查询所有学习记录
     */
    List<LearningRecordDO> findAll();
    
    /**
     * 分页查询学习记录
     */
    List<LearningRecordDO> findByPage(@Param("offset") int offset, @Param("limit") int limit);

int countLearnersByResourceId(@Param("resourceId") Long resourceId);

int countCompletedByResourceId(@Param("resourceId") Long resourceId);

double getAverageProgressByResourceId(@Param("resourceId") Long resourceId);

double getAverageDurationByResourceId(@Param("resourceId") Long resourceId);

int countLearnersByCourseId(@Param("courseId") Long courseId);

int countCompletedStudentsByCourseId(@Param("courseId") Long courseId);

double getAverageProgressByCourseId(@Param("courseId") Long courseId);
} 