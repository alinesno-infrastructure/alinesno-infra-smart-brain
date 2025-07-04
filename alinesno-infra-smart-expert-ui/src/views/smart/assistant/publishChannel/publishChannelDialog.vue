<template>
    <el-dialog v-model="showStartUsing" :title="props.channel.name ? `${props.channel.name} 使用指南` : '使用指南'"
        :width="800 + 'px'">
        <div class="dialog-content">
            
            <div v-if="props.channel.paramKey === 'aip_agent_store'">
                <div class="instruction">
                    <div class="instruction show-copy-btn">
                        <span>验证方式，将以下网页复制到你的浏览器中</span>
                        <span>
                            <el-button type="primary" size="large" text bg icon="CopyDocument" @click="copyClick(getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.showHistory )">复制</el-button>
                            <el-button type="success" size="large" text bg icon="Link" @click="openLink(getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.showHistory )">打开</el-button>
                        </span>
                    </div>
                    <div class="code-block">
                       {{ getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.showHistory  }} 
                    </div>
                    <div style="margin-top:20px;">智能体商店打开方式（<a href="http://alinesno-infra-smart-im-ui.beta.base.infra.linesno.com/agentList" target="_blank">打开商店</a>）</div>
                    <div style="margin-top:20px;">
                        <img :src="getOutlineSvg('aip_store_search')" style="width: 100%;border-radius: 5px;" />
                    </div>
                </div>
            </div>

            <div v-if="props.channel.paramKey === 'web_version'">
                <div class="instruction">
                    <div class="instruction show-copy-btn">
                        <span>将以下网页复制到你的浏览器中：</span>
                        <span>
                            <el-button type="primary" size="large" text bg icon="CopyDocument" @click="copyClick(getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.showHistory )">复制</el-button>
                            <el-button type="success" size="large" text bg icon="Link" @click="openLink(getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.showHistory )">打开</el-button>
                        </span>
                    </div>
                    <div class="code-block">
                       {{ getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.showHistory  }} 
                    </div>
                </div>
            </div>

            <div v-if="props.channel.paramKey === 'web_embedded'">
                <div style="margin-top:0px;padding:10px;">
                    <el-space wrap>
                        <span class="config-item-img" :class="selectedType === 'iframe'?'selected':''" @click="handleImageClick('iframe')">
                            <img :src="getOutlineSvg('iframe')" alt="" />
                        </span>
                        <span class="config-item-img" :class="selectedType === 'script'?'selected':''" @click="handleImageClick('script')">
                            <img :src="getOutlineSvg('script')" alt="" />
                        </span>
                    </el-space>
                </div>
                <div style="margin-top:0px;padding:10px;">
                    <el-row>
                        <el-col :span="7" class="config-item">
                            <span>
                                显示历史
                            </span>
                            <el-switch v-model="config.showHistory" class="ml-2" inline-prompt  @change="switchChange"  />
                        </el-col>
                        <el-col :span="7" class="config-item" v-if="selectedType === 'script'">
                            <span>
                                图标可拖动
                            </span>
                            <el-switch v-model="config.iconDraggable" @change="switchChange" class="ml-2" inline-prompt />
                        </el-col>
                        <el-col :span="7" class="config-item" v-if="selectedType === 'script'">
                            <span>
                                默认打开
                            </span>
                            <el-switch v-model="config.defaultOpen" @change="switchChange" class="ml-2" inline-prompt />
                        </el-col>
                        <el-col :span="7" class="config-item" v-if="selectedType === 'script'">
                            <span>
                                打开图标
                            </span>
                            <el-switch v-model="config.showOpenIcon" class="ml-2" inline-prompt />
                        </el-col>
                        <el-col :span="7" class="config-item" v-if="selectedType === 'script'">
                            <span>
                                关闭图标
                            </span>
                            <el-switch v-model="config.showCloseIcon" class="ml-2" inline-prompt />
                        </el-col>
                    </el-row>
                </div>
                <div style="margin-top:0px;padding:10px;">
                    <div class="instruction show-copy-btn">
                        <span>复制下面代码嵌入到您的网站中：</span>
                        <el-button type="primary" size="large" text bg icon="CopyDocument" @click="copyClick(codeEditorRef.getRawScript())">复制</el-button>
                    </div>
                    <div>
                        <ScriptEditorPanel ref="codeEditorRef" />
                    </div>
                </div>
            </div>
            <div v-if="props.channel.paramKey === 'api_interface'">
                <div class="instruction">
                    <span>您的API访问密钥：</span>
                </div>
                <div class="code-block">
                    <el-input v-model="props.channel.apiKey" disabled placeholder="已自动生成" />
                </div>
                <div class="instruction mt-20">
                    <span>查看文档：</span>
                    <a href="https://api-docs.example.com" target="_blank">API文档</a>
                </div>
            </div>
            <div v-if="props.channel.paramKey === 'wechat_official_account'">
                <div class="instruction show-copy-btn">
                    <span>将以下网页复制到你的浏览器中：</span>
                    <span>
                        <el-button type="primary" size="large" text bg icon="CopyDocument" @click="copyClick('http://alinesno-infra-smart-expert-boot.beta.base.infra.linesno.com/api/infra/smart/assistant/publishWeChat/' + props.shareId)">复制</el-button>
                    </span>
                </div>
                <div class="code-block">
                    {{ 'http://alinesno-infra-smart-expert-boot.beta.base.infra.linesno.com/api/infra/smart/assistant/publishWeChat/' + props.shareId }} 
                </div>
                <div style="margin-top:20px;">
                    <img :src="getOutlineSvg('offiaccount-copylink')" style="width: 100%;border-radius: 5px;" />
                </div>
            </div>
        </div>
        <template #footer>
            <el-button text bg size="large" @click="handleClose">关闭</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import ScriptEditorPanel from './ScriptEditor.vue';
