<template>
    <div class="review-page-container">
        <el-row :gutter="20">
            <!-- 模板分类  -->
            <el-col :span="5" :xs="24">
                <div class="plugin-catalog">
                    <div class="catalog-title">
                        <i class="fa-solid fa-computer"></i> 模板分类
                    </div>
                    <div class="catalog-content">
                      <div  
                        class="catalog-item" 
                        :class="{ 'active': currentlongTextTemplateType === '' }"
                        @click="handleCategoryClick('')">
                        <i class="fa-solid fa-house"></i> 
                        <span class="category-name">全部类型</span>
                            <span v-if="categorySelectedTotalCount > 0" class="selected-count">
                                {{ categorySelectedTotalCount }}
                            </span>
                      </div>
                        <div 
                            v-for="(category, index) in pluginCategories" 
                            :key="index" 
                            @click="handleCategoryClick(category.id)"
                            class="catalog-item" 
                            :class="{ active: category.id == currentlongTextTemplateType }">
                            <i :class="category.icon"></i> 
                            <span class="category-name">{{ category.name }}</span>
                            
                            <span v-if="category.categorySelectedCount > 0" class="selected-count">
                                {{ category.categorySelectedCount }}
                            </span>
                        </div>
                    </div>
                </div>
            </el-col>

            <!-- 模板数据 -->
            <el-col :span="19" :xs="24">
                <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                    <el-form-item label="模板名称" prop="name">
                        <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入模板名称" clearable style="width: 240px"
                            @keyup.enter="handleQuery" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                    </el-form-item>
                </el-form>

                <!-- 卡片列表 -->
                <div v-loading="loading" class="template-card-list">
                    <div 
                        v-for="(template, index) in longTextTemplateList" 
                        :key="index" 
                        class="template-card"
                        :class="isSelected(template)?'selected':''"
                        @click="handleTemplateSelect(template)"
                    >
                        <div class="card-header">
                            <div class="card-icon">
                                <img :src="imagePath(template.icon)" :alt="template.name" />
                            </div>
                            <div class="card-title">
                                {{ template.name }}
                            </div>
                            <div class="card-select-indicator">
                                <i v-if="isSelected(template)" class="fa-solid fa-check"></i>
                            </div>
                        </div>
                        
                        <div class="card-body">
                            <div class="card-description">
                                {{ template.description }}
                            </div>
                        </div>
                    </div>
                </div>
                
                <pagination 
                    v-show="total > 0" 
                    :total="total" 
                    v-model:page="queryParams.pageNum"
                    v-model:limit="queryParams.pageSize" 
                    @pagination="getList" 
                />
            </el-col>
        </el-row>

    </div>
</template>

