<template>
  <div style="width:100%" @click="handleDocumentClick">
    <div class="cm-container">
      <code-mirror ref="editorRef" basic :lang="lang" v-model="data" @change="handleEditorChange"
        :extensions="extensions" style="height: 680px;" :theme="theme" @contextmenu.prevent="showContextMenu" />
    </div>
    <!-- 自定义上下文菜单 -->
    <div id="custom-context-menu" class="hidden" @click.stop>
      <!-- <p>修改文本：【{{ currentSelection.text }}】</p> -->
      <p>请输入您的要求：</p>
      <el-input v-model="userRequirement" placeholder="请输入要求 ..." clearable
        @keyup.enter="handleMenuClick(currentAction)"></el-input>
      <ul>
        <li @click="currentAction = 'rewrite'; handleMenuClick('rewrite')">
          <el-button type="primary" text bg><i class="fa-solid fa-file-pen"></i> 重写</el-button>
        </li>
        <li @click="currentAction = 'polish'; handleMenuClick('polish')">
          <el-button type="primary" text bg><i class="fa-solid fa-pen-nib"></i> 润色</el-button>
        </li>
        <li @click="currentAction = 'expand'; handleMenuClick('expand')">
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
          <el-button @click="dialogVisible = false" text bg>取 消</el-button>
          <el-button type="primary" text bg @click="confirmReplacement">确认替换</el-button>
        </span>
      </div>
    </div>

    <!-- 确认对话框 
    <el-dialog
      title="确认替换"
      v-model="dialogVisible"
      width="50%"
      :before-close="handleCloseDialog"
    >
      <p>{{ confirmationText }}</p>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmReplacement">确 认 替 换</el-button>
      </span>
    </el-dialog>
     -->

  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, defineExpose } from 'vue';
import CodeMirror from 'vue-codemirror6';
import { oneDark } from '@codemirror/theme-one-dark';
import { markdown } from '@codemirror/lang-markdown';

import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";
import { EditorView } from '@codemirror/view';
import { ElInput, ElLoading } from 'element-plus';

const screenId = ref(null);
const lang = markdown();

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
const userRequirement = ref(''); // 用户输入的要求
const currentAction = ref('');   // 当前选择的操作

/** 设置数据内容 */
const setData = (content) => {
  data.value = content ?? '未生成内容.';
  console.log('data.value  = ' + data.value);
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
  try {
    let result;
    switch (action) {
      case 'rewrite':
        result = await simulateAIProcess(text, "重写", requirement);
        break;
      case 'polish':
        result = await simulateAIProcess(text, "润色", requirement);
        break;
      case 'expand':
        result = await simulateAIProcess(text, "扩写", requirement);
        break;
      default:
        throw new Error("未知操作");
    }
    return result;
  } catch (error) {
    throw error;
  }
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
      let top = event.pageY;

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
      userRequirement.value = '';
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

/** 连接sse */
function handleSseConnect(screenId) {
  nextTick(() => {
    if (screenId) {

      let sseSource = openSseConnect(screenId);
      // 接收到数据
      sseSource.onmessage = function (event) {

        if (!event.data.includes('[DONE]')) {
          let resData = event.data;
          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            pushResponseMessageList(data);
          }
        } else {
          console.log('消息接收结束.')
        }

      }
    }
  })
}

// 处理菜单项点击
const handleMenuClick = async (action) => {

  // if (!userRequirement.value.trim()) {
  //   ElMessageBox.alert('请输入您的要求', '提示', {
  //     confirmButtonText: '确定',
  //     callback: () => {
  //       console.log('The user clicked OK');
  //     }
  //   });
  //   return;
  // }

  // 如果有当前选择，则显示确认对话框
  console.log('currentSelection.text:' + currentSelection.text);
  console.log('currentSelection.viewUpdate:' + currentSelection.viewUpdate);

  if (currentSelection && currentSelection.text.trim()) {
    await showConfirmation(currentSelection, action, userRequirement.value);
  }
};

// 确认替换文本
const confirmReplacement = () => {
  const newText = confirmationText.value;
  replaceSelectedText(newText, currentSelection.editorView, currentSelection.from, currentSelection.to);
  dialogVisible.value = false;
  hideContextMenu();
};

// 关闭对话框
// const handleCloseDialog = (done) => {
//   done();
// };

// 模拟AI处理过程
const simulateAIProcess = (text, action, requirement) => {
  const loading = ElLoading.service({
    lock: true,
    text: '正在重写中...',
    background: 'rgba(0, 0, 0, 0.7)',
  });

  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(`这是经过${action}后的文本，根据要求：${requirement}`);
      loading.close(); // 关闭加载提示
    }, 2000); // 2000毫秒后执行
  });

};

// onMounted(() => {
//   // 监听全局点击事件以隐藏上下文菜单
//   // 使用事件委托方式来处理点击事件
// });

// 清除事件监听器
// onUnmounted(() => {
//   // 不需要在此处清除事件监听器，因为我们在模板中使用了@click指令
// });

onMounted(() => {
  screenId.value = getParam('screenId')

  handleSseConnect(screenId.value)
  handleScreenMessage()
})


// 销毁信息
onBeforeUnmount(() => {
  handleCloseSse(screenId.value).then(res => {
    console.log('关闭sse连接成功:' + screenId)
  })
});

const extensions = [
  oneDark,
  EditorView.lineWrapping,
];

defineExpose({
  setData,
  getData
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

<style>
/* required! */
.cm-editor {
  height: 100%;
}

.cm-container {
  width: 100%;
}

#custom-context-menu {
  position: fixed;
  background-color: white;
  border: 1px solid #ccc;
  padding: 10px;
  width: 500px;
  min-height: 468px;
  z-index: 1000;
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;

  &.hidden {
    display: none;
  }

  ul {
    list-style-type: none;
    padding: 0;
    margin: 10px 0 0;

    li {
      padding: 5px 5px;
      cursor: pointer;
      float: left;
      margin: 0px 10px;
    }
  }

  .confimation-text-panel {
    width: 100%;

    p {
      margin: 5px;
    }
  }
}
</style>