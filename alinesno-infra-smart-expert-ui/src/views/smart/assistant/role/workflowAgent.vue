<template>
  <div class="workflow-container">
    <div class="page-header-container">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="mr-3">
              <img :src="imagePath(currentRole.roleAvatar)" style="width:25px;height:25px;border-radius:5px;" /> {{
        '' + currentRole.roleName + ' 配置任务编排' }}
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
        <el-button type="primary" size="large" @click="handlePublishedFlow">
          <i class="fa-solid fa-location-arrow"></i>&nbsp; 发布
        </el-button>
      </div>
    </div>

    <!-- 工作流画板 -->
    <el-row>
      <el-col :span="24" v-loading="loading" element-loading-text="任务正在生成中，请勿刷新 ..." :element-loading-spinner="svg">
        <div class="workflow-main-panel" id="workflowMainPanelId">
          <flowPanel ref="workflowRef" />
        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script setup name="createProcessDefinition">

import {
  getRole
} from "@/api/smart/assistant/role";

import {
  getLatestFlow,
  publishedFlow,
} from "@/api/smart/assistant/flow";

import flowPanel from '@/views/smart/assistant/workflow/flowPanel'
import { ElMessage } from "element-plus";

const router = useRouter();

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

/** 获取角色信息 */
function getRoleInfo() {
  currentRoleId.value = router.currentRoute.value.query.roleId;
  getRole(currentRoleId.value).then(response => {
    currentRole.value = response.data;
  });

  handleGetLastFlow();
}

const handleGetLastFlow = () => {
  getLatestFlow(currentRoleId.value).then(response => {
    if (response.data) {
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

/** 初始化数据 */
onMounted(() => {
  console.log('onMounted');
  getRoleInfo();
})

</script>

<!-- 样式 -->
<style lang="scss" scoped>
.workflow-container {
  margin: 0px;
  padding: 0px;

  .page-header-container {
    padding: 10px;
    border-bottom: 1px solid rgb(229, 229, 229);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .page-header-btn-container {
    float: right;
    margin-right: 10px;
  }

  .mr-3 {
    font-size: 15px;
    font-weight: normal;
  }

}
</style>