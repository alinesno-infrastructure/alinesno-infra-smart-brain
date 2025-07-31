<template>
    <div class="content-formatter-container exam-pagercontainer">
        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="80px" class="exam-pager-aside">
                <FunctionList />
            </el-aside>

            <el-main class="exam-pager-main">
                <!-- 主要内容显示 -->
                <slot></slot>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import FunctionList from './functionList'

</script>

<style lang="scss" scoped>
.content-formatter-container {
    background: #fff;
    height: calc(100vh - 50px);

    .send-button {
        width: 100%;
    }

    .main-content {
        display: flex;
        flex-direction: column;
        padding-top: calc(3vh - 56px);
        text-align: center;
        max-width: 1024px;
        margin: auto;
    }
}
</style>
