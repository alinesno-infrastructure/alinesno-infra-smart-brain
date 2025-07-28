<template>
    <div>
        <el-scrollbar style="height:calc(100vh - 50px)">
            <div class="aip-appinfo-header">
                <div class="header-icon-banner">
                    <i class="fa-brands fa-slack"></i>
                </div>
                <div class="icon">
                    <div class="title">
                        {{ currentWorkplace.name }}
                    </div>
                </div>
            </div>
            <div class="banner-container-panel" style="margin-top:0px;margin-bottom:10px;">
                <el-row>
                    <el-col :span="18">
                        <div class="card-container" style="margin-right: 4rem;margin-left: 1rem;">
                            <div>
                                <p>
                                    <span style="color: rgba(29, 28, 35, 0.6);font-size: 17px;font-weight: 400;line-height: 30px;margin-bottom: 28px;">
                                        {{ currentWorkplace.description? currentWorkplace.description : defaultDesc }}
                                    </span>
                                </p>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="right-container" style="text-align: right;margin-right: 40px;">
                            <img :src="agentBg2" class="bot-banner-bg" style="width: 300px;border-radius: 8px;" alt="Banner Background Image">
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div style="padding: 0px 10px;display: flex;gap: 20px;">
                <span v-for="item in setupConst" :key="item.code" style="background: #f2f3f8;padding: 10px;border-radius: 5px;cursor: pointer;font-size: 14px;color: #444;"  
                    :class="{ 'active': isActive(item.code), 'hovered': isHovered(item.code) }"
                    @click="handleClick(item.code)" 
                    @mouseenter="handleMouseEnter(item.code)"
                    @mouseleave="handleMouseLeave(item.code)">
                    <el-tooltip
                            class="box-item"
                            effect="dark"
                            :content="item.desc"
                            placement="top"
                        >
                        <span>
                            <i :class="item.icon" /> {{ item.label }}
                        </span>
                    </el-tooltip>
                </span>
            </div>
            <div style="margin-top:20px;">
                <h2 class="section-title" style="margin-top: 5px;margin-left: 10px;font-size: 20px;">
                    <i class="type.banner" /> {{ getNameByCode(activeCode)?.label }}
                    <span style="font-size: 13px;color: #777;margin-left:10px;"> {{ getNameByCode(activeCode)?.desc }} </span>
                </h2>
                <!-- 解决方案 -->
                <BusinessAgentPanel v-if="activeCode === 'agent'" ref="businessAgentPanelRef" />
                <ScenePanel v-if="activeCode === 'scene'" ref="scenePanelRef" />
                <ChannelPanel v-if="activeCode === 'channel'" ref="channelPanelRef" />
            </div>
        </el-scrollbar>
        <CreatePortal ref="createPortalRef" @handleHasWorkplace="handleHasWorkplace" />
    </div>
</template>

<script setup name="Index">
import {
    isHasWorkplace, 
    getCurrentWorkplace 
} from "@/api/base/im/workplace"
import BusinessAgentPanel from './businessAgentPanel.vue'
import CreatePortal from './createPortal.vue'
import ChannelPanel from './channelPanel.vue'
import ScenePanel from './scenePanel.vue'
import { ref, onMounted, nextTick } from 'vue';
import AgentBg2Png from '@/assets/banner/agent_bg2.png';

const agentBg2 = ref(AgentBg2Png);
const scenePanelRef = ref(null);
const businessAgentPanelRef = ref(null);
const channelPanelRef = ref(null);

const setupConst = [
    { code: 'agent', label: '智能体', icon: 'fa-solid fa-user-tag' , desc: '单个智能体交互聊天' },
    { code: 'scene', label: '应用场景', icon: 'fa-solid fa-sailboat'  , desc: '多智能体处理具体业务场景应用场景' },
    { code: 'channel', label: '频道库', icon: 'fa-solid fa-box' , desc: '多个智能体业务结合放到同一个群里' },
];

const defaultDesc = ref('AI工作台是融合多种先进技术的综合性工作平台，具备知识管理、内容生成、创作辅助等多种功能，功能包括多模型接入、知识管理、智能创作等功能，能够极大提升工作效率与创新能力，助力企业和个人在数字化时代实现高效、智能的工作模式 。')
const currentWorkplace = ref({
    name: 'AIP产品运营工作台',
    description: defaultDesc.value , 
})

// 从 localStorage 中读取存储的选项，如果没有则默认 'agent'
const activeCode = ref(localStorage.getItem('activeCode') || 'scene');
const hoveredCode = ref('');
const workplaceId = ref(''); 
const createPortalRef = ref(null);

const isActive = (code) => code === activeCode.value;
const isHovered = (code) => code === hoveredCode.value;

const handleClick = (code) => {
    activeCode.value = code;
    // 将选中的选项存储到 localStorage 中
    localStorage.setItem('activeCode', code);
    console.log('code ='+ code);

    nextTick(() => {
        if(activeCode.value === 'agent'){
            businessAgentPanelRef.value.handleGetWorkplaceItem(workplaceId.value, activeCode.value);
        }else if(activeCode.value === 'channel'){
            channelPanelRef.value.handleGetWorkplaceItem(workplaceId.value, activeCode.value);
        }else if(activeCode.value ==='scene'){
            scenePanelRef.value.handleGetWorkplaceItem(workplaceId.value, activeCode.value)
        }
    })
}

const handleMouseEnter = (code) => {
    hoveredCode.value = code;
}

const getNameByCode = (code) => {
    const item = setupConst.find((item) => item.code === code);
    return item
}

const handleMouseLeave = () => {
    hoveredCode.value = '';
}

const handleHasWorkplace = () => {
    isHasWorkplace().then(res => {
        if(!res.data){
            createPortalRef.value.openSelectWorkplace();
        }else{
            workplaceId.value = res.data;
            getCurrentWorkplace(workplaceId.value).then(ress => {
                console.log('ress ='+ ress);
                currentWorkplace.value = ress.data;
                handleClick(activeCode.value)
            })
        }
    })
}

onMounted(() => {
    nextTick(() => {
        handleHasWorkplace();
    });
});
</script>

<style lang="scss" scoped>
.aip-appinfo-header {
    position: relative;
    padding: 10px;
    overflow: hidden;
    display: flex;
    align-items: center;

    .header-icon-banner {
        float: left;
        font-size: 2.0rem;
        margin-right: 10px;
        color: #1d75b0;
    }

    .head-app-status {
        float: right;
        font-size: 14px;
        line-height: 1.7rem;
        margin-bottom: 10px;

        .integrated-item-box {

            margin-left: 10px;
            float: right;

            ul,
            li {
                margin: 0px;
                padding: 0px;
                list-style: none;
            }

            li {
                float: right;
                margin-right: 10px;

                img {
                    border-radius: 5px;
                    width: 25px;
                }
            }

        }
    }

    .cluster-info {
        float: right;
        position: relative;
        font-size: 14px;
        margin-left: 10px;
        font-weight: bold;
        margin-top: 10px;
        color: #1d75b0;

        span {
            margin-left: 20px;
        }
    }

    .icon {
        float: left;
    }

    .title {
        font-weight: 600;
        font-style: normal;
        font-size: 38px;
        color: #242e42;
        text-shadow: 0 4px 8px rgba(36, 46, 66, 0.1);
        display: flex;
        align-items: center;
    }

    .title-desc {
        color: #79879c !important;
        font-size: 12px;
    }
}

.active{
    color: #1d75b0 !important;
    font-weight: bold;
}
</style>
