<template>
  <div class="workflow-container">
    <div class="page-header-container">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="mr-3"> 
              <img :src="imagePath(currentRole.roleAvatar)" style="width:25px;height:25px;border-radius:5px;" /> {{ ''+currentRole.roleName+' 配置任务编排' }} 
            </span>
            <div style="display: flex;gap:10px;align-items: center;" v-if="currentFlow">
              <el-tag effect="danger" v-if="currentFlow?.publishStatus == 'unpublished'">
                未发布
              </el-tag>
              <el-tag effect="primary" v-if="currentFlow?.publishStatus == 'published'">
                已发布
              </el-tag>
              <span style="color: #aaaaaa;font-size: 14px;">最后更新 {{ currentFlow?.updateTime }}</span>
            </div>
          </div>
        </template>
      </el-page-header>

      <div class="page-header-btn-container">
        <!-- <el-button type="primary" text bg size="large" @click="addComponent"><i class="fa-solid fa-feather"></i> 添加组件</el-button> -->
        <!-- <el-button type="primary" text bg size="large" @click="saveWorkflow"><i class="fa-solid fa-floppy-disk"></i> 保存</el-button> -->
        <!-- <el-button type="primary" text bg size="large" @click="debugRun()"><i class="fa-solid fa-ferry"></i> 试运行</el-button> -->
        <el-button type="primary" size="large" @click="handlePublishedFlow">
          <i class="fa-solid fa-paper-plane"></i> &nbsp; 发布
        </el-button>
      </div>
    </div>

    <!-- 添加组件 -->
    <!-- <el-collapse-transition>
      <NodeComponents 
        v-model:show="showComponent" 
        :id="someId" 
        @clickNode="addNode"
        @onmousedown="onmousedown"
        :workflowRef="workflowRef" /> 
    </el-collapse-transition> -->

    <!-- 工作流画板 -->
    <el-row>
      <el-col :span="24" v-loading="loading" element-loading-text="任务正在生成中，请勿刷新 ..." :element-loading-spinner="svg">
        <div class="workflow-main-panel" id="workflowMainPanelId">
            <flowPanel ref="workflowRef" />
        </div>
      </el-col>
    </el-row>

    <!-- 试运行窗口 -->
    <!-- <el-drawer v-model="showDebugRunDialog" 
      :modal="true" 
      size="40%" 
      title="预览与调试" 
      :with-header="true">

      <div style="margin-top: 0px;">
        <RoleChatPanel ref="roleChatPanelRef" />
      </div>

    </el-drawer> -->

  </div>
</template>

<script setup name="createProcessDefinition">

import {
  getRole
} from "@/api/smart/assistant/role";

import {
 processAndSave,
 getLatestFlow , 
 publishedFlow,
} from "@/api/smart/assistant/flow";

import flowPanel from '@/views/smart/assistant/workflow/flowPanel'

// import NodeComponents from '@/views/smart/assistant/workflow/components/NodeComponents.vue'
// import RoleChatPanel from '@/views/smart/assistant/role/chat/index';

import { ElMessage , ElLoading } from "element-plus";

const router = useRouter();

const showComponent = ref(false);
const showDebugRunDialog = ref(false);

// const workflowMainPanelRef = ref(null)
// const roleChatPanelRef = ref(null)
// const someId = ref('your-id');

const workflowRef = ref(null);
const currentFlow = ref(null);
const currentRole = ref({
    roleName: ''
});

const currentRoleId = ref(null)

/** 返回 */
function goBack() {
  router.push({ path: '/expert/smart/assistant/role/index' });
}

// /** 添加组件 */
// function addComponent(){
//   showComponent.value = !showComponent.value ;
// }

// function debugRun(){
//   showDebugRunDialog.value = true ;
//   console.log('debugRun')
// }

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId; 
    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
    });

    // getLatestFlow(currentRoleId.value).then(response => {
    //     if(response.data){
    //         workflowRef.value?.setWorkflowGraphData(response.data?.flowGraphJson);
    //         currentFlow.value = response.data;
    //     }
    // });
    handleGetLastFlow();
}

const handleGetLastFlow = () => {
  getLatestFlow(currentRoleId.value).then(response => {
    if(response.data){
      workflowRef.value?.setWorkflowGraphData(response.data?.flowGraphJson);
      currentFlow.value = response.data;
    }
  });
}

/** 发布当前节点 */
const handlePublishedFlow = () => {
  publishedFlow(currentFlow.value.id).then(response => {
    ElMessage.success('发布成功');
    handleGetLastFlow();
  })
}

/** 保存工作流配置 */
// const saveWorkflow = () => {
//   const workflowData = workflowRef.value?.getWorkflowGraphData()
//   console.log('workflowData = ' + JSON.stringify(workflowData))

//   const loading = ElLoading.service({
//     lock: true,
//     text: '保存中',
//     background: 'rgba(0, 0, 0, 0.7)'
//   });

//   processAndSave(workflowData , currentRoleId.value).then(response => {
//     ElMessage.success('保存成功');
//     loading.close();
//   }).catch(error => {
//     loading.close();
//   })

// }

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