<template>
    <div class="screen-app-container" style="height:calc(100vh - 70px)">
        <el-row class="app-row" style="background-color: transparent;">
            <el-col :span="24">

                <div class="avatar-title-container" style="align-items: center;">
                    <el-avatar shape="square" :size="40" :src="imagePathByPath(currentScreenInfo.screenBanner)" />
                    <div style="display: flex;flex-direction: column;">
                        <span class="title">
                            {{ currentScreenInfo.screenName }}
                        </span>
                        <span class="description">{{ truncateString(currentScreenInfo.screenDesc, 20) }}</span>
                    </div>

                    <el-tooltip class="box-item" effect="dark" content="配置管理人员" placement="top">
                        <span class="edit-header-avatar" style="color:#3b5998" @click="configAgent('leader')">
                            <i class="fa-solid fa-user-secret"></i>
                        </span>
                    </el-tooltip>

                </div>
            </el-col>

            <!-- <el-col :span="8">
                <div class="save-scene">
                    <el-tooltip class="box-item" effect="dark" content="清空会话" placement="top">
                        <el-button type="primary" text bg size="large" @click="addChapter">
                            <i class="fa-solid fa-feather icon-btn"></i>
                        </el-button>
                    </el-tooltip>
                </div>
            </el-col> -->
        </el-row>

        <el-row class="app-row" style="padding:0px;">

            <el-col :span="24">
                <!-- <div class="right-chatbox-contain"></div> -->
                <!-- <LeaderChat ref="leaderChatRef" /> -->
                <RoleChatPanel ref="roleChatPanelRef" />
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
        <!-- <ScreenUploadFile ref="uploadChildComp" /> -->

        <!-- 显示计划界面 -->
        <!-- <el-drawer v-model="displayPlanBox" 
                :title="planBoxTitle" 
                :with-header="false" 
                size="60%">
            <template #default>
                <div>
                    <LeaderPlan />
                </div>
            </template>
            <template #footer>
                <div style="flex: auto">
                    <el-button @click="cancelClick" text bg>关闭</el-button>
                </div>
            </template>
        </el-drawer> -->

    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElLoading } from 'element-plus'

import RoleChatPanel from '@/views/base/scene/common/chatPanel';
// import LeaderPlan from './leaderPlan.vue';
// import ScreenUploadFile from './screenUploadFile.vue'

import { listAll } from '@/api/base/im/user';
// import {
//     updateLeaderRole,
//     getScreen,
//     leaderPlan ,
// } from '@/api/base/im/screen'

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const leaderChatRef = ref(null)
const streamLoading = ref(null)
const agentList = ref([])
const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])
const currentAgentConfigType = ref('worker')

const roleChatPanelRef = ref(null)

const uploadChildComp = ref(null)
const displayPlanBox = ref(false)
const planBoxTitle = ref("团队任务计划")
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
            channelAgentList.value = [res.data.leader.id]; 
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

/** 运行管理者场景 */
const handleLeaderPlan = () => {

    displayPlanBox.value = true;

    // router.push({
    //     path: '/screen/leaderPlan',
    //     query: {
    //         screenId: currentScreenId.value
    //     }
    // })

     // 开始生成
    //  streamLoading.value = ElLoading.service({
    //     lock: true,
    //     background: 'rgba(255, 255, 255, 0.5)',
    //     customClass: 'custom-loading'
    //  });

    // leaderPlan(currentScreenId.value).then(res => {
    //     proxy.$modal.msgSuccess("运行成功");

    //     // 循环执行任务
    //     // let taskList = res.data.taskList
    //     // handleExecuteScreenTask(taskList)
    // })

}

/** 获取到场景详情 */
function handleGetScreen() {
    getScreen(currentScreenId.value).then(res => {
        currentScreenInfo.value = res.data
        currentLeaderRole.value = res.data.leader
        subordinateRoles.value = res.data.workers 

        // 调用子组件方法
        leaderChatRef.value.handleGetInfo(res.data.leader.id);
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
    // handleListAllRole()
    // handleGetScreen()

    roleChatPanelRef.value.openChatBoxWithRole('1896455996704731137') ; 

})

</script>

<style lang="scss" scoped>
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