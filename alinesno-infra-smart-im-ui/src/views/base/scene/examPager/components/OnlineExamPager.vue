<template>
  <div v-if="!validated" class="validate-container">
    <el-card class="validate-card">
      <h2> <i class="fa-solid fa-masks-theater"></i> {{ examInfo?.examName }} </h2>
      <!-- 添加考试信息卡片 -->
      <el-card shadow="never" class="exam-info-card">
        <div style="display: flex;">
          <div class="exam-info-item">
            <i class="fa-solid fa-clock"></i>
            <span>时间：{{ examInfo?.examDuration }}分钟</span>
          </div>
          <div class="exam-info-item">
            <i class="fa-solid fa-star"></i>
            <span>总分：{{ examInfo?.totalScore }}分</span>
          </div>
          <div class="exam-info-item">
            <i class="fa-solid fa-percent"></i>
            <span>及格：{{ examInfo?.passingScore || (examInfo?.totalScore * 0.6).toFixed(0) }}分</span>
          </div>
        </div>
      </el-card>
      <el-form :model="validateForm" size="large" :label-position="labelPosition" :rules="rules" ref="validateFormRef">
        <el-form-item label="试卷唯一码" prop="paperCode">
          <el-input v-model="validateForm.paperCode" placeholder="请输入试卷唯一码"></el-input>
        </el-form-item>
        <el-form-item label="考生号" prop="examineeId">
          <el-input v-model="validateForm.examineeId" placeholder="请输入考生号"></el-input>
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
  getExamInfo,
  saveAccountScore
} from '@/api/base/im/scene/examPaperJob';

const labelPosition = ref('top')
const route = useRoute()
const router = useRouter()
const validated = ref(false)
const accountId = ref(null)
const examId = ref(route.params.examId)
const validateFormRef = ref(null)

const examInfo = ref(null)

const validateForm = reactive({
  paperCode: '',
  name: '',
  examineeId: ''
})

const rules = {
  paperCode: [
    { required: true, message: '请输入试卷唯一码', trigger: 'blur' },
    { 
      pattern: /^\d{6}$/, 
      message: '试卷唯一码必须是6位数字', 
      trigger: 'blur' 
    }
  ],
  examineeId: [
    { required: true, message: '请输入考号', trigger: 'blur' }
  ]
}

const handleValidate = async () => {
  try {
    await validateFormRef.value.validate()
    const res = await validateAccount({
      examId: examId.value,
      ...validateForm
    })
    
    if (res.code == 200) {

      accountId.value = res.data.id
      let examStatus = res.examStatus ; 

      if(res.isExistScore && (examStatus == 'review' || examStatus == 'examination_end' || examStatus == 'review_end'  )){  // 用户已经做过考试

        let channelStreamId = res.channelStreamId ;
        router.push({ 
          path: '/scene/examPager/onlineExamAnalysis/' + examId.value + '/' + accountId.value,
          query: { 'channelStreamId': channelStreamId}
        })
        return ; 
      }

      validateForm.name = res.data.name
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

// 获取考试信息
const handleGetExamInfo = async () => {
  const res = await getExamInfo(examId.value)
  examInfo.value = res.data ;
}

onMounted(() => {

  handleGetExamInfo() ;

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

<style lang="scss" scoped>
.validate-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  padding: 20px;
  background-color: #fafafa;

  .validate-card {
    width: 100%;
    max-width: 500px;
    padding: 20px;
    border-radius: 10px;
    margin-top: 5vh;
    box-sizing: border-box;

    h2 {
      font-size: clamp(1.5rem, 4vw, 2rem);
      text-align: center;
      margin-bottom: 15px;
      
      i {
        margin-right: 8px;
      }
    }
  }
}

.exam-info-card {
  margin: 20px 0;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  
  div {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
  
  .exam-info-item {
    margin: 5px 0;
    font-size: clamp(12px, 3vw, 14px);
    color: #555;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    i {
      margin-right: 5px;
      min-width: 16px;
      color: #409eff;
    }
  }
}

.responsive-input {
  :deep(.el-input__inner) {
    height: 48px;
    font-size: 16px;
    
    @media (max-width: 480px) {
      height: 44px;
    }
  }
}

.submit-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  margin-top: 10px;
  
  @media (max-width: 480px) {
    height: 44px;
  }
}

.el-form-item {
  margin-bottom: 20px;
  
  @media (max-width: 480px) {
    margin-bottom: 15px;
  }
}

@media (max-width: 480px) {
  .exam-info-card {
    div {
      gap: 5px;
    }
    
    .exam-info-item {
      width: 100%;
    }
  }
  
  .validate-container {
    padding: 10px;
    
    .validate-card {
      padding: 15px;
      margin-top: 2vh;
      box-shadow: none !important;
      border: 0px;
    }
  }
}
</style>