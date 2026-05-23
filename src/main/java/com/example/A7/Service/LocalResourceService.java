package com.example.A7.Service;

import com.example.A7.Model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地资源管理服务
 * 不依赖数据库，使用内存和文件系统存储
 */
@Slf4j
@Service
public class LocalResourceService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    // 内存中存储资源信息
    private final Map<Long, ResourceInfo> resourceMap = new ConcurrentHashMap<>();
    private Long nextId = 1L;

    /**
     * 资源信息类
     */
    public static class ResourceInfo {
        private Long id;
        private String title;
        private String type;
        private String url;
        private String fileName;
        private String originalName;
        private String description;
        private Long size;
        private String createTime;

        // 构造函数
        public ResourceInfo() {}

        public ResourceInfo(Long id, String title, String type, String url, String fileName, 
                          String originalName, String description, Long size) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.url = url;
            this.fileName = fileName;
            this.originalName = originalName;
            this.description = description;
            this.size = size;
            this.createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
    }

    /**
     * 添加资源
     */
    public Result<ResourceInfo> addResource(String title, String type, String url, 
                                          String fileName, String originalName, 
                                          String description, Long size) {
        Result<ResourceInfo> result = new Result<>();
        
        try {
            Long id = nextId++;
            ResourceInfo resource = new ResourceInfo(id, title, type, url, fileName, 
                                                   originalName, description, size);
            
            resourceMap.put(id, resource);
            
            log.info("添加资源成功: {} (ID: {})", title, id);
            return result.success(resource);
            
        } catch (Exception e) {
            log.error("添加资源失败", e);
            return result.error("500", "添加资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有资源
     */
    public Result<List<ResourceInfo>> getAllResources() {
        Result<List<ResourceInfo>> result = new Result<>();
        
        try {
            List<ResourceInfo> resources = new ArrayList<>(resourceMap.values());
            resources.sort((a, b) -> b.getId().compareTo(a.getId())); // 按ID倒序
            
            return result.success(resources);
            
        } catch (Exception e) {
            log.error("获取资源列表失败", e);
            return result.error("500", "获取资源列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取资源
     */
    public Result<ResourceInfo> getResourceById(Long id) {
        Result<ResourceInfo> result = new Result<>();
        
        ResourceInfo resource = resourceMap.get(id);
        if (resource != null) {
            return result.success(resource);
        } else {
            return result.error("404", "资源不存在");
        }
    }

    /**
     * 删除资源
     */
    public Result<String> deleteResource(Long id) {
        Result<String> result = new Result<>();
        
        try {
            ResourceInfo resource = resourceMap.get(id);
            if (resource == null) {
                return result.error("404", "资源不存在");
            }

            // 删除文件
            if (resource.getFileName() != null) {
                Path filePath = Paths.get(uploadPath, resource.getFileName());
                try {
                    Files.deleteIfExists(filePath);
                    log.info("删除文件: {}", filePath);
                } catch (IOException e) {
                    log.warn("删除文件失败: {}", e.getMessage());
                }
            }

            // 从内存中删除
            resourceMap.remove(id);
            
            log.info("删除资源成功: {} (ID: {})", resource.getTitle(), id);
            return result.success("删除成功");
            
        } catch (Exception e) {
            log.error("删除资源失败", e);
            return result.error("500", "删除资源失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String fileName) {
        if (fileName == null) return false;
        Path filePath = Paths.get(uploadPath, fileName);
        return Files.exists(filePath);
    }

    /**
     * 获取文件路径
     */
    public Path getFilePath(String fileName) {
        return Paths.get(uploadPath, fileName);
    }
}
