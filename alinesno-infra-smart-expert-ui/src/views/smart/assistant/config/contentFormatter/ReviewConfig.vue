<template>
  <div class="review-config">
    <el-button type="primary" text bg size="large" @click="addRule" icon="Plus" class="mb-20">添加审核规则</el-button>

    <el-table :data="rules" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="name" label="规则名称" width="180" />
      <el-table-column prop="docType" label="文档类型" width="120" />
      <el-table-column prop="riskLevel" label="风险级别" width="120">
        <template #default="{row}">
          <el-tag :type="riskTagType(row.riskLevel)">{{ row.riskLevel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="scope" label="适用范围" width="120">
        <template #default="{row}">
          <el-tag :type="scopeTagType(row.scope)">{{ row.scope }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="规则描述" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="{row}">
          <el-button text bg @click="editRule(row)">编辑</el-button>
          <el-button type="danger" text bg @click="deleteRule(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form :model="currentRule" label-width="80px" size="large">
        <el-form-item label="规则名称">
          <el-input v-model="currentRule.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="文档类型">
          <el-select v-model="currentRule.docType" placeholder="请选择文档类型">
            <el-option label="Word" value="word" />
            <el-option label="Excel" value="excel" />
            <el-option label="PPT" value="ppt" />
            <el-option label="PDF" value="pdf" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险级别">
          <el-select v-model="currentRule.riskLevel" placeholder="请选择风险级别">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用范围">
          <el-select v-model="currentRule.scope" placeholder="请选择适用范围">
            <el-option label="公开" value="public" />
            <el-option label="私有" value="private" />
            <el-option label="组织" value="organization" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则描述">
          <el-input type="textarea" v-model="currentRule.description" :rows="4" placeholder="请输入规则描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const rules = ref([
  {
    id: 1,
    name: '敏感词检测',
    docType: 'word',
    riskLevel: 'high',
    scope: 'public',
    description: '检测文档中是否包含敏感词汇'
  },
  {
    id: 2,
    name: '格式规范检查',
    docType: 'word',
    riskLevel: 'medium',
    scope: 'organization',
    description: '检查文档格式是否符合规范要求'
  }
])

const dialogVisible = ref(false)
const dialogTitle = ref('添加审核规则')
const currentRule = reactive({
  name: '',
  docType: 'word',
  riskLevel: 'medium',
  scope: 'public',
  description: ''
})

const addRule = () => {
  dialogTitle.value = '添加审核规则'
  currentRule.name = ''
  currentRule.docType = 'word'
  currentRule.riskLevel = 'medium'
  currentRule.scope = 'public'
  currentRule.description = ''
  dialogVisible.value = true
}

const editRule = (rule) => {
  dialogTitle.value = '编辑审核规则'
  Object.assign(currentRule, rule)
  dialogVisible.value = true
}

const deleteRule = (rule) => {
  rules.value = rules.value.filter(item => item !== rule)
}

const saveRule = () => {
  if (dialogTitle.value === '添加审核规则') {
    rules.value.push({...currentRule, id: Date.now()})
  }
  dialogVisible.value = false
}

const riskTagType = (level) => {
  switch(level) {
    case 'low': return 'success'
    case 'medium': return 'warning'
    case 'high': return 'danger'
    default: return 'info'
  }
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
.review-config {
  .mb-20 {
    margin-bottom: 20px;
  }
}
</style>