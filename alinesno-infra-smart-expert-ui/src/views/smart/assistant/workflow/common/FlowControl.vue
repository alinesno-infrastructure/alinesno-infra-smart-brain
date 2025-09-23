<template>
    <div>
        <el-card shadow="always" class="flow-control-panel">
            <!-- 缩小按钮 -->
            <el-button link @click="zoomOut">
                <el-tooltip class="box-item" effect="dark" content="缩小" placement="top">
                    <el-icon :size="18" title="缩小">
                        <ZoomOut />
                    </el-icon>
                </el-tooltip>
            </el-button>
            <!-- 放大按钮 -->
            <el-button link @click="zoomIn">
                <el-tooltip class="box-item" effect="dark" content="放大" placement="top">
                    <el-icon :size="18" title="放大">
                        <ZoomIn />
                    </el-icon>
                </el-tooltip>
            </el-button>
            <!-- 适应视图按钮 -->
            <el-button link @click="fitView">
                <el-tooltip class="box-item" effect="dark" content="适应" placement="top">
                    <el-icon :size="18" title="适应">
                        <Position />
                    </el-icon>
                </el-tooltip>
            </el-button>
            <el-divider direction="vertical" />
            <!-- 收起全部节点按钮 -->
            <el-button link @click="retract">
                <el-tooltip class="box-item" effect="dark" content="收起全部节点" placement="top">
                    <el-icon :size="18" title="收起全部节点">
                        <Download />
                    </el-icon>
                </el-tooltip>
            </el-button>
            <!-- 展开全部节点按钮 -->
            <el-button link @click="extend">
                <el-tooltip class="box-item" effect="dark" content="展开全部节点" placement="top">
                    <el-icon :size="18" title="展开全部节点">
                        <Upload />
                    </el-icon>
                </el-tooltip>
            </el-button>
            <!-- 一键美化按钮 -->
            <el-button link @click="layout">
                <el-tooltip class="box-item" effect="dark" content="一键美化" placement="top">
                    <el-icon :size="18" title="一键美化">
                        <Star />
                    </el-icon>
                </el-tooltip>
            </el-button>
            <el-divider direction="vertical" />
            <!-- 添加组件按钮 -->
            <el-button type="primary" bg @click="addComponent">
                <el-tooltip class="box-item" effect="dark" content="添加组件" placement="top">
                    <el-icon :size="16" title="添加组件">
                        <Plus />
                    </el-icon>
                </el-tooltip>
                添加组件
            </el-button>
        </el-card>

        <el-card shadow="always" class="flow-control-panel">
            <!-- 角色配置按钮 -->
            <el-button type="primary" text @click="roleConfiguration">
                <el-tooltip class="box-item" effect="dark" content="角色配置" placement="top">
                    <el-avatar :size="30" :src="imagePath(currentRole.roleAvatar)" />
                </el-tooltip>
                &nbsp;角色配置
            </el-button>
            <!-- 保存按钮 -->
            <el-button type="primary" text bg @click="save">
                <el-tooltip class="box-item" effect="dark" content="保存" placement="top">
                    <el-icon :size="18" title="保存">
                        <CreditCard />
                    </el-icon>
                </el-tooltip>
                保存
            </el-button>
            <el-divider direction="vertical" />
            <!-- 试运行按钮 -->
            <el-button type="success" bg @click="trialRun">
                <el-tooltip class="box-item" effect="dark" content="试运行" placement="top">
                    <el-icon :size="18" title="试运行">
                        <CaretRight />
                    </el-icon>
                </el-tooltip>
                试运行
            </el-button>
        </el-card>

        <!-- 添加组件 -->
        <el-collapse-transition>
            <NodeComponents v-model:show="showComponent" @clickNode="addNode" ref="workflowRef" />
        </el-collapse-transition>

        <!-- 试运行窗口 -->
        <div class="aip-flow-drawer">
            <el-drawer 
                v-model="showDebugRunDialog" 
                :modal="false" 
                size="40%" 
                style="max-width: 700px;position: absolute;" 
                title="预览与调试"
                :with-header="true">
                <div style="margin-top: 0px;">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </div>
            </el-drawer>
        </div>

        <!-- 参数配置窗口 -->
        <div class="aip-flow-drawer">
            <el-drawer 
                v-model="showParamsDialog" 
                v-if="showParamsDialog" 
                :modal="false" 
                size="40%" 
                style="position: absolute; max-width: 600px;" 
                title="角色参数配置"
                :with-header="true">
                <div style="margin-top: 0px;padding:0px !important" class="agent-chat-box  agent-inference-container">
                    <!-- <ParamsConfigPanel ref="paramsConfigRef" :diffHeight="295" /> -->
                    <ParamsConfigPanel ref="paramsConfigRef" @submitModelConfig="submitModelConfig" :diffHeight="295" />
                </div> 
                <div class="config-form-footer">
                    <div class="button-group">
                        <el-button :loading="loading" type="primary" size="large" text bg @click="submitModelConfig">保存配置</el-button>
                    </div>
                </div>
            </el-drawer>
        </div>

    </div>
