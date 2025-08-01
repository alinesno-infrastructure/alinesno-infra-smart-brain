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
                        <!-- <span class="save-time">保存时间：2025-02-14 23:50:44</span> -->
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

                                <el-button v-if="!channel.isConfigured" type="info" size="large" text plain>未配置</el-button>

                                <el-popover :visible="channel.visible" placement="top" :width="180">
                                    <p>确定要下架么?</p>
                                    <div style="text-align: right; margin: 0">
                                        <el-button type="info" text bg @click="channel.visible = false">取消</el-button>
                                        <!-- 
                                        <el-button size="small" type="primary" @click="visible = false">
                                            confirm
                                        </el-button> 
                                        -->
                                        <el-button type="primary" bg @click="handleOfflineChannel(channel)" text plain>确认</el-button>
                                    </div>
                                    <template #reference>
                                        <el-button type="danger" size="large" text bg v-if="channel.isConfigured" @click="channel.visible = true">下架</el-button>
                                    </template>
                                </el-popover>

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
            <el-form :model="configForm" size="large" :rules="formRules" ref="configFormRef" label-width="120px" style="margin-top:30px;">
                <el-form-item label="分享名称" prop="shareName">
                    <el-input v-model="configForm.shareName" placeholder="请输入分享名称"></el-input>
                </el-form-item>

                <!-- 渠道为商店时，需要选择分类_start -->
                <el-form-item v-if="currentConfigChannel.paramKey === 'aip_agent_store'" label="发布商店分类" prop="agentStoreType">
                    <el-select
                        v-model="configForm.agentStoreType"
                        placeholder="发布商店分类"
                        style="width: 100%">
                        <el-option
                            v-for="item in currentConfigChannel.storeType"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"/>
                    </el-select>
                </el-form-item>
                <!-- 渠道为商店时，需要选择分类_end -->

                <!-- 渠道为场景时，需要选择分类_start -->
                <el-form-item v-if="currentConfigChannel.paramKey === 'aip_agent_scene'" label="发布场景类型" prop="sceneId">
                    <el-select
                        v-model="configForm.sceneId"
                        placeholder="请选择发布场景类型"
                        @change="handleChangeSceneType"
                        style="width: 100%">
                        <el-option
                            v-for="item in currentConfigChannel.sceneType"
                            :key="item.id"
                            :label="item.sceneName"
                            :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item v-if="currentConfigChannel.paramKey === 'aip_agent_scene'" label="指定Agent类型" prop="agentTypeId">
                    <el-select
                        v-model="configForm.agentTypeId"
                        placeholder="请选择指定的Agent类型"
                        @change="handleChangeAgentType"
                        style="width: 100%">
                        <el-option
                            v-for="item in currentConfigChannel.agentTypes"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="发布范围" prop="sceneScope" label-width="100px" v-if="currentConfigChannel.paramKey === 'aip_agent_scene'">
                    <el-radio-group v-model="configForm.sceneScope">
                        <el-radio v-for="item in sceneScopeList" 
                        style="margin-top: 10px;margin-bottom: 10px;" 
                        :key="item.value" 
                        :value="item.value"
                        :label="item.value" 
                        size="large">

                        <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                            <span style="font-size:15px;font-weight: bold;">
                            <i :class="item.icon"></i> {{ item.label }}
                            </span>
                            <span style="color:#a5a5a5">
                            {{  item.desc }}
                            </span>
                        </div>

                        </el-radio>
                    </el-radio-group>
                </el-form-item> 

                <el-form-item v-if="currentScreenAgent.needDataModel" label="数据处理模型" props="llmModelId">
                    <LLMSelector :modelType="'large_language_model'" v-model="configForm.llmModelId" />
                </el-form-item>

                <el-form-item v-if="currentScreenAgent.needImgModel" label="图片处理模型" props="imageModelId">
                    <LLMSelector :modelType="'image_generation'" v-model="configForm.imageModelId" />
                </el-form-item>

                <!-- 渠道为场景时，需要选择分类_end -->

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

                <el-form-item label="每分钟并发数" prop="qpm">
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

import {
  getSceneScope
} from "@/api/smart/assistant/scene";

import ChannelDialog from './publishChannelDialog.vue';
import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'

import { getRole , changeSaleField } from "@/api/smart/assistant/role";
import { updateChannelConfig , getChannels , offlineChannel } from "@/api/smart/assistant/channelPublish";

const router = useRouter();
const { proxy } = getCurrentInstance();

const showStartUsingDialog = ref(false);
const currentStartUsingChannel = ref({});
const sceneScopeList = ref([
    {
        "value": "org",
        "label": "组织场景",
        "icon": "fa-solid fa-truck-plane",
        "desc": "组织场景可以选择公共和组织内的智能体加入场景"
    },
    {
        "value": "public",
        "label": "公开场景",
        "icon": "fa-solid fa-globe",
        "desc": "公开场景只能选择公开的智能体加入场景"
    }
])

const currentRole = ref({
    roleName: ''
});

const visible = ref(false)
const shareId = ref(null)
const currentRoleId = ref(null);

const currentScreenAgent = ref(false);

