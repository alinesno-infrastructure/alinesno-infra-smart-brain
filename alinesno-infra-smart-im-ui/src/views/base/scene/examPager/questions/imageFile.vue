<template>
    <questionContainer :question="question">
        <div class="imageFile-question-wrapper">
            <el-upload
                v-model:file-list="fileList"
                class="upload-demo"
                :action="uploadUrl"
                :limit="maxFiles"
                :headers="uploadHeaders"
                :on-success="handleSuccess"
                :on-remove="handleRemove"
                :on-error="handleError"
                :before-upload="beforeUpload"
                list-type="picture-card"
                :multiple="allowMultiple">
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                <template #tip>
                    <div class="el-upload__tip">
                        支持 {{ supportedFormats.join('/') }} 格式，每张图片不超过 {{ maxSize }}MB
                    </div>
                </template>
            </el-upload>
        </div>
    </questionContainer>
</template>

<script setup>
import { defineProps, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

const emit = defineEmits(['update-handleUpdateQuestion'])

// 上传配置
const uploadUrl = import.meta.env.VITE_APP_BASE_API + '/v1/api/infra/base/im/chat/importData'
const displayUrl = import.meta.env.VITE_APP_BASE_API + '/v1/api/infra/base/im/chat/displayImage/'
const uploadHeaders = { Authorization: 'Bearer ' + getToken() }
const maxSize = 2 // MB
const maxFiles = props.question.limit || 1 // 默认最多上传1个文件
const allowMultiple = computed(() => maxFiles > 1)
const supportedFormats = ['jpg', 'jpeg', 'png']

// 文件列表
const fileList = ref([])

// 初始化已有答案
if (props.question.answer && props.question.answer.length > 0) {
    fileList.value = props.question.answer.map(url => ({
        url: displayUrl + url,
        name: url.split('/').pop(),
        status: 'success'
    }))
}

// 上传前校验
const beforeUpload = (file) => {
    const isImage = supportedFormats.some(format => 
        file.type.includes(format) || file.name.toLowerCase().endsWith(`.${format}`)
    )
    const isLtSize = file.size / 1024 / 1024 <= maxSize

    if (!isImage) {
        ElMessage.error(`只支持 ${supportedFormats.join(', ')} 格式!`)
        return false
    }
    if (!isLtSize) {
        ElMessage.error(`图片大小不能超过 ${maxSize}MB!`)
        return false
    }
    return true
}

// 上传成功处理
const handleSuccess = (response, uploadFile) => {
    if (response.code !== 200) {
        ElMessage.error(response.msg || '上传失败')
        return
    }
    
    // 更新题目答案
    const updatedQuestion = {
        ...props.question,
        answer: response.data ? response.data.split(',') : [],
        userAnswer: response.data ? response.data.split(',') : []
    }
    emit('update-handleUpdateQuestion', updatedQuestion)
}

// 移除文件处理
const handleRemove = (file, fileList) => {
    const updatedQuestion = {
        ...props.question,
        answer: fileList.map(f => f.response?.data).filter(Boolean).join(',').split(',') || [],
        userAnswer: fileList.map(f => f.response?.data).filter(Boolean).join(',').split(',') || []
    }
    emit('update-handleUpdateQuestion', updatedQuestion)
}

// 上传错误处理
const handleError = (error) => {
    ElMessage.error(`上传失败: ${error.message || '未知错误'}`)
}
</script>

<style lang="scss" scoped>
.imageFile-question-wrapper {
    padding: 10px 0;
    
    .upload-demo {
        width: 100%;
        
        :deep(.el-upload) {
            width: 100px;
            height: 100px;
            line-height: 100px;
        }
        
        :deep(.el-upload-list__item) {
            transition: all 0.3s ease;
        }
    }
    
    .el-upload__tip {
        margin-top: 10px;
        color: var(--el-text-color-secondary);
        font-size: 12px;
    }
    
    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 100px;
        height: 100px;
        line-height: 100px;
        text-align: center;
    }
}
</style>