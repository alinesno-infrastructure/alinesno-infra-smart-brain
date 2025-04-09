<template>

  <!-- 工作台Agent配置对话框 -->
  <el-dialog v-model="configAgentDialogVisible" :title="workplaceAgentConfigTitle" width="50%">
      <div style="text-align: center">
        <el-transfer 
            v-model="workplaceAgentList" 
            filterable
            style="text-align: left; width:100%; display: inline-block"
            :titles="['源角色', '已选择']"
            :format="{
              noChecked: '${total}',
              hasChecked: '${checked}/${total}',
            }"
            :filter-method="filterAgentMethod"
            filter-placeholder="搜索角色" 
            :data="agentList" >
              <!-- 自定义源列表项 -->
              <template #default="{ option }">
                  <div class="aip-el-transfer-panel__item">
                      <img :src="imagePath(option.avatar)" /> {{ option.label }}
                  </div>
              </template>

          </el-transfer>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="configAgentDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleCloseAgentConfig">确认</el-button>
        </div>
      </template>
  </el-dialog>

</template>

<script setup name="Index">

import {
  listAllRole , 
  listPublicRole
} from '@/api/smart/assistant/role';

const router = useRouter();
const {proxy} = getCurrentInstance();

const agentList = ref([])
const configAgentDialogVisible = ref(false)
const workplaceAgentConfigTitle = ref("")
const workplaceAgentList = ref([])
const currentWorkplaceId = ref(0) ;

/** 获取到所有的角色包括公共角色信息 */
const handleListAllRole = async() => {

  const res = await listAllRole() ; // .then(res => {

  for (let i = 0; i < res.data.length ; i++) {
      let item = res.data[i]

      agentList.value.push({
        key: item.id ,
        avatar: item.roleAvatar,
        label: item.roleName , 
        disabled: false ,
    })
  }

  // })
}

/** 获取到所有的公共角色(即在商店里面的角色) */
const handleListPublicRole = async() => {

  const res = await listPublicRole() ; // .then(res => {

  for (let i = 0; i < res.data.length ; i++) {
      let item = res.data[i]

      agentList.value.push({
        key: item.id ,
        avatar: item.roleAvatar,
        label: item.roleName, 
      })
  }

}

/** 关闭弹窗 */
function handleCloseAgentConfig(){
  configAgentDialogVisible.value = false ;

  updateWorkplaceAgent(currentWorkplaceId.value , workplaceAgentList.value).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    getList() ;
  })
  
}

/** 配置成员 */
const configAgent = async(row) =>{

    configAgentDialogVisible.value = true ;
    workplaceAgentConfigTitle.value = row.name + "成员" ; 

    agentList.value = []

    const workplaceType = row.workplaceType;
    if(workplaceType== 'org'){
      await handleListAllRole() ;
    }else if(workplaceType== 'public'){
      await handleListPublicRole();
    }

    currentWorkplaceId.value = row.id ;
    workplaceAgentList.value = row.roles ;
}

defineExpose({ 
  configAgent 
})

</script>

<style lang="scss" scoped>
// 自定义新式 
</style>