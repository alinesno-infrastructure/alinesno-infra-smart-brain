<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <!-- :class="{ isSelected: props.isSelected?'isSelected':'' }" -->
  <div class="workflow-node-container">
    <!-- 节点标题部分，包含图标和名称 -->
    <div class="node-title">
      <div class="node-icon">
        <i :class="props.properties.icon"></i>
      </div>
      <div class="node-name">
        {{ props.properties.stepName }}
      </div>
    </div>
    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div>
        <!-- 节点设置表单区域 -->
        <div class="settings-form condition-panel">
          <div class="settings-title">IF</div>
          <el-row>
            <el-col :span="10">
              <el-select v-model="value" placeholder="请选择变量" style="margin-right:8px;">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="value" placeholder="请选择条件" style="margin-right:8px;">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-col>
            <el-col :span="7">
              <el-input v-model="value" placeholder="请输入值" />
            </el-col>
            <el-col :span="1">
              <div class="condition-node-remove-icon">
                <i class="fa-solid fa-file-pdf" />
              </div>
            </el-col>
          </el-row>
          <div class="condition-node-add">
            <el-button type="primary" text bg>
              + 添加条件
            </el-button>
          </div>
        </div>

        <div class="settings-form condition-panel">
          <div class="settings-title">ELSE</div>
        </div>

      </div>
      <div class="condition-branch-add">
        <el-button type="primary" text bg>
          + 添加分支
        </el-button>
      </div>
    </div>
    <!-- 输出参数部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">输出参数</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        分支名称 {branch_name}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// 定义组件接收的属性
const props = defineProps({
  properties: {
    type: Object,
    default: () => ({})
  },
  isSelected: {
    type: Boolean,
    default: false
  },
  nodeModel: {
    type: Object
  }
});

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

// 自定义样式

</style>