<template>
  <div class="question-type-config">
    <div class="question-type-row" v-for="(item, index) in examStructure" :key="item.id">
      <!-- 题型选择 -->
      <el-select
        v-model="item.type"
        placeholder="请选择题型"
        size="large"
        style="width: 280px; margin-right: 20px;"
      >
      <el-option
        v-for="item in questionTypes"
        :key="item.type"
        :label="item.typeName"
        :value="item.type"
      > {{ item.typeName }}
    </el-option>
      </el-select>

      <!-- 题目数量 -->
      <el-input-number
        v-model="item.totalQuestion"
        :min="1"
        :max="maxQuestionCount"
        size="large"
        controls-position="right"
        placeholder="请输入题数"
        @change="handleInputChange">
      <template #prefix>
        <span>题数</span>
      </template>
    </el-input-number>

      <!-- 每题分值 -->
      <el-input-number
        v-model="item.scorePerQuestion"
        :min="1"
        :max="maxScorePerQuestion"
        size="large"
        placeholder="请输入分值"
        controls-position="right"
        style="width: 180px; margin-right: 20px;"
        @change="handleInputChange">
      <template #prefix>
        <span>分值</span>
      </template>
    </el-input-number>

      <!-- 删除按钮 -->
      <el-button
        type="danger"
        size="large"
        text 
        bg
        @click.prevent="handleDeleteQuestion(index)"
        style="vertical-align: middle;"
      >
        <i class="fa-solid fa-trash-can"></i>
      </el-button>
    </div>

    <!-- 添加题型按钮 -->
    <el-button
      type="primary"
      size="large"
      text 
      bg
      @click.prevent="handleAddQuestionType"
      class="add-button"
      style="width:100%;"
    >
      <i class="el-icon-circle-plus"></i> 添加题型
    </el-button>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { ElMessage , ElMessageBox } from 'element-plus';

import {
  getQuestionTypes
} from '@/api/base/im/scene/examPaper';

const emit = defineEmits(['update:modelValue']);

// 内置题型选项（可直接修改此处扩展题型）
const questionTypes = ref([]);

// 接收 props
const props = defineProps({
  // 题型配置数据（双向绑定）
  modelValue: {
    type: Array,
    default: () => []
  },
  // 最大题目数量限制（可覆盖内置默认值）
  maxQuestionCount: {
    type: Number,
    default: 100
  },
  // 最大每题分值限制（可覆盖内置默认值）
  maxScorePerQuestion: {
    type: Number,
    default: 100
  }
});

// 双向绑定数据
const examStructure = ref(props.modelValue);

// 数据变化时通知父组件
watch(examStructure, (newVal) => {
  emit('update:modelValue', newVal);
}, { deep: true });

// 添加题型（使用内置题型的默认值）
const handleAddQuestionType = () => {
  const newId = Date.now() + Math.random().toString(36).substr(2, 9);
  const defaultType = questionTypes.value[0]; // 取第一个题型作为默认
  
  examStructure.value.push({
    id: newId,
    type: defaultType.type,
    typeName: defaultType.typeName,
    typeDesc: defaultType.typeDesc,
    totalQuestion: defaultType.defaultTotal,
    scorePerQuestion: defaultType.defaultScore
  });
};

// 删除题型
const handleDeleteQuestion = async (index) => {
  try {
    await ElMessageBox.confirm(
      '确定删除该题型配置？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    examStructure.value.splice(index, 1);
    ElMessage.success('删除成功');
  } catch (error) {
    // User clicked cancel or closed the dialog
    ElMessage.info('已取消删除');
  }
};

// 数据变化时通知父组件
watch(examStructure, (newVal) => {

  // 修改newVal的typeName和typeDesc，使得与type对应上
  examStructure.value.forEach(item => {
    const type = item.type;
    const typeInfo = questionTypes.value.find(t => t.type === type);
    if (typeInfo) {
      item.typeName = typeInfo.typeName;
      item.typeDesc = typeInfo.typeDesc;
    }
  });

  emit('update:modelValue', newVal);
  // 新增：计算并发送统计信息
  const stats = calculateStats(newVal);
  emit('update:total', stats);
}, { deep: true });

// 计算统计信息
const calculateStats = (items) => {
  return items.reduce((acc, item) => {
    acc.totalQuestions += item.totalQuestion || 0;
    acc.totalScore += (item.totalQuestion || 0) * (item.scorePerQuestion || 0);
    return acc;
  }, { totalQuestions: 0, totalScore: 0 });
};

// 初始化时发送一次统计
onMounted(() => {
  const stats = calculateStats(examStructure.value);
  emit('update:total', stats);
});

// // 输入值校验
// const handleInputChange = (val, item) => {
//   // 整数校验
//   if (!Number.isInteger(item.totalQuestion)) {
//     item.totalQuestion = item.totalQuestion || 1;
//     ElMessage.warning('题数必须为整数，已自动修正为最小值');
//   }
//   if (!Number.isInteger(item.scorePerQuestion)) {
//     item.scorePerQuestion = item.scorePerQuestion || 1;
//     ElMessage.warning('分值必须为整数，已自动修正为最小值');
//   }
  
//   // 边界值校验
//   item.totalQuestion = Math.max(1, Math.min(item.totalQuestion, props.maxQuestionCount));
//   item.scorePerQuestion = Math.max(1, Math.min(item.scorePerQuestion, props.maxScorePerQuestion));
// };

// 修改后的正确代码
const handleInputChange = (val) => {
  // 不需要item参数，因为v-model已经绑定了
  examStructure.value.forEach(item => {
    // 整数校验
    if (!Number.isInteger(item.totalQuestion)) {
      item.totalQuestion = Math.floor(item.totalQuestion) || 1;
      ElMessage.warning('题数必须为整数，已自动修正');
    }
    if (!Number.isInteger(item.scorePerQuestion)) {
      item.scorePerQuestion = Math.floor(item.scorePerQuestion) || 1;
      ElMessage.warning('分值必须为整数，已自动修正');
    }
    
    // 边界值校验
    item.totalQuestion = Math.max(1, Math.min(item.totalQuestion, props.maxQuestionCount));
    item.scorePerQuestion = Math.max(1, Math.min(item.scorePerQuestion, props.maxScorePerQuestion));
  });
  
  // 触发统计更新
  const stats = calculateStats(examStructure.value);
  emit('update:total', stats);
};

onMounted(() => {
  getQuestionTypes().then(res => {
    questionTypes.value = res.data;
  })
});

</script>

<style lagn="scss" scoped>
.question-type-config {
  padding: 10px 0px;
}

.question-type-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.question-type-row > * {
  margin-right: 20px;
}

.add-buttom {
  width: 100%;
}

</style>