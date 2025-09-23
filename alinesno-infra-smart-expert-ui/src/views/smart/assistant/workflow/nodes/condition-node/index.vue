<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">
    <!-- 节点设置部分 -->
    <div class="node-settings">
      <div>
        <!-- 渲染 IF/ELSE IF 分支（从 branches 读取，实现回显） -->
        <template v-for="(branch, index) in branches" :key="branch.id || index">
          <div 
            ref="conditionPanelsRef" 
            class="settings-form condition-panel"
            :style="{ height: (index == 0 ? '120' : branch.height) + 'px' }"
          >
            <div class="settings-title">
              <!-- 回显分支类型：IF/ELSE IF N -->
              {{ branch.type || (index === 0 ? 'IF' : `ELSE IF ${index}`) }}
              <!-- 仅 ELSE IF 分支显示删除按钮 -->
              <span 
                v-if="index > 0" 
                class="branch-remove-icon" 
                @click="removeBranch(index)"
              >
                <i class="fa-solid fa-trash"></i>
              </span>
            </div>
            <!-- 渲染当前分支的所有条件（回显条件数据） -->
            <template v-for="(condition, conditionIndex) in branch.conditions" :key="conditionIndex">
              <el-row style="margin-bottom:5px;">
                <el-col :span="10">
                  <!-- FlowCascader 绑定 condition.field（与同步逻辑一致） -->
                  <FlowCascader 
                    :nodeModel="props.nodeModel" 
                    v-model="condition.field"
                  />
                </el-col>
                <el-col :span="6" style="padding-left:5px;padding-right:5px;">
                  <el-select 
                    v-model="condition.condition" 
                    placeholder="请选择条件" 
                    style="margin-right:8px;"
                  >
                    <template v-for="(item, idx) in compareList" :key="idx">
                      <el-option :label="item.label" :value="item.value" />
                    </template>
                  </el-select>
                </el-col>
                <el-col :span="7">
                  <el-input 
                    v-model="condition.value" 
                    placeholder="请输入值" 
                  />
                </el-col>
                <el-col :span="1">
                  <div 
                    class="condition-node-remove-icon" 
                    @click="removeCondition(index, conditionIndex)"
                    v-if="branch.conditions.length > 1" 
                  >
                    <i class="fa-solid fa-file-pdf" />
                  </div>
                </el-col>
              </el-row>
            </template>
          </div>
        </template>

        <!-- 渲染 ELSE 分支（从 branch_condition_list 回显高度） -->
        <div 
          ref="elsePanelRef" 
          class="settings-form condition-panel"
          :style="{ height: elseBranch.height + 'px' }"
        >
          <div class="settings-title">ELSE</div>
        </div>
      </div>

      <div class="condition-branch-add">
        <el-button type="primary" text bg @click="addBranch">
          + 添加分支
        </el-button>
      </div>
    </div>
  </FlowContainer>
</template>

<script setup>
import { ref, reactive, toRaw, nextTick, onMounted, watch } from 'vue'
import { cloneDeep, set, findLast } from 'lodash'
import { compareList } from '@/views/smart/assistant/workflow/common/data'

import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

// 定义组件接收的属性
const props = defineProps({
  properties: {
    type: Object,
    default: () => ({
      branch_condition_list: [] // 默认空数组，避免解构报错
    })
  },
  isSelected: {
    type: Boolean,
    default: false
  },
  nodeModel: {
    type: Object,
    required: true
  }
});

// 1. 响应式变量：存储用于渲染的分支（IF/ELSE IF）
const branches = ref([])
// 2. 响应式变量：存储 ELSE 分支配置（高度、ID 等）
const elseBranch = ref({
  height: 60, // 默认高度
  id: 'else_branch'
})
// 3. 引用：IF/ELSE IF 面板 + ELSE 面板
const conditionPanelsRef = ref([])
const elsePanelRef = ref(null)

