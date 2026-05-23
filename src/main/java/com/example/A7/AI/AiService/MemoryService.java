package com.example.A7.AI.AiService;

import com.example.A7.AI.AiModel.ConversationMemory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface MemoryService {
    // 获取对话历史
    List<Map<String, String>> getConversationHistory(Long userId);

    void saveMessage(Long userId, String role, String content);

    void clearHistory(Long userId);
}
