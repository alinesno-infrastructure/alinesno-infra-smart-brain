<template> 
      <DocumentReviewContainer>
        <el-scrollbar style="height:calc(100vh - 40px)">
          <div class="tpl-app" style="display: flex;height:calc(100vh - 40px);margin-left: 0px;width:100%;background-color: #fff;">

            <!-- <SideTypePanel /> -->

            <div style="width: calc(100%);margin-top: 10px;" v-loading="sceneLoading">

              <div class="search-container-panel">
                <el-row>
                  <el-col :span="24">
                    <div class="feature-team-box">
                      <div style="gap: 12px;">
                        <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                          审核列表 
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
                    <div class="exam-pager-card-container" >
                      <article class="exam-pager-card">
                        <div class="exam-pager-card-content">
                          <div class="scene-header" @click="enterExamPager(item)">
                            <span class="scene-title">{{ item.taskName }}</span>
                            <div class="scene-datetime">
                             <i class="fa-solid fa-calendar-check" /> {{ item.addTime }} 
                            </div>
                          </div>
                        
                          <div class="scene-description">
                            {{ item.taskDescription }}
                          </div>
                          <div class="semi-divider semi-divider-horizontal" style="margin-top:10px;margin-bottom:8px;"></div>

                          

                          <div class="scene-footer">

                            <div class="scene-price">
                              <el-button 
                                text 
                                bg 
                                :type="getStatusConfig(item).type"
                              >
                                <i :class="`fa-solid ${getStatusConfig(item).icon}`" /> 
                                {{ typeof getStatusConfig(item).text === 'function' 
                                   ? getStatusConfig(item).text(item) 
                                   : getStatusConfig(item).text 
                                }}
                              </el-button>
                            </div> 

                            <div class="scene-tag">
                              <div class="scene-stats">
                                <span>用时</span>
                                <span>{{ taskUseTime(item.taskStartTime , item.taskEndTime) }}</span>
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
      </DocumentReviewContainer>
 
</template>

<script setup>

import FunctionList from './functionList'

import {
  pagerListByPage ,
  deleteById
} from '@/api/base/im/scene/documentReviewTask';

// import SideTypePanel from './articleType.vue'

import { onMounted } from 'vue';
import learnLogo from '@/assets/icons/tech_01.svg';
import SnowflakeId from "snowflake-id";
import DocumentReviewContainer from "./DocumentReviewContainer"
const snowflake = new SnowflakeId();

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)

const sceneLoading = ref(true)
const pagerList = ref([])

/** 进入长文本编辑界面 */
function enterExamPager(item) {

  router.push({
      path: '/scene/documentReview/documentParser',
      query: {
          sceneId: sceneId.value , 
          genStatus: true,
          taskId: item.id,
          channelStreamId: item.channelStreamId
      }
  })

}

const statusConfig = [ 
  // 文档解析状态
  {condition: (item) => item.documentParseStatus === 'generating', type: "primary", icon: "fa-spinner fa-spin", text: "文档解析中"},
  {condition: (item) => item.documentParseStatus === 'not_generated' || item.documentParseStatus === null, type: "warning", icon: "fa-hourglass-start", text: "文档未解析"},
  
  // 审核清单生成状态
  {condition: (item) => item.reviewGenStatus === 'generating', type: "primary", icon: "fa-spinner fa-spin", text: "审核清单生成中"},
  {condition: (item) => item.reviewGenStatus === 'failed_no_content', type: "danger", icon: "fa-circle-xmark", text: "生成失败(无内容)"},
  {condition: (item) => item.reviewGenStatus === 'failed', type: "danger", icon: "fa-circle-xmark", text: "审核清单生成失败"},
  {condition: (item) => item.reviewGenStatus === 'timeout', type: "danger", icon: "fa-clock", text: "生成超时"},
  {condition: (item) => item.reviewGenStatus === 'not_generated' || item.reviewGenStatus === null, type: "warning", icon: "fa-hourglass-start", text: "未生成审核清单"},
  
  // 审核结果生成状态 (放在最后，因为这是最终状态)
  {condition: (item) => item.resultGenStatus === 'generating', type: "primary", icon: "fa-spinner fa-spin", text: "审核结果生成中"},
  {condition: (item) => item.resultGenStatus === 'not_generated' || item.resultGenStatus === null, type: "warning", icon: "fa-hourglass-start", text: "未生成审核结果"},
  
  // 成功状态 (放在更后面)
  {condition: (item) => item.reviewGenStatus === 'success' && item.resultGenStatus === 'success', type: "success", icon: "fa-check-circle", text: "审核完成"},
  {condition: (item) => item.reviewGenStatus === 'success', type: "success", icon: "fa-check-circle", text: "审核清单生成"},
  
  // 默认状态
  {condition: () => true, type: "info", icon: "fa-question-circle", text: "未知状态"},
]

// 查找匹配的状态配置
const getStatusConfig = (item) => {
  const matchedConfig = statusConfig.find(config => config.condition(item)) || statusConfig[statusConfig.length - 1]
  console.log('Matching status for item:', item, 'Matched config:', matchedConfig.text)
  return matchedConfig
}

const handleDelete = (item) => {
  deleteById(item.id).then(res => {
    handlePagerListByPage()
  })
}

/** 获取场景列表 */
function handlePagerListByPage() {

  const data = {
    page: 1,
    limit: 30
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
        white-space: nowrap;      /* 禁止换行 */
        overflow: hidden;         /* 隐藏超出部分 */
        text-overflow: ellipsis;  /* 超出部分显示省略号 */
        display: block;          /* 或 inline-block，取决于你的布局需求 */
        width: 100%;             /* 需要指定一个宽度 */
        font-weight: bold ;
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

    .scene-datetime {
      font-size: 13px;
      border-radius: 5px;
      background: #fafafa;
      opacity: 0.7;
      margin-top: 5px;
      padding: 5px;
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