// -------------------------- 核心：初始化回显数据（修复字段名） --------------------------
const initEchoData = () => {
  if (!props.properties.branch_condition_list || props.properties.branch_condition_list.length === 0) {
    // 无数据时，初始化默认 IF 分支（条件字段用 field，而非 variable）
    branches.value = [{
      id: `condition_0`,
      type: 'IF',
      height: 120,
      conditions: [{ 
        field: [], // 与 FlowCascader 绑定的 field 一致（FlowCascader 通常返回数组）
        condition: '', 
        value: '' 
      }]
    }]
    // 初始化默认 ELSE 分支
    elseBranch.value = { id: 'else_branch', height: 60 }
    return
  }

  // 从 props 中提取 IF/ELSE IF 分支（排除 ELSE 分支）
  const ifElseIfBranches = props.properties.branch_condition_list.filter(item => {
    // 识别 ELSE 分支：无 type 或 type 不是 IF/ELSE IF 的视为 ELSE
    const isElse = !item.type || !['IF', 'ELSE IF 1', 'ELSE IF 2', 'ELSE IF 3', 'ELSE IF 4'].includes(item.type)
    if (isElse) {
      // 同步 ELSE 分支配置（回显高度、ID）
      elseBranch.value = {
        id: item.id || 'else_branch',
        height: item.height || 60
      }
    }
    return !isElse // 保留 IF/ELSE IF 分支
  })

  // 将 IF/ELSE IF 分支映射为渲染所需结构（修复：用 field 代替 variable）
  branches.value = ifElseIfBranches.map(branch => ({
    id: branch.id,
    type: branch.type || 'IF', // 回显分支类型（IF/ELSE IF N）
    height: branch.height || 120, // 回显分支高度
    // 回显条件：直接使用 branch.conditions 的 field（与模板绑定一致）
    conditions: branch.conditions?.map(cond => ({
      field: cond.field || [], // 直接继承 props 的 field 数组（适配 FlowCascader）
      condition: cond.compare || '', // 条件运算符（如 >、===）
      value: cond.value || '' // 条件值
    })) || [{ field: [], condition: '', value: '' }] // 至少一个默认条件（用 field）
  }))

  // 若未提取到 IF/ELSE IF 分支，补充默认 IF 分支
  if (branches.value.length === 0) {
    branches.value = [{
      id: `condition_0`,
      type: 'IF',
      height: 120,
      conditions: [{ field: [], condition: '', value: '' }]
    }]
  }
}

// -------------------------- 生命周期：初始化回显 --------------------------
// 组件挂载前初始化回显数据
initEchoData()

// 组件挂载后，同步面板高度（确保 DOM 渲染完成）
onMounted(() => {
  nextTick(() => {
    // 同步 IF/ELSE IF 面板高度到 branches
    if (conditionPanelsRef.value.length > 0) {
      branches.value = branches.value.map((branch, index) => ({
        ...branch,
        height: conditionPanelsRef.value[index]?.offsetHeight || branch.height
      }))
    }
    // 同步 ELSE 面板高度到 elseBranch
    if (elsePanelRef.value) {
      elseBranch.value.height = elsePanelRef.value.offsetHeight
    }
    // 更新 branch_condition_list 并刷新节点
    updateBranchConditionList()
  })
})

// -------------------------- 分支/条件操作方法（修复：新增条件用 field） --------------------------
// 添加条件（当前分支）
const addCondition = (branchIndex) => {
  branches.value[branchIndex].conditions.push({
    field: [], // 新增条件用 field（与模板绑定一致）
    condition: '',
    value: ''
  })
  // 延迟更新高度（等待 DOM 渲染）
  nextTick(() => updateBranchConditionList())
}

// 添加 ELSE IF 分支（修复：新增分支条件用 field）
const addBranch = () => {
  const newBranchIndex = branches.value.length
  // 新增 ELSE IF 分支（条件字段用 field）
  branches.value.push({
    id: `condition_${Date.now()}`,
    type: `ELSE IF ${newBranchIndex}`,
    height: 120, // 默认高度
    conditions: [{ field: [], condition: '', value: '' }]
  })

  nextTick(() => {
    // 同步新分支的实际高度（从 DOM 读取）
    const newBranch = branches.value[newBranchIndex]
    newBranch.height = conditionPanelsRef.value[newBranchIndex]?.offsetHeight || 120
    // 更新 branch_condition_list 并刷新节点
    updateBranchConditionList()
    // 重新计算节点总高度
    updateNodeTotalHeight()
  })
}

