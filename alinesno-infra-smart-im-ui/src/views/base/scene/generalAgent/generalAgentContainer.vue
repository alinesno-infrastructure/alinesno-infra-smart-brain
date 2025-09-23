<template>

    <div class="ppt-pager-container">

        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="80px" class="ppt-pager-aside">
                <FunctionList />
            </el-aside>

            <el-main class="ppt-pager-main">
                <slot>  
                    <!-- 插槽 -->
                </slot>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>

import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
const router = useRouter()
const route = useRoute()
const { proxy } = getCurrentInstance();
  
import FunctionList from './functionList'

import axios from "axios";
import { 
    getScene,
    getSceneResultList,
    getPreviewDocx
} from '@/api/base/im/scene/documentReview';

import {
  getReviewTask,
  pagerListByPage
} from '@/api/base/im/scene/documentReviewTask';

import { nextTick } from 'vue'

// 生成文档预览
const iframeUrl = ref(null);
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const currentSceneInfo = ref({});
const currentTaskInfo = ref({
    taskName: '审核文档'
});
const loadingDocument = ref(true)
const contentRef = ref(null)
const currentActive = ref(1)
const checkResultList = ref([])
const reviewCheckListRef = ref(null)

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const handleStepClick = (index, auditId) => {

    if(index == 3){
        if(checkResultList.value.length == 0){
            ElMessage.warning('当前文档未审核，请先生成审核清单！');
            return ;
        }
    }
    console.log('index = ' + index);
    currentActive.value = index
    showDebugRunDialog.value = false;
}
 
</script>

<style lang="scss" scoped> 

.ppt-pager-container {
    background: #fff;
    height: calc(100vh - 60px);

    .review-footer {
        padding: 10px;
        font-size: 14px;
        background: #fafafa;
        border-radius: 8px;
        text-align: left;
        color: #555;
        margin-top: 10px;
    }

    .main-content {
        display: flex;
        flex-direction: column;
        padding-top: calc(1vh);
        // text-align: center;
        // max-width: 90%;
        margin: auto;
        padding-left: 20px;
        padding-right: 20px;

        .example-result-section {
            padding: 12px;
            border-radius: 10px;
            font-size: 14px;
            text-align: left;
            color: #585a73;
            display: flex;
            flex-direction: row;
            width: 100%;
            align-items: center;
            gap: 10px;
        }

        .title-section {
            display: flex;
            flex-direction: column;
            text-align: center;
            align-items: flex-start;

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
            // box-shadow: rgba(54, 54, 73, 0.06) 0px 12px 24px -16px, rgba(74, 80, 96, 0.12) 0px 12px 40px, rgba(44, 44, 54, 0.02) 0px 0px 1px;
            transition: 0.3s;
            background: rgb(255, 255, 255);
            padding: 10px !important;
            border: 1px solid rgb(232, 234, 242);
            margin-top: 30px;
            margin-bottom: 10px;
            align-items: flex-start;
            flex-direction: column;

            .input-box {
                width: 100%;
                height: 50px;
                border: 0px !important;
                margin-bottom: 0px;
            }
        }

    }

    .review-footer-message {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-top: 10px;
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

}

.ppt-pager-aside {
    padding: 0px;
    border-right: 1px solid #f2f3f7;
    background: #fff;
    margin-bottom: 0px;
}

.ppt-pager-main {
    padding: 0px !important;
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
    position: absolute;
}

.aip-flow-drawer .el-drawer__header {
    margin-bottom: 0px;
}
</style>