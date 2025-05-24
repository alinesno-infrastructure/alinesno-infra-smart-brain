<template>
    <questionContainer :question="question">
        <div class="imageFile-question-wrapper">
            <!-- {{ props.question }} -->
            <!-- 
            <el-upload class="upload-demo" drag action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                multiple>
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                    Drop file here or <em>click to upload</em>
                </div>
                <template #tip>
                    <div class="el-upload__tip">
                        jpg/png files with a size less than 500kb
                    </div>
                </template>
            </el-upload> 
            -->

            <el-upload
                v-model:file-list="fileList"
                class="upload-demo"
                action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                list-type="picture">
                    <el-button type="primary">点击文件上传</el-button>
                    <template #tip>
                    <div class="el-upload__tip">
                        jpg/png 文件小于2M 
                    </div>
                </template>
            </el-upload>

        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps , ref } from 'vue'

import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

const fileList = ref([])

const handleRemove = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles)
}

const handlePreview = (file) => {
  console.log(file)
}

</script>

<style lang="scss" scoped>
</style>