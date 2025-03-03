<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">

      <!-- 节点设置部分 -->
      <div class="node-output">
        <div class="output-title">全局变量</div>
        <template v-for="(item , index) in props.properties.config.globalFields" :key="item.value">
          <div class="output-content copy-coutput-var" 
             @mouseenter="showCopyBtn = index"
             @mouseleave="showCopyBtn = null"
          >
            {{ item.label }} {{ '{' + item.value + '}' }}

            <el-button v-if="showCopyBtn === index"  link @click="copyClick(`{{global.${item.value}}}`)" style="padding: 0">
              <i class="fa-solid fa-clone copy-btn"></i>
            </el-button>

          </div>
        </template>
      </div>

  </FlowContainer>
</template>

<script setup>
import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import { copyClick } from '@/utils/clipboard'

const showCopyBtn = ref(-1);
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

</script>

<style lang="scss" scoped></style>