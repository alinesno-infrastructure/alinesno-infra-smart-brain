<template>
    <div class="review-checklist-container">
        <div class="header">
            审查清单：智能生成
        </div>
        <div class="rule-section">
            <div class="rule-title">
                全部规则({{ contractReviewList.length }})
            </div>
            <el-scrollbar class="scrollable-area">
                <div class="checkbox-group-container">
                    <el-checkbox-group v-model="checkList" style="display: flex;flex-direction: column;">
                        <el-checkbox v-for="(item , index) in contractReviewList" :key="item.id" :label="item.id" size="large">
                            <template #default>
                                {{index+1}}.{{item.ruleName}}
                            </template>
                        </el-checkbox>
                    </el-checkbox-group>
                </div>
            </el-scrollbar>
            <div class="button-section">
                <!-- <el-button type="primary" text bg size="large">自定义审核规则</el-button> -->
                <el-button type="primary" style="width:100%" size="large">
                    <i class="fa-solid fa-rocket"></i> 发起文档审核
                </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>

import { getScene } from '@/api/base/im/scene/documentReview';

const route = useRoute();
const currentSceneInfo = ref(null);
const sceneId = ref(route.query.sceneId)

const contractReviewList = ref([]);

const checkList = ref([]);

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
        contractReviewList.value = res.data.reviewListDtos ;

        // 将所有选项的 ruleName 赋值给 checkList 实现全选
        checkList.value = contractReviewList.value.map(item => item.id);
    })
}

onMounted(() => {
    handleGetScene();
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';
</style>