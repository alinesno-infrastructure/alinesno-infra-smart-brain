<template>
    <questionContainer :question="question">
        <div class="singleLine-question-wrapper">
            <el-input 
                v-model="answer" 
                size="large"
                placeholder="请输入内容"></el-input>
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
        required: true
    }
})

</script>

<style lang="scss" scoped>

</style>