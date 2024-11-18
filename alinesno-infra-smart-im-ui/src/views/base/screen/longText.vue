<template>
    <div class="app-container" style="background-color: #fff;padding-top:10px;">
        <el-row>
            <el-col :span="10">
                <!-- 目录大纲编辑界面 -->
                <OutlineEditor 
                    ref="OutlineEditorRef"
                    @setCurrentScreenInfo="setCurrentScreenInfo"
                    @editContent="editContent"
                     />
            </el-col>
            <el-col :span="14">
                <div class="chapter-edit">
                    <el-card class="box-card" shadow="never">
                        <template #header>
                            <div class="card-header">
                                <div style="display: flex;align-items: center;gap: 10px;;">
                                    章节编辑
                                    <el-tooltip v-for="(item,index) in currentScreenInfo.contentEditors" :key="index"
                                        class="box-item" 
                                        effect="dark" 
                                        :content="item.roleName" 
                                        placement="top">
                                        <span class="edit-header-avatar">
                                            <img :src="imagePathByPath(item.roleAvatar)" @click="openChapterSelectionDialog(item)" />
                                        </span>
                                    </el-tooltip>

                                    <el-tooltip class="box-item" effect="dark" content="添加内容编写人员" placement="top">
                                        <span class="edit-header-avatar" @click="configAgent()">
                                            <i class="fa-solid fa-user-plus"></i>
                                        </span>
                                    </el-tooltip>
                                </div>
                                <div>
                                    <el-tooltip class="box-item" effect="dark" content="执行文档生成" placement="top">
                                        <el-button type="primary" text bg size="large" @click="genChapterContent()">
                                            <i class="fa-solid fa-ferry"></i>
                                        </el-button>
                                    </el-tooltip>
                                    <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top">
                                        <el-button type="danger" text bg size="large" @click="handleUploadFile">
                                            <i class="fa-solid fa-file-word icon-btn"></i>
                                        </el-button>
                                    </el-tooltip>
                                    <el-tooltip class="box-item" effect="dark" content="保存章节" placement="top">
                                        <el-button type="primary" text bg size="large" @click="onSubmitChapter">
                                            <i class="fa-solid fa-floppy-disk"></i>
                                        </el-button>
                                    </el-tooltip>

                                    <el-tooltip class="box-item" effect="dark" content="导出文档" placement="top">
                                        <el-button type="primary" text bg size="large" @click="expertScreen">
                                            <i class="fa-solid fa-download"></i>
                                        </el-button>
                                    </el-tooltip>

                                    <!-- <el-button type="primary" @click="onSubmitChapter">保存</el-button> -->
                                    <!-- <el-button type="danger" @click="onDownloadContent">下载</el-button> -->
                                </div>
                            </div>
                        </template>
                        <el-form :model="form" label-width="100px" label-position="top" v-loading="loading">
                            <el-form-item>
                                <el-input disabled="disabled" v-model="form.title" placeholder="在执行当中的章节名称"></el-input>
                            </el-form-item>
                            <el-form-item label="章节内容">
                                <!-- <el-input type="textarea" v-model="form.content" :rows="31" resize="none" placeholder="请输出针对于本章节内容的一些自定义要求"></el-input> -->
                                 <ChapterEditor ref="chapterEditorRef" />
                            </el-form-item>
                        </el-form>
                    </el-card>
                </div>
            </el-col>
        </el-row>

        <!-- 选择章节对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="chapterEditTitle"
            append-to-body
            width="700px"
            >
            <div class="user-info">
                <img v-if="currentUser.roleAvatar" class="avatar" :src="imagePathByPath(currentUser.roleAvatar)" />
                角色能力: {{ currentUser.responsibilities }}
            </div>
            <el-scrollbar style="height:calc(100vh - 400px)">
                <div class="chapter-tree-content">
                    <el-tree :data="outline" 
                        node-key="id" 
                        default-expand-all 
                        show-checkbox
                        :allow-drop="allowDrop"
                        :allow-drag="allowDrag" 
                        @node-click="handleNodeClick" 
                        @node-contextmenu="handleNodeContextMenu"
                        check-strictly
                        ref="chapterSelectionTree">
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
                                </span>
                            </div>
                        </template>
                    </el-tree>
                </div>
            </el-scrollbar>
            
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="assignChaptersToUser">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 材料上传界面 -->
        <ScreenUploadFile ref="uploadChildComp" />

    </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'
