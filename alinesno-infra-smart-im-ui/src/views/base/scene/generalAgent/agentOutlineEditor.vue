<template>
    <div>
        <el-card class="box-card" shadow="never">
            <template #header>
                <div class="card-header">
                    <div style="display: flex;align-items: center;gap: 10px;">

                        <span style="display: flex;gap: 5px;align-items: center;">
                            <i class="fa-solid fa-file-pdf"></i>

                            <!-- 标题内容 -->
                            <EditableTitle
                                v-if="currentSceneInfo"
                                v-model:title="currentSceneInfo.promptContent"
                                class="article-edit-title"
                            />
                        </span>

                        <el-tooltip v-for="(item,index) in currentSceneInfo.businessProcessorEngineers" :key="index"
                            class="box-item"
                            effect="dark"
                            :content="item.roleName"
                            placement="top">
                            <span class="edit-header-avatar">
                                <img :src="imagePathByPath(item.roleAvatar)" @click="selectChaterGenerator(item)" />
                            </span>
                        </el-tooltip>

                    </div>
                    <span>

                        <el-tooltip class="box-item" effect="dark" content="添加章节" placement="top">
                            <el-button type="primary" text bg size="large" @click="addChapter">
                                <i class="fa-solid fa-plus-minus"></i>  &nbsp; 添加
                            </el-button>
                        </el-tooltip>

                        <el-tooltip class="box-item" effect="dark" content="保存章节" placement="top">
                            <el-button type="success" text bg size="large" @click="getOutlineJsonSingle">
                                <i class="fa-solid fa-file-shield"></i> &nbsp; 保存
                            </el-button>
                        </el-tooltip>

                    </span>
                </div>
            </template>
            <el-scrollbar style="height:calc(100vh - 150px)">
                <div class="data-outline-tree-content">
                    <el-tree :data="outline"
                        node-key="id"
                        default-expand-all
                        draggable
                        :empty-text="treeEmptyText"
                        :allow-drop="allowDrop"
                        :allow-drag="allowDrag"
                        @node-click="handleNodeClick"
                        @node-contextmenu="handleNodeContextMenu">
                        <template #default="{ node, data }">
                            <div class="custom-tree-node" style="height:auto;">
                                <div style="display: flex;flex-direction: column;">
                                    <div style="font-size: 16px;font-weight: bold;">
                                        {{ node.label }}
                                    </div>
                                    <div class="description">
                                        <span style="color: #777;">{{ data.description }}</span>
                                    </div>
                                </div>
                                <span style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">
                                    <el-avatar v-if="data.chapterEditor" :size="20" :src="imagePathByPath(data.chapterEditorAvatar)" style="margin-right:10px"></el-avatar>

                                    <el-button type="text" icon="Plus" bg size="mini" @click.stop="append(data)"></el-button>
                                    <el-button type="text" icon="Edit" bg size="mini" @click.stop="edit(node, data)"></el-button>
                                    <el-button type="text" icon="EditPen" bg size="mini" @click.stop="editContent(node, data)"></el-button>

                                    <el-popconfirm title="确认要删除章节么?" @confirm="remove(node, data)">
                                        <template #reference>
                                            <el-button type="text" icon="Delete" bg size="mini"></el-button>
                                        </template>
                                    </el-popconfirm>
                                </span>
                            </div>
                        </template>
                    </el-tree>
                </div>
            </el-scrollbar>
        </el-card>

        <!-- 编辑节点 -->
        <el-dialog title="编辑节点" v-model="dialogVisible" width="900px">

            <el-form label-width="80px" size="large">
                <el-form-item label="节点名称">
                    <el-input v-model="editingNodeLabel" placeholder="请输入节点名称" />
                </el-form-item>
                <el-form-item label="节点描述">
                    <el-input type="textarea" :rows="5" resize="none" v-model="editingDescription" placeholder="请输入节点描述" />
                </el-form-item>
            </el-form>

            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit()">确 认</el-button>
            </div>
        </el-dialog>

        <!-- 生成流式内容 -->
        <el-dialog title="由以下工程师生成大纲" v-model="chaterDialogVisible" width="600px">

                <el-card class="person-card" shadow="never">
                    <div style="display: flex;gap: 20px;align-items: center;">
                        <div>
                            <el-avatar :size="60" :src="imagePathByPath(person.roleAvatar)"></el-avatar>
                        </div>
                        <div>
                            <h2 style="font-weight: bold;">{{ person.roleName }}</h2>
                            <el-row>
                                <el-col :span="24">
                                    <div style="margin-bottom: 10px;">
                                    {{ person.responsibilities }}
                                    </div>
                                </el-col>
                            </el-row>
                        </div>
                    </div>
                </el-card>

                <el-input size="large" v-model="message" placeholder="请输出针对于本章节内容的一些自定义要求"></el-input>

            <div class="dialog-footer">
                <el-button size="large" @click="chaterDialogVisible = false">取 消</el-button>
                <el-button size="large" type="primary" @click="openChatBoxStreamContent()">开始规划</el-button>
            </div>
        </el-dialog>

        <!-- Agent选择组件_start -->
        <!-- <TransferAgentPanel ref="transferAgentPanel" @handleCloseAgentConfig="handleCloseAgentConfig" /> -->
        <!-- Agent选择组件_end -->

    </div>
