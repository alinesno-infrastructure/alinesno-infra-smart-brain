<template>
  <div class="flow-container">

    <div class="handle-btn-panel">
      <el-button type="text" text bg size="small" class="handle-btn">
        <i class="fa-solid fa-forward-step"></i> &nbsp; 调试
      </el-button>
      <el-button type="text" text bg size="small" class="handle-btn">
        <i class="fa-solid fa-copy"></i> &nbsp; 复制
      </el-button>
      <el-button type="text" text bg size="small" class="handle-btn">
        <i class="fa-solid fa-eraser"></i>&nbsp;  删除
      </el-button>
    </div>

    <div class="workflow-node-container">

      <!-- 节点标题部分，包含图标和名称 -->
      <div class="node-title">
        <div class="node-title-label">
          <span class="node-icon">
            <i :class="props.properties.icon"></i>
          </span>
          <span class="node-name" v-if="showEditorName()">
            <FlowEditorStepPanel
              @mousemove.stop
              @mousedown.stop
              @keydown.stop
              @click.stop
              @change="editName"
              :data="props.properties.stepName"
              trigger="dblclick"
            />
          </span>
          <span class="node-name" v-else>
            {{ props.properties.stepName }}
          </span>
        </div>

        <div class="document-link">
          <el-tooltip class="box-item" effect="dark" content="查看教程">
            <i class="fa-solid fa-file-word"></i>
          </el-tooltip>
        </div>

      </div>

      <!-- 节点自定义内容_start -->
      <slot></slot>
      <!-- 节点自定义内容_end -->

      <!-- 输出参数部分 -->
      <div class="node-output">
        <div class="output-title">输出参数</div>
        <template v-for="(item, index) in stepFields" :key="index">
          <div class="output-content copy-coutput-var" @mouseenter="showCopyBtn = index"
            @mouseleave="showCopyBtn = null">
            {{ item.label }} {{ '{' + item.value + '}' }}

            <el-button v-if="showCopyBtn === index" link @click="copyClick(item.globeLabel)" style="padding: 0">
              <i class="fa-solid fa-clone copy-btn"></i>
            </el-button>
          </div>
        </template>
      </div>

    </div>
  </div>
</template>

<script setup>
import { set } from 'lodash'
import { computed } from 'vue';
import { ElMessage } from 'element-plus';
import { copyClick } from '@/utils/clipboard'

// import { initDefaultShortcut } from '@/views/smart/assistant/workflow/common/shortcut'; // 替换为实际的模块路径
// const { copy_node, paste_node, delete_node } = initDefaultShortcut(lf, graph);

import FlowEditorStepPanel from '@/views/smart/assistant/workflow/common/FlowEditorStepPanel.vue'

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

const showCopyBtn = ref(-1);

// 定义 editName 函数
const editName = (val) => {

  console.log('new stepName = ' + val);

  if (val.trim() && val.trim()!== props.nodeModel.properties.stepName) {
    if (!props.nodeModel.graphModel.nodes?.some((node) => node.properties.stepName === val.trim())) {
      set(props.nodeModel.properties, 'stepName', val.trim());
    } else {
      ElMessage.error('修改失败，节点名称已存在.')
    }
  }
};

const stepFields = computed(() => {
  if (props.nodeModel.properties.config.fields) {
    const fields = props.nodeModel.properties.config.fields.map((field) => {
      return {
        label: field.label,
        value: field.value,
        globeLabel: `{{${props.nodeModel.properties.stepName}.${field.value}}}`,
        globeValue: `{{context['${props.nodeModel.id}'].${field.value}}}`
      };
    });
    return fields;
  }
  return [];
});

const showEditorName = () => {
  return props.nodeModel.type !== 'start' ; 
};

</script>

<style lang="scss" scoped>

.flow-container{

  &:hover {
    .handle-btn-panel {
      display: flex;
    }
  }

.handle-btn-panel {
  position: absolute;
  right: -70px;
  padding-left:10px;
  top: 0px;
  background: transparent;
  display: none;
  flex-direction: column;
  gap: 7px;

  .handle-btn {
    background-color: #fff !important;
    margin-left: 0px;
    box-shadow: 0 2px 4px rgba(31, 35, 41, 0.1215686275);
  }
}
}

</style>