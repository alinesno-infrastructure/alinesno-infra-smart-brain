<template>

    <DocumentReaderContainer>
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
    </DocumentReaderContainer>
</template>

<script setup>

import { useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()

import FunctionList from './functionList'
import ReaderTabPanel from './readerTab'
import DocumentReaderContainer from './documentReaderContainer'

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