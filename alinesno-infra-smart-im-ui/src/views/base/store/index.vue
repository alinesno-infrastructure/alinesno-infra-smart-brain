<template>
    <el-scrollbar height="100vh">
        <div class="container-main" style="padding: 10px 10px 50px;display: flex;flex-direction: row;justify-content: space-between;">

            <SideTypePanel />

            <div class="tpl-app" style="width: calc(100% - 220px);margin: 0px;" v-loading="fullscreenLoading">
                <h2 class="section-title" style="margin-top: 5px;margin-left: 10px;font-size: 20px;">
                    <i class="type.banner" /> 智能体商店
                    <span style="font-size: 13px;color: #777;margin-left:10px;">由组织提供出来公共可用的智能体商店.</span>
                </h2>

                <el-form :model="queryParams" style="padding-left:10px;" ref="queryRef" :inline="true"  label-width="68px">
                        <el-form-item label="角色名称" prop="roleName">
                            <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable style="width: 240px" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" icon="Search" @click="handleAgentStoreRole">搜索</el-button>
                            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                        </el-form-item>
                </el-form>

                <section>
                    <div style="font-size: 20px;font-weight: 500;padding: 10px;" v-if="orgRoleList.length > 0">
                    <i class="fa-solid fa-user-ninja"></i> 推送角色
                    <span style="font-size: 13px;font-weight: normal;">
                        特定组织推送过来的角色，不对外公开
                    </span>
                    </div>
                    <div class="section-body">
                        <el-row>
                            <el-col :span="6" v-for="(item, i) in orgRoleList" :key="i" style="padding:8px;">
                                <div class="semi-card-container" >
                                    <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row;display: flex;">
                                        <div class="cart-head-content">
                                            <div class="cart-head-content" >
                                                <span class="semi-avatar semi-avatar-square" style="border-radius: 8px;" @click="handleRoleChat(item)">
                                                    <img :src="imagePathByPath(item.roleAvatar)">
                                                </span>
                                            </div>
                                        </div>
                                        <div class="semi-space info-container" style="gap: 6px;height:100px">

                                            <span class="semi-typography card-title" @click="handleRoleChat(item)">
                                                <span>
                                                    {{ item.roleName }}
                                                </span>
                                            </span>
                                            <div class="semi-space container-center" style="gap: 6px;">
                                                <span class="semi-typography text" style="flex: 1;font-size: 13px;font-weight: 400;line-height: 18px;color: #a5a5a5;">
                                                    <i class="fa-solid fa-paper-plane"></i>
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
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-message"></i> {{item.chatCount}}</span>
                                                <span class="semi-typography text-h6"><i class="fa-regular fa-thumbs-up"></i> {{item.like}}</span>
                                                <span class="semi-typography text-h6"><i class="fa-regular fa-thumbs-down"></i> {{item.dislike}}</span>
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

                <section>
                    <div style="font-size: 20px;font-weight: 500;padding: 10px;" v-if="publicRoleList.length > 0">
                        <i class="fa-solid fa-route"></i> 公共角色
                        <span style="font-size: 13px;font-weight: normal;">
                            对外所有组织都可以看到的角色
                        </span>
                    </div>
                    <div class="section-body">
                        <el-row>
                            <el-col :span="6" v-for="(item, i) in publicRoleList" :key="i" style="padding:8px;">
                                <div class="semi-card-container">
                                    <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row;display: flex;">
                                        <div class="cart-head-content">
                                            <div class="cart-head-content">
                                                <span class="semi-avatar semi-avatar-square"  @click="handleRoleChat(item)"
                                                    style="border-radius: 8px;">
                                                    <img :src="imagePathByPath(item.roleAvatar)">
                                                </span>
                                            </div>
                                        </div>
                                        <div class="semi-space info-container" style="gap: 6px;height:100px">


                                            <span class="semi-typography card-title"  @click="handleRoleChat(item)">
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
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-message"></i> {{item.chatCount}}</span>
                                                <span class="semi-typography text-h6"><i class="fa-regular fa-thumbs-up"></i> {{item.like}}</span>
                                                <span class="semi-typography text-h6"><i class="fa-regular fa-thumbs-down"></i> {{item.dislike}}</span>
                                            </div>
                                        </div>

                                        <div class="platform-container-YOpW3B">
                                            <div class="semi-space semi-space-align-center semi-space-horizontal" style="font-size:14px;gap:  4px;display: flex;color: #1d75b0;">
                                                <span v-if="item.roleType == 'single_role'">
                                                    <i class="fa-solid fa-user-ninja"></i>
                                                </span>
                                                <span v-if="item.roleType == 'collaborative_role'">
                                                    <i class="fa-solid fa-user-tag"></i>
                                                </span>
                                                <span v-if="item.roleType == 'scenario_role'">
                                                    <i class="fa-solid fa-user-secret"></i>
                                                </span>
                                                <span class="aip-collect-tip" @click="handleCollectAgent(item)" >
                                                    <i class="fa-solid fa-star"></i> 收藏
                                                </span>
                                            </div>
                                        </div>

                                    </div>

                                </div>
                            </el-col>
                        </el-row>

                    </div>
                </section>


                <el-row v-if="publicRoleList.length == 0 && orgRoleList.length == 0">
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

import { 
    getAgentStoreRole 
} from '@/api/base/im/store';

import {
    collectItem
} from "@/api/base/im/workplace"

import SideTypePanel from './sideTypePanel'
import learnLogo from '@/assets/icons/data_03.svg';

const { proxy } = getCurrentInstance();
const snowflake = new SnowflakeId();

const router = useRouter();
const productList = ref([])
const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

const publicRoleList = ref([])
const orgRoleList = ref([])
const demoProductList = ref([])
const dateRange = ref([]);

const data = reactive({
  form: {
    roleAvatar: null,
    scriptType: 'script'
  },
  queryParams: {
    pageNum: 1,
    pageSize: 50,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    industryCatalog: undefined
  },
  rules: {
    roleId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
    roleType: [{ required: true, message: "应用类型不能为空", trigger: "blur" }],
    scriptType: [{ required: true, message: "脚本类型不能为空", trigger: "blur" }],
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "角色名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    responsibilities: [{ required: true, message: "角色描述不能为空", trigger: "blur" }],
    domain: [{ required: true, message: "所属领域不能为空", trigger: "blur" }],
    roleLevel: [{ required: true, message: "角色级别不能为空", trigger: "blur" }],
    storagePath: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    industryCatalog: [{ required: true, message: "角色类型不能为空", trigger: "blur" }],
  },
  chainForm: {
    roleId: undefined,
  },
  chainRules: {
    chainName: [{ required: true, message: "链路名称不能为空", trigger: "blur" }],
    elData: [{ required: true, message: "链路流程不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules, chainForm, chainRules } = toRefs(data);


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

// 收藏角色
const handleCollectAgent = (item) => {
    console.log('收藏智能体')
    const data = {
        itemId: item.id, 
        type: 'agent'
    }
    collectItem(data).then(res => {
        proxy.$modal.msgSuccess("收藏成功.");
    })
}

const handleAgentStoreRole = () => { 
    getAgentStoreRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
        console.log('response = ' + response);
        publicRoleList.value = response.data.publicRoleList || [];
        orgRoleList.value = response.data.orgRoleList || [];
    })
}

handleAgentStoreRole();

</script>
