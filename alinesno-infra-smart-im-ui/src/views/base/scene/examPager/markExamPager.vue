<template>
   <ExamPagerContainer class="exam-pager-container" :currentExam="currentExam">
      <!-- 主内容区 -->
      <div class="marking-pager-container">
         <!-- 左侧区域 -->
         <div class="left-sidebar">
            <!-- 左侧导航栏 -->
            <div class="sidebar-nav">
               <ul>
                  <li v-for="navItem in navItems" :key="navItem.id">
                     <el-button 
                        text 
                        bg 
                        class="nav-item" 
                        :class="{ active: activeNav === navItem.id }"
                        size="large"
                        @click="selectNavItem(navItem)">
                        <!-- @click="activeNav = navItem.id" -->
                        <i :class="navItem.icon"></i>
                        <span>{{ navItem.label }}</span>
                        <span v-if="navItem.badge" class="badge" :class="navItem.badge.type">
                           {{ navItem.badge.value }}
                        </span>
                     </el-button>
                  </li>
               </ul>
            </div>
         </div>

         <!-- 中间学生列表区域 -->
         <div class="student-list">
            <div class="list-header">
               考生列表
            </div>

            <!-- 搜索栏 -->
            <div class="search-box">
               <el-input 
                  v-model="searchQuery"
                  placeholder="搜索考生..." 
                  prefix-icon="el-icon-search" 
                  class="search-input" 
               />
            </div>

            <!-- 学生列表 -->
            <div class="list-content">
               <!-- 学生项 -->
               <div 
                  v-for="student in filteredStudents" 
                  :key="student.id"
                  class="student-item" 
                  :class="{ active: activeStudentId === student.id }"
                  @click="selectStudent(student)">
                  <div class="student-info">
                     <div class="student-details">
                        <div class="student-name">{{ student.userName }}</div>
                     </div>
                     <div class="student-time" :class="{ primary: isRecent(student.submitTime) }">
                        {{ formatTime(student.submitTime) }}
                     </div>
                  </div>
                  <div class="exam-info">
                     <span class="exam-tag info">{{ student.userCode}}</span>
                     <span class="exam-score" :class="{ success: student.score !== null }">
                        总分: {{ student.score !== null ? student.score : '--' }}
                     </span>
                  </div>
               </div>
            </div>
         </div>

         <!-- 右侧试卷区域 -->
         <div class="exam-content">
            <!-- 试卷头部 -->
            <div class="exam-header">
               <div>
                  <!-- <h2>{{ currentExam.title }}</h2> -->
                  <div class="exam-meta">
                     <span>考试时间: {{ currentExam.duration == null ? '30' : currentExam.duration }}分钟</span>
                     <span>|</span>
                     <span>总分: {{ currentExam.totalScore }}分</span>
                     <span>|</span>
                     <span>考生: {{ currentStudent?.userName || '--' }}</span>
                     <span>|</span>
                     <span>学号: {{ currentStudent?.userCode || '--' }}</span>
                  </div>
               </div>

               <div class="exam-actions">
                  <el-button class="print-btn" type="warning" size="large">
                     <i class="fa-solid fa-print"></i>
                     <span>打印</span>
                  </el-button>

                  <!-- 新增自动阅卷按钮 -->
                  <el-button type="success" class="auto-mark-btn" @click="handleAutoMarking" size="large">
                     <i class="fa-solid fa-robot"></i>&nbsp;<span>自动阅卷</span>
                  </el-button>

                  <el-button type="primary" class="save-btn" @click="saveMarking" size="large">
                     <i class="fa-solid fa-train"></i>&nbsp;<span>保存批改</span>
                  </el-button>
               </div>
            </div>

            <!-- 试卷内容区 -->
            <div class="exam-paper">
               <MarkExamQuestionList 
                  ref="markExamPagerRef" 
                  :examId="examId" 
                  :accountId="activeStudentId" 
                  />
            </div>
         </div>
      </div>

      <!-- 底部状态栏 -->
      <footer class="status-bar">
         <div>
            <span>试卷ID: {{ currentExam.id }}</span>
            <span>|</span>
            <span>阅卷进度: {{ markedCount }}/{{ students.length }}</span>
         </div>
         <div>
            <span>上次保存: {{ lastSavedTime }}</span>
            <span>|</span>
            <el-button text class="history-btn">查看历史版本</el-button>
         </div>
      </footer>

