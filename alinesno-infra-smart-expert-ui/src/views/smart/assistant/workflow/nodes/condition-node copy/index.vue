<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">

    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div>
        <!-- 节点设置表单区域 -->
        <template v-for="(branch, index) in branches" :key="index">
          <div ref="conditionPanelsRef" class="settings-form condition-panel">
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
                  <!-- <el-select v-model="condition.variable" placeholder="请选择变量" style="margin-right:8px;">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select> -->
                  <FlowCascader :nodeModel="props.nodeModel" />
                </el-col>
                <el-col :span="6" style="padding-left:5px;padding-right:5px;">
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
            <div class="condition-node-add" style="visibility: hidden;">
              <el-button type="primary" text bg @click="addCondition(index)">
                + 添加条件
              </el-button>
            </div>
          </div>
        </template>

        <div ref="conditionPanelsRef" class="settings-form condition-panel">
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
import { ref, reactive, toRaw, nextTick } from 'vue'
import { cloneDeep, set } from 'lodash'

import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

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
    },
    {
      id: `else_branch`,
      height: 60 // ELSE分支的高度
    }
  ];
}

// 引用所有的 condition-panel 元素
const conditionPanelsRef = ref([]);

// 添加条件
const addCondition = (branchIndex) => {
  branches.value[branchIndex].conditions.push({
    variable: '',
    condition: '',
    value: ''
  })
}

function addBranch() {
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
  });

  nextTick(() => {
    const list = cloneDeep(props.nodeModel.properties.branch_condition_list || []);
    
    // 获取新分支的实际高度
    const newBranchIndex = branches.value.length - 2;
    const panelRef = conditionPanelsRef.value[newBranchIndex];
    const height = panelRef ? panelRef.offsetHeight : 120;

    const obj = {
      conditions: [
        {
          field: [],
          compare: '',
          value: ''
        }
      ],
      type: 'ELSE IF ' + (list.length - 1),
      height: height,
      id: 'condition_' + Date.now(),
      condition: 'and'
    };

    // 插入到ELSE分支之前
    const insertIndex = list.length > 1 ? list.length - 1 : 0;
    list.splice(insertIndex, 0, obj);

    try {
      // 更新节点属性
      set(props.nodeModel.properties, 'branch_condition_list', list);
      
      // 重新计算节点总高度
      const totalHeight = list.reduce((sum, item) => sum + (item.height || 100) + 8, 0) + 100; // 基础高度
      
      // 更新节点高度（如果AppNodeModel有setHeight方法）
      if (props.nodeModel.setHeight) {
        props.nodeModel.setHeight(totalHeight);
      }
      
      // 刷新锚点
      props.nodeModel.refreshBranch();
    } catch (error) {
      console.error('Error in addBranch:', error);
    }
  });
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
    branches.value.splice(branchIndex, 1);
    
    // 更新 branch_condition_list
    const updatedList = [...props.properties.branch_condition_list];
    updatedList.splice(branchIndex, 1);
    props.properties.branch_condition_list = updatedList;
    
    // 刷新节点锚点
    if (props.nodeModel) {
      props.nodeModel.refreshBranch();
    }
  }
}

// 在组件挂载时初始化ELSE分支高度
onMounted(() => {
  nextTick(() => {
    if (conditionPanelsRef.value && props.nodeModel) {
      const elseIndex = conditionPanelsRef.value.length - 1;
      const elsePanel = conditionPanelsRef.value[elseIndex];
      
      if (elsePanel && props.properties.branch_condition_list) {
        const updatedList = [...props.properties.branch_condition_list];
        if (updatedList.length > 0) {
          // 更新ELSE分支高度
          updatedList[updatedList.length - 1] = {
            ...updatedList[updatedList.length - 1],
            height: elsePanel.offsetHeight || 60
          };
          props.properties.branch_condition_list = updatedList;
        }
      }
    }
  });
});

// 更新所有分支的高度
const updateBranchHeights = () => {
  if (conditionPanelsRef.value && props.nodeModel) {
    const updatedList = props.properties.branch_condition_list.map((item, index) => {
      if (conditionPanelsRef.value[index]) {
        return {
          ...item,
          height: conditionPanelsRef.value[index].offsetHeight
        };
      }
      return item;
    });
    
    props.properties.branch_condition_list = updatedList;
    props.nodeModel.refreshBranch();
  }
};

// 监听分支变化，动态调整高度
watch(() => branches.value.length, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    nextTick(() => {
      updateBranchHeights();
    });
  }
});

</script>

<style lang="scss" scoped>
.branch-remove-icon {
  margin-left: 10px;
  cursor: pointer;
}
</style>