<template>
  <div class="exam-pagercontainer">
    <el-container class="exam-container">
      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <el-scrollbar class="scroll-area">
          <div class="tpl-app">
            <div class="content-wrapper" v-loading="sceneLoading">
              <div class="search-container-panel">
                <el-row>
                  <el-col :span="24">
                    <div class="feature-team-box">
                      <div class="title-wrap">
                        <h1 class="page-title">
                          搜索分析报告
                        </h1>
                      </div>
                      <div class="search-container-weDuEn">
                        <el-input
                          v-model="input1"
                          class="search-input"
                          size="large"
                          placeholder="搜索场景（按回车或清空实时过滤）"
                          clearable
                          suffix-icon="el-icon-search"
                          @keyup.enter.native="onSearch"
                        />
                      </div>
                      <span></span>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="channel-container-panel">
                <el-row>
                  <el-col
                    :span="6"
                    v-for="(item, index) in filteredList"
                    :key="item.id || index"
                    class="col-item"
                  >
                    <div class="exam-pager-card-container" @click="enterExamPager(item)">
                      <article class="exam-pager-card">
                        <div class="exam-pager-card-content">
                          <div class="scene-header">
                            <span class="scene-title">{{ item.taskName }}</span>
                          </div>

                          <div class="scene-tags">
                            <span class="scene-tag-time"><i class="fa-solid fa-calendar-check"></i> {{ item.addTime }}</span>
                          </div>

                          <div class="scene-description">
                            {{ item.taskDescription || '当前还没有执行任务，暂时无任务描述。' }}
                          </div>
                          <div class="semi-divider semi-divider-horizontal"></div>
                          <div class="scene-footer">
                            <div class="scene-price">
                              <el-button
                                :type="getStatusConfig(item).type"
                                text
                              >
                                <i :class="`fa-solid ${getStatusConfig(item).icon}`"></i>
                                &nbsp;
                                {{ typeof getStatusConfig(item).text === 'function'
                                   ? getStatusConfig(item).text(item)
                                   : getStatusConfig(item).text
                                }}
                              </el-button>
                            </div>
                            <div class="scene-tag">
                              <div class="scene-stats">
                                <span>时间</span>
                                <span>{{ taskUseTime(item) }}</span>
                              </div>

                              <div class="article-delete-btn" @click.stop>
                                <el-popconfirm title="确定要删除吗？" @confirm="() => handleDelete(item)">
                                  <template #reference>
                                    <el-button type="info" text size="small" @click.stop>
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

                  <el-col :span="24" v-if="!sceneLoading && filteredList.length === 0">
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
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import FunctionList from './functionList';
import learnLogo from '@/assets/icons/tech_01.svg';

import {
  pagerListByPage,
  deleteById
} from '@/api/base/im/scene/deepSearchTask';

import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId || '');
const sceneLoading = ref(true);
const pagerList = ref([]);
const input1 = ref('');

function enterExamPager(item) {
  router.push({
    path: '/scene/deepsearch/taskPanel',
    query: {
      sceneId: sceneId.value,
      genStatus: true,
      taskId: item.id,
      channelStreamId: item.channelStreamId
    }
  });
}

const handleDelete = async (item) => {
  try {
    await deleteById(item.id);
    ElMessage.success('删除成功');
    await handlePagerListByPage();
  } catch (err) {
    ElMessage.error('删除失败');
    console.error(err);
  }
};

const statusConfig = [
  { condition: (item) => item.taskStatus === 'not_run' || item.taskStatus === null || item.taskStatus === undefined, type: "warning", icon: "fa-hourglass-start", text: "任务未开始" },
  { condition: (item) => item.taskStatus === 'running', type: "primary", icon: "fa-spinner fa-spin", text: "任务生成中" },
  { condition: (item) => item.taskStatus === 'cancelling', type: "warning", icon: "fa-ban fa-spin", text: "任务取消中" },
  { condition: (item) => item.taskStatus === 'cancelled', type: "info", icon: "fa-ban", text: "任务已取消" },
  { condition: (item) => item.taskStatus === 'failed', type: "danger", icon: "fa-circle-xmark", text: "任务失败" },
  { condition: (item) => item.taskStatus === 'completed', type: "success", icon: "fa-check-circle", text: "任务完成" },
];

