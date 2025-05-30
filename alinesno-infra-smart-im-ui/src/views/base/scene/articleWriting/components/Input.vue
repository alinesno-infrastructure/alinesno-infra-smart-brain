<template>
  <div 
    class="input"
    :class="{
      'disabled': disabled,
      'focused': focused,
      'simple': simple,
    }"
  >
    <span class="prefix">
      <slot name="prefix"></slot>
    </span>
    <input
      type="text"
      ref="inputRef"
      :disabled="disabled"
      :value="value" 
      :placeholder="placeholder"
      :maxlength="maxlength"
      @input="handleInput"
      @focus="handleFocus"
      @blur="handleBlur"
      @change="$emit('change', $event)"
      @keydown.enter="$emit('enter', $event)"
      @keydown.backspace="$emit('backspace', $event)"
    />
    <span class="suffix">
      <slot name="suffix"></slot>
    </span>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  value: {
    type: String,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  placeholder: {
    type: String,
    default: ''
  },
  simple: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: undefined
  }
})

const emit = defineEmits([
  'update:value',
  'input',
  'change',
  'blur',
  'focus',
  'enter',
  'backspace'
])

const focused = ref(false)
const inputRef = ref(null)

const handleInput = (e) => {
  emit('update:value', e.target.value)
  emit('input', e)
}

const handleBlur = (e) => {
  focused.value = false
  emit('blur', e)
}

const handleFocus = (e) => {
  focused.value = true
  emit('focus', e)
}

const focus = () => {
  if (inputRef.value) inputRef.value.focus()
}

defineExpose({
  focus
})
</script>

<style lang="scss" scoped>
@import  "@/assets/styles/ppt.variable.scss";;

.input {
  background-color: #fff;
  border: 1px solid #d9d9d9;
  padding: 0 5px;
  border-radius: $borderRadius;
  transition: border-color .25s;
  font-size: 13px;
  display: flex;

  input {
    min-width: 0;
    height: 30px;
    outline: 0;
    border: 0;
    line-height: 30px;
    vertical-align: top;
    color: $textColor;
    padding: 0 5px;
    flex: 1;
    font-size: 13px;
    font-family: -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,'Noto Sans',sans-serif,'Apple Color Emoji','Segoe UI Emoji','Segoe UI Symbol','Noto Color Emoji';

    &::placeholder {
      color: #bfbfbf;
    }
  }

  &:not(.disabled):hover, &.focused {
    border-color: $themeColor;
  }

  &.disabled {
    background-color: #f5f5f5;
    border-color: #dcdcdc;
    color: #b7b7b7;

    input {
      color: #b7b7b7;
    }
  }

  &.simple {
    border: 0;
  }

  .prefix, .suffix {
    display: flex;
    justify-content: center;
    align-items: center;
    line-height: 30px;
    user-select: none;
  }
}
</style>