const showChatWindowConfig = ref(false);
const showConfigDialog = ref(false);
const currentConfigChannel = ref({}); // 初始化一个空对象
const configForm = ref({
    shareName: '',
    expireType: 0, // 默认无限
    expireTime: '',
    qpm: '100',
    sceneScope: 'org',
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
        { required: true, message: '过期时间不能为空', trigger: 'blur' }
    ],
    agentStoreType: [
        { required: true, message: '发布商店类型', trigger: 'blur' }
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
    llmModelId: [
        { required: true, message: '场景模型不能为空', trigger: 'blur' }
    ],
    imageModelId: [
        { required: true, message: '场景图片模型不能为空', trigger: 'blur' }
    ],
    sceneId: [
        { required: true, message: '场景ID不能为空', trigger: 'blur' }
    ] , 
    sceneScope: [
        { required: true, message: '场景范围不能为空', trigger: 'blur' }
    ],
    agentTypeId: [
        { required: true, message: '场景Agent类型ID不能为空', trigger: 'blur' }
    ] 
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

/** 场景数据权限 */
// const handleGetSceneScope = () => {
//   getSceneScope().then(res => {
//     sceneScopeList.value = res.data ;
//   })
// }

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

        let tempAgentTypes = currentConfigChannel.value.agentTypes ; 

        currentConfigChannel.value = channel;

        if(tempAgentTypes && tempAgentTypes.length > 0){
            currentConfigChannel.value.agentTypes = tempAgentTypes ; 
        }

        configForm.value = channel ;

        // 设置默认值
        if(!configForm.value.shareName){
            configForm.value.shareName = currentRole.value.roleName;
        }

        if(!configForm.value.expireType){
            configForm.value.expireType = 0;
        }

        if(!configForm.value.qpm){
            configForm.value.qpm = '100';
        }

        if(!configForm.value.hasAuth){
            configForm.value.hasAuth = false;
        }

        // 发布configForm.agentStoreType类型，默认选择第1个选项
        if(!configForm.value.agentStoreType && currentConfigChannel.value.storeType){
            configForm.value.agentStoreType = currentConfigChannel.value.storeType[0].id;
        }

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
        } 
        // else {
        //     proxy.$modal.msgError("请填写完整信息");
        // }
    });
}

const handleQPMInput = (value) => {
    // 只允许输入数字
    configForm.value.qpm = value.replace(/[^0-9]/g, '');
};

const handleGetChannels = () => {
    getChannels(currentRoleId.value).then(response => {

        channelList.value = response.data;

        if(response.data){
            for(let i = 0; i < response.data.length; i++){
                let item = response.data[i];
                if(item.paramKey === 'aip_agent_scene'){
                    // console.log('item = ' + JSON.stringify(item));
                    // handleChangeSceneType(item.sceneId) ; 
                    // currentConfigChannel.value.agentTypes = selectedItem.agents;

                    const selectedId = item.sceneId;
                    console.log('selectedId = ' + selectedId);

                    const selectedItem = item.sceneType.find(
                        (item) => item.id === selectedId
                    );
                    if (selectedItem) {
                        console.log('selectedItem = ' + JSON.stringify(selectedItem.agents));
                        currentConfigChannel.value.agentTypes = selectedItem.agents;

                        console.log('currentConfigChannel.agentTypes = ' + currentConfigChannel.value.agentTypes)
                    }
                }
            }
        }

    });
};

// 场景类型切换
const handleChangeSceneType = (selectedId) => {
    console.log('selectedId = ' + JSON.stringify(selectedId));
    // currentConfigChannel.agentTypes = item.agents;

    const selectedItem = currentConfigChannel.value.sceneType.find(
        (item) => item.id === selectedId
    );

    if (selectedItem) {
        console.log('item = ' + JSON.stringify(selectedItem));
        currentConfigChannel.value.agentTypes = selectedItem.agents;
        currentConfigChannel.value.agentTypeId = null ; 
    }

};

const handleChangeAgentType = (selectedId) => {
    console.log('selectedId = ' + JSON.stringify(selectedId));
    // 从currentConfigChannel.value.agentTypes查询到当前的currentScreenAgent

    currentScreenAgent.value = currentConfigChannel.value.agentTypes.find(
        (item) => item.id === selectedId
    );
    
};

// 渠道下线
const handleOfflineChannel =(channel) => {
    offlineChannel(channel.id).then(response => {
        proxy.$modal.msgSuccess("下线成功");
        handleGetChannels();
    });
}

/** 修改状态 */
const handleChangeSaleField = async (field, value, id) => {
  // 判断tags值 这样就不会进页面时调用了
  const res = await changeSaleField({
    field: field,
    value: value ? 1 : 0,
    id: id
  }).catch(() => { })
  if (res && res.code == 200) {
    // 刷新表格
    getList()
  }
}

// 渠道列表，每个元素添加了 id 属性
const channelList = ref([]);

/** 初始化数据 */
onMounted(() => {
    console.log('onMounted');
    currentRoleId.value = router.currentRoute.value.query.roleId;

    getRoleInfo();
    handleGetChannels();
    // handleGetSceneScope();
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
                    color: #1d75b0;

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