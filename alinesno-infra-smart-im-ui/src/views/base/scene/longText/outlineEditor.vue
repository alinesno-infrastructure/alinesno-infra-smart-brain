<template>
    <div>
        <el-card class="box-card" shadow="never">
            <template #header>
                <div class="card-header">
                    <div style="display: flex;align-items: center;gap: 5px;width: calc(100% - 250px);">
                        <i class="fa-solid fa-file-pdf"></i> 

                        <!-- 标题内容 -->
                        <EditableTitle 
                            v-model:title="taskInfo.taskName" 
                            @save="handleTitleSave"
                            class="article-edit-title" 
                        />

                        <el-tooltip v-for="(item,index) in currentSceneInfo.chapterEditors" :key="index"
                            class="box-item" 
                            effect="dark" 
                            :content="item.roleName" 
                            placement="top">
                            <span class="edit-header-avatar">
                                <img :src="imagePathByPath(item.roleAvatar)" @click="selectChaterGenerator(item)" />
                            </span>
                        </el-tooltip>

                    </div>
                    <span class="chapter-actions">
                        <el-tooltip class="box-item" effect="dark" content=" 展开或者收缩菜单" placement="top">
                            <el-button type="primary" text bg  @click="expandTree()">
                                <i class="fa-solid fa-up-down"></i>  &nbsp; {{ isTreeExpanded ? '收缩' : '展开' }}
                            </el-button>
                        </el-tooltip>
                        
                        <el-tooltip class="box-item" effect="dark" content="添加章节" placement="top">
                            <el-button type="primary" text bg  @click="addChapter">
                                <i class="fa-solid fa-plus-minus"></i>  &nbsp; 添加
                            </el-button>
                        </el-tooltip>

                        <el-tooltip class="box-item" effect="dark" content="保存章节" placement="top">
                            <el-button type="success" text bg  @click="getOutlineJsonSingle">
                                <i class="fa-solid fa-file-shield"></i> &nbsp; 保存
                            </el-button>
                        </el-tooltip> 

                    </span>
                </div>
            </template>
            <el-scrollbar style="height:calc(100vh - 140px)">
                <div class="chapter-tree-content">
                    <el-tree 
                        ref="treeRef"
                        :data="outline" 
                        node-key="id" 
                        default-expand-all 
                        draggable 
                        :empty-text="treeEmptyText"
                        :allow-drop="allowDrop"
                        :allow-drag="allowDrag" 
                        @node-click="handleNodeClick" 
                        @node-contextmenu="handleNodeContextMenu">
                        <template #default="{ node, data }">
                            <div class="custom-tree-node" :class="(selectNodeItem && selectNodeItem.id === node.id)?'active':''" style="height:auto;">
                                <div style="display: flex;flex-direction: column;">
                                    <div style="font-size: 16px;font-weight: bold;">
                                        {{ node.label }} <i v-if="!data.hasContent" class="fa-solid fa-triangle-exclamation"></i>
                                    </div>
                                    <div class="description">
                                        <span style="color: #777;">{{ data.description }}</span>
                                    </div>
                                </div>
                                <span style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">
                                    <el-avatar v-if="data.chapterEditor" :size="20" :src="imagePathByPath(data.chapterEditorAvatar)" style="margin-right:10px"></el-avatar>

                                    <el-button type="text" icon="Plus" bg size="mini" @click.stop="append(data)"></el-button>
                                    <el-button type="text" icon="EditPen" bg size="mini" @click.stop="edit(node, data)"></el-button>
                                    <el-button type="text" icon="Position" bg size="mini" @click.stop="editContent(node, data)"></el-button>

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
                <el-button size="large" type="primary" @click="openChatBoxStreamContent()">生成大纲</el-button>
            </div>
        </el-dialog>

        <!-- Agent选择组件_start -->
        <TransferAgentPanel ref="transferAgentPanel" @handleCloseAgentConfig="handleCloseAgentConfig" />
        <!-- Agent选择组件_end -->

        <!-- AI生成状态 -->
        <AIGeneratingStatus 
            ref="generatingStatusRef" 
            :back-to-path="'/scene/longText/longTextManager'"
            :route-params="{ sceneId: sceneId }" 
            :takeOverEnable="takeOverEnable"
            @takeOver="handleTakeOver"
        />

    </div>
</template>

<script setup>

import { ref, reactive, nextTick } from 'vue';
import { ElMessage , ElMessageBox , ElLoading } from 'element-plus';

import EditableTitle from './components/EditableTitle.vue'
import { getParam } from '@/utils/ruoyi'
import TransferAgentPanel from '@/views/base/scene/common/transferAgent'
import AIGeneratingStatus from '@/components/GeneratingStatus/index.vue'

