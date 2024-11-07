<template>
    <div class="app-container" style="background-color: #fff;padding-top:10px;" v-loading="loading">
        <el-row>
            <el-col :span="10">
                <!-- 目录大纲编辑界面 -->
                <OutlineEditor 
                    ref="OutlineEditorRef"
                    @setCurrentScreenInfo="setCurrentScreenInfo" />
            </el-col>
            <el-col :span="14">
                <div class="chapter-edit">
                    <el-card class="box-card" shadow="never">
                        <template #header>
                            <div class="card-header">
                                <div style="display: flex;align-items: center;gap: 10px;;">
                                    章节编辑
                                    <el-tooltip v-for="(item,index) in currentScreenInfo.contentEditors" :key="index"
                                        class="box-item" 
                                        effect="dark" 
                                        :content="item.roleName" 
                                        placement="top">
                                        <span class="edit-header-avatar">
                                            <img :src="imagePathByPath(item.roleAvatar)" @click="openChapterSelectionDialog(item)" />
                                        </span>
                                    </el-tooltip>

                                    <el-tooltip class="box-item" effect="dark" content="添加内容编写人员" placement="top">
                                        <span class="edit-header-avatar" @click="configAgent()">
                                            <i class="fa-solid fa-user-plus"></i>
                                        </span>
                                    </el-tooltip>
                                </div>
                                <div>
                                    <el-tooltip class="box-item" effect="dark" content="执行文档生成" placement="top">
                                        <el-button type="primary" text bg size="large" @click="genChapterContent()">
                                            <i class="fa-solid fa-ferry"></i>
                                        </el-button>
                                    </el-tooltip>
                                    <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top">
                                        <el-button type="danger" text bg size="large" @click="handleUploadFile">
                                            <i class="fa-solid fa-file-word icon-btn"></i>
                                        </el-button>
                                    </el-tooltip>
                                    <el-button type="primary" @click="onSubmit">保存</el-button>
                                    <el-button type="danger" @click="onSubmit">下载</el-button>
                                </div>
                            </div>
                        </template>
                        <el-form :model="form" label-width="100px" label-position="top">
                            <el-form-item :label="currentChaterTitle">
                                <el-input v-model="form.title" placeholder="请输出针对于本章节内容的一些自定义要求"></el-input>
                            </el-form-item>
                            <el-form-item label="章节内容">
                                <el-input type="textarea" v-model="form.content" :rows="31" resize="none" placeholder="请输出针对于本章节内容的一些自定义要求"></el-input>
                            </el-form-item>
                        </el-form>
                    </el-card>
                </div>
            </el-col>
        </el-row>

        <!-- 选择章节对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="chapterEditTitle"
            append-to-body
            width="700px"
            >
            <div style="padding: 10px;margin-bottom: 10px;">
              角色能力: {{  currentUser.responsibilities }}
            </div>
            <el-scrollbar style="height:calc(100vh - 300px)">
                <div class="chapter-tree-content">
                    <el-tree :data="outline" 
                        node-key="id" 
                        default-expand-all 
                        show-checkbox
                        :allow-drop="allowDrop"
                        :allow-drag="allowDrag" 
                        @node-click="handleNodeClick" 
                        @node-contextmenu="handleNodeContextMenu"
                        check-strictly
                        ref="chapterSelectionTree">
                        <template #default="{ node, data }">
                            <div class="custom-tree-node" style="height:auto;">
                                <div style="display: flex;flex-direction: column;">
                                    <div style="font-size: 16px;font-weight: bold;color:#333">
                                        {{ node.label }}
                                    </div>
                                    <div class="description">
                                        <span style="color: #777;">{{ data.description }}</span>
                                    </div>
                                </div>
                                <span style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">
                                    <el-avatar v-if="data.chapterEditor" :size="20" :src="imagePathByPath(data.chapterEditorAvatar)"></el-avatar>
                                </span>
                            </div>
                        </template>
                    </el-tree>
                </div>
            </el-scrollbar>
            
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="assignChaptersToUser">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 材料上传界面 -->
        <ScreenUploadFile ref="uploadChildComp" />

    </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';
import ChapterEditor from './chapterEditor'

import ScreenUploadFile from './screenUploadFile.vue'

