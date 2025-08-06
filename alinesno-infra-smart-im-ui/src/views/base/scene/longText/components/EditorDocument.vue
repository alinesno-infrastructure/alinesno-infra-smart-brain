<template>

<el-dialog
    v-model="dialogVisible"
    :title="title"
    :close-on-press-escape="false"
    width="500"
    fullscreen 
    :before-close="handleClose">
    <span> 
    	<ChapterEditor 
    		ref="chapterEditorRef"
    		v-model:articleData="articleData"
    		:outlineEnable="true"
    		:abcHeight="150"
    		autoFocus="true" 
    	/>
    </span>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="large" @click="handleClose">取消</el-button>
        <el-button size="large" type="primary" @click="exportWord">导出Word文档</el-button>
      </div>
    </template>
  </el-dialog>

	<el-button type="primary" text bg @click="expertScene">
        <i class="fa-solid fa-display"></i>&nbsp; 预览下载
    </el-button>
</template>

<script setup>

import { ElMessageBox } from 'element-plus'
import ChapterEditor from '../chapterEditor'

import {
    getLongTextTask ,
    getMarkdownContent
} from '@/api/base/im/scene/longTextTask' ;


const props = defineProps({
  taskId: {
    type: String,
    required: true
  },
  sceneId: {
    type: String,
    required: true
  } 
})

const { proxy } = getCurrentInstance();

const dialogVisible = ref(false)
const title = ref("文档在线编辑.")

const taskInfo = ref(null)
const chapterEditorRef = ref(null)

const articleData = ref({
    content: "" , 
})

const expertScene = () => {
	dialogVisible.value = true ; 
	handleGetTask();
}
 
const handleClose = () => {
  ElMessageBox.confirm('你确认要退出么?')
    .then(() => {
      dialogVisible.value = false ; 
    })
    .catch(() => {
      // catch error
    })
}

const exportWord = () => { 
 
 articleData.value.fileName = taskInfo.value.taskName ;
	proxy.download(
	  "/api/infra/smart/assistant/scene/longTextTask/exportWord",
	  { ...articleData.value },
	  `${taskInfo.value.taskName}_${new Date().getTime()}.docx`  // 如：任务名称_1712345678901.docx
	);
}; 

const handleGetTask = async () => {

  try {
    await getLongTextTask(props.taskId).then(res => {
      taskInfo.value = res.data
      title.value = "编辑：" + taskInfo.value.taskName ;
     }) ; 

     await getMarkdownContent(props.sceneId , props.taskId).then(res => {
     	articleData.value.content = res.data ;
     }) 
  } catch (error) {
    console.error("查询任务出错:", error)
  }
}

</script>

<style lang="scss" scoped>

</style>