</ExamPagerContainer>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage , ElLoading } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import ExamPagerContainer from "./examPagerContainer"
import MarkExamQuestionList from "./components/MarkExamQuestionList"

import {
   getExamineeList
} from '@/api/base/im/scene/examPaperManager'

import {
 detail , 
 saveMarkingResults 
} from '@/api/base/im/scene/examInfoManager'

const markExamPagerRef = ref(null)
const router = useRouter()
const route = useRoute()

const examId = ref(route.query.examId)
const sceneId = ref(route.query.sceneId)

// 导航菜单数据
const navItems = ref([
  { id: 'all', label: '全部试卷', icon: 'fa-solid fa-list' },
  { id: 'pending', label: '待阅卷', icon: 'fa-solid fa-clock', badge: { type: 'warning', value: 12 } },
  { id: 'marked', label: '已阅卷', icon: 'fa-solid fa-circle-check' },
  { id: 'ranking', label: '成绩排名', icon: 'fa-solid fa-chart-column' }
])

// 学生数据
const students = ref([])

// 考试数据
const currentExam = ref({
  id: '20250101',
  title: '2025年高三数学期中考试',
  duration: 120,
  totalScore: 150
})

// 状态管理
const activeNav = ref('all')
const activeStudentId = ref(null)
const searchQuery = ref('')

const lastSavedTime = ref('刚刚')
const studentAnswers = ref({})
const questionScores = ref({})

// 计算属性 - 简洁写法
const filteredStudents = computed(() => 
  students.value.filter(student => 
    student.userName.includes(searchQuery.value) || 
    student.studentId.includes(searchQuery.value)
  )
)

const currentStudent = computed(() => {

   // 查询待阅卷的考生数(navItems.id=pending) 待阅卷的考生数
   const length = students.value.filter(student => student.examStatus === 'examination_end').length

   // 保存navItems.pending.badge.value，通过过id来判断
   navItems.value.find(item => item.id === 'pending').badge.value = length
   
  return students.value.find(student => student.id === activeStudentId.value)
})

const markedCount = computed(() => {
  return students.value.filter(student => student.score !== null).length
})

// 选择导航项
const selectNavItem = (item) => {
  activeNav.value = item.id
  activeStudentId.value = null

   getExamineeList(examId.value , item.id).then(res => {
      students.value = res.data
      selectStudent(filteredStudents.value[0])
   })
}

// 方法
const selectStudent = (student) => {
  activeStudentId.value = student.id
  // 这里可以加载学生的答卷数据
  loadStudentAnswers(student)
}

// 加载学生答卷数据
const loadStudentAnswers = (student) => {
   // 通过studentId从students里面查询到应对的学生
   currentExam.value.student = student ;
   console.log('reviewResult = ' + JSON.stringify(student.reviewResult))
   // currentExam.value.questions = student.questions ;
   markExamPagerRef.value.handlePagerDetail(currentExam.value);
}

