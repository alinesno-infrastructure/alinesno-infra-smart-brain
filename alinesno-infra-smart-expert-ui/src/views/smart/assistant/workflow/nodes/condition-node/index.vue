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
        <template v-for="(branch, index) in branches" :key="index">
          <div class="settings-form condition-panel">
            <div class="settings-title">
              {{ index === 0 ? 'IF' : `ELSE IF ${index}` }}
              <!-- 添加删除分支按钮 -->
              <span v-if="index > 0" class="branch-remove-icon" @click="removeBranch(index)">
                <i class="fa-solid fa-trash"></i>
              </span>
            </div>
            <template v-for="(condition, conditionIndex) in branch.conditions" :key="conditionIndex">
              <el-row style="margin-bottom:5px;">
                <el-col :span="10">
                  <el-select v-model="condition.variable" placeholder="请选择变量" style="margin-right:8px;">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-select v-model="condition.condition" placeholder="请选择条件" style="margin-right:8px;">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-col>
                <el-col :span="7">
                  <el-input v-model="condition.value" placeholder="请输入值" />
                </el-col>
                <el-col :span="1">
                  <div class="condition-node-remove-icon" @click="removeCondition(index, conditionIndex)">
                    <i class="fa-solid fa-file-pdf" />
                  </div>
                </el-col>
              </el-row>
            </template>
            <div class="condition-node-add">
              <el-button type="primary" text bg @click="addCondition(index)">
                + 添加条件
              </el-button>
            </div>
          </div>
        </template>

        <div class="settings-form condition-panel">
          <div class="settings-title">ELSE</div>
        </div>

      </div>
      <div class="condition-branch-add">
        <el-button type="primary" text bg @click="addBranch">
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
import { ref, reactive , toRaw } from 'vue'
import { cloneDeep, set } from 'lodash'

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

// 存储所有分支及其条件
const branches = ref([
  {
    conditions: [
      {
        variable: '',
        condition: '',
        value: ''
      }
    ]
  }
])

// 初始化 branch_condition_list
if (!props.properties.branch_condition_list) {
  props.properties.branch_condition_list = [
    {
      id: `condition_0`,
      height: 100 // 假设初始高度
    }
  ];
}

// 添加条件
const addCondition = (branchIndex) => {
  branches.value[branchIndex].conditions.push({
    variable: '',
    condition: '',
    value: ''
  })
}

// 添加分支
// const addBranch = () => {

//   const list = cloneDeep(props.properties.branch_condition_list)
//   console.log('list = ' + list);
//   console.log('nodeModel = ' + props.nodeModel)

//   branches.value.push({
//     conditions: [
//       {
//         variable: '',
//         condition: '',
//         value: ''
//       }
//     ]
//   })
//   // 更新 branch_condition_list
//   props.properties.branch_condition_list.push({
//     id: `condition_${props.properties.branch_condition_list.length}`,
//     index: 12 ,
//     height: 130 // 假设新增分支高度
//   });

//   // 刷新节点锚点
//   // set(props.nodeModel.properties.branch_condition_list, 'branch', list)
//   // props.nodeModel.refreshBranch();

//   set(props.nodeModel.properties, 'branch_condition_list', list)
//   props.nodeModel.refreshBranch()

// }

function addBranch() {
  // 获取原始的 nodeModel 对象，避免响应式代理问题
  // const rawNodeModel = toRaw(props.nodeModel);

  if (!props.nodeModel) {
    console.error('Node model is not available.');
    return;
  }

  branches.value.push({
    conditions: [
      {
        variable: '',
        condition: '',
        value: ''
      }
    ]
  })

  const list = cloneDeep(props.nodeModel.properties.branch_condition_list);
  const obj = {
    conditions: [
      {
        field: [],
        compare: '',
        value: ''
      }
    ],
    type: 'ELSE IF ' + (list.length - 1),
    height: 110,
    id: '12312312',
    condition: 'and'
  };

  list.splice(list.length - 1, 0, obj);

  try {
    refreshBranchAnchor(list, true, props.nodeModel);
    set(props.nodeModel.properties, 'branch_condition_list', list);
  } catch (error) {
    console.error('Error in addBranch:', error);
  }
}

function refreshBranchAnchor(list, is_add, nodeModel) {
  const branch_condition_list = cloneDeep(
    nodeModel.properties.branch_condition_list
      ? nodeModel.properties.branch_condition_list
      : []
  );

  const new_branch_condition_list = list
    .map((item, index) => {
      const find = branch_condition_list.find((b) => b.id === item.id);
      if (find) {
        return { index: index, height: find.height, id: item.id };
      } else {
        if (is_add) {
          return { index: index, height: 12, id: item.id };
        }
      }
    })
    .filter((item) => item);

  try {
    set(nodeModel.properties, 'branch_condition_list', new_branch_condition_list);
    nodeModel.refreshBranch();
  } catch (error) {
    console.error('Error in refreshBranchAnchor:', error);
  }
}

// 删除条件
const removeCondition = (branchIndex, conditionIndex) => {
  if (branches.value[branchIndex].conditions.length > 1) {
    branches.value[branchIndex].conditions.splice(conditionIndex, 1)
  }
}

// 删除分支
const removeBranch = (branchIndex) => {
  if (branches.value.length > 1) {
    branches.value.splice(branchIndex, 1)
    // 更新 branch_condition_list
    props.properties.branch_condition_list.splice(branchIndex, 1);
    // 刷新节点锚点
    if (props.nodeModel) {
      props.nodeModel.refreshBranch();
    }
  }
}
</script>

<style lang="scss" scoped>
.branch-remove-icon {
  margin-left: 10px;
  cursor: pointer;
}
</style>