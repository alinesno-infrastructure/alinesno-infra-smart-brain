<template>

    <div class="ppt-pager-container">

        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="280px" class="ppt-pager-aside">
                <FunctionList />
            </el-aside>

            <el-main class="ppt-pager-main">
                <div class="docuemnt-parser-container" style="padding: 10px">
                    <el-row>
                        <el-col :span="12">
                            <div style="font-size: 16px;font-weight: bold;display: flex;align-items: center;gap:20px; justify-content: flex-start;">
                                <el-button type="primary" text bg @click="onClickLeft()">
                                    <i class="fa-solid fa-arrow-left"></i>
                                </el-button>
                                <span>
                                    {{ currentSceneInfo?.documentName }} 阅读分析
                                </span>
                            </div>
                            <div class="document-wrapper" v-loading="loadingDocument">
                                <el-scrollbar class="scrollable-area" style="height: calc(-140px + 100vh);margin-top: 20px;padding-right: 0px;background: #fafafa;border-radius: 5px;">
                                    <div id="fileshow" class="content" style="width: 100%;height:auto ;border-radius: 5px;" ref="contentRef"></div>
                                </el-scrollbar>
                            </div>
                        </el-col>
                        <el-col :span="12" style="padding-left: 20px;">
                            <!-- 配置和操作toolbar-->
                            <div class="toolbar-container">
                                <div class="reader-toolbar">
                                    <div>
                                        阅读角色
                                    </div>
                                    <div class="avatar-group">
                                        <el-avatar 
                                            v-for="item in avatarGroup" 
                                            :key="item.id"
                                            :size="30" 
                                            :src="item.avatar">
                                        </el-avatar>
                                    </div>
                                </div>
                                <div>
                                    <el-button type="primary" text bg @click="getDocxContent">
                                        <i class="fa-solid fa-arrows-rotate"></i>&nbsp;刷新
                                    </el-button>
                                </div>
                            </div>

                            <!-- 聊天面板-->
                            <div>
                                <ReaderTabPanel ref="readerTabPanelRef" />
                            </div>
                        </el-col>
                    </el-row>

                </div>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>

import { useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()

import FunctionList from './functionList'
import ReaderTabPanel from './readerTab'

import axios from "axios";
import { getScene } from '@/api/base/im/scene/documentReader';
import { getPreviewDocx } from '@/api/base/im/scene/documentReader';
import { renderAsync } from 'docx-preview';

const sceneId = ref(route.query.sceneId)
const currentSceneInfo = ref({});
const loadingDocument = ref(true)
const contentRef = ref(null)
const readerTabPanelRef = ref(null)

const avatarGroup = ref([
    {
        id: 1,
        name: '张三',
        avatar: 'http://staticok.oss-cn-hangzhou.aliyuncs.com/avatar-share/thumbnail-6046836d-7766-4bfd-a93d-85fd52d2b0e4.webp' , 
    } , 
    {
        id: 2,
        name: '李四',
        avatar: 'http://alinesno-infra-smart-im-ui.beta.base.infra.linesno.com/prod-api/v1/api/infra/base/im/chat/displayImage/1851917403521929217' , 
    } , 
    {
        id: 3,
        name: '王五',
        avatar: 'http://staticok.oss-cn-hangzhou.aliyuncs.com/avatar-share/thumbnail-d809e295-17bb-4247-9d3e-daafedefdf6e.webp' , 
    }
])

const getDocxContent = async () => {
    try {
        loadingDocument.value = true;

        const response = await getPreviewDocx(sceneId.value);

        console.log('response.data: ' + response); // 打印数据内容
        renderAsync(response, contentRef.value, null, {
            inWrapper: true
        });

        loadingDocument.value = false;

        console.log('文档渲染成功');
    } catch (error) {
        console.error('获取或渲染文档失败', error);
    }
}

const onClickLeft = () => {
    // 判断历史记录中是否有回退
    if (history.state?.back) {
        router.back()
    } else {
        router.push('/')
    }
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;

    })
}

nextTick(() => {
    handleGetScene();
    getDocxContent()
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';
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

.ppt-pager-aside{
  padding: 0px;
  border-right: 1px solid #f2f3f7;
  background: #fff;
  margin-bottom: 0px;
}

.ppt-pager-main{
  padding: 0px !important;
}

.pager-gen-result-panel{
  margin-bottom:20px;
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

.toolbar-container {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .reader-toolbar {
        display: flex;
        gap: 10px;
        align-items: center;

        .avatar-group{
            display: flex;
            align-items: center;
            gap: 7px;
        }
    }

}
</style>