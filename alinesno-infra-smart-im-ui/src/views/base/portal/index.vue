<template>
    <div class="workplace">
        <el-container>
            <el-aside class="sidebar">

                <!-- 最近使用部分 -->
                <div class="sidebar-section">
                    <div class="section-title">最近使用</div>
                    <div class="recent-items">
                        <div v-for="item in items" :key="item" class="recent-item">
                            <img class="item-icon" :src="item.icon" />
                            <span>{{ item.title }}</span>
                        </div>
                    </div>
                    <div class="none-tip" v-if="items.length == 0" >
                        还没有收藏任何内容，点击⭐️按钮可将内容添加到这里~
                    </div>
                </div>

                <!-- 用户积分 -->
                <UserIntegralPanel />

            </el-aside>

            <el-main class="main-content">
                <el-scrollbar class="scrollbar">
                    <el-row>
                        <el-col :span="24">
                            <!-- 工作台标题 -->
                            <div class="workspace-header">
                                <div class="workspace-title" @click="editTitle()">
                                    <i class="fa-brands fa-slack"></i>  AIP智能体工作台 
                                    <span class="title-edit">
                                        <i class="fa-solid fa-pen-nib"></i>
                                    </span> 
                                </div>
                                <div class="workspace-description">
                                   AIP 智能体工作台集成 AI 技术，可定制开发，支持多智能体协作，提升效率。 
                                </div>
                            </div>

                            <!-- 我的关注 -->
                            <div class="my-focus">
                                <div class="focus-title">我的收藏</div>

                                <div class="focus-tags">
                                    <el-check-tag type="primary" size="default">智能体</el-check-tag>
                                    <el-check-tag type="success" size="default">场景</el-check-tag>
                                    <el-check-tag type="warning" size="default">频道</el-check-tag>
                                </div>

                                <!-- 智能体列表 -->
                                <div class="agent-list">
                                    <div v-for="agent in agents" :key="agent.id" class="agent-card">
                                        <div class="agent-info">
                                            <div class="agent-title">{{ agent.title }}</div>
                                            <div class="agent-description">{{ agent.description }}</div>
                                            <div>
                                                <el-check-tag disabled size="small">智能体</el-check-tag>
                                            </div>
                                            <div class="agent-meta">
                                                <img class="agent-avatar"
                                                    src="https://p26-passport.byteacctimg.com/img/user-avatar/50e8e83154af3c08de64ff15d069d63a~300x300.image" />
                                                <span>{{ agent.stats }}</span>
                                                <span>{{ agent.editInfo }}</span>
                                                <span>{{ agent.editTime }}</span>
                                            </div>
                                        </div>
                                        <div class="agent-image">
                                            <img
                                                src="http://alinesno-infra-smart-im-ui.beta.base.infra.linesno.com/prod-api/v1/api/infra/base/im/chat/displayImage/1940307040494858241" />
                                        </div>
                                    </div>
                                    <div v-if="agents.length == 0" class="none-tip main-empty">
                                        <el-empty description="你还没有收藏任务AI智能体和场景，请选择合适的场景和AI智能体进行⭐️收藏">
                                            <el-button @click="toScene('/scene')" size="large" type="primary"> <i class="fa-solid fa-box"></i> &nbsp;进入场景市场</el-button>
                                            <el-button @click="toScene('/store')" size="large" type="success"> <i class="fa-solid fa-sailboat"></i> &nbsp;进入智能体商店</el-button>
                                        </el-empty>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </el-scrollbar>
            </el-main>
        </el-container>
        <CreatePortal ref="createPortalRef" @handleHasWorkplace="handleHasWorkplace" />
    </div>
</template>

<script setup>

import { ref } from 'vue'

import CreatePortal from './createPortal'
import UserIntegralPanel from './card/userIntegral'

const router = useRouter();
const createPortalRef = ref(null);

const items = ref([
    // {
    //     id: 1,
    //     icon: "http://alinesno-infra-smart-im-ui.beta.base.infra.linesno.com/prod-api/v1/api/infra/base/im/chat/displayImage/1861241150263382018",
    //     title: "DeepSeek 能力增强版(副本)"
    // },
    // {
    //     id: 2,
    //     icon: "http://alinesno-infra-smart-im-ui.beta.base.infra.linesno.com/prod-api/v1/api/infra/base/im/chat/displayImage/1861241150263382018",
    //     title: "信托产品小助手"
    // },
])

