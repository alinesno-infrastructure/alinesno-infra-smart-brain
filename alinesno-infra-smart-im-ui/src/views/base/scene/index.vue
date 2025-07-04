<template>

    <el-scrollbar style="height:calc(100vh - 60px)">
        <div class="tpl-app" style="display: flex;margin-left: 0px;">

            <SideTypePanel ref="sideTypePanelRef" @selectType="selectType" />

            <div style="width: calc(100% - 220px); padding: 10px;margin: -10px;margin-top: 0px;margin-left: 230px; " v-loading="sceneLoading">

                <div class="search-container-panel">
                    <el-row>
                        <el-col :span="24">
                            <div class="feature-header-xqbyQk feature-team-box">
                                <div style="gap: 12px;">
                                    <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                                        场景市场
                                    </h1>
                                </div>
                                <div class="search-container-weDuEn">
                                    <el-input v-model="queryParams.sceneName" style="width: 400px" size="large" placeholder="搜索场景">
                                        <template #suffix>
                                            <el-icon>
                                                <Search />
                                            </el-icon>
                                        </template>
                                    </el-input>
                                    <el-button type="primary" size="large" text bg icon="Search" @click="handleSearch">搜索</el-button>
                                </div>
                            </div>
                        </el-col>
                    </el-row>

                </div>

                <!--
                <div class="banner-container-panel" v-if="recommendRole" style="margin-left:10px;">
                    <el-row>
                        <el-col :span="18">

                        <div class="card-container">
                            <div>
                            <h1><span>🪡深度结合工作细节场景，精准服务，多场景应用</span></h1>
                            <p><span>{{ truncateString(recommendRole.responsibilities , 50) }}</span></p>
                            </div>
                            <el-button type="primary" bg text size="large" @click="handleRoleChat()">
                            <span class="semi-button-content">立即聊聊</span>
                            </el-button>
                        </div>


                        </el-col>
                        <el-col :span="6">

                        <div class="right-container">
                            <img :src="agentBg" class="bot-banner-bg" alt="Banner Background Image">

                            <div class="banner-info">
                            <span class="avatar">
                                <img :src="imagePathByPath(recommendRole.roleAvatar)"  alt="Avatar Image">
                            </span>
                            <div class="info-text">
                                <p class="category">{{ recommendRole.roleName }}</p>
                                <h1 class="title">{{ recommendRole.roleName }}</h1>
                                <div class="author-info">
                                <div class="author-name"><span>罗小东</span></div>
                                <div class="at-name"><span>@Easton</span></div>
                                </div>
                            </div>
                            </div>
                        </div>

                        </el-col>
                    </el-row>
                </div>
                -->

                <div class="header">
                    <span style="font-size: 13px;margin-left:10px;color: #a5a5a5;">这里包含所有需要运营的能力服务列表</span>
                </div>

                <div class="channel-container-panel">
                    <el-row>
                        <el-col :span="6" v-for="(item, index) in sceneLists" :key="index" style="padding:8px;">
                            <div class="scene-card-container" @click="enterScreen(item)">
                                <article class="scene-card">
                                    <div class="scene-image-container">
                                        <img :src="imagePathByPath(item.sceneBanner)" class="scene-card-image">
                                    </div>
                                    <div class="scene-card-content">
                                        <div class="scene-header">
                                            <span class="scene-title">{{ item.sceneName }}</span>
                                        </div>
                                        <div class="scene-author-info">
                                            <span class="scene-name">
                                                <i class="fa-solid fa-user-shield"></i>
                                                {{ item.orgName }}
                                            </span>
                                        </div>
                                        <div class="scene-description">
                                            {{ item.sceneDesc }}
                                        </div>
                                        <div class="semi-divider semi-divider-horizontal"></div>
                                    </div>
                                </article>
                                <div class="scene-card-footer">
                                    <el-button type="text" size="small" text bg>
                                        <i :class="JSON.parse(item.fieldProp)?.icon"></i>&nbsp;{{
                                        JSON.parse(item.fieldProp)?.sceneName }}
                                    </el-button>
                                        <div class="scene-footer">
                                            <div class="scene-price">
                                                <el-tag v-if="item.sceneScope == 'private'" type="danger"><i class="fa-solid fa-lock" /> 私有</el-tag>
                                                <el-tag v-else-if="item.sceneScope == 'public'" type="warning"><i class="fa-solid fa-globe" /> 公开</el-tag>
                                                <el-tag v-else type="primary"><i class="fa-solid fa-truck-plane" />组织</el-tag>
                                            </div>
                                            <div class="scene-tag">
                                                <div class="scene-stats">
                                                    <span>{{ item.usage_count }}</span>
                                                    <span>使用</span>
                                                </div>
                                            </div>
                                        </div>
                                </div>
                            </div>
                        </el-col>

                        <el-col :span="24" v-if="sceneLists.length == 0">
                            <el-empty :image-size="400" :image="learnLogo"
                                description="当前未创建业务场景，你的业务场景还未为空，可以在侧边栏快速创建。" />
                        </el-col>

                    </el-row>
                </div>
            </div>

        </div>
    </el-scrollbar>

