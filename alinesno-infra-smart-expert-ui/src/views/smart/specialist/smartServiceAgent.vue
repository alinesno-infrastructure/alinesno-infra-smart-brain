<template>
  <div class="robot-chat-help-container">
    <div class="robot-chat-help-panel">
      <div class="robot-chat-help-title">专家服务Agent</div>
      <div class="robot-chat-help-item-list-panel">
        <div class="process-panel">
          <ul>
            <li class="item-process" v-for="(item, index) in favouriteList" :key="index">
              <img style="width:30px;height:30px;border-radius: 50%;position: absolute;" :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 35) + '.png'" />
              <div
                style="margin-left: 40px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis; margin-top: -2px;">
                {{ item.name }}
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="robot-chat-help-panel">
      <div class="robot-chat-help-title">任务执行Agent</div>
      <div class="robot-chat-help-item-list-panel">
        <div class="process-panel">
          <ul>
            <li class="item-process" v-for="(item, index) in helpAutoList" :key="item.id">
              <img style="width:30px;height:30px;border-radius: 50%;position: absolute;"
                :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 15) + '.png'" />
              <div
                style="margin-left: 40px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis; margin-top: -2px;">
                {{ item.name }}
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

const favouriteList = ref([
  { id: '1', icon: 'fa-solid fa-truck-fast', name: '技术指导Agent' },
  { id: '2', icon: 'fa-solid fa-pen-nib', name: '技术学习材料Agent' },
  { id: '3', icon: 'fa-solid fa-pen-ruler', name: '解决方案编写Agent' },
  { id: '4', icon: 'fa-solid fa-paint-roller', name: '技术架构分析Agent' },
  { id: '7', icon: 'fa-solid fa-file-word', name: '代码工程结构Agent' },
  { id: '4', icon: 'fa-solid fa-compass-drafting', name: '需求转代码Agent' },
  { id: '6', icon: 'fa-solid fa-user-nurse', name: '数据库设计Agent' },
])

const helpAutoList = ref([
  { id: '1', icon: 'fa-solid fa-truck-fast', name: '技术指导Agent' },
  { id: '2', icon: 'fa-solid fa-pen-nib', name: '技术学习材料Agent' },
  { id: '8', icon: 'fa-brands fa-dashcube', name: '考核题目生成Agent' },
  { id: '3', icon: 'fa-solid fa-pen-ruler', name: '解决方案编写Agent' },
  { id: '4', icon: 'fa-solid fa-paint-roller', name: '技术架构分析Agent' },
  { id: '7', icon: 'fa-solid fa-file-word', name: '代码工程结构Agent' },
  { id: '4', icon: 'fa-solid fa-compass-drafting', name: '需求转代码Agent' },
  { id: '5', icon: 'fa-solid fa-feather', name: '技术架构Agent' },
  { id: '6', icon: 'fa-solid fa-user-nurse', name: '数据库设计Agent' },
])

</script>