import { getOutlineSvg } from '@/utils/llmIcons'
import { copyClick } from '@/utils/clipboard'

const showStartUsing = ref(false);

const props = defineProps({
    show: {
        type: Boolean,
        default: false
    },
    shareId: {
        type: String,
        required: true
    },
    channel: {
        type: Object,
        required: true
    }
});

const emit = defineEmits(['update:show', 'close']);

const config = ref({
    showHistory: false,
    iconDraggable: false,
    defaultOpen: false,
    showOpenIcon: false,
    showCloseIcon: false
});

const codeEditorRef = ref(null);
const selectedType = ref('iframe');

const handleClose = () => {
    emit('update:show', false);
    // emit('close');
    showStartUsing.value = false;
};

// 引入getRootPath方法
const getRootPath = (url = window.location.href) => {
    try {
        const urlObj = new URL(url);
        return `${urlObj.protocol}//${urlObj.host}`;
    } catch (error) {
        console.error('Invalid URL:', error);
        return null;
    }
};

const openLink = (link) => {
    window.open(link);
}

const handleImageClick = (type) => {

    console.log('props = ' + props.shareId)
    selectedType.value = type;

    if (type === 'iframe') {

        const webEmbeddedIframeCodeString = '<iframe \n' +
            ' src="'+getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.value.showHistory+ '" \n' +
            ' style="width: 100%; height: 100%;" \n' +
            ' frameborder="0" \n' +
            ' allow="*" \n' +
            '></iframe>\n';

        codeEditorRef.value.setRawScript(webEmbeddedIframeCodeString);
    } else if (type === 'script') {

        const empty = "" ; 
        let webEmbeddedScriptCodeString = '<script \n' +
                ' type="text/javascript" \n' +
                ' src="http://data.linesno.com/aip-channel/aip-iframe.js" \n' +
                ' id="chatbot-iframe" \n' +
                ' data-bot-src="'+getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.value.showHistory+ '" \n' +
                ' data-default-open="' + config.value.defaultOpen + '" \n' +
                ' data-drag="' + config.value.iconDraggable + '" \n' +
                ' data-open-icon="data:image/svg+xml;base64,PHN2ZyB0PSIxNjkwNTMyNzg1NjY0IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjQxMzIiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj48cGF0aCBkPSJNNTEyIDMyQzI0Ny4wNCAzMiAzMiAyMjQgMzIgNDY0QTQxMC4yNCA0MTAuMjQgMCAwIDAgMTcyLjQ4IDc2OEwxNjAgOTY1LjEyYTI1LjI4IDI1LjI4IDAgMCAwIDM5LjA0IDIyLjRsMTY4LTExMkE1MjguNjQgNTI4LjY0IDAgMCAwIDUxMiA4OTZjMjY0Ljk2IDAgNDgwLTE5MiA0ODAtNDMyUzc3Ni45NiAzMiA1MTIgMzJ6IG0yNDQuOCA0MTZsLTM2MS42IDMwMS43NmExMi40OCAxMi40OCAwIDAgMS0xOS44NC0xMi40OGw1OS4yLTIzMy45MmgtMTYwYTEyLjQ4IDEyLjQ4IDAgMCAxLTcuMzYtMjMuMzZsMzYxLjYtMzAxLjc2YTEyLjQ4IDEyLjQ4IDAgMCAxIDE5Ljg0IDEyLjQ4bC01OS4yIDIzMy45MmgxNjBhMTIuNDggMTIuNDggMCAwIDEgOCAyMi4wOHoiIGZpbGw9IiM0ZTgzZmQiIHAtaWQ9IjQxMzMiPjwvcGF0aD48L3N2Zz4=" \n' +
                ' data-close-icon="data:image/svg+xml;base64,PHN2ZyB0PSIxNjkwNTM1NDQxNTI2IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjYzNjciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj48cGF0aCBkPSJNNTEyIDEwMjRBNTEyIDUxMiAwIDEgMSA1MTIgMGE1MTIgNTEyIDAgMCAxIDAgMTAyNHpNMzA1Ljk1NjU3MSAzNzAuMzk1NDI5TDQ0Ny40ODggNTEyIDMwNS45NTY1NzEgNjUzLjYwNDU3MWE0NS41NjggNDUuNTY4IDAgMSAwIDY0LjQzODg1OCA2NC40Mzg4NThMNTEyIDU3Ni41MTJsMTQxLjYwNDU3MSAxNDEuNTMxNDI5YTQ1LjU2OCA0NS41NjggMCAwIDAgNjQuNDM4ODU4LTY0LjQzODg1OEw1NzYuNTEyIDUxMmwxNDEuNTMxNDI5LTE0MS42MDQ1NzFhNDUuNTY4IDQ1LjU2OCAwIDEgMC02NC40Mzg4NTgtNjQuNDM4ODU4TDUxMiA0NDcuNDg4IDM3MC4zOTU0MjkgMzA1Ljk1NjU3MWE0NS41NjggNDUuNTY4IDAgMCAwLTY0LjQzODg1OCA2NC40Mzg4NTh6IiBmaWxsPSIjNGU4M2ZkIiBwLWlkPSI2MzY4Ij48L3BhdGg+PC9zdmc+" \n' +
                ' defer \n' +
            '></'+empty+'script>';

        codeEditorRef.value.setRawScript(webEmbeddedScriptCodeString);
    }else if(type === 'web'){
        let webEmbeddedScriptCodeString = getRootPath()+'/chat/publish?shareId=' + props.shareId + '&showHistory=' +config.value.showHistory ;
        codeEditorRef.value.setRawScript(webEmbeddedScriptCodeString);
    }
};

