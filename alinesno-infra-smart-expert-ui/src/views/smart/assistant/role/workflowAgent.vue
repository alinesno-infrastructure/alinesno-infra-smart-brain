<template>
  <div class="workflow-container">
    <div class="page-header-container">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="mr-3"> 
              <img :src="imagePath(currentRole.roleAvatar)" style="width:25px;height:25px;border-radius:5px;" /> {{ ''+currentRole.roleName+' 配置任务编排' }} 
            </span>
            <span style="color: #aaaaaa;font-size: 14px;">保存时间：2025-02-14 23:50:44</span>
          </div>
        </template>
      </el-page-header>

      <div class="page-header-btn-container">
        <el-button type="primary" text bg @click="addComponent"><i class="fa-solid fa-feather"></i> 添加组件</el-button>
        <el-button type="primary" text bg @click="saveWorkflow"><i class="fa-solid fa-floppy-disk"></i> 保存</el-button>
        <el-button type="primary" text bg @click="debugRun()"><i class="fa-solid fa-ferry"></i> 试运行</el-button>
        <el-button type="primary" text bg @click="deployWorkflow"><i class="fa-brands fa-wordpress"></i> 发布</el-button>
      </div>
    </div>

    <!-- 添加组件 -->
    <el-collapse-transition>
      <NodeComponents 
        v-model:show="showComponent" 
        :id="someId" 
        @clickNode="addNode"
        @onmousedown="onmousedown"
        :workflowRef="workflowRef" /> 
    </el-collapse-transition>

    <!-- 工作流画板 -->
    <el-row>
      <el-col :span="24" v-loading="loading" element-loading-text="任务正在生成中，请勿刷新 ..." :element-loading-spinner="svg">
        <div class="workflow-main-panel" id="workflowMainPanelId">
            <flowPanel ref="workflowRef" />
        </div>
      </el-col>
    </el-row>

    <!-- 试运行窗口 -->
    <el-drawer v-model="showDebugRunDialog" 
      :modal="true" 
      size="40%" 
      title="预览与调试" 
      :with-header="true">

      <div style="margin-top: 0px;">
        <RoleChatPanel ref="roleChatPanelRef" />
      </div>

    </el-drawer>

  </div>
</template>

<script setup name="createProcessDefinition">

import {
  getRole
} from "@/api/smart/assistant/role";

import flowPanel from '@/views/smart/assistant/workflow/flowPanel'
import NodeComponents from '@/views/smart/assistant/workflow/components/NodeComponents.vue'

import RoleChatPanel from '@/views/smart/assistant/role/chat/index';

const router = useRouter();

const showComponent = ref(false);
const showDebugRunDialog = ref(false);

const workflowMainPanelRef = ref(null)
const roleChatPanelRef = ref(null)

const someId = ref('your-id');
const workflowRef = ref(null);

const currentRole = ref({
    roleName: ''
});

const currentRoleId = ref(null)

/** 返回 */
function goBack() {
  router.push({ path: '/expert/smart/assistant/role/index' });
}

/** 添加组件 */
function addComponent(){
  showComponent.value = !showComponent.value ;
}

function debugRun(){
  showDebugRunDialog.value = true ;
  console.log('debugRun')
}

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId; 
    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
    });
}

/** 保存工作流配置 */
const saveWorkflow = () => {
  const workflowData = workflowRef.value?.getWorkflowGraphData()
  console.log('workflowData = ' + JSON.stringify(workflowData))
}

/** 初始化数据 */
onMounted(() => {
  console.log('onMounted');
  getRoleInfo();
})

</script>

<!-- 样式 -->
<style lang="scss" scoped>

.workflow-container {
  margin:0px;
  padding:0px;

  .page-header-container {
    padding : 10px;
    border-bottom: 1px solid rgb(229, 229, 229);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .page-header-btn-container {
    float: right;
    margin-right: 10px;
  }

  .mr-3{
    font-size: 15px;
    font-weight: normal;
  }

}

</style>