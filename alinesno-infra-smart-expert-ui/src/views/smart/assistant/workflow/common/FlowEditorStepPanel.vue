<template>
  <div>
    <slot name="read">
      <div style="display: flex;align-items: center;gap: 10px;" v-if="!isEdit" @dblclick="dblclick">
        <el-tooltip :content="data" placement="top">
          {{ data }}
        </el-tooltip>

        <!-- <el-button
          v-if="trigger === 'default' && showEditIcon"
          class="ml-4"
          @click.stop="editNameHandle"
          text
        >
        asdfasdf
        </el-button> -->
        <div class="document-link" >
            <i class="fa-solid fa-feather"></i>
        </div>
      </div>
    </slot>
    <slot>
      <div class="flex align-center" @click.stop v-if="isEdit">
        <div class="w-full">
          <el-input
            ref="inputRef"
            v-model="writeValue"
            :placeholder="请输入新的名称"
            autofocus
            :maxlength="maxlength || '-'"
            :show-word-limit="maxlength ? true : false"
            @blur="isEdit = false"
            @keyup.enter="submit"
            clearable
          ></el-input>
        </div>

      </div>
    </slot>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';

// 定义 props
const props = defineProps({
  data: {
    type: String,
    default: ''
  },
  showEditIcon: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: 0
  },
  trigger: {
    type: String,
    default: 'default',
    validator: function (value) {
      return ['default', 'dblclick', 'manual'].includes(value);
    }
  },
  write: {
    type: Boolean,
    default: false
  }
});

// 定义 emits
const emit = defineEmits(['change', 'close']);

// 定义响应式变量
const inputRef = ref(null);
const isEdit = ref(false);
const writeValue = ref('');
const loading = ref(false);

// 监听 isEdit 变化
watch(isEdit, (bool) => {
  if (!bool) {
    writeValue.value = '';
    emit('close');
  } else {
    setTimeout(() => {
      nextTick(() => {
        inputRef.value?.focus();
      });
    }, 200);
  }
});

// 监听 props.write 变化
watch(
  () => props.write,
  (bool) => {
    if (bool && props.trigger === 'manual') {
      editNameHandle();
    } else {
      isEdit.value = false;
    }
  }
);

// 双击事件处理函数
function dblclick() {
  if (props.trigger === 'dblclick') {
    editNameHandle();
  }
}

// 提交函数
function submit() {
  loading.value = true;
  emit('change', writeValue.value);
  setTimeout(() => {
    isEdit.value = false;
    loading.value = false;
  }, 200);
}

// 编辑名称处理函数
function editNameHandle() {
  writeValue.value = props.data;
  isEdit.value = true;
}

// 挂载钩子
onMounted(() => {});
</script>

<style lang="scss" scoped></style>