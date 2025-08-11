<template>

    <div class="ppt-pager-container">

        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="80px" class="ppt-pager-aside">
                <FunctionList />
            </el-aside>

            <el-main class="ppt-pager-main">
                <div>
                    <el-row>
                        <el-col :span="10">
                            <!-- 目录大纲编辑界面 -->
                            <OutlineEditor ref="OutlineEditorRef" 
                                @openChatBox="openChatBox"
                                @handleExecuteHandle="handleExecuteHandle"
                                @handleGetChannelStreamId="handleGetChannelStreamId"
                                @closeShowDebugRunDialog="closeShowDebugRunDialog"
                                @genChapterContentByAgent="genChapterContentByAgent"
                                @setCurrentSceneInfo="setCurrentSceneInfo" 
                                @editContent="editContent" />
                        </el-col>
                        <el-col :span="14">

                            <div class="chapter-edit">
                                <el-card class="box-card" shadow="never">
                                    <template #header>
                                        <div class="card-header">
                                            <div style="display: flex;align-items: center;gap: 5px;">
                                                章节编辑
                                                <el-tooltip v-for="(item, index) in currentSceneInfo.contentEditors"
                                                    :key="index" class="box-item" effect="dark" :content="item.roleName"
                                                    placement="top">
                                                    <span class="edit-header-avatar">
                                                        <img :src="imagePathByPath(item.roleAvatar)"
                                                            @click="openChapterSelectionDialog(item)" />
                                                    </span>
                                                </el-tooltip>
                                            </div>

                                            <!-- 执行按钮_start -->
                                            <div>
                                                <el-tooltip class="box-item" effect="dark" content="执行文档生成(已生成的会跳过)"
                                                    placement="top">
                                                    <el-button type="primary" text bg 
                                                        @click="genChapterContent()">
                                                        <i class="fa-solid fa-ferry"></i>&nbsp; 重新生成
                                                    </el-button>
                                                </el-tooltip>

                                                <el-tooltip class="box-item" effect="dark" content="重新生成章节"
                                                    placement="top">
                                                    <el-button type="primary" text bg
                                                        @click="genSingleChapterContent()">
                                                        <i class="fa-brands fa-firefox-browser"></i>&nbsp; 章节生成
                                                    </el-button>
                                                </el-tooltip>
  
                                                <el-tooltip class="box-item" effect="dark" content="保存章节"
                                                    placement="top">
                                                    <el-button type="primary" text bg
                                                        @click="onSubmitChapter">
                                                        <i class="fa-solid fa-floppy-disk"></i>&nbsp; 保存
                                                    </el-button>
                                                </el-tooltip>

                                                <el-tooltip class="box-item" effect="dark" content="预览导出文档" placement="top"> 
                                                    <EditorDocument :taskId="taskId" :sceneId="sceneId" />
                                                </el-tooltip>
 
                                            </div>
                                            <!-- 执行按钮_end -->
                                        </div>
                                    </template>
                                    <el-form :model="form" label-width="100px" label-position="top" v-loading="loading">
                                        <div class="chapter-title">
                                            {{ form.title?form.title:'未生成章节.' }}
                                        </div> 
                                        <el-form-item label="章节内容" class="chapter-content">
                                            <ChapterEditor v-model:articleData="articleData" ref="chapterEditorRef" />
                                        </el-form-item>
                                    </el-form>
                                </el-card>
                            </div>

                        </el-col>
                    </el-row>

                    <!-- 选择章节对话框 -->
                    <el-dialog v-model="dialogVisible" :title="chapterEditTitle" append-to-body width="700px">
                        <div class="user-info">
                            <img v-if="currentUser.roleAvatar" class="avatar"
                                :src="imagePathByPath(currentUser.roleAvatar)" />
                            角色能力: {{ currentUser.responsibilities }}
                        </div>
                        <el-scrollbar style="height:calc(100vh - 400px)">
                            <div class="chapter-tree-content">
                                <el-tree :data="outline" node-key="id" default-expand-all show-checkbox
                                    :allow-drop="allowDrop" :allow-drag="allowDrag" @node-click="handleNodeClick"
                                    @node-contextmenu="handleNodeContextMenu" check-strictly ref="chapterSelectionTree">
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
                                            <span
                                                style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">
                                                <el-avatar v-if="data.chapterEditor" :size="20"
                                                    :src="imagePathByPath(data.chapterEditorAvatar)"></el-avatar>
                                            </span>
                                        </div>
                                    </template>
                                </el-tree>
                            </div>
                        </el-scrollbar>

                        <div class="dialog-footer">
                            <el-button size="large" @click="dialogVisible = false">取 消</el-button>
                            <el-button type="primary" size="large" @click="assignChaptersToUser">确 定</el-button>
                        </div>
                    </el-dialog>

                    <!-- 材料上传界面 -->
                    <SceneUploadFile ref="uploadChildComp" />

                    <!-- 角色选择面板 -->
                    <ExecuteHandle ref="executeHandleRef" @openChatBox="openChatBox" @handleGetScene="handleGetScene" />

                    <!-- 运行抽屉 -->
                    <div class="aip-flow-drawer">
                        <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;"
                            title="预览与调试" :with-header="true">
                            <div style="margin-top: 0px;">
                                <RoleChatPanel ref="roleChatPanelRef" :showDebugRunDialog="showDebugRunDialog" />
                            </div>
                        </el-drawer>
                    </div>

                    <!-- 显示pdf文件 -->
                    <el-dialog v-model="showPdfDialog" :title="pdfTitle" width="1300px"
                        :before-close="handleClosePdfDialog" :show-close="true">
                        <div class="document-wrapper" v-loading="loadingDocument">
                            <el-scrollbar class="scrollable-area"
                                style="height: calc(70vh);margin-top:20px; padding-right:0px">
                                <iframe :src="iframeUrl"
                                    style="position: absolute;width: 100%;border-radius: 5px; height: 100%;border: 0px;padding-bottom: 10px;background: #323639;">
                                </iframe>
                            </el-scrollbar>
                        </div>
                        <!-- 显示下载word文档按钮 -->
                        <div class="word-download-button">
                            <el-button type="primary" size="large" :loading="loadingDocument"
                                @click="downloadWordDocument">
                                <i class="el-icon-download"></i> 下载Word文档
                            </el-button>
                        </div>
                    </el-dialog>

                </div>

            </el-main>
        </el-container>

        <!-- AI生成状态 -->
        <AIGeneratingStatus 
            ref="generatingStatusRef" 
            :close-enable="false"
            :back-to-path="'/scene/longText/longTextManager'"
            :route-params="{ sceneId: sceneId }" 
        />

    </div>

