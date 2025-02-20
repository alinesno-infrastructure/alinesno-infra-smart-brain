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
                        <div>
                            <div style="margin-top: 20px;font-size: 18px; color:#333; font-weight: normal;">
                                发布平台
                            </div>
                            <div style="margin-top: 10px;margin-bottom:5px; color: #a5a5a5; font-size: 14px;font-weight: normal;">
                                在以下平台发布你的智能体，即表示你已充分理解并同意遵循各发布渠道服务条款（包括但不限于任何隐私政策、社区指南、数据处理协议等）。
                            </div>
                        </div>
                        <!-- 微信小程序 -->
                        <div class="cc-agent-channel-box" :data-param="channels.wechat_miniprogram.param">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fab fa-weixin channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">微信小程序</span>
                                    <span class="channel-desc">通过接口集成方式和窗口嵌入到当前系统中</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未授权</el-button>
                                <el-button type="primary" text plain>配置</el-button>
                            </div>
                        </div>
                        <!-- 服务号 -->
                        <div class="cc-agent-channel-box" :data-param="channels.service_account.param">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fab fa-weixin channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">服务号</span>
                                    <span class="channel-desc">通过接口与系统进行交互</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未授权</el-button>
                                <el-button type="primary" text plain>配置</el-button>
                            </div>
                        </div>
                        <!-- 客服 -->
                        <div class="cc-agent-channel-box" :data-param="channels.customer_service.param">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fas fa-user-tie channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">客服</span>
                                    <span class="channel-desc">提供客户服务接入系统的渠道</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未授权</el-button>
                                <el-button type="primary" text plain>配置</el-button>
                            </div>
                        </div>
                        <!-- 订阅号 -->
                        <div class="cc-agent-channel-box" :data-param="channels.subscription_account.param">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fab fa-weixin channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">订阅号</span>
                                    <span class="channel-desc">可推送消息到用户的订阅渠道</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未授权</el-button>
                                <el-button type="primary" text plain>配置</el-button>
                            </div>
                        </div>
                        <!-- 钉钉 -->
                        <div class="cc-agent-channel-box" :data-param="channels.dingtalk.param">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fab fa-alipay channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">钉钉</span>
                                    <span class="channel-desc">与钉钉平台集成的渠道</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未授权</el-button>
                                <el-button type="primary" text plain>配置</el-button>
                            </div>
                        </div>
                        <!-- API接口 -->
                        <div class="cc-agent-channel-box" :data-param="channels.api_interface.param">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fas fa-plug channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">API接口</span>
                                    <span class="channel-desc">提供系统间数据交互的接口渠道</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未授权</el-button>
                                <el-button type="primary" text plain>配置</el-button>
                            </div>
                        </div>
                        <!-- Chat窗口 -->
                        <div class="cc-agent-channel-box">
                            <div class="cc-agent-channel-box-item">
                                <div class="channel-icon">
                                    <i class="fas fa-comment-dots channel-img"></i>
                                </div>
                                <div class="channel-details">
                                    <span class="channel-name">Chat窗口</span>
                                    <span class="channel-desc">用于实时聊天的窗口渠道</span>
                                </div>
                            </div>
                            <div class="channel-actions">
                                <el-button type="info" size="small" text bg plain>未配置</el-button>
                                <el-button type="primary" @click="configChatWindow()" text plain>配置</el-button>
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
                <pre><code>&lt;iframe
    src="http://portal.infra.linesno.com"
    style="width: 100%; height: 100%;"
    frameborder="0" 
    allow="*"
/&gt;</code></pre>
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

const router = useRouter();
const { proxy } = getCurrentInstance();

const currentRole = ref({
    roleName: ''
});

const currentRoleId = ref(null);

const showChatWindowConfig = ref(false)

// 定义渠道参数对象
const channels = ref({
    wechat_miniprogram: { 
        param: 'wechat_miniprogram_param' 
    },
    service_account: { 
        param:'service_account_param' 
    },
    customer_service: { 
        param: 'customer_service_param' 
    },
    subscription_account: { 
        param:'subscription_account_param' 
    },
    dingtalk: { 
        param: 'dingtalk_param' 
    },
    api_interface: { 
        param: 'api_interface_param' 
    },
    chat_window: { 
        enable: true ,
        param: 'chat_window_param' , 
        token: '1234567890',
        ipLimit: '100', // IP 限流（人/分钟）
        outTime: '1d'   // 30m/1d/7d/30d/never (30分钟/1天/7天/30天/永久)
    }
});

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

/** 配置Chat窗口 */
function configChatWindow(){
    showChatWindowConfig.value = true
}

/** 初始化数据 */
onMounted(() => {
    console.log('onMounted');
    getRoleInfo();
});
</script>

<style lang="scss" scoped>

.header-content {
    display: flex;
    gap: 10px;
}

.role-info {
    display: flex;
    align-items: center;
    gap: 5px;
    margin-right: 15px;
}

.role-avatar {
    width: 25px;
    height: 25px;
    border-radius: 5px;
}

.save-time {
    color: #aaaaaa;
    font-size: 14px;
}

.channel-list {
    max-width: 980px;
    margin: auto;
    padding: 15px 0px;
    gap: 15px;
    display: flex;
    flex-wrap: wrap;
}

.cc-agent-channel-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border: 1px solid #efeff0;
    flex-basis: calc(100%);
    background: #efeff0;
    padding: 15px;
    border-radius: 5px;
    position: relative;
}

.cc-agent-channel-box-item {
    font-size: 14px;
    color: rgb(68, 68, 68);
    display: flex;
    align-items: flex-start;
    gap: 10px;
}

.channel-icon {
    /* 渠道图标容器样式 */
    font-size: 23px;
    color: #3b5998;
}

.channel-img {
    width: 35px;
    height: 35px;
    border-radius: 5px;
    text-align: center; /* 让图标居中 */
    line-height: 35px; /* 让图标垂直居中 */
}

.channel-details {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.channel-name {
    /* 渠道名称样式 */
    font-size: 15px;
    font-weight: bold;
    color: #333;
}

.channel-desc {
    font-size: 13px;
    color: #8c7f84;
}

.channel-actions {
    display: flex;
    flex-direction: row;
    align-items: center;
}

.dialog-content {
  padding: 20px;
  display: flex;
  margin: 15px;
  gap: 20px;
  margin-bottom: 20px;
  flex-direction: column;
  background: #efeff0;
  border-radius: 3px;
}

.instruction {
  color: #222;
  font-size: 14px;
  font-weight: bold;
}

.code-block {
  white-space: pre-wrap;
}

</style>