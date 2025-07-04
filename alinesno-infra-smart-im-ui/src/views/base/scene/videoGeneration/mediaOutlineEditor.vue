<template>
    <div>
        <el-card class="box-card" shadow="never">
            <template #header>
                <div class="card-header">
                    <div style="display: flex;align-items: center;gap: 5px;">
                        <!-- <i class="fa-solid fa-file-pdf"></i>  -->
                        <!-- {{ currentScreenInfo.screenName }} -->

                        <span style="display: flex;gap: 5px;align-items: center;">
                            <i class="fa-solid fa-file-pdf"></i> 

                            <!-- 标题内容 -->
                            <EditableTitle 
                                v-if="currentScreenInfo"    
                                v-model:title="currentScreenInfo.screenName" 
                                class="article-edit-title" 
                            />
                        </span>

                        <el-tooltip v-for="(item,index) in currentScreenInfo.chapterEditors" :key="index"
                            class="box-item" 
                            effect="dark" 
                            :content="item.roleName" 
                            placement="top">
                            <span class="edit-header-avatar">
                                <img :src="imagePathByPath(item.roleAvatar)" @click="selectChaterGenerator(item)" />
                            </span>
                        </el-tooltip>

                        <!-- <el-tooltip class="box-item" effect="dark" content="添加章节编写人员" placement="top">
                            <span class="edit-header-avatar"  @click="configAgent('chapter')">
                                <i class="fa-solid fa-user-plus"></i>
                            </span>
                        </el-tooltip> -->
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
                                <div style="display: flex;padding-top:10px;flex-direction: column;">
                                    <div style="font-size: 16px;font-weight: bold;">镜头:{{ node.label }}
                                    </div>
                                    <div class="description">设计思考:<span style="color: #777;">{{ data.thought }}</span>
                                    </div>
                                    <div class="description" style="padding-top:0px;">镜头描述:<span style="color: #777;">{{ data.description }}</span>
                                    </div>
                                </div>
                                <span style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">

                                    <el-tooltip class="box-item" effect="dark" content="编辑章节标题" placement="left">
                                        <el-button type="text" icon="Edit" bg size="mini" @click.stop="edit(node, data)"></el-button>
                                    </el-tooltip>

                                    <el-tooltip class="box-item" effect="dark" content="编辑章节内容" placement="left">
                                        <el-button type="text" icon="EditPen" bg size="mini" @click.stop="editContent(node, data)"></el-button>
                                    </el-tooltip>

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
        <el-dialog title="由以下工程师生成目录" v-model="chaterDialogVisible" width="40%">

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

            <div class="dialog-footer">
                <el-button @click="chaterDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="genStreamContent()">生成章节</el-button>
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
                    :data="agentList">

                </el-transfer>
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

import EditableTitle from './components/EditableTitle.vue'
import { listAll } from '@/api/base/im/user';

import {
    // updateChapterEditor, 
    getScene,
    // saveChapter,
    // chatRole,
} from '@/api/base/im/scene'

const route = useRoute();
const { proxy } = getCurrentInstance();

const emit = defineEmits(['setCurrentScreenInfo' , 'editContent'])

const streamLoading = ref(null)

// 定义人物信息
const person = ref({
  roleAvatar: '' , 
  roleName: '',
  responsibilities: '',
  email: 'zhangsan@example.com',
});

