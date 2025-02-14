<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <div class="workflow-node-container">
    <!-- 节点标题部分，包含图标和名称 -->
    <div class="node-title">
      <div class="node-icon">
        <i class="fa-solid fa-book"></i>
      </div>
      <div class="node-name">知识库检索</div>
    </div>
    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title">节点设置</div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="form" label-width="auto" label-position="top">
          <el-form-item label="关联知识库">
            <el-input type="input" resize="none" placeholder="请选择知识库" />
          </el-form-item>
          <el-form-item label="检索问题">
            <el-input type="input" resize="none" placeholder="检索用户问题" />
          </el-form-item>
        </el-form>
        <div class="settings-title">检索参数</div>
        <div class="settings-content">
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">检索模式</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">向量检索</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">相似度高于</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">0.600</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">引用分段数TOP</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">3</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">最多引用字符</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">5000</span>
            </el-col>
          </el-row>
        </div>
      </div>

    </div>
    <!-- 输出参数部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">输出参数</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        检索结果的分段列表 {paragraph_list}
      </div>
      <div class="output-content">
        满足直接回答的分段列表{is_hit_handling_method_list}
      </div>
      <div class="output-content">
        检索结果{data}
      </div>
      <div class="output-content">
        满足直接回答的分段内容 {directly_return}
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

<style lang="scss" scoped></style>