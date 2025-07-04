<template>


    <div class="ppt-pager-container">

        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="280px" class="ppt-pager-aside">
                <FunctionList />
            </el-aside>

            <el-main class="ppt-pager-main">
                <div>
                    <el-row>
                        <el-col :span="9">
                            <!-- 目录大纲编辑界面 -->
                            <MediaOutlineEditor ref="OutlineEditorRef" @setCurrentScreenInfo="setCurrentScreenInfo"
                                @editContent="editContent" />
                        </el-col>
                        <el-col :span="15" class="miedia-editor-panel">
                            <div class="chapter-edit">
                                <el-card class="box-card" shadow="never">
                                    <template #header>
                                        <div class="card-header">
                                            <div style="display: flex;align-items: center;gap: 5px;">
                                                镜头编辑
                                                <el-tooltip v-for="(item, index) in currentScreenInfo.contentEditors"
                                                    :key="index" class="box-item" effect="dark" :content="item.roleName"
                                                    placement="top">
                                                    <span class="edit-header-avatar">
                                                        <img :src="imagePathByPath(item.roleAvatar)"
                                                            @click="openChapterSelectionDialog(item)" />
                                                    </span>
                                                </el-tooltip>

                                                <!-- 
                                                <el-tooltip class="box-item" effect="dark" content="添加内容编写人员"
                                                    placement="top">
                                                    <span class="edit-header-avatar" @click="configAgent()">
                                                        <i class="fa-solid fa-user-plus"></i>
                                                    </span>
                                                </el-tooltip> 
                                                -->

                                            </div>
                                            <div>
                                                <el-tooltip class="box-item" effect="dark" content="执行文档生成"
                                                    placement="top">
                                                    <el-button type="primary" text bg size="large"
                                                        @click="genChapterContent()">
                                                        <i class="fa-solid fa-ferry"></i>
                                                    </el-button>
                                                </el-tooltip>
                                                <el-tooltip class="box-item" effect="dark" content="上传文档文件"
                                                    placement="top">
                                                    <el-button type="danger" text bg size="large"
                                                        @click="handleUploadFile">
                                                        <i class="fa-solid fa-file-word icon-btn"></i>
                                                    </el-button>
                                                </el-tooltip>
                                                <el-tooltip class="box-item" effect="dark" content="保存章节"
                                                    placement="top">
                                                    <el-button type="primary" text bg size="large"
                                                        @click="onSubmitChapter">
                                                        <i class="fa-solid fa-floppy-disk"></i>
                                                    </el-button>
                                                </el-tooltip>

                                                <el-tooltip class="box-item" effect="dark" content="导出文档"
                                                    placement="top">
                                                    <el-button type="primary" text bg size="large"
                                                        @click="expertScreen">
                                                        <i class="fa-solid fa-download"></i>
                                                    </el-button>
                                                </el-tooltip>

                                                <el-tooltip class="box-item" effect="dark" content="全局配置"
                                                    placement="top">
                                                    <el-button type="danger" text bg size="large" @click="globalConfig">
                                                        <i class="fa-solid fa-pen-nib"></i>
                                                    </el-button>
                                                </el-tooltip>

                                                <!-- <el-button type="primary" @click="onSubmitChapter">保存</el-button> -->
                                                <!-- <el-button type="danger" @click="onDownloadContent">下载</el-button> -->
                                            </div>
                                        </div>
                                    </template>
                                    <el-scrollbar style="height:calc(100vh - 180px)">
                                        <el-form :model="form" label-width="100px" label-position="top"
                                            v-loading="loading">
                                            <el-form-item label="镜头名称">
                                                <el-input v-model="form.title" placeholder="在执行当中的章节名称"
                                                    style="width:50%"></el-input>
                                            </el-form-item>
                                            <el-form-item>
                                                <!-- <el-input type="textarea" :rows="1" resize="none" placeholder="请输入镜像镜像口播内容" /> -->
                                                <el-row :gutter="20" style="width:100%">
                                                    <el-col :span="24">
                                                        <div v-for="(title, index) in form.titles" :key="index"
                                                            style="margin-bottom: 16px;">
                                                            <el-form-item :label="index === 0 ? '视频口播内容' : ''">
                                                                <el-input v-model="form.titles[index]"
                                                                    style="width:calc(100% - 70px)"
                                                                    placeholder="请输入视频口播内容" />
                                                                <el-button type="danger" @click="removeTitle(index)"
                                                                    style="margin-left: 8px;"
                                                                    :disabled="form.titles.length <= 1">删除</el-button>
                                                            </el-form-item>
                                                        </div>
                                                        <div style="margin-bottom:20px;text-align: right;">
                                                            <el-button type="primary" icon="Plus"
                                                                @click="addTitle">添加标题文案</el-button>
                                                        </div>
                                                    </el-col>
                                                </el-row>
                                            </el-form-item>
                                            <!-- <el-form-item label="媒体类型" prop="resource">
                                <el-radio-group v-model="form.resource">
                                    <el-radio value="Sponsorship">视频</el-radio>
                                    <el-radio value="Venue">图片</el-radio>
                                </el-radio-group>
                            </el-form-item> -->
                                            <el-form-item label="特效" prop="type">
                                                <el-checkbox-group v-model="form.type">
                                                    <el-checkbox value="Online activities" name="type">
                                                        自动旋转
                                                    </el-checkbox>
                                                    <el-checkbox value="Promotion activities" name="type">
                                                        旋转
                                                    </el-checkbox>
                                                    <el-checkbox value="Offline activities" name="type">
                                                        缩放
                                                    </el-checkbox>
                                                    <el-checkbox value="Simple brand exposure" name="type">
                                                        镜像
                                                    </el-checkbox>
                                                </el-checkbox-group>
                                            </el-form-item>
                                            <el-form-item label="广告文案">
                                                <el-input placeholder="镜像中出现的商家广告信息，比如logo信息之类" resize="none"
                                                    style="width:50%" />
                                            </el-form-item>
                                            <el-form-item label="素材管理">
                                                <div style="width: 100%;text-align: right;">
                                                    <el-button type="primary" @click="addMaterial" icon="Plus"
                                                        style="position: absolute;right: 20px;top: -30px;">添加素材</el-button>
                                                    <div
                                                        style="display: flex; width:90%; flex-wrap: wrap;justify-content: flex-start;align-items: center;gap: 10px;margin-top: 10px;">
                                                        <div v-for="(item, index) in videoMaterials" :key="index"
                                                            style="display: flex;align-items: flex-end;flex-direction: column;align-content: space-between;">
                                                            <img :src="item.cover" class="cover-image" />
                                                            <div style="padding: 0px;">
                                                                <el-button type="primary" text
                                                                    @click="() => removeMaterial(index)">删除</el-button>
                                                            </div>
                                                        </div>
                                                        <div v-if="videoMaterials.length === 0">暂无数据</div>
                                                    </div>

                                                </div>

                                                <!-- <el-upload action="#" list-type="picture-card" :on-preview="handlePictureCardPreview"
                                    :on-remove="handleRemove" :auto-upload="false" :limit="1" :on-change="handleChange">
                                    <i class="el-icon-plus"></i>
                                </el-upload>
                                <el-dialog :visible.sync="mediaDialogVisible">
                                    <img width="100%" :src="dialogImageUrl" alt="" />
                                </el-dialog> -->
                                            </el-form-item>
                                        </el-form>
                                    </el-scrollbar>
                                </el-card>
                            </div>
                        </el-col>
                    </el-row>

                    <!-- 选择章节对话框 -->
                    <el-dialog v-model="dialogVisible" :title="chapterEditTitle" append-to-body width="700px">
                        <div class="user-info">
                            <img v-if="currentUser.roleAvatar" class="avatar"
                                :src="imagePathByPath(currentUser.roleAvatar)" />
                            角色能力: {{ currentUser.responsibilities }}
                        </div>
                        <el-scrollbar style="height:calc(100vh - 400px)">
                            <div class="chapter-tree-content">
                                <el-tree :data="outline" node-key="id" default-expand-all show-checkbox
                                    :allow-drop="allowDrop" :allow-drag="allowDrag" @node-click="handleNodeClick"
                                    @node-contextmenu="handleNodeContextMenu" check-strictly ref="chapterSelectionTree">
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
                                            <span
                                                style="margin-right: 10px;display: flex;align-items: center;gap: 5px;">
                                                <el-avatar v-if="data.chapterEditor" :size="20"
                                                    :src="imagePathByPath(data.chapterEditorAvatar)"></el-avatar>
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

                    <!-- 全局配置对话框 -->
                    <MediaGlobalConfigPanel ref="globalConfigPanelComp" />

                </div>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'
