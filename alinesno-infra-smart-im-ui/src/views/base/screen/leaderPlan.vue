<template>
  <div class="task-progress">
    <el-row>
      <el-col :span="24">
        <div class="leader-info">
          <el-avatar :size="50" :src="imagePathByPath('1856467931933298689')"></el-avatar>
          <div>
            AIP内容运营高级经理
            <div class="plan-header" >
              团队
              <span style="left: 0">{{ projectStartDate }}</span>
              到
              <span style="left: 100%">{{ projectEndDate }}</span>
              工作计划安排
            </div>
          </div>
        </div>

        <div class="word-message-info">
          <i class="fa-solid fa-truck-fast"></i> 
          现在是准备1月1日，准备过年，春节期间是消费高峰期，消费者有更多的闲暇时间和消费意愿。通过快速启动产品运营并收集市场反馈，
          可以迅速调整策略，抢占市场份额，确保在新的一年里占据有利位置。
        </div>

      </el-col>
      <!-- 左边的任务列表 -->
      <el-col :span="24">
      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button
               type="primary"
               icon="Plus"
               bg
               text
               @click="handleAdd"
               v-hasPermi="['system:role:add']"
            >新增</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="success"
               bg
               text
               icon="Edit"
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['system:role:edit']"
            >启动</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               bg
               text
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['system:role:remove']"
            >停止</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
        <el-table :data="tasks" style="width: 100%">

          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="title" label="标题" />

          <el-table-column label="进度" width="130" align="center">
            <template #default="scope">
              <el-progress 
                v-if="scope.row.progress == 100"
                :text-inside="true" 
                :stroke-width="20" 
                striped
                :percentage="scope.row.progress"
                :color="progressColor(scope.row.progress)" />
              <el-progress 
                v-else
                :text-inside="true" 
                :stroke-width="20" 
                :percentage="scope.row.progress"
                striped
                striped-flow
                :color="progressColor(scope.row.progress)" />
            </template>
          </el-table-column>

          <el-table-column label="负责人" align="left" width="180">
            <template #default="scope">
              <span style="display: flex;align-items: center;gap: 5px;">
                <el-avatar :size="40" :src="imagePathByPath(scope.row.responsible.avatar)"></el-avatar>
                {{ scope.row.responsible.name }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="start" align="center" width="240" label="任务时间段">
            <template #default="scope">
              <span>{{ scope.row.start }}</span>-<span>{{ scope.row.end }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="priority" width="100" align="center" label="优先级">
            <template #default="scope">
              <el-button bg text>
                {{ scope.row.priority }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <br />

        <el-pagination background layout="total, prev, pager, next" :total="12" />
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import dayjs from 'dayjs';

// 假设项目从2024年1月1日开始，到1月7日结束
const projectStartDate = ref('2024-01-01 00:00');
const projectEndDate = ref('2024-01-01 23:59');

// 每周任务数据
const tasks = ref([
  { id: 1, title: '产品运营启动与初期市场反馈收集', type: '计划组', responsible: { avatar: '1856236263733579778', name: 'AIP品牌故事专员' }, start: '09:00', end: '17:00', progress: 100, priority: '高' },
  { id: 2, title: '深度市场调研与竞品分析报告撰写', type: '市场营销团队', responsible: { avatar: '1850924967857344514', name: '公众号电台主播' }, start: '09:00', end: '17:00', progress: 80, priority: '中' },
  { id: 3, title: '内容规划与编辑：创建有吸引力的用户故事', type: '内容发布组', responsible: { avatar: '1851917403521929217', name: 'AIP内容发布专员' }, start: '09:00', end: '17:00', progress: 70, priority: '低' },
  { id: 4, title: '深入梳理用户需求并制定相应的产品优化方案', type: '迭代', responsible: { avatar: '1852209567086792705', name: 'AIP导购销售专员' }, start: '09:00', end: '17:00', progress: 90, priority: '高' },
  { id: 5, title: '品牌故事创作：构建品牌核心价值传递给受众', type: '设计组', responsible: { avatar: '1853334663864111105', name: 'AIP品牌故事专员' }, start: '09:00', end: '17:00', progress: 60, priority: '中' },
  { id: 6, title: '社交媒体推广：提升品牌知名度和用户参与度', type: '计划组', responsible: { avatar: '1850924967857344514', name: '小红书发布专员' }, start: '09:00', end: '17:00', progress: 100, priority: '高' },
  { id: 7, title: '周末总结：回顾一周工作成果与下周计划安排', type: '计划组', responsible: { avatar: '1852209567086792705', name: '韦恩W' }, start: '09:00', end: '17:00', progress: 100, priority: '高' },
  { id: 8, title: '用户体验优化：根据用户反馈调整界面设计', type: '用户体验团队', responsible: { avatar: '1853334663864111105', name: 'UI设计师' }, start: '09:00', end: '17:00', progress: 50, priority: '高' },
  { id: 9, title: '开发新功能：实现个性化推荐以提高用户满意度', type: '技术开发团队', responsible: { avatar: '1850924967857344514', name: '高级程序员' }, start: '09:00', end: '17:00', progress: 40, priority: '高' },
  { id: 10, title: '合作伙伴关系建立：拓展业务合作渠道和资源', type: '商业合作部门', responsible: { avatar: '1856236263733579778', name: '商务经理' }, start: '09:00', end: '17:00', progress: 30, priority: '中' }
]);

// 计算属性：生成时间轴标签
const timelineDays = computed(() => {
  const days = [];
  let currentDate = dayjs(projectStartDate.value);
  const endDate = dayjs(projectEndDate.value);

  while (currentDate <= endDate) {
    if (currentDate.hour() === 0 && currentDate.minute() === 0) {
      days.push({ label: currentDate.format('M/D'), left: getPercentage(currentDate) });
    }
    currentDate = currentDate.add(1, 'day');
  }
  return days;
});

// 方法：计算甘特条样式
const getBarStyle = (task) => {
  const totalDuration = dayjs(projectEndDate.value).diff(dayjs(projectStartDate.value), 'minute');
  const startMinute = dayjs(task.start).diff(dayjs(projectStartDate.value), 'minute');
  const durationMinute = dayjs(task.end).diff(dayjs(task.start), 'minute');

  return {
    left: `${(startMinute / totalDuration) * 100}%`,
    width: `${(durationMinute / totalDuration) * 100}%`
  };
};

// 方法：计算百分比位置
const getPercentage = (date) => {
  const totalDuration = dayjs(projectEndDate.value).diff(dayjs(projectStartDate.value), 'minute');
  const diff = dayjs(date).diff(dayjs(projectStartDate.value), 'minute');
  return (diff / totalDuration) * 100;
};

// 方法：格式化时间
const formatTime = (time) => {
  return dayjs(time).format('M/D HH:mm');
};

// 方法：根据进度返回颜色
const progressColor = (progress) => {
  if (progress >= 90) return '#67C23A'; // Success
  else if (progress >= 70) return '#409EFF'; // Brand Color
  else if (progress >= 50) return '#E6A23C'; // Warning
  else if (progress >= 30) return '#F56C6C'; // Danger
  else return '#909399'; // Info
};

// 方法：根据优先级返回标签类型
const priorityTagType = (priority) => {
  switch (priority) {
    case '高':
      return 'danger';
    case '中':
      return 'warning';
    case '低':
      return 'info';
    default:
      return '';
  }
};
</script>

<style scoped>
.task-progress {
  display: flex;
}

.timeline-header {
  position: relative;
  height: 40px;
  margin-bottom: 10px;
  border-bottom: 1px solid #ddd;
}

.timeline-header span {
  position: absolute;
  top: -10px;
  font-size: 12px;
  transform: translateX(-50%);
}

.gantt-item {
  height: 40px;
  margin-bottom: 10px;
  position: relative;
}

.gantt-bar {
  background-color: #67C23A;
  height: 20px;
  position: absolute;
  bottom: 0;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.gantt-bar span {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  color: white;
  padding: 0 5px;
}

.start-time,
.end-time {
  position: absolute;
  top: -20px;
  color: #333;
  font-size: 12px;
}

.start-time {
  left: 0;
}

.end-time {
  right: 0;
}

.plan-header {
  margin-bottom: 0px !important;
  margin-top:3px;
  font-size: 14px;
  color: #909399;
}

.leader-info {
    display: flex;
    align-items: center;
    gap: 10px;
    background: #f5f5f5;
    padding: 10px;
    border-radius: 5px;
    margin-bottom: 10px;
}

.word-message-info {
    font-size: 14px;
    color: #909399;
    line-height: 1.4rem;
    margin-top:10px;
    margin-bottom: 20px;
}

</style>