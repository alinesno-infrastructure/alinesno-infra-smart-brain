<template>
  <div style="width:100%" @click="handleDocumentClick">
    <div class="cm-container content-formater-box " :class="locateAfterTextStatus?'find-text-locate':''">
      <code-mirror ref="editorRef" basic :lang="lang" v-model="data" @change="handleEditorChange"
        :extensions="extensions" style="height: calc( 100vh - 120px);" :theme="theme" @contextmenu.prevent="showContextMenu" />
    </div>
    <!-- 自定义上下文菜单 -->
    <div id="custom-context-menu" class="hidden" @click.stop>
      <p>请输入您的要求：</p>
      <el-form style="width:100%" :model="formData" :rules="rules" ref="formRef">
        <el-form-item prop="userRequirement">
          <el-input 
            v-model="formData.userRequirement" 
            placeholder="请输入要求 ..." 
            clearable
            @keyup.enter="submitForm"
          ></el-input>
        </el-form-item>
      </el-form>

      <ul>
        <li @click="currentAction = 'rewrite'; submitForm('rewrite')">
          <el-button type="primary" text bg><i class="fa-solid fa-file-pen"></i> 重写</el-button>
        </li>
        <li @click="currentAction = 'polish'; submitForm('polish')">
          <el-button type="primary" text bg><i class="fa-solid fa-pen-nib"></i> 润色</el-button>
        </li>
        <li @click="currentAction = 'expand'; submitForm('expand')">
          <el-button type="primary" text bg><i class="fa-solid fa-marker"></i> 扩写</el-button>
        </li>
      </ul>
      <div class="confimation-text-panel">
        <el-divider />
        <p>以下为新的内容:</p>
        <el-scrollbar height="150px">
          <p style="height: 150px;">
            {{ confirmationText }}
          </p>
        </el-scrollbar>
        <br />
        <span slot="footer" class="dialog-footer">
          <el-button @click="hideContextMenu()" text bg>取 消</el-button>
          <el-button type="primary" text bg @click="confirmReplacement">确认替换</el-button>
        </span>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose } from 'vue';
import { ElMessage } from 'element-plus';
import CodeMirror from 'vue-codemirror6';
import { oneDark } from '@codemirror/theme-one-dark';
import { markdown } from '@codemirror/lang-markdown';

import { getParam } from '@/utils/ruoyi'
// import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";
import { EditorView } from '@codemirror/view';
import { ElInput, ElLoading } from 'element-plus';

import { 
  processParagraph 
} from '@/api/base/im/scene/longText'

const sceneId = ref(null);
const editorRoleId = ref(null) // 编辑角色
const lang = markdown();
const { proxy } = getCurrentInstance();

// 主题样式设
const theme = {
  "&": {
    fontSize: "10.5pt",
  }
}

const props = defineProps({
  businessId: {
    type: String,
    default: null
  }
})

const data = ref('');
// const userRequirement = ref(''); // 用户输入的要求
const currentAction = ref('');   // 当前选择的操作

// 定义表单数据模型
const formData = reactive({
  userRequirement: ''
});

// 定义校验规则
const rules = reactive({
  userRequirement: [
    { required: true, message: '请输入要求', trigger: 'blur' },
    { pattern: /^\S+.*$/, message: '不能全为空白字符', trigger: 'blur' }
  ]
});

// 获取表单引用
const formRef = ref(null);

/** 设置数据内容 */
const setData = (content , chapterEditor) => {

  if(!content){
    content = '未生成内容' ;
  }

  data.value = content ; // ? content:'未生成内容.' ; 
  editorRoleId.value = chapterEditor ?? '0';
  // console.log('data.value  = ' + data.value + ' , chapterEditor = ' + editorRoleId.value) ; 
}

/** 获取数据内容 */
const getData = () => {
  return data.value;
}

/** 编辑器改变 **/
const handleEditorChange = (content) => {
  console.log('this is a test:' + content);
}

let editorRef = ref(null);

// 用于存储当前的选择信息
let currentSelection = null;

// 对话框状态
const dialogVisible = ref(false);
const confirmationText = ref('');

const locateAfterTextStatus = ref(null)

// 显示确认对话框，带多个选项
const showConfirmation = async ({ text, from, to, viewUpdate }, selectedAction, requirement) => {
  try {
    const result = await handleAIRequest(text, viewUpdate, selectedAction, from, to, requirement);
    confirmationText.value = `${result}`;
    dialogVisible.value = true;
  } catch (error) {
    console.error('处理文本时发生错误:', error);
  }
};

// 处理AI请求
const handleAIRequest = async (text, viewUpdate, action, from, to, requirement) => {
  const loading = ElLoading.service({
    lock: true,
    text: '正在重写中...',
    background: 'rgba(0, 0, 0, 0.2)',
  });

  let requestData = {
    roleId: editorRoleId.value ,
    sceneId: sceneId.value ,
    modifyContent: text , 
    action: action ,
    requirement: requirement ,
  }

  let result = '' ;

  await processParagraph(requestData).then(res => {
    if(res.data){
      result = res.data;
      // userRequirement.value = '' ;
    }
    loading.close();
  }).catch(err => {
    loading.close();
  })

  return result ;
};

