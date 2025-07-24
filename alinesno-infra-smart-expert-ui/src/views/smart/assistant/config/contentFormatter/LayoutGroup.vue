<template>
  <div class="group-management">
    <div class="header">
      <h3>分组管理</h3>
      <el-button type="primary" size="large" text bg @click="showAddDialog">
        <el-icon>
          <Plus />
        </el-icon> 添加分组
      </el-button>
    </div>

    <div class="group-list">
      <div v-for="group in groups" :key="group.id" class="group-item" 
        @click="selectGroup(group)" 
        @mouseenter="hoverGroupId = group.id"
        @mouseleave="hoverGroupId = null">
        <span>
         <i v-if="group.dataScope === 'public'" class="fa-solid fa-globe"></i>  
         <i v-if="group.dataScope === 'org'" class="fa-solid fa-truck-plane"></i>  
         {{ group.name }}
        </span>
        <div v-show="hoverGroupId === group.id" class="actions">
          <el-button type="primary" text bg size="small" @click="showEditDialog(group)">
            <el-icon>
              <Edit />
            </el-icon>
          </el-button>
          <el-button type="danger" text bg size="small" @click="confirmDelete(group.id)">
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑分组' : '添加分组'" width="30%">
      <el-form ref="groupForm" :model="currentGroup" :rules="formRules" size="large" label-width="80px">
        <el-form-item label="数据范围" prop="dataScope">
          <el-radio-group v-model="currentGroup.dataScope">
            <el-radio v-for="item in workplaceTypesArr" style="margin-top: 10px;margin-bottom: 10px;" :key="item.id"
              :value="item.id" :label="item.id" size="large">
              <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                <span style="font-size:15px;font-weight: bold;">
                  <i :class="item.icon"></i> {{ item.name }}
                </span>
                <span style="color:#a5a5a5">
                  {{ item.desc }}
                </span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="分组名称" prop="name">
          <el-input v-model="currentGroup.name" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="currentGroup.sort" :min="0" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="currentGroup.icon" placeholder="请选择图标" style="width: 100%">
            <el-option v-for="icon in iconOptions" :key="icon" :label="icon" :value="icon">
              <el-icon :size="16">
                <component :is="icon" />
              </el-icon>
              <span style="margin-left: 10px">{{ icon }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import * as icons from '@element-plus/icons-vue'
import {
  listGroups,
  addGroup as apiAddGroup,
  updateGroup,
  delGroup,
  getGroup
} from '@/api/smart/scene/contentFormatterGroup'

const emit = defineEmits(['filterGroup' , 'settingGroup'])
const props = defineProps({
  groupType: {
    type: String,
    required: true
  }
})

const { proxy } = getCurrentInstance()

const workplaceTypesArr = [
  { "id": "public", "name": "公开", "icon": "fa-solid fa-globe", "desc": "公开审核清单只能选择公共智能体、公开频道、公开场景" },
  { "id": "org", "name": "组织", "icon": "fa-solid fa-truck-plane", "desc": "组织审核清单可以选择公共和组织内的智能体、频道和场景" }
];

const loading = ref(true)
const groups = ref([])
const hoverGroupId = ref(null)
const dialogVisible = ref(false)
const isEditing = ref(false)
const groupForm = ref(null)

// 分组类型选项
// const groupTypeOptions = ref([
//   { value: 'layout', label: '排版分组' },
//   { value: 'audit', label: '审核分组' }
// ])

// 图标选项
const iconOptions = Object.keys(icons)

// 表单验证规则
const formRules = reactive({
  name: [
    { required: true, message: '分组名称不能为空', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '排序不能为空', trigger: 'blur' }
  ]
})

// 当前编辑的分组
const currentGroup = reactive({
  id: null,
  groupType: props.groupType ,
  name: '',
  icon: '',
  sort: 0
})

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  groupType: props.groupType ,
  templateName: undefined,
  promptDesc: undefined,
  catalogId: undefined
})

// 生命周期钩子
onMounted(() => {
  fetchGroups()
})

// 获取分组列表
const fetchGroups = async () => {
  try {
    loading.value = true
    const res = await listGroups(queryParams.value)
    groups.value = res.rows || []
    emit('settingGroup', groups.value)
  } catch (error) {
    console.error('获取分组列表失败:', error)
    ElMessage.error('获取分组列表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEditing.value = false
  resetForm()
  dialogVisible.value = true
}

const showEditDialog = (group) => {
  isEditing.value = true
  getGroup(group.id).then(res => {
    Object.assign(currentGroup, res.data)
    dialogVisible.value = true
  })
}

// 提交表单
const submitForm = async () => {
  try {
    await groupForm.value.validate()

    if (isEditing.value) {
      // 更新现有分组
      await updateGroup(currentGroup)
      ElMessage.success('更新成功')
    } else {
      // 添加新分组
      await apiAddGroup(currentGroup)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    fetchGroups()
  } catch (error) {
    console.error('保存失败:', error)
    if (error !== 'cancel') {
      ElMessage.error('保存失败')
    }
  }
}

const confirmDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分组吗？删除后无法恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await delGroup(id)
    ElMessage.success('分组已删除')
    fetchGroups()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 选择分组
const selectGroup = (group) => {
  console.log('选择分组:', group)
  emit('filterGroup', group.id)
}

// 重置表单
const resetForm = () => {
  currentGroup.id = null
  currentGroup.groupType = props.groupType 
  currentGroup.name = ''
  currentGroup.icon = ''
  currentGroup.sort = 0
  if (groupForm.value) {
    groupForm.value.resetFields()
  }
}
</script>

<style lang="scss" scoped>
.group-management {
  padding: 0px;
  max-width: 800px;
  margin: 5px auto;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    margin-top: 0px;

    h3 {
      font-size: 15px;
      margin: 0;
    }
  }

  .group-list {
    .group-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      cursor: pointer;
      padding: 12px 15px;
      margin-bottom: 8px;
      background-color: #f5f7fa;
      transition: all 0.3s;
      font-size: 14px;
      height: 45px;
      color: #444;
      border-radius: 5px;

      &:hover {
        background-color: #e6f0f8;
      }

    }
  }

  :deep(.el-button--primary) {
    background-color: #1d75b0;
    border-color: #1d75b0;

    &:hover {
      background-color: #166092;
      border-color: #166092;
    }
  }

  :deep(.el-divider) {
    margin: 10px 0 20px;
  }
}
</style>