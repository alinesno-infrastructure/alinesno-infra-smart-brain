<template>
    <div>

        <el-scrollbar style="height:calc(100vh - 50px)">

            <div class="aip-appinfo-header">
                <div class="header-icon-banner">
                    <i class="fa-brands fa-slack"></i>
                </div>
                <div class="icon">
                    <div class="title">
                        {{ currentEnvClusterObj.appName }}
                    </div>
                </div>
            </div>


            <div class="banner-container-panel" style="margin-top:0px;margin-bottom:10px;">
                <el-row>
                    <el-col :span="18">

                        <div class="card-container" style="margin-right: 4rem;margin-left: 1rem;">
                            <div>
                                <p><span style="color: rgba(29, 28, 35, 0.6);font-size: 17px;font-weight: 400;line-height: 30px;margin-bottom: 28px;">
                                        清晰，还充满惊喜和幽默感，保证让你的作品脱颖而出，成为朋友圈的焦点！🔥👀 快来试试“镜界大导演”，让它带你领略创意无限的新世界！🌍🌟
                                        从此，你不再是孤军奋战的创作者，而是拥有智能助手的电影大师！🎬👑 赶紧来体验吧，让我们一起开启分镜创作的新
                                    </span></p>
                            </div>
                        </div>

                    </el-col>
                    <el-col :span="6">

                        <div class="right-container" style="text-align: right;margin-right: 40px;">
                            <img src="http://data.linesno.com/banner/agent_bg2.png" class="bot-banner-bg" style="width: 300px;border-radius: 8px;" alt="Banner Background Image">
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
                    <i :class="item.icon" /> {{ item.label }}
                </span>
            </div>

            <div style="margin-top:20px;">


                <h2 class="section-title" style="margin-top: 5px;margin-left: 10px;font-size: 20px;">
                    <i class="type.banner" /> {{ getNameByCode(activeCode) }}
                    <span style="font-size: 13px;color: #777;margin-left:10px;">由组织提供出来公共可用的智能体商店.</span>
                </h2>

                <!-- 解决方案 -->
                <!-- <el-collapse-transition> -->
                <transition name="el-fade-in-linear">
                    <BusinessAgentPanel v-if="activeCode === 'agent'" />
                </transition>
                <transition name="el-fade-in-linear">
                    <ChannelPanel v-if="activeCode === 'channel'" />
                </transition>
                <transition name="el-fade-in-linear">
                    <ScenePanel v-if="activeCode === 'scene'" />
                </transition>
                <!-- </el-collapse-transition> -->
            </div>

        </el-scrollbar>

        <CreatePortal />

    </div>
</template>

<script setup name="Index">

// import {
//   getGreeting
// } from '@/api/console/dashboard'

// import DashboardProductAll from './dashboard/product-all'
import BusinessAgentPanel from './businessAgentPanel.vue'
// import DashboardService from './dashboardService'
import CreatePortal from './createPortal.vue'
import ChannelPanel from './channelPanel.vue'
import ScenePanel from './scenePanel.vue'
// import SideAgentPanel from './sideAgentPanel.vue'
// import DashboardNotices from './dashboard/notices.vue'

const setupConst = [
    { code: 'agent', label: '单聊智能体', icon: 'fa-solid fa-user-tag' },
    { code: 'channel', label: '频道库', icon: 'fa-solid fa-box' },
    { code: 'scene', label: '应用场景', icon: 'fa-solid fa-sailboat' }
];

const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

const currentEnvClusterObj = ref({
    appName: 'GDG人工智能协会',
    clusterName: '赋能团队创建自主智能体的全生命周期管理平台，驱动业务创新与智能自动化',
})

const activeCode = ref('agent');
const hoveredCode = ref('');

const isActive = (code) => code === activeCode.value;
const isHovered = (code) => code === hoveredCode.value;

const handleClick = (code) => {
    activeCode.value = code;
    console.log('code = ' + code);
};

const handleMouseEnter = (code) => {
    hoveredCode.value = code;
};

const getNameByCode = (code) => {
    const item = setupConst.find((item) => item.code === code);
    return item ? item.label : '';
};

const handleMouseLeave = () => {
    hoveredCode.value = '';
};

// /** 与单个频道发信息 */
// function handleChannelChat(item){
//     // roleChatUri.value = "/channelChat?channel=" + item.id;
//     roleChatUri.value = "http://alinesno-infra-smart-im-ui.beta.smart.infra.linesno.com/channelChat?channel=1"
//     chatTitle.value = item.channelName;
//     dialogVisible.value = true ;
// }

// onMounted(() => {
//   getGreeting().then(res => {
//     currentEnvClusterObj.value.appName = res.data;
//   })
// })

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
        color: #3b5998;
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
        color: #3b5998;

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
    color: #3b5998 !important;
    font-weight: bold;
}
</style>
