<template>

  <div class="app-container tpl-app">
    <div class="search-container-panel">
      <el-row>
        <el-col :span="24">
          <div class="feature-header-xqbyQk feature-team-box">
            <div style="gap: 12px;">
              <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                我的场景
              </h1>
            </div>
            <div class="search-container-weDuEn">
              <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索场景" :suffix-icon="Search" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="channel-container-panel" style="margin-top:30px">
      <el-row>

        <el-col :span="6" v-for="(item, index) in screenLists" :key="index" style="padding:8px;">
            <div class="screen-card-container" @click="enterScreen(item)">
              <article class="screen-card">
                  <div class="screen-image-container">
                      <img :src="imagePathByPath(item.screenBanner)" class="screen-card-image">
                  </div>
                  <div class="screen-card-content">
                      <div class="screen-header">
                          <span class="screen-title">{{ item.screenName }}</span>
                          <div class="screen-tag">
                              <span class="screen-tag-icon"></span>
                              <span>{{ item.fieldProp }}</span>
                          </div>
                      </div>
                      <div class="screen-author-info">
                          <img src="http://data.linesno.com/switch_header.png" alt="Author Avatar" class="screen-avatar">
                          <span class="screen-name">罗小东</span>
                          <span class="screen-username">@Easton</span>
                      </div>
                      <div class="screen-description">
                        {{ item.screenDesc }}
                      </div>
                      <div class="screen-footer">
                          <div class="screen-price">免费</div>
                          <div class="screen-stats">
                              <span>{{ item.usage_count }}</span>
                              <span>使用</span>
                          </div>
                      </div>
                  </div>
              </article>
          </div>
        </el-col>
      </el-row>
    </div>

  </div>

</template>

<script setup>

import { 
    screenList 
} from '@/api/base/im/screen' ;
import { onMounted } from 'vue';

const router = useRouter();

const screenLists = ref([]) 

/** 进入长文本编辑界面 */
function enterScreen(item) {
    // 跳转至详情页
    if(item.screenType === 'leader_model'){  // 领导模型
        router.push({
            path: '/screen/leaderModel',
            query: { 'screenId': item.id }
        })
    }else{  // 默认长文本模型
        router.push({
            path: '/screen/longText',
            query: { 'screenId': item.id}
        })
    }
}

/** 获取场景列表 */
function handleScreenList(){
    screenList().then(res => {
        screenLists.value = res.data
    })
}

onMounted(() => {
    handleScreenList()
})

</script>

<style lang="scss" scoped>
.screen-card-container {
    display: flex;
    flex-grow: 1;
    border-radius: 8px;
}

.screen-card {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    overflow: hidden;
    padding: 12px 12px 16px;
    border: 1px solid rgba(6, 7, 9, 0.1) ;
    border-radius: 8px;
    background-color: #fff ; 
    cursor: pointer;
    transition: box-shadow 0.3s;

    &:hover {
        box-shadow: var(--coz-shadow-large);
    }

    .screen-image-container {
        position: relative;
        width: 100%;
        height: 140px;
        border-radius: 4px;
        overflow: hidden;

        .screen-card-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
        }
    }

    .screen-card-content {
        margin-top: 8px;
        padding: 0 4px;
        flex-grow: 1;
        display: flex;
        flex-direction: column;

        .screen-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 8px 0px;

            .screen-title {
                font-weight: 500;
                font-size: 16px;
                line-height: 22px;
                color: var(--coz-fg-primary);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .screen-tag {
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

        .screen-author-info {
            display: flex;
            align-items: center;
            gap: 4px;
            margin-top: 4px;

            .screen-avatar {
                width: 14px;
                height: 14px;
                border-radius: 50%;
            }

            .screen-name, .screen-username {
                font-weight: 500;
                font-size: 12px;
                line-height: 16px;
                color: var(--coz-fg-primary);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
        }

        .screen-description {
            margin-top: 8px;
            margin-bottom: 8px;
            font-size: 14px;
            line-height: 20px;
            color: var(--coz-fg-secondary);
            display: -webkit-box;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .screen-footer {
            margin-top: 4px;
            display: flex;
            align-items: center;
            justify-content: space-between;

            .screen-price {
                font-weight: 500;
                font-size: 16px;
                line-height: 22px;
                color: var(--coz-fg-primary);
            }

            .screen-stats {
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