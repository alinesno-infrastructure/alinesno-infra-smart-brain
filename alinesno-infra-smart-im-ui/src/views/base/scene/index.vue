<template>

    <div class="app-container tpl-app">
        <div class="search-container-panel">
            <el-row>
                <el-col :span="24">
                    <div class="feature-header-xqbyQk feature-team-box">
                        <div style="gap: 12px;">
                            <h1
                                style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                                我的场景
                            </h1>
                        </div>
                        <div class="search-container-weDuEn">
                            <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索场景"
                                :suffix-icon="Search" />
                        </div>
                    </div>
                </el-col>
            </el-row>

        </div>
        <div class="header">
            <span style="font-size: 13px;margin-left:10px;color: #a5a5a5;">这里包含所有需要运营的能力服务列表</span>
        </div>

        <div class="channel-container-panel" style="margin-top:20px">
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
                                    <div class="scene-tag">
                                        <span class="scene-tag-icon"></span>
                                        <span>{{ item.fieldProp }}</span>
                                    </div>
                                </div>
                                <div class="scene-author-info">
                                    <img src="http://data.linesno.com/switch_header.png" alt="Author Avatar"
                                        class="scene-avatar">
                                    <span class="scene-name">罗小东</span>
                                    <span class="scene-username">@Easton</span>
                                </div>
                                <div class="scene-description">
                                    {{ item.sceneDesc }}
                                </div>
                                <div class="semi-divider semi-divider-horizontal"></div>
                                <div class="scene-footer">
                                    <div class="scene-price">免费{{ item.usage_count }}</div>
                                    <div class="scene-stats">
                                        <span>{{ item.usage_count }}</span>
                                        <span>使用</span>
                                    </div>
                                </div>
                            </div>
                        </article>
                    </div>
                </el-col>

                <el-col :span="24" v-if="sceneLists.length == 0">
                    <el-empty :image-size="400" :image="learnLogo" description="当前未创建业务场景，你的业务场景还未为空，可以在侧边栏快速创建。" />
                </el-col>

            </el-row>
        </div>

    </div>

</template>

<script setup>

import {
    sceneList
} from '@/api/base/im/scene';

import { onMounted } from 'vue';
import learnLogo from '@/assets/icons/tech_01.svg';

const router = useRouter();

const sceneLists = ref([])

/** 进入长文本编辑界面 */
function enterScreen(item) {
  const path = '/scene/'+ item.sceneType +'/index';
  router.push({
      path: path , 
      query: { 'sceneId': item.id }
  })

    // // 跳转至详情页
    // if (item.sceneType === 'leader_model') {  // 领导模型
    //     router.push({
    //         path: '/scene/leaderModel',
    //         query: { 'sceneId': item.id }
    //     })
    // } else if (item.sceneType === 'exam') {
    //     router.push({
    //         path: '/scene/exam',
    //         query: { 'sceneId': item.id }
    //     })
    // } else if (item.sceneType === 'video_clip') {
    //     router.push({
    //         path: '/scene/mediaClip',
    //         query: { 'sceneId': item.id }
    //     })
    // } else {  // 默认长文本模型
    //     router.push({
    //         path: '/scene/longText',
    //         query: { 'sceneId': item.id }
    //     })
    // }

}

/** 获取场景列表 */
function handleScreenList() {
    sceneList().then(res => {
        sceneLists.value = res.data
    })
}

onMounted(() => {
    handleScreenList()
})

</script>

<style lang="scss" scoped>
.scene-card-container {
    display: flex;
    flex-grow: 1;
    border-radius: 8px;
}

.scene-card {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    overflow: hidden;
    padding: 12px 12px 16px;
    border: 1px solid rgba(6, 7, 9, 0.1);
    border-radius: 8px;
    background-color: #fff;
    cursor: pointer;
    transition: box-shadow 0.3s;

    &:hover {
        box-shadow: var(--coz-shadow-large);
    }

    .scene-image-container {
        position: relative;
        width: 100%;
        height: 100px;
        // height: 100px;
        // width: 100px;
        border-radius: 4px;
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

        .scene-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 8px 0px;

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
                color: var(--coz-fg-primary);
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
        }

        .scene-footer {
            margin-top: 4px;
            display: flex;
            align-items: center;
            justify-content: space-between;

            .scene-price {
                font-weight: 500;
                font-size: 16px;
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
    }
}
</style>