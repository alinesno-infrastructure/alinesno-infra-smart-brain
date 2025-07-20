<template>
  <div class="ai-rewrite-container">
    <h3>AI重写</h3>
    
    <div class="rewrite-options">
      
      <!-- 文书类型 -->
      <div class="option-group">
        <h3 class="option-title">文书类型</h3>
        <div class="option-items">
          <button
            v-for="type in rewriteOptions.docTypes"
            :key="type.value"
            class="option-button"
            :class="{ active: selectedDocType === type.value }"
            @click="selectedDocType = type.value"
          >
            {{ type.label }}
          </button>
        </div>
      </div>

      
      <!-- 应用场景 -->
      <div class="option-group">
        <h3 class="option-title">应用场景</h3>
        <div class="option-items">
          <button
            v-for="scene in rewriteOptions.scenes"
            :key="scene.value"
            class="option-button"
            :class="{ active: selectedScene === scene.value }"
            @click="selectedScene = scene.value"
          >
            {{ scene.label }}
          </button>
        </div>
      </div>

      <!-- 风格选择 -->
      <div class="option-group">
        <h3 class="option-title">文章风格</h3>
        <div class="option-items">
          <button
            v-for="style in rewriteOptions.styles"
            :key="style.value"
            class="option-button"
            :class="{ active: selectedStyle === style.value }"
            @click="selectedStyle = style.value"
          >
            {{ style.label }}
          </button>
        </div>
      </div>
      
      <!-- 字数选择 -->
      <div class="option-group">
        <h3 class="option-title">字数要求</h3>
        <div class="option-items">
          <button
            v-for="count in rewriteOptions.wordCounts"
            :key="count.value"
            class="option-button"
            :class="{ active: selectedWordCount === count.value }"
            @click="selectedWordCount = count.value"
          >
            {{ count.label }}
          </button>
        </div>
      </div>

    </div>
    
    <div class="action-section">
      <el-button
        size="large"
        class="rewrite-button"
        @click="handleRewrite"
        :disabled="!isSelectionValid || isRewriting"
      >
        <span v-if="!isRewriting">开始重写</span>
        <span v-else>重写中...</span>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 重写选项配置
const rewriteOptions = {
  styles: [
    { value: 'formal', label: '正式严谨' },
    { value: 'casual', label: '轻松随意' },
    { value: 'academic', label: '学术专业' },
    { value: 'creative', label: '创意新颖' },
    { value: 'persuasive', label: '说服性强' },
    { value: 'descriptive', label: '描述生动' }
  ],
  wordCounts: [
    { value: 'short', label: '简短 (300字以内)' },
    { value: 'medium', label: '中等 (300-800字)' },
    { value: 'long', label: '长篇 (800字以上)' },
    { value: 'exact', label: '精确字数' }
  ],
  scenes: [
    { value: 'marketing', label: '营销推广' },
    { value: 'education', label: '教育培训' },
    { value: 'business', label: '商业文书' },
    { value: 'technical', label: '技术文档' },
    { value: 'news', label: '新闻媒体' },
    { value: 'entertainment', label: '娱乐内容' }
  ],
  docTypes: [
    { value: 'article', label: '文章' },
    { value: 'report', label: '报告' },
    { value: 'proposal', label: '提案' },
    { value: 'email', label: '邮件' },
    { value: 'ad', label: '广告文案' },
    { value: 'summary', label: '摘要' },
    { value: 'story', label: '故事' }
  ]
}

// 选中的选项（单选）
const selectedStyle = ref('')
const selectedWordCount = ref('')
const selectedScene = ref('')
const selectedDocType = ref('')

// 重写状态
const isRewriting = ref(false)

// 验证是否已选择必要选项
const isSelectionValid = computed(() => {
  return selectedStyle.value && selectedWordCount.value
})

// 处理重写请求
const handleRewrite = () => {
  isRewriting.value = true
  setTimeout(() => {
    isRewriting.value = false
    alert('重写完成！')
  }, 1500)
}
</script>

<style lang="scss" scoped>
.ai-rewrite-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  
  h3 {
    text-align: left;
    margin-bottom: 30px;
    color: #333;
    font-size: 16px;
    font-weight: 600;
  }
  
  .rewrite-options {
    display: flex;
    flex-direction: column;
    gap: 30px;
    margin-bottom: 40px;
    
    .option-group {
      .option-title {
        margin: 0 0 15px 0;
        font-size: 16px;
        color: #444;
        font-weight: 500;
      }
      
      .option-items {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        
        .option-button {
          padding: 7px 16px;
          border: 1px solid #dcdfe6;
          border-radius: 4px;
          background-color: #fff;
          color: #606266;
          cursor: pointer;
          transition: all 0.3s;
          font-size: 14px;
          
          &:hover {
            color: #409eff;
            border-color: #c6e2ff;
            background-color: #ecf5ff;
          }
          
          &.active {
            color: #fff;
            background-color: #409eff;
            border-color: #409eff;
          }
        }
      }
    }
  }
  
  .action-section {
    display: flex;
    justify-content: center;
    
    .rewrite-button {
      width: 200px;
      height: 40px;
      font-size: 16px;
      color: #fff;
      background-color: #409eff;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background-color: #66b1ff;
      }
      
      &:disabled {
        background-color: #a0cfff;
        cursor: not-allowed;
      }
    }
  }
}

@media (max-width: 768px) {
  .ai-rewrite-container {
    padding: 15px;
    
    .rewrite-options {
      .option-group {
        .option-items {
          gap: 8px;
          
          .option-button {
            padding: 8px 12px;
            font-size: 13px;
            flex: 1 0 calc(50% - 8px);
          }
        }
      }
    }
  }
}
</style>