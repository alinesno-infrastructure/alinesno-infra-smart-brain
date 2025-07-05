<template>
        <div class="container-main" style="display: flex;flex-direction: row;justify-content: space-between;">

            <div style="width: calc(100% - 0px);margin: 0px;" v-loading="fullscreenLoading">

                <section>
                    <div class="section-body">
                        <el-row>
                            <el-col :span="6" v-for="(item, i) in publicRoleList" :key="i" style="padding:8px;">
                                <div class="semi-card-container" @click="handleRoleChat(item)">
                                    <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row;display: flex;">
                                        <div class="cart-head-content">
                                            <div class="cart-head-content">
                                                <span class="semi-avatar semi-avatar-square"
                                                    style="border-radius: 8px;">
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
                                                <span class="semi-typography text" style="flex: 1;font-size: 13px;font-weight: 400;line-height: 18px;color: #a5a5a5;">
                                                    <i class="fa-solid fa-user-shield"></i>
                                                    {{ item.orgName }}
                                                </span>
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


                <el-row v-if="publicRoleList.length == 0 && !fullscreenLoading">
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
    <!-- </el-scrollbar> -->
</template>

<script setup name="ServiceList">

import SnowflakeId from "snowflake-id";

import {
    getWorkplaceItem
} from "@/api/base/im/workplace"

import learnLogo from '@/assets/icons/data_03.svg';
const snowflake = new SnowflakeId();

const fullscreenLoading = ref(true)
const router = useRouter();
const dialogVisible = ref(false)
const roleChatUri = ref("")

const publicRoleList = ref([])
const orgRoleList = ref([])

/** 与单个Role发信息 */
function handleRoleChat(item) {
    router.push({
        path: '/single/agentChat',
        query: { 'roleId': item.id, 'channelId': snowflake.generate() }
    })
}

/** 关闭对话框 */
function handleClose() {
    dialogVisible.value = false;
}

const handleGetWorkplaceItem = (workplaceId , type) => {
    getWorkplaceItem(workplaceId , type).then(response => {
        publicRoleList.value = response.data || [];
        fullscreenLoading.value = false 
    })
} 

// 对外暴露的方法 
defineExpose({
    handleGetWorkplaceItem
})

</script>
