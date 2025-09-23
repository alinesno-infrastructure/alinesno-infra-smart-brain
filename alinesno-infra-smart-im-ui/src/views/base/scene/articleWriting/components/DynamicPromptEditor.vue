<template>
  <div class="dynamic-prompt-editor" @click="handleContainerClick">
    <div class="editor-content" ref="editorRef">
      <template v-for="(segment, index) in parsedSegments" :key="index">
        <span 
          v-if="segment.type === 'text'" 
          class="static-text"
        >{{ segment.content }}</span>
        
        <span
          v-else
          :class="['placeholder', { 'editing': isEditing(segment.key) }]"
          :style="{ backgroundColor: getPlaceholderColor(segment.key) }"
          ref="placeholderRefs"
          :data-key="segment.key"
          contenteditable="true"
          @focus="handleFocus($event, segment.key)"
          @blur="handleBlur($event, segment.key)"
          @input="handleInput($event, segment.key)"
          @keydown="handleKeydown($event, segment.key)"
        >{{ getPlaceholderDisplayText(segment.key) }}</span>
      </template>
    </div>
    
    <div v-if="isInvalidTemplate" class="error-message">
      无效的模板格式
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'

// 预设颜色板
const COLOR_PALETTE = [
  '#E3F2FD', '#F3E5F5', '#E8F5E9', '#FFF3E0', 
  '#E0F7FA', '#FBE9E7', '#EFEBE9', '#E8EAF6'
]

