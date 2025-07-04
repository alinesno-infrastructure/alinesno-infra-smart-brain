<template>
    <div class="dynamic-prompt-editor">
        <div class="editor-container" ref="editorContainer" @click="handleContainerClick">
            <template v-if="hasParsedSegments">
                <template v-for="(segment, index) in parsedSegments" :key="index">
                    <span v-if="!segment?.isPlaceholder" class="static-text">{{ segment?.text }}</span>

                    <span v-else class="placeholder" :style="{
            backgroundColor: getPlaceholderColor(segment.placeholderKey),
            minWidth: shouldShowPlaceholder(segment.placeholderKey) ? 'auto' : '60px'
        }" contenteditable="true" @input="handlePlaceholderInput(segment.placeholderKey, $event)"
                        @focus="handlePlaceholderFocus(segment.placeholderKey, $event)"
                        @blur="handlePlaceholderBlur(segment.placeholderKey, $event)"
                        :ref="el => setPlaceholderRef(el, segment.placeholderKey)">{{ getPlaceholderDisplayText(segment)
                        }}</span>

                </template>
            </template>
            <div v-else class="error-message">
                无效的模板格式
            </div>
        </div>

        <input type="hidden" v-model="generatedText" />
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    promptText: {
        type: String,
        default: '',
        validator: value => typeof value === 'string'
    }
})

const emit = defineEmits(['update:modelValue'])

// 响应式数据
const placeholderValues = ref({})
const currentPlaceholder = ref(null)
const placeholderRefs = ref({})
const editorContainer = ref(null)
const parseError = ref(null)

// 响应式数据
const focusedPlaceholders = ref(new Set())

const colorPalette = [
    '#e3f2fd', '#fff8e1', '#e8f5e9', '#f3e5f5',
    '#ffebee', '#fce4ec', '#f3e5f5', '#ede7f6',
    '#e8eaf6', '#e3f2fd', '#e1f5fe', '#e0f7fa'
]

// 计算显示文本
const getPlaceholderDisplayText = (segment) => {
    if (!segment.isPlaceholder) return segment.text
    // 如果当前聚焦，不显示占位标记
    if (focusedPlaceholders.value.has(segment.placeholderKey)) {
        return placeholderValues.value[segment.placeholderKey] || ''
    }
    // 如果有值显示值，否则显示占位标记
    return placeholderValues.value[segment.placeholderKey] || segment.originalText
}

// 处理聚焦事件 - 改进版
const handlePlaceholderFocus = (key, event) => {
    currentPlaceholder.value = key
    focusedPlaceholders.value.add(key)

    // 设置最小宽度防止收缩
    if (event.target) {
        event.target.style.minWidth = `${event.target.offsetWidth}px`
    }

    // 如果当前内容是占位标记，清空但保留一个空格
    if (event.target.innerText === `\${${key}}`) {
        event.target.innerText = placeholderValues.value[key] || ' '
        setCaretPosition(event.target, 0)
    }
}

// 处理失去焦点事件 - 改进版
const handlePlaceholderBlur = (key, event) => {
    focusedPlaceholders.value.delete(key)

    // 移除固定宽度
    if (event.target) {
        event.target.style.minWidth = ''
    }

    // 处理内容
    const text = event.target.innerText.trim()
    if (!text) {
        event.target.innerText = `\${${key}}`
        placeholderValues.value[key] = ''
    } else if (text === ' ') {
        event.target.innerText = `\${${key}}`
        placeholderValues.value[key] = ''
    } else {
        placeholderValues.value[key] = text
    }
}

// 计算属性
const hasParsedSegments = computed(() => {
    return !parseError.value && parsedSegments.value?.length > 0
})

const generatedText = computed({
    get() {
        return props.modelValue || ''
    },
    set(value) {
        emit('update:modelValue', value || '')
    }
})

// 方法
const getPlaceholderColor = (key) => {
    try {
        if (!key) return colorPalette[0]

        let hash = 0
        for (let i = 0; i < key.length; i++) {
            hash = key.charCodeAt(i) + ((hash << 5) - hash)
        }
        return colorPalette[Math.abs(hash) % colorPalette.length] || colorPalette[0]
    } catch {
        return colorPalette[0]
    }
}

const setPlaceholderRef = (el, key) => {
    if (el && key) {
        placeholderRefs.value[key] = el
    }
}

const handlePlaceholderInput = (key, event) => {
    if (!key || !event?.target) return

    try {
        const newValue = event.target.innerText || ''
        placeholderValues.value[key] = newValue
        updateGeneratedText()

        if (!newValue.trim()) {
            event.target.innerText = `\${${key}}`
            setCaretPosition(event.target, 2)
            placeholderValues.value[key] = ''
        }
    } catch (error) {
        console.error('处理输入时出错:', error)
    }
}

const handleContainerClick = (event) => {
    if (!event?.target || !placeholderRefs.value) return

    try {
        if (!event.target.classList.contains('placeholder')) {
            if (currentPlaceholder.value && placeholderRefs.value[currentPlaceholder.value]) {
                placeholderRefs.value[currentPlaceholder.value].focus()
            } else if (Object.keys(placeholderRefs.value).length > 0) {
                const firstKey = Object.keys(placeholderRefs.value)[0]
                if (placeholderRefs.value[firstKey]) {
                    placeholderRefs.value[firstKey].focus()
                }
            }
        }
    } catch (error) {
        console.error('处理点击时出错:', error)
    }
}

