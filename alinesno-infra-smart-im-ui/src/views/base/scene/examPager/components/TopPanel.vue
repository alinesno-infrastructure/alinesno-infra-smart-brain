<template>
    <div class="top-panel"> 
        <div class="top-panel-header">
            <div style="display: flex;align-items: center;">
                <div style="display: flex;gap: 10px;align-items: center;margin-right:10px;">
                    <el-button text bg type="primary" @click="handleBack()">
                        <i class="fa-solid fa-arrow-left"></i>
                    </el-button>
                    <span>
                        {{ props.currentPageInfo?.title }}
                    </span>
                </div>
                <el-button type="primary" text bg @click="handleMenu">
                    <i class="fa-solid fa-file-pen"></i>&nbsp;编辑
                </el-button>
                <el-button type="primary" text bg @click="handleMenu">
                    <i class="fa-solid fa-eye"></i>&nbsp;预览
                </el-button>
            </div>

            <div style="display: flex;flex-direction: row;align-items: center;align-content: center;">
                <el-button type="warning" text bg @click="handleSavePager()">
                    <i class="fa-solid fa-floppy-disk"></i>&nbsp;保存
                </el-button>
                <el-button type="primary" text bg @click="handleExportPaper(props.currentPageInfo)">
                    <i class="fa-solid fa-floppy-disk"></i>&nbsp;导出试卷
                </el-button>
                <el-button type="primary" text bg @click="handleExportPaper(props.currentPageInfo, true)">
                    <i class="fa-solid fa-floppy-disk"></i>&nbsp;导出试卷(含答案)
                </el-button>
                <el-button type="danger" text bg @click="handlePublishExam(props.currentPageInfo)">
                    <i class="fa-solid fa-share"></i>&nbsp;发布考试
                </el-button>

            </div>
        </div>
    </div>
</template>

<script setup>

import { ref, defineEmits } from 'vue'
import { ElLoading , ElMessage } from 'element-plus'
// import {
//     exportPaper,
//     getExportUrl
// } from '@/api/base/im/scene/examPaperManager'

const emit = defineEmits(['handleSavePager', 'handleExportPaper' , 'handlePublishExam'])

const { proxy } = getCurrentInstance();
const router = useRouter()
const route = useRoute()

const sceneId = ref(route.query.sceneId)

const props = defineProps({
    currentPageInfo: {
        type: Object,
        default: null
    }
})

const handleSavePager = () => {
    emit('handleSavePager')
}

const handlePublishExam = (currentPageInfo) => {
    emit('handlePublishExam' , currentPageInfo)
}

// 导出试卷
const handleExportPaper = (pager , showAnswer = false) => {
    proxy.download('/api/infra/smart/assistant/scene/examPagerManager/export/' + pager.id + "?showAnswer=" + showAnswer, {}, 
        `${pager.title}_${new Date().getTime()}.docx`)
}

const handleBack = () => {
    console.log('返回')
    const path = '/scene/examPager/pagerManager';
    router.push({
        path: path,
        query: { 'sceneId': sceneId.value }
    })
}

</script>

<style lang="scss" scoped>
.top-panel {
    background: #fff;

    .top-panel-header {
        display: flex; 
        align-items: center; 
        justify-content: space-between; 
        border-bottom: 1px solid rgb(232, 232, 232); 
        padding: 0px 24px; height: 45px;
    }
}
</style>
