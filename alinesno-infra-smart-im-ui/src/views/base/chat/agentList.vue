<template>
    <el-scrollbar height="100vh">

        <div class="container-main" style="padding:10px;padding-bottom:50px;">
            <div class="tpl-app" v-loading="fullscreenLoading">

                <div class="search-container-panel">
                    <el-row>
                        <el-col :span="24">
                            <div class="feature-header-xqbyQk feature-team-box">
                                <div style="gap: 12px;">
                                    <h1>我的团队</h1>
                                </div>
                                <div class="search-container-weDuEn">
                                    <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索智能体"
                                        :suffix-icon="Search" />
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </div>

                <div class="header">
                    <span style="font-size: 13px;margin-left:10px;color: #a5a5a5;">这里包含所有需要运营的团队能力服务列表</span>
                </div>

                <!-- 
                <div class="popular border-bottom">
                    <div class="popular-item" v-for="(item, index) in demoProductList" :key="index">
                        <img :alt="item.name" class="popular-item__banner"
                            :src="'http://data.linesno.com/banner/' + (index % 4 + 1) + '.png'" />
                        <h1 class="popular-item__title">
                            {{ item.name }}
                        </h1>
                        <div class="popular-item__desc" style="text-align: left">
                            {{ truncateString(item.description, 20) }}
                        </div>
                    </div>
                </div> 
                -->

                <section v-for="(type, index) in productList" :key="index">
                    <h2 class="section-title" style="margin-left:10px">
                        <i :class="type.banner" /> {{ type.name }}
                    </h2>
                    <div class="section-body">

                        <el-row>
                            <el-col :span="6" v-for="(item, i) in type.agents" :key="i" style="padding:8px;">
                                <div class="semi-card-container" @click="handleRoleChat(item)">
                                    <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row-reverse;">
                                        <div class="cart-head-content">
                                            <div class="cart-head-content">
                                                <span class="semi-avatar semi-avatar-square"
                                                    style="border-radius: 50%;">
                                                    <img :src="imagePathByPath(item.roleAvatar)">
                                                </span>
                                            </div>
                                        </div>
                                        <div class="semi-space info-container" style="gap: 6px;height:100px">


                                            <span class="semi-typography card-title">
                                                <span>
                                                    {{ item.roleName }}
                                                </span>
                                            </span>
                                            <div class="semi-space container-center" style="gap: 6px;">
                                                <div class="semi-image avatar-oDHtb3"
                                                    style="width: 14px; height: 14px;">
                                                    <img src="http://data.linesno.com/switch_header.png"
                                                        class="semi-image-img" style="border-radius: 50%;">
                                                </div>
                                                <div class="semi-space semi-space-align-center semi-space-horizontal"
                                                    style="gap: 2px;">
                                                    <span class="semi-typography text"
                                                        style="max-width: 150px;"><span>韦恩W</span></span>
                                                </div>
                                            </div>
                                            <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;margin-bottom:0px">
                                                <span>
                                                    {{ truncateString(item.responsibilities, 100) }}
                                                </span>
                                            </p>
                                            <div class="semi-space card-tag-list" style="gap: 4px;"></div>
                                        </div>
                                    </div>

                                    <div class="semi-divider semi-divider-horizontal"></div>
                                    
                                    <div class="semi-space" style="width: 100%;gap: 8px;display: flex;justify-content: space-between;align-items: center;">
                                        <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children" style="display: inline-flex;">
                                            <div class="semi-space card-statics" style="gap: 8px;">
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 1.2K</span>
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                                            </div>
                                        </div>

                                        <div class="platform-container-YOpW3B">
                                            <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;display: flex;color: #1d75b0;" v-if="item.roleType">
                                                <span v-if="item.roleType == 'single_role'">
                                                    <i class="fa-solid fa-user-ninja"></i>
                                                </span>
                                                <span v-if="item.roleType == 'collaborative_role'">
                                                    <i class="fa-solid fa-user-tag"></i>
                                                </span>
                                                <span v-if="item.roleType == 'scenario_role'">
                                                    <i class="fa-solid fa-user-secret"></i>
                                                </span>
                                            </div>
                                        </div>

                                    </div>

                                </div>
                            </el-col>
                        </el-row>

                    </div>
                </section>
                <el-row v-if="productList.length == 0">
                    <el-col :span="24">
                        <el-empty 
                            :image-size="400"
                            :image="learnLogo"
                            description="当前未创建团队，可以进入智能体人才市场选择智能体，或者自行创建团队。" />
                    </el-col>
                </el-row>
            </div>

            <el-dialog v-model="dialogVisible" width="860" :before-close="handleClose">
                <iframe :src="roleChatUri" class="role-chat-iframe"></iframe>
            </el-dialog>

        </div>
    </el-scrollbar>
</template>

<script setup name="ServiceList">

import { ElLoading } from 'element-plus'
import { getAllCatalog } from "@/api/base/im/robot";
import SnowflakeId from "snowflake-id";

import learnLogo from '@/assets/icons/data_03.svg';

const snowflake = new SnowflakeId();

const router = useRouter();
const productList = ref([])
const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")


const demoProductList = ref([])

/**  获取产品列表 */
function handleGetProductList() {
    const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
    })
    getAllCatalog().then(response => {
        console.log('response = ' + response);
        productList.value = response.data;

        // 循环处理 jsonData.data 数组中的每一个元素
        response.data.forEach(item => {
            if (demoProductList.value.length < 7) {
                demoProductList.value.push({
                    name: item.name, // 使用 data 中的 name 字段
                    typeDescribe: "", // 如果没有对应的字段，这里可以留空或自定义
                    teamSize: 0, // 如果没有对应的字段，这里可以留空或自定义
                    mainResponsibilities: [item.description], // 使用 data 中的 description 字段作为 responsibilities 的一个元素
                    description: item.description // 使用 data 中的 description 字段
                });
            }
        });

        loading.close();
    });
}

/** 与单个Role发信息 */
function handleRoleChat(item) {

    router.push({
        path: '/single/agentChat',
        query: { 'roleId': item.id, 'channelId': snowflake.generate() }
    })

    // roleChatUri.value = "/agentChat?roleId=" + item.id;
    // chatTitle.value = item.roleName;
    // dialogVisible.value = true;
}

/** 关闭对话框 */
function handleClose() {
    dialogVisible.value = false;
}

handleGetProductList();

</script>