// import ChapterEditor from './chapterEditor'

// import ScreenUploadFile from './screenUploadFile.vue'
// import MediaGlobalConfigPanel from './mediaGlobalConfigPanel.vue'

import {
    // updateChapterContentEditor,
    getChapterByRole,
    getChapterContent,
    updateChapterContent,
    // chatRoleSync,
    uploadOss
} from '@/api/base/im/scene'

import { ElMessage } from 'element-plus';

import MediaOutlineEditor from './mediaOutlineEditor.vue'
import FunctionList from './functionList'

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
    roleAvatar: '',
    roleName: '张三',
    responsibilities: '主要负责与客户进行沟通，解答客户关于产品的各种疑问，提供专业的咨询服务，以帮助客户更好地理解和使用产',
    email: 'zhangsan@example.com',
});


const chapterEditorRef = ref(null)
const uploadChildComp = ref(null)
const OutlineEditorRef = ref(null); // 滚动条的处理_starter
const globalConfigPanelComp = ref(null)

const streamLoading = ref(null)
const screenId = ref(route.query.screenId)

const totalNodes = ref(0);

const form = reactive({
    id: 0,
    titles: [''], // 初始化时至少有一个空标题
    title: '',
    content: ''
});

// 文章目录结构数据
const outline = ref([]);