</template>

<script setup>

import { ref, reactive, nextTick } from 'vue';
import { ElMessage , ElMessageBox , ElLoading } from 'element-plus';

import EditableTitle from './components/EditableTitle.vue'
import { getParam } from '@/utils/ruoyi'

import {
    // updateChapterEditor,
    updateSceneGenStatus ,
    getScene,
    getGeneralAgentScene ,
    saveDataPlan,
    chatRole,
} from '@/api/base/im/scene/generalAgent'

import {
    getLongTextTask,
    submitTask,
    updateTaskStatus ,
    submitChapterTask,
    updateTaskGenStatus
} from '@/api/base/im/scene/generalAgentTask'

const route = useRoute();
const { proxy } = getCurrentInstance();

// const route = useRoute();
const channelStreamId = ref(route.query.channelStreamId);

const treeEmptyText = ref("你先还没有生成章节内容,请点击智能体头像生成章节内容")
const emit = defineEmits([
    'setCurrentSceneInfo' ,
    'getChannelStreamId',
    'openChatBox' ,
    'closeShowDebugRunDialog',
    'genChapterContentByAgent',
    'editContent' ,
    'handleExecuteHandle'
])

// 选择Agent组件
const transferAgentPanel = ref(null)

const streamLoading = ref(null)

// 定义人物信息
const person = ref({
  roleAvatar: '' ,
  roleName: '',
  responsibilities: '',
  email: 'zhangsan@example.com',
});

const taskInfo = ref({
    taskName: '' ,
})

const outline = ref([]);

const dialogVisible = ref(false)
const timer = ref(null) // 定时器引用
const sceneId = ref(route.query.sceneId)
const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])
const currentAgentConfigType = ref('chapter')

const currentSceneInfo = ref({
    id: 0,
    sceneName: null ,
    sceneDesc: null ,
    sceneCount: 0,
    knowledgeId: null ,
    chapterEditors: [],
    contentEditors: []
})
const currentSceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const isCheckGenStatus = ref(route.query.genStatus)

const agentList = ref([])
const chaterDialogVisible = ref(false);
const editingNode = ref(null);
const editingNodeLabel = ref('');
const editingDescription = ref('');

const message = ref("")  // 生成内容的要求
const isRoleWriter = ref(false)

const allowDrop = (draggingNode, dropNode, type) => {
    return type !== 'inner';
};

const allowDrag = (draggingNode) => {
    return draggingNode.data.id !== 1; // 不允许拖动根节点
};

const handleNodeClick = (data) => {
    console.log('Node clicked: ', data);
};

const openChatBoxStreamContent = () => {
    chaterDialogVisible.value = false ;
    emit('openChatBox' , person.value.id, message.value)
}

const handleNodeContextMenu = (event, data, node) => {
    event.preventDefault();
    console.log('Node context menu: ', data, node);
};

const append = (data) => {
    const newChild = { id: Date.now(), label: '新节点', description: '节点内容描述信息', children: [] };
    if (!data.children) {
        data.children = []; // 直接赋值，避免使用 this.$set
    }
    data.children.push(newChild);
};

