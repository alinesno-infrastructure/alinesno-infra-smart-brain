<template>
    <el-dialog v-model="configDialogVisible" :title="workplaceConfigTitle" width="50%">

      <div style="text-align: center">
        <el-transfer 
            v-loading="loading"
            v-model="workplaceChannelList" 
            filterable
            style="text-align: left; width:100%; display: inline-block"
            :titles="['源角色', '已选择']"
            :format="{
              noChecked: '${total}',
              hasChecked: '${checked}/${total}',
            }"
            :filter-method="filterAgentMethod"
            filter-placeholder="搜索角色" 
            :data="channelList" >
              <!-- 自定义源列表项 -->
              <template #default="{ option }">
                  <div class="aip-el-transfer-panel__item">
                      <img :src="imagePath(option.avatar)" /> {{ option.label }}
                  </div>
              </template>

          </el-transfer>
      </div>

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
  allPublicChannel 
} from '@/api/smart/assistant/channel';

import {
  updateWorkplaceItem
} from "@/api/smart/assistant/workplace";

const emit = defineEmits(['getList'])

const router = useRouter();
const {proxy} = getCurrentInstance();

const loading = ref(false)
const configDialogVisible = ref(false)
const workplaceConfigTitle = ref('频道配置')

const currentWorkplaceId = ref(0) ;
const workplaceChannelList = ref([])
const channelList = ref([])

const openConfigDialog =() => {
  configDialogVisible.value = true
}

const handleCloseConfig = () => {
  configDialogVisible.value = false

  const data = {
    workplaceId: currentWorkplaceId.value ,
    itemIds: workplaceChannelList.value ,
    itemType: 'channel'
  }
  
  updateWorkplaceItem(data).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    // getList() ;
    emit('getList')
  })
}

/** 配置成员 */
const configChannel = async(row) =>{

    configDialogVisible.value = true ;
    workplaceConfigTitle.value = row.name + "频道" ; 

    channelList.value = []

    const workplaceType = row.workplaceType;
    loading.value = true ;

    if(workplaceType== 'org'){
      await handleListAllChannel() ;
    }else if(workplaceType== 'public'){
      await handleListPublicChannel();
    }

    loading.value = false;
    currentWorkplaceId.value = row.id ;

    if(row.channelsList){
      workplaceChannelList.value = row.channelsList.map(item => item.id) ;
    }

}

/** 获取到所有的角色包括公共角色信息 */
const handleListAllChannel = async() => {

const res = await listAllChannel() ; // .then(res => {

for (let i = 0; i < res.data.length ; i++) {
    let item = res.data[i]

    channelList.value.push({
      key: item.id ,
      avatar: item.icon,
      label: item.channelName , 
      disabled: false ,
  })
}

// })
}

/** 搜索过滤方法 */
const filterAgentMethod = (query, item) => {
  // return item.initial.includes(query)
  return item ;
}

/** 获取到所有的公共角色(即在商店里面的角色) */
const handleListPublicChannel = async() => {

const res = await allPublicChannel() ; // .then(res => {

for (let i = 0; i < res.data.length ; i++) {
    let item = res.data[i]

    channelList.value.push({
      key: item.id ,
      avatar: item.icon,
      label: item.channelName, 
    })
}

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