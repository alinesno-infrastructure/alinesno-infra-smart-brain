<template>
  <div class="exam-function-panel">
    <div class="title">
      <i class="fa-solid fa-file-pdf"></i> AI 智能文档
    </div>
    <div class="add-task">
      <router-link :to="'/scene/contentFormatter/index?sceneId=' + sceneId + '&channelStreamId=' + snowflake.generate()">
        <el-button type="primary" text bg size="large" style="width:100%;">
          <i class="fa-solid fa-plus"></i> 新建
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
            {{ item.name }}

          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)

const emits = defineEmits([
'handleTaskClick',
'handleNewTask'
])

const functionList = ref([
  {
    name: '文档管理', // 菜单名称（可自定义）
    icon: 'fa-solid fa-file-pen', // Font Awesome 6.x 试卷/编辑图标
    status: 'done', // 任务状态（可根据实际情况设置为 loading/done）
    log: '已完成排版的文档', // 任务描述
    isSelected: false, 
    link: '/scene/contentFormatter/documentManager' // 路由链接（需根据实际业务路径调整）
  },
])

const handleTaskClick = (item) => {
  router.push({
    path: item.link ,
    query: {
      sceneId: sceneId.value 
    }
  })

}

const enterFunction = () => {
}

</script>

<style lang="scss" scoped>
.exam-function-panel {
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