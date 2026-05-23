@echo off
chcp 65001 >nul
echo 🚀 智能教育平台 AI 服务配置助手
echo ==================================
echo.

REM 检查是否已设置环境变量
echo 📋 检查当前环境变量配置...
echo.

if "%DASHSCOPE_API_KEY%"=="" (
    echo ❌ DASHSCOPE_API_KEY 未设置
) else (
    echo ✅ DASHSCOPE_API_KEY 已设置
)

if "%OPENAI_API_KEY%"=="" (
    echo ❌ OPENAI_API_KEY 未设置
) else (
    echo ✅ OPENAI_API_KEY 已设置
)

echo.
echo 📝 配置说明：
echo 1. DashScope (阿里云通义千问) - 推荐使用，国内访问稳定
echo    获取地址: https://dashscope.console.aliyun.com/
echo    免费额度: 每月100万tokens
echo.
echo 2. OpenAI GPT - 功能强大，需要海外网络
echo    获取地址: https://platform.openai.com/api-keys
echo    需要付费使用
echo.

REM 询问用户是否要设置API密钥
set /p setup_keys="是否要设置API密钥？(y/n): "

if /i "%setup_keys%"=="y" (
    echo.
    echo 🔧 设置API密钥...
    
    REM 设置DashScope API密钥
    set /p dashscope_key="请输入DashScope API密钥 (留空跳过): "
    if not "%dashscope_key%"=="" (
        set DASHSCOPE_API_KEY=%dashscope_key%
        echo ✅ DASHSCOPE_API_KEY 已设置
    )
    
    REM 设置OpenAI API密钥
    set /p openai_key="请输入OpenAI API密钥 (留空跳过): "
    if not "%openai_key%"=="" (
        set OPENAI_API_KEY=%openai_key%
        echo ✅ OPENAI_API_KEY 已设置
    )
    
    echo.
    echo 💡 提示：当前设置只在本次会话有效
    echo 要永久保存，请在系统环境变量中设置：
    echo - DASHSCOPE_API_KEY
    echo - OPENAI_API_KEY
)

echo.
echo 🚀 启动应用...
echo ==================================

REM 启动Spring Boot应用
mvn spring-boot:run

pause
