<template>
  <div class="ppt-type-config">
    <div class="config-row">
      <!-- 动态生成配置选项 -->
      <template v-for="(fieldConfig, index) in dynamicFieldConfigs" :key="index">
        <!-- {{ fieldConfig }} -->
        <!-- 按钮类型 -->
        <!-- <el-button
          v-if="fieldConfig.type === 'button'"
          :type="fieldConfig.buttonType || 'primary'"
          size="large"
          style="width: calc(33% - 5px);"
          @click="handleButtonClick(fieldConfig.field)"
        >
          {{ fieldConfig.title }}
        </el-button> -->
        
        <!-- 下拉选择类型 -->
        <el-select 
          v-if="fieldConfig.type === 'select'"
          v-model="config[fieldConfig.field]"
          :placeholder="fieldConfig.placeholder || `请选择${fieldConfig.title}`"
          size="large"
          style="width: calc(33% - 5px);"
          clearable
        >
          <el-option
            v-for="option in fieldConfig.options"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
          <template #prefix>
            <span>{{ fieldConfig.title }}</span>
          </template>
        </el-select>
        
        <!-- 文本输入类型 -->
        <!-- <el-input
          v-else-if="fieldConfig.type === 'input' || fieldConfig.type === 'textarea'"
          v-model="config[fieldConfig.field]"
          :type="fieldConfig.type"
          :placeholder="fieldConfig.placeholder || `请输入${fieldConfig.title}`"
          :maxlength="fieldConfig.maxLength"
          size="large"
          :resize="fieldConfig.type === 'textarea' ? 'vertical' : 'none'"
          :rows="fieldConfig.type === 'textarea' ? 3 : 1"
          style="width: calc(33% - 5px);"
          clearable
        >
          <template #prefix v-if="fieldConfig.type === 'input'">
            <span>{{ fieldConfig.title }}</span>
          </template>
        </el-input> -->

        <!-- 单选按钮组 -->
        <div v-else-if="fieldConfig.type === 'radio'" class="radio-group" style="width: calc(33% - 5px);">
          <div class="radio-title">{{ fieldConfig.title }}</div>
          <el-radio-group v-model="config[fieldConfig.field]" size="large">
            <el-radio
              v-for="option in fieldConfig.options"
              :key="option.value"
              :label="option.value"
              :border="fieldConfig.border"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 复选框组 -->
        <div v-else-if="fieldConfig.type === 'checkbox'" class="checkbox-group" style="width: calc(33% - 5px);">
          <div class="checkbox-title">{{ fieldConfig.title }}</div>
          <el-checkbox-group v-model="config[fieldConfig.field]" size="large">
            <el-checkbox
              v-for="option in fieldConfig.options"
              :key="option.value"
              :label="option.value"
              :border="fieldConfig.border"
            >
              {{ option.label }}
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <!-- 单个复选框 -->
        <div v-else-if="fieldConfig.type === 'checkbox-single'" class="checkbox-single" style="width: calc(33% - 5px);">
          <el-checkbox
            v-model="config[fieldConfig.field]"
            :label="fieldConfig.label || fieldConfig.title"
            size="large"
            :border="fieldConfig.border"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';

const emit = defineEmits(['update:modelValue', 'button-click']);

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  artcleTemplate: {
    type: Object,
    default: () => ({})
  }
});

// 解析模板配置
const parsedConfig = computed(() => {
  try {
    return JSON.parse(props.artcleTemplate.config || '[]');
  } catch (e) {
    console.error('解析模板配置失败:', e);
    return [];
  }
});

// 动态生成字段配置
const dynamicFieldConfigs = computed(() => {
  return parsedConfig.value.map(field => {
    const baseConfig = {
      field: field.field,
      title: field.title,
      type: field.type || 'input',
      placeholder: field.placeholder,
      maxLength: field.maxLength,
      required: field.required,
      isShow: field.isShow !== false,
      border: field.border || false,
      label: field.label,
      buttonType: field.buttonType || 'primary'
    };

    // 处理select/radio/checkbox类型的选项
    if (['select', 'radio', 'checkbox'].includes(field.type) && field.options) {
      baseConfig.options = field.options.map(opt => {
        if (typeof opt === 'string') {
          return { value: opt, label: opt };
        }
        return {
          value: opt.value || opt.key || opt,
          label: opt.label || opt.name || opt.value || opt.key || opt
        };
      });
    }

    return baseConfig;
  }).filter(field => field.isShow); // 只显示isShow为true的字段
});

// 初始化配置数据
const initConfig = () => {
  const defaultConfig = {};
  dynamicFieldConfigs.value.forEach(field => {
    if (field.type === 'select' && field.options?.length > 0) {
      defaultConfig[field.field] = field.options[0].value;
    } else if (field.type === 'checkbox') {
      defaultConfig[field.field] = Array.isArray(field.value) ? [...field.value] : [];
    } else if (field.type === 'checkbox-single') {
      defaultConfig[field.field] = field.value || false;
    } else if (field.type === 'radio' && field.options?.length > 0) {
      defaultConfig[field.field] = field.options[0].value;
    } else if (field.type !== 'button') { // 按钮类型不需要在config中存储值
      defaultConfig[field.field] = field.value || '';
    }
  });
  return defaultConfig;
};

// 双向绑定数据
const config = ref(initConfig());

// 处理按钮点击事件
const handleButtonClick = (field) => {
  emit('button-click', field);
};

// 数据变化时通知父组件
watch(config, (newVal) => {
  emit('update:modelValue', newVal);
}, { deep: true });

// 当artcleTemplate变化时重新初始化配置
watch(() => props.artcleTemplate, () => {
  config.value = initConfig();
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
  flex-wrap: wrap;
  gap: 10px;
}

.radio-group,
.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  .radio-title,
  .checkbox-title {
    font-size: 14px;
    color: var(--el-text-color-regular);
    margin-bottom: 4px;
  }
}

.checkbox-single {
  display: flex;
  align-items: center;
  height: 40px; // 与其它表单元素高度一致
}

.el-radio-group,
.el-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.el-radio,
.el-checkbox {
  margin-right: 8px;
}

.el-button {
  // 确保按钮与其他表单元素高度一致
  height: 40px;
}
</style>