<template>
  <el-select
    :model-value="props.modelValue"
    :placeholder="placeholder"
    :disabled="disabled"
    :size="size"
    :clearable="clearable"
    @update:model-value="handleUpdate"
  >
    <el-option
      v-for="kb in knowledgeBases"
      :key="kb.id"
      :label="kb.groupName"
      :value="kb.id"
    >
      <div class="option-content">
        <div class="option-title">{{ kb.groupName }}</div>
        <div class="option-meta">
          <span class="documents-count">
            <el-icon size="12"><Document /></el-icon>
            {{ kb.documentCount || 0 }} 篇文档
          </span>
          <span class="update-time">
            <el-icon size="12"><Clock /></el-icon>
            {{ formatDate(kb.addTime) }}
          </span>
        </div>
      </div>
    </el-option>
  </el-select>
</template>

<script setup name="KnowledgeBaseSelector">
import { ref, onMounted, watch } from 'vue';
import { ElIcon, ElMessage } from 'element-plus';
import { Document, Clock } from '@element-plus/icons-vue';
import { listGroup } from '@/api/base/im/scene/productResearchDataset';

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: null
  },
  placeholder: { type: String, default: '请选择知识库' },
  disabled: { type: Boolean, default: false },
  size: {
    type: String,
    default: 'default',
    validator: (v) => ['large', 'default', 'small', 'mini'].includes(v)
  },
  clearable: { type: Boolean, default: true },
  statusFilter: { type: String, default: 'normal' }
});

// 包含 update:modelValue 的 emits
const emit = defineEmits(['update:modelValue', 'change', 'load-success', 'load-fail']);

const knowledgeBases = ref([]);
const loading = ref(false);

const formatDate = (dateString) => {
  if (!dateString) return '未知时间';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
};

const fetchKnowledgeBases = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const params = { pageNum: 1, pageSize: 1000, status: props.statusFilter };
    const response = await listGroup(params);
    if (response.code === 200 && Array.isArray(response.data)) {
      knowledgeBases.value = response.data;
      emit('load-success', response.data);
    } else {
      ElMessage.error('加载知识库列表失败');
      emit('load-fail', response.msg || '未知错误');
    }
  } catch (err) {
    console.error(err);
    ElMessage.error('获取知识库数据异常');
    emit('load-fail', err.message);
  } finally {
    loading.value = false;
  }
};

// 当 el-select 触发 update:model-value 时使用此处理器
const handleUpdate = (value) => {
  // 将新值通知给父组件（标准 v-model 行为）
  emit('update:modelValue', value);
  // 同时转发 change 事件（如果需要）
  emit('change', value);
};

watch(() => props.statusFilter, () => fetchKnowledgeBases());
onMounted(() => fetchKnowledgeBases());
</script>