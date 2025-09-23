<template>
  <div class="flow-container">

    <div class="handle-btn-panel">
      <el-button 
        type="text" 
        :disabled="true"
        text 
        bg 
        size="small" 
        class="handle-btn">
        <i class="fa-solid fa-forward-step"></i> &nbsp; 调试
      </el-button>
      <el-button 
        :disabled="!lf"
        :loading="isCopyLoading"
        type="text" 
        text 
        bg 
        size="small" 
        @click="handleCopyNode"
        class="handle-btn">
        <i class="fa-solid fa-copy"></i> &nbsp; 复制
      </el-button>
      <el-button 
        v-if="props.nodeModel.type !== 'start'"
        :disabled="!lf"
        :loading="isDeleteLoading"
        type="text" 
        text 
        bg 
        size="small" 
        @click="handleDeleteNode"
        class="handle-btn">
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

import { useWorkflowStore } from '@/store/modules/workflowStore'; // 导入 Pinia Store
import { initDefaultShortcut } from '@/views/smart/assistant/workflow/common/shortcut'; // 替换为实际的模块路径
// const { copy_node, paste_node, delete_node } = initDefaultShortcut(lf.value, lf.value.graphModel);

import FlowEditorStepPanel from '@/views/smart/assistant/workflow/common/FlowEditorStepPanel.vue'

// 初始化 Pinia Store
const workflowStore = useWorkflowStore();

// 从 Pinia 中获取 lf 实例（响应式）
const lf = ref(workflowStore.lf);

// 3. 新增：标记快捷键是否已初始化（防止重复调用）
const isShortcutInited = ref(false);

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

// 加载状态
const isDebuggerLoading = ref(true);
const isCopyLoading = ref(false);
const isDeleteLoading = ref(false);

// 确保当前节点被选中
const ensureNodeSelected = () => {
  if (!props.lf) return;
  props.lf.clearSelectElements(); // 清除其他选中
  props.lf.selectElementById(props.nodeModel.id, true); // 选中当前节点
};

// 监听选中状态，初始化时选中
watch(
  () => props.isSelected,
  (newVal) => {
    if (newVal && lf.value) { // 改为 lf.value
      ensureNodeSelected();
    }
  },
  { immediate: true }
);

// 监听 Pinia 中 lf 的变化（确保实例初始化后同步）
watch(
  () => workflowStore.lf,
  (newLF) => {
    if (newLF) {
      lf.value = newLF; // 当 Pinia 中的 lf 就绪后，同步到组件内
    }
  },
  { immediate: true }
);

// 8. 核心：监听 Pinia 中 lf 的变化，初始化快捷键
watch(
  () => workflowStore.lf,
  (newLF) => {
    // 条件：newLF 存在（lf 实例就绪）+ 未初始化过快捷键 + newLF 有 graphModel（流程图模型就绪）
    if (newLF && !isShortcutInited.value && newLF.graphModel) {
      // 更新组件内 lf 实例
      lf.value = newLF;

      // 调用 initDefaultShortcut 初始化快捷键，传递 lf 实例和 graphModel
      initDefaultShortcut(newLF, newLF.graphModel);

      // 标记为已初始化，防止重复调用
      isShortcutInited.value = true;

      console.log('快捷键 initDefaultShortcut 初始化成功');
    }
  },
  { immediate: true } // 初始时立即执行一次（处理 lf 已就绪的场景）
);

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

// 10. 新增：复制/删除按钮点击事件（之前遗漏，补充完整功能）
const handleCopyNode = async () => {
  if (isCopyLoading.value || !lf.value) return;
  isCopyLoading.value = true;

  try {
    ensureNodeSelected();
    // 从 initDefaultShortcut 中解构 copy_node（需确保 shortcut.js 导出该函数）
    const { copy_node, paste_node } = initDefaultShortcut(lf.value, lf.value.graphModel);

    // 执行复制操作
    const copyResult = copy_node(); 
    
    // 只有复制成功时才执行粘贴
    if (copyResult === false) {
      ElMessage.success('正在粘贴 ...');
      
      // 延迟500ms执行粘贴，确保复制的数据处理完成
      setTimeout(() => {
        paste_node() ; 
      }, 500);
    }
  } catch (err) {
    ElMessage.error('复制失败：' + err.message);
  } finally {
    isCopyLoading.value = false;
  }
};

const handleDeleteNode = async () => {
  if (isDeleteLoading.value || !lf.value) return;
  isDeleteLoading.value = true;

  try {
    ensureNodeSelected();
    const { delete_node } = initDefaultShortcut(lf.value, lf.value.graphModel);
    delete_node(); // 内部含确认弹窗和校验
  } catch (err) {
    ElMessage.error('删除失败：' + err.message);
  } finally {
    isDeleteLoading.value = false;
  }
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