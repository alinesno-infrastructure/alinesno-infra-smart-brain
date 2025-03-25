<template>
  <div class="user-question-suggestions-container" v-if="userQuestionSuggestions.length > 0">
    <div class="user-question-suggestions-wrapper">
        <div v-for="(item, index) in userQuestionSuggestions" :key="index" @click="handleUserQuestionSuggestionsClick(item)">
          <span class="user-question-suggestions-item" >
            {{ item }} <el-icon><Right /></el-icon>
          </span>
        </div>
    </div>
  </div>
</template>

<script setup name="UserQuestionSuggestions">

import { getRoleQuestionSuggestion } from '@/api/smart/assistant/roleChat'
const emit = defineEmits(['handleUserQuestionSuggestionsClick', 'initChatBoxScroll'])

const props = defineProps({
  roleId: {
    type: Number ,
    default: 0
  } , 
  greetingQuestion: {
    type: Array , 
    default: [] 
  } ,
  channelId: {
    type: Number ,
    default: 0
  },
  chatStreamLoading: {
    type: Boolean ,
    default: false
  }
})

const userQuestionSuggestions = ref(props.greetingQuestion? props.greetingQuestion : [])

// 一开始进来就获取问题建议
const handleGetUserQuestionSuggestions = () => {
  const data = {
    roleId: props.roleId ,
    channelId: props.channelId 
  }
  getRoleQuestionSuggestion(data).then(res => {
    userQuestionSuggestions.value = res.data
    emit('initChatBoxScroll')
  })
}

const handleUserQuestionSuggestionsClick = (item) => {
  emit('handleUserQuestionSuggestionsClick' , item)
}

// 如果说chatStreamLoading为false，则获取问题建议
watch(() => props.chatStreamLoading , (newValue , oldValue) => {
  if (!newValue) {
    handleGetUserQuestionSuggestions()
  }
})

// 如果说greetingQuestion有值，则设置为greetingQuestion拼接起来
watch(() => props.greetingQuestion , (newValue , oldValue) => {
  if (newValue.length > 0) {
    userQuestionSuggestions.value = newValue
  }
})

</script>

<style lang="scss" scoped>

.user-question-suggestions-container {

  .user-question-suggestions-wrapper {
    display: flex;
    gap: 5px;
    flex-direction: column;
  }

  span.user-question-suggestions-item {
    background: #fafafa;
    padding: 9px 10px 9px 16px;
    color: #777070;
    font-size: 14px;
    cursor: pointer;
    border-radius: 10px;
    float: left;
    margin-top: 2px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

</style>