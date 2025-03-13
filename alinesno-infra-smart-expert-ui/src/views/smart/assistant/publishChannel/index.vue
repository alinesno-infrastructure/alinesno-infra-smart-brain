<template>
    <div class="app-container publish-channel-container">
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
                            <div style="margin-top: 20px;margin-bottom:5px; color: #222; font-size: 15px;font-weight: normal;">
                                在以下平台发布你的智能体，即表示你已充分理解并同意遵循各发布渠道服务条款（包括但不限于任何隐私政策、社区指南、数据处理协议等）。
                            </div>
                        </div>
                        <div v-for="channel in channelList" :key="channel.id" class="cc-agent-channel-box">
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
                                <el-button v-if="channel.isConfigured" type="primary" text bg plain size="large" @click="startUsing(channel)">
                                    开始使用
                                </el-button>
                                <el-button :type="channel.isConfigured ? 'primary' : 'info'" size="large" text plain>
                                    {{ channel.isConfigured ? '已配置' : '未配置' }}
                                </el-button>
                                <el-button  type="primary" text bg plain size="large" @click="configureChannel(channel)">
                                    <i class="fa-solid fa-screwdriver-wrench"></i> &nbsp; 配置
                                </el-button>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 配置模态框 -->
        <el-dialog v-model="showConfigDialog" :title="currentConfigChannel.name ? `${currentConfigChannel.name} 渠道配置` : '渠道配置'" :width="800 + 'px'">
            <el-form :model="configForm" :rules="formRules" ref="configFormRef" label-width="120px" style="margin-top:30px;">
                <el-form-item label="分享名称" prop="shareName">
                    <el-input v-model="configForm.shareName" placeholder="请输入分享名称"></el-input>
                </el-form-item>
                <el-form-item label="过期时间">
                    <el-row style="width:100%">
                        <el-col :span="15">
                            <el-radio-group v-model="configForm.expireType">
                                <el-radio :label="0">无限</el-radio>
                                <el-radio :label="1">7天</el-radio>
                                <el-radio :label="2">30天</el-radio>
                                <el-radio :label="3">180天</el-radio>
                                <el-radio :label="4">自定义</el-radio>
                            </el-radio-group>
                        </el-col>
                        <el-col :span="9" v-if="configForm.expireType === 4">
                            <el-date-picker
                                style="width: 100%;"
                                v-model="configForm.expireTime"
                                type="datetime"
                                placeholder="请选择日期和时间"
                            />
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-form-item label="QPM" prop="qpm">
                    <el-input v-model="configForm.qpm" placeholder="请输入QPM（数字）" @input="handleQPMInput"></el-input>
                </el-form-item>
                <el-divider v-if="currentConfigChannel.name && currentConfigChannel.paramKey === 'wechat_official_account'" content-position="left">
                    {{ currentConfigChannel.name }} 额外配置
                </el-divider>
                <el-form-item v-if="currentConfigChannel.paramKey === 'wechat_official_account'" label="appId" prop="appId">
                    <el-input v-model="configForm.appId" placeholder="请输入appId"></el-input>
                </el-form-item>
                <el-form-item v-if="currentConfigChannel.paramKey === 'wechat_official_account'" label="Secret" prop="secret">
                    <el-input v-model="configForm.secret" placeholder="请输入Secret"></el-input>
                </el-form-item>
                <el-form-item v-if="currentConfigChannel.paramKey === 'wechat_official_account'" label="Token" prop="token">
                    <el-input v-model="configForm.token" placeholder="请输入Token"></el-input>
                </el-form-item>
                <el-form-item v-if="currentConfigChannel.paramKey === 'wechat_official_account'" label="AES Key" prop="aesKey">
                    <el-input v-model="configForm.aesKey" placeholder="请输入AES Key"></el-input>
                </el-form-item>

                <el-divider v-if="currentConfigChannel.name && ['web_version', 'web_embedded'].includes(currentConfigChannel.paramKey)" content-position="left">
                    {{ currentConfigChannel.name }} 额外配置
                </el-divider>

                <el-form-item label="身份验证" v-if="['web_version', 'web_embedded'].includes(currentConfigChannel.paramKey)" prop="authPassword">
                    <el-row style="width:100%">
                        <el-col :span="8">
                            <el-radio-group v-model="configForm.hasAuth">
                                <el-radio :label="true">验证</el-radio>
                                <el-radio :label="false">不验证</el-radio>
                            </el-radio-group>
                        </el-col>
                        <el-col :span="16" v-if="configForm.hasAuth">
                            <el-input v-model="configForm.authPassword" placeholder="请输入身份验证密码"></el-input>
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-divider v-if="currentConfigChannel.name && currentConfigChannel.paramKey === 'api_interface'" content-position="left">
                    {{ currentConfigChannel.name }} 额外配置
                </el-divider>
                <el-form-item v-if="currentConfigChannel.paramKey === 'api_interface'" label="APIKey">
                    <el-input v-model="configForm.apiKey" :placeholder="configForm.apiKey?configForm.apiKey:'apiKey会自动生成'" disabled></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button text bg size="large" @click="showConfigDialog = false">取消</el-button>
                <el-button type="primary" text bg size="large" @click="saveConfig">保存</el-button>
            </template>
        </el-dialog>

        <!-- 引入新组件 -->
        <ChannelDialog 
            v-model="showStartUsingDialog"
            :channel="currentStartUsingChannel"
            :shareId="shareId"
            @close="handleDialogClose"
        />

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

