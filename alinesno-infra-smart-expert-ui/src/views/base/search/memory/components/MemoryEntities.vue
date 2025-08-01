<template>
  <div class="memory-entities">
    <el-table :data="entities" style="width: 100%" @row-click="handleRowClick">
      <el-table-column prop="name" label="名称" sortable />
      <el-table-column prop="type" label="类型" sortable />
      <el-table-column prop="frequency" label="出现频率" sortable />
      <el-table-column prop="lastAccessed" label="最后访问" sortable />
      <el-table-column label="标签">
        <template #default="{ row }">
          <el-tag
            v-for="tag in row.tags"
            :key="tag"
            size="small"
            class="tag"
          >
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
const props = defineProps({
  entities: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['entitySelected'])

const handleRowClick = (entity) => {
  emit('entitySelected', entity)
}
</script>