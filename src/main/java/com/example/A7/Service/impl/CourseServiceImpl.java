package com.example.A7.Service.impl;

import com.example.A7.DO.CourseDO;
import com.example.A7.DO.ChapterDO;
import com.example.A7.DO.ResourceDO;
import com.example.A7.DO.StudentCourseDO;
import com.example.A7.Mapper.CourseMapper;
import com.example.A7.Mapper.ChapterMapper;
import com.example.A7.Mapper.ResourceMapper;
import com.example.A7.Mapper.StudentCourseMapper;
import com.example.A7.Model.Paging;
import com.example.A7.Model.Result;
import com.example.A7.Service.CourseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private ResourceMapper resourceMapper;
    
    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Override
    public Result<CourseDO> createCourse(CourseDO courseDO) {
        Result<CourseDO> result = new Result<>();
        try {
            // 设置创建时间和更新时间
            courseDO.setCreateTime(LocalDateTime.now());
            courseDO.setUpdateTime(LocalDateTime.now());
            // 默认状态为正常
            if (courseDO.getStatus() == null) {
                courseDO.setStatus(1);
            }
            
            int rows = courseMapper.add(courseDO);
            if (rows > 0) {
                return result.success(courseDO);
            }
            return result.error("400", "创建课程失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<CourseDO> updateCourse(CourseDO courseDO) {
        Result<CourseDO> result = new Result<>();
        try {
            // 更新时间
            courseDO.setUpdateTime(LocalDateTime.now());
            
            int rows = courseMapper.update(courseDO);
            if (rows > 0) {
                return result.success(courseDO);
            }
            return result.error("400", "更新课程失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> deleteCourse(Long id) {
        Result<Boolean> result = new Result<>();
        try {
            // 首先删除课程下的所有学生选课记录
            studentCourseMapper.deleteByCourseId(id);
            // 删除课程下所有的章节和资源
            List<ChapterDO> chapters = chapterMapper.findByCourseId(id);
            for (ChapterDO chapter : chapters) {
                resourceMapper.deleteByChapterId(chapter.getId());
            }
            chapterMapper.deleteByCourseId(id);
            // 最后删除课程
            int rows = courseMapper.delete(id);
            return result.success(rows > 0);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<CourseDO> getCourseById(Long id) {
        Result<CourseDO> result = new Result<>();
        try {
            CourseDO course = courseMapper.findById(id);
            if (course != null) {
                return result.success(course);
            }
            return result.error("404", "课程不存在");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Paging<CourseDO>> findAllCourses(Integer pageNum, Integer pageSize) {
        Result<Paging<CourseDO>> result = new Result<>();
        try {
            // 设置默认值
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            
            Page<CourseDO> page = PageHelper.startPage(pageNum, pageSize)
                    .doSelectPage(() -> courseMapper.findAll());
            
            result.setSuccess(true);
            result.setData(new Paging<>(
                    page.getPageNum(), 
                    page.getPageSize(), 
                    page.getPages(), 
                    page.getTotal(), 
                    page.getResult()));
            return result;
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<CourseDO>> getCoursesByTeacherId(Long teacherId) {
        Result<List<CourseDO>> result = new Result<>();
        try {
            List<CourseDO> courses = courseMapper.findByTeacherId(teacherId);
            return result.success(courses);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<ChapterDO> addChapter(ChapterDO chapterDO) {
        Result<ChapterDO> result = new Result<>();
        try {
            int rows = chapterMapper.add(chapterDO);
            if (rows > 0) {
                return result.success(chapterDO);
            }
            return result.error("400", "添加章节失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<ChapterDO> updateChapter(ChapterDO chapterDO) {
        Result<ChapterDO> result = new Result<>();
        try {
            int rows = chapterMapper.update(chapterDO);
            if (rows > 0) {
                return result.success(chapterDO);
            }
            return result.error("400", "更新章节失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> deleteChapter(Long id) {
        Result<Boolean> result = new Result<>();
        try {
            // 首先删除章节下的所有资源
            resourceMapper.deleteByChapterId(id);
            // 然后删除章节
            int rows = chapterMapper.delete(id);
            return result.success(rows > 0);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<ChapterDO>> getChaptersByCourseId(Long courseId) {
        Result<List<ChapterDO>> result = new Result<>();
        try {
            List<ChapterDO> chapters = chapterMapper.findByCourseId(courseId);
            return result.success(chapters);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<ResourceDO> addResource(ResourceDO resourceDO) {
        Result<ResourceDO> result = new Result<>();
        try {
            resourceDO.setCreateTime(LocalDateTime.now());
            int rows = resourceMapper.add(resourceDO);
            if (rows > 0) {
                return result.success(resourceDO);
            }
            return result.error("400", "添加资源失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<ResourceDO> updateResource(ResourceDO resourceDO) {
        Result<ResourceDO> result = new Result<>();
        try {
            int rows = resourceMapper.update(resourceDO);
            if (rows > 0) {
                return result.success(resourceDO);
            }
            return result.error("400", "更新资源失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteResource(Long id) {
        Result<Boolean> result = new Result<>();
        try {
            int rows = resourceMapper.delete(id);
            return result.success(rows > 0);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<ResourceDO>> getResourcesByChapterId(Long chapterId) {
        Result<List<ResourceDO>> result = new Result<>();
        try {
            List<ResourceDO> resources = resourceMapper.findByChapterId(chapterId);
            return result.success(resources);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<StudentCourseDO> studentEnrollCourse(Long studentId, Long courseId) {
        Result<StudentCourseDO> result = new Result<>();
        try {
            // 检查是否已选课
            StudentCourseDO existRecord = studentCourseMapper.findByStudentIdAndCourseId(studentId, courseId);
            if (existRecord != null) {
                if (existRecord.getStatus() == 0) {
                    // 如果之前退课，更新状态为正常
                    existRecord.setStatus(1);
                    studentCourseMapper.update(existRecord);
                    return result.success(existRecord);
                }
                return result.error("400", "已选修此课程");
            }
            
            // 创建新选课记录
            StudentCourseDO studentCourse = new StudentCourseDO();
            studentCourse.setStudentId(studentId);
            studentCourse.setCourseId(courseId);
            studentCourse.setProgress(0);
            studentCourse.setJoinTime(LocalDateTime.now());
            studentCourse.setStatus(1);
            
            int rows = studentCourseMapper.add(studentCourse);
            if (rows > 0) {
                return result.success(studentCourse);
            }
            return result.error("400", "选课失败");
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> studentDropCourse(Long id) {
        Result<Boolean> result = new Result<>();
        try {
            // 设置状态为退课
            int rows = studentCourseMapper.updateStatus(id, 0);
            return result.success(rows > 0);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<List<StudentCourseDO>> getCoursesByStudentId(Long studentId) {
        Result<List<StudentCourseDO>> result = new Result<>();
        try {
            List<StudentCourseDO> courses = studentCourseMapper.findByStudentId(studentId);
            return result.success(courses);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCourseStatistics(Long courseId) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> statistics = new HashMap<>();
            // 获取选课人数
            int studentCount = studentCourseMapper.countStudentByCourseId(courseId);
            statistics.put("studentCount", studentCount);
            
            // 获取完成课程的人数
            int completedCount = studentCourseMapper.countCompletedByCourseId(courseId);
            statistics.put("completedCount", completedCount);
            
            // 获取章节数
            int chapterCount = chapterMapper.countByCourseId(courseId);
            statistics.put("chapterCount", chapterCount);
            
            // 获取资源数
            int resourceCount = resourceMapper.countByCourseId(courseId);
            statistics.put("resourceCount", resourceCount);
            
            return result.success(statistics);
        } catch (Exception e) {
            return result.error("500", "系统异常：" + e.getMessage());
        }
    }
} 