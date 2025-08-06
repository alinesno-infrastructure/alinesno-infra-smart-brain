<template>

<!-- 添加遮罩层 -->
  <div class="overlay-mask" v-show="visible"></div>

  <div class="ai-generating-container" v-show="visible">
    <div class="ai-generation-status">
      <i class="fa-solid fa-robot ai-robot-icon"></i>
    </div>
    <div class="ai-generating-content">
      <div class="ai-generating-text"> 
        <span>{{ currentText }}</span>
        <span> 
          <el-button v-if="props.closeEnable" type="primary"  icon="Position" @click="backTo">返回管理界面</el-button>
          <el-button v-else type="danger" icon="Warning">请勿关闭界面</el-button>
        </span>
      </div>
      <el-progress 
        :percentage="100" 
        status="primary" 
        :indeterminate="true" 
        :duration="1"
        :stroke-width="15"
        :show-text="false"
        striped 
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElProgress, ElButton } from 'element-plus'

const route = useRoute();
const router = useRouter();

const props = defineProps({
  // 返回路径
  backToPath: {
    type: String,
    required: true
  },
  // 是否允许关闭
  closeEnable: {
    type: Boolean , 
    default: true
  },
  // 路由参数
  routeParams: {
    type: Object,
    default: () => ({})
  }
})

const visible = ref(false)
const currentText = ref('AI 正在生成内容，请稍候...')

// 打开加载状态
const loading = () => {
  visible.value = true
}

// 返回管理界面
const backTo = () => {
  router.push({
    path: props.backToPath,
    query: props.routeParams
  })
  close()
}

// 关闭组件
const close = () => {
  visible.value = false
}

// 设置显示文本
const setText = (text) => {
  currentText.value = text || 'AI 正在生成内容，请稍候...'
}

// 暴露方法给父组件
defineExpose({
  loading,
  close,
  setText
})
</script>

<style lang="scss" scoped> 
.overlay-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0);
  z-index: 3999; 
}

/* 修改容器z-index使其在遮罩之上 */
.ai-generating-container {
  position: fixed;
  background-color: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color);
  box-shadow: 0 -2px 12px 6px rgba(0, 0, 0, 0.1);
  z-index: 4000;
  bottom: 50px;
  width: 80%;
  margin: auto;
  max-width: 1024px;
  border-radius: 10px;
  padding: 16px;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  flex-direction: row;
  flex-wrap: wrap;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 -2px 16px 8px rgba(0, 0, 0, 0.15);
  }
}

.ai-generating-content {
  max-width: 1200px;
  width: calc(100% - 100px);
  margin: 0 auto;
}

.ai-generation-status {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background-color: rgba(var(--el-color-primary-rgb), 0.1);
    animation: pulse-ring 2s infinite ease-out;
  }
}

.ai-robot-icon {
  font-size: 2rem;
  color: var(--el-color-primary);
  position: relative;
  z-index: 1;
  animation: 
    pulse 1.5s infinite ease-in-out, 
    float 3s infinite ease-in-out,
    tilt 5s infinite ease-in-out;
}

.ai-generating-text {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 20px;
  font-weight: bold;
  color: var(--el-text-color-regular);
  justify-content: space-between;
  align-items: center;
  width: 100%;

  span:first-child {
    flex-grow: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
  100% { transform: translateY(0px); }
}

@keyframes tilt {
  0%, 100% { transform: rotate(-5deg); }
  50% { transform: rotate(5deg); }
}

@keyframes pulse-ring {
  0% {
    transform: scale(0.8);
    opacity: 0.8;
  }
  80%, 100% {
    transform: scale(1.2);
    opacity: 0;
  }
}
</style>