// 初始视频素材数据
const videoMaterials = ref([
    { cover: 'http://training-static.linesno.com/public/2024-12-29/67706f8de4b03e1aaa099357.png' },
    { cover: 'http://training-static.linesno.com/public/2024-12-18/676299eee4b03e1aaa09931c.png' }
]);

// 对话框相关状态
const dialogImageUrl = ref('');
const mediaDialogVisible = ref(false);

// 添加新素材的方法
const addMaterial = () => {
    // 这里仅做示意，实际上应该从素材库选择或上传新素材
    videoMaterials.value.push({ cover: 'http://training-static.linesno.com/public/2024-12-18/6761fcc6e4b03e1aaa0992f5.png' });
};

// 删除指定索引的素材方法
const removeMaterial = (index) => {
    videoMaterials.value.splice(index, 1);
};

// 处理移除文件
const handleRemove = (file, fileList) => {
    console.log(file, fileList);
};

// 预览图片
const handlePictureCardPreview = (file) => {
    dialogImageUrl.value = file.url;
    dialogVisible.value = true;
};

// 当选择文件时触发
const handleChange = (file, fileList) => {
    if (file.raw) {
        const isJPGorPNG = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png';
        const isLt2M = file.raw.size / 1024 / 1024 < 2;

        if (!isJPGorPNG) {
            ElMessage.error('上传封面图片只能是 JPG 或 PNG 格式!');
        }
        if (!isLt2M) {
            ElMessage.error('上传封面图片大小不能超过 2MB!');
        }

        if (isJPGorPNG && isLt2M) {
            // 假设这里将新封面的URL赋值给新素材对象
            addMaterial();
        }
    }
};

const onSubmitChapter = () => {
    // 这里处理表单提交逻辑
    console.log('提交的数据:', form);

    let data = {
        id: form.id,
        title: form.title,
        content: chapterEditorRef.value.getData()
    }

    updateChapterContent(data).then(res => {
        ElMessage.success('保存' + form.title + '成功！');
    })

};

/** 导出word文档 */
function expertScreen() {
    uploadOss(screenId.value).then(res => {
        window.open(res.data)
    })
}

/** 上传文档文件 */
function handleUploadFile() {
    uploadChildComp.value.handleOpenUpload(true);
}

// 添加新的标题输入框
const addTitle = () => {
    form.titles.push('')
}

// 删除指定索引的标题
const removeTitle = (index) => {
    form.titles.splice(index, 1)
}

function configAgent() {
    OutlineEditorRef.value.configAgent('content');
}

function setCurrentScreenInfo(data) {
    currentScreenInfo.value = data
    outline.value = data.chapterTree
}

