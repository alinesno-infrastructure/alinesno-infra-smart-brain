<template>
  <el-popover placement="top" :width="350" v-model:visible="popoverVisible">
    <div>
      <p class="title">
        {{ props.feel ? "你觉得什么让你满意？" : "你觉得什么让你不满意？"}}
      </p>

      <div class="reason-list">
        <!-- 渲染理由列表 -->
        <el-checkbox-group v-model="selectedReasons" size="small" class="checkbox-group">
          <el-checkbox 
            v-for="(reason, index) in reasonList" 
            :key="index" 
            :label="reason"   
            class="reason-item"
          >
            {{ reason }}
          </el-checkbox>
        </el-checkbox-group>
        
        <!-- 其他理由输入框 -->
        <el-checkbox 
          v-model="showOtherInput" 
          label="其他"
          size="small"
          class="reason-item"
        >
          其他
        </el-checkbox>
        
        <el-input
          v-if="showOtherInput"
          v-model="otherReason"
          maxlength="200"
          show-word-limit
          placeholder="请输入其他理由"
          class="other-input" 
        ></el-input>
      </div>

    </div>
    <div style="text-align: right; margin: 0"> 
      <el-button  
        type="primary" 
        text bg
        @click="handleSubmit"
        :disabled="!canSubmit"
      >
        提交
      </el-button>
    </div>
    <template #reference>  
      <el-button  
        link 
        size="small"
      >
        <i :class="props.feel ? 'fa-regular fa-thumbs-up' : 'fa-regular fa-thumbs-down'"></i>
      </el-button> 
    </template>
  </el-popover>
</template>

<script setup>
import { ref, computed, defineEmits } from 'vue';

// 定义事件
const emit = defineEmits(['submit']);

// 状态管理
const popoverVisible = ref(false);
const selectedReasons = ref([]); // 存储选中的理由
const showOtherInput = ref(false); // 控制输入框显示
const otherReason = ref(""); // 存储其它理由
const visible = ref(false)

const props = defineProps({
  message: {
    type: Object,
    default: () => ({})
  },
  feel: {
    type: Boolean,
    default: true
  }
});

const canSubmit = computed(() => {
  return selectedReasons.value.length > 0 || (showOtherInput.value && otherReason.value.trim());
});

// 不满意的理由列表
const noLikeMessageReason = ref([
  "有害/不安全",
  "信息虚假",
  "没有帮助",
  "隐私相关"
]);

// 满意的理由列表
const likeMessageReason = ref([
  "内容准确",
  "易于理解",
  "内容完善"
]);

// 根据feel属性动态选择显示的理由列表
const reasonList = computed(() => {
  return props.feel ? likeMessageReason.value : noLikeMessageReason.value;
});

// 处理提交
const handleSubmit = () => {
  // 收集所有选中的理由
  let allReasons = [...selectedReasons.value];
  
  // 如果填写了其他理由，添加到列表中
  if (showOtherInput.value && otherReason.value.trim()) {
    allReasons.push(`其他: ${otherReason.value.trim()}`);
  }
  
  // 触发提交事件，将结果传递给父组件
  emit('submit', {
    feel: props.feel,
    reasons: allReasons,
    messageId: props.message.messageId,
    timestamp: new Date().toISOString()
  });
  
  // 重置状态并关闭弹窗
  resetState();
  popoverVisible.value = false;
};

// 重置表单状态
const resetState = () => {
  selectedReasons.value = [];
  showOtherInput.value = false;
  otherReason.value = "";
};
</script>

<style lang="scss" scoped>
.reason-list {
  margin: 5px 0;
}

.title{
  margin: 0px;
  margin-botton: 10px;
  margin-top: 5px;
}

.checkbox-group {
  display: flex;
    flex-direction: row;
        margin-bottom: 10px;
    gap: 8px;
    flex-wrap: wrap;
}

.reason-item {
  display: flex;
  align-items: center;
}

.other-input {
  margin-top: 15px;
  width: 100%;
}
</style>