const formatTime = (time) => {

   // time是String类型，格式为"2023-05-01 10:30:00"
   time = new Date(time)

  const now = new Date()
  const diff = now - time
  
  if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`
  } else if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  } else {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }
}

const isRecent = (time) => {
  const now = new Date()
  return (now - time) < 24 * 60 * 60 * 1000 // 24小时内算最近
}

// 自动阅卷方法
const handleAutoMarking = async () => {
    try {
        // 调用子组件的自动阅卷方法
        await markExamPagerRef.value.autoMarkQuestions();
        ElMessage.success('自动阅卷完成');
    } catch (error) {
        ElMessage.error('自动阅卷失败');
        console.error('自动阅卷失败:', error);
    }
};

const saveMarking = () => {
    // 计算总分
    const totalScore = markExamPagerRef.value.getTotalScore();

    console.log('totalScore = ' + JSON.stringify(totalScore))
    
    // 更新学生分数
    const studentIndex = students.value.findIndex(s => s.id === activeStudentId.value);
    if (studentIndex !== -1) {
        students.value[studentIndex].score = totalScore.totalScore;
    }

    // 添加loading加载过程
    const loading = ElLoading.service({
      lock: true,
      text: '保存中...',
      background: 'rgba(255, 255, 255, 0.5)',
      customClass: 'custom-loading'
    });

    // 保存批改结果
    const data = {
      examineeId: students.value[studentIndex].userId , // activeStudentId.value,
      examId: examId.value,
      sceneId: sceneId.value , 
      totalScore: totalScore.totalScore,
      scorePercentage: totalScore.scorePercentage,
      questionScores: totalScore.questionScores,
    }

    saveMarkingResults(data).then(res => {
      lastSavedTime.value = '刚刚';
      ElMessage.success('保存批改结果成功');
      loading.close();

         getExamineeList(examId.value).then(res => {
            console.log('res = ' + res);
            students.value = res.data;
         })

    }).catch(err => {
      loading.close()
      ElMessage.error('保存批改结果失败')
      console.error('保存批改结果失败:', err)
    })
    
}

// 初始化
onMounted(async() => {

   // 获取到试卷详情
   await detail(examId.value).then(res => {
      console.log('res = ' + res);
      currentExam.value = res.data ;
   })

   await getExamineeList(examId.value).then(res => {
      console.log('res = ' + res);
      students.value = res.data;

      // 默认选中第一个学生
      nextTick(() => {
         if (students.value.length > 0) {
            selectStudent(students.value[0])
         }
      })
   })

   // 配置快捷键(Ctrl+s保存批改 ctrl+q自动阅卷)
   document.addEventListener('keydown', (e) => { 
      if (e.ctrlKey && e.key === 's') {
         e.preventDefault();
         saveMarking();
      } else if (e.ctrlKey && e.key === 'd') {
         e.preventDefault();
         handleAutoMarking();
      }
   })

})
</script>

<!-- 样式 -->
<style lang="scss" scoped>
.exam-pager-container {
   display: flex;
   flex-direction: column;
   height: calc(100vh - 50px);
   background-color: #f9fafb;
   font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", Arial, sans-serif;

   .marking-pager-container {
      display: flex;
      margin-top: 10px;
      height: calc(100% - 70px);
      overflow: hidden;
   }

   // 左侧边栏
   .left-sidebar {
      width: 250px;
      background-color: #fff;
      border-right: 0px solid #e5e7eb;
      display: flex;
      flex-direction: column;
      overflow: hidden;

      .sidebar-nav {

         ul {
            padding: 8px 0;
            padding-top: 0px;
            margin: 0;
            list-style: none;

            li {
               margin: 0;

               .nav-item {

                  width: calc(100% - 10px);
                  align-items: center;
                  padding: 12px 16px;
                  color: #4b5563;
                  font-size: 14px;
                  justify-content: flex-start;
                  margin-bottom: 10px;
                  border-radius: 5px;

                  i {
                     margin-right: 12px;
                     font-size: 16px;
                  }

                  .badge {
                     margin-left: auto;
                     padding: 2px 6px;
                     border-radius: 999px;
                     font-size: 12px;

                     &.warning {
                        background-color: rgba(245, 158, 11, 0.2);
                        color: #f59e0b;
                     }
                  }

                  &.active {
                     color: #3b82f6;
                     background-color: rgba(59, 130, 246, 0.05);
                  }
               }
            }
         }
      }

      .filter-section {
         padding: 12px;
         border-bottom: 1px solid #e5e7eb;

         h3 {
            font-size: 14px;
            font-weight: 500;
            color: #374151;
            margin-bottom: 8px;
         }

         .filter-options {
            display: flex;
            flex-direction: column;
            gap: 8px;

            :deep(.el-checkbox) {
               display: flex;
               align-items: center;
               margin-right: 0;

               .el-checkbox__label {
                  display: flex;
                  align-items: center;
                  font-size: 13px;
                  color: #4b5563;

                  .badge {
                     margin-left: 8px;
                     padding: 2px 6px;
                     border-radius: 999px;
                     font-size: 12px;
                     background-color: #f3f4f6;
                     color: #1f2937;

                     &.info {
                        background-color: rgba(59, 130, 246, 0.1);
                        color: #1d4ed8;
                     }

                     &.warning-light {
                        background-color: rgba(245, 158, 11, 0.2);
                        color: #f59e0b;
                     }
                  }
               }
            }
         }
      }
   }

   // 学生列表
   .student-list {
      width: 280px;
      background-color: #fff;
      border-right: 1px solid #e5e7eb;
      border-left: 1px solid #e5e7eb;
      border-top: 1px solid #e5e7eb;
      border-bottom: 1px solid #e5e7eb;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      overflow: hidden;

      .list-header {
         padding: 12px;
         background-color: #f9fafb;
         font-size: 14px;
         font-weight: 600;
         color: #6b7280;
      }

      .list-content {
         flex: 1;
         overflow-y: auto;

         .student-item {
            padding: 12px;
            cursor: pointer;
            border-left: 4px solid transparent;
            transition: all 0.2s;
            border-radius: 9px;
            margin-left: 5px;
            margin-right: 5px;

            &.active {
               border-left-color: #3b82f6;
               background-color: rgba(59, 130, 246, 0.05);
            }

            &:hover {
               background-color: #f9fafb;
            }

            .student-info {
               display: flex;
               align-items: center;
               justify-content: space-between;
               margin-bottom: 8px;

               .student-avatar {
                  img {
                     width: 32px;
                     height: 32px;
                     border-radius: 50%;
                     object-fit: cover;
                  }
               }

               .student-details {
                  flex: 1;
                  margin-left: 0px;

                  .student-name {
                     font-size: 14px;
                     font-weight: 500;
                     color: #111827;
                  }

                  .student-class {
                     font-size: 12px;
                     color: #6b7280;
                  }
               }

               .student-time {
                  font-size: 12px;

                  &.primary {
                     color: #3b82f6;
                  }
               }
            }

            .exam-info {
               display: flex;
               align-items: center;

               .exam-tag {
                  padding: 4px 8px;
                  border-radius: 4px;
                  font-size: 12px;

                  &.info {
                     background-color: rgba(59, 130, 246, 0.1);
                     color: #1d4ed8;
                  }
               }

               .exam-score {
                  margin-left: auto;
                  font-size: 12px;
                  font-weight: 500;

                  &.success {
                     color: #16a34a;
                  }
               }
            }
         }
      }
   }

   // 试卷内容区
   .exam-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background-color: #f9fafb;

      .exam-header {
         padding: 16px 24px;
         background-color: #fff;
         // border-bottom: 1px solid #e5e7eb;
         display: flex;
         align-items: center;
         padding-top: 0px;
         justify-content: space-between;

         h2 {
            font-size: 20px;
            font-weight: 600;
            color: #111827;
            margin-top: 4px;
         }

         .exam-meta {
            font-size: 14px;
            color: #6b7280;

            span {
               margin: 0 8px;

               &:first-child {
                  margin-left: 0;
               }
            }
         }

         .exam-actions {
            display: flex;
            gap: 12px;

            .print-btn {
               i {
                  margin-right: 8px;
               }
            }
         }
      }

      .exam-paper {
         flex: 1;
         overflow-y: auto;
         padding: 10px;

         .question-section {
            margin-bottom: 24px;

            h3 {
               font-size: 16px;
               font-weight: 600;
               color: #111827;
               margin-bottom: 16px;
            }

            .question-item {
               background-color: #fff;
               border: 1px solid #e5e7eb;
               border-radius: 8px;
               padding: 16px;
               margin-bottom: 16px;

               p {
                  margin-bottom: 16px;
                  font-size: 15px;
                  line-height: 1.6;
               }

               .answer-area {
                  margin-bottom: 16px;

                  :deep(.el-radio-group) {
                     display: flex;
                     flex-direction: column;
                     gap: 8px;

                     .el-radio {
                        margin-right: 0;
                     }
                  }
               }

               .marking-toolbar {
                  display: flex;
                  align-items: center;
                  gap: 12px;

                  .score-text {
                     font-size: 14px;
                     color: #4b5563;
                  }
               }
            }
         }
      }
   }

   // 底部状态栏
   .status-bar {
      padding: 8px 10px;
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 14px;
      color: #6b7280;

      div {
         span {
            margin: 0 8px;

            &:first-child {
               margin-left: 0;
            }
         }
      }

      .history-btn {
         padding: 0;
         height: auto;
         color: #3b82f6;
      }
   }
}

.search-box {
   padding: 12px;

   .search-input {
      :deep(.el-input__inner) {
         padding-left: 32px;
      }

      :deep(.el-input__prefix) {
         left: 8px;
      }
   }
}
</style>