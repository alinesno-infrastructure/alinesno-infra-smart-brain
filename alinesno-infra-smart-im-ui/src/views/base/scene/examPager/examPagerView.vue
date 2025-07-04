<template>
  <div class="exam-pager-container">
    <TopPanel 
      @handleSavePager="handleSavePager"
      @handlePublishExam="handlePublishExam"
      :currentPageInfo="currentPageInfo"
      ref="topPanelRef" />

    <el-row>
      <el-col :span="5">
        <LeftPanel 
          @handleAddQuestionCard="handleAddQuestionCard"
          :currentPageInfo="currentPageInfo"
          ref="leftPanelRef" />
      </el-col>
      <el-col :span="14">
        <PagerContainer 
          @setCurrentPageInfo="setCurrentPageInfo"
          ref="pagerContainerRef" />
      </el-col>
      <el-col :span="5">
        <RightPanel 
          :currentPageInfo="currentPageInfo"
          ref="rightPanelRef" />
      </el-col>
    </el-row>

    <!-- 考试配置弹出窗 -->
     <!-- 考试配置弹出窗 -->
    <el-dialog
      v-model="examConfigVisible"
      title="考试发布配置"
      width="560"
      :close-on-click-modal="false"
      @closed="handleConfigDialogClosed"
    >
      <el-form
        ref="examConfigFormRef"
        size="large"
        :model="examConfigForm"
        :rules="examConfigRules"
        label-width="120px"
        label-position="right"
      >
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="examConfigForm.examName" placeholder="请输入考试名称" />
        </el-form-item>
        
        <el-form-item label="考试时间" required>
          <el-col :span="11">
            <el-form-item prop="startTime">
              <el-date-picker
                v-model="examConfigForm.startTime"
                type="datetime"
                placeholder="开始时间"
                style="width: 100%"
                :disabled-date="disabledStartDate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="2" class="text-center">-</el-col>
          <el-col :span="11">
            <el-form-item prop="endTime">
              <el-date-picker
                v-model="examConfigForm.endTime"
                type="datetime"
                placeholder="结束时间"
                style="width: 100%"
                :disabled-date="disabledEndDate"
              />
            </el-form-item>
          </el-col>
        </el-form-item>
        
        <el-form-item label="考试时长(分钟)" prop="duration">
          <el-input-number 
            v-model="examConfigForm.duration" 
            :min="10" 
            :max="600" 
            controls-position="right"
          />
        </el-form-item>
        
        <el-form-item label="及格分数" prop="passScore">
          <el-input-number 
            v-model="examConfigForm.passScore" 
            :min="0" 
            :max="100" 
            controls-position="right"
          />
        </el-form-item>
        
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="examConfigVisible = false">取消</el-button>
          <el-button type="primary" size="large" @click="submitExamConfig">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>

import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 引入组件
import TopPanel from './components/TopPanel.vue'
import RightPanel from './components/RightPanel.vue'
import PagerContainer from './components/PagerContainer.vue'
import LeftPanel from './components/LeftPanel.vue'

import {
  addExamInfo
} from "@/api/base/im/scene/examInfoManager" ; 

// 引用组件
const topPanelRef = ref(null)
const pagerContainerRef = ref(null)
const leftPanelRef = ref(null)
const rightPanelRef = ref(null)
const currentPageInfo = ref(null)

const { proxy } = getCurrentInstance();
const router = useRouter()
const route = useRoute()

const sceneId = ref(route.query.sceneId)

// 考试配置弹出窗相关
const examConfigVisible = ref(false)
const examConfigFormRef = ref(null)

const examConfigForm = reactive({
  examName: '',
  examType: 1,
  startTime: '',
  endTime: '',
  duration: 60,
  sceneId: sceneId.value , 
  pageId: 0, 
  passScore: 60,
  description: ''
})


// 验证开始时间
const validateStartTime = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择开始时间'))
  } else if (value.getTime() < Date.now()) {
    callback(new Error('开始时间不能早于当前时间'))
  } else {
    callback()
  }
}

// 验证结束时间
const validateEndTime = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择结束时间'))
  } else if (examConfigForm.startTime && value.getTime() <= examConfigForm.startTime.getTime()) {
    callback(new Error('结束时间必须晚于开始时间'))
  } else {
    callback()
  }
}

const examConfigRules = reactive({
  examName: [
    { required: true, message: '请输入考试名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  examType: [
    { required: true, message: '请选择考试类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' },
    { validator: validateStartTime, trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    { validator: validateEndTime, trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入考试时长', trigger: 'blur' }
  ],
  passScore: [
    { required: true, message: '请输入及格分数', trigger: 'blur' }
  ]
})

// 禁用开始日期（不能选择今天之前的日期）
const disabledStartDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 禁用结束日期（不能选择开始时间之前的日期）
const disabledEndDate = (time) => {
  if (!examConfigForm.startTime) return false
  return time.getTime() < examConfigForm.startTime.getTime()
}


// 处理配置对话框关闭
const handleConfigDialogClosed = () => {
  examConfigFormRef.value?.resetFields()
}

// 提交考试配置
const submitExamConfig = async () => {
  try {
    await examConfigFormRef.value.validate()
    
    // 这里可以添加API调用逻辑
    addExamInfo(examConfigForm).then(res => {
      ElMessage.success('考试配置保存成功')
      examConfigVisible.value = false

      // 跳转到考试界面
       router.push('/scene/examPager/examManager?sceneId=' + sceneId.value)
    })
    
  } catch (error) {
    console.error('考试配置验证失败:', error)
  }
}

// 添加问题
const handleAddQuestionCard = (newQuestionItem) => {
  pagerContainerRef.value.handleAddQuestionCard(newQuestionItem)
}

// 保存试卷
const handleSavePager  = () => {
  pagerContainerRef.value.handleSavePager()
}

// 发布考试
const handlePublishExam = (currentPageInfo) => {

  console.log('currentPageInfo = ' + JSON.stringify(currentPageInfo))

  // 打开考试配置弹窗
  examConfigVisible.value = true
  
  // 如果已有配置，填充表单
  if (currentPageInfo) {
    // Object.assign(examConfigForm, currentPageInfo)
    examConfigForm.examName = currentPageInfo.title
    examConfigForm.paperId = currentPageInfo.id
  }
}

const setCurrentPageInfo = (pageInfo) => {
  currentPageInfo.value = pageInfo
}

</script>

<style lang="scss" scoped>

.exam-pager-container {
    background: #f5f5f5;
}

</style>