import ChapterEditor from './chapterEditor'

import ScreenUploadFile from './screenUploadFile.vue'

import { 
    updateChapterContentEditor , 
    getChapterByRole , 
    getChapterContent , 
    updateChapterContent,
    chatRoleSync,
    uploadOss 
} from '@/api/base/im/screen'

import { ElMessage } from 'element-plus';

import OutlineEditor from './outlineEditor.vue'

const route = useRoute();
const { proxy } = getCurrentInstance();

const currentScreenInfo = ref([])

const loading = ref(false)
const chapterEditTitle = ref("")
const dialogVisible = ref(false)
const chapterSelectionTree = ref(null);
const currentUser = ref(null);

// 定义人物信息
const person = ref({
  roleAvatar: '' , 
  roleName: '张三',
  responsibilities: '主要负责与客户进行沟通，解答客户关于产品的各种疑问，提供专业的咨询服务，以帮助客户更好地理解和使用产',
  email: 'zhangsan@example.com',
});


const chapterEditorRef = ref(null)
const uploadChildComp = ref(null)
const OutlineEditorRef = ref(null); // 滚动条的处理_starter

const streamLoading = ref(null)
const screenId = ref(route.query.screenId)

const totalNodes = ref(0);

const form = reactive({
    id: 0 ,
    title: '',
    content: ''
});

// 文章目录结构数据
const outline = ref([]);

const onSubmitChapter = () => {
    // 这里处理表单提交逻辑
    console.log('提交的数据:', form);

    let data = {
        id: form.id ,
        title: form.title,
        content: chapterEditorRef.value.getData() 
    }

    updateChapterContent(data).then(res => {
        ElMessage.success('保存'+form.title+'成功！');
    })

};

/** 导出word文档 */
function expertScreen(){
    uploadOss(screenId.value).then(res => {
        window.open(res.data)
    })
}

/** 上传文档文件 */
function handleUploadFile() {
    uploadChildComp.value.handleOpenUpload(true);
}

function configAgent(){
    OutlineEditorRef.value.configAgent('content');
}

function setCurrentScreenInfo(data){
    currentScreenInfo.value = data
    outline.value = data.chapterTree
}

// 打开选择章节对话框
const openChapterSelectionDialog = (role) => {
    chapterEditTitle.value = "请选择["+role.roleName+"]需要编辑的章节"

    person.value = role ;
    person.value.email =  'zhangsan@example.com';
    
    currentUser.value = role;
    currentUser.value.selectedChapters = [];
    dialogVisible.value = true;

    // 初始化已选择节点
    nextTick(() => {
        getChapterByRole(role.id, screenId.value).then(res => {
            if(res.data){
                let selectKey = res.data.map(item => item.id);
                console.log('selectKey = ' + selectKey);
                chapterSelectionTree.value.setCheckedKeys(selectKey) ;
            }
        })

    });

};

// 定义一个异步函数来调用 chatRoleSync
const genChapterContent = async () => {
  try {
    totalNodes.value = countNodes(outline.value); 
    console.log('文档总数量 = ' + totalNodes.value);

    // 将树转换成列表形式
    const nodeList = [];
    const flattenTree = (nodes) => {
      for (let node of nodes) {
        nodeList.push(node);
        if (node.children && node.children.length) {
          flattenTree(node.children);
        }
      }
    };
    flattenTree(outline.value);

     // 开始生成
     streamLoading.value = ElLoading.service({
        lock: true,
        background: 'rgba(255, 255, 255, 0.5)',
        customClass: 'custom-loading'
     });

    // 遍历输出每个节点的信息
    for (let i = 0; i < nodeList.length; i++) {
      const node = nodeList[i];
      console.log(`节点 ${i + 1}: ID = ${node.id}, Label = ${node.label}`);
      let processMsg = ` ${i + 1}: 章节:${node.label}`;

      let text = processMsg + ' 任务生成中，还有【'+(totalNodes.value - i)+'】篇';
      streamLoading.value.setText(text)

      form.title = node.label;

      let formData = {
        screenId: screenId.value,
        chapterTitle: node.label,
        chapterDescription: node.description,
        chapterId : node.id ,
      }
      const result = await chatRoleSync(formData);
      chapterEditorRef.value.setData(result.data) ;
      
    }
  } catch (error) {
    console.error('Error fetching data:', error);
    proxy.$modal.msgError("生成失败");
    streamLoading.value.close();
  }

    streamLoading.value.close();
};

