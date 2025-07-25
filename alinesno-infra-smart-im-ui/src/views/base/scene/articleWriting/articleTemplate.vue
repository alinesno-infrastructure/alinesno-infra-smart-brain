<template>
  <div class="article-template-container">
    <div class="article-template-type-list">
      <div class="article-template-type-item" :class="{ 'active': selectedType === 0 }" @click="selectType(0)">
        <i class="fa-solid fa-list-ul"></i> 全部
      </div>
      <div class="article-template-type-item" v-for="item in templateTypeList" :key="item.code"
        :class="{ 'active': selectedType === item.code }" @click="selectType(item.code)">
        <i :class="item.icon"></i> {{ item.label }}
      </div>
    </div>

    <!-- 模型列表 -->
    <el-scrollbar style="height:calc(100vh - 320px)">
      <div class="article-template-list" v-if="templateList.length > 0">

        <div class="article-template-item" v-for="item in templateList" :key="item.id"
          :class="{ 'active': selectedTemplateCode.id === item.id }" @click="selectTemplate(item)">
          <div class="template-icon">
            <img :src="imagePathByPath(item.icon)" alt="模板图标" />
          </div>

          <div class="template-content">
            <div class="article-template-item-title">
              {{ truncateString(item.name, 10) }}
            </div>
            <div class="article-template-item-content">
              {{ truncateString(item.description, 34) }}
            </div>
          </div>
        </div>
      </div>

      <div style="justify-content: center;" v-if="templateList.length == 0">
        <el-empty style="padding: 10px;" description="此类型还没有模板，请选项其它类型的模板" image-size="70" />
      </div>
    </el-scrollbar>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  getAllArticleTemplateType,
  getTemplateByType , 
  getTemplateDetail ,
} from '@/api/base/im/scene/articleWriting';

// 接收选中的模板类型
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

// 发出选中的模板类型
const emit = defineEmits(['update:modelValue' , 'selectTemplate']);

const templateTypeList = ref([]);
const templateList = ref([]);
const selectedType = ref(0); // 默认选中"全部"
const selectedTemplateCode = ref(props.modelValue);

// 计算属性：根据选中的类型过滤模板列表
// const filteredTemplateList = computed(() => {
//   if (selectedType.value === 0) {
//     // return templateList.value;
//     return templateList.value ; // .slice(0, 7);
//   }
//   return templateList.value.filter(item => item.articleTemplateTypeId === selectedType.value);
// });

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

  // 查询出模板配置项
  getTemplateDetail(item.id).then(res => {
    emit('update:modelValue', res.data);
    emit('selectTemplate', res.data);
  })

};

onMounted(() => {
  // 获取所有模板类型
  getAllArticleTemplateType().then(res => {
    templateTypeList.value = res.data;
  });

  // 获取默认模板列表（全部类型）
  getTemplateByType(0).then(res => {
    templateList.value = res.data;
    if (res.data.length > 0 && !props.modelValue) {
      selectTemplate(res.data[0]);
    }
  });
});

// 监听外部传入的模板值变化
watch(() => props.modelValue, (newVal) => {
  selectedTemplateCode.value = newVal;
});
</script>

<style lang="scss" scoped>
.article-template-container {
  margin-top: 10px;
  display: grid;

  .article-template-type-list {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 10px;

    .article-template-type-item {
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

  .article-template-list {
    display: flex;
    flex-wrap: wrap;

    .article-template-item {
      display: flex;
      font-size: 14px;
      background: #fff;
      border-radius: 8px;
      padding: 8px;
      margin-right: 10px;
      margin-bottom: 10px;
      gap: 10px;
      width: calc(33% - 10px);
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
      }

      .template-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        line-height: 18px;
      }

      .article-template-item-title {
        font-size: 14px;
        font-weight: bold;
        color: #444;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        i {
          margin-right: 5px;
        }
      }

      .article-template-item-content {
        margin-top: 3px;
        font-size: 12px;
        color: #777;
      }
    }
  }
}
</style>