const remove = (node, data) => {
    const parent = node.parent;
    const children = parent.data.children || parent.data;
    const index = children.findIndex(d => d.id === data.id);
    children.splice(index, 1);
};

const edit = (node, data) => {
    editingNode.value = node;
    editingDescription.value = data.description;
    editingNodeLabel.value = node.label;
    dialogVisible.value = true;
};

/** 编辑内容 */
const editContent = (node , data) => {
    console.log('editContent' , node , data)
    emit('editContent' , node , data)
}

// 保存编辑节点
const saveEdit = () => {
    if (editingNode.value) {
        editingNode.value.data.label = editingNodeLabel.value;
        editingNode.value.data.description = editingDescription.value;
        ElMessage.success('编辑成功');
        dialogVisible.value = false;
    }
};

/** 生成内容 */
const genStreamContentByMessage = async (roleIdVal , messageVal) => {
    message.value = messageVal ;
    person.value.id = roleIdVal ;
    currentSceneId.value = getParam('sceneId') ;

    await genStreamContent();

}

const onClickLeft = () => {
    proxy.$router.push({
        path: '/scene/generalAgent/index' ,
        query: {
            sceneId: sceneId.value,
            back: true
        }
    })
}

/** 生成内容 */
const genStreamContent = async(text) => {

//   if (!message.value) {
//     proxy.$modal.msgError("请输入消息内容.");
//     return;
//   }

// //   const channelStreamId = emit('getChannelStreamId');
//   console.log('channelStreamId = ' + channelStreamId.value);

//   if(!channelStreamId.value){
//     proxy.$modal.msgError("请先创建场景.");
//     return;
//   }

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '规划任务执行中，请勿操作其它界面 ...',
    background: 'rgba(255, 255, 255, 0.5)',
    customClass: 'custom-loading'
  })

  if(text){
    streamLoading.value.setText(text) ;
  }


//   let formData = {
//     sceneId: currentSceneId.value,
//     channelStreamId: channelStreamId.value ,
//     taskId: taskId.value ,
//     message: message.value,
//     roleId: person.value.id ,
//   }

  console.log('outline.value = ' + outline.value);
  emit('openChatBox' , person.value.id) ; 

//   const response = await chatRole(formData)
//   console.log('response = ' + response)

//   proxy.$modal.msgSuccess("发送成功");
//   chaterDialogVisible.value = false ;
//   message.value = '';

//   // 合并数组
//   Array.prototype.push.apply(outline.value, response.data);
//   closeStreamDialog();

  // 自动保存章节
//   getOutlineJson();

};

const addChapter = () => {
    const newChapter = { id: Date.now(), label: `第${outline.value.length + 1}章`, description: '节点内容描述信息', children: [] };
    outline.value.push(newChapter);
};

/** 选择角色生成目录 */
function selectChaterGenerator(item) {
    chaterDialogVisible.value = true;
    console.log('selectChaterGenerator')
    // console.log(' personCardRef.value = ' +  personCardRef.value)
    person.value = item ;
    person.value.email =  'zhangsan@example.com';
}

/** 配置成员 */
function configAgent(type){

    configAgentDialogVisible.value = true ;
    console.log('configAgent')

    currentAgentConfigType.value = type
    const title = type == 'chapter'?'选择大纲生成专员':'选择内容生成专员' ;

    if(type === 'chapter'){
        channelAgentList.value = currentSceneInfo.value.chapterEditors.map(item => item.id);
    }else if(type === 'content'){
        channelAgentList.value = currentSceneInfo.value.contentEditors.map(item => item.id);
    }

    transferAgentPanel.value.handleOpendAgent(title , channelAgentList.value)

}

/** 关闭选择角色弹窗 */
function handleCloseAgentConfig(selectAgentList){
  configAgentDialogVisible.value = false ;

  updateChapterEditor(currentSceneId.value , selectAgentList , currentAgentConfigType.value).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    handleGetScene() ;
  })
}

