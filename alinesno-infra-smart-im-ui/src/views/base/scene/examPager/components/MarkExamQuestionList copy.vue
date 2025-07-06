<template>
    <div class="online-exam-container">
        <!-- 考试内容区 -->
        <div class="exam-content">
            <!-- 题目区域 - 复用现有组件系统 -->
            <div class="questions-area">
                <div v-for="(question, index) in pagerInfo.questions" :key="question.id" :id="'question-' + (index + 1)" class="question-wrapper">
                    <!-- 动态加载题目组件 -->
                    <component 
                        :is="getQuestionComponent(question.type)" 
                        :question="question" 
                        :isQuestionEdit="false"
                        :currentSelect="false" 
                        @update-handleUpdateQuestion="handleAnswerChange" />
                    
                    <!-- 评分区域 -->
                    <div class="marking-area" v-if="question.score > 0">
                        <el-button type="primary" @click="openMarkingDialog(question)">
                            <i class="fa-solid fa-pen"></i> 评分
                        </el-button>
                        <span class="score-display" v-if="question.markedScore !== undefined">
                            得分: {{ question.markedScore }}/{{ question.score }}
                        </span>
                    </div>
                    
                    <!-- 评语区域 -->
                    <div class="comment-area" v-if="question.comment">
                        <div class="comment-label">评语:</div>
                        <div class="comment-content">{{ question.comment }}</div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 评分弹窗 -->
        <!-- <el-dialog v-model="markingDialogVisible" title="题目评分" width="50%">
            <div class="marking-dialog-content">
                <h3>{{ currentMarkingQuestion.question }}</h3>
                
                <div class="score-selection">
                    <span>评分 (满分{{ currentMarkingQuestion.score }}分):</span>
                    <el-input-number 
                        v-model="currentMarkingScore" 
                        :min="0" 
                        :max="currentMarkingQuestion.score" 
                        :step="1"
                        controls-position="right" />
                </div>
                
                <div class="comment-input">
                    <el-input
                        v-model="currentMarkingComment"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入评语..."
                        maxlength="200"
                        show-word-limit />
                </div>
            </div>
            
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="markingDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmMarking">
                        确定
                    </el-button>
                </span>
            </template>
        </el-dialog> -->

        <!-- 评分弹窗 -->
<el-dialog v-model="markingDialogVisible" title="题目评分" width="50%">
    <div class="marking-dialog-content">
        <h3>{{ currentMarkingQuestion.question }}</h3>
        
        <div class="score-selection">
            <div class="score-title">请选择分数 (满分{{ currentMarkingQuestion.score }}分):</div>
            
            <!-- 分数按钮网格 -->
            <div class="score-buttons-grid">
                <button
                    v-for="score in getAllPossibleScores(currentMarkingQuestion.score)"
                    :key="score"
                    class="score-button"
                    :class="{ active: currentMarkingScore === score }"
                    @click="currentMarkingScore = score">
                    {{ score }}
                </button>
            </div>
            
            <!-- 快速选择 -->
            <div class="quick-actions" v-if="currentMarkingQuestion.score > 5">
                <el-button
                    size="small"
                    @click="currentMarkingScore = 0">
                    0分
                </el-button>
                <el-button
                    size="small"
                    @click="currentMarkingScore = Math.floor(currentMarkingQuestion.score / 2)">
                    一半分 ({{ Math.floor(currentMarkingQuestion.score / 2) }})
                </el-button>
                <el-button
                    size="small"
                    @click="currentMarkingScore = currentMarkingQuestion.score">
                    满分 ({{ currentMarkingQuestion.score }})
                </el-button>
            </div>
        </div>
        
        <div class="comment-input">
            <el-input
                v-model="currentMarkingComment"
                type="textarea"
                :rows="4"
                placeholder="请输入评语..."
                maxlength="200"
                show-word-limit />
        </div>
    </div>
    
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="markingDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmMarking">
                确定
            </el-button>
        </span>
    </template>
</el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 导入所有题目组件
import RadioSelect from '../questions/radioSelect.vue'
import Checkbox from '../questions/checkBox.vue'
import SingleLine from '../questions/singleLine.vue'
import MultiFill from '../questions/multiFill.vue'
import MultiLine from '../questions/multiLine.vue'
import ImageFile from '../questions/imageFile.vue'
import ImageRadio from '../questions/imageRadio.vue'
import ImageCheckbox from '../questions/imageCheckbox.vue'
import Location from '../questions/locationFill.vue'
import Datetime from '../questions/datetimeFill.vue'
import DynamicTable from '../questions/dynamicTable.vue'
import DescriptionPanel from '../questions/descriptionPanel.vue'

const props = defineProps({
    examId: {
        type: String,
        required: true
    },
    accountId: {
        type: String,
        required: true
    }
})

const emit = defineEmits(['submit'])

// 考试相关状态
const pagerInfo = ref({
    title: '',
    desc: '',
    questions: []
})

// 评分相关状态
const markingDialogVisible = ref(false)
const currentMarkingQuestion = ref({})
const currentMarkingScore = ref(0)
const currentMarkingComment = ref('')

