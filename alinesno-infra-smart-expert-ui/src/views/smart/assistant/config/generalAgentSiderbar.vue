<template>
  <div class="category-sidebar">

    <div class="category-header" style="">  
        <span>
          模板分类
        </span>
                <!-- 添加分类按钮 -->
              <el-button class="add-category" type="primary" text bg size="large" @click="addCategory()">
                <el-icon>
                  <Plus />
                </el-icon> 添加分类
              </el-button> 


    </div>

    <!-- 分类列表 -->
    <div  
      class="category-item" 
      :class="{ 'active': activeCategory === '' }"
      @click="selectCategory('')">
      <i class="fa-solid fa-house"></i> &nbsp; 所有场景分类 
    </div>
    <div 
      v-for="category in categories" 
      :key="category.id"
      class="category-item"
      :class="{ 'active': activeCategory === category.id }"
      @click="selectCategory(category.id)">
      <i :class="category.icon"></i> &nbsp;{{ category.name }}
      <div class="category-actions" v-if="activeCategory === category.id">
        <el-button 
          size="small" 
          circle 
          @click.stop="editCategory(category)"
          class="action-button"
        >
          <el-icon><Edit /></el-icon>
        </el-button>
        <el-button 
          size="small" 
          circle 
          @click.stop="deleteCategory(category.id)"
          class="action-button"
          v-if="category.id !== 'all'"
        >
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>

    <div v-if="categories.length == 0">
      <el-empty  
         image-size="100"
         description="当前并没有集成分类，请点击右上角添加分类。" />
    </div>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog 
      v-model="showAddModal" 
      :title="editingCategory ? '编辑分类' : '添加分类'"
      width="500px"
    >
      <el-form :model="categoryForm" size="large" :rules="rules" label-width="80px" ref="formRef">
        <el-form-item label="图标" prop="icon">
          <el-select v-model="categoryForm.icon" placeholder="请选择图标">
            <el-option
              v-for="icon in iconOptions"
              :key="icon.value"
              :label="icon.label"
              :value="icon.value"
            >
              <span style="margin-right: 10px;">
                <i :class="icon.value"></i>
              </span>
              <span>{{ icon.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称"  prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="分类描述"  prop="remark">
          <el-input v-model="categoryForm.remark" placeholder="请输入分类描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="showAddModal = false">取消</el-button>
          <el-button size="large" type="primary" @click="submitForm">
            {{ editingCategory ? '更新' : '确认' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, defineProps, defineExpose , defineEmits } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import {
  addTemplateGroup , 
  updateTemplateGroup , 
  getAllTemplateGroup ,
  deleteTemplateGroup
} from "@/api/smart/scene/generalAgentTemplate"

const emit = defineEmits(['update:activeCategory', 'update:categories'])

// 表单引用
const formRef = ref(null)
const categories = ref([])
const activeCategory = ref(null)

// 分类表单
const showAddModal = ref(false)
const editingCategory = ref(null)
const categoryForm = ref({
  id: '',
  name: '',
  icon: ''
})

// 表单校验规则
const rules = ref({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  remark: [
    { required: true, message: '请输入分类描述', trigger: 'blur' } 
  ],
  icon: [
    { required: true, message: '请选择分类图标', trigger: 'change' }
  ]
})

// 图标选项
const iconOptions = ref([
  { value: 'fa-solid fa-layer-group', label: '分组' },
  { value: 'fa-solid fa-chart-line', label: '图表' },
  { value: 'fa-solid fa-pen-fancy', label: '编辑' },
  { value: 'fa-solid fa-headset', label: '客服' },
  { value: 'fa-solid fa-robot', label: '机器人' },
  { value: 'fa-solid fa-gear', label: '设置' },
  { value: 'fa-solid fa-database', label: '数据库' },
  { value: 'fa-solid fa-users', label: '用户' }
])

// 选择分类
const selectCategory = (id) => {
  emit('update:activeCategory', id)
  activeCategory.value = id ;
}

// 编辑分类
const editCategory = (category) => {
  editingCategory.value = category.id
  categoryForm.value = { ...category }
  showAddModal.value = true
}

// 删除分类
const deleteCategory = (id) => {
  ElMessageBox.confirm('确定要删除此分类吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => { 
    
    deleteTemplateGroup(id).then(res => {
      ElMessage.success('分类删除成功.');
      handleGetAllTemplateGroup();
    })
  
  }).catch(() => {
    // 取消操作
  })
}

// 提交表单校验
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      submitCategory()
    }
  })
}

// 提交分类
const submitCategory = () => {
 
  console.log("categoryForm.value = " + JSON.stringify(categoryForm.value))

  if(categoryForm.value.id){
    updateTemplateGroup(categoryForm.value).then(res => {
      ElMessage.success('分类更新成功');
      handleGetAllTemplateGroup();
    })
  }else {
    addTemplateGroup(categoryForm.value).then(res => {
      ElMessage.success('分类添加成功');
      handleGetAllTemplateGroup();
    })
  }
 
  showAddModal.value = false
  resetForm()
}

const addCategory = () => {
  resetForm();
  showAddModal.value = true
}

const handleGetAllTemplateGroup = () => {
  getAllTemplateGroup().then(res => {
    console.log('res = ' + res);
      categories.value = res.data ;
  })
}

// 重置表单
const resetForm = () => {
  categoryForm.value = {
    id: '',
    name: '',
    remark: '',
    icon: ''
  }
  editingCategory.value = null
}

// 获取到大模型能力信息
const getAllTemplate = () => {
  return categories.value; 
}

handleGetAllTemplateGroup();

defineExpose({
  addCategory, 
  getAllTemplate
})

</script>

<style lang="scss" scoped>
.category-sidebar {
  width: 300px;
  flex-shrink: 0;
  background-color: var(--el-bg-color);
  border-radius: var(--el-border-radius-base);
  padding: 8px 0;
  padding-top: 0px;

  .category-item {
    position: relative;
    padding: 12px 16px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    font-size: 14px;
    display: flex;
    align-items: center;
    background-color: #fafafa;
    margin-bottom: 6px;
    border-left: 3px solid #fafafa;

    &:hover {
      background-color: #f5f5f5;
      color: var(--el-color-primary);
      
      .category-actions {
        display: flex;
      }
    }

    &.active {
      font-weight: 500;
      background-color: #f5f5f5;
      border-radius: 5px;
      color: var(--el-color-primary);
      border-left: 3px solid #1d75b0;
    }

    .category-actions {
      display: none;
      position: absolute;
      right: 8px;
      gap: 4px;

      .action-button {
        padding: 4px;
        background-color: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(4px);

        &:hover {
          background-color: var(--el-bg-color);
        }
      }
    }
  }


  .add-category {
    padding: 12px 16px;
    cursor: pointer;
    color: var(--el-color-primary);
    font-size: 14px; 
    display: flex;
    align-items: center;
    gap: 8px;

    &:hover {
      background-color: #f5f5f5;
    }
  }

.category-header{
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    align-items: center;
    font-size: 14px;
}

}
</style>