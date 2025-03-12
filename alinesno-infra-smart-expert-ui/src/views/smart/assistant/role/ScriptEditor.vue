<template>
  <div class="cm-container">
    <code-mirror basic :lang="lang" v-model="codeVal" :style="'height:calc(100vh - 290px)'" :theme="theme"
      :extensions="extensions" />

    <div class="function-CodemirrorEditor__footer" >
      <el-button text @click="openCodemirrorDialog" style="background-color: transparent !important;" class="magnify">
        <i class="fa-solid fa-up-right-and-down-left-from-center"></i>
      </el-button>
    </div>

    <el-dialog v-model="dialogVisible" :title="'函数内容（Groovy）'" append-to-body fullscreen>
        <template #header>
          <div class="dialog-footer mt-24" style="display: flex;justify-content: space-between;align-items:center">
            <div>
              函数内容(Groovy)
            </div>
            <div>
              <!-- <el-button type="warning" @click="validateScript" bg text size="large"> 试运行</el-button> -->
              <el-button type="primary" @click="submitDialog" bg text size="large"> 确定 </el-button>
            </div>
          </div>
        </template>
      <div class="full-eidtor">
        <code-mirror basic :lang="lang" v-model="cloneContent" :style="'height:calc(100vh - 120px)'" :theme="theme" :extensions="extensions" />
      </div>
    </el-dialog>

  </div>
</template>

<script setup>

import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { python } from '@codemirror/lang-python';
import { java } from '@codemirror/lang-java';

import { json } from '@codemirror/lang-json';
import { sql } from '@codemirror/lang-sql';
import { yaml } from '@codemirror/lang-yaml';
import { markdown } from '@codemirror/lang-markdown';

const router = useRouter();
const { proxy } = getCurrentInstance();

const dialogVisible = ref(false)
const cloneContent = ref('')

const props = defineProps({
  lang: {
    type: String,
    default: 'python',
  },
  height: {
    type: String,
    default: '600px',
  },
});

// 初始化
let codeVal = ref('');
const editorHeight = ref(props.height);

const lang = props.lang == 'python' ? python() :
  props.lang == 'json' ? json() :
    props.lang == 'yaml' ? yaml() :
      props.lang == 'sql' ? sql() :
        props.lang == 'markdown' ? markdown() :
          props.lang == 'java' ? java() :
            python();

// 扩展
const extensions = [oneDark];

// 主题样式设置
const theme = {
  "&": {
    fontSize: "10pt",
  }
}

const calculateFirstSectionHeight = () => {
  const windowHeight = window.innerHeight;
  const offset = 280;
  editorHeight.value = `${windowHeight - offset}px`;
  console.log(editorHeight.value); //打印高度
}

const submitDialog = () => {
  codeVal.value = cloneContent.value
  dialogVisible.value = false
}

/**
 * 获取到codeValue
 */
function getRawScript() {
  console.log('codeVal.value = '+ codeVal.value);
  return codeVal.value;
}

/** 设置值  */
function setRawScript(val) {
  console.log('setRawScript = '+ val);
  if(val){
    return codeVal.value = val;
  }
}

onMounted(() => {
  calculateFirstSectionHeight();
  window.addEventListener('resize', calculateFirstSectionHeight);
});

/** 打开编辑器 */
function openCodemirrorDialog() {
  cloneContent.value = codeVal.value
  dialogVisible.value = true
}

defineExpose({
  getRawScript,
  setRawScript
})

</script>

<style>
/* required! */
.cm-editor {
  height: 100% ;
}

/* .full-eidtor.cm-container {
  height: calc(100vh - 190px);
} */

.cm-container {
  width: 100%;
}

.cm-container .function-CodemirrorEditor__footer {
  position: absolute;
  bottom: 5px;
  right: 0px;
}
</style>
