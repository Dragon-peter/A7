package com.example.A7.Controller;

import com.example.A7.Model.Result;
import com.example.A7.Service.LocalResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 本地资源管理控制器
 * 不依赖数据库，使用本地文件系统
 */
@Slf4j
@RestController
@RequestMapping("/api/local/resource")
@CrossOrigin(origins = "*")
public class LocalResourceController {

    @Autowired
    private LocalResourceService localResourceService;

    /**
     * 添加资源
     */
    @PostMapping
    public Result<LocalResourceService.ResourceInfo> addResource(@RequestBody Map<String, Object> request) {
        log.info("添加本地资源: {}", request);

        try {
            String title = (String) request.get("title");
            String type = (String) request.get("type");
            String url = (String) request.get("url");
            String fileName = (String) request.get("fileName");
            String originalName = (String) request.get("originalName");
            String description = (String) request.get("description");
            
            // 处理size字段
            Long size = null;
            Object sizeObj = request.get("size");
            if (sizeObj != null) {
                if (sizeObj instanceof Number) {
                    size = ((Number) sizeObj).longValue();
                } else if (sizeObj instanceof String) {
                    try {
                        size = Long.parseLong((String) sizeObj);
                    } catch (NumberFormatException e) {
                        log.warn("无法解析size字段: {}", sizeObj);
                    }
                }
            }

            if (title == null || title.trim().isEmpty()) {
                return new Result<LocalResourceService.ResourceInfo>().error("400", "资源标题不能为空");
            }

            if (type == null || type.trim().isEmpty()) {
                return new Result<LocalResourceService.ResourceInfo>().error("400", "资源类型不能为空");
            }

            return localResourceService.addResource(title, type, url, fileName, originalName, description, size);

        } catch (Exception e) {
            log.error("添加资源失败", e);
            return new Result<LocalResourceService.ResourceInfo>().error("500", "添加资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有资源
     */
    @GetMapping
    public Result<List<LocalResourceService.ResourceInfo>> getAllResources() {
        log.info("获取所有本地资源");
        return localResourceService.getAllResources();
    }

    /**
     * 根据ID获取资源
     */
    @GetMapping("/{id}")
    public Result<LocalResourceService.ResourceInfo> getResourceById(@PathVariable Long id) {
        log.info("获取本地资源: {}", id);
        return localResourceService.getResourceById(id);
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        log.info("删除本地资源: {}", id);
        return localResourceService.deleteResource(id);
    }
}