</template>

<script setup>


import {
    processAndSave
} from "@/api/smart/assistant/flow";

import {
    getRole , 
    saveRoleWithWorkflowConfig
} from "@/api/smart/assistant/role";

const { proxy } = getCurrentInstance();

import NodeComponents from '@/views/smart/assistant/workflow/components/NodeComponents.vue'
import RoleChatPanel from '@/views/smart/assistant/role/chat/index';
// import ParamsConfigPanel from '@/views/smart/assistant/workflow/components/ParamsConfigPanel.vue';
import ParamsConfigPanel from '@/views/smart/assistant/role/ParamsConfigPanel.vue';

import { ElMessage, ElLoading } from "element-plus";
import { onMounted, onUnmounted, ref } from "vue";

const emits = defineEmits(['clickNode' , 'saveFlow'])

let escKeydownListener = null;

const router = useRouter();
const currentRoleId = ref(null)

const loading = ref(false)

const showComponent = ref(false);
const showParamsDialog = ref(false);
const showDebugRunDialog = ref(false);
// const emits = defineEmits(['getWorkflowGraphData'])

const currentRole = ref({
    roleName: ''
});
const paramsConfigRef = ref(null);
const roleChatPanelRef = ref(null)
const workflowRef = ref(null);

/**
 * 定义组件的 props
 * @property {Object|String|null} lf - 用于操作图形的对象，默认为 null
 */
const props = defineProps({
    lf: {
        type: [Object, String, null],
        default: null
    }
})

/**
 * 放大视图的方法
 * 如果 props.lf 存在，则调用其 zoom 方法进行放大操作
 */
function zoomIn() {
    if (props.lf) {
        props.lf.zoom(true, [0, 0]);
    }
}

/**
 * 缩小视图的方法
 * 如果 props.lf 存在，则调用其 zoom 方法进行缩小操作
 */
function zoomOut() {
    if (props.lf) {
        props.lf.zoom(false, [0, 0]);
    }
}

/**
 * 适应视图的方法
 * 如果 props.lf 存在，则依次调用 resetZoom、resetTranslate 和 fitView 方法来适应视图
 */
function fitView() {
    if (props.lf) {
        props.lf.resetZoom();
        props.lf.resetTranslate();
        props.lf.fitView();
    }
}

/**
 * 一键美化布局的方法
 * 如果 props.lf 存在，则调用其 extension.dagre.layout 方法进行布局美化
 */
const layout = () => {
    if (props.lf) {
        props.lf.extension.dagre.layout();
    }
}

/**
 * 收起全部节点的方法
 * 如果 props.lf 存在，则遍历所有节点，将其 showNode 属性设置为 false
 */
const retract = () => {
    if (props.lf) {
        props.lf.graphModel.nodes.forEach((element) => {
            element.properties.showNode = false;
        });
    }
}

