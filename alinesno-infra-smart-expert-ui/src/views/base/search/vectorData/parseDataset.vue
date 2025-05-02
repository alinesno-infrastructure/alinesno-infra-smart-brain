<template>
    <div class="app-container">
        <el-page-header @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3" style="font-size: 16px;"> 配置 [{{ currentDataset?.name }}]</span>
            </template>
        </el-page-header>
        <div style="margin-top:20px">
            <DatasetList :dataset="currentDataset" />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { getParam } from "@/utils/ruoyi";

import {
    getDataset
} from "@/api/base/search/vectorDataset";

import DatasetList from './datasetList';
import DatasetSearch from './datasetSearch';

const currentDatasetId = getParam("datasetId");

const tabPosition = ref('left')
const router = useRouter();
const currentDataset = ref(null);
const datasetSearchRef = ref(null);

const goBack = () => {
    let path = '/base/search/vectorData/index';
    router.push(path)
}

const handleGetDataset = () => {
    getDataset(currentDatasetId).then(res => {
        currentDataset.value = res.data;
    })
}

const handleClick = (tab , event) => {
    datasetSearchRef.value.setDataset(currentDataset.value);
    handleGetDataset();
}

handleGetDataset();

</script>

<style lang="scss">
.demo-tabs {
    .el-tabs__nav-wrap::after {
        height: 0px !important;
    }
}
</style>