// 计算总节点数
const countNodes = (nodes) => {
  let count = 0;
  for (let node of nodes) {
    count++;
    if (node.children && node.children.length) {
      count += countNodes(node.children);
    }
  }
  return count;
};

// 递归查找节点
const findNodeById = (nodes, id) => {
  for (let node of nodes) {
    console.log('nodeName = ' + node.label + ', id = ' + node.id) ; 
    if (node.id === id) {
        return node;
    }
    if (node.children && node.children.length) {
      const foundNode = findNodeById(node.children, id);
      if (foundNode) {
        return foundNode;
      }
    }
  }
  return null;
};

/** 编辑章节内容 */
const editContent = (node , data) => {
    console.log('node = ' + node)
    console.log('data = ' + data)

    let chapterId = data.id ;

    form.id = data.id ;
    form.title = node.label;

    getChapterContent(chapterId).then(res => {
        console.log('res = ' + JSON.stringify(res))
        // form.content = res.data ;
        chapterEditorRef.value.setData(res.data);
        loading.value = false ;
    })

}

// 分配章节给用户
const assignChaptersToUser = () => {
  const checkedNodes = chapterSelectionTree.value.getCheckedNodes(false, true);
  console.log('newChapterIds= ' + checkedNodes)

  const newChapterIds = checkedNodes.map(node => node.id);
  console.log('newChapterIds = ' + newChapterIds);

  currentUser.value.selectedChapters = [...new Set([...currentUser.value.selectedChapters, ...newChapterIds])];
  console.log('currentUser.value.selectedChapters=' + currentUser.value.selectedChapters);

  let data = {
      screenId: screenId.value,
      roleId: currentUser.value.id,
      chapters: currentUser.value.selectedChapters
  }

  updateChapterContentEditor(data).then(res => {
    console.log(res)
    proxy.$modal.msgSuccess("分配成功");
    dialogVisible.value = false;

    OutlineEditorRef.value.handleGetScreen();
  })

};

</script>

<style lang="scss" scoped>

// 定义一些常用的变量
$spacing: 10px;
$avatar-size: 30px;

.user-info {
    padding: $spacing;
    margin-bottom: $spacing;
    display: flex;
    gap: $spacing;

    .avatar {
        width: $avatar-size;
        height: $avatar-size;
        border-radius: 50%;
    }
}

.chapter-edit {
    margin: 0px;

    .box-card {
        border: 0px;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .el-form {
        .el-form-item {
            &.el-form-item--small {
                margin-bottom: 15px;
            }

            .el-input,
            .el-textarea {
                width: 100%;
            }
        }
    }

    .el-button+.el-button {
        margin-left: 10px;
    }

    .scroll-panel {
        height: calc(100vh - 310px);
        width: 100%;
        border: 1px solid #dcdfe6 ;
        border-radius: 4px;
    }

    .chater-text-body {
        background: #fafafa;
        padding: 10px;
        height: 100%;
        width: 100%;
        border-radius: 4px;
    }
}

.inner-smart-container {
    max-width: 100% ! important;

    .robot-chat-windows {
        border: 0px !important;
    }

    .inner-robot-chat-body {
        height: calc(100vh - 100px);
    }
}

.scroll-panel {
    padding-bottom: 10px;
    float: left;
    width: 100%;
    height: calc(100% - 55px);
    overflow: hidden;
}

.show-tools {
    visibility: visible;
}

.hide-tools {
    visibility: hidden;
}

.robot-chat-ai-say-box {
    float: left;
    width: 100%;

    .say-right-window {
        float: right !important;

        .say-message-info {
            text-align: right !important;
        }
    }

    .chat-ai-header {
        float: left;
        width: 50px;
        margin: 10px;

        .header-images {
            padding: 5px;

            img {
                width: 100%;
                border-radius: 50%;
            }

        }

    }

    .chat-ai-say-body {
        float: left;
        margin-top: 15px;
        font-size: 14px;

        .say-message-info {
            font-size: 13px;
            margin-bottom: 5px;
            color: #999;
        }

        .say-message-body {
            padding: 10px;
            line-height: 1.4rem;
            border-radius: 3px;
            background: #fafafa;
        }

    }

    .message-list {
        margin-top: 20px;
    }

    .message {
        margin-bottom: 8px;
    }

}

.dialog-footer {
    text-align: right;
    margin-top: 19px;
}

</style>
