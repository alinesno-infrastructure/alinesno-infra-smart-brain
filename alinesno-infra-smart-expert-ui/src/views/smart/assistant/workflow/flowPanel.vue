<template>
  <div className="workflow-app" id="flow-container"></div>
  <Control class="workflow-control" v-if="lf" :lf="lf"></Control>
</template>

<script setup>

import { ref, onMounted, computed } from 'vue'

import LogicFlow from '@logicflow/core'
import '@logicflow/extension/lib/style/index.css'
import '@logicflow/core/dist/style/index.css'

import Dagre from './plugins/dagre.js'
import AppEdge from './common/edge'

import Control from './common/FlowControl.vue'
const nodes = import.meta.glob('./nodes/**/index.js', { eager: true })

import { initDefaultShortcut } from './common/shortcut';

const lf = ref();
const flowId = ref('');

const renderGraphData = (data) => {
  const container = document.querySelector('#flow-container');
  if (container) {
    console.log('container = ' + container)

    lf.value = new LogicFlow({
      plugins: [Dagre],
      textEdit: false,
      adjustEdge: false,
      adjustEdgeStartAndEnd: false,
      background: {
        backgroundColor: '#f5f6f7'
      },
      grid: {
        size: 10,
        type: 'dot',
        config: {
          color: '#DEE0E3',
          thickness: 1
        }
      },
      keyboard: {
        enabled: true
      },
      isSilentMode: false,
      container: container
    })

    // 设置主题
    lf.value.setTheme({
      bezier: {
        stroke: '#afafaf',
        strokeWidth: 1
      }
    })

    // 获取流程ID
    lf.value.on('graph:rendered', () => {
      flowId.value = lf.value.graphModel.flowId;
    });

    initDefaultShortcut(lf.value, lf.value.graphModel);
    lf.value.batchRegister([...Object.keys(nodes).map((key) => nodes[key].default), AppEdge])
    lf.value.setDefaultEdgeType('app-edge')

    data = {
      nodes:[{
        type: 'start',
        properties: {
          icon: 'fa-solid fa-paper-plane',
          color: "#2962FF",
          stepName: '开始',
          showNode: true,
          x: 140,
          y: 140,
          height: 380,
          width: 280
        },
        x: 440,
        y: 340,
      }]
    }

    console.log('lf.value:', lf.value)
    console.log('data = ' + data)

    lf.value.render(data ? data : {});

    lf.value.graphModel.eventCenter.on('delete_edge', function (id_list) {
      id_list.forEach(function (id) {
        lf.value.deleteEdge(id);
      });
    });

    // // 保存当前的缩放比例和位置
    // const currentZoom = lf.value.getZoom()
    // const currentTranslate = lf.value.getTranslate()

    // console.log('currentZoom = ' + currentZoom + ', currentTranslate = ' + currentTranslate)

    // TODO  重新渲染之后缩放比例不正确的问题
    // setTimeout(function () {
    //   if (lf.value) {
    //     lf.value.fitView();
    //   }
    // }, 500);

  }
};

const clickNode = (shapeItem) => {
  // 清除所有选中的元素
  lf.value.clearSelectElements();

  // 获取虚拟矩形的中心位置
  const { virtualRectCenterPositionX, virtualRectCenterPositionY } = lf.value.graphModel.getVirtualRectSize();
  console.log('virtualRectCenterPositionX = ' + virtualRectCenterPositionX + ', virtualRectCenterPositionY = ' + virtualRectCenterPositionY);

  // 添加一个新的节点
  const newNode = lf.value.graphModel.addNode({
    type: shapeItem.type,
    properties: shapeItem.properties,
    x: virtualRectCenterPositionX,
    y: virtualRectCenterPositionY - lf.value.graphModel.height / 2
  });

  // 设置新节点为选中和悬停状态
  newNode.isSelected = true;
  newNode.isHovered = true;

  newNode.helloworld = 'hello world';

  // 将新节点置于最上层
  lf.value.toFront(newNode.id);
};

const getWorkflowGraphData = () => {
  return lf.value.getGraphData();
};

onMounted(() => {
  nextTick(() => {
    renderGraphData();
  })
});

defineExpose({
  onmousedown,
  clickNode,
  getWorkflowGraphData
})

</script>

<style lang="scss">
.workflow-app {
  width: 100%;
  height: calc(100vh - 100px);
  position: relative;
  background-color: #fafafa;
  background-image: radial-gradient(#d6d9db 10%, transparent 0);
  background-size: 10px 10px;
}

.workflow-control {
  position: absolute;
  bottom: 24px;
  left: 24px;
  z-index: 2;
}

.lf-drag-able {
  cursor: pointer;
}
</style>