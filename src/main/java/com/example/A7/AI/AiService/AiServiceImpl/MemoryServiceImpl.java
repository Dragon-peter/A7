package com.example.A7.AI.AiService.AiServiceImpl;

import com.example.A7.AI.AiService.MemoryService;
import com.example.A7.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoryServiceImpl implements MemoryService {
    private final JdbcTemplate jdbcTemplate;
    private static final int MAX_HISTORY_PER_CONVERSATION = 30;

    public MemoryServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void validateUserActive(Long userId) {
        String sql = "SELECT status FROM user WHERE id = ?";
        try {
            Integer status = jdbcTemplate.queryForObject(sql, Integer.class, userId);
            if (status == null || status ==0 ) {
                throw new IllegalStateException("用户不存在或已被禁用");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException("用户不存在");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Map<String, String>> getConversationHistory(Long userId) {
        // 移除用户验证，允许任何登录用户使用AI功能
        // validateUserActive(userId);

        String sql = "SELECT role, content, created_at " +
                "FROM conversation_history " +
                "WHERE user_id = ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ?";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> message = new LinkedHashMap<>();
                message.put("role", rs.getString("role"));
                message.put("content", rs.getString("content"));
                message.put("time", rs.getTimestamp("created_at").toString());
                return message;
            }, userId, MAX_HISTORY_PER_CONVERSATION);
        } catch (Exception e) {
            // 如果查询失败（比如用户ID不存在对话历史），返回空列表
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void saveMessage(Long userId, String role, String content) {
        // 移除用户验证，允许任何登录用户保存对话历史
        // validateUserActive(userId);

        try {
            String sql = "INSERT INTO conversation_history " +
                    "(user_id, role, content, created_at) " +
                    "VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(sql, userId, role, content, Timestamp.from(Instant.now()));

            // 清理旧消息(保留最近的MAX_HISTORY条)
            String cleanupSql = "DELETE FROM conversation_history " +
                    "WHERE user_id = ? AND id NOT IN (" +
                    "  SELECT id FROM (" +
                    "    SELECT id FROM conversation_history " +
                    "    WHERE user_id = ? " +
                    "    ORDER BY created_at DESC " +
                    "    LIMIT ?" +
                    "  ) AS tmp" +
                    ")";

            jdbcTemplate.update(cleanupSql, userId, userId, MAX_HISTORY_PER_CONVERSATION);
        } catch (Exception e) {
            // 如果保存失败，记录日志但不抛出异常，确保AI功能仍然可用
            System.err.println("保存对话历史失败，用户ID: " + userId + ", 错误: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void clearHistory(Long userId) {
        jdbcTemplate.update(
                "DELETE FROM conversation_history WHERE user_id = ?",
                userId
        );
    }
}
