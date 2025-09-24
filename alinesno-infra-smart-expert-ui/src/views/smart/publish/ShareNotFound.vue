<template>
  <div class="share-channel-not-exist">
    <div class="error-container">
      <div class="error-header">
        <div class="error-icon">
          <i class="fa-solid fa-exclamation-circle"></i>
        </div>
        <h1>分享链接无效或已过期</h1>
      </div>
      
      <div class="error-content">
        <p>很抱歉，您访问的分享链接可能已过期、被撤销或不存在。</p>
        
        <div class="error-suggestions">
          <h3>您可以尝试以下操作：</h3>
          <ul>
            <li>检查链接是否正确无误</li>
            <li>联系分享者获取最新链接</li>
            <li>返回平台首页重新开始</li>
          </ul>
        </div>
        
        <!-- 
        <div class="error-actions">
          <el-button type="primary" @click="onGoToHome">返回首页</el-button>
          <el-button type="default" @click="onCopyLink">复制当前链接</el-button>
        </div> 
        -->
      </div>
      
      <div class="error-footer">
        <p>如有疑问，请联系系统管理员支持</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus';

// 定义组件的事件回调
const emit = defineEmits(['goToHome']);

// 前往首页
const onGoToHome = () => {
  emit('goToHome');
};

// 复制当前链接
const onCopyLink = () => {
  navigator.clipboard.writeText(window.location.href)
    .then(() => {
      ElMessage.success('链接已复制到剪贴板');
    })
    .catch(err => {
      console.error('无法复制链接: ', err);
      ElMessage.error('复制失败，请手动复制');
    });
};
</script>

<style lang="scss" scoped>
// 分享渠道不存在界面样式
.share-channel-not-exist {
  width: 100%;
  min-height: 100vh;
  background-color: #f8fafc;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
}

.error-container {
  width: 100%;
  max-width: 600px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.02);
  overflow: hidden;
  transition: transform 0.3s ease;

  // &:hover {
  //   transform: translateY(-5px);
  // }
}

.error-header {
  background: #1d75b0 ; 
  color: white;
  padding: 30px 20px;
  text-align: center;

  .error-icon {
    font-size: 50px;
    margin-bottom: 15px;
    animation: pulse 2s infinite;
  }

  h1 {
    font-size: 24px;
    font-weight: 600;
    margin: 0;
  }
}

.error-content {
  padding: 30px 20px;

  p {
    color: #64748b;
    font-size: 14px;
    text-align: center;
    margin-bottom: 25px;
    line-height: 1.6;
  }

  .error-suggestions {
    background-color: #f8fafc;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 30px;

    h3 {
      color: #334155;
      font-size: 18px;
      margin-top: 0;
      margin-bottom: 15px;
      display: flex;
      align-items: center;

      &::before {
        content: "💡";
        margin-right: 8px;
      }
    }

    ul {
      padding-left: 25px;
      margin: 0;

      li {
        color: #64748b;
        margin-bottom: 10px !important;
        line-height: 1.5;
        font-size: 14px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .error-actions {
    display: flex;
    justify-content: center;
    gap: 15px;
  }
}

.error-footer {
  background-color: #f8fafc;
  padding: 25px 20px;
  text-align: center;
  border-top: 0px solid #e2e8f0;

  p {
    margin: 0;
    color: #94a3b8;
    font-size: 14px;

    a {
      color: #3b82f6;
      text-decoration: none;
      transition: color 0.2s;

      &:hover {
        color: #2563eb;
        text-decoration: underline;
      }
    }
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}
</style>
