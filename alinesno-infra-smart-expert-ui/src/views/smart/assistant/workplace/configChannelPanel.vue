<template>
    <el-dialog v-model="configDialogVisible" :title="workplaceConfigTitle" width="50%">

        <!-- 底部 -->
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="configDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="handleCloseConfig">确认</el-button>
          </div>
        </template>
    </el-dialog>
</template>

<script setup name="Index">

import {
  listAllChannel, 
  listPublicChannel 
} from '@/api/smart/assistant/channel';

const configDialogVisible = ref(false)
const workplaceConfigTitle = ref('频道配置')

const openConfigDialog =() => {
  configDialogVisible.value = true
}

const handleCloseConfig = () => {
  configDialogVisible.value = false
}

/** 配置成员 */
const configChannel = async(row) =>{

    configDialogVisible.value = true ;
    workplaceConfigTitle.value = row.name + "频道" ; 

    agentList.value = []

    // const workplaceType = row.workplaceType;
    // if(workplaceType== 'org'){
    //   await handleListAllRole() ;
    // }else if(workplaceType== 'public'){
    //   await handleListPublicRole();
    // }

    // currentWorkplaceId.value = row.id ;
    // workplaceAgentList.value = row.roles ;

}

defineExpose({ 
  configChannel
})

// defineExpose({
//   openConfigDialog
// })

</script>

<style lang="scss" scoped>
// 自定义新式 
</style>