import {
    updateChapterEditor, 
    updateSceneGenStatus ,
    getScene, 
    saveChapter, 
} from '@/api/base/im/scene/longText'

import {
    getLongTextTask,
    submitTask,
    updateTaskTitle ,
    submitChapterTask,
    updateTaskGenStatus,
    takeOver
} from '@/api/base/im/scene/longTextTask'
import SnowflakeId from "snowflake-id";
 
const snowflake = new SnowflakeId();
const route = useRoute();
const { proxy } = getCurrentInstance();

const takeOverEnable = ref(false)
 
const channelStreamId = ref(route.query.channelStreamId);
const taskId = ref(route.query.taskId);
const sceneId = ref(route.query.sceneId);

const treeRef = ref(null); // 获取 tree 实例的引用
const isTreeExpanded = ref(true); // 跟踪树节点的展开状态

const treeEmptyText = ref("你先还没有生成章节内容,请点击智能体头像生成章节内容")
const emit = defineEmits([
    'setCurrentSceneInfo' , 
    'getChannelStreamId',
    'openChatBox' ,
    'closeShowDebugRunDialog',
    'genChapterContentByAgent',
    'editContent' , 
    'handleExecuteHandle'])

// 选择Agent组件
const transferAgentPanel = ref(null)
const generatingStatusRef = ref(null)
const selectNodeItem = ref(null)
const streamLoading = ref(null)

const taskInfo = ref({
    taskName: '' ,
})

// 定义人物信息
const person = ref({
  roleAvatar: '' , 
  roleName: '',
  responsibilities: '',
  email: 'zhangsan@example.com',
});

const outline = ref([]);
const timer = ref(null) // 定时器引用
const dialogVisible = ref(false)

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
    const newChild = { 
        id: snowflake.generate(), 
        label: '新节点', 
        description: '节点内容描述信息', 
        sortOrder: data.children ? `${data.sortOrder}.${data.children.length + 1}` : `${data.sortOrder}.1`,
        children: [] 
    };
    
    if (!data.children) {
        data.children = [];
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
    selectNodeItem.value = node ;
    emit('editContent' , node , data)
}

const handleTitleSave = (newTitle) => {
  // 调用API保存标题
  updateTaskTitle(newTitle, taskId.value).then(res => {
    proxy.$modal.msgSuccess("更新标题成功");
  })
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
    currentSceneId.value = sceneId.value ; 

    await genStreamContent();

}

/** 生成内容 */
const genStreamContent = async(text) => {

  generatingStatusRef.value?.loading()
  if(text){
    generatingStatusRef.value.setText(text) ;
  }

  emit('openChatBox' , person.value.id) ; 
 
};

