<template>
  <div class="research-template-container">
    <div class="research-template-type-list">
      <div class="research-template-type-item" :class="{ 'active': selectedType === 0 }" @click="selectType(0)">
        <i class="fa-solid fa-list-ul"></i> 全部
      </div>
      <div class="research-template-type-item" v-for="item in templateTypeList" :key="item.id"
        :class="{ 'active': selectedType === item.id}" @click="selectType(item.id)">
        <i class="fa-solid fa-feather-pointed"></i> {{ item.groupName }}
      </div>
    </div>

    <!-- 模型列表 -->
    <el-scrollbar style="max-height:130px">
      <div class="research-template-list" v-if="templateList.length > 0">

        <div class="research-template-item" v-for="item in templateList" :key="item.id"
          :class="{ 'active': selectedTemplateCode.id === item.id }" @click="selectTemplate(item)">
          <div class="template-icon">
             <i :class="getFileIcon(item.fileType)"></i>
          </div>

          <div class="template-content">
            <div class="research-template-item-title">
              {{ truncateString(item.docName, 10) }}
            </div>
            <div class="research-template-item-content">
              {{ truncateString(item.docContent, 17) }}
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
import { ref, computed, onMounted } from 'vue';
import {
  getAllArticleTemplateType,
  getTemplateByType , 
  getTemplateDetail ,
} from '@/api/base/im/scene/productResearchDataset';

// 接收选中的知识库类型
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

const route = useRoute();

const sceneId = computed(() => route.query.sceneId);
// 发出选中的知识库类型
const emit = defineEmits(['update:modelValue' , 'selectTemplate']);

const templateTypeList = ref([]);
const templateList = ref([]);
const selectedType = ref(0); // 默认选中"全部"
const selectedTemplateCode = ref(props.modelValue);

// 计算属性：根据选中的类型过滤知识库列表
// const filteredTemplateList = computed(() => {
//   if (selectedType.value === 0) {
//     // return templateList.value;
//     return templateList.value ; // .slice(0, 7);
//   }
//   return templateList.value.filter(item => item.articleTemplateTypeId === selectedType.value);
// });

// 选择知识库类型
const selectType = (typeId) => {
  if (selectedType.value === typeId) return;

  selectedType.value = typeId;
  emit('update:modelValue', typeId);

  getTemplateByType(typeId , sceneId.value).then(res => {
    templateList.value = res.data.slice(0, 6);
  });
};

// 选择知识库
const selectTemplate = (item) => {
  selectedTemplateCode.value = item.id;

  // 查询出知识库配置项
  getTemplateDetail(item.id).then(res => {
    // emit('update:modelValue', res.data);
    emit('selectTemplate', res.data);
  })

};

// 根据文件类型获取对应的图标
const getFileIcon = (fileType) => {
  const iconMap = {
    pdf: 'fa-solid fa-file-pdf',
    doc: 'fa-solid fa-file-word',
    docx: 'fa-solid fa-file-word',
    xls: 'fa-solid fa-file-excel',
    xlsx: 'fa-solid fa-file-excel',
    ppt: 'fa-solid fa-file-powerpoint',
    pptx: 'fa-solid fa-file-powerpoint',
    txt: 'fa-solid fa-file-lines',
    csv: 'fa-solid fa-file-csv',
    zip: 'fa-solid fa-file-zipper',
    rar: 'fa-solid fa-file-zipper',
    jpg: 'fa-solid fa-file-image',
    png: 'fa-solid fa-file-image',
    gif: 'fa-solid fa-file-image',
    mp3: 'fa-solid fa-file-audio',
    mp4: 'fa-solid fa-file-video',
    default: 'fa-solid fa-file'
  };
  
  // 转换为小写以匹配更多情况
  const lowerType = (fileType || '').toLowerCase();
  return iconMap[lowerType] || iconMap.default;
};

onMounted(() => {
  // 获取所有知识库类型
  getAllArticleTemplateType(sceneId.value).then(res => {
    templateTypeList.value = res.data;
  });

  // 获取默认知识库列表（全部类型）
  getTemplateByType(0 , sceneId.value).then(res => {

    // 取前面的6个
    // templateList.value = res.data;
    templateList.value = res.data.slice(0, 6);
    if (res.data.length > 0 && !props.modelValue) {
      selectTemplate(res.data[0]);
    }
  });
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
      background: #fafafa;
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
        width: 30px;
        height: 30px;
        flex-shrink: 0;
        padding: 5px;
        color: #f05e5b;
        font-size: 25px; 

        .fa-file-pdf {
          color: #f05e5b;
        }
        .fa-file-word {
          color: #2b579a;
        }
        .fa-file-excel {
          color: #217346;
        }
        .fa-file-powerpoint {
          color: #d24726;
        }
        .fa-file-image, .fa-file-video, .fa-file-audio {
          color: #7c4dff;
        }
        .fa-file-zipper {
          color: #795548;
        }
        .fa-file-csv {
          color: #4caf50;
        }
        .fa-file-lines {
          color: #666;
        }
        .fa-file {
          color: #3b5998;
        }
      }

      .template-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        line-height: 18px;
      }

      .research-template-item-title {
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

      .research-template-item-content {
        margin-top: 3px;
        font-size: 12px;
        color: #777;
      }
    }
  }
}
</style>