<template>
  <div class="review-config">

    <el-row>
      <el-col :span="4">
          <RuleGroupPanel 
              :groupType="'audit'"
              @filterGroup="filterGroup"
              @settingGroup="settingGroup"
              />
      </el-col>
      <el-col :span="20">
        <div style="margin-left:20px;margin-top:5px;">
        <el-button type="primary" text bg size="large" @click="addRule" icon="Plus" class="mb-20">添加审核规则</el-button>

        <el-table :data="rules" style="width: 100%" v-loading="loading">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="ruleName" label="规则名称" width="180" />
          <el-table-column prop="docType" label="文档类型" width="120">
            <template #default="{ row }">
              {{ formatDocType(row.docType) }}
            </template>
          </el-table-column>
          <el-table-column prop="riskLevel" label="风险级别" width="120">
            <template #default="{ row }">
              <el-tag :type="riskTagType(row.riskLevel)">{{ formatRiskLevel(row.riskLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="scope" label="适用范围" width="120">
            <template #default="{ row }">
              <el-tag :type="scopeTagType(row.scope)">{{ formatScope(row.scope) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reviewPosition" label="审核立场" width="120">
            <template #default="{ row }">
              {{ formatReviewPosition(row.reviewPosition) }}
            </template>
          </el-table-column>
          <el-table-column prop="ruleDescription" label="规则描述" />
          <el-table-column label="操作" align="center" width="200">
            <template #default="{ row }">
              <el-button text bg @click="editRule(row)">编辑</el-button>
              <el-button type="danger" text bg @click="deleteRule(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination style="margin-bottom:30px" v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">


      <el-form :model="currentRule" label-width="100px" size="large" :rules="formRules" ref="ruleForm">

        <!-- 所属分组 -->
         <el-form-item label="所属分组" required> 
          <el-select v-model="currentRule.groupId" placeholder="请选择分组" @change="changeGroup"> 
            <el-option v-for="item in groups" :key="item.id" :label="item.name" :value="item.id"> 
            </el-option> 
          </el-select>
         </el-form-item>

        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="currentRule.ruleName" placeholder="请输入规则名称" maxlength="128" show-word-limit />
        </el-form-item>

        <!--
        <el-form-item label="文档类型" prop="docType">
          <el-radio-group v-model="currentRule.docType">
            <el-radio v-for="item in docTypeOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        -->

        <el-form-item label="风险级别" prop="riskLevel">
          <el-radio-group v-model="currentRule.riskLevel">
            <el-radio v-for="item in riskLevelOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="审核立场" prop="reviewPosition">
          <el-radio-group v-model="currentRule.reviewPosition">
            <el-radio v-for="item in reviewPositionOptions" :key="item.value" :label="item.value">{{ item.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="适用范围" prop="scope">
          <el-radio-group v-model="currentRule.scope">
            <el-radio v-for="item in scopeOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="规则内容" prop="ruleContent">
          <el-input type="textarea" v-model="currentRule.ruleContent" :rows="4" placeholder="请输入规则内容" />
        </el-form-item>

        <el-form-item label="规则描述" prop="ruleDescription">
          <el-input type="textarea" v-model="currentRule.ruleDescription" :rows="2" placeholder="请输入规则描述"
            maxlength="500" show-word-limit />
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
import {
  listRules as getRules,
  addRule as createRule,
  updateRule,
  delRule
} from '@/api/smart/scene/contentFormatterRule'

import RuleGroupPanel from "./LayoutGroup"

const loading = ref(true)
const rules = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加审核规则')
const ruleForm = ref(null)

const { proxy } = getCurrentInstance();

const props = defineProps({
  groupType: {
    type: String ,
    required: true
  }
})

const dateRange = ref([]);
const groups = ref([])
const total = ref(0);

// 表单验证规则
const formRules = reactive({
  ruleName: [
    { required: true, message: '规则名称不能为空', trigger: 'blur' },
    { max: 128, message: '长度不能超过128个字符', trigger: 'blur' }
  ],
  docType: [{ required: true, message: '请选择文档类型', trigger: 'change' }],
  riskLevel: [{ required: true, message: '请选择风险级别', trigger: 'change' }],
  reviewPosition: [{ required: true, message: '请选择审核立场', trigger: 'change' }],
  scope: [{ required: true, message: '请选择适用范围', trigger: 'change' }],
  ruleContent: [{ required: true, message: '规则内容不能为空', trigger: 'blur' }],
  ruleDescription: [{ max: 500, message: '长度不能超过500个字符', trigger: 'blur' }]
})

// 当前编辑的规则
const currentRule = reactive({
  id: null,
  ruleName: '',
  docType: '',
  riskLevel: '',
  reviewPosition: '',
  scope: '',
  ruleContent: '',
  ruleDescription: ''
})

// 选项配置
const docTypeOptions = [
  { label: 'Word', value: 'word' },
  { label: 'Excel', value: 'excel' },
  { label: 'PPT', value: 'ppt' },
  { label: 'PDF', value: 'pdf' }
]

const riskLevelOptions = [
  { label: '低', value: 'low' },
  { label: '中', value: 'medium' },
  { label: '高', value: 'high' }
]

const reviewPositionOptions = [
  { label: '严格', value: 'strict' },
  { label: '适中', value: 'moderate' },
  { label: '宽松', value: 'lenient' }
]

const scopeOptions = [
  { label: '公开', value: 'public' },
  { label: '私有', value: 'private' },
  { label: '组织', value: 'organization' }
]


const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  templateName: undefined,
  promptDesc: undefined,
  catalogId: undefined
})

// 获取规则列表
const fetchRules = async () => {
  try {
    loading.value = true
    const res = await getRules(proxy.addDateRange(queryParams.value, dateRange.value))
    rules.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取规则列表失败:', error)
    ElMessage.error('获取规则列表失败')
  } finally {
    loading.value = false
  }
}

// 添加规则
const addRule = () => {
  dialogTitle.value = '添加审核规则'
  resetForm()
  dialogVisible.value = true
}

// 编辑规则
const editRule = (rule) => {
  dialogTitle.value = '编辑审核规则'
  Object.assign(currentRule, rule)
  dialogVisible.value = true
}

// 删除规则
const deleteRule = async (rule) => {
  try {
    await ElMessageBox.confirm(`确认删除规则 "${rule.ruleName}" 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await delRule(rule.id)
    ElMessage.success('删除成功')
    fetchRules()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  try {
    await ruleForm.value.validate()

    if (dialogTitle.value === '添加审核规则') {
      await createRule(currentRule)
      ElMessage.success('添加成功')
    } else {
      await updateRule(currentRule)
      ElMessage.success('更新成功')
    }

    dialogVisible.value = false
    fetchRules()
  } catch (error) {
    console.error('保存失败:', error)
    if (error !== 'cancel') {
      ElMessage.error('保存失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  currentRule.id = null
  currentRule.ruleName = ''
  currentRule.docType = ''
  currentRule.riskLevel = ''
  currentRule.reviewPosition = ''
  currentRule.scope = ''
  currentRule.ruleContent = ''
  currentRule.ruleDescription = ''
  if (ruleForm.value) {
    ruleForm.value.resetFields()
  }
}

// 格式化显示
const formatDocType = (type) => {
  const item = docTypeOptions.find(item => item.value === type)
  return item ? item.label : type
}

const formatRiskLevel = (level) => {
  const item = riskLevelOptions.find(item => item.value === level)
  return item ? item.label : level
}

const formatReviewPosition = (position) => {
  const item = reviewPositionOptions.find(item => item.value === position)
  return item ? item.label : position
}

const formatScope = (scope) => {
  const item = scopeOptions.find(item => item.value === scope)
  return item ? item.label : scope
}

// 标签类型
const riskTagType = (level) => {
  switch (level) {
    case 'low': return 'success'
    case 'medium': return 'warning'
    case 'high': return 'danger'
    default: return 'info'
  }
}

const scopeTagType = (scope) => {
  switch (scope) {
    case 'public': return 'success'
    case 'private': return 'danger'
    case 'organization': return 'warning'
    default: return 'info'
  }
}


// 通过分组id查询模板
const filterGroup = (groupId) => {
  queryParams.value.groupId = groupId;
  fetchRules();
}

// 设置分组
const settingGroup = (groupsArr) => {
  console.log("groups = " + groupsArr);
  groups.value = groupsArr || [];
}

// 生命周期钩子
onMounted(() => {
  fetchRules()
})

</script>

<style lang="scss" scoped>
.review-config {
  padding: 0px;

  .mb-20 {
    margin-bottom: 20px;
  }

  .el-radio-group {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    .el-radio {
      margin-right: 0;
    }
  }
}
</style>