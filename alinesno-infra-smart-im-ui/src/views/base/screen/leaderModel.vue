<template>
    <div class="app-container tpl-app screen-app-container">
        <el-row class="app-row">
            <el-col :span="8">


                <div class="avatar-title-container">
                    <el-avatar shape="circle" :size="40" :src="imagePathByPath(currentLeaderRole.roleAvatar)" />
                    <div style="display: flex;flex-direction: column;">
                        <span class="title">{{ currentLeaderRole.roleName }}</span>
                        <span class="description">{{ truncateString(currentLeaderRole.responsibilities , 20) }}</span>
                    </div>

                    <el-tooltip class="box-item" effect="dark" content="配置管理人员" placement="top">
                        <span class="edit-header-avatar" style="color:#3b5998" @click="configAgent('leader')">
                            <i class="fa-solid fa-user-secret"></i>
                        </span>
                    </el-tooltip>

                </div>
            </el-col>
            <el-col :span="8">
                <div class="task-management">
                    <span>
                        {{ currentScreenInfo.screenName }}
                    </span>
                    <span style="font-size:13px;font-weight: lighter;color:#a5a5a5">
                        {{ currentScreenInfo.screenDesc }}
                    </span>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="save-scene">

                    <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top">
                        <el-button type="danger" text bg size="large" @click="handleUploadFile">
                            <i class="fa-solid fa-file-word icon-btn"></i>
                        </el-button>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="保存场景" placement="top">
                        <el-button type="primary" text bg size="large" @click="addChapter">
                            <i class="fa-solid fa-feather icon-btn"></i>
                        </el-button>
                    </el-tooltip>
                </div>
            </el-col>
        </el-row>

        <el-row class="app-row" style="padding:0px;">

            <!--
      <el-col :span="7">
        <div class="left-aside-contain">
          <div class="header">人设与回复逻辑</div>
          <div class="textarea-container">
            <el-input type="textarea" style="border:0px !important" :rows="30" resize="none" v-model="promptContent" />
          </div>
        </div>
      </el-col>
      -->
            <el-col :span="19">
                <div class="right-chatbox-contain">
                    <LeaderChat />
                </div>
            </el-col>

            <el-col :span="5">
                <div class="middle-section">
                    <div style="margin-bottom:20px">
                        <div class="section-header">管理范围</div>
                        <div style="font-size: 15px;line-height: 1.4rem;">
                            探索最新的科技创新和技术趋势，提供有关人工智能、大数据、云计算、物联网等领域的深入报道和专家分析。适合对技术进步感兴趣的读者
                        </div>
                    </div>

                    <div class="subordinate-roles">
                        <div class="section-title">
                            下属角色
                            <el-tooltip class="box-item" effect="dark" content="添加章节编写人员" placement="top">
                                <span class="edit-header-avatar" @click="configAgent('worker')">
                                    <i class="fa-solid fa-user-tag"></i>
                                </span>
                            </el-tooltip>
                        </div>
                        <el-scrollbar style="height: calc(100vh - 290px);">
                            <ul>
                                <li v-for="item in subordinateRoles" :key="item">
                                    <div style="width:45px">
                                        <el-avatar shape="circle" :size="40" :src="imagePathByPath(item.roleAvatar)" />
                                    </div>
                                    <div class="role-info">
                                        <span class="role-title">
                                            {{ item.roleName }}
                                        </span>
                                        <span class="role-description">{{ truncateString(item.responsibilities, 15) }}</span>
                                    </div>
                                </li>
                            </ul>
                        </el-scrollbar>
                    </div>

                </div>
            </el-col>
        </el-row>

        <!-- 选择人员 -->
        <el-dialog v-model="configAgentDialogVisible" :title="channelAgentConfigTitle" width="900">
            <div class="dialog-body-content" style="text-align: center">
                <el-transfer v-model="channelAgentList" 
                    filterable
                    style="text-align: left; width:100%; display: inline-block" 
                    :titles="['源角色', '已选择']" 
                    :format="{
                        noChecked: '${total}',
                        hasChecked: '${checked}/${total}',
                    }" @left-check-change="handleLeftCheckChange" 
                    @change="handleChange"
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

        <!-- 材料上传界面 -->
        <ScreenUploadFile ref="uploadChildComp" />

    </div>
</template>

<script setup>
import { ref } from 'vue';

import LeaderChat from './leaderChat.vue';
import ScreenUploadFile from './screenUploadFile.vue'

import { listAll } from '@/api/base/im/user';
import {
    updateLeaderRole,
    getScreen,
} from '@/api/base/im/screen'

const route = useRoute();
const { proxy } = getCurrentInstance();

const agentList = ref([])
const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])
const currentAgentConfigType = ref('worker')

const uploadChildComp = ref(null)

const currentScreenInfo = ref({
    id: 0,
    screenName: null,
    screenDesc: null,
    screenCount: 0,
    knowledgeId: null,
    chapterEditors: [],
    contentEditors: []
})

const currentLeaderRole = ref({
    roleAvatar: null ,
    roleName: null,
    responsibilities: null 
})

const currentScreenId = ref(route.query.screenId)

const subordinateRoles = ref([]);

