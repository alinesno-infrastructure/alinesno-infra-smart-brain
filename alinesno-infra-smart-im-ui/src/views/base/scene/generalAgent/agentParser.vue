<template>

    <GeneralAgentContainer>

                <div style="padding:0px;margin:0px;padding-left:0px !important">
                    <el-row>
                        <el-col :span="9">
                            <!-- 目录大纲编辑界面 -->
                            <GeneratorAgentOutlineEditor ref="OutlineEditorRef"
                                @openChatBox="openChatBox"
                                @handleExecuteHandle="handleExecuteHandle"
                                @handleGetChannelStreamId="handleGetChannelStreamId"
                                @closeShowDebugRunDialog="closeShowDebugRunDialog"
                                @genChapterContentByAgent="genChapterContentByAgent"
                                @setCurrentSceneInfo="setCurrentSceneInfo"
                                @editContent="editContent" />
                        </el-col>

                        <el-col :span="15">
                            <div class="chapter-edit">
                                <el-card class="box-card" shadow="never">
                                    <template #header>
                                        <div class="card-header">
                                            <div style="display: flex;align-items: center;gap: 5px;">
                                                任务执行节点
                                                <el-tooltip
                                                    v-for="(item, index) in currentSceneInfo.businessExecuteEngineers"
                                                    :key="index" class="box-item" effect="dark" :content="item.roleName"
                                                    placement="top">
                                                    <span class="edit-header-avatar">
                                                        <img :src="imagePathByPath(item.roleAvatar)"
                                                            @click="openChapterSelectionDialog(item)" />
                                                    </span>
                                                </el-tooltip>
                                                <el-tooltip v-for="(item, index) in currentSceneInfo.dataViewerEngineers"
                                                    :key="index" class="box-item" effect="dark" :content="item.roleName"
                                                    placement="top">
                                                    <span class="edit-header-avatar">
                                                        <img :src="imagePathByPath(item.roleAvatar)" />
                                                    </span>
                                                </el-tooltip>
                                            </div>

                                            <!-- 执行按钮_start -->
                                            <div>
                                                <el-button type="primary" text bg 
                                                    @click="genChapterContent()">
                                                    <i class="fa-solid fa-paper-plane"></i> &nbsp; 重新生成
                                                </el-button>

                                                <el-button type="primary" text bg 
                                                    @click="genSingleChapterContent()">
                                                    <i class="fa-brands fa-firefox-browser"></i> &nbsp; 生成章节
                                                </el-button>

                                                <el-button type="primary" text bg @click="expertScene">
                                                    <i class="fa-solid fa-download"></i> &nbsp; 报告生成
                                                </el-button>

                                            </div>
                                            <!-- 执行按钮_end -->
                                        </div>
                                    </template> 

                                    <el-form :model="form" label-width="100px" label-position="top" v-loading="loading">
                                        <div class="chapter-title">
                                            {{ form.title?form.title:'未生成章节.' }}
                                        </div> 
                                        <el-form-item label="章节内容" class="chapter-content">
                                            <DataAnalysisDisplay ref="dataAnalysisDisplayRef" />
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

            </GeneralAgentContainer>
</template>

<script setup>

import { ElLoading, ElMessage } from 'element-plus'

import {
    updateChapterContentEditor,
    getChapterByRole,
    chatRoleSync,
    genDataReport,
    getScene,
    getGeneralAgentScene,
    getPreviewDocx,
    dispatchAgent,
    getPreviewUrl,
} from '@/api/base/im/scene/generalAgent'

import GeneralAgentContainer from './generalAgentContainer'
import FunctionList from './functionList'
import GeneratorAgentOutlineEditor from './agentOutlineEditor.vue'
import DataAnalysisDisplay from './agentContentDisplay.vue'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';

const route = useRoute();
const { proxy } = getCurrentInstance();

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const OutlineEditorRef = ref(null);
const dataAnalysisDisplayRef = ref(null);

const channelStreamId = ref(route.query.channelStreamId);
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const streamLoading = ref(null)

const chapterEditTitle = ref("")
const dialogVisible = ref(false)
const chapterSelectionTree = ref(null);
const currentUser = ref(null);
const editorRoleId = ref(null);

const totalNodes = ref(0);

const showPdfDialog = ref(false);
const pdfTitle = ref('')
const iframeUrl = ref('')
const loadingDocument = ref(false);
const currentStoreId = ref(null)

const currentSceneInfo = ref({})
const outline = ref([])
const loading = ref(false);

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

// 打开对话窗口
const openChatBox = (roleId, message) => {

    // 如果状态本来是打开的，则不再地继续执行
    if (showDebugRunDialog.value) {
        return;
    }

    showDebugRunDialog.value = true;
    console.log('roleId = ' + roleId + ' , message = ' + message);

    nextTick(() => {
        roleChatPanelRef.value.openChatBoxWithRole(roleId) ;  
    })

}
  
