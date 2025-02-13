<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <div class="workflow-node-container">
    <!-- 节点标题部分，包含图标和名称 -->
    <div class="node-title">
      <div class="node-icon">
        <i class="fa-solid fa-image"></i>
      </div>
      <div class="node-name">图片理解</div>
    </div>
    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title">节点设置</div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="form" label-width="auto" label-position="top">
          <el-form-item label="图片理解模型">
            <el-select v-model="value" placeholder="请选择图片理解模型" style="width: 240px">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="系统角色">
            <el-input type="textarea" :rows="3" resize="none" placeholder="角色设置" />
          </el-form-item>
          <el-form-item label="提示词">
            <el-input type="textarea" :rows="4" resize="none" placeholder="角色设置" />
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="value1" size="small" />
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!-- 输出参数部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">输出参数</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        回答内容 {answer}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// 绑定选择框的值
const value = ref('')
const value1 = ref('')

// 表单数据对象
const form = reactive({
  name: '',
  region: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})

// 选择框的选项列表
const options = [
  {
    value: 'Option1',
    label: 'Option1',
  },
  {
    value: 'Option2',
    label: 'Option2',
  },
  {
    value: 'Option3',
    label: 'Option3',
  },
  {
    value: 'Option4',
    label: 'Option4',
  },
  {
    value: 'Option5',
    label: 'Option5',
  },
]
</script>

<style lang="scss" scoped>
</style>