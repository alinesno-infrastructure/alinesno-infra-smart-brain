<template>
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

                <!-- 自定义源列表项 -->
                <template #default="{ option }">
                    <div class="aip-el-transfer-panel__item">
                        <img :src="imagePathByPath(option.avatar)" /> {{ option.label }}
                    </div>
                </template>

            </el-transfer>
        </div>

        <template #footer>
            <div class="dialog-footer">
                <el-button @click="configAgentDialogVisible = false">关闭</el-button>
                <el-button type="primary" @click="handleCloseAgentConfig">确认</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>

import { listAll } from '@/api/base/im/user';

const agentList = ref([])
const channelAgentConfigTitle = ref("")
const configAgentDialogVisible = ref(false)
const channelAgentList = ref([])

const emit = defineEmits(['handleCloseAgentConfig'])

// 打开选择人员对话框
const handleOpendAgent = async (title, agents) => {

    console.log('agents = ' + agents)


    configAgentDialogVisible.value = true
    channelAgentConfigTitle.value = title

    handleListAllRole(agents)
}

/** 关闭选择角色弹窗 */
const handleCloseAgentConfig = () => {
    configAgentDialogVisible.value = false;
    emit('handleCloseAgentConfig', channelAgentList.value)
}


/** 获取到所有的角色信息 */
const handleListAllRole = async (agents) => {
    agentList.value = []

    listAll().then(res => {
        for (let i = 0; i < res.data.length; i++) {
            let item = res.data[i]

            agentList.value.push({
                key: item.id,
                avatar: item.roleAvatar,
                label: item.roleName,
                disabled: false,
            })


        }
        nextTick(() => {
            channelAgentList.value = agents ? agents: [];
        })
    })
}

defineExpose({
    handleOpendAgent
})

</script>

<style lang="scss" scoped></style>