</template>

<script setup>

import { ElLoading } from 'element-plus'

import AIGeneratingStatus from '@/components/GeneratingStatus/index.vue'
import ChapterEditor from './chapterEditor'
import SceneUploadFile from './sceneUploadFile.vue'
import OutlineEditor from './outlineEditor.vue'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import ExecuteHandle from './executeHandle'
import FunctionList from './functionList'
import EditorDocument from './components/EditorDocument'

import {
    updateChapterContentEditor,
    getChapterByRole,
    getChapterContent,
    updateChapterContent,
    chatRoleSync,
    getScene,
    getPreviewDocx,
    dispatchAgent,
    uploadOss,
    getPreviewUrl,
    getChapterById
} from '@/api/base/im/scene/longText'

import { ElMessage } from 'element-plus';
import { nextTick } from 'vue'


const route = useRoute();
const { proxy } = getCurrentInstance();

const currentSceneInfo = ref({})

const channelStreamId = ref(route.query.channelStreamId);
const loading = ref(false)
const chapterEditTitle = ref("")
const dialogVisible = ref(false)
const chapterSelectionTree = ref(null);
const currentUser = ref(null);
const editorRoleId = ref(null);

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)
const generatingStatusRef = ref(null)
const executeHandleRef = ref(null)

const chapterEditorRef = ref(null)
const uploadChildComp = ref(null)
const OutlineEditorRef = ref(null); // 滚动条的处理_starter