import { getParam } from '@/utils/ruoyi'
import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";
import { 
    updateChapterContentEditor , 
    getChapterByRole 
} from '@/api/base/im/screen'

import { ElMessage } from 'element-plus';

import OutlineEditor from './outlineEditor.vue'

const route = useRoute();
const { proxy } = getCurrentInstance();

const currentScreenInfo = ref([])

const chapterEditTitle = ref("")
const dialogVisible = ref(false)
const chapterTree = ref(null);
const chapterSelectionTree = ref(null);
const currentUser = ref(null);

// 定义人物信息
const person = ref({
  roleAvatar: '' , 
  roleName: '张三',
  responsibilities: '主要负责与客户进行沟通，解答客户关于产品的各种疑问，提供专业的咨询服务，以帮助客户更好地理解和使用产',
  email: 'zhangsan@example.com',
});


const loading = ref(true)

const uploadChildComp = ref(null)
const innerRef = ref(null); // 滚动条的处理_starter
const OutlineEditorRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);
const messageList = ref([]);

const currentChaterTitle = ref("章节标题")

const mdi = new MarkdownIt({
    html: true,
    linkify: true,
    highlight(code, language) {
        const validLang = !!(language && hljs.getLanguage(language));
        if (validLang) {
            const lang = language || '';
            return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
        }
        return highlightBlock(hljs.highlightAuto(code).value, '');
    },
});

function highlightBlock(str, lang) {
    return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });

const streamLoading = ref(null)
const screenId = ref('')
const chaterText = ref("")

const form = reactive({
    title: '',
    content: ''
});

// 文章目录结构数据
const outline = ref([]);

const onSubmit = () => {
    // 这里处理表单提交逻辑
    ElMessage.success('保存成功！');
    console.log('提交的数据:', form);
};

/** 上传文档文件 */
function handleUploadFile() {
    uploadChildComp.value.handleOpenUpload(true);
}

// 推送消息到当前面板
const pushResponseMessageList = (newMessage) => {
    console.log(`--->>> newMessage = ${JSON.stringify(newMessage)}`);

    if (newMessage.llmStream === true) { // 是否为流式输出

        // 查找是否有相同businessId的消息
        const existingIndex = messageList.value.findIndex(item => item.businessId === newMessage.businessId);

        if (existingIndex !== -1) {
            // 如果找到，更新该消息
            messageList.value[existingIndex].chatText += newMessage.chatText;
        } else {
            // 否则，添加新消息
            messageList.value.push(newMessage);
        }
    } else {
        messageList.value.push(newMessage);
    }

    // 调用初始化滚动条的函数
    initChatBoxScroll();
};

function configAgent(){
    OutlineEditorRef.value.configAgent('content');
}

/** 显示工具条 */
function showTools(item) {
    messageList.value.forEach((i) => {
        i.showTools = i === item; // 只有当前项的 showTools 被设置为 true
    });
}

function setCurrentScreenInfo(data){
    currentScreenInfo.value = data
    outline.value = data.chapterTree
}

/** 隐藏工具条 */
function hideTools(item) {
    item.showTools = false; // 鼠标移出时隐藏 tools
}

function initChatBoxScroll() {

    nextTick(() => {
        const element = innerRef.value;  // 获取滚动元素
        const scrollHeight = element.scrollHeight;

        scrollbarRef.value.setScrollTop(scrollHeight);
    })

}

// 打开选择章节对话框
const openChapterSelectionDialog = (role) => {
    chapterEditTitle.value = "请选择["+role.roleName+"]需要编辑的章节"

    person.value = role ;
    person.value.email =  'zhangsan@example.com';
    
    currentUser.value = role;
    currentUser.value.selectedChapters = [];
    dialogVisible.value = true;

    // 初始化已选择节点
    nextTick(() => {
        getChapterByRole(role.id, screenId.value).then(res => {
            if(res.data){
                let selectKey = res.data.map(item => parseInt(item.id));
                chapterSelectionTree.value.setCheckedKeys(selectKey) ;
            }
        })

    });

};

// 循环生成任务使用
const genChapterContent = () => {
  const node = findNodeById(outline.value, -1);
  return node ? node.label : '';
};

