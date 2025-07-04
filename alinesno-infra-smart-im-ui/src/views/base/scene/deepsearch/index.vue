<template>
  <div class="deepsearch-app-container">
    <el-container style="height:calc(100vh - 40px );background-color: #fff;">
      <el-aside width="80px" class="deepsearch-app-aside">
        <TaskListPanel 
          @handleNewTask="handleNewTask"
          @handleTaskClick="handleTaskClick" />
      </el-aside>
        <el-main class="deepsearch-app-main">
          <SearchPanel />
          <!-- <ChatPanel v-else  :taskItem="taskItem" /> -->
        </el-main>
    </el-container>
  </div>
</template>

<script setup>

import TaskListPanel from './functionList.vue'
import SearchPanel from './searchPanel.vue'
import ChatPanel from './chatPanel.vue'

const hasNwTask = ref(true)
const taskItem = ref({})

const handleTaskClick = (item) => {
  console.log('item = ' + item.name);
  hasNwTask.value = false 
  taskItem.value = item ;
}

const handleNewTask = () => {
  hasNwTask.value = true
}

</script>

<style lang="scss" scoped>
.deepsearch-app-container {
  padding: 0px ;
  background-color: #f5f5f5 !important;

  .deepsearch-app-aside {
    padding: 0px !important;
    margin-bottom: 0px !important;
    background-color: #f5f5f5 !important;
    border-right: 1px solid #ebebeb !important;
  }

  .deepsearch-app-main {
    padding: 0px !important;
    height: calc(100vh - 60px);
  }

}
</style>