const streamLoading = ref(null)
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)

const totalNodes = ref(0);

const showPdfDialog = ref(false);
const pdfTitle = ref('')
const iframeUrl = ref('')
const loadingDocument = ref(false);
const currentStoreId = ref(null)

const articleData = ref({
    content: "" , 
})

const handleClosePdfDialog = () => {

    // 如果下载中，则允许关闭
    if (loadingDocument.value) {
        ElMessage.warning('正在下载中，请稍后...');
        return;
    }

    showPdfDialog.value = false;
    pdfTitle.value = ''
    iframeUrl.value = ''
}

// 定义人物信息
const person = ref({
    roleAvatar: '',
    roleName: '张三',
    responsibilities: '主要负责与客户进行沟通，解答客户关于产品的各种疑问，提供专业的咨询服务，以帮助客户更好地理解和使用产',
    email: 'zhangsan@example.com',
});

const form = reactive({
    id: 0,
    title: '',
    description: '',
    content: ''
});

// 文章目录结构数据
const outline = ref([]);

const onSubmitChapter = () => {
    // 这里处理表单提交逻辑
    console.log('提交的数据:', form);

    let data = {
        id: form.id,
        title: form.title,
        // content:  chapterEditorRef.value.getData()
        content: articleData.value.content 
    }

    updateChapterContent(data).then(res => {
        ElMessage.success('保存' + form.title + '成功！');
    })

};

/** 导出word文档 */
function expertScene() {

    pdfTitle.value = '显示文档列表'
    showPdfDialog.value = true;
    loadingDocument.value = true;

    uploadOss(sceneId.value , taskId.value).then(res => { 
        const storegeId = res.data;
        currentStoreId.value = storegeId;

        nextTick(async () => {
            const response = await getPreviewDocx(storegeId);
            console.log('response.data:' + response); // 打印数据内容
            iframeUrl.value = window.URL.createObjectURL(response);
            loadingDocument.value = false;
        })
    })
}
 

function setCurrentSceneInfo(data) {
    currentSceneInfo.value = data
    outline.value = data.chapterTree
}

// 打开选择章节对话框
const openChapterSelectionDialog = (role) => {
    chapterEditTitle.value = "请选择[" + role.roleName + "]需要编辑的章节"

    person.value = role;
    person.value.email = 'zhangsan@example.com';

    currentUser.value = role;
    currentUser.value.selectedChapters = [];
    dialogVisible.value = true;

    // 初始化已选择节点
    nextTick(() => {
        getChapterByRole(role.id, sceneId.value).then(res => {
            if (res.data) {
                let selectKey = res.data.map(item => item.id);
                console.log('selectKey = ' + selectKey);
                chapterSelectionTree.value.setCheckedKeys(selectKey);
            }
        })

    });

};

// 重新生成指定的章节内容
const genSingleChapterContent = async () => {

    if (!form.id) {
        ElMessage.error('请先选择指定章节！');
        return;
    } 
 
    showDebugRunDialog.value = true; 
    generatingStatusRef.value.loading()

    let text = '正在重新生成【' + form.title + '】内容，请稍等.';
    generatingStatusRef.value.setText(text)

    let formData = {
        sceneId: sceneId.value,
        taskId: taskId.value,
        channelStreamId: channelStreamId.value,
        chapterTitle: form.title,
        chapterDescription: form.description,
        chapterId: form.id,
    }

    nextTick(() => {
        roleChatPanelRef.value.openChatBox(editorRoleId.value, formData.chapterTitle);
    })

    try{
        const result = await chatRoleSync(formData); 
        articleData.value.content = result.data;

        generatingStatusRef.value.close();
        showDebugRunDialog.value = false;
    }catch(e){
        console.log('e = ' + e)
        generatingStatusRef.value.close();
        showDebugRunDialog.value = false;
    }

};

