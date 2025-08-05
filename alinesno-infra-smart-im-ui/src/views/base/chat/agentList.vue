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
                                    <el-input 
                                        v-model="searchKeyword" 
                                        style="width: 400px" 
                                        size="large" 
                                        placeholder="搜索智能体" 
                                        :suffix-icon="Search"
                                        @input="handleAgentSearch"
                                    />
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </div>

                <div class="header">
                    <span style="font-size: 13px;margin-left:10px;color: #a5a5a5;">这里包含所有需要运营的团队能力服务列表</span>
                </div>

                <!-- 使用过滤后的列表 -->
                <section v-for="(type, index) in filteredAgentGroups" :key="index">
                    <h2 class="section-title" style="margin-left:10px">
                        <i :class="type.banner" /> {{ type.name }}
                    </h2>
                    <div class="section-body">

                        <el-row>
                            <el-col :span="6" v-for="(agent, i) in type.agents" :key="i" style="padding:8px;">
                                <div class="semi-card-container" @click="handleRoleChat(agent)">
                                    <!-- 卡片内容保持不变 -->
                                    <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row-reverse;">
                                        <div class="cart-head-content">
                                            <div class="cart-head-content">
                                                <span class="semi-avatar semi-avatar-square"
                                                    style="border-radius: 50%;">
                                                    <img :src="imagePathByPath(agent.roleAvatar)">
                                                </span>
                                            </div>
                                        </div>
                                        <div class="semi-space info-container" style="gap: 6px;height:100px">
                                            <span class="semi-typography card-title">
                                                <span>{{ agent.roleName }}</span>
                                            </span>
                                            <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;margin-bottom:0px">
                                                <span>{{ truncateString(agent.responsibilities, 100) }}</span>
                                            </p>
                                            <div class="semi-space card-tag-list" style="gap: 4px;"></div>
                                        </div>
                                    </div>
                                    
                                    <div class="semi-space" style="width: 100%;gap: 8px;display: flex;justify-content: space-between;align-items: center;">
                                        <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children" style="display: inline-flex;">
                                            <div class="semi-space card-statics">
                                                
                                                <el-button type="info" text size="small">
                                                  <i class="fa-solid fa-message"></i>&nbsp;{{agent.chatCount}}
                                                </el-button>

                                                <el-button type="info" v-if="agent.scriptType === 'flow'" text size="small">
                                                  <i class="fa-solid fa-sailboat"></i>流程
                                                </el-button>
                                                <el-button type="info" v-if="agent.scriptType === 'react'" text size="small">
                                                  <i class="fas fa-file-signature"></i>推理
                                                </el-button>
                                                <el-button type="info" v-if="agent.scriptType === 'script'" text size="small">
                                                  <i class="fa-solid fa-code"></i> 脚本 
                                                </el-button>
                                                <!-- deepsearch-->
                                                 <el-button type="info" v-if="agent.scriptType === 'deepsearch'" text size="small">
                                                  <i class="fa-solid fa-magnifying-glass"></i> 深度搜索
                                                </el-button>
                                            </div>
                                        </div>

                                        <div class="platform-container-YOpW3B">
                                            <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;display: flex;color: #1d75b0;">
                                            <span class="semi-typography text-h6"></span>
                                                <span v-if="agent.roleType == 'single_role'"><i class="fa-solid fa-user-ninja"></i></span>
                                                <span v-if="agent.roleType == 'collaborative_role'"><i class="fa-solid fa-user-tag"></i></span>
                                                <span v-if="agent.roleType == 'scenario_role'"><i class="fa-solid fa-user-secret"></i></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </el-col>
                        </el-row>
                    </div>
                </section>
                
                <!-- 空状态判断使用过滤后的列表 -->
                <el-row v-if="filteredAgentGroups.length == 0">
                    <el-col :span="24">
                        <el-empty 
                            :image-size="400"
                            :image="learnLogo"
                            description="当前未找到匹配的智能体，请尝试其他搜索关键词或查看全部智能体。" />
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
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import learnLogo from '@/assets/icons/data_03.svg';
import { Search } from '@element-plus/icons-vue'; // 确保导入Search组件

const snowflake = new SnowflakeId();
const router = useRouter();

// 状态管理变量
const agentGroups = ref([]); // 原始智能体分组数据
const searchKeyword = ref(''); // 搜索关键词
const filteredAgentGroups = ref([]); // 过滤后的智能体分组
const dialogVisible = ref(false);
const roleChatUri = ref("");
const chatTitle = ref("");

/** 初始化过滤列表 */
function initFilteredList() {
  filteredAgentGroups.value = [...agentGroups.value];
}

/** 处理智能体搜索 */
function handleAgentSearch() {
  const keyword = searchKeyword.value.trim().toLowerCase();
  
  // 如果搜索为空，显示全部
  if (!keyword) {
    initFilteredList();
    return;
  }
  
  // 根据智能体名称过滤
  filteredAgentGroups.value = agentGroups.value
    .map(group => ({
      ...group,
      agents: group.agents.filter(agent => 
        agent.roleName.toLowerCase().includes(keyword)
      )
    }))
    .filter(group => group.agents.length > 0); // 过滤掉没有匹配项的分组
}

/** 与单个智能体聊天 */
function handleRoleChat(agent) {
  router.push({
    path: '/single/agentChat',
    query: { 'roleId': agent.id, 'channelId': snowflake.generate() }
  });
}

/** 关闭对话框 */
function handleClose() {
  dialogVisible.value = false;
}

/** 获取智能体列表数据 */
function fetchAgentGroups() {
  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
  });
  
  getAllCatalog().then(response => {
    console.log('智能体分组数据:', response);
    agentGroups.value = response.data;
    initFilteredList(); // 初始化过滤列表
  }).catch(error => {
    console.error('获取智能体数据失败:', error);
  }).finally(() => {
    loading.close();
  });
}

// 监听搜索关键词变化，实时过滤
watch(searchKeyword, handleAgentSearch);

// 初始加载数据
fetchAgentGroups();
</script>
