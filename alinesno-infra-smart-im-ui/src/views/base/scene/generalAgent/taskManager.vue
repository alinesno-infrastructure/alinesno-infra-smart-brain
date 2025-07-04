<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <el-scrollbar style="height:calc(100vh - 50px)">
          <div class="tpl-app" style="display: flex;margin-left: 0px;width:100%;background-color: #fff;">

            <!-- <SideTypePanel /> -->

            <div style="width: calc(100%);margin-top: 10px;" v-loading="sceneLoading">

              <div class="search-container-panel">
                <el-row>
                  <el-col :span="24">
                    <div class="feature-team-box">
                      <div style="gap: 12px;">
                        <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                          任务列表 
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

              <div class="channel-container-panel" style="margin-top:20px">
                <el-row>
                  <el-col :span="6" v-for="(item, index) in pagerList" :key="index" style="padding:8px;">
                    <div class="exam-pager-card-container" @click="enterExamPager(item)">
                      <article class="exam-pager-card">
                        <div class="exam-pager-card-content">
                          <div class="scene-header">
                            <span class="scene-title">{{ item.taskName }}</span>
                          </div>

                          <div class="scene-description">
                            {{ item.taskDescription }}
                          </div>
                          <div class="semi-divider semi-divider-horizontal"></div>
                          <div class="scene-footer">
                            <!-- 
                            <div class="scene-price">
                              <el-tag v-if="item.sceneScope == 'private'" type="info"><i class="fa-solid fa-lock" />
                                私有</el-tag>
                              <el-tag v-else-if="item.sceneScope == 'public'" type="info"><i
                                  class="fa-solid fa-globe" /> 公开</el-tag>
                              <el-tag v-else type="info"><i class="fa-solid fa-truck-plane" />
                                组织</el-tag>
                            </div> 
                            -->
                            <div class="scene-price">
                              <!-- 大纲状态 -->
                              <el-button text bg v-if="item.taskStatus === '0' || item.taskStatus === null" type="warning">
                                <i class="fa-solid fa-hourglass-start" /> 大纲未开始
                              </el-button>

                              <el-button text bg  v-else-if="item.taskStatus === '2'" type="primary">
                                <i class="fa-solid fa-spinner fa-spin" /> 大纲生成中
                              </el-button>

                              <el-button text bg 
                                v-else-if="item.taskStatus === '1' && (item.chapterStatus === '0' || item.chapterStatus === null)"
                                type="warning">
                                <i class="fa-solid fa-list-check" /> 章节未开始
                              </el-button>

                              <!-- 章节状态 -->
                              <el-button text bg  v-else-if="item.chapterStatus === '2'" type="primary">
                                <i class="fa-solid fa-spinner fa-spin" />
                                生成章节中
                                <!-- : {{ item.currentChapterLabel || '当前章节' }} -->
                              </el-button>

                              <el-button text bg  v-else-if="item.chapterStatus === '1'" type="success">
                                <i class="fa-solid fa-check-circle" /> 全部完成
                              </el-button>

                              <!-- 未知状态 -->
                              <el-button text bg  v-else type="info">
                                <i class="fa-solid fa-question-circle" /> 未知状态
                              </el-button>
                            </div>
                            <div class="scene-tag">
                              <div class="scene-stats">
                                <span>时间</span>
                                <span>{{ item.updateTime? item.updateTime : item.addTime }}</span>
                              </div>

                              <div class="article-delete-btn" @click.stop>
                                <el-popconfirm title="确定要删除吗？" @confirm="handleDelete(item)">
                                  <template #reference>
                                    <el-button type="danger" text bg size="small" @click.stop>
                                      <i class="fa-solid fa-trash"></i>&nbsp;删除
                                    </el-button>
                                  </template>
                                </el-popconfirm>
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
  pagerListByPage , 
  deleteById
} from '@/api/base/im/scene/generalAgent';

// import SideTypePanel from './articleType.vue'

import { onMounted } from 'vue';
import learnLogo from '@/assets/icons/tech_01.svg';
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)

const sceneLoading = ref(true)
const pagerList = ref([])

/** 进入长文本编辑界面 */
function enterExamPager(item) {
  router.push({
    path: '/scene/generalAgent/agentParser',
    query: {
      sceneId: sceneId.value,
      genStatus: true,
      channelStreamId: item.channelStreamId , // snowflake.generate(),
      taskId: item.id,
    }
  })
}

// 删除角色
const handleDelete =(item) => {
  deleteById(item.id).then(res => {
    handlePagerListByPage();
  })
}

/** 获取场景列表 */
function handlePagerListByPage() {

  const data = {
    page: 1,
    limit: 100
  }

  pagerListByPage(data , sceneId.value).then(res => {
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

.semi-divider.semi-divider-horizontal {
    margin-top: 20px;
    margin-bottom: 16px;
    border-bottom: 0.5px solid #f0f0f5;
    display: flex;
    width: 100%;
    box-sizing: border-box;
    color: var(--semi-color-text-0);
}

.exam-pager-card {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow: hidden;
  border: 1px solid rgba(6, 7, 9, 0.1);
  border-radius: 8px;
  background-color: #fff;
  cursor: pointer;
  gap: 10px;
  padding: 15px;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: var(--coz-shadow-large);
  }

  .exam-pager-image-container {
    position: relative;
    width: 100%;
    height: 10px;
    // border-radius: 8px;
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
    padding: 0 4px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    width: calc(100%);
    padding-left: 10px;

    .scene-header {
      display: flex;
      align-items: flex-start;
      gap: 8px;
      margin: 8px 0px;
      margin-top: 0px;
      padding-left: 0px;
      flex-direction: column;

      .scene-title {
        font-weight: 500;
        font-size: 16px;
        line-height: 22px;
        color: var(--coz-fg-primary);
        // white-space: nowrap;
        // overflow: hidden;
        // text-overflow: ellipsis;
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

  .article-delete-btn{
    margin-left: 10px;
  }
  
}
</style>