<template>
  <div class="review-header-title">

    {{ currentSeneInfo?.sceneName }}

    <div style="display: flex;gap: 20px; align-items: center;">

      <el-button text bg type="primary" size="large" @click="configAgent()">选择角色</el-button>

      <div style="display: flex;align-items: center;gap: 10px;font-size: 14px;" v-for="item in currentSupportSceneAgents" :key="item.id">
        <span>{{ item.name }}</span>
        <!-- 使用计算属性显示 agent 列表 -->
        <img class="review-agent" v-for="(i , index) in agentListMap[item.id]" :key="index" :src="imagePathByPath(i.roleAvatar)" />
      </div> 

    </div>

    <el-dialog v-model="configAgentDialogVisible" :title="sceneAgentConfigTitle" width="960px">
      <!-- 配置场景智能体角色 -->
     <ConfigSceneAgent ref="configSceneAgentRef" @updateSceneAgents="handleGetScene" />
    </el-dialog>

  </div>
</template>

<script setup>
import {
  supportScene,
  getRoleList,
  getScene 
} from "@/api/base/im/scene";
import { ref, watch, onMounted } from 'vue';
import ConfigSceneAgent from './configSceneAgent.vue'; 
import { ElMessage } from "element-plus";

const route = useRoute();
const sceneId = ref(route.query.sceneId);
const emit = defineEmits(['openExecuteHandle']);

const configAgentDialogVisible = ref(false);
const sceneAgentConfigTitle = ref("");

const supportSceneList = ref([]);
const currentSupportScene = ref(null);
const configSceneAgentRef = ref(null);
const currentSeneInfo = ref(null);

const currentSupportSceneAgents = ref([]);
// 存储每个 item 的 agent 列表
const agentListMap = ref({});

/** 配置成员 */
function configAgent() {

  // 公共场景只允许在后台配置
  if(currentSeneInfo.value && currentSeneInfo.value.sceneScope === 'public'){
    ElMessage.warning('公共场景不允许在前台配置成员');
    return ;
  }

  configAgentDialogVisible.value = true;
  sceneAgentConfigTitle.value = currentSeneInfo.value.sceneName + "配置"; 
  nextTick(() => {
    configSceneAgentRef.value.configSceneAgent(currentSeneInfo.value, supportSceneList.value);
  });
}

const handleGetScene = async () => {

  if(!sceneId.value){
    return ;
  }

  try {
    // 并行发起两个请求
    const [sceneRes, supportSceneRes] = await Promise.all([
      getScene(sceneId.value),
      supportScene()
    ]);

    currentSeneInfo.value = sceneRes.data;
    supportSceneList.value = supportSceneRes.data;

    const filteredArray = supportSceneList.value.filter(item => item.code === currentSeneInfo.value.sceneType);

    if (filteredArray.length > 0) {
      currentSupportScene.value = filteredArray[0];
      currentSupportSceneAgents.value = currentSupportScene.value.agents;
    } else {
      currentSupportScene.value = null;
    }
  } catch (error) {
    console.error('获取场景信息失败:', error);
  }
};

// 监听 currentSupportSceneAgents 变化，更新 agentListMap
watch(currentSupportSceneAgents, (newAgents) => {
  newAgents.forEach(async (item) => {
    const data = {
      sceneId: sceneId.value,
      sceneTypeCode: currentSeneInfo.value.sceneType, 
      agentTypeId: item.id, 
      agentTypeCode: item.code, 
    };
    try {
      const res = await getRoleList(data);
      agentListMap.value = {
        ...agentListMap.value,
        [item.id]: res.data
      };
    } catch (error) {
      console.error('获取角色列表失败:', error);
      agentListMap.value = {
        ...agentListMap.value,
        [item.id]: []
      };
    }
  });
}, { immediate: true });

// 在组件挂载完成后立即执行数据获取和处理操作
onMounted(async () => {
  await handleGetScene();
});

defineExpose({
  configAgent
});
</script>

<style lang="scss" scoped>
.review-agent {
  width: 35px;
  border-radius: 50%;
}

.review-header-title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px 0px 20px;
  justify-content: space-between;
}
</style>

<style>
.el-transfer-panel {
  width: 350px;
}

.el-card__header {
  border: 0px;
}

.chapter-tree-content .el-tree-node__content {
  height: auto !important;
}
</style>    