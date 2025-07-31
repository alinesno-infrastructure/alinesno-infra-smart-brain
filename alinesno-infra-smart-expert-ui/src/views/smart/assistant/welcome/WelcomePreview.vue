<template>
  <div class="welcome-preview-container">
    <div class="welcome-card">
      <!-- 头部区域 -->
      <div class="card-header">
        <div class="icon-circle">
          <img :src="imagePath(currentRole.roleAvatar)" class="role-avatar" />
        </div>
        <div class="header-text">
          <h1>你好，我是 {{ currentRole.roleName }} </h1>
          <p class="description">专业的数据分析与可视化智能助手</p>
        </div>
      </div>

      <!-- 核心能力区域 -->
      <div class="feature-section">
        <h3>核心能力</h3>
        <div class="feature-grid">
          <div v-for="(feature, index) in features" :key="index" class="feature-item">
            <div class="feature-icon">
              <i :class="feature.icon"></i>
            </div>
            <div class="feature-text">
              <h4>{{ feature.title }}</h4>
              <p>{{ feature.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 使用示例区域 -->
      <div class="example-section">
        <h3>使用示例</h3>
        <ul class="example-list">
          <li v-for="(example, index) in examples" :key="index" class="example-item">
            <div class="example-image-wrapper">
              <img v-if="example.image" :src="example.image" alt="示例图片">
              <div v-else class="image-placeholder">
                <i class="el-icon-picture-outline"></i>
              </div>
            </div>
            <div class="example-label">{{ example.label }}</div>
          </li>
        </ul>
      </div>

      <!-- 提示区域 -->
      <div class="tip-section">
        <div class="tip-icon">
          <i class="fas fa-lightbulb"></i>
        </div>
        <div class="tip-content">
          <p>{{ tips[0] || '提示内容将显示在这里' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  currentRole: {
    type: Object,
    default: () => ({
      avatar: '',
      name: ''
    })
  },
  features: {
    type: Array,
    default: () => [
      { icon: 'fas fa-database', title: '数据智能处理', description: '支持Excel/CSV等多种格式，自动清洗与预处理' },
      { icon: 'fas fa-chart-bar', title: '可视化分析', description: '自动生成最佳图表，直观呈现数据洞察' }
    ]
  },
  examples: {
    type: Array,
    default: () => [
      { image: '', label: '分析过去6个月的销售额趋势' },
      { image: '', label: '客户群体细分分析' }
    ]
  },
  tips: {
    type: Array,
    default: () => ['提示：使用自然语言描述你的分析需求']
  }
})
</script>

<style lang="scss" scoped>
.welcome-preview-container {
  padding: 10px;
  background-color: #fff;
}

.welcome-card {
  max-width: 100%;
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;

  .icon-circle {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, #3a7bd5, #00d2ff);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .role-avatar {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
    }

    i {
      color: white;
      font-size: 28px;
    }
  }

  .header-text {
    h1 {
      font-size: 24px;
      font-weight: 600;
      margin: 0;
      color: #2c3e50;
    }

    .description {
      color: #7f8c8d;
      margin: 8px 0 0;
      font-size: 14px;
      line-height: 1.4;
    }
  }
}

.feature-section {
  margin-bottom: 20px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #eee;
  }

  .feature-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .feature-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 12px;
      border-radius: 8px;
      background-color: #f8fafc;
      border: 1px solid #e2e8f0;

      .feature-icon {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        background-color: #e3f2fd;
        color: #1976d2;

        i {
          font-size: 16px;
        }
      }

      .feature-text {
        h4 {
          font-weight: 500;
          margin: 0 0 6px 0;
          color: #2c3e50;
          font-size: 15px;
        }

        p {
          font-size: 13px;
          color: #7f8c8d;
          margin: 0;
          line-height: 1.4;
        }
      }
    }
  }
}

.example-section {
  margin-bottom: 20px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #eee;
  }

  .example-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;

    .example-item {
      background: #f8fafc;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #e2e8f0;

      .example-image-wrapper {
        width: 100%;
        height: 100px;
        background-color: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        border-bottom: 1px solid #e2e8f0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .image-placeholder {
          color: #c0c4cc;
          font-size: 24px;
        }
      }

      .example-label {
        font-size: 13px;
        padding: 12px;
        color: #2c3e50;
        line-height: 1.4;
      }
    }
  }
}

.tip-section {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  background-color: #f0f7ff;
  border-radius: 8px;
  margin-top: 20px;
  border-left: 4px solid #3a7bd5;

  .tip-icon {
    color: #3a7bd5;
    font-size: 18px;
    margin-top: 2px;
  }

  .tip-content {
    p {
      margin: 0;
      font-size: 14px;
      color: #2c3e50;
      line-height: 1.5;
    }
  }
}
</style>