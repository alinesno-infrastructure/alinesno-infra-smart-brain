<template>
    <div>
        <el-card class="box-card" shadow="never">
            <template #header>
                <div class="card-header">
                    <div style="display: flex;align-items: center;gap: 10px;;">
                        <i class="fa-solid fa-file-pdf"></i> {{ currentScreenInfo.screenName }}

                        <el-tooltip v-for="(item,index) in currentScreenInfo.chapterEditors" :key="index"
                            class="box-item" 
                            effect="dark" 
                            :content="item.roleName" 
                            placement="top">
                            <span class="edit-header-avatar">
                                <img :src="imagePathByPath(item.roleAvatar)" @click="selectChaterGenerator(item)" />
                            </span>
                        </el-tooltip>

                        <el-tooltip class="box-item" effect="dark" content="添加章节编写人员" placement="top">
                            <span class="edit-header-avatar"  @click="configAgent('chapter')">
                                <i class="fa-solid fa-user-plus"></i>
                            </span>
                        </el-tooltip>
                    </div>
                    <span>

                        <el-tooltip class="box-item" effect="dark" content="添加章节" placement="top">
                            <el-button type="primary" text bg size="large" @click="addChapter">
                                <i class="fa-solid fa-feather icon-btn"></i>
                            </el-button>
                        </el-tooltip>

                        <el-tooltip class="box-item" effect="dark" content="保存章节" placement="top">
                            <el-button type="success" text bg size="large" @click="getOutlineJson">
                                <i class="fa-solid fa-file-shield"></i>
                            </el-button>
                        </el-tooltip>

                        <!-- <el-button type="success" @click="getOutlineJson">获取目录 JSON</el-button> -->
                    </span>
                </div>
            </template>
            <el-scrollbar style="height:calc(100vh - 180px)">
                <div class="chapter-tree-content">
                    <el-tree :data="outline" 
                        node-key="id" 
                        default-expand-all 
                        draggable 
                        :allow-drop="allowDrop"
                        :allow-drag="allowDrag" 
                        @node-click="handleNodeClick" 
                        @node-contextmenu="handleNodeContextMenu">
                        <template #default="{ node, data }">
                            <div class="custom-tree-node" style="height:auto;">
                                <div style="display: flex;flex-direction: column;">
                                    <div style="font-size: 16px;font-weight: bold;color:#333">
                                        {{ node.label }}
                                    </div>
                                    <div class="description">
                                        <span style="color: #777;">{{ data.description }}</span>
                                    </div>
                                </div>
                                <span style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">
                                    <el-avatar v-if="data.chapterEditor" :size="20" :src="imagePathByPath(data.chapterEditorAvatar)"></el-avatar>
                                    <el-button type="text" icon="Plus" bg size="mini" @click.stop="append(data)"></el-button>
                                    <el-button type="text" icon="Edit" bg size="mini" @click.stop="edit(node, data)"></el-button>
                                    <el-button type="text" icon="Delete" bg size="mini" @click.stop="remove(node, data)"></el-button>
                                </span>
                            </div>
                        </template>
                    </el-tree>
                </div>
            </el-scrollbar>
        </el-card>

        <!-- 生成流式内容 -->
        <el-dialog title="由以下工程师生成目录" v-model="chaterDialogVisible" width="40%">

            <div v-if="!isRoleWriter" class="dialog-body-content">
                <!-- <PersonCard ref="personCardRef" /> -->
                <el-card class="person-card" shadow="never">
                    <el-row type="flex" justify="center" align="middle">
                    <el-col :span="16" class="info-container">
                        <h2>{{ person.roleName }}</h2>
                        <el-row>
                        <el-col :span="24">
                            <div style="margin-bottom: 10px;">
                            {{ person.responsibilities }}
                            </div>
                        </el-col>
                        <el-col :span="24">
                            <el-link :underline="false" :href="person.emailLink" target="_blank">
                            <el-icon><Message /></el-icon> {{ person.email }}
                            </el-link>
                        </el-col>
                        </el-row>
                    </el-col>
                    <el-col :span="8" class="avatar-container">
                        <el-avatar :size="100" :src="imagePathByPath(person.roleAvatar)"></el-avatar>
                    </el-col>
                    </el-row>
                </el-card>

                <el-input size="large" v-model="message" placeholder="请输出针对于本章节内容的一些自定义要求"></el-input>

                <!-- 
                <div class="upload-knowledge" style="margin-top:20px;">
                    <el-upload 
                        v-model:file-list="fileList" 
                        class="upload-demo"
                        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" 
                        :on-change="handleChange">
                        <el-button type="primary">上传知识库</el-button>
                        <template #tip>
                            <div class="el-upload__tip">
                               支持 DOC、DOCX、TXT、MD文件，且不超过 50MB。 
                            </div>
                        </template>
                    </el-upload>
                </div> 
                -->
            </div>
            <div v-if="isRoleWriter">
                <div style="display: flex;gap: 8px;align-items: center;margin-bottom: 20px;">
                    <el-avatar :size="40" :src="imagePathByPath(person.roleAvatar)"></el-avatar>
                    <p>{{ person.roleName }} 正在编写，请稍等</p>
                </div>
                <el-input type="textarea" :rows="20" resize="none" class="dialog-chapter-stream-content">
                </el-input>
            </div>

            <div class="dialog-footer">
                <el-button @click="chaterDialogVisible = false">取 消</el-button>
                <el-button v-if="!isRoleWriter" type="primary" @click="genStreamContent()">生成章节</el-button>
                <el-button v-if="isRoleWriter" type="danger" @click="confimStreamContent()">使用内容</el-button>
            </div>
        </el-dialog>


        <!-- 选择人员 -->
        <el-dialog v-model="configAgentDialogVisible" :title="channelAgentConfigTitle" width="900">
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

