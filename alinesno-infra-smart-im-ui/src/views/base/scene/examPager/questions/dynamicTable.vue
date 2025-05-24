<template>
    <questionContainer :question="question">
        <div class="dynamicTable-question-wrapper">
            {{ props.question }}
        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps } from 'vue'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

</script>

<style lang="scss" scoped>

</style>