</template>

<script setup>

import {
    sceneList,
    getRecommendRole ,
    sceneListByPage
} from '@/api/base/im/scene';

import SideTypePanel from './sideTypePanel'

import { onMounted } from 'vue';
import learnLogo from '@/assets/icons/tech_01.svg';
import SnowflakeId from "snowflake-id";
import AgentBgPng from '@/assets/banner/agent_bg.png';

const snowflake = new SnowflakeId();

const router = useRouter();

const recommendRole = ref(null);
const sceneLoading = ref(true)
const sceneLists = ref([])
const agentBg = ref(AgentBgPng)

const sideTypePanelRef = ref(null)
const queryParams = ref({
    pageSize: 100,
    pageNum: 0 ,
    sceneName: undefined,
    sceneType: undefined
})

/** 进入长文本编辑界面 */
function enterScreen(item) {
    const path = '/scene/' + item.sceneType + '/index';
    router.push({
        path: path,
        query: { 'sceneId': item.id }
    })
}

/** 与单个Role发信息 */
function handleRoleChat() {

  let id = recommendRole.value.id 
  router.push({
      path: '/single/agentChat',
      query: { 'roleId': id, 'channelId': snowflake.generate() }
  })
}

// 选择场景类型
function selectType(type){
    console.log('type = ' + type)
    queryParams.value.sceneType = type
    handleScreenList()
}

// 点击搜索按钮
function handleSearch() {
    handleScreenList()
}

/** 获取场景列表 */
function handleScreenList() {
    sceneListByPage(queryParams.value).then(res => {
        sceneLists.value = res.data
        sceneLoading.value = false
    })
}

// 获取推荐角色
function handleGetRecommendRole() {
    getRecommendRole().then(response => {
        recommendRole.value = response.data ;
    })
}

onMounted(() => {
    handleGetRecommendRole();
    handleScreenList();

    // 监听Enter，然后调用handleSearch
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    });

})

</script>

<style lang="scss" scoped>
.scene-card-container {
    display: flex;
    flex-grow: 1;
    border-radius: 8px;
    border-radius: 8px;
    border: 1px solid rgba(6, 7, 9, 0.1);
    background-color: #fff;
    flex-direction: column;

    .scene-card-footer {
        display: flex;
        justify-content: space-between;
        font-size: 14px;
        border-top: 0.5px solid #f0f0f5;
        margin-right: 20px;
        margin-left: 20px;
        width: auto;
        padding: 10px 0px; 
    }
}

