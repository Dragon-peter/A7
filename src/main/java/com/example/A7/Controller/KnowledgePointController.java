package com.example.A7.Controller;

import com.example.A7.DO.KnowledgePointDO;
import com.example.A7.Model.Result;
import com.example.A7.Service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识点管理控制器
 */
@RestController
@RequestMapping("/api/knowledge-point")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService knowledgePointService;

    /**
     * 添加知识点
     */
    @PostMapping
    @ResponseBody
    public Result<KnowledgePointDO> addKnowledgePoint(@RequestBody KnowledgePointDO knowledgePointDO) {
        return knowledgePointService.addKnowledgePoint(knowledgePointDO);
    }

    /**
     * 更新知识点
     */
    @PutMapping("/{id}")
    @ResponseBody
    public Result<KnowledgePointDO> updateKnowledgePoint(@PathVariable Long id, @RequestBody KnowledgePointDO knowledgePointDO) {
        knowledgePointDO.setId(id);
        return knowledgePointService.updateKnowledgePoint(knowledgePointDO);
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result<Boolean> deleteKnowledgePoint(@PathVariable Long id) {
        return knowledgePointService.deleteKnowledgePoint(id);
    }

    /**
     * 根据ID查询知识点
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Result<KnowledgePointDO> getKnowledgePointById(@PathVariable Long id) {
        return knowledgePointService.getKnowledgePointById(id);
    }

    /**
     * 根据课程ID查询知识点
     */
    @GetMapping("/course/{courseId}")
    @ResponseBody
    public Result<List<KnowledgePointDO>> getKnowledgePointsByCourseId(@PathVariable Long courseId) {
        return knowledgePointService.getKnowledgePointsByCourseId(courseId);
    }

    /**
     * 根据章节ID查询知识点
     */
    @GetMapping("/chapter/{chapterId}")
    @ResponseBody
    public Result<List<KnowledgePointDO>> getKnowledgePointsByChapterId(@PathVariable Long chapterId) {
        return knowledgePointService.getKnowledgePointsByChapterId(chapterId);
    }

    /**
     * 搜索知识点
     */
    @GetMapping("/search")
    @ResponseBody
    public Result<List<KnowledgePointDO>> searchKnowledgePoints(@RequestParam String keyword) {
        return knowledgePointService.searchKnowledgePoints(keyword);
    }

    /**
     * 获取所有知识点
     */
    @GetMapping
    @ResponseBody
    public Result<List<KnowledgePointDO>> getAllKnowledgePoints() {
        return knowledgePointService.getAllKnowledgePoints();
    }
}
