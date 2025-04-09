<template>
    <div style="display: flex;gap: 20px; align-items: center;">

        <el-button text bg type="primary" size="large" @click="configAgent()">选择角色</el-button>

      <div style="display: flex;align-items: center;gap: 10px;font-size: 14px;">
        <span>内容分析专员</span>
        <img v-if="props.currentSeneInfo.summaryAgentEngineerEntity?.roleAvatar" class="review-agent" :src="imagePathByPath(props.currentSeneInfo.summaryAgentEngineerEntity?.roleAvatar)" />
      </div>

      <div style="display: flex;align-items: center;gap: 10px;font-size: 14px;">
        <span>案例查询专员</span>
        <img v-if="props.currentSeneInfo.caseQueryAgentEngineerEntity?.roleAvatar" class="review-agent" :src="imagePathByPath(props.currentSeneInfo.caseQueryAgentEngineerEntity?.roleAvatar)" />
      </div>

    </div>
</template>

<script setup>

// import TransferAgentPanel from '@/views/base/scene/common/transferAgent'

const emit = defineEmits(['openExecuteHandle'])

const props = defineProps({
  currentSeneInfo: {
      type: Object,
      default: () => {
          return {} 
      }
  }
})

// 选择Agent组件
const transferAgentPanel = ref(null)
const channelAgentList = ref([])

/** 选择角色生成目录 */
// function selectChaterGenerator(item) {
    // chaterDialogVisible.value = true;
    // console.log('selectChaterGenerator')
    // console.log(' personCardRef.value = ' +  personCardRef.value)
//     person.value = item ;
//     person.value.email =  'zhangsan@example.com';
// }

/** 配置成员 */
function configAgent(){

    // configAgentDialogVisible.value = true ;
    // console.log('configAgent')
    // currentAgentConfigType.value = type 

    // const title = type == 'chapter'?'选择大纲生成专员':'选择内容生成专员' ; 

    // if(type === 'analysisAgent'){
    //     channelAgentList.value = props.analysisAgentEngineers ;// currentSceneInfo.value.chapterEditors.map(item => item.id); 
    // }else if(type === 'logicReviewer'){
    //     channelAgentList.value = props.logicReviewerEngineers ; // currentSceneInfo.value.contentEditors.map(item => item.id); 
    // }

    // transferAgentPanel.value.handleOpendAgent(title , channelAgentList.value)

    emit('openExecuteHandle') ; 

}


</script>

<style lang="scss" scoped>
.review-agent{
  width: 35px;
  border-radius: 50%;
}
</style>

<style>
.el-transfer-panel {
    width:350px;
}

.el-card__header {
    border: 0px;
}

.chapter-tree-content .el-tree-node__content {
    height: auto !important;
}

</style>