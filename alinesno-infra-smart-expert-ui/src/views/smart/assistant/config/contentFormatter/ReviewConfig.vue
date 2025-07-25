<template>
  <div class="review-config">

    <el-row>
      <el-col :span="4">
          <RuleGroupPanel 
              ref="ruleGroupPanelRef"
              :groupType="'audit'"
              @filterGroup="filterGroup"
              @refreshRules="refreshRules"
              @settingGroup="settingGroup"
              />
      </el-col>
      <el-col :span="20">
        <div style="margin-left:20px;margin-top:5px;">

          <div>
            <el-button type="primary" text bg size="large" @click="addRule" icon="Plus" class="mb-20">添加审核规则</el-button>

            <!-- 添加重置-->
            <el-button type="primary" text bg size="large" icon="Refresh" @click="refreshLayout" class="mb-20">刷新</el-button>

            <el-button type="warning" text bg size="large" icon="Upload" @click="handleImport" class="mb-20" v-hasPermi="['system:user:import']">导入</el-button>
          </div>

        <el-table :data="rules" style="width: 100%" v-loading="loading">
          <el-table-column type="index" label="序号" align="center" width="50" />
          <el-table-column prop="ruleName" label="规则名称" width="180">
                <template #default="{ row }">
                    <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
                      {{ row.ruleName}}
                    </div>
                </template>
          </el-table-column>

          <el-table-column prop="docType" align="center" label="所属分组" width="180">
            <template #default="{ row }">
                {{ getGroupNameById(row.groupId) }}
            </template>
          </el-table-column> 
          <el-table-column prop="riskLevel" align="center" label="风险" width="90">
            <template #default="{ row }">
              <el-tag :type="riskTagType(row.riskLevel)">{{ formatRiskLevel(row.riskLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reviewPosition" align="center" label="立场" width="120">
            <template #default="{ row }">
              {{ formatReviewPosition(row.reviewPosition) }}
            </template>
          </el-table-column>

          <el-table-column prop="ruleContent" label="规则描述" />
          <el-table-column label="操作" align="center" width="200">
            <template #default="{ row }">
              <el-button text bg @click="editRule(row)">编辑</el-button>
              <el-button type="danger" text bg @click="deleteRule(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination style="margin-bottom:30px" v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="fetchRules" />
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

        <el-form-item label="规则内容" prop="ruleContent">
          <el-input type="textarea" v-model="currentRule.ruleContent" :rows="7" placeholder="请输入规则内容" />
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

        <!-- 模板导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body :before-close="handleClose">
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport" 
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xls、xlsx格式文件。</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="upload.isUploading" @click="submitFileForm">确 定</el-button>
          <el-button :disabled="upload.isUploading" @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from "@/utils/auth";
import {
  listRules as getRules,
  addRule as createRule,
  updateRule,
  delRule
} from '@/api/smart/scene/contentFormatterRule'

import RuleGroupPanel from "./LayoutGroup"

const ruleGroupPanelRef = ref(null)
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

/*** 模板导入参数 */
const upload = reactive({
  // 是否显示弹出层（模板导入）
  open: false,
  // 弹出层标题（模板导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的模板数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 头像上传的地址
  headerUrl: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/contentFormatterRule/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

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

const getGroupNameById = (groupId) => {
  const group = groups.value.find(g => g.id === groupId)
  return group ? group.name : '未分组'
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
// const formatDocType = (type) => {
//   const item = docTypeOptions.find(item => item.value === type)
//   return item ? item.label : type
// }

const formatRiskLevel = (level) => {
  const item = riskLevelOptions.find(item => item.value === level)
  return item ? item.label : level
}

const formatReviewPosition = (position) => {
  const item = reviewPositionOptions.find(item => item.value === position)
  return item ? item.label : position
}

// const formatScope = (scope) => {
//   const item = scopeOptions.find(item => item.value === scope)
//   return item ? item.label : scope
// }

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

// 更新规则信息
const refreshRules = () => {
  queryParams.value.groupId = null;
  fetchRules()
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "审核规则导入";
  upload.open = true;
};

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};

const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);

  const responseData = response.data;

  // 构建导入结果信息
  let message = '';
  if (responseData.failedCount > 0) {
    message += `<div class="import-summary">
                  <p>导入成功: ${responseData.successCount} 条</p>
                  <p class="text-danger">导入失败: ${responseData.failedCount} 条</p>
                </div>
                <div class="failed-list mt-3">
                  <h4 class="mb-2">失败详情</h4>
                  <ul class="list-group">`;

    responseData.failedList.forEach(item => {
      message += `<li class="list-group-item list-group-item-danger">
                    <div class="font-weight-bold">规则: ${item.ruleName} (分组: ${item.failReason.groupName || '无'})</div>
                    <div class="text-muted">原因: ${item.failReason.error || item.failReason.message || '未知错误'}</div>
                  </li>`;
    });

    message += `</ul></div>`;
  } else {
    message += `<div class="text-success">全部 ${responseData.successCount} 条数据导入成功</div>`;
  }

  proxy.$alert(`<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>${message}</div>`,
    "导入结果",
    { dangerouslyUseHTMLString: true });

  ruleGroupPanelRef.value.refreshGroupType();
  fetchRules();
};

const handleClose = (done) => {
  // 如果正在上传文件，则阻止关闭对话框
  if (upload.isUploading) {
    proxy.$modal.msgError("正在上传文件，请稍后再试");
    return;
  }
}

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
};

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