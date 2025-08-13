<template>
  <div class="chapter-tree-content">
 
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
                    <!-- <el-button type="text" icon="EditPen" bg size="mini" @click.stop="editContent(node, data)"></el-button> -->

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
</template>

<script setup>

import { ref, reactive, nextTick } from 'vue';
import { ElMessage , ElMessageBox , ElLoading } from 'element-plus';

import { getParam } from '@/utils/ruoyi'
// import TransferAgentPanel from '@/views/base/scene/common/transferAgent'

import { 
    saveChapter, 
} from '@/api/base/im/scene/productResearch'
 
const route = useRoute();
const { proxy } = getCurrentInstance();
 
const treeEmptyText = ref("你先还没有生成章节内容,请点击智能体头像生成章节内容")
 
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

const outline = ref([]);

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
 

const addChapter = () => {
    const newChapter = { id: Date.now(), label: `第${outline.value.length + 1}章`, description: '节点内容描述信息', children: [] };
    outline.value.push(newChapter);
};
 
const getOutlineJson = () => {
    const jsonResult = JSON.stringify(outline.value, null, 2);

    saveChapter(jsonResult , route.query.sceneId).then(res => {
        proxy.$modal.msgSuccess("保存成功");
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
 
const setOutlineJson = (outlineVal) => {
    console.log("outline = " + outlineVal);
    outline.value = outlineVal
}

// 主动暴露childMethod方法
defineExpose({ 
    // closeStreamDialog ,
    // handleGetScene,
    // genStreamContentByMessage,
    // configAgent
    setOutlineJson
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

.chapter-tree-content{
    padding-right: 20px;
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