/** 获取到场景详情 */
const handleGetScene = async () => {

    await getGeneralAgentScene(currentSceneId.value , taskId.value).then(res => {
      currentSceneInfo.value = res.data
      person.value = currentSceneInfo.value.businessProcessorEngineers[0];

      outline.value = res.data.chapterTree || [];

      emit('setCurrentSceneInfo' , res.data)

    //   const genStatus = currentSceneInfo.value.genStatus ;
    //   message.value = currentSceneInfo.value.promptContent;

    //   if(genStatus == 0 && isCheckGenStatus.value && currentSceneInfo.value.promptContent){ // 未生成章节
    //     ElMessageBox.confirm( '检测到数据分析指令['+ currentSceneInfo.value.promptContent +']是否执行分析规划？', '提示', {
    //         confirmButtonText: '确定',
    //         cancelButtonText: '取消',
    //         type: 'warning'
    //     })
    //     .then(() => {
    //         updateSceneGenStatus(currentSceneId.value , 1).then(res => {
    //             person.value = currentSceneInfo.value.businessProcessorEngineers[0];
    //             message.value = currentSceneInfo.value.promptContent;
    //             openChatBoxStreamContent();
    //         })
    //     })
    //     .catch(() => {
    //         console.log('用户取消了生成章节操作');
    //     });
    //   }

      if(outline.value.length > 0){

        const loading = ElLoading.service({
            lock: true,
            text: '加载中...',
            background: 'rgba(0, 0, 0, 0.7)',
        })

        // 默认显示第1章节
        nextTick(() => {
            const item = outline.value[0];
            const node = {
                label: item.label ,
                data: { chapterEditor:  item.chapterEditor ,  description: item.description }
            }
            const data = { id: item.id }
            editContent(node , data)
            loading.close();
        })
      }

    })

}


// 点击当前的chapter
const openCurrentChapter = (currentChapterId) => {
    // 递归查找函数
    const findChapter = (nodes) => {
        for (const node of nodes) {
            // 检查当前节点是否匹配
            if (node.id === currentChapterId) {
                return node;
            }
            // 递归检查子节点
            if (node.children && node.children.length > 0) {
                const found = findChapter(node.children);
                if (found) return found;
            }
        }
        return null; // 未找到
    };

    // 从整个chapterTree中查找
    const chapter = findChapter(outline.value); // 假设outline.value存储整个章节树

    console.log("businessProcessorEngineers = " + JSON.stringify(chapter))
    
    if (chapter) {
        // 构建node对象
        const node = {
            label: chapter.label,
            data: {
                chapterEditor: chapter.chapterEditor,
                description: chapter.description,
                chapterEditorAvatar: chapter.chapterEditorAvatar // 保留头像信息
            }
        };
        
        // 调用编辑方法
        editContent(node, { id: chapter.id });
    } else {
        console.warn(`未找到ID为 ${currentChapterId} 的章节`);
        // 可添加默认处理逻辑
    }
};

const getOutlineJson = () => {
    const jsonResult = JSON.stringify(outline.value, null, 2);
    console.log(jsonResult);
    // 可以在这里处理 JSON 数据，例如发送到服务器或显示在页面上

    saveDataPlan(jsonResult , route.query.sceneId , taskId.value).then(res => {
        proxy.$modal.msgSuccess("保存成功");
        isCheckGenStatus.value = false ;
        handleGetScene()
        // 章节生成完成之后，开始生成内容
        emit('genChapterContentByAgent')
    })
};

const getOutlineJsonSingle = () => {
    const jsonResult = JSON.stringify(outline.value, null, 2);

    saveChapter(jsonResult , route.query.sceneId).then(res => {
        proxy.$modal.msgSuccess("保存成功");
        handleGetScene()
    })
}

const closeStreamDialog = () => {
  console.log('child method. ' + streamLoading.value)
  if(streamLoading.value){
    streamLoading.value.close()
  }
  emit('closeShowDebugRunDialog')
}