<script setup name="LongTextTemplate">
import { listLongTextTemplate, getAllLongTextTemplate } from "@/api/smart/scene/longTextTemplate";
import { reactive, ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { getCurrentInstance } from 'vue';

const props = defineProps({
    initialSelectedTemplates: {
        type: Array,
        default: () => []
    },
    sceneData: {
        type: Object,
        default: () => ({})
    }
})

const router = useRouter();
const { proxy } = getCurrentInstance();

// 状态管理
const longTextTemplateList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const categorySelectedTotalCount = ref(0)
const dateRange = ref([]);
const currentlongTextTemplateType = ref('');
const selectedTemplates = ref([]); // 存储选中的模板

// 列显隐信息
const columns = ref([
    { key: 0, label: `图标`, visible: true },
    { key: 1, label: `模板名称`, visible: true },
    { key: 2, label: `模板类型`, visible: true },
    { key: 3, label: `使用场景`, visible: true },
    { key: 4, label: `状态`, visible: true },
    { key: 5, label: `模板描述`, visible: true },
    { key: 6, label: `模板目标`, visible: true },
    { key: 7, label: `创建时间`, visible: true },
    { key: 8, label: `编辑`, visible: true },
]);

// 表单数据
const data = reactive({
    form: {},
    queryParams: {
        pageNum: 1,
        pageSize: 16,
        toolName: undefined,
        name: undefined,
        pluginType: undefined,
        status: undefined,
        deptId: undefined,
        longTextTemplateType: undefined
    },
    rules: {
        id: [{ required: true, message: "模板编号不能为空", trigger: "blur" }],
        name: [{ required: true, message: "模板名称不能为空", trigger: "blur" }, {
            min: 2,
            max: 20,
            message: "模板名称长度必须介于 2 和 20 之间",
            trigger: "blur"
        }],
        icon: [{ required: true, message: "图标不能为空", trigger: "blur" }],
        pluginType: [{ required: true, message: "模板类型不能为空", trigger: "blur" }],
        scene: [{ required: true, message: "使用场景不能为空", trigger: "blur" }],
        hasStatus: [{ required: true, message: "状态不能为空", trigger: "blur" }],
        description: [{ required: true, message: "模板描述不能为空", trigger: "blur" }],
        prompt: [{ required: true, message: "模板Prompt不能为空", trigger: "blur" }],
        longTextTemplateType: [{ required: true, message: "模板类型不能为空", trigger: "blur" }],
        longTextTemplatePermission: [{ required: true, message: "数据范围不能为空", trigger: "blur" }],
    }
});

const { queryParams, form, rules } = toRefs(data);

// 模板分类
const pluginCategories = ref([
    { code: 'fa-solid fa-newspaper', label: '新闻阅读' },
    { code: 'fa-solid fa-image', label: '图像' },
    { code: 'fa-solid fa-screwdriver-wrench', label: '实用模板' },
    { code: 'fa-solid fa-house-chimney', label: '便利生活' },
    { code: 'fa-solid fa-magnifying-glass', label: '网页搜索' },
    { code: 'fa-solid fa-graduation-cap', label: '科学与教育' },
    { code: 'fa-solid fa-users', label: '社交' },
    { code: 'fa-solid fa-gamepad', label: '游戏与娱乐' },
    { code: 'fa-solid fa-money-bill-trend-up', label: '金融与商业' }
]);

// 初始化选中的模板
onMounted(() => {
    // 从父组件传入的初始选中数据进行初始化
    if (props.initialSelectedTemplates && props.initialSelectedTemplates.length) {
        selectedTemplates.value = [...props.initialSelectedTemplates];
    }
});

// 处理分类点击
const handleCategoryClick = (id) => {
    queryParams.value.longTextTemplateType = id;
    currentlongTextTemplateType.value = id;
    queryParams.value.pageNum = 1; // 重置页码
    getList();
};

// 查询模板列表
function getList() {
    loading.value = true;
    console.log("queryParams.value = " + queryParams.value)
    listLongTextTemplate(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
        loading.value = false;
        longTextTemplateList.value = res.rows;
        total.value = res.total;
    });
};

// 搜索
function handleQuery() {
    queryParams.value.pageNum = 1;
    getList();
};

// 重置搜索
function resetQuery() {
    dateRange.value = [];
    proxy.resetForm("queryRef");
    queryParams.value.name = undefined;
    queryParams.value.pluginType = undefined;
    proxy.$refs.deptTreeRef?.setCurrentKey(null);
    handleQuery();
};

// 处理模板选择
const handleTemplateSelect = (template) => {
    const isAlreadySelected = selectedTemplates.value.some(item => item.id === template.id);
    
    if (isAlreadySelected) {
        // 取消选择
        selectedTemplates.value = selectedTemplates.value.filter(item => item.id !== template.id);
    } else {
        // 选中
        selectedTemplates.value.push({...template});
    }

    // 重新计算分类的categorySelectedCount
    reCountPluginCategories();
};

// 判断模板是否被选中
const isSelected = (template) => {
    return selectedTemplates.value.some(item => item.id === template.id);
};

// 获取权限类型样式和文本
const getPermissionType = (permission) => {
    switch(permission) {
        case 'person':
            return { label: '私有', type: 'info' };
        case 'org':
            return { label: '组织', type: 'success' };
        case 'public':
            return { label: '公开', type: 'primary' };
        default:
            return { label: '未知', type: 'default' };
    }
};

// 获取分类下选中的数量
const getCategorySelectedCount = (categoryCode) => {
    return selectedTemplates.value.filter(
        item => item.longTextTemplateType === categoryCode
    ).length;
};

// 获取分类列表
const getlongTextTemplateTypeList = async() => {
    await getAllLongTextTemplate().then(response => {
        pluginCategories.value = response.data;
    });
}

