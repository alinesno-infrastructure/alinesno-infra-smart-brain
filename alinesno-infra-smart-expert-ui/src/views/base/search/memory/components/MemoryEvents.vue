<template>
  <div class="memory-events">
    <el-table :data="events" style="width: 100%" @row-click="handleRowClick">
      <el-table-column prop="title" label="事件标题" />
      <el-table-column prop="type" label="事件类型" />
      <el-table-column prop="date" label="发生时间" sortable />
      <el-table-column prop="duration" label="持续时间(分)" sortable />
      <el-table-column label="参与者">
        <template #default="{ row }">
          <el-tag
            v-for="participant in row.participants"
            :key="participant"
            size="small"
            type="info"
            class="tag"
          >
            {{ participant }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="发生地点" />
    </el-table>
  </div>
</template>

<script setup>
const props = defineProps({
  events: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['eventSelected'])

const handleRowClick = (event) => {
  emit('eventSelected', event)
}
</script>