export default {
  name: 'DynamicPromptEditor',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    promptText: {
      type: String,
      default: '',
      validator: value => typeof value === 'string'
    }
  },
  emits: ['update:modelValue'],
  
  setup(props, { emit }) {
    const editorRef = ref(null)
    const placeholderRefs = ref([])
    const placeholderValues = ref({})
    const focusedPlaceholder = ref(null)
    const isInvalidTemplate = ref(false)
    
    // 解析模板文本
    const parsedSegments = computed(() => {
      isInvalidTemplate.value = false
      const segments = []
      const template = props.promptText
      let currentIndex = 0
      
      while (currentIndex < template.length) {
        const startIndex = template.indexOf('${', currentIndex)
        
        if (startIndex === -1) {
          // 没有更多占位符，添加剩余文本
          if (currentIndex < template.length) {
            segments.push({
              type: 'text',
              content: template.slice(currentIndex)
            })
          }
          break
        }
        
        // 添加起始位置到占位符开始之间的文本
        if (startIndex > currentIndex) {
          segments.push({
            type: 'text',
            content: template.slice(currentIndex, startIndex)
          })
        }
        
        const endIndex = template.indexOf('}', startIndex + 2)
        if (endIndex === -1) {
          // 没有闭合的 }，标记为无效模板
          isInvalidTemplate.value = true
          break
        }
        
        const key = template.slice(startIndex + 2, endIndex).trim()
        if (!key) {
          // 空占位符 key，标记为无效
          isInvalidTemplate.value = true
          break
        }
        
        segments.push({
          type: 'placeholder',
          key: key,
          content: template.slice(startIndex, endIndex + 1)
        })
        
        currentIndex = endIndex + 1
      }
      
      // 清理不存在的占位符数据
      if (!isInvalidTemplate.value) {
        const validKeys = segments
          .filter(s => s.type === 'placeholder')
          .map(s => s.key)
        
        Object.keys(placeholderValues.value).forEach(key => {
          if (!validKeys.includes(key)) {
            delete placeholderValues.value[key]
          }
        })
      }
      
      return isInvalidTemplate.value ? [] : segments
    })
    
    // 生成最终文本
    const generatedText = computed(() => {
      if (isInvalidTemplate.value) return ''
      
      let result = ''
      parsedSegments.value.forEach(segment => {
        if (segment.type === 'text') {
          result += segment.content
        } else {
          const value = placeholderValues.value[segment.key] || ''
          result += value.trim() || segment.content
        }
      })
      
      return result
    })
    
    // 监听生成文本变化并同步到父组件
    watch(generatedText, (newValue) => {
      emit('update:modelValue', newValue)
    }, { immediate: true })
    
    // 监听模板变化，重置状态
    watch(() => props.promptText, () => {
      focusedPlaceholder.value = null
    })
    
    // 哈希函数生成颜色
    const getPlaceholderColor = (key) => {
      let hash = 0
      for (let i = 0; i < key.length; i++) {
        hash = key.charCodeAt(i) + ((hash << 5) - hash)
      }
      return COLOR_PALETTE[Math.abs(hash) % COLOR_PALETTE.length]
    }
    
    // 获取占位符显示文本
    const getPlaceholderDisplayText = (key) => {
      const value = placeholderValues.value[key]
      // 当正在编辑且值为空时，显示空字符串以便用户输入
      if (isEditing(key) && (!value || !value.trim())) {
        return ''
      }
      // 否则显示用户输入值或默认占位符
      return value && value.trim() ? value : `\${${key}}`
    }
    
    // 检查是否正在编辑
    const isEditing = (key) => {
      return focusedPlaceholder.value === key
    }
    
    // 处理聚焦事件
    const handleFocus = (event, key) => {
      focusedPlaceholder.value = key
      const element = event.target
      
      // 如果是默认文本，清空内容
      if (!placeholderValues.value[key] || !placeholderValues.value[key].trim()) {
        element.textContent = ''
      }
      
      // 设置最小宽度防止收缩
      element.style.minWidth = '60px'
      
      // 确保光标在起始位置
      nextTick(() => {
        const selection = window.getSelection()
        const range = document.createRange()
        range.selectNodeContents(element)
        range.collapse(true)
        selection.removeAllRanges()
        selection.addRange(range)
      })
    }
    
    // 处理失焦事件
    const handleBlur = (event, key) => {
      focusedPlaceholder.value = null
      const element = event.target
      const text = element.textContent || ''
      
      // 移除最小宽度
      element.style.minWidth = ''
      
      if (!text.trim()) {
        // 内容为空，恢复默认状态
        placeholderValues.value[key] = ''
        // 显式设置文本为占位符，确保视觉一致性
        element.textContent = `\${${key}}`
      } else {
        // 保存有效内容
        placeholderValues.value[key] = text
      }
    }
    
    // 处理输入事件 - 修复了空内容处理问题
    const handleInput = (event, key) => {
      const text = event.target.textContent || ''
      // 当内容为空时，清除保存的值而不是保存空字符串
      placeholderValues.value[key] = text.trim() || ''
    }
    
    // 处理键盘事件，增加对删除键的处理
    const handleKeydown = (event, key) => {
      // 当按下退格键且内容为空时，确保状态正确
      if ((event.key === 'Backspace' || event.key === 'Delete') && 
          !event.target.textContent.trim()) {
        placeholderValues.value[key] = ''
      }
    }
    
    // 处理容器点击事件
    const handleContainerClick = (event) => {
      // 如果点击的是容器本身而不是占位符
      if (event.target === editorRef.value) {
        const firstPlaceholder = placeholderRefs.value[0]
        if (firstPlaceholder) {
          firstPlaceholder.focus()
        }
      }
    }
    
    // 验证所有占位符是否已填写
    const validatePlaceholders = () => {
      const emptyPlaceholders = parsedSegments.value
        .filter(segment => segment.type === 'placeholder')
        .filter(segment => {
          const value = placeholderValues.value[segment.key]
          return !value || !value.trim()
        })
        .map(segment => segment.key)
      
      if (emptyPlaceholders.length > 0) {
        // 找到第一个空占位符并聚焦
        const firstEmptyKey = emptyPlaceholders[0]
        const placeholderElement = placeholderRefs.value.find(
          el => el.getAttribute('data-key') === firstEmptyKey
        )
        if (placeholderElement) {
          placeholderElement.focus()
        }
        return false
      }
      
      return true
    }
    
    return {
      editorRef,
      placeholderRefs,
      parsedSegments,
      isInvalidTemplate,
      getPlaceholderColor,
      getPlaceholderDisplayText,
      isEditing,
      handleFocus,
      handleBlur,
      handleInput,
      handleKeydown,
      handleContainerClick,
      validatePlaceholders
    }
  }
}
</script>

<style scoped>
.dynamic-prompt-editor {
  border-radius: 4px;
  padding: 8px;
  min-height: 40px;
  cursor: text;
}

.editor-content {
  outline: none;
  min-height: 24px;
  line-height: 2.5rem;
}

.static-text {
  user-select: none;
  pointer-events: none;
}

.placeholder {
  display: inline-block;
  min-height: 24px;
  border-radius: 3px;
  padding: 0 4px;
  margin: 0 2px;
  outline: none;
  cursor: text;
  transition: background-color 0.2s;
}

.placeholder.editing {
  box-shadow: 0 0 0 2px #2196F3;
}

.error-message {
  color: #f44336;
  font-size: 14px;
  margin-top: 8px;
}
</style>
