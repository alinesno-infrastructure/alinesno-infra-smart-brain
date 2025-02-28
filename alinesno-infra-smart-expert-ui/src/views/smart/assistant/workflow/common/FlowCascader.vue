<template>
    <div style="width:100%">
        <el-cascader
            style="width:100%"
            v-model="data"
            :options="options"
            :props="{ expandTrigger: 'hover'}"
            placeholder="请选择内容"
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
    type: Object
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
    return props.modelValue
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
            label: item.properties.stepName,
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

// const handleMouseOver = () => {
//   console.log('handleMouseOver , currentData = ' + currentData.value);
//   if(!currentData.value){
//     nodeIncomingNodeRefresh();
//   }
// };

onMounted(() => {

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