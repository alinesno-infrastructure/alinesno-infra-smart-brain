<template>
    <div class="app-container" style="background-color: #fff;padding-top:10px;padding-left:0px !important">
        <el-row>
            <el-col :span="10">
                <!-- 目录大纲编辑界面 -->
                <DataAnalysisOutlineEditor
                    ref="OutlineEditorRef"
                    @openChatBox="openChatBox"
                    @handleExecuteHandle="handleExecuteHandle"
                    @handleGetChannelStreamId="handleGetChannelStreamId"
                    @closeShowDebugRunDialog="closeShowDebugRunDialog"
                    @genChapterContentByAgent="genChapterContentByAgent"
                    @setCurrentSceneInfo="setCurrentSceneInfo"
                    @editContent="editContent"
                     />
            </el-col>

            <el-col :span="14">
                <div class="chapter-edit">
                    <el-card class="box-card" shadow="never">
                        <template #header>
                            <div class="card-header">
                                <div style="display: flex;align-items: center;gap: 5px;">
                                    章节编辑
                                    <el-tooltip v-for="(item,index) in currentSceneInfo.dataMinerEngineers" :key="index"
                                        class="box-item" 
                                        effect="dark" 
                                        :content="item.roleName" 
                                        placement="top">
                                        <span class="edit-header-avatar">
                                            <img :src="imagePathByPath(item.roleAvatar)" @click="openChapterSelectionDialog(item)" />
                                        </span>
                                    </el-tooltip>
                                </div>

                                <!-- 执行按钮_start -->
                                <div>
                                    <el-tooltip class="box-item" effect="dark" content="执行文档生成" placement="top">
                                        <el-button type="primary" text bg size="large" @click="genChapterContent()">
                                            <i class="fa-solid fa-paper-plane"></i>
                                        </el-button>
                                    </el-tooltip>

                                    <el-tooltip class="box-item" effect="dark" content="重新生成章节" placement="top">
                                        <el-button type="primary" text bg size="large" @click="genSingleChapterContent()">
                                            <i class="fa-brands fa-firefox-browser"></i>
                                        </el-button>
                                    </el-tooltip>

                                    <el-tooltip class="box-item" effect="dark" content="导出文档" placement="top">
                                        <el-button type="primary" text bg size="large" @click="expertScene">
                                            <i class="fa-solid fa-download"></i>
                                        </el-button>
                                    </el-tooltip>

                                </div>
                                <!-- 执行按钮_end -->
                            </div>
                        </template>

                        <div>
                            <DataAnalysisDisplay />
                        </div>

                    </el-card>
                </div>
            </el-col>
        </el-row>

         <!-- 运行抽屉 -->
        <div class="aip-flow-drawer">
            <el-drawer v-model="showDebugRunDialog" 
                :modal="false" 
                size="40%" 
                style="max-width: 700px;" 
                title="预览与调试"
                :with-header="true">
                <div style="margin-top: 0px;">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </div>
            </el-drawer>
        </div>

    </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'

import DataAnalysisOutlineEditor from './dataAnalysisOutlineEditor.vue'
import DataAnalysisDisplay from './dataAnalysisDisplay.vue'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const OutlineEditorRef = ref(null); 

const currentSceneInfo = ref({})
const outline = ref([])

// 打开对话窗口
const openChatBox = (roleId , message) => {

    showDebugRunDialog.value = true ;
    console.log('roleId = ' + roleId + ' , message = ' + message);

    nextTick(() => {
        roleChatPanelRef.value.openChatBox(roleId , message);
        OutlineEditorRef.value.genStreamContentByMessage(roleId , message);
    })

}

// 关闭对话窗口
const closeShowDebugRunDialog = () => {
    showDebugRunDialog.value = false ;
}

function setCurrentSceneInfo(data){
    currentSceneInfo.value = data
    outline.value = data.chapterTree
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
    margin-bottom:20px;
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