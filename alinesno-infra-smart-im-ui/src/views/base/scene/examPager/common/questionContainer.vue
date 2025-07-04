<template>
    <div class="question-container" :class="props.isQuestionEdit? '':'question-not-edit'">

        <div class="question-tags">
            <span>
                <span class="tag" v-if="props?.question?.type === 'radio'">单选</span>
                <span class="tag" v-if="props?.question?.type === 'checkbox'">多选</span>

                <span class="tag" v-if="props?.question?.type === 'image-radio'">图片单选</span>
                <span class="tag" v-if="props?.question?.type === 'image-file'">图片上传</span>
                <span class="tag" v-if="props?.question?.type === 'image-checkbox'">图片多选</span>

                <span class="tag" v-if="props?.question?.type === 'single-line'">单行文本</span>
                <span class="tag" v-if="props?.question?.type === 'multi-line'">多行文本</span>
                <span class="tag" v-if="props?.question?.type === 'multi-fill'">多项填空</span>

                <span class="tag" v-if="props?.question?.type === 'datetime'">日期时间</span>
                <span class="tag" v-if="props?.question?.type === 'location'">位置</span>

                <span class="tag" v-if="props?.question?.type === 'dynamic-table'">动态表单</span>

                <span class="tag" v-if="props?.question?.type === 'description'">内容分析</span>

            </span>
            <span>
                <span class="tag">
                    {{ props?.question?.score }}分
                </span>
            </span>

            <span class="switch-item" v-if="currentSelect && props.isQuestionEdit">
                <el-switch size="small" v-model="requiredStatus" @change="handleRequiredChange" /> 必须
                <el-button type="text" size="small" text @click="handleDeleteQuestion()">
                    <i class="fa-solid fa-trash"></i>&nbsp;删除
                </el-button>
            </span>

        </div>

        <div class="question-title">
            <div class="title">

                <span class="question-required"
                    :style="'visibility: ' + (props?.question?.isRequired ? 'visible' : 'hidden')">
                    <i aria-hidden="true">*</i>
                </span>

                <span style="width: 30px;text-align: center;">
                 {{ props?.question?.order }} 
                </span>

                <EditableMediaText
                    style="width:100%" 
                    v-model="questionData" 
                    :isQuestionEdit="props.isQuestionEdit"
                    :mediaFiles="mediaFiles"
                    @update:modelValue="handleUpdateTitle"
                    @upload-media="handleUploadMedia"
                    placeholder="请输入文本" />

            </div>
            <div class="desc">{{ props?.question?.desc }}</div>
        </div>

        <div class="question-content">
            <slot @update-handleUpdateQuestion="$emit('updateHandleUpdateQuestion', $event)">
                <!-- 自定义内容插槽 -->
            </slot>
        </div>

        <!-- 底部功能-->
        <div class="question-footer" v-if="currentSelect && props.isQuestionEdit"> 
            <el-button v-if="isSelectType()" type="text" size="large" text @click="handleAddOption()">
                <i class="fa-solid fa-plus"></i>&nbsp;添加选项
            </el-button>
            <el-button v-if="isSelectType()" type="text" size="large" text @click="handleAddOption('other')">
                <i class="fa-solid fa-circle-question"></i>&nbsp;添加其它
            </el-button>

            <!-- 分数设置按钮绑定 Popover -->
            <el-popover
                v-model:visible="scorePopoverVisible"
                trigger="click"
                placement="bottom"
                width="240"
                @visible-change="handlePopoverVisibleChange">

                <!-- Popover 内容区 -->
                <el-form
                    ref="scoreFormRef"
                    :model="scoreForm"
                    :rules="scoreRules"
                    label-width="60px"
                    class="popover-form">
                    <el-form-item label="分值" prop="score">
                        <el-input
                            type="number"
                            v-model.number="scoreForm.score"
                            placeholder="请输入分值"
                            :min="0"
                            :max="100"
                            style="width: 100px;">
                        </el-input>
                    </el-form-item>
                </el-form>

                <!-- 操作按钮 -->
                <template #reference>
                    <el-button type="text" size="large" text @click.stop>
                    <i class="fa-solid fa-sliders"></i>&nbsp;分数设置
                    </el-button>
                </template>

                <div style="text-align: center; padding: 8px">
                    <el-button @click="handleCancel">取消</el-button>
                    <el-button type="primary" @click="handleSave">保存</el-button>
                </div>

            </el-popover>

            <el-button v-if="isBlankType()" type="text" size="large" text @click="handleAddBlank()">
                <i class="fa-solid fa-square-plus"></i>&nbsp;添加填空
            </el-button>
        </div>
    </div>
</template>

<script setup>

import { set, cloneDeep } from 'lodash'
import { defineProps, ref, computed, defineEmits, reactive } from 'vue'

import { ElMessageBox , ElMessage } from 'element-plus'

import EditableText from '../components/EditableText.vue'
import EditableMediaText from '../components/EditableMediaText.vue'