/** 配置成员 */
function configAgent(type) {

    configAgentDialogVisible.value = true;
    console.log('configAgent')

    currentAgentConfigType.value = type
    channelAgentConfigTitle.value = "选择角色";
    channelAgentList.value = [];

    getScreen(currentScreenId.value).then(res => {
        if(currentAgentConfigType.value == 'leader'){
            channelAgentList.value = []; 
        }else if(currentAgentConfigType.value == 'worker'){
            channelAgentList.value = res.data.workers.map(item => item.id)
        }
    }) 
}

/** 上传文档文件 */
function handleUploadFile() {
    uploadChildComp.value.handleOpenUpload(true);
}

// 处理左侧菜单只能选择一个选项的问题
const handleLeftCheckChange = (value, direction, movedKeys) => {
    if (currentAgentConfigType.value == 'leader') {
        if (value.length > 1) {
            value.splice(0, 1);
        }
    }
}

// 监听职务绑定变化情况，记录变化后的职务id数组
const handleChange = (value, direction, movedKeys) => {
    if (currentAgentConfigType.value == 'leader') {
        channelAgentList.value = value;
        if (channelAgentList.value.length > 1) {
            channelAgentList.value.shift();
        }
    }
}

/** 获取到场景详情 */
function handleGetScreen() {
    getScreen(currentScreenId.value).then(res => {
        currentScreenInfo.value = res.data
        currentLeaderRole.value = res.data.leader
        subordinateRoles.value = res.data.workers 
    })
}


/** 关闭选择角色弹窗 */
function handleCloseAgentConfig(){
  configAgentDialogVisible.value = false ;

  let data = {
    screenId: currentScreenId.value , 
    workerIds: channelAgentList.value , 
    type: currentAgentConfigType.value , 
    leaderId: channelAgentList.value?channelAgentList.value[0]:null
  }

  updateLeaderRole(data).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    handleGetScreen() ;
  })
  
}

/** 获取到所有的角色信息 */
function handleListAllRole() {
    listAll().then(res => {
        for (let i = 0; i < res.data.length; i++) {
            let item = res.data[i]

            agentList.value.push({
                key: item.id,
                label: item.roleName,
                disabled: false,
            })
        }
    })
}

nextTick(() => {
    console.log('screenId = ' + route.query.screenId);
    handleListAllRole()
    handleGetScreen()
})

</script>

<style lang="scss" scoped>
.screen-app-container {
    padding: 0;
    width: 100%;
    height: calc(100vh - 40px);

    .app-row {
        padding: 10px;
        margin-top: 0;
        border-top: 1px solid #e5e5e5;
        background-color: #fff;

        &:first-child {
            margin-top: 0;
            border-top: none;
        }

        .el-col {
            &:first-child {
                .avatar-title-container {
                    display: flex;
                    gap: 8px;
                    align-items: flex-end;

                    .title {
                        color: var(--light-color-black-black, #000);
                        font-size: 14px;
                        font-weight: 600;
                        line-height: inherit;
                        max-width: calc(50vw - 237px);
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }

                    .description {
                        padding: 1px 5px 1px 0;
                        font-size: 13px;
                        color: #a5a5a5;
                    }
                }
            }

            &:nth-child(2) {
                .task-management {
                    display: flex;
                    font-size: 16px;
                    color: #3b5998;
                    font-weight: 600;
                    align-items: center;
                    justify-content: center;
                    height: 40px;
                    flex-direction: column;
                }
            }

            &:last-child {
                .save-scene {
                    display: flex;
                    flex-direction: row;
                    align-items: center;
                    position: absolute;
                    right: 10px;
                }
            }
        }
    }

    .left-aside-contain {
        padding: 10px;
        margin-top: 0;
        height: calc(100vh - 125px);
        padding-left: 10px;
        border-right: 1px solid #e5e5e5;

        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 14px;
            font-weight: 600;
            height: 40px;
            border-radius: 3px;
        }

        .textarea-container {
            padding-right: 0px;
        }
    }

    .middle-section {
        padding: 0px 20px;
        background: #fafafa;
        border-left: 1px solid #e6e5e9;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        align-items: stretch;
        flex-wrap: nowrap;

        .section-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 14px;
            font-weight: 600;
            height: 40px;
            color: #a5a5a5;
            border-radius: 3px;
            gap: 8px;
        }

        .subordinate-roles,
        .triggers,
        .introduction {
            .section-title {
                font-size: 14px;
                color: #444;
                margin-bottom: 10px;
                font-weight: bold;
                margin-top: 10px;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            ul {
                list-style: none;
                padding: 0;
                margin: 0;

                li {
                    display: flex;
                    gap: 5px;
                    padding: 5px;

                    .role-info {
                        display: flex;
                        flex-direction: column;
                        gap: 3px;

                        .role-title {
                            color: var(--light-color-black-black, #000);
                            font-size: 14px;
                            font-weight: 600;
                            line-height: inherit;
                            max-width: calc(50vw - 237px);
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                        }

                        .role-description {
                            padding: 1px 5px 1px 0;
                            font-size: 13px;
                            color: #a5a5a5;
                        }
                    }
                }
            }
        }
    }

    .right-chatbox-contain {
        padding: 10px;
        height: calc(100vh - 300px);
        background: #fff;

        .preview-debug-header {
            font-size: 18px;
            font-weight: 500;
            line-height: 24px;
            margin-right: 24px;
        }
    }
}
</style>

<style>
.el-transfer-panel {
    width: 350px;
}

.el-card__header {
    border: 0px;
}

.chapter-tree-content .el-tree-node__content {
    height: auto !important;
}
</style>