import ChannelDialog from './publishChannelDialog.vue';

import { getRole } from "@/api/smart/assistant/role";
import { updateChannelConfig , getChannels } from "@/api/smart/assistant/channelPublish";

const router = useRouter();
const { proxy } = getCurrentInstance();

const showStartUsingDialog = ref(false);
const currentStartUsingChannel = ref({});

const currentRole = ref({
    roleName: ''
});

const shareId = ref(null)
const currentRoleId = ref(null);

const showChatWindowConfig = ref(false);
const showConfigDialog = ref(false);
const currentConfigChannel = ref({}); // 初始化一个空对象
const configForm = ref({
    shareName: '',
    expireType: 0, // 默认无限
    expireTime: '',
    qpm: '100',
    appId: '',
    secret: '',
    token: '',
    paramKey: '' ,
    aesKey: '',
    hasAuth: false, // 默认不验证
    authPassword: '',
    apiKey: ''
});
const configFormRef = ref(null);

const formRules = {
    shareName: [
        { required: true, message: '分享名称不能为空', trigger: 'blur' }
    ],
    expireTime: [
        { required: false, message: '过期时间不能为空', trigger: 'blur' }
    ],
    qpm: [
        { required: true, message: 'QPM不能为空', trigger: 'blur' }
    ],
    appId: [
        { required: false, message: 'appId不能为空', trigger: 'blur' }
    ],
    secret: [
        { required: false, message: 'Secret不能为空', trigger: 'blur' }
    ],
    token: [
        { required: false, message: 'Token不能为空', trigger: 'blur' }
    ],
    aesKey: [
        { required: false, message: 'AES Key不能为空', trigger: 'blur' }
    ],
    authPassword: [
        { required: false, message: '身份验证密码不能为空', trigger: 'blur' }
    ],
    // apiKey: [
    //     { required: false, message: 'APIKey不能为空', trigger: 'blur' }
    // ]
};

const updateFormRules = () => {
    formRules.appId[0].required = currentConfigChannel.value.paramKey === 'wechat_official_account';
    formRules.secret[0].required = currentConfigChannel.value.paramKey === 'wechat_official_account';
    formRules.token[0].required = currentConfigChannel.value.paramKey === 'wechat_official_account';
    formRules.aesKey[0].required = currentConfigChannel.value.paramKey === 'wechat_official_account';
    formRules.authPassword[0].required = ['web_version', 'web_embedded'].includes(currentConfigChannel.value.paramKey) && configForm.value.hasAuth;
    formRules.expireTime[0].required = configForm.value.expireType === 4;
};

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
};

/** 返回 */
function goBack() {
    router.go(-1);
}

const startUsing = (channel) => {


  currentStartUsingChannel.value = {
    ...channel,
    apiKey: channel.paramKey === 'api_interface' ? 'sk_xxxxxx' : ''
  };
  showStartUsingDialog.value = true;
  shareId.value = channel.apiKey;

  console.log('shareId = ' + shareId.value)
};

const handleDialogClose = () => {
    // 可在此处添加关闭后的回调逻辑
};

/** 获取角色信息 */
function getRoleInfo() {
    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
        configForm.value.shareName = currentRole.value.roleName;
    });
}

/** 配置渠道 */
function configureChannel(channel) {
    if (channel.paramKey === 'chat_window') {
        showChatWindowConfig.value = true;
    } else {
        currentConfigChannel.value = channel;
        configForm.value = channel ;
        updateFormRules(); // 更新表单验证规则
        showConfigDialog.value = true;
    }
}

/** 保存配置 */
async function saveConfig() {
    configFormRef.value.validate((valid) => {
        if (valid) {
            const configData = {
                ...configForm.value,
                publishChannelId: currentConfigChannel.value.id ,
                paramKey: currentConfigChannel.value.paramKey,
                id: currentConfigChannel.value.id,
                roleId: currentRoleId.value
            };
            updateChannelConfig(configData)
              .then(() => {
                    currentConfigChannel.value.isConfigured = true;
                    showConfigDialog.value = false;
                    proxy.$modal.msgSuccess("配置保存成功");
                    handleGetChannels();
                })
              .catch(() => {
                    proxy.$modal.msgError("配置保存失败");
                });
        } else {
            proxy.$modal.msgError("请填写完整信息");
        }
    });
}

const handleQPMInput = (value) => {
    // 只允许输入数字
    configForm.value.qpm = value.replace(/[^0-9]/g, '');
};

const handleGetChannels = () => {
    getChannels(currentRoleId.value).then(response => {
        channelList.value = response.data;
    });
};

// 渠道列表，每个元素添加了 id 属性
const channelList = ref([]);

/** 初始化数据 */
onMounted(() => {
    console.log('onMounted');
    currentRoleId.value = router.currentRoute.value.query.roleId;

    getRoleInfo();
    handleGetChannels();
});
</script>

<style lang="scss" scoped>
.app-container.publish-channel-container{
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
            border: 1px solid #FBFBFC;
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
            background: #FBFBFC;
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