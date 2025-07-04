<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <el-scrollbar style="height:calc(100vh - 50px)">
          <div class="tpl-app" style="display: flex;margin-left: 0px;width:100%;background-color: #fff;">

            <div style="width: calc(100%);margin-top: 10px;" v-loading="sceneLoading">

              <div class="search-container-panel">
                <el-row>
                  <el-col :span="24">
                    <div class="feature-team-box">
                      <div style="gap: 12px;">
                        <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                         检索管理 
                        </h1>
                      </div>
                      <div class="search-container-weDuEn">
                        <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索项目"
                          :suffix-icon="Search" />
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="channel-container-panel" style="margin-top:20px">
                <el-row>
                  <el-col :span="6" v-for="(item, index) in pagerList" :key="index">
                    <div class="exam-pager-card-container">
                      <article class="exam-pager-card">
                        <div class="exam-pager-card-content">
                          <div class="scene-header">

                            <span class="scene-title" @click="enterProductParser(item)">
                              <i v-if="index%2==0" class="fa-brands fa-github"></i>
                              <i v-if="index%2==1" class="fa-brands fa-square-gitlab"></i>
                              {{ item.taskName || "名称未解析" }}
                            </span>

                            <span>
                              <el-button v-if="item.syncInterval == 0" type="text" size="small" text>
                                <i class="fa-solid fa-arrows-rotate"></i> 同步
                              </el-button>
                              <el-button v-if="item.syncInterval == 0" type="text" size="small" text>
                                <i class="fa-solid fa-screwdriver-wrench"></i> 配置
                              </el-button>
                            </span>

                          </div>
                          <div class="scene-author-info">
                            <span class="scene-name">
                              {{ item.outline }}
                            </span>
                          </div>
                          <div style="padding: 10px 0px;"></div>
                          <div class="scene-footer">
                            <div class="scene-price">
                              <el-button text bg
                                  :type="getStatusType(item.status)" 
                                  abled="item.status === 'PROCESSING' || item.status === 'QUEUED'"
                                  size="default">
                                  <i :class="getStatusIcon(item.status)" class="mr-1"></i>&nbsp;{{ getStatusText(item.status) }}
                                </el-button>
                            </div>
                            <div class="scene-tag">
                              <div class="scene-stats" v-if="item.addTime">
                                 <i class="fa-solid fa-clock"></i> 最后更新: <span>{{ item.addTime }}</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </article>
                    </div>
                  </el-col>

                  <el-col :span="24" v-if="pagerList.length == 0">
                    <el-empty :image-size="400" :image="learnLogo" description="当前未创建业务场景，你的业务场景还未为空，可以在侧边栏快速创建。" />
                  </el-col>

                </el-row>
              </div>
            </div>

          </div>
        </el-scrollbar>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import FunctionList from './functionList'

import {
  pagerListByPage
} from '@/api/base/im/scene/productResearch';

// import SideTypePanel from './pptType.vue'

import { onMounted } from 'vue';
import learnLogo from '@/assets/icons/tech_01.svg';

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)

const sceneLoading = ref(true)
const pagerList = ref([])

/** 进入长文本编辑界面 */
function enterProductParser(item) {
  const path = '/scene/productResearch/productParser';
  router.push({
    path: path,
    query: { 
      'taskId': item.id ,
      'sceneId': sceneId.value
    }
  })
}

// 状态类型映射
const getStatusType = (status) => {
  const typeMap = {
    QUEUED: "info",
    PROCESSING: "primary",
    COMPLETED: "success",
    FAILED: "danger",
    RETRYING: "warning",
    CANCELED: "default"
  };
  return typeMap[status] || "default";
};

// 状态文本映射
const getStatusText = (status) => {
  const statusMap = {
    QUEUED: "排队中",
    PROCESSING: "处理中",
    COMPLETED: "已完成",
    FAILED: "失败",
    RETRYING: "重试中",
    CANCELED: "已取消"
  };
  return statusMap[status] || status;
};

// 状态图标映射
const getStatusIcon = (status) => {
  const iconMap = {
    QUEUED: "fa-solid fa-clock",
    PROCESSING: "fa-solid fa-spinner fa-spin",
    COMPLETED: "fa-solid fa-check",
    FAILED: "fa-solid fa-times",
    RETRYING: "fa-solid fa-refresh",
    CANCELED: "fa-solid fa-ban"
  };
  return iconMap[status] || "fa-solid fa-question";
};

/** 获取场景列表 */
function handlePagerListByPage() {
  pagerListByPage(sceneId.value).then(res => {
    pagerList.value = res.data
    sceneLoading.value = false
  }).catch(err => {
    sceneLoading.value = false
  })  
}

onMounted(() => {
  handlePagerListByPage()
})

</script>

<style lang="scss" scoped>
.exam-pager-card-container {
  display: flex;
  flex-grow: 1;
  border-radius: 8px;
}

.exam-pager-card {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow: hidden;
  border: 1px solid rgba(6, 7, 9, 0.1);
  border-radius: 8px;
  background-color: #fff;
  gap: 10px;
  padding-bottom:0px;
  margin-right: 20px;
  margin-bottom: 20px;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: var(--coz-shadow-large);
  }

  .exam-pager-image-container {
    position: relative;
    width: 100%;
    height: 10px;
    overflow: hidden;

    .exam-pager-card-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      object-position: center;
    }
  }

  .exam-pager-card-content {
    margin-top: 10px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    width: calc(100%);
    padding: 20px;

    .scene-header {
      // display: flex;
      // align-items: flex-start;
      // gap: 8px;
      // margin: 8px 0px;
      // margin-top: 0px;
      // padding-left: 0px;
      // flex-direction: column;

      display: flex;
      align-items: center;
      gap: 8px;
      margin: 8px 0px;
      margin-top: 0px;
      padding-left: 0px;
      flex-direction: row;
      justify-content: space-between;

      .scene-title {
        font-weight: 500;
        font-size: 16px;
        line-height: 22px;
        color: var(--coz-fg-primary);
        cursor: pointer;
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

      .scene-name{
        white-space: normal !important;
        height: 50px !important;
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