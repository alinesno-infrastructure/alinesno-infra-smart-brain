<template>
  <div class="ppt-function-panel">
    <div class="title">
      <i class="fa-solid fa-graduation-cap"></i> 项目检索分析
    </div>
    <div class="add-task">
      <router-link :to="'/scene/productResearch/index?sceneId=' + sceneId">
        <el-button type="primary" text bg size="large" style="width:100%;">
          <i class="fa-solid fa-plus"></i> 导入新项目 
        </el-button>
      </router-link>
    </div>
    <div class="task-list">
      <div v-for="(item, index) in functionList" :key="index" class="task-item" @click="handleTaskClick(item)" :class="{ 'active': item.isSelected }">
          <div class="item-status">
            <span>
              <i :class="item.icon"></i>
            </span>
          </div>  
          <div class="item-name">
            <span class="item-label">
                {{ item.name }}
            </span>
            <span class="item-log">{{ item.log }}</span>
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
  // 添加数据分析
  {
    "name": "Agent分析", // 新增菜单：AI分析
    "icon": "fa-solid fa-robot", // AI相关图标
    "status": "new", // 状态示例：新功能
    "log": "智能体数据分析与洞察建议",
    "isSelected": false,
    "link": "/scene/productResearch/agentChatPanel" // 路由链接
  },
  {
    "name": "项目管理", // 新增菜单：项目管理
    "icon": "fa-solid fa-tasks", // 示例图标（可根据需求更换）
    "status": "progress", // 状态示例：进行中
    "log": "进行项目计划与进度跟踪", // 操作日志示例
    "isSelected": false,
    "link": "/scene/productResearch/projectManagement" // 路由链接
  },
  {
    "name": "任务监控", // 新增菜单：任务监控
    "icon": "fa-solid fa-eye", // 示例图标
    "status": "wait", // 状态示例：待开始
    "log": "接入任务执行数据监控", // 操作日志示例
    "isSelected": false,
    "link": "/scene/productResearch/taskMonitoring" // 路由链接
  },
  {
    "name": "团队配置", // 新增菜单：团队配置
    "icon": "fa-solid fa-users", // 示例图标
    "status": "done", // 状态示例：已完成
    "log": "团队成员权限与协作规则", // 操作日志示例
    "isSelected": false,
    "link": "/scene/productResearch/teamConfiguration" // 路由链接
  }
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
    text-align: left;
    font-size: 15px;
    margin-left: 5px;
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

      .item-status{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 30px;
        font-size: 16px;
        color: #666; // 增加图标默认颜色
        margin-right: 10px; // 增加图标与文字间距
      }

      .item-name{
        display: flex;
        flex-direction: column;
        gap: 5px;
        line-height: 1rem;

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