// 根据题目类型返回对应组件
const getQuestionComponent = (type) => {
    const componentMap = {
        'radio': RadioSelect,
        'checkbox': Checkbox,
        'single-line': SingleLine,
        'multi-fill': MultiFill,
        'multi-line': MultiLine,
        'image-file': ImageFile,
        'image-radio': ImageRadio,
        'image-checkbox': ImageCheckbox,
        'location': Location,
        'datetime': Datetime,
        'dynamic-table': DynamicTable,
        'description': DescriptionPanel
    }
    return componentMap[type] || null
}

const handlePagerDetail = async (examInfoData) => {
    pagerInfo.value = {
        ...pagerInfo.value,
        id: examInfoData.id,
        title: examInfoData.examName,
        desc: examInfoData.examName,
        pagerType: examInfoData.pagerType,
        difficulty: examInfoData.difficulty,
        questions: examInfoData.student.questions.map(q => ({
            ...q,
            markedScore: q.markedScore || undefined,
            comment: q.comment || ''
        }))
    }
}

// 打开评分对话框
const openMarkingDialog = (question) => {
    currentMarkingQuestion.value = question
    currentMarkingScore.value = question.markedScore || 0
    currentMarkingComment.value = question.comment || ''
    markingDialogVisible.value = true
}

// 确认评分
const confirmMarking = () => {
    const questionIndex = pagerInfo.value.questions.findIndex(
        q => q.id === currentMarkingQuestion.value.id
    )
    
    if (questionIndex !== -1) {
        pagerInfo.value.questions[questionIndex] = {
            ...pagerInfo.value.questions[questionIndex],
            markedScore: currentMarkingScore.value,
            comment: currentMarkingComment.value
        }
    }
    
    markingDialogVisible.value = false
    saveMarkingResults()
}

// 保存评分结果
const saveMarkingResults = async () => {
    try {
        // 这里调用API保存评分结果
        const payload = {
            examId: props.examId,
            accountId: props.accountId,
            questions: pagerInfo.value.questions
                .filter(q => q.markedScore !== undefined)
                .map(q => ({
                    questionId: q.id,
                    score: q.markedScore,
                    comment: q.comment
                }))
        }
        
        // 调用API保存评分
        // await saveExamMarking(payload)
        
        ElMessage.success('评分已保存')
    } catch (error) {
        ElMessage.error('保存评分失败')
        console.error('保存评分失败:', error)
    }
}

// 获取所有可能的分数
const getAllPossibleScores = (maxScore) => {
    return Array.from({ length: maxScore + 1 }, (_, i) => i)
}

defineExpose({
    handlePagerDetail,
    saveMarkingResults
})
</script>

<style lang="scss" scoped>
.online-exam-container {
    background-color: #fafafa;
    border-radius: 10px;
    max-height: 100vh;
    display: flex;
    flex-direction: column;

    .exam-content {
        padding: 20px;
    }
}

.questions-area {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;

    .question-wrapper {
        background-color: #fff;
        border-radius: 0.75rem;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }
}

.marking-area {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px dashed #eee;
    display: flex;
    align-items: center;
    gap: 20px;
    
    .score-display {
        font-weight: bold;
        color: #409eff;
    }
}

.comment-area {
    margin-top: 15px;
    padding: 15px;
    background-color: #f8f9fa;
    border-radius: 4px;
    
    .comment-label {
        font-weight: bold;
        margin-bottom: 8px;
        color: #666;
    }
    
    .comment-content {
        white-space: pre-wrap;
        line-height: 1.6;
    }
}

// .marking-dialog-content {
//     h3 {
//         margin-bottom: 20px;
//         font-size: 16px;
//         color: #333;
//     }
    
//     .score-selection {
//         margin-bottom: 20px;
//         display: flex;
//         align-items: center;
//         gap: 15px;
        
//         span {
//             font-weight: bold;
//         }
//     }
    
//     .comment-input {
//         margin-top: 20px;
//     }
// }

.marking-dialog-content {
    h3 {
        margin-bottom: 20px;
        font-size: 16px;
        color: #333;
        padding: 8px;
        background-color: #f5f7fa;
        border-radius: 4px;
    }
    
    .score-selection {
        margin-bottom: 20px;
        
        .score-title {
            font-weight: bold;
            margin-bottom: 15px;
        }
        
        .score-buttons-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(50px, 1fr));
            gap: 10px;
            margin-bottom: 15px;
            
            .score-button {
                height: 40px;
                border: 1px solid #dcdfe6;
                border-radius: 4px;
                background-color: #f5f7fa;
                cursor: pointer;
                transition: all 0.2s;
                font-size: 14px;
                
                &:hover {
                    background-color: #e4e7ed;
                }
                
                &.active {
                    background-color: #409eff;
                    color: white;
                    border-color: #409eff;
                }
            }
        }
        
        .quick-actions {
            display: flex;
            gap: 10px;
            margin-bottom: 15px;
        }
    }
    
    .comment-input {
        margin-top: 20px;
    }
}

</style>