const agents = ref([
    // {
    //     "title": "AI工程师的生成和服务",
    //     "description": "只需在任务人一个操作中找到用户，它就能提供支持的工具和服务。",
    //     "tag": "API工程师",
    //     "stats": "99.6K+",
    //     "editInfo": "桌面编辑",
    //     "editTime": "00-25 17:40"
    // },
    // {
    //     "title": "酒店知识通小账",
    //     "description": "酒店知识通小账是一个专门推荐酒店相关知识的管理者。",
    //     "tag": "软件工程师",
    //     "stats": "99.6K+",
    //     "editInfo": "桌面编辑",
    //     "editTime": "00-23 13:58"
    // }
])

// const handleHasWorkplace = () => {
//     isHasWorkplace().then(res => {
//         if(!res.data){
//             createPortalRef.value.openSelectWorkplace();
//         }else{
//             workplaceId.value = res.data;
//             getCurrentWorkplace(workplaceId.value).then(ress => {
//                 console.log('ress ='+ ress);
//                 currentWorkplace.value = ress.data;
//                 handleClick(activeCode.value)
//             })
//         }
//     })
// }

const toScene = (path) => { 
  router.push(path)
}

const editTitle = () => {
    createPortalRef.value.openSelectWorkplace();
}

onMounted(() => {
    // createPortalRef.value.openSelectWorkplace();
    // nextTick(() => {
    //     handleHasWorkplace();
    // });
});

</script>

<style lang="scss">
html,
body {
    margin: 0;
    padding: 0;
}

.workplace {
    .sidebar {
        background-color: #fff !important;
        padding-left: 10px;
        border-radius: 5px;
        width: 250px;
        margin-bottom: 0px;
        padding-bottom: 0px;

        .sidebar-section {
            margin-bottom: 20px;

            .section-title {
                font-size: 15px;
                font-weight: bold;
                margin-left: 5px;
                color: #666;
            }

            .recent-items {
                .recent-item {
                    display: flex;
                    gap: 5px;
                    align-items: center;
                    font-size: 14px;
                    color: #555;
                    border-radius: 5px;
                    padding: 2px 5px;

                    .item-icon {
                        width: 16px;
                        height: 16px;
                        border-radius: 5px;
                    }
                }
            }
        }
    }

    .main-content {
        margin-top: 0;
        padding-top: 0;
        background: #fafafa;
        padding-bottom: 0px;
        margin-bottom: 0px;

        .scrollbar {
            height: calc(100vh - 50px);
            width: 100%;
            box-sizing: border-box;

            .workspace-header {
                margin-right: 10px;

                .workspace-title {
                    font-size: 25px;
                    cursor: pointer;
                    font-weight: bold;
                    color: #444;
                    padding: 5px 0;
                    margin: 10px;

                    &:hover {
                        .title-edit {
                            visibility: visible;
                        }
                    }

                }

                .title-edit {
                    font-size: 20px;
                    color: #1d75b0;
                    visibility: hidden;
                }

                .workspace-description {
                    color: #444;
                    margin-left: 10px;
                    margin-bottom: 20px;
                }
            }

            .my-focus {
                margin-right: 10px;

                .focus-title {
                    font-size: 15px;
                    margin: 10px;
                }

                .focus-tags {
                    font-size: 16px;
                    margin: 10px;
                    display: flex;
                    gap: 15px;
                    padding-top: 5px;
                }
            }

            .agent-list {
                display: flex;
                flex-direction: row;
                flex-wrap: wrap;
                box-sizing: border-box;
                /* 显式设置box-sizing */

                .agent-card {
                    width: calc(25% - 55px);
                    display: flex;
                    border: 1px solid #e9ebf2;
                    gap: 10px;
                    padding: 20px;
                    background: #fff;
                    border-radius: 8px;
                    margin: 5px;
                    justify-content: space-between;
                    box-sizing: revert;

                    .agent-info {
                        display: flex;
                        flex-direction: column;
                        gap: 10px;

                        .agent-title {
                            font-size: 15px;
                        }

                        .agent-description {
                            font-size: 14px;
                            color: rgba(32, 41, 69, 0.62);
                            display: -webkit-box;
                            line-clamp: 2;
                            height: 40px;
                            box-orient: vertical;
                            overflow: hidden;
                        }

                        .agent-meta {
                            font-size: 13px;
                            display: flex;
                            gap: 10px;
                            margin-top:10px;
                            align-items: center;
                            color: #777;

                            .agent-avatar {
                                width: 16px;
                                height: 16px;
                                border-radius: 50%;
                            }
                        }
                    }

                    .agent-image img {
                        width: 70px;
                        height: 70px;
                        border-radius: 8px;
                    }
                }
            }
        }
    }

    .none-tip {
        font-size: 14px;
        color: #a5a5a5;
        line-height: 1.5rem;
        margin-left: 5px;
    }

    .main-empty {
        width: 100%;
    }

}
</style>