/**
 * 展开全部节点的方法
 * 如果 props.lf 存在，则遍历所有节点，将其 showNode 属性设置为 true
 */
const extend = () => {
    if (props.lf) {
        props.lf.graphModel.nodes.forEach((element) => {
            element.properties.showNode = true;
        });
    }
}

/**
 * 添加组件的方法
 * 目前仅打印添加组件操作的日志，可根据实际需求添加具体逻辑
 */
const addComponent = () => {
    console.log('添加组件操作');
    showComponent.value = !showComponent.value;
}

/**添加节点信息 */
const addNode = (item) => {
    console.log('clickNode item = ' + item)
    showComponent.value = !showComponent.value;
    emits('clickNode', item)
}

/**
 * 角色配置方法
 * 目前仅打印角色配置操作的日志，可根据实际需求添加具体逻辑
 */
const roleConfiguration = () => {
    console.log('角色配置操作');
    showParamsDialog.value = !showParamsDialog.value;
};

/**
 * 保存方法
 * 目前仅打印保存操作的日志，可根据实际需求添加具体逻辑
 */
const save = () => {
    console.log('保存操作');
    const workflowData = props.lf.getGraphData();

    console.log('workflowData = ' + JSON.stringify(workflowData))

    const loading = ElLoading.service({
        lock: true,
        text: '保存中',
        background: 'rgba(0, 0, 0, 0.7)'
    });

    processAndSave(workflowData, currentRoleId.value).then(response => {
        ElMessage.success('保存成功');
        loading.close();

        emits('saveFlow', response.data)
    }).catch(error => {
        loading.close();
    })
};

/**
 * 试运行方法
 * 目前仅打印试运行操作的日志，可根据实际需求添加具体逻辑
 */
const trialRun = () => {
    console.log('试运行操作');
    showDebugRunDialog.value = true;
};

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId;

    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
        // roleChatPanelRef.value.setRoleInfo(currentRole.value)
        // displayRoleInfoBack(currentRole.value);
    });

}

/** 提交脚本任务 */
const submitModelConfig = async() => {

    console.log('submitModelConfig');

    const { valid, formData } = await paramsConfigRef.value.validateForm();
    if (!valid) {
        console.log('formData = ' + JSON.stringify(formData));
        return 
    }

    // const type = scriptType.value && scriptType.value !== 'params'?scriptType.value:'execute' ;
    // console.log('type = ' + type);

    // const scriptCode = getCode()[type];
    // const roleId = currentRoleId.value;

    loading.value = true

    const agentConfigParams = paramsConfigRef.value.getAgentConfigParams();
    console.log('agentConfigParmas = ' + JSON.stringify(agentConfigParams));

    saveRoleWithWorkflowConfig(agentConfigParams).then(res => {
        console.log('res = ' + res);
        loading.value = false
        proxy.$modal.msgSuccess("更新成功");
        // getRoleInfo();
    }).catch(err => {
        console.log('err = ' + err);
        loading.value = false
    })

}

onMounted(() => {
    // console.log('props.lf = ' + JSON.stringify(props.lf))
    currentRoleId.value = router.currentRoute.value.query.roleId;
    getRoleInfo();

    escKeydownListener = (e) => {
        if (e.key === "Escape" && showComponent.value) {
        showComponent.value = false; // Close the panel when ESC is pressed
        }
    };
    document.addEventListener("keydown", escKeydownListener);
})

onUnmounted(() => {
  if (escKeydownListener) {
    document.removeEventListener("keydown", escKeydownListener);
  }
});

</script>

<style lang="scss" scoped>

.config-form-footer{
    margin-top: 20px;
    text-align: right;
}
</style>

<style>
.flow-control-panel .el-card__body {
    padding: 13px !important
}

.aip-flow-drawer .el-drawer.ltr,
.aip-flow-drawer .el-drawer.rtl {
    height: 93%;
    bottom: 10px;
    top: auto;
    right: 10px;
    border-radius: 8px;
}

.aip-flow-drawer .el-drawer__header {
    margin-bottom: 0px;
}
</style>