const switchChange = (val) => {
    handleImageClick(selectedType.value) ;
};

// onMounted(() => {
//     // 默认选中iframe
// });
nextTick(() => {

    // setTimeout(() => {
    //     handleImageClick('iframe') ;
    // }, 1000)

    // if(props.channel.paramKey == 'web_versoin'){
    //     handleImageClick('web') ;
    // }

});
</script>

<style lang="scss" scoped>
.dialog-content {
    display: flex;
    margin-bottom: 20px;
    flex-direction: column;
    border-radius: 3px;

    .instruction {
        margin-top: 0px;
        margin-bottom: 20px;
        font-size: 14px;
        font-weight: bold;
    }

    .show-copy-btn{
        display: flex ; 
        align-items: center;
        justify-content: space-between;
    }

    .config-item {
        background: #F4F6F8;
        padding: 5px 15px;
        gap: 30px;
        display: flex;
        margin: 5px;
        align-items: center;
        border-radius: 5px;
        justify-content: space-between;
    }

    .config-item-img {
        cursor: pointer;
        border-radius: 9px;
        border: 1px solid rgb(232, 235, 240);

        &:hover {
            border: 1px solid #409EFF;
        }
    }

    .selected{
        border: 1px solid #409EFF;
    }

    .code-block {
        white-space: pre-wrap;
        font-weight: normal;
        background: #f8f9fa;
        padding: 15px;
        line-height: 23px;
        border-radius: 4px;
        font-size: 14px;

        pre {
            margin: 0;
            word-wrap: break-word;
        }
    }

    .mt-20 {
        margin-top: 20px;
    }

    ul {
        list-style: disc;
        padding-left: 20px;
        margin-top: 10px;
    }
}
</style>