// 删除 ELSE IF 分支（至少保留 1 个 IF 分支）
const removeBranch = (branchIndex) => {
  if (branches.value.length <= 1) return // 禁止删除最后一个 IF 分支
  branches.value.splice(branchIndex, 1)
  // 重新编号 ELSE IF 分支（如删除 ELSE IF 2 后，ELSE IF 3 → ELSE IF 2）
  branches.value.forEach((branch, idx) => {
    if (idx > 0) branch.type = `ELSE IF ${idx}`
  })
  // 更新 branch_condition_list 并刷新节点
  nextTick(() => {
    updateBranchConditionList()
    updateNodeTotalHeight()
  })
}

// 删除条件（当前分支至少保留 1 个条件）
const removeCondition = (branchIndex, conditionIndex) => {
  const currentBranch = branches.value[branchIndex]
  if (currentBranch.conditions.length <= 1) return
  currentBranch.conditions.splice(conditionIndex, 1)
  // 同步高度
  nextTick(() => updateBranchConditionList())
}

// -------------------------- 辅助方法：同步数据与高度（修复：用 field 同步 props） --------------------------
// 更新 props.properties.branch_condition_list（同步 branches + elseBranch）
const updateBranchConditionList = () => {
  if (!props.nodeModel) return

  // 1. 组装 IF/ELSE IF 分支数据（修复：用 condition.field 同步到 props）
  const ifElseIfList = branches.value.map(branch => ({
    id: branch.id,
    type: branch.type,
    height: branch.height,
    condition: 'and', // 固定为 and（原数据结构）
    // 同步条件到 props 格式：直接使用 condition.field（无需转换，与 FlowCascader 输出一致）
    conditions: branch.conditions.map(cond => ({
      field: cond.field || [], // 核心修复：用 cond.field 代替 cond.variable
      compare: cond.condition,
      value: cond.value
    }))
  }))

  // 2. 组装 ELSE 分支数据
  const elseList = [{
    id: elseBranch.value.id,
    height: elseBranch.value.height,
    index: ifElseIfList.length // ELSE 分支索引 = IF/ELSE IF 分支数量
  }]

  // 3. 合并为完整的 branch_condition_list
  const newBranchList = [...ifElseIfList, ...elseList]
  // 更新 props（用 set 确保响应式）
  set(props.nodeModel.properties, 'branch_condition_list', newBranchList)
  // 刷新节点锚点
  props.nodeModel.refreshBranch()
}

// 计算并更新节点总高度（所有分支高度 + 间距）
const updateNodeTotalHeight = () => {
  if (!props.nodeModel) return

  // 总高度 = IF/ELSE IF 分支高度总和 + ELSE 分支高度 + 基础间距（120px）
  const ifElseIfTotalHeight = branches.value.reduce((sum, branch) => sum + branch.height + 8, 0)
  const totalHeight = ifElseIfTotalHeight + elseBranch.value.height + 120
  // 更新节点高度
  if (props.nodeModel.setHeight) {
    props.nodeModel.setHeight(totalHeight)
  }
  // 同步 props 中的 height
  set(props.nodeModel.properties, 'height', totalHeight)
}

// -------------------------- 监听：branches 变化时同步数据 --------------------------
watch(
  () => branches.value,
  (newVal) => {
    nextTick(() => {
      updateBranchConditionList()
      updateNodeTotalHeight()
    })
  },
  { deep: true } // 深度监听条件变化（如修改 field、condition、value）
)
</script>

<style lang="scss" scoped>
.branch-remove-icon {
  margin-left: 10px;
  cursor: pointer;
  color: #f56c6c; // 优化删除按钮颜色，提升辨识度
}
.condition-panel {
  margin-bottom: 8px; // 分支间距，与高度计算对应
  padding: 8px;
  border: 0px solid #e6e6e6;
  border-radius: 8px !important;
}
.settings-title {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}
.condition-node-add {
  margin-top: 8px;
  visibility: visible !important; // 修复原代码中按钮隐藏的问题
}
.condition-branch-add {
  margin-top: 12px;
}
.condition-node-remove-icon {
  cursor: pointer;
  color: #f56c6c;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>