package com.example.A7.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class AiConfig {
    
    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);
    
    @Value("${dashscope.api-key:}")
    private String dashscopeApiKey;
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;
    
    @PostConstruct
    public void validateApiKeys() {
        log.info("正在验证AI服务API密钥配置...");
        
        if (dashscopeApiKey == null || dashscopeApiKey.trim().isEmpty() || dashscopeApiKey.equals("sk-test-key-please-set-real-key")) {
            log.warn("⚠️  DashScope API密钥未配置或使用测试密钥！");
            log.warn("请设置环境变量 DASHSCOPE_API_KEY 或在application.properties中配置 dashscope.api-key");
            log.warn("获取API密钥: https://dashscope.console.aliyun.com/");
        } else {
            log.info("✅ DashScope API密钥已配置");
        }
        
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty() || openaiApiKey.equals("sk-test-key-please-set-real-key")) {
            log.warn("⚠️  OpenAI API密钥未配置或使用测试密钥！");
            log.warn("请设置环境变量 OPENAI_API_KEY 或在application.properties中配置 openai.api.key");
            log.warn("获取API密钥: https://platform.openai.com/api-keys");
        } else {
            log.info("✅ OpenAI API密钥已配置");
        }
        
        if (isValidApiKey(dashscopeApiKey) || isValidApiKey(openaiApiKey)) {
            log.info("🚀 AI服务已准备就绪！");
        } else {
            log.error("❌ 没有配置有效的AI服务API密钥！");
            log.error("AI功能将无法正常工作，请配置至少一个有效的API密钥");
        }
    }
    
    private boolean isValidApiKey(String apiKey) {
        return apiKey != null && 
               !apiKey.trim().isEmpty() && 
               !apiKey.equals("sk-test-key-please-set-real-key") &&
               apiKey.startsWith("sk-");
    }
    
    public String getDashscopeApiKey() {
        return dashscopeApiKey;
    }
    
    public String getOpenaiApiKey() {
        return openaiApiKey;
    }
    
    public boolean isDashscopeConfigured() {
        return isValidApiKey(dashscopeApiKey);
    }
    
    public boolean isOpenaiConfigured() {
        return isValidApiKey(openaiApiKey);
    }
}
