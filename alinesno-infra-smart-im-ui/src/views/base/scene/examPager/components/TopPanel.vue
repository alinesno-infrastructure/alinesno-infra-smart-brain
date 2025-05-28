<template>
    <div class="top-panel">

        <div style="display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid rgb(232, 232, 232); padding: 0px 24px; height: 45px;">
            <div style="display: flex;align-items: center;">
                <div style="display: flex;gap: 10px;align-items: center;margin-right:10px;">
                    <el-button text bg type="primary" @click="handleBack()">
                        <i class="fa-solid fa-arrow-left"></i>
                    </el-button>
                    <span>
                        {{ props.currentPageInfo?.title }}
                    </span>
                </div>
                <el-button  type="primary" text bg @click="handleMenu">
                    <i class="fa-solid fa-file-pen"></i>&nbsp;编辑
                </el-button>
                <el-button  type="primary" text bg @click="handleMenu">
                    <i class="fa-solid fa-eye"></i>&nbsp;预览
                </el-button>
            </div>

            <div style="display: flex;flex-direction: row;align-items: center;align-content: center;">
                <el-button  type="warning" text bg @click="handleSavePager()">
                  <i class="fa-solid fa-floppy-disk"></i>&nbsp;保存 
                </el-button>
                <el-button  type="danger" text bg @click="handleMenu">
                    <i class="fa-solid fa-share"></i>&nbsp;发布并分享
                </el-button>

            </div>
        </div>
    </div>
</template>

<script setup>

import { ref , defineEmits } from 'vue'
const emit = defineEmits(['handleSavePager'])

const router = useRouter()
const route = useRoute()

const sceneId = ref(route.query.sceneId)

const props = defineProps({
    currentPageInfo : {
        type: Object,
        default: null 
    }
})

const handleSavePager = () => {
    emit('handleSavePager')
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
}
</style>
