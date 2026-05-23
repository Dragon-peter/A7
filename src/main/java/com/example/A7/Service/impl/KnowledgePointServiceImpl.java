package com.example.A7.Service.impl;

import com.example.A7.DO.KnowledgePointDO;
import com.example.A7.Mapper.KnowledgePointMapper;
import com.example.A7.Model.Result;
import com.example.A7.Service.KnowledgePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识点服务实现
 */
@Slf4j
@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    @Override
    public Result<KnowledgePointDO> addKnowledgePoint(KnowledgePointDO knowledgePointDO) {
        Result<KnowledgePointDO> result = new Result<>();
        try {
            // 设置创建时间
            knowledgePointDO.setCreateTime(LocalDateTime.now());
            knowledgePointDO.setUpdateTime(LocalDateTime.now());
            
            int rows = knowledgePointMapper.add(knowledgePointDO);
            if (rows > 0) {
                return result.success(knowledgePointDO);
            }
            return result.error("400", "添加知识点失败");
        } catch (Exception e) {
            log.error("添加知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<KnowledgePointDO> updateKnowledgePoint(KnowledgePointDO knowledgePointDO) {
        Result<KnowledgePointDO> result = new Result<>();
        try {
            // 设置更新时间
            knowledgePointDO.setUpdateTime(LocalDateTime.now());
            
            int rows = knowledgePointMapper.update(knowledgePointDO);
            if (rows > 0) {
                return result.success(knowledgePointDO);
            }
            return result.error("400", "更新知识点失败");
        } catch (Exception e) {
            log.error("更新知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteKnowledgePoint(Long id) {
        Result<Boolean> result = new Result<>();
        try {
            int rows = knowledgePointMapper.delete(id);
            if (rows > 0) {
                return result.success(true);
            }
            return result.error("400", "删除知识点失败");
        } catch (Exception e) {
            log.error("删除知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<KnowledgePointDO> getKnowledgePointById(Long id) {
        Result<KnowledgePointDO> result = new Result<>();
        try {
            KnowledgePointDO knowledgePoint = knowledgePointMapper.findById(id);
            if (knowledgePoint != null) {
                return result.success(knowledgePoint);
            }
            return result.error("404", "知识点不存在");
        } catch (Exception e) {
            log.error("查询知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<KnowledgePointDO>> getKnowledgePointsByCourseId(Long courseId) {
        Result<List<KnowledgePointDO>> result = new Result<>();
        try {
            List<KnowledgePointDO> knowledgePoints = knowledgePointMapper.findByCourseId(courseId);
            return result.success(knowledgePoints);
        } catch (Exception e) {
            log.error("查询课程知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<KnowledgePointDO>> getKnowledgePointsByChapterId(Long chapterId) {
        Result<List<KnowledgePointDO>> result = new Result<>();
        try {
            List<KnowledgePointDO> knowledgePoints = knowledgePointMapper.findByChapterId(chapterId);
            return result.success(knowledgePoints);
        } catch (Exception e) {
            log.error("查询章节知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<KnowledgePointDO>> searchKnowledgePoints(String keyword) {
        Result<List<KnowledgePointDO>> result = new Result<>();
        try {
            List<KnowledgePointDO> knowledgePoints = knowledgePointMapper.findByName("%" + keyword + "%");
            return result.success(knowledgePoints);
        } catch (Exception e) {
            log.error("搜索知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<KnowledgePointDO>> getAllKnowledgePoints() {
        Result<List<KnowledgePointDO>> result = new Result<>();
        try {
            List<KnowledgePointDO> knowledgePoints = knowledgePointMapper.findAll();
            return result.success(knowledgePoints);
        } catch (Exception e) {
            log.error("查询所有知识点失败", e);
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }
}