const setCaretPosition = (element, position) => {
    if (!element || position === undefined) return

    try {
        const range = document.createRange()
        const selection = window.getSelection()
        if (selection) {
            range.setStart(element.firstChild || element, Math.max(0, position))
            range.collapse(true)
            selection.removeAllRanges()
            selection.addRange(range)
        }
    } catch (error) {
        console.error('设置光标位置时出错:', error)
    }
}

const updateGeneratedText = () => {
    try {
        if (!props.promptText) {
            generatedText.value = ''
            return
        }

        let result = props.promptText
        for (const [key, value] of Object.entries(placeholderValues.value)) {
            if (key && value !== undefined) {
                result = result.replace(new RegExp(`\\\$\\{${key}\\}`, 'g'), value || '')
            }
        }
        generatedText.value = result
    } catch (error) {
        console.error('更新生成文本时出错:', error)
        generatedText.value = ''
    }
}

// 判断是否应该显示占位标记
const shouldShowPlaceholder = (key) => {
    return !focusedPlaceholders.value.has(key) && !placeholderValues.value[key]
}

// 当前模板的占位符集合
const currentTemplatePlaceholders = ref(new Set())

// 解析模板时提取占位符
const extractPlaceholders = (text) => {
  const placeholders = new Set()
  const regex = /\$\{([^}]+)\}/g
  let match
  while ((match = regex.exec(text))) {
    if (match[1]) {
      placeholders.add(match[1])
    }
  }
  return placeholders
}

// 清理不存在的占位符值
const cleanStalePlaceholderValues = (newPlaceholders) => {
  const newValues = {}
  newPlaceholders.forEach(key => {
    newValues[key] = placeholderValues.value[key] || ''
  })
  placeholderValues.value = newValues
}

// 计算属性
const parsedSegments = computed(() => {
  if (!props.promptText) return []
  
  // 提取当前模板的所有占位符
  const newPlaceholders = extractPlaceholders(props.promptText)
  currentTemplatePlaceholders.value = newPlaceholders
  
  // 清理不再存在的占位符值
  cleanStalePlaceholderValues(newPlaceholders)
  
  // 剩余解析逻辑保持不变...
  const segments = []
  const placeholderRegex = /\$\{([^}]+)\}/g
  let lastIndex = 0
  let match
  
  while ((match = placeholderRegex.exec(props.promptText))) {
    if (match.index > lastIndex) {
      segments.push({
        isPlaceholder: false,
        text: props.promptText.substring(lastIndex, match.index)
      })
    }
    
    const placeholderKey = match[1]
    segments.push({
      isPlaceholder: true,
      placeholderKey: placeholderKey,
      displayText: placeholderValues.value[placeholderKey] || match[0],
      originalText: match[0]
    })
    
    lastIndex = match.index + match[0].length
  }
  
  if (lastIndex < props.promptText.length) {
    segments.push({
      isPlaceholder: false,
      text: props.promptText.substring(lastIndex)
    })
  }
  
  return segments
})


// 添加这个方法来验证占位符是否全部填写
const validatePlaceholders = () => {
    // 查找第一个未填写的占位符
    const emptyPlaceholder = parsedSegments.value.find(
        segment => segment.isPlaceholder && !placeholderValues.value[segment.placeholderKey]
    );
    
    if (emptyPlaceholder) {
        // 聚焦到第一个未填写的占位符
        const placeholderEl = placeholderRefs.value[emptyPlaceholder.placeholderKey];
        if (placeholderEl) {
            placeholderEl.focus();
        }
        return false; // 返回false表示有未填写的占位符
    }
    
    return true; // 返回true表示所有占位符都已填写
};


// 生命周期
onMounted(() => {
    updateGeneratedText()
})

// 监听promptText变化，主动清理旧值
watch(() => props.promptText, (newVal, oldVal) => {

    updateGeneratedText()

  if (newVal !== oldVal) {
    const newPlaceholders = extractPlaceholders(newVal)
    cleanStalePlaceholderValues(newPlaceholders)
    currentTemplatePlaceholders.value = newPlaceholders
  }
})

watch(parsedSegments, () => {
    updateGeneratedText()
}, { deep: true })

// 将方法暴露给父组件使用
defineExpose({
    validatePlaceholders
});

</script>

<style scoped>
.dynamic-prompt-editor {
    /* border: 1px solid #dcdfe6; */
    border-radius: 4px;
    padding: 10px;
    min-height: 80px;
    line-height: 1.5;
    font-size: 15px;
    color: #222;
    position: relative;
}

.editor-container {
    white-space: pre-wrap;
    outline: none;
    min-height: 60px;
    line-height: 1.6rem;
}

.static-text {
    user-select: none;
}

.placeholder:focus {
    border-bottom: 1px solid #409eff;
}

.error-message {
    color: #f56c6c;
    font-size: 14px;
    padding: 8px;
    background-color: #fef0f0;
    border-radius: 4px;
}

.placeholder {
    display: inline-block;
    min-width: 60px;
    border-radius: 3px;
    padding: 0 3px;
    outline: none;
    border-bottom: 1px dashed #999;
    margin: 0 2px;
    transition: min-width 0.2s ease;
    white-space: nowrap;
    color: #0057ff;
    font-weight: bold;
    opacity: 0.9;
}
</style>