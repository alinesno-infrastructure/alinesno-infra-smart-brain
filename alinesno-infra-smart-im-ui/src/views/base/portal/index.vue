<template>
    <div class="workplace">
        <el-container>
            <el-aside class="sidebar">

                <!-- 最近使用部分 -->
                <div class="sidebar-section">
                    <div class="section-title">最近使用</div>
                    <div class="none-tip">
                        最近操作列表 ~~
                    </div>
                    <div class="recent-items">
                        <div v-for="item in items" :key="item.id" class="recent-item" @click="enterItem(item)">
                            <img class="item-icon" :src="imagePathByPath(item.avator)" />
                            <span>{{ item.name }}</span>
                        </div>
                    </div>
                    <div class="none-tip" v-if="items.length == 0">
                        还没有操作任何内容， 最近操作可将内容添加到这里 ~~
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
                                    <i class="fa-brands fa-slack"></i> {{ currentWorkplace.name }}
                                    <span class="title-edit">
                                        <i class="fa-solid fa-pen-nib"></i>
                                    </span>
                                </div>
                                <div class="workspace-description">
                                    {{ currentWorkplace.description }}
                                </div>
                            </div>

                            <!-- 我的关注 -->
                            <div class="my-focus">
                                <div class="focus-title">我的收藏</div>

                                <div class="focus-tags">
                                    <el-check-tag  :checked="filterTag === ''"  type="primary"  size="default" @click="handleTagClick('')" >全部</el-check-tag>
                                    <el-check-tag  :checked="filterTag === 'scene'"  type="primary" size="default" @click="handleTagClick('scene')">场景</el-check-tag>
                                    <el-check-tag  :checked="filterTag === 'agent'"  type="primary" size="default" @click="handleTagClick('agent')">智能体</el-check-tag>
                                    <el-check-tag  :checked="filterTag === 'channel'"  type="primary" size="default" @click="handleTagClick('channel')" >频道</el-check-tag>
                                </div>

                                <!-- 智能体列表 -->
                                <div class="agent-list">
                                    <div v-for="agent in agents" :key="agent.id" class="agent-card">
                                        <div class="agent-info">
                                            <div class="agent-title" @click="enterItem(agent)">{{ agent.title }}</div>
                                            <div class="agent-description">{{ agent.description }}</div>
                                            <div class="agent-type-tags" >
                                                <el-check-tag checked="true" type="primary" size="small" v-if="agent.type === 'scene'">场景</el-check-tag>
                                                <el-check-tag checked="true" type="success" size="small" v-if="agent.type === 'agent'">智能体</el-check-tag>
                                                <el-check-tag checked="true" type="warning" size="small" v-if="agent.type === 'channel'">频道</el-check-tag>
                                                <el-check-tag type="danger" size="small" v-if="agent.sceneTypeName"> <i :class="agent.sceneIcon"></i>  {{ agent.sceneTypeName }} </el-check-tag>
                                            </div>
                                            <div class="agent-meta">
                                                <span>{{ agent.editInfo }}</span>
                                                <span>{{ agent.editTime }}</span>
                                                <div class="agent-collect-remove" @click="handleUnCollectItem(agent)">
                                                    <span><i class="fa-solid fa-eraser"></i> 取消</span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="agent-image">
                                            <img :src="imagePathByPath(agent.avatar)" />
                                        </div>
                                    </div>
                                    <div v-if="agents.length == 0" class="none-tip main-empty">
                                        <el-empty description="你还没有收藏AI智能体和场景，请选择合适的场景和AI智能体进行⭐️收藏">
                                            <el-button @click="toScene('/scene')" size="large" type="primary"> <i
                                                    class="fa-solid fa-box"></i> &nbsp;进入场景市场</el-button>
                                            <el-button @click="toScene('/store')" size="large" type="success"> <i
                                                    class="fa-solid fa-sailboat"></i> &nbsp;进入智能体商店</el-button>
                                        </el-empty>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </el-scrollbar>
            </el-main>
        </el-container>
        <CreatePortal ref="createPortalRef" @handleHasWorkplace="handleGetCurrentWorkplace" />
    </div>
</template>

<script setup>

import { ref } from 'vue'

import {
    isHasAccountWorkplace,
    unCollectItem,
    getFrequentItem,
    getCurrentWorkplace
} from "@/api/base/im/workplace"

