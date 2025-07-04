<template>
  <div class="protoype-function-panel">
    <div class="title">
      <i class="fa-solid fa-feather"></i> AI 原型设计 
    </div>
    <div class="add-task">
      <router-link :to="'/scene/prototypeDesign/index?sceneId=' + sceneId">
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
            <!-- 
              <span class="item-label"></span>
              <span class="item-log">{{ item.log }}</span> 
            -->
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
    "name": "原型管理",
    "icon": "fa-solid fa-file-powerpoint",
    "status": "done",
    "log": "已完成 原型上传与分类管理",
    "isSelected": false,
    "link": "/scene/prototypeDesign/prototypeManager"
  }
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
.protoype-function-panel {
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