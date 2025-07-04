<template>
    <div class="custom-tabs">
        <!-- Tab Headers -->
        <div class="tab-headers">
            <div 
                v-for="tab in tabs" 
                :key="tab.name"
                class="tab-header"
                :class="{ 'active': activeName === tab.name }"
                @click="switchTab(tab.name)"
            >
                <span>
                    <i :class="tab.icon"></i>&nbsp;{{ tab.label }}
                </span>
            </div>
        </div>

        <!-- Tab Content -->
        <div class="tab-content">
            <div v-if="activeName === 'chat'" class="tab-pane">
                <RoleChatPanel ref="roleChatPanelRef" />
            </div>
            <div v-else class="tab-pane">
                <div class="placeholder-content">
                    {{ currentTab.label }} content will be displayed here
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import RoleChatPanel from '@/views/base/scene/common/chatPanel';

const roleChatPanelRef = ref(null);
const activeName = ref('chat');

const tabs = [
    { name: 'chat', label: '对话', icon: 'fa-solid fa-comments' },
    { name: 'graph', label: '知识图谱', icon: 'fa-solid fa-project-diagram' },
    { name: 'notes', label: '笔记', icon: 'fa-solid fa-notes-medical' }
];

const currentTab = computed(() => tabs.find(tab => tab.name === activeName.value) || tabs[0]);

const switchTab = (tabName) => {
    activeName.value = tabName;
    console.log('选中标签页:', tabName);
    if (tabName === 'chat') {
        console.log('选中标签页:', tabName);
        roleChatPanelRef.value?.openChatBoxWithRole('1919192288224464897');
    }
};

onMounted(() => {
    roleChatPanelRef.value?.openChatBoxWithRole('1919192288224464897');
});
</script>

<style lang="scss" scoped>
.custom-tabs {
    display: flex;
    margin-top:10px;
    flex-direction: column;
    height: 100%;
}

.tab-headers {
    display: flex;
}

.tab-header {
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 14px;
    color: #666;
    font-weight: 500;
    border-radius: 8px;
    margin-right: 10px;
    padding: 8px 20px;

    &:hover {
        color: #409eff;
        background-color: #f0f0f0;
    }

    &.active {
        color: #409eff;
        font-weight: 500;
        border-radius: 8px;
        background: #f5f5f5;
        padding: 8px 20px;
    }

}

.tab-content {
    flex: 1;
    padding: 1px;
    overflow: auto;
}

.tab-pane {
    height: 100%;
}

.placeholder-content {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    color: #999;
    font-size: 16px;
}
</style>