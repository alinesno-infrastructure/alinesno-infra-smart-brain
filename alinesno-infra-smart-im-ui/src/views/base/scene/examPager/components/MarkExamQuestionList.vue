<template>
    <div class="online-exam-container">
        <!-- 考试内容区 -->
        <div class="exam-content">
            <!-- 题目区域 -->
            <div class="questions-area">
                <div v-for="(question, index) in pagerInfo.questions" :key="question.id" 
                     :id="'question-' + (index + 1)" class="question-wrapper">
                    
                    <!-- 动态加载题目组件 -->
                    <component 
                        :is="getQuestionComponent(question.type)" 
                        :question="question" 
                        :isQuestionEdit="false"
                        :currentSelect="false" 
                        @update-handleUpdateQuestion="handleAnswerChange" />
                    
                    <!-- 评分区域 -->
                    <div class="marking-area" v-if="question.score > 0">
                        <el-popover
                            placement="bottom"
                            :width="400"
                            trigger="click"
                            v-model:visible="question.showPopover">
                            <template #reference>
                                <el-button size="large" type="primary" @click="prepareMarking(question)">
                                    <i class="fa-solid fa-pen"></i> 评分
                                </el-button>
                            </template>
                            
                            <div class="popover-marking-content">
                                <h4>{{ question.question }}</h4>
                                
                                <div class="score-selection">
                                    <div class="score-title">请选择分数 (满分{{ question.score }}分):</div>
                                    
                                    <div class="score-buttons-grid">
                                        <button
                                            size="large"
                                            v-for="score in getAllPossibleScores(question.score)"
                                            :key="score"
                                            class="score-button"
                                            :class="{ active: question.markedScore === score }"
                                            @click="updateScore(question, score)">
                                            {{ score }}
                                        </button>
                                    </div>
                                    
                                    <div class="quick-actions" v-if="question.score > 5">
                                        <el-button
                                            size="large"
                                            @click="updateScore(question, 0)">
                                            0分
                                        </el-button>
                                        <el-button
                                            size="large"
                                            @click="updateScore(question, Math.floor(question.score / 2))">
                                            一半分 ({{ Math.floor(question.score / 2) }})
                                        </el-button>
                                        <el-button
                                            size="large"
                                            @click="updateScore(question, question.score)">
                                            满分 ({{ question.score }})
                                        </el-button>
                                    </div>
                                </div>
                                
                                <div class="comment-input">
                                    <el-input
                                        v-model="question.comment"
                                        type="textarea"
                                        :rows="3"
                                        resize="none"
                                        placeholder="请输入评语..."
                                        maxlength="500"
                                        show-word-limit />
                                </div>
                                
                                <div class="popover-footer">
                                    <el-button 
                                        size="large"
                                        @click="question.showPopover = false">
                                        关闭
                                    </el-button>
                                    <el-button 
                                        type="primary" 
                                        size="large"
                                        @click="saveMarking(question)">
                                        保存
                                    </el-button>
                                </div>
                            </div>
                        </el-popover>
                        
                        <span class="score-display" v-if="question.markedScore !== undefined">
                            得分: {{ question.markedScore }}/{{ question.score }}
                        </span>

                        <!-- 
                        <span class="score-display" 
                            :class="{ 'visually-hidden': question.showPopover }"
                            v-if="question.markedScore !== undefined">
                            得分: {{ question.markedScore }}/{{ question.score }}
                        </span>
                         -->

                    </div>
                    
                    <!-- 评语区域 -->
                    <div class="comment-area" v-if="question.comment">
                        <div class="comment-label">评语:</div>
                        <div class="comment-content">{{ question.comment }}</div>
                    </div>
                </div>
            </div>
        </div>

        <el-dialog
    v-model="scoringDialogVisible"
    title="阅卷统计"
    width="60%"
    :close-on-click-modal="false">
    <div class="scoring-summary">
        <h3>总分: {{ totalScore }}/{{ maxScore }}</h3>
        
        <el-divider content-position="left">自动评分题目</el-divider>
        <el-table :data="scoringStats.autoScored" border>
            <el-table-column prop="order" label="题号" width="80" />
            <el-table-column prop="question" label="题目" show-overflow-tooltip />
            <el-table-column label="得分" width="120">
                <template #default="{row}">
                    <span :class="getScoreClass(row.score, row.total)">
                        {{ row.score }}/{{ row.total }}
                    </span>
                </template>
            </el-table-column>
        </el-table>
        
        <el-divider v-if="scoringStats.manualScored.length > 0" content-position="left">已手动评分题目</el-divider>
        <el-table v-if="scoringStats.manualScored.length > 0" :data="scoringStats.manualScored" border>
            <el-table-column prop="order" label="题号" width="80" />
            <el-table-column prop="question" label="题目" show-overflow-tooltip />
            <el-table-column label="得分" width="120">
                <template #default="{row}">
                    <span :class="getScoreClass(row.score, row.total)">
                        {{ row.score }}/{{ row.total }}
                    </span>
                </template>
            </el-table-column>
        </el-table>
        
        <el-divider v-if="scoringStats.notScored.length > 0" content-position="left">未评分题目</el-divider>
        <el-table v-if="scoringStats.notScored.length > 0" :data="scoringStats.notScored" border>
            <el-table-column prop="order" label="题号" width="80" />
            <el-table-column prop="question" label="题目" show-overflow-tooltip />
            <el-table-column label="满分" width="120">
                <template #default="{row}">
                    <span class="not-scored">{{ row.total }}分</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
    
    <template #footer>
        <el-button type="primary" @click="scoringDialogVisible = false">确定</el-button>
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

