<template>
  <div class="article-template-container">

    <!-- 模型列表 -->
    <el-scrollbar style="height:calc(100vh - 200px)">
      <div class="article-template-list" v-if="filteredTemplateList.length > 0">

        <div class="article-template-item" v-for="item in filteredTemplateList" :key="item.id"
          :class="{ 'active': selectedTemplateCode === item.id }" @click="selectTemplate(item)">

          <div class="template-content">
            <div class="article-template-item-title">
              {{ truncateString(item.name, 8) }}
            </div>
            <div class="article-template-item-content">
              {{ truncateString(item.description, 18) }}
            </div>
          </div>

          <div class="template-icon" :style="'background-size: 100%; background-image: url(&quot;' + imagePathByPath(item.icon) + '&quot;)'">
          </div>

        </div>
      </div>

      <div style="justify-content: center;" v-if="filteredTemplateList.length == 0">
        <el-empty style="padding: 10px;" description="此类型还没有模板，请选项其它类型的模板" image-size="70" />
      </div>
    </el-scrollbar>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  getAllArticleTemplateType,
  getTemplateByType
} from '@/api/base/im/scene/articleWriting';

// 接收选中的模板类型
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

// 发出选中的模板类型
const emit = defineEmits(['update:modelValue']);

const templateTypeList = ref([]);
const templateList = ref([]);
const selectedType = ref(0); // 默认选中"全部"
const selectedTemplateCode = ref(props.modelValue);

// 计算属性：根据选中的类型过滤模板列表
const filteredTemplateList = computed(() => {
  if (selectedType.value === 0) {
    // return templateList.value;
    return templateList.value.slice(0, 10);
  }
  return templateList.value.filter(item => item.articleTemplateTypeId === selectedType.value);
});

// 选择模板类型
const selectType = (typeId) => {
  if (selectedType.value === typeId) return;

  selectedType.value = typeId;
  getTemplateByType(typeId).then(res => {
    templateList.value = res.data;
  });
};

// 选择模板
const selectTemplate = (item) => {
  selectedTemplateCode.value = item.id;
  emit('update:modelValue', item.id);
};

// // 截取字符串
// const truncateString = (str, maxLength) => {
//   if (!str) return '';
//   return str.length > maxLength ? str.substring(0, maxLength) + '...' : str;
// };

// 图片路径处理函数
// const imagePathByPath = (path) => {
//   // 这里应该是项目中的图片路径处理函数
//   return path || 'https://picsum.photos/60/60?random=template';
// };

onMounted(() => {
  // 获取所有模板类型
  getAllArticleTemplateType().then(res => {
    templateTypeList.value = res.data;
  });

  // 获取默认模板列表（全部类型）
  getTemplateByType(0).then(res => {
    templateList.value = res.data;
  });
});

// 监听外部传入的模板值变化
watch(() => props.modelValue, (newVal) => {
  selectedTemplateCode.value = newVal;
});
</script>

<style lang="scss" scoped>
.article-template-container {
  padding: 10px;
  background-color: #fff ;
  border-radius:10px;
  margin-top: 10px;
  display: grid;

  .article-template-type-list {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 10px;

    .article-template-type-item {
      background: #f5f5f5;
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

  .article-template-list {
    display: flex;
    flex-wrap: wrap;

    .article-template-item {
      display: flex;
      font-size: 13px;
      padding: 8px 10px;
      margin-right: 10px;
      margin-bottom: 10px;
      background-color: #fafafa;
      gap: 10px;
      height: 140px;
      overflow: hidden;
      width: calc(33% - 10px);
      cursor: pointer;
      transition: all 0.3s;
      align-items: flex-start;
      line-height: 23px;
      border-radius: 12px;

      &:hover {
        background: #e8f3ff;
        border: 0px solid #409eff;
      }

      &.active {
        background: #e8f3ff;
        border: 0px solid #409eff;
      }

      .template-icon {
        width: 50%;
        max-width: 157px;
        height: 102px;
        flex-shrink: 0;
        box-shadow: 0 -2.7px 16.2px 0 rgba(56, 49, 80, .12);
        position: relative;
        transform-origin: right bottom;
        border-radius: 5px;
      }

      .template-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        width: 50%;
        justify-content: flex-start;
      }

      .article-template-item-title {
        color: #444;
        font-size: 16px;
        font-weight: 550;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        i {
          margin-right: 5px;
        }
      }

      .article-template-item-content {
        margin-top: 3px;
        font-size: 13px;
        color: #777;
      }
    }
  }
}
</style>