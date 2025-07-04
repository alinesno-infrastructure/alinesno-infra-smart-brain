<template>
  <div class="ppt-function-panel">
    <div class="title">
      <i class="fa-solid fa-file-word"></i> 项目检索分析
    </div>
    <div class="task-list">
      <div class="add-task">
        <router-link :to="'/scene/productResearch/index?sceneId=' + sceneId">
          <el-button type="primary" text bg size="large" style="width:100%;">
            <i class="fa-solid fa-plus"></i> 检索 
          </el-button>
        </router-link>
      </div>
      <div v-for="(item, index) in functionList" :key="index" class="task-item" @click="handleTaskClick(item)" :class="{ 'active': item.isSelected }">
          <div class="item-status">
            <span>
              <i :class="item.icon"></i>
            </span>
          </div>  
          <div class="item-name">
            {{ item.name }}
            <!-- <span class="item-label">
                {{ item.name }}
            </span> -->
            <!-- <span class="item-log">{{ item.log }}</span> -->
          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)
import {
  getScene,
} from '@/api/base/im/scene/productResearch';

const emits = defineEmits([
  'handleTaskClick',
  'handleNewTask'
])

const currentSceneInfo = ref({});
const progressAnalyzerEngineer = ref(null)

const functionList = ref([
  {
    "name": "检索管理",
    "icon": "fa-solid fa-diagram-project", // 更适合项目管理的图标
    "status": "progress",
    "log": "进行项目计划与进度跟踪",
    "isSelected": false,
    "link": "/scene/productResearch/projectManagement"
  },
  {
    "name": "知识库",
    "icon": "fa-solid fa-book", // 更适合知识库的图标
    "status": "new",
    "log": "智能体数据分析与洞察建议",
    "isSelected": false,
    "link": "/scene/productResearch/agentChatPanel"
  },
]);

const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;

    if (currentSceneInfo.value.progressAnalyzerEngineer) { // 选择配置角色
      progressAnalyzerEngineer.value = currentSceneInfo.value.progressAnalyzerEngineer;
    }

  })
}

const handleTaskClick = (item) => {
  // 取消所有任务的选中状态
  // functionList.value.forEach(task => task.isSelected = false);
  // // 设置当前任务为选中状态
  // item.isSelected = true;
  // emits('handleTaskClick' , item)

  router.push({
    path: item.link ,
    query: {
      sceneId: sceneId.value ,
      channelId: sceneId.value ,
      roleId: progressAnalyzerEngineer.value 
    }
  })

}

const enterFunction = () => {
}

onMounted (() => {
  handleGetScene();
})

</script>

<style lang="scss" scoped>
.ppt-function-panel {
  padding: 10px;
  background: #fff;
  height: calc(100vh - 80px);

  .title {
    margin-bottom: 15px;
    margin-top: 5px;
    font-size: 15px;
    display: flex;
    flex-direction: column;
    align-items: center;
    line-height: 1.5rem;
    text-align: center;
  }

  .task-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 15px;

    .task-item {
      display: flex;
      font-size: 14px;
      cursor: pointer;
      color: #555;
      background: #fafafa;
      border-radius: 5px;
      padding: 10px;
      transition: all 0.3s ease; // 添加过渡效果
      flex-direction: column;

      &:hover {
        background: #e9f2ff; // 优化hover颜色
      }

      &.active {
        background: #409EFF; // 修改选中状态背景色
        color: #fff; // 增加文字对比度
        font-weight: bold;

        .item-status i {
          color: #fff; // 图标颜色同步调整
        }

        .item-log {
          color: #fff !important; // 增加文字对比度
        }

      }

      .item-status {
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 16px;
        color: #666;
      }

      .item-name {
        line-height: 1rem;
        text-align: center;

        .item-label {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          width: 200px;
        }

        .item-log {
          font-size: 13px;
          color: #888;
        }
      }
    }
  }
}
</style>