const outline = ref(
    [
        {
            "label": "店外景",
            "merchantAdvertisement": "真好吃",
            "thought": "先展示店铺外观，给观众第一印象。",
            "MediaSpeechTextArray": [
                "欢迎来到‘真好吃’，这里有着传承多年的老友粉。",
                "在这家店里，您能品尝到正宗的老友粉。",
                "走进‘真好吃’，感受传统美食的魅力。"
            ],
            "description": "使用手机拍摄店铺正面全景，包括招牌。早晨阳光下，顾客陆续进店。",
            "duration": "5秒"
        },
        {
            "label": "店内环境",
            "merchantAdvertisement": "真好吃",
            "thought": "接着转换到内部环境，展现店铺氛围。",
            "MediaSpeechTextArray": [
                "温馨的用餐环境，让每一餐都成为享受。",
                "舒适的座位，热情的服务，这里是食客们的天堂。",
                "店内的每一个角落，都充满了故事。"
            ],
            "description": "用单反相机拍摄中景，捕捉店内装饰、顾客用餐场景。",
            "duration": "4秒"
        },
        {
            "label": "厨师准备",
            "merchantAdvertisement": "真好吃",
            "thought": "通过厨师准备食材的过程，强调食品的新鲜度。",
            "MediaSpeechTextArray": [
                "新鲜的食材是美味的关键。",
                "每一份老友粉都是用心制作的结果。",
                "精心挑选的原料，只为带给您最纯正的味道。"
            ],
            "description": "特写镜头，记录厨师处理食材的手部动作，使用手机或单反。",
            "duration": "3秒"
        },
        {
            "label": "烹饪过程",
            "merchantAdvertisement": "真好吃",
            "thought": "展示烹饪过程，增加食欲。",
            "MediaSpeechTextArray": [
                "看，这便是老友粉的灵魂所在。",
                "快火翻炒，香味四溢。",
                "每一道工序，都是对传统的致敬。"
            ],
            "description": "用手机慢动作拍摄厨师快速翻炒老友粉的细节。",
            "duration": "6秒"
        },
        {
            "label": "成品特写",
            "merchantAdvertisement": "真好吃",
            "thought": "聚焦于成品，突出卖相。",
            "MediaSpeechTextArray": [
                "色泽诱人，香气扑鼻的老友粉。",
                "一碗碗热腾腾的老友粉，等待着您的品尝。",
                "这就是真好吃的老友粉，光是看着就让人垂涎欲滴。"
            ],
            "description": "使用单反相机拍摄老友粉成品的特写镜头，注意光线角度，使食物看起来更加吸引人。",
            "duration": "5秒"
        },
        {
            "label": "顾客享用",
            "merchantAdvertisement": "真好吃",
            "thought": "通过顾客的表情传达美食的好吃。",
            "MediaSpeechTextArray": [
                "看那满意的笑容，就知道有多美味。",
                "每一口都是对老友粉的赞美。",
                "顾客的笑容是最好的证明。"
            ],
            "description": "用手机捕捉顾客享用老友粉时的满意表情，选择合适的时机进行拍摄。",
            "duration": "4秒"
        },
        {
            "label": "老友粉特色",
            "merchantAdvertisement": "真好吃",
            "thought": "讲解老友粉的特色，加深记忆点。",
            "MediaSpeechTextArray": [
                "独特的调料，让味道更加浓郁。",
                "老友粉的秘诀在于它的汤底。",
                "细嚼慢咽，感受每一丝细腻。"
            ],
            "description": "用无人机航拍厨房一角，然后切换至老友粉配料的特写。",
            "duration": "7秒"
        },
        {
            "label": "历史回顾",
            "merchantAdvertisement": "真好吃",
            "thought": "以回忆的形式介绍老友粉的历史。",
            "MediaSpeechTextArray": [
                "数十年的传统，造就了今天的美味。",
                "从街边小吃到餐桌常客，老友粉的故事还在继续。",
                "每一代人都有自己的老友粉回忆。"
            ],
            "description": "使用电脑录屏结合老照片，讲述老友粉的历史变迁。",
            "duration": "6秒"
        }
    ]
);

const dialogVisible = ref(false)

const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])
const currentAgentConfigType = ref('chapter')

const currentScreenInfo = ref({
    id: 0,
    screenName: '业务场景名称', 
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
const genStreamContent = () => {

  if (!message.value) {
    proxy.$modal.msgError("请输入消息内容.");
    return;
  }

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '任务执行中，请勿操作其它界面 ...',
    background: 'rgba(0, 0, 0, 0.2)',
  })

  let formData = {
    screenId: currentScreenId.value,
    message: message.value,
    roleId: person.value.id ,
  }

  chatRole(formData).then(res => {
    proxy.$modal.msgSuccess("发送成功");
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
    getScreen(currentScreenId.value).then(res => {
      currentScreenInfo.value = res.data
    //   outline.value = res.data.chapterTree
      emit('setCurrentScreenInfo' , res.data)
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