// 打开选择章节对话框
const openChapterSelectionDialog = (role) => {
    chapterEditTitle.value = "请选择[" + role.roleName + "]需要编辑的章节"

    person.value = role;
    person.value.email = 'zhangsan@example.com';

    currentUser.value = role;
    currentUser.value.selectedChapters = [];
    dialogVisible.value = true;

    // 初始化已选择节点
    nextTick(() => {
        getChapterByRole(role.id, screenId.value).then(res => {
            if (res.data) {
                let selectKey = res.data.map(item => item.id);
                console.log('selectKey = ' + selectKey);
                chapterSelectionTree.value.setCheckedKeys(selectKey);
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

            let text = processMsg + ' 任务生成中，还有【' + (totalNodes.value - i) + '】篇';
            streamLoading.value.setText(text)

            form.title = node.label;

            let formData = {
                screenId: screenId.value,
                chapterTitle: node.label,
                chapterDescription: node.description,
                chapterId: node.id,
            }
            const result = await chatRoleSync(formData);
            chapterEditorRef.value.setData(result.data);

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
        console.log('nodeName = ' + node.label + ', id = ' + node.id);
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

/** 全局配置 */
const globalConfig = () => {
    globalConfigPanelComp.value.openGlobalConfigPanel();
};

/** 编辑章节内容 */
const editContent = (node, data) => {
    console.log('node = ' + node)
    console.log('data = ' + data)

    let chapterId = data.id;

    form.id = data.id;
    form.title = node.label;

    getChapterContent(chapterId).then(res => {
        console.log('res = ' + JSON.stringify(res))
        // form.content = res.data ;
        chapterEditorRef.value.setData(res.data);
        loading.value = false;
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
        height: calc(100vh - 210px);
        width: 100%;
        border: 1px solid #dcdfe6;
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

.cover-image {
    width: 160px;
    border-radius: 2px;
    height: 100px;
}

.ppt-pager-container {
    background: #fff;
    height: calc(100vh - 60px);

    .review-footer {
        padding: 10px;
        font-size: 14px;
        background: #fafafa;
        border-radius: 8px;
        text-align: left;
        color: #555;
        margin-top: 10px;
    }

    .main-content {
        display: flex;
        flex-direction: column;
        padding-top: calc(1vh);
        // text-align: center;
        // max-width: 90%;
        margin: auto;
        padding-left: 20px;
        padding-right: 20px;

        .example-result-section {
            padding: 12px;
            border-radius: 10px;
            font-size: 14px;
            text-align: left;
            color: #585a73;
            display: flex;
            flex-direction: row;
            width: 100%;
            align-items: center;
            gap: 10px;
        }

        .title-section {
            display: flex;
            flex-direction: column;
            text-align: center;
            align-items: flex-start;

            .title {
                color: #2C2C36;
                font-weight: 600;
                font-size: 28px;
                margin-bottom: 10px;
                line-height: 40px;
            }

            .description {
                margin-top: 10px;
                color: #8F91A8;
                font-weight: 400;
                font-size: 16px;
                line-height: 24px;
            }
        }

        .input-button-section {
            display: flex;
            gap: 10px;
            position: relative;
            box-sizing: border-box;
            width: 100%;
            border-radius: 15px;
            // box-shadow: rgba(54, 54, 73, 0.06) 0px 12px 24px -16px, rgba(74, 80, 96, 0.12) 0px 12px 40px, rgba(44, 44, 54, 0.02) 0px 0px 1px;
            transition: 0.3s;
            background: rgb(255, 255, 255);
            padding: 10px !important;
            border: 1px solid rgb(232, 234, 242);
            margin-top: 30px;
            margin-bottom: 10px;
            align-items: flex-start;
            flex-direction: column;

            .input-box {
                width: 100%;
                height: 50px;
                border: 0px !important;
                margin-bottom: 0px;
            }
        }

    }

    .review-footer-message {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-top: 10px;
        height: 36px;
        padding: 12px 0px;
        text-align: center;

        .footer-message {
            margin-bottom: 4px;
            color: #C8CAD9;
            font-size: 12px;
            line-height: 12px;
        }
    }

    .review-question-preview-title {
        padding: 10px;
        text-align: left;
        font-weight: bold;
        margin-left: 20px;
        margin-right: 10px;
        margin-bottom: 10px;
        border-radius: 10px;
        background: #fafafa;
        color: #444;
        font-size: 15px;
        display: flex;
        align-content: center;
        align-items: center;
        justify-content: space-between;
    }

    .review-question-preview {
        height: calc(100vh - 170px);
        margin-left: 20px;
        border-radius: 8px;
        background: #fafafa;
        border: 1px solid #e8eaf2;
        overflow: hidden;
    }

}

.ppt-pager-aside {
    padding: 0px;
    border-right: 1px solid #f2f3f7;
    background: #fff;
    margin-bottom: 0px;
}

.ppt-pager-main {
    padding: 0px !important;
}

.pager-gen-result-panel {
    margin-bottom: 20px;
    margin-left: 20px;
    text-align: left;

    .pager-container {
        background-color: #fafafa;
        margin: 10px 10px;
        margin-right: 0px;
        border-radius: 8px;
        height: calc(100vh - 190px);
        padding: 10px;
        padding-left: 10px;
        margin-left: 20px;
        margin-bottom: 0px;
    }
}
</style>
