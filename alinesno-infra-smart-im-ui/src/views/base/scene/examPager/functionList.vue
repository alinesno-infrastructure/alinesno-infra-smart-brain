<template>
  <div class="exam-function-panel">
    <div class="title">
      <i class="fa-solid fa-graduation-cap"></i> AI 考试培训 
    </div>
    <div class="add-task">
      <router-link :to="'/scene/examPager/index?sceneId=' + sceneId">
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

const router = useRouter();
const route = useRoute();

const sceneId = ref(route.query.sceneId)

const emits = defineEmits([
  'handleTaskClick',
  'handleNewTask'
])

const functionList = ref([
  {
    name: '考试',
    icon: 'fa-solid fa-graduation-cap', // Font Awesome 6.x 毕业帽（象征考试/教育）
    status: 'loading', // 任务状态示例
    log: '正在进行考试安排与通知', // 任务描述
    isSelected: false,
    link: '/scene/examPager/examManager'
  },
  {
    name: '试卷',
    icon: 'fa-solid fa-file-pen', // Font Awesome 6.x 试卷/编辑图标
    status: 'done', // 任务状态（loading/done）
    log: '已完成试卷创建与审核', // 任务描述
    isSelected: false , // 是否选中
    link: '/scene/examPager/pagerManager'
  },
  {
    name: '考生',
    icon: 'fa-solid fa-user-pen', // 更符合考生概念的图标（用户+编辑）
    status: 'loading',
    log: '考生信息管理与考试分配',
    isSelected: false,
    link: '/scene/examPager/examineeManager' // 使用examinee命名
  },
  // {
  //   name: '题库', // 菜单名称
  //   icon: 'fa-solid fa-book-open-reader', // Font Awesome 6.x 书籍图标（象征知识库/管理）
  //   status: 'done', // 任务状态（可根据实际改为 loading）
  //   log: '已完成题库分类与题目维护', // 任务描述
  //   isSelected: false, 
  //   link: '/scene/examPager/questionBankManager' // 路由链接（建议与业务路径匹配）
  // },
])

const handleTaskClick = (item) => {
  // 取消所有任务的选中状态
  // functionList.value.forEach(task => task.isSelected = false);
  // // 设置当前任务为选中状态
  // item.isSelected = true;
  // emits('handleTaskClick' , item)

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
  height: calc(100vh - 40px);

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