<template>
  <div class="research-template-container">
    <div class="research-template-type-list">
      <div class="research-template-type-item" :class="{ 'active': selectedType === 0 }" @click="selectType(0)">
        <i class="fa-solid fa-list-ul"></i> 全部
      </div>
      <div class="research-template-type-item" v-for="item in templateTypeList" :key="item.id"
        :class="{ 'active': selectedType === item.id}" @click="selectType(item.id)">
        <i :class="item.icon"></i> {{ item.name }}
      </div>
    </div>

    <!-- 模型列表 -->
    <el-scrollbar style="max-height:calc(100vh - 350px)">
      <div class="research-template-list" v-if="templateList.length > 0">
        <div class="research-template-item" v-for="item in templateList" :key="item.id"
          :class="{ 'active': selectedTemplateCode === item.id }" @click="selectTemplate(item)">
          <div class="template-icon">
            <img :src="imagePathByPath(item.icon)" alt="模板图标" />
          </div>
          <div class="template-content">
            <div class="research-template-item-title">
              {{ truncateString(item.name , 15) }}
            </div>
            <div class="research-template-item-content">
              {{ truncateString(item.description, 17) }}
            </div>
          </div>
        </div>
      </div>

      <div style="justify-content: center;" v-if="templateList.length == 0">
        <el-empty style="padding: 10px;" description="此类型还没有知识库，请选项其它类型的知识库" image-size="50" />
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';

import {
  getAllLongTextTemplateType,
  getTemplateByType , 
  getTemplateDetail ,
} from '@/api/base/im/scene/longText';

// 接收选中的知识库类型
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

// 发出选中的知识库类型
const emit = defineEmits(['update:modelValue', 'selectTemplate']);

const templateTypeList = ref([]);
const templateList = ref([]);
const selectedType = ref(0); // 默认选中"全部"
const selectedTemplateCode = ref(props.modelValue);

// 根据文档类型获取图标
const getIconByType = (type) => {
  const icons = {
    pdf: 'fa-solid fa-file-pdf',
    word: 'fa-solid fa-file-word',
    excel: 'fa-solid fa-file-excel',
    default: 'fa-solid fa-file'
  };
  return icons[type] || icons.default;
};

// 截断字符串
const truncateString = (str, num) => {
  if (!str) return '';
  return str.length > num ? str.slice(0, num) + '...' : str;
};

// 选择知识库类型
const selectType = async (typeId) => {
  if (selectedType.value === typeId) return;

  selectedType.value = typeId;

  try {
    const res = await getTemplateByType(typeId);
    templateList.value = res.data ; 
    // 如果切换类型后当前选中的模板不在新列表中，则取消选中
    if (!templateList.value.some(item => item.id === selectedTemplateCode.value)) {
      selectedTemplateCode.value = '';
    }
  } catch (error) {
    console.error('获取模板列表失败:', error);
    templateList.value = [];
  }
};

// 选择知识库
const selectTemplate = async (item) => {
  selectedTemplateCode.value = item.id;
  emit('update:modelValue', item.id);
  
  try {
    const res = await getTemplateDetail(item.id);
    emit('selectTemplate', res.data);
  } catch (error) {
    console.error('获取模板详情失败:', error);
  }
};

onMounted(async () => {
  try {
    // 获取所有知识库类型
    const typeRes = await getAllLongTextTemplateType();
    templateTypeList.value = typeRes.data;
    
    // 获取默认知识库列表（全部类型）
    const templateRes = await getTemplateByType(0);
    templateList.value = templateRes.data ; // .slice(0, 6);
    if (templateRes.data.length > 0 && !props.modelValue) {
      selectTemplate(templateRes.data[0]);
    }
  } catch (error) {
    console.error('初始化数据失败:', error);
    templateTypeList.value = [];
    templateList.value = [];
  }
});

// 监听外部传入的知识库值变化
watch(() => props.modelValue, (newVal) => {
  selectedTemplateCode.value = newVal;
});
</script>

<style lang="scss" scoped>
.research-template-container {
  margin-top: 10px;
  display: grid;

  .research-template-type-list {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 10px;

    .research-template-type-item {
      background: #f2f3f7;
      width: auto;
      border-radius: 10px;
      padding: 5px 10px;
      margin-right: 10px;
      margin-bottom: 10px;
      font-size: 14px;
      color: #555;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #e0e2e9;
      }

      &.active {
        background: #409eff;
        color: white;
      }

      i {
        margin-right: 5px;
      }
    }
  }

  .research-template-list {
    display: flex;
    flex-wrap: wrap;

    .research-template-item {
      display: flex;
      font-size: 14px;
      background: #fff ; // #fafafa;
      border-radius: 8px;
      padding: 8px;
      margin-right: 10px;
      margin-bottom: 10px;
      gap: 10px;
      width: calc(33% - 20px);
      cursor: pointer;
      transition: all 0.3s;
      align-items: flex-start;

      &:hover {
        background: #e8f3ff;
        border: 0px solid #409eff;
      }

      &.active {
        background: #e8f3ff;
        border: 0px solid #409eff;
        // box-shadow: 0 0 0 1px #409eff;
      }

      .template-icon {
        width: 80px;
        height: 80px;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          border-radius: 5px;
          object-fit: cover;
          box-shadow: 0 -2.7px 16.2px 0 rgba(56, 49, 80, 0.12);
        }
        
        .fa-file-pdf {
          color: #f05e5b;
        }
        .fa-file-word {
          color: #2b579a;
        }
        .fa-file-excel {
          color: #217346;
        }
        .fa-file {
          color: #666;
        }
      }

      .template-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        line-height: 18px;
        align-items: flex-start;
      }

      .research-template-item-title {
        font-size: 14px;
        font-weight: bold;
        color: #444;
        // white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        i {
          margin-right: 5px;
        }
      }

      .research-template-item-content {
        margin-top: 3px;
        font-size: 12px;
        color: #777;
        text-align: left;
      }
    }
  }
}
</style>