.scene-card {
    display: flex;
    flex-direction: row;
    flex-grow: 1;
    overflow: hidden;
    padding: 20px;
    padding-bottom: 10px;
    cursor: pointer;
    transition: box-shadow 0.3s;

    &:hover {
        box-shadow: var(--coz-shadow-large);
    }

    .scene-image-container {
        position: relative;
        width: 60px;
        height: 60px;
        border-radius: 10px;
        overflow: hidden;

        .scene-card-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
        }
    }

    .scene-card-content {
        margin-top: 0px;
        padding: 0 4px;
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        width: calc(100% - 100px);
        padding-left: 20px;

        .scene-header {
            display: flex;
            align-items: flex-start;
            gap: 8px;
            margin: 4px 0px;
            margin-top: 0px;
            padding-left: 0px;
            flex-direction: column;

            .scene-title {
                font-weight: 500;
                font-size: 16px;
                line-height: 22px;
                color: var(--coz-fg-primary);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .scene-tag {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                padding: 2px 4px;
                border-radius: 4px;
                background-color: var(--coz-tag-primary);
                color: #999;
                font-weight: 500;
                font-size: 12px;
                line-height: 16px;
                min-height: 20px;
                white-space: nowrap;
                user-select: none;

                svg {
                    width: 1em;
                    height: 1em;
                    fill: currentColor;
                }

                span {
                    margin-left: 2px;
                }
            }
        }

        .scene-author-info {
            display: flex;
            align-items: center;
            gap: 4px;
            margin-top: 4px;

            .scene-avatar {
                width: 14px;
                height: 14px;
                border-radius: 50%;
            }

            .scene-name,
            .scene-username {
                font-weight: 500;
                font-size: 12px;
                line-height: 16px;
                color: var(--coz-fg-primary);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
        }

        .scene-description {
            margin-top: 8px;
            margin-bottom: 8px;
            font-size: 14px;
            line-height: 20px;
            color: var(--coz-fg-secondary);
            display: -webkit-box;
            -webkit-box-orient: vertical;
            height: 40px;
            overflow: hidden;
            display: -webkit-box;
            display: -moz-box;
            display: box;
            -webkit-box-orient: vertical;
            -moz-box-orient: vertical;
            box-orient: vertical;
            -webkit-line-clamp: 2;
            -moz-line-clamp: 2;
            line-clamp: 2;
            color: #899 ;
        }

    }
}

.search-container-weDuEn {
    display: flex;
    gap: 10px;
}

.scene-footer {
    margin-top: 4px;
    display: flex;
    gap: 10px;
    align-items: center;
    justify-content: space-between;

    .scene-price {
        font-weight: 500;
        font-size: 14px;
        line-height: 22px;
        color: var(--coz-fg-primary);
    }

    .scene-stats {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        line-height: 16px;
        color: var(--coz-fg-secondary);
    }
}

.right-container {
  position: relative; // 确保子元素可以绝对定位在容器内

  .bot-banner-bg {
    width: 100%;
    height: 100%;
    box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.12);
    border-radius: 10px;
  }

  .banner-info {
    display: flex;
    align-items: center;
    gap: 16px;
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 5px;
    background: linear-gradient(0deg, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0) 100%);
    flex-direction: row;
    border-radius: 10px;

    .avatar {
      width: 64px;
      height: 64px;
      background: #f0f0f5;
      border-radius: 8px;

      img {
        display: block;
        height: 100%;
        object-fit: cover;
        width: 100%;
        border-radius: 8px;
      }
    }

    .info-text {
      overflow: hidden;
      flex: 1;
      align-items: flex-start;
      display: flex;
      flex-direction: column;

      .category {
        font-size: 10px;
        font-weight: 400;
        line-height: 12px;
        color: #FFF;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin: 0;
      }

      .title {
        font-size: 18px;
        font-weight: 600;
        line-height: 24px;
        color: #FFF;
        margin: 0;
      }

      .author-info {
        display: flex;
        align-items: center;
        gap: 4px;
        flex-shrink: 0;
        max-width: 100%;
        height: 18px;
        color: rgba(255, 255, 255, 0.39);

        .semi-image {
          width: 14px;
          height: 14px;
          overflow: hidden;
          border-radius: 12px;

          img {
            width: 14px;
            height: 14px;
          }
        }

        .author-name,
        .at-name {
          font-size: 12px;
          font-weight: 400;
          line-height: 18px;
          flex: 1;
        }
      }
    }
  }

}
</style>