<template>
  <div v-if="!validated" class="validate-container">
    <el-card class="validate-card">
      <h2> <i class="fa-solid fa-masks-theater"></i> 考试信息验证</h2>
      <el-form :model="validateForm" size="large" :label-position="labelPosition" :rules="rules" ref="validateFormRef">
        <el-form-item label="试卷唯一码" prop="paperCode">
          <el-input v-model="validateForm.paperCode" placeholder="请输入试卷唯一码"></el-input>
        </el-form-item>
        <el-form-item label="考号" prop="examineeId">
          <el-input v-model="validateForm.examineeId" placeholder="请输入考号"></el-input>
        </el-form-item>
        <el-form-item label="考生姓名" prop="name">
          <el-input v-model="validateForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <br/>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleValidate">开始考试</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  
  <OnlineExam v-else :examId="examId" :accountId="accountId" @submit="handleExamSubmit"/>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import OnlineExam from './OnlineExam.vue'

import {
  validateAccount , 
  saveAccountScore
} from '@/api/base/im/scene/examPaperJob';

const labelPosition = ref('top')
const route = useRoute()
const router = useRouter()
const validated = ref(false)
const accountId = ref(null)
const examId = ref(route.params.examId)
const validateFormRef = ref(null)

const validateForm = reactive({
  paperCode: '',
  name: '',
  examineeId: ''
})

const rules = {
  paperCode: [{ required: true, message: '请输入试卷唯一码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  examineeId: [{ required: true, message: '请输入考号', trigger: 'blur' }]
}

const handleValidate = async () => {
  try {
    await validateFormRef.value.validate()
    const res = await validateAccount({
      examId: examId.value,
      ...validateForm
    })
    
    if (res.code == 200) {
      accountId.value = res.data
      validated.value = true

      // 保存考生信息到本地Storage
      localStorage.setItem(`exam_user_${accountId.value}`, JSON.stringify({
        accountId: res.data,
        examId: examId.value,
        name: validateForm.name,
        examineeId: validateForm.examineeId,
        paperCode: validateForm.paperCode,
        timestamp: new Date().getTime()
      }))

    } else {
      ElMessage.error(res.message || '验证失败')
    }
  } catch (error) {
    console.error('验证失败:', error)
  }
}

const handleExamSubmit = async (answers, isAutoSubmit = false , pagerInfo) => {
  try {
    const res =  await saveAccountScore({
      accountId: accountId.value,
      examId: examId.value,
      answers,
      isAutoSubmit,
      isAbnormal: false ,  // 正常提交
      questions: pagerInfo.questions
    })

    // 提交成功后清除本地存储
    localStorage.removeItem('exam_account')
    localStorage.removeItem(`exam_answers_${accountId.value}`)
    localStorage.removeItem(`exam_user_${accountId.value}`)

    ElMessage.success('试卷提交成功')

    // 跳转到结果页面
    router.push({ 
      path: '/scene/examPager/onlineExamAnalysis/' + examId.value + '/' + accountId.value,
      query: { 'channelStreamId': res.data }
     })
  } catch (error) {
    ElMessage.error('提交失败: ' + error.message)
  }
}

onMounted(() => {
  // 检查本地是否有保存的考生信息
  const savedAccount = localStorage.getItem(`exam_user_${accountId.value}`)
  if (savedAccount) {
    const account = JSON.parse(savedAccount)
    
    // 检查是否过期（假设考试信息24小时内有效）
    const now = new Date().getTime()
    if (now - account.timestamp < 24 * 60 * 60 * 1000) {
      accountId.value = account.accountId
      examId.value = account.examId
      validateForm.name = account.name
      validateForm.examineeId = account.examineeId
      validateForm.paperCode = account.paperCode
      validated.value = true
    } else {
      // 过期清除
      localStorage.removeItem(`exam_user_${accountId.value}`)
    }
  }
})

</script>

<style scoped>
.validate-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: 100vh;
  background-color: #fafafa;
}

.validate-card {
  width: 500px;
  padding: 30px;
  border-radius: 10px;
  margin-top: 10vh;
}
</style>