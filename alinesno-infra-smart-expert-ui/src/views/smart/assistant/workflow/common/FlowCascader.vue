<template>
    <div style="width:100%">
        <el-cascader
            style="width:100%"
            v-model="data"
            :options="options"
            :props="{ expandTrigger: 'hover'}"
            placeholder="请选择内容"
            @visible-change="handleVisibleChange"
            />
            <!-- @change="handleChange" -->
    </div>
</template>

<script setup>

const props = defineProps({
  properties: {
    type: Object,
    default: () => ({})
  },
  nodeModel: {
    type: Object,
    required: true
  },
  modelValue: { // 接收父组件v-model的值（formData.questionReference）
    type: Array,
    default: () => [] // 默认空数组，避免undefined
  }
});

const currentData = ref(null)

const emit = defineEmits(['update:modelValue'])
const data = computed({
  set: (value) => {
    console.log('data = ' + JSON.stringify(value))
    currentData.value = value
    emit('update:modelValue', value)
  },
  get: () => {
    // return props.modelValue
    return Array.isArray(props.modelValue) ? props.modelValue : []
  }
})

const upNodeListObj = ref(null); // 上游节点列表
const options = ref([])  // 节点列表

  // 获取节点 upstream 节点列表
const getUpNodeList = () =>{

  const visited = new Set(); // 用于记录已经访问过的节点，避免循环引用
  const result = []; // 存储所有上一级节点

  const traverseUpNodes = (nodeId) => {
      const upNodeList = props.nodeModel.graphModel.getNodeIncomingNode(nodeId);

      for (const upNode of upNodeList) {

        if (!visited.has(upNode.id)) {
            visited.add(upNode.id);
            result.push(upNode);
            traverseUpNodes(upNode.id); // 递归调用，继续向上查找
        }
      }
  };

  traverseUpNodes(props.nodeModel.id);

  return result;

}

// 将节点列表转换为 options 格式
const convertToOptions = (nodeList) => {
    const options = [];
    let firstElement = null;

    nodeList.forEach((item) => {
        const option = {
            value: item.id,
            label: item.properties.stepName || '未命名节点', // 增加默认值，避免空标签
            type: item.type,
            children: item.properties?.config?.fields || []
        };
        options.unshift(option);

        // console.log('type = '+item.type+' item.properties = ' + JSON.stringify(item.properties))
        console.log(item.properties.config.globalFields);

        if (item.properties?.config?.globalFields && item.type === 'start') {
            firstElement = {
                value: 'global',
                label: '全局变量', // 这里需要根据 t 函数替换，这里先写死
                type: 'global',
                children: item.properties?.config?.globalFields || []
            };
        }
    });

    if (firstElement) {
        options.unshift(firstElement);
    }

    return options;
};

const nodeIncomingNodeRefresh = () =>{
  upNodeListObj.value = getUpNodeList();
  options.value = convertToOptions(upNodeListObj.value);
}

// 6. 新增：下拉展开时刷新选项（确保每次打开都有最新上游节点数据）
const handleVisibleChange = (isVisible) => {
  if (isVisible) {
    nodeIncomingNodeRefresh()
  }
}

// 7. 监听modelValue变化：父组件值更新时，同步刷新选项（关键回显逻辑）
watch(
  () => props.modelValue,
  (newVal) => {
    // 若新值存在且非空，刷新选项列表（确保有数据可回显）
    if (Array.isArray(newVal) && newVal.length > 0) {
      nodeIncomingNodeRefresh()
    }
  },
  { immediate: true, deep: true } // 立即执行+深度监听（数组元素变化也触发）
)

onMounted(() => {

  nodeIncomingNodeRefresh()

  props.nodeModel.graphModel.eventCenter.on('element:click', () => {
    if(!currentData.value){
      nodeIncomingNodeRefresh();
    }
  })

})

defineExpose({ nodeIncomingNodeRefresh });

</script>

<style lang="scss" scoped>
</style>