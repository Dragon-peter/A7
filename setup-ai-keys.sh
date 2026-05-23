#!/bin/bash

echo "🚀 智能教育平台 AI 服务配置助手"
echo "=================================="
echo ""

# 检查是否已设置环境变量
echo "📋 检查当前环境变量配置..."
echo ""

if [ -z "$DASHSCOPE_API_KEY" ]; then
    echo "❌ DASHSCOPE_API_KEY 未设置"
else
    echo "✅ DASHSCOPE_API_KEY 已设置: ${DASHSCOPE_API_KEY:0:10}..."
fi

if [ -z "$OPENAI_API_KEY" ]; then
    echo "❌ OPENAI_API_KEY 未设置"
else
    echo "✅ OPENAI_API_KEY 已设置: ${OPENAI_API_KEY:0:10}..."
fi

echo ""
echo "📝 配置说明："
echo "1. DashScope (阿里云通义千问) - 推荐使用，国内访问稳定"
echo "   获取地址: https://dashscope.console.aliyun.com/"
echo "   免费额度: 每月100万tokens"
echo ""
echo "2. OpenAI GPT - 功能强大，需要海外网络"
echo "   获取地址: https://platform.openai.com/api-keys"
echo "   需要付费使用"
echo ""

# 询问用户是否要设置API密钥
read -p "是否要设置API密钥？(y/n): " setup_keys

if [ "$setup_keys" = "y" ] || [ "$setup_keys" = "Y" ]; then
    echo ""
    echo "🔧 设置API密钥..."
    
    # 设置DashScope API密钥
    read -p "请输入DashScope API密钥 (留空跳过): " dashscope_key
    if [ ! -z "$dashscope_key" ]; then
        export DASHSCOPE_API_KEY="$dashscope_key"
        echo "✅ DASHSCOPE_API_KEY 已设置"
    fi
    
    # 设置OpenAI API密钥
    read -p "请输入OpenAI API密钥 (留空跳过): " openai_key
    if [ ! -z "$openai_key" ]; then
        export OPENAI_API_KEY="$openai_key"
        echo "✅ OPENAI_API_KEY 已设置"
    fi
    
    echo ""
    echo "💡 提示：当前设置只在本次会话有效"
    echo "要永久保存，请将以下命令添加到 ~/.bashrc 或 ~/.zshrc："
    echo ""
    if [ ! -z "$dashscope_key" ]; then
        echo "export DASHSCOPE_API_KEY=\"$dashscope_key\""
    fi
    if [ ! -z "$openai_key" ]; then
        echo "export OPENAI_API_KEY=\"$openai_key\""
    fi
fi

echo ""
echo "🚀 启动应用..."
echo "=================================="

# 启动Spring Boot应用
mvn spring-boot:run