// 重新生成指定的章节内容
const genSingleChapterContent = async () => {

    if (!form.id) {
        ElMessage.error('请先选择指定章节！');
        return;
    }

    // 开始生成
    streamLoading.value = ElLoading.service({
        lock: true,
        background: 'rgba(255, 255, 255, 0.2)',
        customClass: 'custom-loading'
    });

    showDebugRunDialog.value = true;

    let text = '正在重新生成【' + form.title + '】内容，请稍等.';
    streamLoading.value.setText(text)

    let formData = {
        sceneId: sceneId.value,
        channelStreamId: channelStreamId.value,
        chapterTitle: form.title,
        taskId: taskId.value ,
        chapterDescription: form.description,
        chapterId: form.id,
    }

    nextTick(() => {
        roleChatPanelRef.value.openChatBox(editorRoleId.value, formData.chapterTitle);
    })

    const result = await chatRoleSync(formData);
    // chapterEditorRef.value.setData(result.data) ;
    dataAnalysisDisplayRef.value.setPlanItemContentData(result.data);

    streamLoading.value.close();
    showDebugRunDialog.value = false;

};


// 定义一个异步函数来调用 chatRoleSync
const genChapterContent = async () => {

    OutlineEditorRef.value.genChapterContent(); // 重新生成 

    // try {
    //     totalNodes.value = countNodes(outline.value);
    //     console.log('文档总数量 = ' + totalNodes.value);

    //     // 将树转换成列表形式
    //     const nodeList = [];
    //     const flattenTree = (nodes) => {
    //         for (let node of nodes) {
    //             nodeList.push(node);
    //             if (node.children && node.children.length) {
    //                 flattenTree(node.children);
    //             }
    //         }
    //     };
    //     flattenTree(outline.value);

    //     // 开始生成
    //     streamLoading.value = ElLoading.service({
    //         lock: true,
    //         background: 'rgba(255, 255, 255, 0.5)',
    //         customClass: 'custom-loading'
    //     });

    //     showDebugRunDialog.value = true;

    //     // 遍历输出每个节点的信息
    //     for (let i = 0; i < nodeList.length; i++) {
    //         const node = nodeList[i];
    //         console.log('节点 = ' + JSON.stringify(node));
    //         console.log(`节点 ${i + 1}: ID = ${node.id}, Label = ${node.label}`);
    //         let processMsg = ` ${i + 1}: 章节:${node.label}`;

    //         let text = processMsg + ' 任务生成中，还有【' + (totalNodes.value - i) + '】篇';
    //         streamLoading.value.setText(text)

    //         form.title = node.label;
    //         dataAnalysisDisplayRef.value.setPlanItem(node);

    //         let formData = {
    //             sceneId: sceneId.value,
    //             channelStreamId: channelStreamId.value,
    //             chapterTitle: node.label,
    //             taskId: taskId.value ,
    //             chapterDescription: node.description,
    //             chapterId: node.id,
    //         }

    //         nextTick(() => {
    //             console.log('--->> editorRoleId = ' + node.chapterEditor)
    //             roleChatPanelRef.value.openChatBox(node.chapterEditor, node.label);
    //         })

    //         const result = await chatRoleSync(formData);
    //         dataAnalysisDisplayRef.value.setPlanItemContentData(result.data);

    //     }
    // } catch (error) {
    //     console.error('Error fetching data:', error);
    //     proxy.$modal.msgError("生成失败");
    //     streamLoading.value.close();
    // }

    // streamLoading.value.close();
    // showDebugRunDialog.value = false;
    // // 编写完成之后，直接可以预览
    // expertScene();

};

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/** 导出word文档 */
function expertScene() {

    // 生成数据报告
    // genDataReport(sceneId.value).then(res => {
    //     console.log('res = ' + res);
    // })

    pdfTitle.value = '显示结果报告列表'
    showPdfDialog.value = true;
    loadingDocument.value = true;

    genDataReport(sceneId.value , taskId.value).then(res => {
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
    // let chapterId = data.id ;

    editorRoleId.value = node.data.chapterEditor;
    console.log('editorRoleId = ' + editorRoleId.value)

    form.id = data.id;
    form.title = node.label;
    form.chapterEditor = node.chapterEditor;
    form.description = node.data.description;

    nextTick(() => {
        dataAnalysisDisplayRef.value.setData(form);
    })
    

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
        chapters: currentUser.value.selectedChapters
    }

    updateChapterContentEditor(data).then(res => {
        console.log(res)
        proxy.$modal.msgSuccess("分配成功");
        dialogVisible.value = false;

        OutlineEditorRef.value.handleGetScene();
    })

};

const downloadWordDocument = () => {
    getPreviewUrl(currentStoreId.value).then(res => {
        window.open(res.data);
    })
}

// 关闭对话窗口
const closeShowDebugRunDialog = () => {
    showDebugRunDialog.value = false;
}

function setCurrentSceneInfo(data) {
    currentSceneInfo.value = data
    outline.value = data.chapterTree
}

</script>

<style lang="scss" scoped>
// 定义一些常用的变量
$spacing: 10px;
$avatar-size: 30px;

.ppt-pager-aside {
    padding: 0px;
    border-right: 1px solid #f2f3f7;
    background: #fff;
    margin-bottom: 0px;
}

.ppt-pager-main{
padding: 0px !important;
}

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
            width: 50px;
            height: 50px;

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

.chapter-tree-content .el-tree-node__content {
    height: auto !important;
}
</style>
