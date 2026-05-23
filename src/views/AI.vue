<script setup>
import { nextTick, ref, onMounted } from 'vue';
import { User, Cpu } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 初始消息
const messages = ref([
  { role: 'ai', content: '您好！我是AI助手，有什么可以帮您？', time: formatTime() }
]);
const inputText = ref('');
const loading = ref(false);
const userId = ref(null);

// 获取用户ID
onMounted(() => {
  // 首先尝试获取存储的userId
  userId.value = localStorage.getItem('userId');

  // 如果没有userId，但有userName，则生成一个默认的userId
  if (!userId.value) {
    const userName = localStorage.getItem('userName');
    if (userName) {
      // 为不同用户生成不同的ID
      if (userName === 'admin') {
        userId.value = '1';
      } else if (userName === 'teacher') {
        userId.value = '2';
      } else {
        // 为其他用户生成基于用户名的简单ID
        userId.value = String(userName.length + Date.now() % 1000);
      }
      // 保存生成的userId以便下次使用
      localStorage.setItem('userId', userId.value);
      console.log('为用户生成userId:', userId.value);
    } else {
      console.warn('未找到用户信息，AI功能可能无法正常工作');
    }
  }
});

// 格式化时间
function formatTime(date = new Date()) {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// 创建SSE请求的简化版本
const createSSERequest = async (message, userId, onMessage, onError, onComplete) => {
  const token = localStorage.getItem('token');
  const tokenType = localStorage.getItem('tokenType') || 'Bearer';

  const params = new URLSearchParams({
    message: message,
    userId: userId
  });

  // 将token添加到查询参数中（用于SSE认证）
  if (token) {
    params.append('token', token);
  }

  const url = `http://localhost:8080/api/ai/stream?${params}`;

  try {
    const headers = {
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache',
    };

    // 只有当token存在且有效时才添加Authorization头
    if (token && token !== 'null' && token !== 'undefined') {
      headers['Authorization'] = `${tokenType} ${token}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: headers,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();

    let buffer = '';

    const readStream = async () => {
      try {
        while (true) {
          const { done, value } = await reader.read();

          if (done) {
            if (onComplete) onComplete();
            break;
          }

          buffer += decoder.decode(value, { stream: true });

          // 处理SSE数据格式
          const lines = buffer.split('\n');
          buffer = lines.pop() || '';

          for (const line of lines) {
            console.log('处理SSE行:', line);

            // 处理标准SSE格式：data: {...}
            if (line.startsWith('data: ')) {
              const data = line.slice(6).trim();
              if (data && onMessage) {
                console.log('提取的数据:', data);
                onMessage(data);
              }
            }
            // 处理可能的双重前缀：data:data: {...}
            else if (line.startsWith('data:data: ')) {
              const data = line.slice(11).trim();
              if (data && onMessage) {
                console.log('提取的双重前缀数据:', data);
                onMessage(data);
              }
            }
          }
        }
      } catch (error) {
        console.error('读取流数据错误:', error);
        if (onError) onError(error);
      }
    };

    readStream();

    return () => {
      reader.cancel();
      if (onComplete) onComplete();
    };

  } catch (error) {
    console.error('创建SSE请求失败:', error);
    if (onError) onError(error);
    return () => {};
  }
};



const handleSend = async () => {
  const content = inputText.value.trim();
  if (!content) return;

  if (!userId.value) {
    ElMessage.error('用户未登录，无法使用AI功能');
    return;
  }

  // 清空输入框
  inputText.value = '';

  // 添加用户消息
  const userMsg = {
    role: 'user',
    content: content,
    time: formatTime()
  };
  messages.value.push(userMsg);

  // 创建AI消息占位符
  const aiMsg = {
    role: 'ai',
    content: '',
    time: formatTime(),
    isStreaming: true
  };
  messages.value.push(aiMsg);

  try {
    loading.value = true;

    // 调用真实的AI API
    const closeSSE = await createSSERequest(
      content,
      userId.value,
      (data) => {
        // 处理流式响应数据
        try {
          console.log('收到AI数据:', data);

          // 跳过空数据
          if (!data || data.trim() === '') {
            console.log('跳过空数据');
            return;
          }

          // 尝试解析JSON响应
          if (data.startsWith('{')) {
            const jsonData = JSON.parse(data);
            console.log('解析的JSON数据:', jsonData);

            // 新的统一格式：{type, content, timestamp, metadata}
            if (jsonData.type === 'text' && jsonData.content) {
              console.log('添加文本内容:', jsonData.content);
              // 直接添加文本内容
              aiMsg.content += jsonData.content;
              scrollToBottom();
            }
            // 处理元数据（不显示，但记录日志）
            else if (jsonData.metadata) {
              console.log('收到元数据:', jsonData.metadata);
              // 不添加到消息内容中，只记录日志
            }
            // 兼容旧格式：DashScope格式
            else if (jsonData.output && jsonData.output.text) {
              console.log('DashScope格式文本:', jsonData.output.text);
              aiMsg.content += jsonData.output.text;
              scrollToBottom();
            }
            // 兼容旧格式：OpenAI格式
            else if (jsonData.choices && jsonData.choices[0] && jsonData.choices[0].delta && jsonData.choices[0].delta.content) {
              console.log('OpenAI格式文本:', jsonData.choices[0].delta.content);
              aiMsg.content += jsonData.choices[0].delta.content;
              scrollToBottom();
            }
            // 处理完成标记
            else if (jsonData.type === 'done') {
              console.log('AI回复完成:', jsonData.metadata);
            }
            // 处理错误
            else if (jsonData.type === 'error') {
              console.error('AI回复错误:', jsonData.content);
              aiMsg.content += `\n[错误: ${jsonData.content}]`;
            }
            else {
              console.log('未处理的JSON数据格式:', jsonData);
            }
          } else {
            // 直接添加文本内容
            console.log('直接添加文本:', data);
            aiMsg.content += data;
            scrollToBottom();
          }
          scrollToBottom();
        } catch (e) {
          console.error('JSON解析错误:', e, '原始数据:', data);
          // 如果不是JSON，直接添加文本
          aiMsg.content += data;
          scrollToBottom();
        }
      },
      (error) => {
        console.error('AI请求错误:', error);
        aiMsg.content = '抱歉，AI服务暂时不可用，请稍后再试。';
        aiMsg.isStreaming = false;

        loading.value = false;
        scrollToBottom();
      },
      () => {
        // 完成回调
        aiMsg.isStreaming = false;
        loading.value = false;
        scrollToBottom();
      }
    );

    // 立即触发滚动
    scrollToBottom();

  } catch (error) {
    console.error('发送消息失败:', error);
    aiMsg.content = '抱歉，我遇到了一些问题，请稍后再试。';
    aiMsg.isStreaming = false;

    loading.value = false;
    scrollToBottom();
  }
};





// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    const container = document.querySelector('.message-container');
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  });
}
</script>

<template>
<div class="ai-container">
    <!-- 消息容器 -->
    <el-scrollbar class="message-container" height="60vh">
      <div 
        v-for="(msg, index) in messages"
        :key="index"
        class="message-item"
        :class="{ 'user-message': msg.role === 'user', 'ai-message': msg.role === 'ai' }"
      >
        <el-icon v-if="msg.role === 'user'" class="avatar"><User /></el-icon>
        <el-icon v-if="msg.role === 'ai'" class="avatar"><Cpu /></el-icon>
        <div class="content-box">
          <div class="content">{{ msg.content }}</div>
          <div class="timestamp">{{ msg.time }}</div>
        </div>
      </div>
      <el-skeleton v-if="loading" :rows="3" animated />
    </el-scrollbar>

    <!-- 输入区域 -->
    <div class="input-area">
      <el-input
        v-model="inputText"
        placeholder="请输入您的问题"
        :disabled="loading"
        @keyup.enter="handleSend"
      >
        <template #append>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSend"
          >
            发送
          </el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<style scoped>
.ai-container {
  padding: 20px;
  max-width: 1250px;
  max-height: 800px;
  height: calc(100vh - 180px);
  margin: 0 auto;
  background-color: #fff;
  border-radius: 12px;
}

.message-container {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  height: calc(100% - 80px);
}

.message-item {
  display: flex;
  margin: 15px 0;
}

.avatar {
  font-size: 24px;
  margin-right: 12px;
  color: #409EFF;
}

.content-box {
  max-width: 85%;
}

.user-message {
  flex-direction: row-reverse;
  .content-box {
    text-align: right;
  }
  .content {
    background: #409EFF;
    color: white;
  }
}

.ai-message .content {
  background: #f5f7fa;
}

.content {
  padding: 10px 15px;
  border-radius: 6px;
  line-height: 1.6;
}

.timestamp {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}



.input-area {
  padding: 0 20px;
}
</style>