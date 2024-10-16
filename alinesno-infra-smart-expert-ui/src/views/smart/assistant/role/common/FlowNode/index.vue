<template>
  <div class="flow-row-container">

    <!-- 节点 -->
    <FlowGroovyNode v-if="node && node.type == 7" :node="node" :readable="readable" />
    <FlowNotice v-if="node && node.type == 9" :node="node" :readable="readable" />
    
    <!-- 子节点-->
    <FlowNode v-if="node && node.childNode && node.childNode.hasOwnProperty('name')" :node="node.childNode" :readable="readable" />
    
  </div>
</template>

<script setup>

import FlowGroovyNode from './Groovy/index' // 引入Groovy组件(7)
import FlowNotice from './Notice/index'

const props = defineProps({
  node: {
    type: Object,
    default: () => ({}),  // 对象类型的默认值应通过函数返回
  },
  readable: {
    type: Boolean,
    default: false,  // 基本类型可以直接指定默认值
  },
});

</script>
