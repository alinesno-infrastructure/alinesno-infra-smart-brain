<template>
    <questionContainer :question="question">
        <div class="descriptionPanel-question-wrapper">
            <el-input 
                type="textarea" 
                placeholder="请输入内容" 
                :rows="8"
                resize="none"
                v-model="answer" 
                size="large">
            </el-input>
        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps , ref } from 'vue'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const answer = ref('')

const props = defineProps({
    question: {
        type: Object,
        required: false
    }
})

</script>

<style lang="scss" scoped>

</style>