<template>
  <div class="ppt-type-config">
    <div class="config-row">
      <!-- 写作类型选择 -->
      <el-select v-model="config.writingType" placeholder="请选择写作类型" size="large" style="width: 200px; margin-right: 20px;">
        <el-option
          v-for="type in writingTypeOptions"
          :key="type.value"
          :label="type.label"
          :value="type.value"
        >
          <span style="margin-right: 10px;">
            <i :class="type.icon"></i>
          </span>
          <span>{{ type.label }}</span>
        </el-option>
        <template #prefix>
          <span>类型</span>
        </template>
      </el-select>

      <!-- 受众选择 -->
      <el-select v-model="config.audience" placeholder="请选择受众" size="large" style="width: 200px; margin-right: 20px;">
        <el-option
          v-for="audience in audienceOptions"
          :key="audience.value"
          :label="audience.label"
          :value="audience.value"
        />
        <template #prefix>
          <span>受众</span>
        </template>
      </el-select>

      <!-- 场景选择 -->
      <el-select v-model="config.scenario" placeholder="请选择场景" size="large" style="width: 200px; margin-right: 20px;">
        <el-option
          v-for="scenario in scenarioOptions"
          :key="scenario.value"
          :label="scenario.label"
          :value="scenario.value"
        />
        <template #prefix>
          <span>场景</span>
        </template>
      </el-select>

      <!-- 语句风格选择 -->
      <el-select v-model="config.tone" placeholder="请选择风格" size="large" style="width: 200px;">
        <el-option
          v-for="tone in toneOptions"
          :key="tone.value"
          :label="tone.label"
          :value="tone.value"
        />
        <template #prefix>
          <span>语句</span>
        </template>
      </el-select>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const emit = defineEmits(['update:modelValue']);

// 写作类型选项 (with Font Awesome 6.x icons)
const writingTypeOptions = ref([
  { value: 'general', label: '通用', icon: 'fa-solid fa-file-lines' },
  { value: 'experience', label: '心得体会', icon: 'fa-solid fa-heart' },
  { value: 'speech_draft', label: '发言稿', icon: 'fa-solid fa-microphone' },
  { value: 'meeting_minutes', label: '会议纪要', icon: 'fa-solid fa-clipboard-list' },
  { value: 'application', label: '申请书', icon: 'fa-solid fa-file-signature' },
  { value: 'policy_interpretation', label: '政策解读文章', icon: 'fa-solid fa-gavel' },
  { value: 'advanced_deeds', label: '先进事迹报道', icon: 'fa-solid fa-medal' },
  { value: 'speech', label: '讲话发言稿', icon: 'fa-solid fa-bullhorn' }
]);

// 受众选项
const audienceOptions = ref([
  { value: 'general', label: '大众' },
  { value: 'investor', label: '投资者' },
  { value: 'boss', label: '老板' },
  { value: 'interviewer', label: '面试官' },
  { value: 'employee', label: '员工' },
  { value: 'colleague', label: '同事同行' },
  { value: 'visitor', label: '在线访客' },
  { value: 'team', label: '组员' }
]);

// 场景选项
const scenarioOptions = ref([
  { value: 'general', label: '通用' },
  { value: 'analysis', label: '分析报告' },
  { value: 'teaching', label: '教学课件' },
  { value: 'promotion', label: '宣传材料' },
  { value: 'speech', label: '公众演讲' },
  { value: 'media', label: '在线媒体' },
  { value: 'announcement', label: '公告' },
  { value: 'research', label: '研究报告' },
  { value: 'conference', label: '学术会议' },
  { value: 'project', label: '项目汇报' },
  { value: 'introduction', label: '个人介绍' },
  { value: 'business_plan', label: '商业计划书' },
  { value: 'solution', label: '解决方案' },
  { value: 'product', label: '产品介绍' },
  { value: 'meeting', label: '会议流程' },
  { value: 'annual_plan', label: '年度计划' },
  { value: 'annual_summary', label: '年度总结' },
  { value: 'health', label: '健康科普' },
  { value: 'financial', label: '财务报告' },
  { value: 'project_plan', label: '项目计划书' },
  { value: 'business', label: '商业' }
]);

// 语句风格选项
const toneOptions = ref([
  { value: 'professional', label: '专业' },
  { value: 'motivational', label: '励志' },
  { value: 'humorous', label: '幽默' },
  { value: 'friendly', label: '亲切' },
  { value: 'confident', label: '自信' },
  { value: 'gentle', label: '温柔' }
]);

// 接收 props
const props = defineProps({
  // 配置数据（双向绑定）
  modelValue: {
    type: Object,
    default: () => ({
      writingType: 'general', // 默认值改为通用
      audience: 'general',
      scenario: 'general',
      tone: 'professional'
    })
  }
});

// 双向绑定数据
const config = ref({
  writingType: props.modelValue.writingType || 'general',
  audience: props.modelValue.audience || 'general',
  scenario: props.modelValue.scenario || 'general',
  tone: props.modelValue.tone || 'professional'
});

// 数据变化时通知父组件
watch(config, (newVal) => {
  emit('update:modelValue', newVal);
}, { deep: true });
</script>

<style lang="scss" scoped>
.ppt-type-config {
  padding: 10px 0px;
}

.config-row {
  display: flex;
  align-items: center;
  margin-bottom: 0px;
}

.config-row > * {
  margin-right: 10px;
}
</style>