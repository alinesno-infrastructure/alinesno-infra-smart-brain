<template>
  <div class="group-management">
    <el-button type="primary" text bg size="large" @click="addGroup" icon="Plus" class="mb-20">添加分组</el-button>

    <el-table :data="groups" style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="groupType" label="分组类型" width="200" >
        <template #default="{ row }">
          {{ groupTypeOptions.find(item => item.value === row.groupType)?.label || row.groupType }}
        </template>
      </el-table-column> 
      <el-table-column prop="icon" label="分组名称">
        <template #default="{ row }">
          <div style="display: flex;align-items: center;gap: 10px;">
            <el-icon v-if="row.icon">
              <component :is="row.icon" /> 
            </el-icon>
            <span v-else>无图标</span>
            {{ row.name}}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="{ row }">
          <el-button text bg @click="editGroup(row)">编辑</el-button>
          <el-button type="danger" text bg @click="deleteGroup(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination style="margin-bottom:30px" v-show="total > 0" 
            :total="total" 
            v-model:page="queryParams.pageNum" 
            v-model:limit="queryParams.pageSize" 
            @pagination="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="40%">
      <el-form :model="currentGroup" label-width="100px" size="large" :rules="formRules" ref="groupForm">

        <el-form-item label="分组类型" prop="groupType">
          <el-radio-group v-model="currentGroup.groupType">
            <el-radio 
              v-for="item in groupTypeOptions" 
              :key="item.value" 
              :label="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="分组名称" prop="name">
          <el-input v-model="currentGroup.name" placeholder="请输入分组名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="分组图标" prop="icon">
          <el-select v-model="currentGroup.icon" placeholder="请选择图标" style="width: 100%">
            <el-option
              v-for="item in iconOptions"
              :key="item"
              :label="item"
              :value="item"
            >
              <span style="float: left">
                <el-icon><component :is="item" /></el-icon>
              </span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="currentGroup.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as icons from '@element-plus/icons-vue'
import {
  listGroups,
  addGroup as apiAddGroup,  // Rename here
  updateGroup,
  delGroup
} from '@/api/smart/scene/contentFormatterGroup'

const loading = ref(true)
const groups = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加分组')
const groupForm = ref(null)
const dateRange = ref([]); 
const total = ref(0);

const { proxy } = getCurrentInstance();

// 分组类型选项
const groupTypeOptions = ref([
  { value: 'layout', label: '排版分组' },
  { value: 'audit', label: '审核分组' }
])

// 表单验证规则
const formRules = reactive({
  groupType: [
    { required: true, message: '请选择分组类型', trigger: 'change' }
  ],
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
  groupType: '',
  name: '',
  icon: '',
  sort: 0
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  templateName: undefined,
  promptDesc: undefined,
  catalogId: undefined
})

// 图标选项
const iconOptions = Object.keys(icons)

// 生命周期钩子
onMounted(() => {
  fetchGroups()
})

// 获取分组列表
const fetchGroups = async () => {
  try {
    loading.value = true
    const res = await listGroups(proxy.addDateRange(queryParams.value, dateRange.value))
    groups.value = res.rows || []
    total.value = res.total || 0 
  } catch (error) {
    console.error('获取分组列表失败:', error)
    ElMessage.error('获取分组列表失败')
  } finally {
    loading.value = false
  }
}

// 添加分组
const addGroup = () => {
  dialogTitle.value = '添加分组'
  resetForm()
  dialogVisible.value = true
}

// 编辑分组
const editGroup = (group) => {
  dialogTitle.value = '编辑分组'
  Object.assign(currentGroup, group)
  dialogVisible.value = true
}

// 删除分组
const deleteGroup = async (group) => {
  try {
    await ElMessageBox.confirm(`确认删除分组 "${group.name}" 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await delGroup(group.id)
    ElMessage.success('删除成功')
    fetchGroups()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  try {
    await groupForm.value.validate()

    if (currentGroup.id) {
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

// 重置表单
const resetForm = () => {
  currentGroup.id = null
  currentGroup.groupType = ''
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

  .mb-20 {
    margin-bottom: 20px;
  }
}
</style>