// 生成全部章节内容通过角色
const genChapterContentByAgent = async () => {
    console.log('开始生成全部章节内容通过角色 = genChapterContentByAgent');
    // 分配章节角色
    dispatchAgent(sceneId.value , taskId.value).then(res => {
        getScene(sceneId.value , taskId.value).then(res => {
            setCurrentSceneInfo(res.data)

            if(res.data.chapterTree){
                OutlineEditorRef.value.setOutline(res.data.chapterTree) ;
            }

            // genChapterContent();
            showDebugRunDialog.value = true;
        })

    })
};

const handleGetChannelStreamId = () => {
    const streamId = roleChatPanelRef.value.getChannelStreamId();
    console.log('getChannelStreamId = ' + streamId);
    return streamId;
}

// 定义一个异步函数来调用 chatRoleSync
const genChapterContent = async () => {
 try {
     totalNodes.value = countNodes(outline.value);
     console.log('文档总数量 = ' + totalNodes.value);

     // 将树转换成列表形式
     const nodeList = [];
     const flattenTree = (nodes) => {
         for (let node of nodes) {
             console.log('--->>> node = ' + JSON.stringify(node));
             nodeList.push(node);
             if (node.children && node.children.length) {
                 flattenTree(node.children);
             }
         }
     };
     flattenTree(outline.value);

     // 开始生成
     generatingStatusRef.value.loading() 
     showDebugRunDialog.value = true;

     // 遍历输出每个节点的信息
     for (let i = 0; i < nodeList.length; i++) {

         const node = nodeList[i];
         console.log('节点 = ' + JSON.stringify(node));
         console.log(`节点 ${i + 1}: ID = ${node.id}, Label = ${node.label}`);

         if(!node.chapterEditor){
            proxy.$modal.msgError(`[${node.label}]未分配编辑人员。`);
             generatingStatusRef.value.close();
             showDebugRunDialog.value = false;
            return ; 
        }

         // 先判断当前章节是否已经生成有内容
         const chapterId = node.id;
         const chapterContent = await getChapterById(chapterId);
         if(chapterContent.data.content){
             // 跳过
             continue;
         }

         let processMsg = ` ${i + 1}: 章节:${node.label}`;

         let text = processMsg + ' 任务生成中，还有【' + (totalNodes.value - i) + '】篇';
         generatingStatusRef.value.setText(text)

         form.title = node.label;

         let formData = {
             sceneId: sceneId.value,
             taskId: taskId.value,
             channelStreamId: channelStreamId.value,
             chapterTitle: node.label,
             chapterDescription: node.description,
             chapterId: node.id,
         }

         nextTick(() => { 
             roleChatPanelRef.value.openChatBox(node.chapterEditor, node.label);
         })

         const result = await chatRoleSync(formData); 
         articleData.value.content = result.data;

     }
 } catch (error) {
     console.error('--->>>> Error fetching data:', error); 
     generatingStatusRef.value.close();
 }

 generatingStatusRef.value.close();
 showDebugRunDialog.value = false;

 // 编写完成之后，直接可以预览
 //expertScene();
};

// 计算总节点数
const countNodes = (nodes) => {
    let count = 0;
    for (let node of nodes) {
        count++;
        if (node.children && node.children.length) {
            count += countNodes(node.children);
        }
    }
    return count;
};