// 准备评分
const prepareMarking = (question) => {
    // 初始化评分状态
    if (question.markedScore === undefined) {
        question.markedScore = 0
    }
    if (!question.comment) {
        question.comment = ''
    }
}

// 更新分数
const updateScore = (question, score) => {
    question.markedScore = score
}

// 保存评分
const saveMarking = async (question) => {
    try {
        question.showPopover = false
        
        // 这里调用API保存评分结果
        const payload = {
            examId: props.examId,
            accountId: props.accountId,
            questions: [{
                questionId: question.id,
                score: question.markedScore,
                comment: question.comment
            }]
        }
        
        ElMessage.success('评分已保存')
    } catch (error) {
        ElMessage.error('保存评分失败')
        console.error('保存评分失败:', error)
    }
}

const getScoreClass = (score, total) => {
    if (score === 0) {
        return 'zero-score';  // 0分
    } else if (score === total) {
        return 'full-score';  // 满分
    } else {
        return 'partial-score'; // 部分得分
    }
};

// 暴露自动阅卷方法
const autoMarkQuestions = () => {
    // 获取学生的答案映射 {questionId: [selectedAnswers]}
    const studentAnswers = pagerInfo.value.student?.answers || {};
    
    pagerInfo.value.questions = pagerInfo.value.questions.map(q => {
        // 只对未评分的选择题进行自动评分
        if (q.markedScore === undefined && ['radio', 'checkbox', 'image-radio', 'image-checkbox'].includes(q.type)) {
            const autoScore = calculateAutoScore(q, studentAnswers[q.id]);
            if (autoScore !== undefined) {
                return {
                    ...q,
                    markedScore: autoScore,
                    scoreSource: 'auto'
                };
            }
        }
        return q;
    });
    
    // 显示阅卷统计
    const scoringStats = {
        autoScored: [],
        manualScored: [],
        notScored: []
    };
    
    pagerInfo.value.questions.forEach(q => {
        if (q.scoreSource === 'auto') {
            scoringStats.autoScored.push({
                id: q.id,
                order: q.order,
                question: q.question,
                score: q.markedScore,
                total: q.score
            });
        } else if (q.markedScore !== undefined) {
            scoringStats.manualScored.push({
                id: q.id,
                order: q.order,
                question: q.question,
                score: q.markedScore,
                total: q.score
            });
        } else {
            scoringStats.notScored.push({
                id: q.id,
                order: q.order,
                question: q.question,
                score: 0,
                total: q.score
            });
        }
    });
    
    const totalScore = pagerInfo.value.questions.reduce((sum, q) => sum + (q.markedScore || 0), 0);
    const maxScore = pagerInfo.value.questions.reduce((sum, q) => sum + q.score, 0);
    
    showScoringDialog(scoringStats, totalScore, maxScore);
};

// 修改 handlePagerDetail 方法，移除自动评分逻辑
const handlePagerDetail = async (examInfoData) => {

    // 创建reviewResult的映射，方便快速查找
    const reviewResultMap = {};
    if (examInfoData.student.reviewResult && examInfoData.student.reviewResult.length > 0) {
        examInfoData.student.reviewResult.forEach(item => {
            reviewResultMap[item.id] = {
                markedScore: item.score,
                comment: item.comment || ''
            };
        });
    }

    pagerInfo.value = {
        ...pagerInfo.value,
        id: examInfoData.id,
        title: examInfoData.examName,
        desc: examInfoData.examName,
        pagerType: examInfoData.pagerType,
        difficulty: examInfoData.difficulty,
        student: examInfoData.student,
        questions: examInfoData.student.questions.map(q => {
            // 检查是否有对应的reviewResult
            const reviewData = reviewResultMap[q.id];

            return {
                ...q,
                markedScore: reviewData ? reviewData.markedScore : (q.markedScore !== undefined ? q.markedScore : undefined),
                comment: reviewData ? reviewData.comment : (q.comment || ''),
                showPopover: false
            };
        })
    };

};

// 显示阅卷统计对话框
const scoringDialogVisible = ref(false);
const scoringStats = ref({});
const totalScore = ref(0);
const maxScore = ref(0);

const showScoringDialog = (stats, score, max) => {
    scoringStats.value = stats;
    totalScore.value = score;
    maxScore.value = max;
    scoringDialogVisible.value = true;
};