// 替换选择的文本
const replaceSelectedText = (newText, editorView, from, to) => {
  if (!editorView) {
    console.error('editorView is undefined');
    return;
  }

  console.log('new text = ' + newText);

  // 创建并分发事务（transaction）
  const transaction = {
    changes: {
      from,
      to,
      insert: newText
    }
  };

  // 使用 editorView.dispatch 直接分发更新
  editorView.dispatch(transaction);
};

// 在显示上下文菜单时保存 editorView 到当前的选择信息中
const showContextMenu = (event) => {
  // 隐藏之前显示的菜单（如果有）
  hideContextMenu();

  // 获取当前的选择信息
  const editorView = editorRef.value?.view;

  if (editorView) {
    const selection = editorView.state.sliceDoc(
      editorView.state.selection.main.from,
      editorView.state.selection.main.to
    );

    if (selection.trim()) {
      currentSelection = {
        text: selection,
        from: editorView.state.selection.main.from,
        to: editorView.state.selection.main.to,
        editorView // 保存 editorView 以备后续使用
      };

      // 计算菜单位置，考虑页面滚动和视口边界
      const contextMenu = document.getElementById('custom-context-menu');
      const menuWidth = 500; // contextMenu.offsetWidth;
      const menuHeight = 468; // contextMenu.offsetHeight;

      let left = event.pageX;
      let top = event.pageY + 10;

      // 如果菜单超出了右侧边界，则左移菜单
      if (left + menuWidth > window.innerWidth) {
        left -= menuWidth;
      }

      // 如果菜单超出了底部边界，则上移菜单
      if (top + menuHeight > window.innerHeight) {
        top -= menuHeight;
      }

      console.log('window.innerWidth = ' + window.innerWidth + ' left + menuWidth = ' + (left + menuWidth))
      console.log('window.innerHeight = ' + window.innerHeight + ' top + menuHeight = ' + (top + menuHeight))

      // 应用计算后的位置，并显示菜单
      contextMenu.style.left = `${left}px`;
      contextMenu.style.top = `${top}px`;
      contextMenu.classList.remove('hidden');

      // 清空用户要求输入框
      formData.userRequirement = '';
      confirmationText.value = '' ;
    }
  }
};

// 隐藏自定义上下文菜单
const hideContextMenu = () => {
  const contextMenu = document.getElementById('custom-context-menu');
  contextMenu.classList.add('hidden');
};

// 处理全局点击事件，避免点击菜单内元素时隐藏菜单
const handleDocumentClick = (event) => {
  const contextMenu = document.getElementById('custom-context-menu');
  if (!contextMenu.contains(event.target)) {
    hideContextMenu();
  }
};

// 提交表单的方法
const submitForm = (currentAction) => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 如果校验通过，执行提交逻辑
      handleMenuClick(currentAction);
    } else {
      console.log('校验失败');
      return false;
    }
  });
};

// 处理菜单项点击
const handleMenuClick = async (action) => {

  // 如果有当前选择，则显示确认对话框
  console.log('currentSelection.text:' + currentSelection.text);
  console.log('currentSelection.viewUpdate:' + currentSelection.viewUpdate);

  if (currentSelection && currentSelection.text.trim()) {
    await showConfirmation(currentSelection, action, formData.userRequirement);
  }
};

// 确认替换文本
const confirmReplacement = () => {
  const newText = confirmationText.value;
  if(newText == ''){
    proxy.$modal.msgWarning("替换内容为空.");
    return ;
  }

  const editorView = currentSelection.editorView;
  const from = currentSelection.from;
  const to = currentSelection.to;

  // 检查选择范围的有效性
  const docLength = editorView.state.doc.length;
  if (from < 0 || to < 0 || from > docLength || to > docLength) {
    console.error('Selection range is out of document bounds');
    return;
  }

  replaceSelectedText(newText, currentSelection.editorView, currentSelection.from, currentSelection.to);

  // 更新 currentSelection
  currentSelection = {
    text: newText,
    from: from,
    to: from + newText.length,
    editorView
  };

  dialogVisible.value = false;
  hideContextMenu();
};

// 新增定位方法
const locateAfterText = (targetText) => {

  locateAfterTextStatus.value = true ;

  const editorView = editorRef.value?.view;
  if (editorView) {
    const doc = editorView.state.doc.toString();
    const index = doc.indexOf(targetText);
    if (index !== -1) {
      const position = index + targetText.length;
      console.log('position = ' + position) 
      editorView.dispatch({
        selection: { anchor: position },
        scrollIntoView: true
      });
    } else {
      console.log('未找到指定字符');
      locateAfterTextStatus.value = null ; 
      ElMessage.warning('未找到指定字符');
    }
  }
};

onMounted(() => {
  sceneId.value = getParam('sceneId')
})

const extensions = [
  oneDark,
  EditorView.lineWrapping,
];

defineExpose({
  setData,
  getData,
  locateAfterText
});
</script>

<style scoped lang="scss">
.submit-button-group {
  margin-top: 20px;
  margin-bottom: 0px;
  width: 100%;
  text-align: right;
}
</style>

<style lang="css">
.content-formater-box .cm-editor{
    border-radius: 10px;
}

.find-text-locate .cm-activeLine.cm-line{
  background-color: #373332;
  border: 2px solid #ff875a;
  border-radius: 4px;
}
</style>
