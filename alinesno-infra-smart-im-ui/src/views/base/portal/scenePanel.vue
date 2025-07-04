<template>

    <div style="display: flex;margin-left: 0px;">

        <div style="width: calc(100% - 0px);margin-top: 0px;" v-loading="fullscreenLoading">

            <div class="channel-container-panel" style="margin-top:0px">
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
                                        <span class="semi-typography text"
                                            style="flex: 1;font-size: 13px;font-weight: 400;line-height: 18px;color: #a5a5a5;">
                                            <i class="fa-solid fa-user-shield"></i>
                                            {{ item.orgName }}
                                        </span>
                                    </div>
                                    <div class="scene-description">
                                        {{ item.sceneDesc }}
                                    </div>
                                    <div class="semi-divider semi-divider-horizontal"></div>
                                    <div class="scene-footer">
                                        <el-button type="primary" size="small" text bg>
                                            <i :class="JSON.parse(item.fieldProp)?.icon"></i>&nbsp;{{JSON.parse(item.fieldProp)?.sceneName }}
                                        </el-button>
                                        <div class="scene-tag">
                                            <div class="scene-stats">
                                                <div class="scene-price">
                                                    <el-tag v-if="item.sceneScope == 'private'" type="danger"><i
                                                            class="fa-solid fa-lock" /> 私有</el-tag>
                                                    <el-tag v-else-if="item.sceneScope == 'public'" type="warning"><i
                                                            class="fa-solid fa-globe" /> 公开</el-tag>
                                                    <el-tag v-else type="info"><i class="fa-solid fa-truck-plane" />
                                                        组织</el-tag>
                                                </div>
                                                <span>使用</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </article>
                            <div class="scene-card-footer">

                            </div>
                        </div>
                    </el-col>

                    <el-col :span="24" v-if="sceneLists.length == 0 && !fullscreenLoading">
                        <el-empty :image-size="400" :image="learnLogo" description="当前未创建业务场景，你的业务场景还未为空，可以在侧边栏快速创建。" />
                    </el-col>

                </el-row>
                <!-- </el-scrollbar> -->
            </div>
        </div>

    </div>

</template>

<script setup>


import {
    getWorkplaceItem
} from "@/api/base/im/workplace"

import learnLogo from '@/assets/icons/tech_01.svg';
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();
const channelStreamId = ref(snowflake.generate());

const router = useRouter();
const fullscreenLoading = ref(true);
const sceneLists = ref([])

/** 进入长文本编辑界面 */
function enterScreen(item) {
    const path = '/scene/' + item.sceneType + '/index';
    router.push({
        path: path,
        query: { 'sceneId': item.id, 'channelStreamId': channelStreamId.value }
    })
}

const handleGetWorkplaceItem = (workplaceId, type) => {
    getWorkplaceItem(workplaceId, type).then(response => {
        sceneLists.value = response.data || [];
        fullscreenLoading.value = false;
    })
}

// 对外暴露的方法 
defineExpose({
    handleGetWorkplaceItem
})


</script>

<style lang="scss" scoped>
.scene-card-container {
    display: flex;
    flex-grow: 1;
    border-radius: 8px;
}

.semi-divider {
    margin-top: 20px;
    margin-bottom: 16px;
    border-bottom: .5px solid #f0f0f5;
    display: flex;
    width: 100%;
    box-sizing: border-box;
    color: var(--semi-color-text-0);
}

.scene-card {
    display: flex;
    flex-direction: row;
    flex-grow: 1;
    overflow: hidden;
    // padding: 12px 12px 16px;
    padding: 20px;
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
            color: #899;
        }

        .scene-footer {
            margin-top: 4px;
            display: flex;
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
    }
}
</style>