// 目录收缩与打开 
const expandTree = () => {
    if (!treeRef.value) return;
    
    // 获取所有节点
    const nodes = treeRef.value.store.nodesMap;
    
    // 切换所有节点的展开状态
    Object.values(nodes).forEach(node => {
        node.expanded = !isTreeExpanded.value;
    });
    
    // 更新状态
    isTreeExpanded.value = !isTreeExpanded.value; 
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
const handleGetScene = async() => {

    streamLoading.value = ElLoading.service({
        lock: true,
        text: '正在加载数据模块中', 
        background: 'rgba(255, 255, 255, 0.5)',
        customClass: 'custom-loading' 
    })

    await getScene(currentSceneId.value , taskId.value).then(res => {
      currentSceneInfo.value = res.data
      person.value = currentSceneInfo.value.chapterEditors[0];

      outline.value = res.data.chapterTree

      emit('setCurrentSceneInfo' , res.data)

      // 重新加载场景默认点击第1章节
      nextTick(() => {
        const item = outline.value[0];
        if(item && item.children && item.children.length > 0){
          const node = {
              label: item.label ,
              data: { chapterEditor:  item.chapterEditor ,  description: item.description , hasContent: item.hasContent }
          }
          const data = { id: item.id }
          editContent(node , data)
        }
        streamLoading.value.close();
      })

    })
}

const getOutlineJson = () => {

    streamLoading.value = ElLoading.service({
        lock: true,
        text: '数据正在保存中...', 
        background: 'rgba(255, 255, 255, 0.5)',
        customClass: 'custom-loading' 
    })

    // 生成安全的整数排序值（确保无小数点、无null）
    const generateSafeSortOrder = (nodes, baseOrder = 0) => {
        nodes.forEach((node, index) => {
            // 当前节点的顺序 = 父节点顺序 * 100 + (当前索引 + 1)
            // 示例：父节点是1，当前是第2个子节点 => 1 * 100 + 2 = 102
            node.sortOrder = (baseOrder || 0) * 100 + (index + 1); // 强制为整数
            if (node.children?.length) {
                generateSafeSortOrder(node.children, node.sortOrder);
            }
        });
    };

    // 为所有节点生成排序值
    generateSafeSortOrder(outline.value);

    const jsonResult = JSON.stringify(outline.value, null, 2);

    saveChapter(jsonResult , sceneId.value , taskId.value).then(res => {
        proxy.$modal.msgSuccess("保存成功");

        handleGetTask()
        // 章节生成完成之后，开始生成内容
        emit('genChapterContentByAgent')
    })
};

const getOutlineJsonSingle = () => {

    streamLoading.value = ElLoading.service({
        lock: true,
        text: '数据正在保存中...', 
        background: 'rgba(255, 255, 255, 0.5)',
        customClass: 'custom-loading' 
    })

    // 生成安全的整数排序值（确保无小数点、无null）
    const generateSafeSortOrder = (nodes, baseOrder = 0) => {
        nodes.forEach((node, index) => {
            // 当前节点的顺序 = 父节点顺序 * 100 + (当前索引 + 1)
            // 示例：父节点是1，当前是第2个子节点 => 1 * 100 + 2 = 102
            node.sortOrder = (baseOrder || 0) * 100 + (index + 1); // 强制为整数
            if (node.children?.length) {
                generateSafeSortOrder(node.children, node.sortOrder);
            }
        });
    };

    // 为所有节点生成排序值
    generateSafeSortOrder(outline.value);

    const jsonResult = JSON.stringify(outline.value, null, 2);

    saveChapter(jsonResult , sceneId.value , taskId.value).then(res => {
        proxy.$modal.msgSuccess("保存成功");
        handleGetTask()
    })
}

const closeStreamDialog = () => { 
  if(streamLoading.value){
    streamLoading.value.close()
  }

  if(generatingStatusRef.value){
    generatingStatusRef.value.close();
  }

  emit('closeShowDebugRunDialog')
}

// 设置主目录
const setOutline = (outlineVal) => {
    outline.value = outlineVal
}

// 接管任务
const handleTakeOver = () => {
    console.log("handleTaskOver")
    takeOver(taskId.value).then(res => {
        handleGetTask();
        // 接管成功
        proxy.$modal.msgSuccess("任务接管成功，请手工生成章节内容。");
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
 

const handleGetTask = async () => {

  try {
    await getLongTextTask(taskId.value).then(res => {
      taskInfo.value = res.data
      
      const taskStatus = taskInfo.value.taskStatus
      const chapterStatus = taskInfo.value.chapterStatus
      const currentChapterId = taskInfo.value.currentChapterId
      const currentChapterLabel = taskInfo.value.currentChapterLabel
      const taskEndTime = taskInfo.value.taskEndTime // 获取任务开始时间

      // 检查是否满足停止条件（大纲和章节都完成）
      if (taskStatus === 'completed' && chapterStatus === 'completed') {
        // 关闭dialog
        closeStreamDialog();

        clearInterval(timer.value)
        timer.value = null
        console.log("任务已完成，停止轮询")
        return
      }
      
      // 原有逻辑保持不变
      if (taskStatus == 'not_run' || taskStatus == null) {
        submitTask(taskId.value, channelStreamId.value).then(res => {
          genStreamContent()
        })
      } else if (taskStatus == 'running') {
        genStreamContent()
      } else if (taskStatus == 'completed') {  // 章节生成完成
 
        if(outline.value.length == 0 ){
           getScene(currentSceneId.value , taskId.value).then(res => {
                outline.value = res.data.chapterTree
           })
        }

        person.value = currentSceneInfo.value.contentEditors[0]
        
        if (chapterStatus == 'not_run' || chapterStatus == null) {
          submitChapterTask(taskId.value, channelStreamId.value).then(res => {
            genStreamContent("开始生成章节内容.")
          })
        } else if (chapterStatus == 'running') {
          openCurrentChapter(currentChapterId)
          genStreamContent(currentChapterLabel)

          const now = new Date();
          const start = new Date(taskEndTime);
          const durationMinutes = (now - start) / (1000 * 60);

          // 如果状态是running而且运行超过半个小时的，则显示人工接入takeOverEnable为true          
          if (durationMinutes > 30) {
            console.log("运行超过半个小时，显示人工接管按钮.")
            takeOverEnable.value = true;
          }

        } else if (chapterStatus == 'completed') {
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

nextTick( async () => { 
    await handleGetScene() ;
    await handleGetTask() ;
    startPolling() // 启动定时轮询
});

onUnmounted(() => {
  stopPolling() // 组件卸载时清除定时器
})

// 主动暴露childMethod方法
defineExpose({ 
    closeStreamDialog ,
    handleGetScene,
    genStreamContentByMessage,
    configAgent , 
    setOutline
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
    padding: 0px 0px;
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