// 递归查找节点
const findNodeById = (nodes, id) => {
    for (let node of nodes) {
        console.log('nodeName = ' + node.label + ', id = ' + node.id);
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

/** 编辑章节内容 */
const editContent = (node, data) => {
    console.log('node = ' + JSON.stringify(node.data)) 

    let chapterId = data.id; 
    editorRoleId.value = node.data.chapterEditor;

    if(!editorRoleId.value){
        proxy.$modal.msgError("章节未分配编辑人员.");
        return ; 
    }

    console.log('editorRoleId = ' + editorRoleId.value)

    form.id = data.id;
    form.title = node.label;
    form.chapterEditor = node.chapterEditor;
    form.description = node.data.description;

    getChapterContent(chapterId).then(res => { 
        articleData.value.content = res.data == null ? '' : res.data;
        loading.value = false;
    })

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
        sceneId: sceneId.value,
        roleId: currentUser.value.id,
        taskId: taskId.value,
        chapters: currentUser.value.selectedChapters
    }

    updateChapterContentEditor(data).then(res => {
        console.log(res)
        proxy.$modal.msgSuccess("分配成功");
        dialogVisible.value = false;

        OutlineEditorRef.value.handleGetScene();
    })

};

// 获取场景信息
const handleGetScene = () => {
    console.log('handleGetScene');
    OutlineEditorRef.value.handleGetScene();
};

// 打开角色选择窗口
const handleExecuteHandle = (currentSceneInfo) => {
    console.log('currentSceneInfo = ' + currentSceneInfo);
    executeHandleRef.value.handleOpen(currentSceneInfo);
};

/** 打开窗口 */
const openChatBox = (roleId, message) => {

    if(showDebugRunDialog.value){
        return ;
    }

    showDebugRunDialog.value = true;
    console.log('roleId = ' + roleId + ' , message = ' + message);

    nextTick(() => {
        roleChatPanelRef.value.openChatBoxWithRole(roleId) ;   
    })

}

const downloadWordDocument = () => {
    getPreviewUrl(currentStoreId.value).then(res => {
        window.open(res.data);
    })
}

const closeShowDebugRunDialog = () => {
    console.log("--->>>> closeShowDebugRunDialog");
    if(generatingStatusRef.value?.isLoading()){
        return ; 
    }
    showDebugRunDialog.value = false;
}

</script>

<style lang="scss" scoped>
// 定义一些常用的变量
$spacing: 10px;
$avatar-size: 30px;

.user-info {
    padding: $spacing;
    margin-bottom: $spacing;
    display: flex;
    gap: $spacing;

    .avatar {
        width: $avatar-size;
        height: $avatar-size;
        border-radius: 50%;
    }
}

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
        border: 1px solid #dcdfe6;
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

    // .inner-robot-chat-body {
    //     height: calc(100vh - 100px);
    // }
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

.chapter-content {
    margin-bottom: 0px !important
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
                border-radius: 50%;
                width: 100%;
                height: 100%;
                object-fit: cover;
                object-position: center;
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
    margin-bottom: 20px;
}

.word-download-button {
    margin-top: 20px;
    margin-bottom: 20px;
    text-align: right;
}

.ppt-pager-container {
  background: #fff;

  .ppt-pager-aside {
    padding: 0px;
    border-right: 1px solid #f2f3f7;
    background: #fff;
    margin-bottom: 0px;
  }

  .ppt-pager-main {
    padding: 0px !important;
  }

  .long-text-container {
    background: #fff;
    height: calc(100vh - 50px);

    .main-content {
      display: flex;
      flex-direction: column;
      padding-top: calc(1vh);
      text-align: center;
      max-width: 960px;
      margin: auto;

      .title-section {
        display: flex;
        flex-direction: column;
        text-align: left;

        .title {
          color: #2C2C36;
          font-weight: 600;
          font-size: 28px;
          margin-bottom: 10px;
          line-height: 40px;
        }

        .description {
          margin-top: 10px;
          color: #8F91A8;
          font-weight: 400;
          font-size: 16px;
          line-height: 24px;
        }
      }

      .input-button-section {
        display: flex;
        gap: 10px;
        position: relative;
        box-sizing: border-box;
        width: 100%;
        border-radius: 15px;
        transition: 0.3s;
        background: rgb(255, 255, 255);
        padding: 10px !important;
        border: 1px solid rgb(232, 234, 242);
        margin-top: 10px;
        margin-bottom: 10px;
        align-items: flex-start;
        flex-direction: column;

        .input-box {
          width: 100%;
          border: 0px !important;
          margin-bottom: 0px;
        }

        .upload-button-section {
          display: flex;
          width: 100%;
          justify-content: space-between;
          align-items: center;
        }
      }

      .example-section {
        padding: 0px 0px;

        .example-title {
          color: rgb(44, 44, 54);
          font-size: 14px;
          text-align: left;
          margin-left: 5px;
          margin-top: 15px;
          margin-bottom: 15px;
        }

        .example-list {
          display: flex;
          flex-wrap: wrap;

          .example-item {
            position: relative;
            display: flex;
            gap: 8px;
            align-items: center;
            height: 40px;
            background: rgb(242, 243, 247);
            border-radius: 8px;
            cursor: pointer;
            transition: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
            will-change: opacity, transform;
            width: calc(50% - 10px);
            box-sizing: border-box;
            padding: 10px;
            margin: 5px 5px 8px 5px;

            &:hover {
              background: rgb(232 233 235);

              .example-icon {
                display: block;
              }
            }

            .example-icon {
              display: none;
              color: #585a73;
              font-size: 12px;
            }

            .example-text {
              flex: 1 1;
              overflow: hidden;
              color: #585a73;
              font-size: 14px;
              white-space: nowrap;
              text-align: left;
              text-overflow: ellipsis;
              transition: padding-right .2s ease-out;
            }
          }
        }
      }
    }

    .review-footer-message {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-top: 50px;
      height: 36px;
      padding: 12px 0px;
      text-align: center;

      .footer-message {
        margin-bottom: 4px;
        color: #C8CAD9;
        font-size: 12px;
        line-height: 12px;
      }
    }
  }

  .review-footer {
    padding: 10px;
    font-size: 14px;
    background: #fafafa;
    border-radius: 8px;
    text-align: left;
    color: #555;
    margin-top: 10px;
  }

  .review-question-preview-title {
    padding: 10px;
    text-align: left;
    font-weight: bold;
    margin-left: 20px;
    margin-right: 10px;
    margin-bottom: 10px;
    border-radius: 10px;
    background: #fafafa;
    color: #444;
    font-size: 15px;
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: space-between;
  }

  .review-question-preview {
    height: calc(100vh - 170px);
    margin-left: 20px;
    border-radius: 8px;
    background: #fafafa;
    border: 1px solid #e8eaf2;
    overflow: hidden;
  }

  .pager-gen-result-panel {
    margin-bottom: 20px;
    margin-left: 20px;
    text-align: left;

    .pager-container {
      background-color: #fafafa;
      margin: 10px 10px;
      margin-right: 0px;
      border-radius: 8px;
      height: calc(100vh - 190px);
      padding: 10px;
      padding-left: 10px;
      margin-left: 20px;
      margin-bottom: 0px;
    }
  }
}

.chapter-title {
    font-size: 17px;
    font-weight: bold;
    line-height: 2.8;
    border-radius: 5px;
    padding-left: 20px;
    margin-bottom: 10px;
    background: #f5f5f5;
    margin-top: -10px;
}

</style>

<style>
.flow-control-panel .el-card__body {
    padding: 13px !important
}

.aip-flow-drawer .el-drawer.ltr,
.aip-flow-drawer .el-drawer.rtl {
    height: 93%;
    bottom: 10px;
    top: auto;
    right: 10px;
    border-radius: 8px;
}

.aip-flow-drawer .el-drawer__header {
    margin-bottom: 0px;
}
</style>