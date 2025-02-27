<template>
    <div style="width:100%" @mouseover="handleMouseOver">
        <el-cascader
            style="width:100%"
            v-model="value"
            :options="options"
            :props="{ expandTrigger: 'hover'}"
            @change="handleChange"
            placeholder="请选择内容"
            />
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

const upNodeListObj = ref(null); // 上游节点列表
const options = ref([])  // 节点列表

  // 获取节点 upstream 节点列表
const getUpNodeList = () =>{

  const visited = new Set(); // 用于记录已经访问过的节点，避免循环引用
  const result = []; // 存储所有上一级节点

  const traverseUpNodes = (nodeId) => {
      const upNodeList = props.nodeModel.graphModel.getNodeIncomingNode(nodeId);

      for (const upNode of upNodeList) {
        console.log('upNode.properties = ' + JSON.stringify(upNode.properties))
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

        if (item.properties?.globalFields && item.type === 'start') {
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

const handleMouseOver = () =>{
    upNodeListObj.value = getUpNodeList();
    console.log('function mouseOver = ' + props.nodeModel.id + ' , upNodeList = ' + upNodeListObj.value)
    options.value = convertToOptions(upNodeListObj.value);
    console.log('options = ' + JSON.stringify(options.value))
}


//   {
//     value: 'guide',
//     label: 'Guide',
//     children: [
//       {
//         value: 'disciplines',
//         label: 'Disciplines',
//         children: [
//           {
//             value: 'consistency',
//             label: 'Consistency',
//           },
//           {
//             value: 'controllability',
//             label: 'Controllability',
//           },
//         ],
//       },
//       {
//         value: 'navigation',
//         label: 'Navigation',
//         children: [
//           {
//             value: 'side nav',
//             label: 'Side Navigation',
//           },
//           {
//             value: 'top nav',
//             label: 'Top Navigation',
//           },
//         ],
//       },
//     ],
//   },
//   {
//     value: 'component',
//     label: 'Component',
//     children: [
//       {
//         value: 'basic',
//         label: 'Basic',
//         children: [
//           {
//             value: 'layout',
//             label: 'Layout',
//           },
//           {
//             value: 'button',
//             label: 'Button',
//           },
//         ],
//       },
//       {
//         value: 'form',
//         label: 'Form',
//         children: [
//           {
//             value: 'radio',
//             label: 'Radio',
//           },
//           {
//             value: 'form',
//             label: 'Form',
//           },
//         ],
//       },
//       {
//         value: 'data',
//         label: 'Data',
//         children: [
//           {
//             value: 'table',
//             label: 'Table',
//           },
//           {
//             value: 'badge',
//             label: 'Badge',
//           },
//         ],
//       },
//       {
//         value: 'notice',
//         label: 'Notice',
//         children: [
//           {
//             value: 'alert',
//             label: 'Alert',
//           },
//           {
//             value: 'notification',
//             label: 'Notification',
//           },
//         ],
//       },
//       {
//         value: 'navigation',
//         label: 'Navigation',
//         children: [
//           {
//             value: 'menu',
//             label: 'Menu',
//           },
//           {
//             value: 'steps',
//             label: 'Steps',
//           },
//         ],
//       },
//       {
//         value: 'others',
//         label: 'Others',
//         children: [
//           {
//             value: 'dialog',
//             label: 'Dialog',
//           },
//           {
//             value: 'collapse',
//             label: 'Collapse',
//           },
//         ],
//       },
//     ],
//   },
//   {
//     value: 'resource',
//     label: 'Resource',
//     children: [
//       {
//         value: 'axure',
//         label: 'Axure Components',
//       },
//       {
//         value: 'docs',
//         label: 'Design Documentation',
//       },
//     ],
//   },
// ]

</script>

<style lang="scss" scoped>
</style>