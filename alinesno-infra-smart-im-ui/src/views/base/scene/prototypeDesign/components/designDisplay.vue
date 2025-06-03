<template>
    <div class="data-display-container">

        <div class="time-data-range-container">
            <span class="title">
                {{ platItem?.title || '未选择章节' }} 
            </span>
        </div>

        <div id="vditor" class="data-vditor-panel">
            <iframe 
                class="inner-iframe"
                src="http://data.linesno.com/demo_index.html"
                sandbox="allow-scripts allow-same-origin allow-downloads allow-popups allow-top-navigation-by-user-activation allow-top-navigation-to-custom-protocols"
                frameborder="0">
            </iframe>
        </div>

    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

// import Vditor from 'vditor';
// import 'vditor/dist/index.css';

const contentEditor = ref(null);
const platItem = ref({
    title:'' , 
    description:''
});

import { 
    getChapterContent , 
} from '@/api/base/im/scene/generalAgent'

const htmlContent = ref(`

`);

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

    .inner-iframe {
        width: 100%;
        height: calc(100vh - 190px);
        border: 1px solid #e5e5e5;
        border-radius: 10px;
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