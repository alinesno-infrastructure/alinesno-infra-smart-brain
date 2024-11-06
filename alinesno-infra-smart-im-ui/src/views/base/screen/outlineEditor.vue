<template>
    <div>
        <el-card class="box-card" shadow="never">
            <template #header class="card-header-tpl">
                <div class="card-header">
                    <div style="display: flex;align-items: center;gap: 10px;;">
                        <i class="fa-solid fa-file-pdf"></i>招投标书编写

                        <el-tooltip class="box-item" effect="dark" content="罗小东，擅长生成业务编辑目录." placement="top">
                            <span class="edit-header-avatar">
                                <img src="http://data.linesno.com/switch_header.png" @click="selectChaterGenerator()" />
                            </span>
                        </el-tooltip>

                        <el-tooltip class="box-item" effect="dark" content="添加章节编写人员" placement="top">
                            <span class="edit-header-avatar"  @click="configAgent()">
                                <i class="fa-solid fa-user-plus"></i>
                            </span>
                        </el-tooltip>
                    </div>
                    <span>

                        <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top">
                            <el-button type="danger" text bg size="large" @click="handleUploadFile">
                                <i class="fa-solid fa-file-word icon-btn"></i>
                            </el-button>
                        </el-tooltip>

                        <el-tooltip class="box-item" effect="dark" content="添加章节" placement="top">
                            <el-button type="primary" text bg size="large" @click="addChapter">
                                <i class="fa-solid fa-feather icon-btn"></i>
                            </el-button>
                        </el-tooltip>

                        <!-- <el-button type="success" @click="getOutlineJson">获取目录 JSON</el-button> -->
                    </span>
                </div>
            </template>
            <el-scrollbar style="height:calc(100vh - 180px)">


                <el-tree :data="outline" node-key="id" default-expand-all draggable :allow-drop="allowDrop"
                    :allow-drag="allowDrag" @node-click="handleNodeClick" @node-contextmenu="handleNodeContextMenu">
                    <template #default="{ node, data }">
                        <div class="custom-tree-node" style="height:auto;">
                            <div style="display: flex;flex-direction: column;">
                                <div style="font-size: 16px;font-weight: bold;color:#333">{{ node.label }}</div>
                                <div class="description">
                                    <span style="color: #777;">{{ data.description }}</span>
                                </div>
                            </div>
                            <span>
                                <el-button type="text" size="mini" @click.stop="append(data)">
                                    添加
                                </el-button>

                                <el-dropdown class="func-drapdown">
                                    <span class="el-dropdown-link">
                                        <el-icon class="el-icon--right">
                                            <arrow-down />
                                        </el-icon>
                                    </span>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item>
                                                <el-button type="text" size="mini" @click.stop="edit(node, data)">
                                                    编辑
                                                </el-button>
                                            </el-dropdown-item>
                                            <el-dropdown-item>
                                                <el-button type="text" size="mini" @click.stop="remove(node, data)">
                                                    删除
                                                </el-button>
                                            </el-dropdown-item>
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>

                            </span>
                        </div>
                    </template>
                </el-tree>
            </el-scrollbar>
        </el-card>

        <el-dialog title="由以下工程师生成目录" v-model="chaterDialogVisible" width="30%">

            <div class="dialog-body-content">
                <PersonCard />

                <el-input size="large" v-model="inputContent" placeholder="请输出针对于本章节内容的一些自定义要求"></el-input>

                <div class="upload-knowledge" style="margin-top:20px;">
                    <el-upload v-model:file-list="fileList" class="upload-demo"
                        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" :on-change="handleChange">
                        <el-button type="primary">上传知识库</el-button>
                        <template #tip>
                            <div class="el-upload__tip">
                               支持 DOC、DOCX、TXT、MD文件，且不超过 50MB。 
                            </div>
                        </template>
                    </el-upload>
                </div>
            </div>


            <div slot="footer" class="dialog-footer">
                <el-button @click="chaterDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">生 成</el-button>
            </div>
        </el-dialog>

        <!-- 材料上传界面 -->
        <ScreenUploadFile ref="uploadChildComp" />

        <!-- 选择人员 -->
        <el-dialog v-model="configAgentDialogVisible" :title="channelAgentConfigTitle" width="50%">
            <div class="dialog-body-content" style="text-align: center">
                 <el-transfer 
                    v-model="channelAgentList" 
                    filterable
                    style="text-align: left; width:100%; display: inline-block"
                    :titles="['源角色', '已选择']"
                    :format="{
                        noChecked: '${total}',
                        hasChecked: '${checked}/${total}',
                    }"
                    :filter-method="filterAgentMethod"
                    filter-placeholder="搜索角色" 
                    :data="agentList" />
            </div> 

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="configAgentDialogVisible = false">关闭</el-button>
                    <el-button type="primary" @click="handleCloseAgentConfig">确认</el-button>
                </div>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>