// 修改后的自动评分计算方法
const calculateAutoScore = (question, userSelectedAnswers = []) => {
    if (!question.answers || !question.answers.length) {
        console.log('No answers for question', question.id);
        return 0;
    }
    
    // 获取正确答案
    const correctAnswers = question.answers
        .filter(a => a.isCorrect)
        .map(a => a.label);
    
    console.log('Question ID:', question.id, 
               'Correct Answers:', JSON.stringify(correctAnswers), 
               'User Selected Answers:', userSelectedAnswers);
    
    // 如果没有用户答案，返回0分
    if (!userSelectedAnswers || !userSelectedAnswers.length) {
        console.log('No user answers for question', question.id);
        return 0;
    }

    console.log('question.type = ' + question.type)
    
    // 单选题目评分
    if (question.type === 'radio' || question.type === 'image-radio') {
        const isCorrect = userSelectedAnswers.length > 0 && 
                         correctAnswers.length > 0 &&
                         userSelectedAnswers[0] === correctAnswers[0];
        
        console.log('Radio question', question.id, 'is correct:', isCorrect);
        
        return isCorrect ? question.score : 0;
    }
    
    // 多选题目评分
    if (question.type === 'checkbox' || question.type === 'image-checkbox') {

        console.log('userSelectedAnswers = ' + JSON.stringify(userSelectedAnswers));
        console.log('correctAnswers = ' + JSON.stringify(correctAnswers)) ;

        // 全对才得分
        if (userSelectedAnswers.length !== correctAnswers.length) {
            console.log('Checkbox question', question.id, 'answer length mismatch');
            return 0;
        }
        
        const allCorrect = userSelectedAnswers.every(ua => 
            correctAnswers.includes(ua)
        );
        
        console.log('Checkbox question', question.id, 'all correct:', allCorrect);
        
        return allCorrect ? question.score : 0;
    }
    
    console.log('Non-auto-scored question type:', question.type);
    return 0;
}

// 获取所有可能的分数
const getAllPossibleScores = (maxScore) => {
    return Array.from({ length: maxScore + 1 }, (_, i) => i)
}

const getTotalScore = () => {
    // 计算总分数
    const totalScore = pagerInfo.value.questions.reduce((sum, q) => {
        return sum + (q.markedScore || 0);
    }, 0);

    // 计算满分
    const maxScore = pagerInfo.value.questions.reduce((sum, q) => {
        return sum + q.score;
    }, 0);

    // 收集每道题目的评分详情
    const questionScores = pagerInfo.value.questions.map(q => ({
        id: q.id,
        score: q.markedScore || 0,
        maxScore: q.score,
        comment: q.comment || '',
    }));

    // 返回包含所有评分信息的对象
    return {
        totalScore,
        maxScore,
        questionScores,
        // 计算得分率（百分比）
        scorePercentage: maxScore > 0 ? Math.round((totalScore / maxScore) * 100) : 0
    };
}


defineExpose({
    handlePagerDetail,
    getTotalScore,
    autoMarkQuestions
});

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
    justify-content: flex-end;
    
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

.popover-marking-content {
    padding: 10px;
    
    h4 {
        margin-bottom: 15px;
        font-size: 15px;
        color: #333;
        padding: 8px;
        background-color: #f5f7fa;
        border-radius: 4px;
    }
    
    .score-selection {
        margin-bottom: 15px;
        
        .score-title {
            font-weight: bold;
            margin-bottom: 10px;
            font-size: 14px;
        }
        
        .score-buttons-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
            gap: 8px;
            margin-bottom: 10px;
            
            .score-button {
                height: 32px;
                border: 1px solid #dcdfe6;
                border-radius: 4px;
                background-color: #f5f7fa;
                cursor: pointer;
                transition: all 0.2s;
                font-size: 13px;
                
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
            gap: 8px;
            margin-bottom: 10px;
            
            button {
                padding: 5px 10px;
                font-size: 12px;
            }
        }
    }
    
    .comment-input {
        margin: 15px 0;
    }
    
    .popover-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        padding-top: 10px;
        border-top: 1px solid #eee;
    }
}

.scoring-summary {
    max-height: 60vh;
    overflow-y: auto;
    
    h3 {
        text-align: center;
        margin-bottom: 20px;
        font-size: 18px;
    }
    
    .auto-score {
        color: #67c23a;
        font-weight: bold;
    }
    
    .manual-score {
        color: #409eff;
        font-weight: bold;
    }
    
    .not-scored {
        color: #f56c6c;
        font-weight: bold;
    }
    
    .el-divider {
        margin: 15px 0;
    }
    
    .el-table {
        margin-bottom: 20px;
    }

    .zero-score {
        color: #f56c6c; /* 红色表示0分 */
        font-weight: bold;
    }
    
    .full-score {
        color: #67c23a; /* 绿色表示满分 */
        font-weight: bold;
    }
    
    .partial-score {
        color: #e6a23c; /* 橙色表示部分得分 */
        font-weight: bold;
    }
    
    .not-scored {
        color: #909399; /* 灰色表示未评分 */
        font-weight: bold;
    }
}
</style>