<template>
    <questionContainer :question="question">
        <div class="dateTime-question-wrapper">
            <!-- {{ props.question }} -->
            <el-input type="date" placeholder="选择日期" v-model="answer" size="large"></el-input>
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