<template>
    <questionContainer :question="question">
        <div class="multiLine-question-wrapper">
            <!-- {{ props.question }} -->
            <el-input v-model="answer" type="textarea" :rows="6" resize="none" placeholder="请输入内容" />
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