<template>
  <div class="editable-title-container">
    <div v-if="!isEditing" class="title-display" @click="startEditing">
      <el-popover
        placement="top"
        :width="Math.min(500, title.length * 10 + 50)"
        trigger="hover"
        :content="title"
        :disabled="title.length <= 50"
      >
        <template #reference>
          <div class="title-text">
            {{ displayTitle }}
          </div>
        </template>
      </el-popover>
    </div>
    
    <el-input
      v-else
      ref="titleInput"
      v-model="editTitle"
      @blur="saveTitle"
      @keyup.enter="saveTitle"
      size="large"
      class="title-input"
    />
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '测试标题',
    required: false 
  }
})

const emit = defineEmits(['update:title'])

const isEditing = ref(false)
const editTitle = ref('')
const titleInput = ref(null)

const displayTitle = computed(() => {
  return props.title.length > 50 ? props.title.substring(0, 50) + '...' : props.title
})

const startEditing = () => {
  isEditing.value = true
  editTitle.value = props.title
  nextTick(() => {
    titleInput.value?.focus()
  })
}

const saveTitle = () => {
  if (editTitle.value.trim() !== '') {
    emit('update:title', editTitle.value.trim())
  }
  isEditing.value = false
}
</script>

<style lang="scss" scoped>
.editable-title-container {
  width:100%;

  .title-display {
    cursor: pointer;
    font-size: 17px;
    font-weight: bold;
    padding: 5px;
    height: 40px;
    line-height: 28px;
    border-radius: 4px;
    background-color: #f5f5f5;

    &:hover {
      background-color: #f5f5f5;
    }
  }

  .title-input {
    font-size: 17px;
    font-weight: bold;

    :deep(.el-input__inner) {
      font-size: 17px;
      font-weight: bold;
    }
  }
}
</style>