const getStatusConfig = (item) => {
  return statusConfig.find(config => config.condition(item)) || statusConfig[statusConfig.length - 1];
};

async function handlePagerListByPage() {
  sceneLoading.value = true;
  const data = { page: 1, limit: 100 };
  try {
    const res = await pagerListByPage(data, sceneId.value);
    pagerList.value = Array.isArray(res.data) ? res.data : (res.data?.list || []);
  } catch (err) {
    ElMessage.error('获取任务列表失败');
    console.error(err);
  } finally {
    sceneLoading.value = false;
  }
}

const onSearch = () => {
  // 当前使用本地过滤，按需可在此处调用后端搜索接口
};

const taskUseTime = (item) => {
  if (!item.taskStartTime || !item.taskEndTime) return '--';
  const startTime = new Date(item.taskStartTime);
  const endTime = new Date(item.taskEndTime);
  if (isNaN(startTime.getTime()) || isNaN(endTime.getTime()) || endTime < startTime) return '--';
  const diffSec = Math.floor((endTime - startTime) / 1000);
  if (diffSec < 60) return `${diffSec}秒`;
  const minutes = Math.floor(diffSec / 60);
  const seconds = diffSec % 60;
  if (minutes < 60) return `${minutes}分${seconds}秒`;
  const hours = Math.floor(minutes / 60);
  const remainingMinutes = minutes % 60;
  return `${hours}时${remainingMinutes}分${seconds}秒`;
};

const filteredList = computed(() => {
  const keyword = (input1.value || '').trim().toLowerCase();
  if (!keyword) return pagerList.value;
  return pagerList.value.filter(item => {
    const name = (item.taskName || '').toString().toLowerCase();
    const desc = (item.taskDescription || '').toString().toLowerCase();
    return name.includes(keyword) || desc.includes(keyword);
  });
});

onMounted(() => {
  handlePagerListByPage();
});
</script>

<style lang="scss" scoped>
/* 布局相关 */
.exam-container {
  height: calc(100vh - 40px);
  background-color: #fff;
}

.scroll-area {
  height: calc(100vh - 50px);
}

/* tpl-app 原来带 inline style */
.tpl-app {
  display: flex;
  margin-left: 0;
  width: 100%;
  background-color: #fff;
  height: calc(100vh - 50px);
  box-sizing: border-box;
}

/* 内容容器 */
.content-wrapper {
  width: 100%;
  margin-top: 10px;
  box-sizing: border-box;
}

/* 搜索面板 */
.search-container-panel {
  padding: 0 12px;
}

.feature-team-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 标题 */
.title-wrap {
  display: flex;
  gap: 12px;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 500;
  line-height: 32px;
  color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha));
  margin: 0 0 0 10px;
  float: left;
}

/* 搜索输入框宽度 */
.search-container-weDuEn {
  display: flex;
  align-items: center;
}

.search-input {
  width: 400px;
}

/* 卡片列表容器间距 */
.channel-container-panel {
  margin-top: 20px;
}

/* 每列 padding（替代 el-col 的 inline style）*/
.col-item {
  padding: 8px;
}

/* 保留之前的样式（大部分未改变）*/
.exam-pager-card-container {
  display: flex;
  flex-grow: 1;
  border-radius: 8px;
}

.semi-divider.semi-divider-horizontal {
  margin-top: 10px;
  margin-bottom: 6px;
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
        font-weight: bold;
        font-size: 16px;
        line-height: 22px;
        color: var(--coz-fg-primary);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: block;
        width: 100%;
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
      // height: 40px;
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

  .scene-tags {
    display:flex ;
    span.scene-tag-time {
      font-size: 13px;
      border-radius: 5px;
      background: #fafafa;
      opacity: 0.7;
      margin-top:5px;
      padding: 5px;
    }
  }

  .article-delete-btn {
    margin-left: 10px;
  }
}
</style>