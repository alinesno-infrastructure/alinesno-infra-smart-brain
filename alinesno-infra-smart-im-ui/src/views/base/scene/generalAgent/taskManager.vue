<template>
  <GeneralAgentContainer>
        <el-scrollbar style="height:calc(100vh - 40px)">
          <div class="tpl-app"> 
            <div style="width: calc(100%);margin-top: 10px;" v-loading="sceneLoading">

              <div class="search-container-panel">
                <el-row>
                  <el-col :span="24">
                    <div class="feature-team-box">
                      <div style="gap: 12px;">
                        <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4),var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                          任务列表 
                        </h1>
                      </div>
                      <div class="search-container-weDuEn">
                        <el-input 
                          v-model="searchQuery" 
                          style="width: 400px" 
                          size="large" 
                          placeholder="搜索任务名称或描述"
                          :suffix-icon="Search"
                          clearable
                          @input="handleSearch"
                          @clear="handleSearchClear"
                        />
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="channel-container-panel" style="margin-top:20px">
                <el-row>
                  <el-col :span="6" v-for="(item, index) in filteredPagerList" :key="index" style="padding:8px;">
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
                            {{ generateDocumentSummary(item.outline) }}
                          </div>
                          <div class="semi-divider semi-divider-horizontal"></div>
                          <div class="scene-footer">
               
                            <div class="scene-price">
                              <el-button text bg :type="getTaskStatusInfo(item).type">
                                <i :class="getTaskStatusInfo(item).icon" /> {{ getTaskStatusInfo(item).text }}
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
      </GeneralAgentContainer>
</template>

<script setup>

import FunctionList from './functionList'

import {
  pagerListByPage , 
  deleteById
} from '@/api/base/im/scene/generalAgent';

import { onMounted } from 'vue';
import learnLogo from '@/assets/icons/tech_01.svg';
import SnowflakeId from "snowflake-id";

import { truncateString } from '@/utils/ruoyi'

import GeneralAgentContainer from './generalAgentContainer'
 
const snowflake = new SnowflakeId();

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)

const sceneLoading = ref(true)
const pagerList = ref([])
const searchQuery = ref('')
 
const filteredPagerList = computed(() => {
  if (!searchQuery.value) {
    return pagerList.value
  }
  
  const query = searchQuery.value.toLowerCase()
  return pagerList.value.filter(item => 
    item.taskName.toLowerCase().includes(query))
})
 
const handleSearch = () => { 
}
 
const handleSearchClear = () => {
  searchQuery.value = ''
}

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

const getTaskStatusInfo = (item) => {
  const { genPlanStatus = 'not_run', genContentStatus = 'not_run', genResultStatus = 'not_run' } = item;
  
  // Status priority: result > content > plan
  if (genResultStatus === 'completed') {
    return {
      type: 'success',
      icon: 'fa-solid fa-check-circle',
      text: '全部完成'
    };
  }
  
  if (genResultStatus === 'running') {
    return {
      type: 'primary',
      icon: 'fa-solid fa-spinner fa-spin',
      text: '生成结果中'
    };
  }
  
  if (genContentStatus === 'completed') {
    return {
      type: 'warning',
      icon: 'fa-solid fa-list-check',
      text: '结果未开始'
    };
  }
  
  if (genContentStatus === 'running') {
    return {
      type: 'primary',
      icon: 'fa-solid fa-spinner fa-spin',
      text: item.currentChapterLabel?truncateString(item.currentChapterLabel, 13):'生成内容中'
    };
  }
  
  if (genPlanStatus === 'completed') {
    return {
      type: 'warning',
      icon: 'fa-solid fa-hourglass-start',
      text: '内容未开始'
    };
  }
  
  if (genPlanStatus === 'running') {
    return {
      type: 'primary',
      icon: 'fa-solid fa-spinner fa-spin',
      text: '生成大纲中'
    };
  }
  
  // Default status (not_run or unknown)
  return {
    type: 'warning',
    icon: 'fa-solid fa-hourglass-start',
    text: '大纲未开始'
  };
};

/**
 * 生成文档摘要
 * @param {Object|string|null} jsonData - 可以是JSON对象、JSON字符串或null
 * @returns {string} 生成的摘要文本
 */
const generateDocumentSummary = (jsonData) => {
  try {
    // 处理null/undefined输入
    if (jsonData == null) {
      return '';
    }

    // 如果是字符串则解析为JSON对象，否则直接使用
    const data = typeof jsonData === 'string' ? JSON.parse(jsonData) : jsonData;
    
    // 确保data是数组类型
    if (!Array.isArray(data)) {
      return '';
    }

    // 收集所有label和description文本内容
    let allText = '';
    
    /**
     * 递归提取节点文本
     * @param {Object|null} node - 树节点对象
     */
    function extractText(node) {
      if (!node) return;
      
      // 添加label文本
      if (node.label) allText += node.label + ' ';
      // 添加description文本
      if (node.description) allText += node.description + ' ';
      
      // 递归处理子节点
      if (node.children && Array.isArray(node.children)) {
        node.children.forEach(child => extractText(child));
      }
    }
    
    // 遍历所有顶级节点
    data.forEach(item => extractText(item));
    
    // 去除首尾空格
    const trimmed = allText.trim();
    // 空内容处理
    if (!trimmed) return '';
    
    // 截取前200个字符
    const summary = trimmed.substring(0, 200);
    // 查找最后一个空格位置
    const lastSpace = summary.lastIndexOf(' ');
    
    // 确保不在单词中间截断
    return lastSpace > 0 ? summary.substring(0, lastSpace) + '...' : summary + '...';
  } catch (error) {
    console.error('生成文档摘要时出错:', error);
    return '';
  }
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
  .tpl-app {
    display: flex;
    margin-left: 0px;
    width:100%;
    background-color: #fff;
    height:calc(100vh - 40px)
  }
  
.semi-divider.semi-divider-horizontal {
    margin-top: 10px;
    margin-bottom: 8px;
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
        font-weight: bold;
        font-size: 16px;
        line-height: 22px;
        color: var(--coz-fg-primary); 
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
  .scene-tags{
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

  .article-delete-btn{
    margin-left: 10px;
  }
  
}
</style>