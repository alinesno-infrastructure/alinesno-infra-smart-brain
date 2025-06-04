<template>
    <div class="data-display-container">

        <div class="time-data-range-container">
            <span class="title">
                {{ platItem?.title || '未选择章节' }} 
            </span>
            <span class="description">
                {{ platItem?.description || '暂无描述' }} 
            </span>
        </div>

        <div>
            <div id="vditor" class="data-vditor-panel"></div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

import Vditor from 'vditor';
import 'vditor/dist/index.css';

const contentEditor = ref(null);
const platItem = ref({
    title:'' , 
    description:''
});

import { 
    // updateChapterContentEditor , 
    // getChapterByRole , 
    getChapterContent , 
    // updateChapterContent,
    // chatRoleSync,
    // getScene,
    // getPreviewDocx , 
    // dispatchAgent , 
    // uploadOss , 
    // getPreviewUrl , 
} from '@/api/base/im/scene/generalAgent'

const defaultMd = `` ;

const setData = (planItemVal) => {
    platItem.value = planItemVal;

    getChapterContent(planItemVal.id).then(res => {
        contentEditor.value.setValue(res.data == null ? '' : res.data) ; 
    })
}

const setPlanItem = (planItemVal) => {
    console.log('setPlanItem = ' + JSON.stringify(planItemVal));
    platItem.value.title = planItemVal.label;
    platItem.value.description = planItemVal.description;
}

const setPlanItemContentData = (content) => {
    contentEditor.value.setValue(content);
}

onMounted(() => {
    contentEditor.value = new Vditor('vditor', {
        height: 'calc(100vh - 220px)',
        width: '100%',
        mode: "wysiwyg",
        cdn: '/vditor',
        toolbarConfig: {
            pin: true,
        },
        cache: {
            enable: false,
        },
        after: () => {
            contentEditor.value.setValue(defaultMd);
        }
    });
});

defineExpose({
    setData , 
    setPlanItem ,
    setPlanItemContentData 
});

</script>

<style lang="scss" scoped>
.data-display-container {
    margin-top: -10px;
    height: calc(100vh - 160px);

    .time-data-range-container {
        display: flex;
        flex-direction: column;
        gap: 8px;
        margin-bottom: 20px;

        .title {
            font-size: 21px;
            font-weight: bold;
        }

        .description {
            font-size: 13px;
            color: #a5a5a5;
        }
    }
}
</style>

<style >
.data-vditor-panel .vditor-toolbar , 
.data-vditor-panel .vditor-reset
{
    padding-left: 10px !important;
    padding-right: 10px !important;
}
</style>