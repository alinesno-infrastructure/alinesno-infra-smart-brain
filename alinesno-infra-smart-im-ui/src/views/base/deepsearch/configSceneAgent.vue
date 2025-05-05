<template>
  <div class="config-agent-container">
    <div class="description">
      配置场景智能体，将场景能力进一步结合到场景中，实现场景智能体的功能。 
    </div>
    <div v-for="parentItem in currentSupportSceneAgents" :key="parentItem.id" class="agent-item">
      <div class="agent-info">
        <span class="agent-name">{{ parentItem.name }}</span>
        <span class="agent-desc">{{ parentItem.description }}</span>
      </div>
      <div class="role-radio-group">
        <el-radio-group v-model="parentItem.selectAgentId" class="radio-group">
          <el-radio v-for="item in parentItem.roles" :key="item.id" :value="item.id" :label="item.id" size="large"
            class="role-radio">
            <div class="role-item">
              <div class="role-avatar">
                <img :src="imagePathByPath(item.roleAvatar)" style="width: 40px;height: 40px;border-radius: 50%;" />
              </div>
              <div class="role-details">
                <span class="role-name">{{ item.roleName }}</span>
                <span class="role-responsibilities">{{ item.responsibilities }}</span>
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
    </div>

    <div class="save-button-container">
      <el-button type="primary" @click="saveRole" size="large">确认保存</el-button>
    </div>
  </div>
</template>

<script setup name="Index">
import {
  getRoleBySceneIdAndAgentType , 
  updateSceneAgents
} from '@/api/base/im/scene'

import { ElMessage } from 'element-plus'
import { ref, watch } from 'vue';

const emit = defineEmits(['updateSceneAgents'])

const currentSceneInfo = ref(null)
const currentSupportScene = ref(null)
const currentSupportSceneAgents = ref(null)
const supportSceneList = ref([])

const configSceneAgent = (row, supportSceneListVal) => {
  currentSceneInfo.value = row;
  supportSceneList.value = supportSceneListVal;
  currentSupportSceneAgents.value = [] ;

  const filteredArray = supportSceneListVal.filter(item => item.code === row.sceneType);
  if (filteredArray.length > 0) {
    currentSupportScene.value = filteredArray[0];
    currentSupportSceneAgents.value = currentSupportScene.value.agents;
  } else {
    currentSupportScene.value = null;
  }

  console.log('currentSupportSceneAgents = ' + JSON.stringify(currentSupportSceneAgents.value))
}

const handleGetRoleBySceneIdAndAgentType = async (item) => {
  try {
    const response = await getRoleBySceneIdAndAgentType(currentSupportScene.value.id, item.id)
    item.roles = response.data;
  } catch (error) {
    console.error('Error fetching roles:', error);
    item.roles = [];
  }
}

const saveRole = () => {

  const sceneTypeId = currentSupportScene.value.id
  const sceneTypeCode = currentSupportScene.value.code 

  let selectArr = [] ; 
  for (const item of currentSupportSceneAgents.value) {
      const i = {
        id: item.id , 
        code: item.code , 
        selectAgentId: item.selectAgentId 
      }
      selectArr.push(i)
  }

  updateSceneAgents(currentSceneInfo.value.id , sceneTypeId , sceneTypeCode , selectArr).then(res => {
    ElMessage.success('保存成功')
    emit('updateSceneAgents')
  })

}

watch(() => currentSupportScene.value?.agents, async (agents) => {
  if (agents) {
    for (const item of agents) {
      await handleGetRoleBySceneIdAndAgentType(item);
    }
  }
})

defineExpose({
  configSceneAgent
})
</script>

<style lang="scss" scoped>
.config-agent-container {
  .description {
    font-size: 15px;
    padding: 10px 0;
    color: #a5a5a5;
  }

  .agent-item {
    padding: 10px 0;

    .agent-info {
      display: flex;
      flex-direction: column;
      gap: 10px;

      .agent-name {
        font-size: 15px;
        font-weight: bold;
      }

      .agent-desc {
        font-size: 13px;
        color: #a5a5a5;
      }
    }

    .role-radio-group {
      .radio-group {
        display: inline-flex;
        flex-flow: column;
        font-size: 0px;
        justify-content: flex-start;
        align-items: flex-start;
        gap: 15px;
        margin-top: 10px;
        margin-bottom: 10px;

        .role-radio {
          .role-item {
            display: flex;
            flex-direction: row;
            gap: 10px;

            .role-avatar {
              width: 40px;
              height: 40px;
              border-radius: 50%;
            }

            .role-details {
              display: flex;
              flex-direction: column;
              gap: 5px;

              .role-name {
                font-size: 15px;
                font-weight: bold;
              }

              .role-responsibilities {
                font-size: 13px;
                color: #a5a5a5;
              }
            }
          }
        }
      }
    }
  }

  .save-button-container {
    margin-top: 20px;
    text-align: right;
  }
}
</style>