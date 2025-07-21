<template>
  <div class="parse-config">
    <el-button type="primary" @click="addOfficeTool" class="mb-20">添加Office工具</el-button>

    <el-table :data="officeTools" border style="width: 100%">
      <el-table-column prop="toolName" label="工具名称" width="180" />
      <el-table-column prop="toolPath" label="工具路径" />
      <el-table-column prop="requestToken" label="请求Token" width="200" />
      <el-table-column prop="dataScope" label="工具范围" width="150">
        <template #default="{row}">
          <el-tag :type="scopeTagType(row.dataScope)">{{ row.dataScope }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{row}">
          <el-button size="small" @click="editOfficeTool(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteOfficeTool(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form :model="currentTool" label-width="120px">
        <el-form-item label="工具名称">
          <el-input v-model="currentTool.toolName" placeholder="请输入工具名称" />
        </el-form-item>
        <el-form-item label="工具路径">
          <el-input v-model="currentTool.toolPath" placeholder="请输入工具路径" />
        </el-form-item>
        <el-form-item label="请求Token">
          <el-input v-model="currentTool.requestToken" placeholder="请输入请求Token" />
        </el-form-item>
        <el-form-item label="工具范围">
          <el-select v-model="currentTool.dataScope" placeholder="请选择工具范围">
            <el-option label="公开" value="public" />
            <el-option label="私有" value="private" />
            <el-option label="组织" value="organization" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOfficeTool">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const officeTools = ref([
  { toolName: 'Office转换工具', toolPath: 'http://localhost:8080/office', requestToken: 'token123', dataScope: 'public' },
  { toolName: 'PDF解析服务', toolPath: 'http://localhost:8080/pdf', requestToken: 'token456', dataScope: 'organization' }
])

const dialogVisible = ref(false)
const dialogTitle = ref('添加Office工具')
const currentTool = reactive({
  toolName: '',
  toolPath: '',
  requestToken: '',
  dataScope: 'public'
})

const addOfficeTool = () => {
  dialogTitle.value = '添加Office工具'
  currentTool.toolName = ''
  currentTool.toolPath = ''
  currentTool.requestToken = ''
  currentTool.dataScope = 'public'
  dialogVisible.value = true
}

const editOfficeTool = (tool) => {
  dialogTitle.value = '编辑Office工具'
  Object.assign(currentTool, tool)
  dialogVisible.value = true
}

const deleteOfficeTool = (tool) => {
  officeTools.value = officeTools.value.filter(item => item !== tool)
}

const saveOfficeTool = () => {
  if (dialogTitle.value === '添加Office工具') {
    officeTools.value.push({...currentTool})
  }
  dialogVisible.value = false
}

const scopeTagType = (scope) => {
  switch(scope) {
    case 'public': return 'success'
    case 'private': return 'danger'
    case 'organization': return 'warning'
    default: return 'info'
  }
}
</script>

<style lang="scss" scoped>
.parse-config {
  .mb-20 {
    margin-bottom: 20px;
  }
}
</style>