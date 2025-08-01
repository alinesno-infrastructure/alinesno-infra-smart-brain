<template>
  <div class="app-container memory-display-container"> 
        <div class="page-header-container" style="margin-bottom: 20px;">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="text-large font-600 mr-3"> 
                <img :src="imagePath(currentRole.roleAvatar)" class="role-avatar" />
            </span>
            <span class="text-large font-600 mr-3"> 
               [{{ currentRole.roleName }}]运行监控
            </span>
            <span style="color: #aaaaaa;font-size: 14px;">{{ currentRole.responsibilities }}</span>
          </div>
        </template>
      </el-page-header>
    </div>
    
    <div class="agent-monitor-container">
      <el-tabs v-model="activeTab" :tab-position="tabPosition">
        <el-tab-pane lazy="true" label="运行指标" name="metrics">
          <Metrics :role-id="currentRoleId" />
        </el-tab-pane>
        
        <el-tab-pane lazy="true" label="消息日志" name="logs">
          <Logs :role-id="currentRoleId" />
        </el-tab-pane>
        
        <el-tab-pane lazy="true" label="记忆管理" name="memory">
          <Memory :role-id="currentRoleId" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getRole } from "@/api/smart/assistant/role"
import Metrics from './metrics.vue'
import Logs from './logs.vue'
import Memory from './memory.vue'

const router = useRouter()
const route = useRoute()

const tabPosition = ref("top")

const currentRoleId = ref(route.query.roleId)
const currentRole = ref({ roleName: '' })
const activeTab = ref('metrics')

// 获取角色信息
function getRoleInfo() {
  getRole(currentRoleId.value).then(response => {
    currentRole.value = response.data
  })
}

// 返回 
function goBack() {
  router.go(-1);
}

onMounted(() => { 
  getRoleInfo()
})
</script>

<style lang="scss" scoped>
  .role-avatar {
    width: 25px;
    height: 25px;
    border-radius: 5px;
  }

</style>