const emit = defineEmits([
'update:question', 
'updateQuestionIsRequired' , 
'updateHandleUpdateQuestion',
'delete-question',
'update-questionTitle'
])

const props = defineProps({
    question: {
        type: Object,
        required: false
    },
    isQuestionEdit: {
        type: Boolean,
        default: true,
        required: false
    },
    currentSelect: {
        type: Boolean,
        default: false,
        required: false
    }
})

// Popover 显示状态
const scorePopoverVisible = ref(false)
const scoreFormRef = ref(null)
const mediaFiles = ref(props.question.mediaFiles || [])

// 表单数据
const scoreForm = reactive({
  score: props.question.score || 0 // 初始化分值
})

// 保存分值
const handleSave = async () => {
  if (scoreFormRef.value) {
    await scoreFormRef.value.validate((valid) => {
      if (valid) {
        // 触发父组件更新事件
        emit('updateHandleUpdateQuestion', {
          ...props.question,
          score: scoreForm.score
        })
        ElMessage.success('分值更新成功')
        scorePopoverVisible.value = false
      }
    })
  }
}

// 取消操作
const handleCancel = () => {
  scoreForm.score = props.question.score || 0 // 恢复原值
  scorePopoverVisible.value = false
}

const questionData = ref(props.question.question)

// 是否是选择题
const isSelectType = () => {
    return props.currentSelect && (props?.question?.type === 'radio' || props?.question?.type === 'checkbox' || props?.question?.type === 'image-radio' || props?.question?.type === 'image-checkbox')
}

// 是否是填空题
const isBlankType = () => {
    return props.currentSelect && (props?.question?.type === 'multi-fill')
}

// 表单数据对象
const selectAnswersData = computed({
    get: () => {
        return props.question.answers
    },
    set: (value) => {
        set(props.question.answers, 'answers_node_data', value)
    }
})

// 当前问题添加选项
const handleAddOption = (type) => {

    let item = null;

    if (type === 'other') {
        item = { label: 'D', value: '76', content: '其它', isCorrect: false, score: 0 }
    } else {
        item = { label: 'D', value: '76', content: '选项1', isCorrect: false, score: 0 }
    }

    selectAnswersData.value.push(item);
}

// 表单数据对象
const blankAnswersData = computed({
    get: () => {
        return props.question.blanks
    },
    set: (value) => {
        set(props.question.blanks, 'answers_blanks_node_data', value)
    }
})

// 添加填空题
const handleAddBlank = () => {
    const item = { correctAnswer: 'A', index: 6 }
    blankAnswersData.value.push(item);
}

// 处理必填状态的computed属性
const requiredStatus = computed({
    get: () => {
        return props.question?.isRequired || false
    },
    set: (value) => {
        console.log('requiredStatus', value)
        emit('updateQuestionIsRequired', props.question.id, value)
    }
})

const handleDeleteQuestion = async () => {
    try {
        await ElMessageBox.confirm(
            '确定要删除这个问题吗？',
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        emit('delete-question', props.question.id)
    } catch {
        // 用户取消了删除
    }
}

const handleUpdateTitle = (newValue) => {
  // 可以添加额外处理逻辑
  console.log('值已更新:', newValue);
  emit('update-questionTitle', props.question.id , newValue)
};

// 获取上传的图片并更新
const handleUploadMedia = (uploadItem) => {
    console.log('uploadItem = ' + JSON.stringify(uploadItem) + ' , type = ' + uploadItem.type)
    mediaFiles.value.push(uploadItem);

    emit('updateHandleUpdateQuestion', {
        ...props.question,
        mediaFiles : mediaFiles.value
    })
}

</script>

<style lang="scss" scoped>

.question-not-edit {
    width: 100% !important;
    box-shadow: none !important;
}

.question-container {

    width: 90%;
    max-width: 900px;
    margin: auto;
    margin-bottom: 10px;
    margin-top: 10px;
    border-radius: 10px;
    padding: 30px;
    text-align: left;
    padding-top: 10px;
    padding-bottom: 20px;
    border: 2px solid transparent;

    &:hover {
        cursor: move;
        box-shadow: 0px 4px 16px rgba(0, 0, 0, 0.07);
    }

    .question-required {
        margin-left: -20px;

        i {
            color: rgba(218, 35, 25, 1);
            font-style: normal;
        }
    }

    .question-tags {

        margin-top: 10px;
        margin-bottom: 10px;

        .switch-item {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 13px;
            float: right;
            margin-right: 0px;
        }

        .tag {
            display: inline-block;
            vertical-align: 2px;
            padding: 2px 7px;
            background: #ebf3fd;
            background-color: rgba(0, 0, 0, 0.05);
            border-radius: 3px;
            font-size: 14px;
            margin-right: 10px;
        }
    }

    .question-title {

        line-height: 2rem;
        margin-bottom: 10px;

        .title {
            font-weight: normal;
            word-break: break-word;
            cursor: text;
            font-size: 17px;
            display: flex;
            align-items: center;
        }
    }

    .question-footer {
        margin-top: 20px;
    }

}
</style>