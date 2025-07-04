<template>
  <div class="deepsearch-task-panel">
    <div class="title">
      <i class="fa-solid fa-poo-storm"></i> 深度搜索
    </div>
    <div class="add-task">
      <el-button type="primary" text bg size="large" style="width:100%;" @click="addTask">
        <i class="fa-solid fa-plus"></i> 添加任务
      </el-button>
    </div>
    <div class="task-list">
      <div v-for="(item, index) in taskList" :key="index" class="task-item" @click="handleTaskClick(item)" :class="{ 'active': item.isSelected }">
        <div class="item-status">
          <span v-if="item.status == 'loading'">
            <i class="fa-solid fa-spinner"></i>
          </span>
          <span v-if="item.status == 'done'">
            <i class="fa-solid fa-check-double"></i>
          </span>
        </div>  
        <div class="item-name">
          <span class="item-label">{{ item.name }}</span>
          <span class="item-log">{{ item.log }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

import { 
 listAllDeepsearchScene 
} from '@/api/base/im/scene/deepSearch';

const route = useRoute();
const sceneId = ref(route.query.sceneId)

const emits = defineEmits([
'handleTaskClick',
'handleNewTask'
])

const taskList = ref([])

const handleTaskClick = (item) => {
  // 取消所有任务的选中状态
  taskList.value.forEach(task => task.isSelected = false);
  // 设置当前任务为选中状态
  item.isSelected = true;
  emits('handleTaskClick' , item)
}

const addTask = () => {
  emits('handleNewTask')
  taskList.value.forEach(task => task.isSelected = false);
}

onMounted(() => {
  listAllDeepsearchScene(sceneId.value).then(res => {
    console.log(res)
    taskList.value = res.data.map(item => {
      return {
        id: item.id,
        name: item.taskName,
        roleId: item.searchPlannerEngineer,
        status: 'done',
        log: '已执行完成，可继续对话',
        isSelected: false
      }
    })
  })
})

</script>

<style lang="scss" scoped>
.deepsearch-task-panel {
  padding: 10px;
  background: #fafafa;
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
      background: #f5f5f5;
      border-radius: 5px;
      padding: 10px;

      &:hover {
        background: #f5f5f5;
      }

      &.active {
        background: #f5f5f5;
      }

      .item-status{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 30px;
        font-size: 16px;
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