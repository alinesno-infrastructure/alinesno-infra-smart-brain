<template>
    <div class="app-container workflow-container">
        <div class="page-header-container">
            <el-page-header @back="goBack">
                <template #content>
                    <div class="header-content">
                        <span class="role-info">
                            <img :src="imagePath(currentRole.roleAvatar)" class="role-avatar" />
                            {{ currentRole.roleName }} 发布渠道配置
                        </span>
                        <span class="save-time">保存时间：2025-02-14 23:50:44</span>
                    </div>
                </template>
            </el-page-header>
        </div>
        <div>
            <el-row :gutter="20">
                <el-col :span="24" :xs="24">
                    <div class="channel-list">
                        <div style="margin: 30px 0px;text-align: center;">
                            <div style="margin-top: 20px;font-size: 28px;color: rgb(51, 51, 51);font-weight: bold;">
                                <i class="fa-solid fa-square-up-right"></i> 发布平台
                            </div>
                            <div style="margin-top: 10px;margin-bottom:5px; color: #222; font-size: 15px;font-weight: normal;">
                                在以下平台发布你的智能体，即表示你已充分理解并同意遵循各发布渠道服务条款（包括但不限于任何隐私政策、社区指南、数据处理协议等）。
                            </div>
                        </div>
                        <div v-for="channel in channelList" :key="channel.id" class="cc-agent-channel-box" >
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i :class="channel.iconClass" class="channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">{{ channel.name }}</span>
                                    <span class="channel-desc">{{ channel.desc }}</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="large" text plain>
                                    {{ channel.isConfigured ? '已配置' : '未配置' }}
                                </el-button>
                                <el-button type="primary" text bg plain size="large" @click="configureChannel(channel)">
                                    <i class="fa-solid fa-screwdriver-wrench"></i> &nbsp; 配置
                                </el-button>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 配置Chat窗口 模态框 -->
        <el-dialog v-model="showChatWindowConfig" title="配置Chat窗口" :width="800 + 'px'">
            <div class="dialog-content">
                <div class="instruction">
                    <span>复制下面 iframe 加入到你的网站中</span>
                </div>
                <div class="code-block">
                </div>
            </div>
            <template #footer>
                <el-button type="primary" text bg icon="CopyDocument" @click="copyCode">复制代码片段</el-button>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getRole } from "@/api/smart/assistant/role";

import { updateChannelPublish , getChannels } from "@/api/smart/assistant/channelPublish";

const router = useRouter();
const { proxy } = getCurrentInstance();

const currentRole = ref({
    roleName: ''
});

const currentRoleId = ref(null);

const showChatWindowConfig = ref(false);

// 渠道列表，每个元素添加了 id 属性
const channelList = ref([]);

// // 定义渠道参数对象
// const channels = ref({
//     wechat_miniprogram: {
//         param: 'wechat_miniprogram_param'
//     },
//     service_account: {
//         param: 'service_account_param'
//     },
//     customer_service: {
//         param: 'customer_service_param'
//     },
//     subscription_account: {
//         param: 'subscription_account_param'
//     },
//     dingtalk: {
//         param: 'dingtalk_param'
//     },
//     api_interface: {
//         param: 'api_interface_param'
//     },
//     chat_window: {
//         enable: true,
//         param: 'chat_window_param',
//         token: '1234567890',
//         ipLimit: '100', // IP 限流（人/分钟）
//         outTime: '1d'   // 30m/1d/7d/30d/never (30分钟/1天/7天/30天/永久)
//     }
// });

const copyCode = () => {
    const code = `<iframe
  src="http://portal.infra.linesno.com"
  style="width: 100%; height: 100%;"
  frameborder="0" 
  allow="*"
/>;`;
    const textarea = document.createElement('textarea');
    textarea.value = code;
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
    proxy.$modal.msgSuccess("已复制");
}

/** 返回 */
function goBack() {
    router.go(-1);
}

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId;
    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
    });
}

/** 配置渠道 */
function configureChannel(channel) {
    if (channel.paramKey === 'chat_window') {
        showChatWindowConfig.value = true;
    }
    // 模拟配置成功
    channel.isConfigured = true;
}

const handleGetChannels = () => {
    getChannels().then(response => {
        channelList.value = response.data;
    });
};

/** 初始化数据 */
onMounted(() => {
    console.log('onMounted');
    getRoleInfo();
    handleGetChannels();
});
</script>

<style lang="scss" scoped>
.app-container.workflow-container {
    .page-header-container {
        .el-page-header {
            .header-content {
                display: flex;
                gap: 10px;

                .role-info {
                    display: flex;
                    align-items: center;
                    gap: 5px;
                    margin-right: 15px;

                    .role-avatar {
                        width: 25px;
                        height: 25px;
                        border-radius: 5px;
                    }
                }

                .save-time {
                    color: #aaaaaa;
                    font-size: 14px;
                }
            }
        }
    }

    .channel-list {
        max-width: 980px;
        margin: auto;
        padding: 15px 0px;
        gap: 15px;
        display: flex;
        flex-wrap: wrap;

        .cc-agent-channel-box {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border: 1px solid #efeff0;
            flex-basis: calc(100%);
            background: #FBFBFC;
            padding: 15px;
            border-radius: 5px;
            position: relative;

            .cc-agent-channel-box-item {
                font-size: 14px;
                color: rgb(68, 68, 68);
                display: flex;
                align-items: flex-start;
                gap: 10px;

                .channel-icon {
                    font-size: 25px;
                    color: #3b5998;

                    .channel-img {
                        width: 35px;
                        height: 35px;
                        border-radius: 5px;
                        text-align: center;
                        line-height: 35px;
                    }
                }

                .channel-details {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;

                    .channel-name {
                        font-size: 16px;
                        font-weight: bold;
                        color: #333;
                    }

                    .channel-desc {
                        font-size: 13px;
                        color: #8c7f84;
                    }
                }
            }

            .channel-actions {
                display: flex;
                flex-direction: row;
                align-items: center;
            }
        }
    }

    .el-dialog {
        .dialog-content {
            padding: 20px;
            display: flex;
            margin: 15px;
            gap: 20px;
            margin-bottom: 20px;
            flex-direction: column;
            background: #efeff0;
            border-radius: 3px;

            .instruction {
                color: #222;
                font-size: 14px;
                font-weight: bold;
            }

            .code-block {
                white-space: pre-wrap;
            }
        }
    }
}
</style>