import { ref, reactive } from 'vue';
import { ElMessage , ElLoading } from 'element-plus';

import PersonCard from './components/personCard.vue';
import ScreenUploadFile from './screenUploadFile.vue'

import { listAll } from '@/api/base/im/user';

const uploadChildComp = ref(null)

const outline = ref([]);

const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])

const agentList = ref([])
const chaterDialogVisible = ref(false);
const editingNode = ref(null);
const editingLabel = ref('');

const inputContent = ref("")  // 生成内容的要求

const allowDrop = (draggingNode, dropNode, type) => {
    return type !== 'inner';
};

const allowDrag = (draggingNode) => {
    return draggingNode.data.id !== 1; // 不允许拖动根节点
};

const handleNodeClick = (data) => {
    console.log('Node clicked: ', data);
};

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
    editingLabel.value = data.label;
    dialogVisible.value = true;
};

const saveEdit = () => {
    if (editingNode.value) {
        editingNode.value.data.label = editingLabel.value;
        ElMessage.success('编辑成功');
        dialogVisible.value = false;
    }
};

const addChapter = () => {
    const newChapter = { id: Date.now(), label: `第${outline.value.length + 1}章`, children: [] };
    outline.value.push(newChapter);
};

/** 上传文档文件 */
function handleUploadFile() {
    uploadChildComp.value.handleOpenUpload(true);
}

// 监听上传文件
const fileList = ref([
  {
    name: 'food.jpeg',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  },
  {
    name: 'food2.jpeg',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  },
])

const handleChange = (uploadFile, uploadFiles) => {
  fileList.value = fileList.value.slice(-3)
}

/** 选择角色生成目录 */
function selectChaterGenerator() {
    chaterDialogVisible.value = true;
    console.log('selectChaterGenerator')
}

/** 获取到所有的角色信息 */
function handleListAllRole(){
  listAll().then(res => {
    for (let i = 0; i < res.data.length ; i++) {
        let item = res.data[i]

        agentList.value.push({
          key: item.id ,
          label: item.roleName , 
          disabled: false ,
      })
    }
  })
}

/** 配置成员 */
function configAgent(){

    configAgentDialogVisible.value = true ;
    console.log('configAgent')
    // channelAgentConfigTitle.value = row.channelName + "成员" ; 
    // currentChannelId.value = row.id ;
    // channelAgentList.value = row.roles ;
}

const getOutlineJson = () => {
    const jsonResult = JSON.stringify(outline.value, null, 2);
    ElMessage.success('目录 JSON 获取成功');
    console.log(jsonResult);
    // 可以在这里处理 JSON 数据，例如发送到服务器或显示在页面上
};

handleListAllRole()

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

.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;

    .description {
        margin-top: 0px;
        width: 100%;
        padding: 8px 0;
        padding-left: 5px;
        padding-right: 20px;
        white-space: pre-wrap;
        /* 使文本自动换行 */
    }

    .func-drapdown {
        padding: 10px;
        margin-right: 20px;
    }

}

.dialog-body-content {
    .upload-knowledge {
        margin-bottom: 20px;
    }

}

.dialog-body-content >>> .el-transfer-panel {
    width:350px;
}
.dialog-footer {
    text-align: right;
    margin-top: 20px;
}
</style>

<style>
.el-card__header {
    border: 0px;
}

.el-tree-node__content {
    height: auto;
}
</style>