// 递归查找节点
const findNodeById = (nodes, id) => {
  for (let node of nodes) {

    console.log('nodeName = ' + node.label + ', id = ' + node.id)

    if (node.id === id) {
        return node;
    }
    if (node.children && node.children.length) {
      const foundNode = findNodeById(node.children, id);
      if (foundNode) {
        return foundNode;
      }
    }
  }
  return null;
};

const onCancel = () => {
    // 这里处理取消操作
    ElMessage.info('已取消');
    form.title = '';
    form.content = '';
}

// 分配章节给用户
const assignChaptersToUser = () => {
  const checkedNodes = chapterSelectionTree.value.getCheckedNodes(false, true);

  console.log('newChapterIds= ' + checkedNodes)

  const newChapterIds = checkedNodes.map(node => node.id);

  console.log('newChapterIds = ' + newChapterIds);

  currentUser.value.selectedChapters = [...new Set([...currentUser.value.selectedChapters, ...newChapterIds])];

  console.log('currentUser.value.selectedChapters=' + currentUser.value.selectedChapters);

  let data = {
      screenId: screenId.value,
      roleId: currentUser.value.id,
      chapters: currentUser.value.selectedChapters
  }

  updateChapterContentEditor(data).then(res => {
    console.log(res)
    proxy.$modal.msgSuccess("分配成功");
    dialogVisible.value = false;

    OutlineEditorRef.value.handleGetScreen();
  })

};

/** 读取html文本 */
function readerHtml(chatText) {
    return mdi.render(chatText);
}

// 进入初始化
onMounted(() => {
    screenId.value = route.query.screenId;
    loading.value = false

    OutlineEditorRef.value.closeStreamDialog();

    // handleSseConnect(screenId.value)
})

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
                        // chaterText.value += data.chatText;
                        pushResponseMessageList(data);
                    }
                } else {
                    console.log('消息接收结束.')
                    if (streamLoading.value) {
                        streamLoading.value.close();
                    }
                    OutlineEditorRef.value.closeStreamDialog();
                }
            }
        }
    })
}

// 销毁信息
onBeforeUnmount(() => {
    // handleCloseSse(screenId.value).then(res => {
    //     console.log('关闭sse连接成功:' + screenId)
    // })
});

</script>

<style lang="scss" scoped>
.chapter-edit {
    margin: 0px;

    .box-card {
        border: 0px;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .el-form {
        .el-form-item {
            &.el-form-item--small {
                margin-bottom: 15px;
            }

            .el-input,
            .el-textarea {
                width: 100%;
            }
        }
    }

    .el-button+.el-button {
        margin-left: 10px;
    }

    .scroll-panel {
        height: calc(100vh - 310px);
        width: 100%;
        border: 1px solid #dcdfe6 ;
        border-radius: 4px;
    }

    .chater-text-body {
        background: #fafafa;
        padding: 10px;
        height: 100%;
        width: 100%;
        border-radius: 4px;
    }
}

.inner-smart-container {
    max-width: 100% ! important;

    .robot-chat-windows {
        border: 0px !important;
    }

    .inner-robot-chat-body {
        height: calc(100vh - 100px);
    }
}

.scroll-panel {
    padding-bottom: 10px;
    float: left;
    width: 100%;
    height: calc(100% - 55px);
    overflow: hidden;
}

.show-tools {
    visibility: visible;
}

.hide-tools {
    visibility: hidden;
}

.robot-chat-ai-say-box {
    float: left;
    width: 100%;

    .say-right-window {
        float: right !important;

        .say-message-info {
            text-align: right !important;
        }
    }

    .chat-ai-header {
        float: left;
        width: 50px;
        margin: 10px;

        .header-images {
            padding: 5px;

            img {
                width: 100%;
                border-radius: 50%;
            }

        }

    }

    .chat-ai-say-body {
        float: left;
        margin-top: 15px;
        font-size: 14px;

        .say-message-info {
            font-size: 13px;
            margin-bottom: 5px;
            color: #999;
        }

        .say-message-body {
            padding: 10px;
            line-height: 1.4rem;
            border-radius: 3px;
            background: #fafafa;
        }

    }

    .message-list {
        margin-top: 20px;
    }

    .message {
        margin-bottom: 8px;
    }

}

.dialog-footer {
    text-align: right;
    margin-top: 19px;
}

</style>
