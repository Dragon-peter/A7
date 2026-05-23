package com.example.A7.AI.AiModel;

import java.util.ArrayList;
import java.util.List;

public class ConversationMemory{
    private final List<String> userMessages = new ArrayList<>();
    private final List<String> assistantMessages = new ArrayList<>();
    private final int maxHistoryLength; // 历史消息最大长度

    public ConversationMemory(int maxHistoryLength) {
        this.maxHistoryLength = maxHistoryLength;
    }

    public void addUserMessage(String message) {
        if (userMessages.size() >= maxHistoryLength) {
            userMessages.remove(0); // 移除最早的消息
        }
        userMessages.add(message);
    }

    public void addAssistantMessage(String message) {
        if (assistantMessages.size() >= maxHistoryLength) {
            assistantMessages.remove(0);
        }
        assistantMessages.add(message);
    }

    public List<String> getRecentHistory() {
        List<String> history = new ArrayList<>();
        int startIdx = Math.max(0, userMessages.size() - maxHistoryLength);
        for (int i = startIdx; i < userMessages.size(); i++) {
            history.add("用户: " + userMessages.get(i));
            if (i < assistantMessages.size()) {
                history.add("助手: " + assistantMessages.get(i));
            }
        }
        return history;
    }
}