import SnowflakeId from "snowflake-id";
const snowflake = new SnowflakeId();

import CreatePortal from './createPortal'
import UserIntegralPanel from './card/userIntegral'

const { proxy } = getCurrentInstance();
const router = useRouter();
const createPortalRef = ref(null);
const filterTag = ref('')

const defaultDesc = ref('AI智能体工作台是融合多种先进技术的综合性工作平台，具备知识管理、内容生成、创作辅助等多种功能，功能包括多模型接入、知识管理、智能创作等功能，能够极大提升工作效率与创新能力，助力企业和个人在数字化时代实现高效、智能的工作模式 。')
const currentWorkplace = ref({
    name: 'AIP智能体运营工作台',
    description: defaultDesc.value,
})
const workplaceId = ref('');

const items = ref([])
const agents = ref([])

const toScene = (path) => {
    router.push(path)
}

const editTitle = () => {
    createPortalRef.value.openSelectWorkplace(currentWorkplace.value);
}

// 标签点击处理方法
const handleTagClick = (tag) => {

    // 如果当前的 filterTag 和 tag 相同，则不处理
    if (filterTag.value === tag) {
        return;
    }

    filterTag.value = tag
    handleGetCurrentWorkplace();
}

const handleHasWorkplace = () => {
    isHasAccountWorkplace().then(res => {
        if (!res.data) {
            createPortalRef.value.openSelectWorkplace();
        } else {
            workplaceId.value = res.data;
            handleGetCurrentWorkplace();
        }
    })
}

const handleGetCurrentWorkplace = () => { 
    getCurrentWorkplace(workplaceId.value , filterTag.value).then(ress => {
        console.log('ress =' + ress);
        currentWorkplace.value = ress.data;
        agents.value = ress.workplaceItems
    })
}

// 取消收藏
const handleUnCollectItem = (item) => {
    const data = {
        itemId: item.itemId,
        type: item.type
    }
    unCollectItem(data).then(res => {
        proxy.$modal.msgSuccess("取消收藏成功.");
        handleHasWorkplace();
    })
}

const handleGetFrequentItem = () => {
    getFrequentItem().then(res => {
        items.value = res.data;
    })
}

// 进入指定的item
const enterItem = (item) => {
    const type = item.type;
    const id = item.id; 

    console.log('type = ' + type + ' , id = ' + id);

    if(type === 'scene'){
        enterScreen(item);
    }else if(type === 'channel'){
        enterChannel(item);
    }else if(type === 'agent'){
        handleRoleChat(item);
    }

}

/** 进入长文本编辑界面 */
function enterScreen(item) {
    const path = '/scene/' + item.sceneType + '/index';
    router.push({
        path: path,
        query: { 'sceneId': item.id }
    })
}

function handleRoleChat(item) {
    router.push({
        path: '/single/agentChat',
        query: { 'roleId': item.id, 'channelId': snowflake.generate() }
    })
}

function enterChannel(item) {
  router.push({
    path: '/chat',
    query: { 'channel': item.id }
  })
}

onMounted(() => {
    nextTick(() => {
        handleHasWorkplace();
        handleGetFrequentItem();
    });
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

                    &:hover {
                        background-color: #eaeaea;
                        cursor: pointer;
                    }

                    .item-icon {
                        width: 16px;
                        height: 16px;
                        border-radius: 5px;
                        object-fit: cover;
                        object-position: center;
                    }
                }
            }
        }
    }

    .main-content {
        margin-top: 0;
        padding-top: 0;
        padding-right: 0;
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

                    &:hover {
                        .agent-collect-remove {
                            display: block !important;
                        }
                    }

                    .agent-info {
                        display: flex;
                        flex-direction: column;
                        gap: 10px;


                        .agent-title {
                            font-size: 16px;
                            cursor: pointer;
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
                            margin-top: 10px;
                            align-items: center;
                            color: #777;


                            .agent-collect-remove {
                                align-items: center;
                                display: none;
                                cursor: pointer;
                                gap: 10px;
                            }
                        }
                    }

                    .agent-image img {
                        width: 70px;
                        height: 70px;
                        border-radius: 8px;
                        object-fit: cover;
                        object-position: center;
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

    .agent-type-tags {
        display: flex;
        gap: 5px;
    }

}
</style>