const handleGetTask = async () => {

  try {
    await getLongTextTask(taskId.value).then(res => {
      taskInfo.value = res.data
      
      const taskStatus = taskInfo.value.taskStatus
      const chapterStatus = taskInfo.value.chapterStatus
      const currentChapterId = taskInfo.value.currentChapterId
      const currentChapterLabel = taskInfo.value.currentChapterLabel
      
      // 检查是否满足停止条件（大纲和章节都完成）
      if (taskStatus === '1' && chapterStatus === '1') {
        // 关闭dialog
        closeStreamDialog();

        clearInterval(timer.value)
        timer.value = null
        console.log("任务已完成，停止轮询")
        return
      }
      
      // 原有逻辑保持不变
      if (taskStatus == 0 || taskStatus == null) {
        submitTask(taskId.value, channelStreamId.value).then(res => {
          genStreamContent()
        })
      } else if (taskStatus == 2) {
        genStreamContent()
      } else if (taskStatus == 1) {  // 章节生成完成

        if(outline.value.length == 0 ){
           getScene(currentSceneId.value , taskId.value).then(res => {
                outline.value = res.data.chapterTree
           })
        }

        person.value = currentSceneInfo.value.businessExecuteEngineers[0] //contentEditors[0]
        
        if (chapterStatus == 0 || chapterStatus == null) {
          submitChapterTask(taskId.value, channelStreamId.value).then(res => {
            genStreamContent("开始生成章节内容.")
          })
        } else if (chapterStatus == 2) {
          openCurrentChapter(currentChapterId)
          genStreamContent(currentChapterLabel)
        } else if (chapterStatus == 1) {
          // 章节生成完成
        }
      }

    })
  } catch (error) {
    console.error("查询任务出错:", error)
  }
}

// 启动轮询
const startPolling = () => {
  if (!timer.value) {
    timer.value = setInterval(handleGetTask, 5000) // 每5秒轮询一次
    console.log("启动任务轮询")
  }
}

// 停止轮询
const stopPolling = () => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
    console.log("停止任务轮询")
  }
}

// 重新开始编写生成内容
const genChapterContent = () => {
    updateTaskStatus(taskId.value).then(res => {
        handleGetTask() ;
        startPolling() // 启动定时轮询
    })
}

nextTick( async() => {
    console.log('sceneId = ' + route.query.sceneId) ;
    await handleGetScene()
    await handleGetTask() ;
    startPolling() // 启动定时轮询
})

onUnmounted(() => {
  stopPolling() // 组件卸载时清除定时器
})


// 主动暴露childMethod方法
defineExpose({
    closeStreamDialog ,
    handleGetScene,
    genChapterContent ,
    genStreamContentByMessage,
    configAgent
})


</script>

<style lang="scss" scoped>
.box-card {
    width: 100%;
    margin: auto;
    border: 0px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    padding: 0px 20px;
    align-items: center;
}


.dialog-body-content {
    margin-top:20px;
    .upload-knowledge {
        margin-bottom: 20px;
    }

}

.dialog-footer {
    text-align: right;
    margin-top: 20px;
}

.person-card {
  margin: 0 auto;
  margin-bottom: 20px;

  .avatar-container {
    text-align: center;
  }

  .info-container {
    h2 {
      margin-top: 0;
    }

    p {
      margin: 5px 0 15px;
      font-size: 16px;
      color: #666;
    }

    .el-link {
      display: block;
      font-size: 14px;
      color: #409eff;

      .el-icon {
        margin-right: 5px;
      }
    }
  }
}

.dialog-chapter-stream-content {
    background-color: #f8f8f8; /* 浅灰色背景 */
    color: #333; /* 深灰色文字 */
    overflow: auto; /* 当内容超出容器时显示滚动条 */
    line-height: 1.6; /* 行间距 */
    white-space: pre-wrap; /* 保留空白符序列，并且在必要时换行 */
    width: 100%; /* 宽度为100% */
    word-wrap: break-word; /* 长单词或URL地址在必要时断开并换行 */
    overflow-wrap: break-word; /* 同上，更现代的属性名称 */
}

.chat-gen-container {
    height: 400px;
    border: 1px solid #414243;
    margin-bottom: 20px;
    margin-top:20px;
    border-radius: 5px;
}

</style>

<style>
/*
.el-transfer-panel {
    width:350px;
}
*/

.el-card__header {
    border: 0px;
}

.data-outline-tree-content .el-tree-node__content {
    height: auto !important;
}

</style>
