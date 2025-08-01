<template>
  <div class="memory-relationships">
    <el-table :data="relationships" style="width: 100%">
      <el-table-column prop="from" label="来源" />
      <el-table-column label="关系类型">
        <template #default="{ row }">
          <el-tag :type="getRelationshipType(row.type)">
            {{ row.type }}
          </el-tag>
          <el-progress 
            :percentage="row.strength * 100" 
            :stroke-width="10"
            :show-text="false"
          />
        </template>
      </el-table-column>
      <el-table-column prop="to" label="目标" />
    </el-table>
  </div>
</template>

<script setup>
const props = defineProps({
  relationships: {
    type: Array,
    required: true
  }
})

const getRelationshipType = (type) => {
  const types = {
    '参与项目': 'success',
    '咨询': 'warning',
    '常去': 'primary',
    '使用': 'info',
    '参与': 'danger'
  }
  return types[type] || ''
}
</script>