import { ref, reactive, nextTick } from 'vue';
import { ElMessage , ElLoading } from 'element-plus';

// import PersonCard from './components/personCard.vue';
// import { chatRole } from '@/api/base/im/roleChat'

import { listAll } from '@/api/base/im/user';
import {
    updateChapterEditor, 
    getScreen,
    saveChapter,
    chatRole,
} from '@/api/base/im/screen'

const route = useRoute();
const { proxy } = getCurrentInstance();

const emit = defineEmits(['setCurrentScreenInfo'])

// const personCardRef = ref(null)
const streamLoading = ref(null)

// 定义人物信息
const person = ref({
  roleAvatar: '' , 
  roleName: '张三',
  responsibilities: '主要负责与客户进行沟通，解答客户关于产品的各种疑问，提供专业的咨询服务，以帮助客户更好地理解和使用产',
  email: 'zhangsan@example.com',
});

const outline = ref([]);

const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])
const currentAgentConfigType = ref('chapter')

const currentScreenInfo = ref({
    id: 0,
    screenName: null , 
    screenDesc: null , 
    screenCount: 0,
    knowledgeId: null ,
    chapterEditors: [],
    contentEditors: [] 
})
const currentScreenId = ref(route.query.screenId)

const agentList = ref([])
const chaterDialogVisible = ref(false);
const editingNode = ref(null);
const editingLabel = ref('');

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

/** 生成内容 */
const genStreamContent = () => {
    // if (editingNode.value) {
    //     editingNode.value.data.label = editingLabel.value;
    //     ElMessage.success('编辑成功');
    //     dialogVisible.value = false;
    // }

  if (!message.value) {
    proxy.$modal.msgError("请输入消息内容.");
    return;
  }

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '任务执行中，请勿操作其它界面 ...',
    background: 'rgba(255, 255, 255, 0.1)',
  })

  let formData = {
    screenId: currentScreenId.value,
    message: message.value,
    roleId: person.value.id ,
  }

  chatRole(formData).then(res => {
    proxy.$modal.msgSuccess("发送成功");
    // pushResponseMessageList(res.data);

    // isRoleWriter.value = true ;
    chaterDialogVisible.value = false ;
    message.value = '';

    // 合并数组
    Array.prototype.push.apply(outline.value, res.data);

    closeStreamDialog();
  })
};

const addChapter = () => {
    const newChapter = { id: Date.now(), label: `第${outline.value.length + 1}章`, description: '节点内容描述信息', children: [] };
    outline.value.push(newChapter);
};


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
function selectChaterGenerator(item) {
    chaterDialogVisible.value = true;
    console.log('selectChaterGenerator')
    // console.log(' personCardRef.value = ' +  personCardRef.value)
    person.value = item ;
    person.value.email =  'zhangsan@example.com';
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
function configAgent(type){

    configAgentDialogVisible.value = true ;
    console.log('configAgent')

    currentAgentConfigType.value = type 
    channelAgentConfigTitle.value = "选择角色" ; 

    if(type === 'chapter'){
        console.log('currentScreenInfo.value.chapterEditors= ' + currentScreenInfo.value.chapterEditors);
        channelAgentList.value = currentScreenInfo.value.chapterEditors.map(item => item.id); 
    }else if(type === 'content'){
        console.log('currentScreenInfo.value.contentEditors= ' + currentScreenInfo.value.contentEditors);
        channelAgentList.value = currentScreenInfo.value.contentEditors.map(item => item.id); 
    }

}


/** 关闭选择角色弹窗 */
function handleCloseAgentConfig(){
  configAgentDialogVisible.value = false ;

  updateChapterEditor(currentScreenId.value , channelAgentList.value , currentAgentConfigType.value).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    handleGetScreen() ;
  })
  
}

/** 获取到场景详情 */
function handleGetScreen() {

    // streamLoading.value = ElLoading.service({
    //     lock: true,
    //     text: '初始化界面中 ...',
    //     background: 'rgba(255, 255, 255, 0.1)',
    // })

    getScreen(currentScreenId.value).then(res => {
      currentScreenInfo.value = res.data

      outline.value = res.data.chapterTree

      emit('setCurrentScreenInfo' , res.data)

    //   closeStreamDialog()

    //   proxy.$modal.msgSuccess("获取成功");
    })
}

const getOutlineJson = () => {
    const jsonResult = JSON.stringify(outline.value, null, 2);
    console.log(jsonResult);
    // 可以在这里处理 JSON 数据，例如发送到服务器或显示在页面上

    saveChapter(jsonResult , route.query.screenId).then(res => {
        proxy.$modal.msgSuccess("保存成功");
        handleGetScreen()
    })
};

const closeStreamDialog = () => {
  console.log('child method. ' + streamLoading.value)
  if(streamLoading.value){
    streamLoading.value.close()
  }
}

// 主动暴露childMethod方法
defineExpose({ 
    closeStreamDialog ,
    handleGetScreen,
    configAgent
})


nextTick(() => {
    console.log('screenId = ' + route.query.screenId) ;
    handleListAllRole()
    handleGetScreen()
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