onMounted(async () => {
    // 从父组件传入的初始选中数据进行初始化
    if (props.initialSelectedTemplates && props.initialSelectedTemplates.length) {
        selectedTemplates.value = [...props.initialSelectedTemplates];
    }

    console.log('onMounted --> selectedTemplates.value = ' + selectedTemplates.value);

    // 初始化后加载模板列表
    getList(); 
    
    // 获取分类列表
    await getlongTextTemplateTypeList();

    // 重新计算分类
    reCountPluginCategories();
});

// 重新计算
const reCountPluginCategories =() => {
    if(pluginCategories.value){
      categorySelectedTotalCount.value = 0 ; 
      for(var i = 0 ; i < pluginCategories.value.length ; i ++){
        let item = pluginCategories.value[i] ; 
        const categorySelectedCount = getCategorySelectedCount(item.id);

        pluginCategories.value[i].categorySelectedCount = categorySelectedCount ; 
        categorySelectedTotalCount.value += categorySelectedCount ;
      }
    }
}

// 暴露方法给父组件
const getConfigData = () => {
  // 遍历模板数据，仅保留需要的字段
  const simplifiedTemplates = selectedTemplates.value.map(template => ({
      id: template.id,        
      icon: template.icon,        
      name: template.name,        
      longTextTemplateType: template.longTextTemplateType,        
      description: template.description  
  }));

  return {
      selectedTemplateIds: simplifiedTemplates  
  };
};

// 监听初始值变化并设置选中状态
watch(() => props.initialSelectedTemplates, (newVal) => {
  if (Array.isArray(newVal)) {
    // 如果直接传递的是模板ID数组
    selectedTemplates.value = newVal
  } else if (newVal && Array.isArray(newVal.selectedTemplateIds)) {
    // 如果传递的是包含selectedTemplateIds属性的对象
    selectedTemplates.value = newVal.selectedTemplateIds
  } else {
    // 其他情况设为空数组
    selectedTemplates.value = []
  }

  reCountPluginCategories();

}, { immediate: true, deep: true })

defineExpose({ 
    getConfigData
})

</script>

<style lang="scss" scoped>

.plugin-catalog {
    border-radius: 6px;
    border: 1px solid #e5e7eb;
    padding: 12px;
    background-color: #fff;

    .catalog-title {
        padding: 10px 0;
        font-size: 16px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;
        border-bottom: 1px solid #f3f4f6;
    }

    .catalog-content {
        .catalog-item {
            padding: 10px 12px;
            color: #4b5563;
            cursor: pointer;
            margin-bottom: 4px;
            border-radius: 4px;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            transition: all 0.2s ease;
            border-left: 3px solid #fafafa;
            line-height: 19px;

            &:hover {
                background-color: #f3f4f6;
            }

            i {
                margin-right: 8px;
                width: 18px;
                text-align: center;
            }

            .category-name {
                flex: 1;
            }

            .selected-count {
                background-color: #3b82f6;
                color: white;
                border-radius: 50%;
                font-size: 12px;
                text-align: center;
                height: 20px;
                width: 20px;
                display: flex;
                align-items: center;
                justify-content: center; 
                line-height: 1rem;
            }
        }

        .active {
            background-color: #eff6ff;
            color: var(--el-color-primary);
            border-left: 3px solid #1d75b0;
            font-weight: 500;
        }
    }
}

.template-card-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
}

.template-card {
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 16px;
    background-color: #fff;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;

    &:hover {
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        border-color: #d1d5db;
    }

    &.selected {
        border-color: #3b82f6;
        background-color: #f9fafb;
    }
}

.card-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .card-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 12px;
        flex-shrink: 0;

        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
    }

    .card-title {
        font-weight: 600;
        font-size: 16px;
        color: #1f2937;
        flex: 1;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .card-select-indicator {
        color: #3b82f6;
        
        .el-checkbox {
            .el-checkbox__input {
                display: none;
            }
            
            i {
                font-size: 18px;
            }
        }
    }
}

.card-body {
    .card-description {
           font-size: 14px;
    color: var(--el-text-color-secondary);
    margin-bottom: 10px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    min-height: 40px;
    line-height: 20px; 
    }

    .card-meta {
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-size: 12px;
        color: #9ca3af;

        .permission-tag {
            margin-right: 8px;
        }

        .template-type {
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 180px;
        }
    }
}

// 适配响应式
@media (max-width: 768px) {
    .template-card-list {
        grid-template-columns: 1fr;
    }
}
</style>
