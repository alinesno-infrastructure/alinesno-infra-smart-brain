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
          height: 380,
          width: 280
        },
        x: 240,
        y: 340,
      }]
    }

    data = {
    "nodes": [
        {
            "id": "22690168-c83c-4c93-85f8-09a681b358bb",
            "type": "start",
            "x": 240,
            "y": 340,
            "properties": {
                "icon": "fa-solid fa-paper-plane",
                "color": "#2962FF",
                "stepName": "开始",
                "showNode": true,
                "height": 380,
                "width": 280,
                "config": {
                    "fields": [
                        {
                            "label": "用户消息",
                            "value": "message"
                        },
                        {
                            "label": "文档",
                            "value": "document"
                        }
                    ],
                    "globalFields": [
                        {
                            "value": "time",
                            "label": "当前时间"
                        },
                        {
                            "value": "pre_content",
                            "label": "上节点内容"
                        },
                        {
                            "value": "channelId",
                            "label": "频道号"
                        },
                        {
                            "value": "history_content",
                            "label": "历史对话"
                        }
                    ]
                }
            }
        },
        {
            "id": "9a8cd2d3-bb16-48cc-ae7c-d6f66b4376d5",
            "type": "function",
            "x": 890,
            "y": 350,
            "properties": {
                "icon": "fas fa-file-signature",
                "color": "#424242",
                "stepName": "脚本功能",
                "showNode": true,
                "height": 540,
                "width": 280,
                "config": {
                    "fields": [
                        {
                            "label": "结果",
                            "value": "result"
                        }
                    ]
                },
                "node_data": {
                    "params": [

                    ],
                    "isPrint": false
                }
            }
        },
        {
            "id": "57ef29d4-4409-4d6c-93f1-1f63a417b07a",
            "type": "reply",
            "x": 1350,
            "y": 340,
            "properties": {
                "icon": "fa-solid fa-reply",
                "color": "#FF9800",
                "stepName": "指定回复",
                "showNode": true,
                "height": 400,
                "width": 280,
                "config": {
                    "fields": [
                        {
                            "label": "内容",
                            "value": "answer"
                        }
                    ]
                },
                "node_data": {
                    "replayType": "text",
                    "replayParams": [

                    ],
                    "replayContet": "",
                    "isPrint": false
                }
            }
        }
    ],
    "edges": [
        {
            "id": "fc481501-79ff-48d4-ad2b-305f76db1e5a",
            "type": "app-edge",
            "sourceNodeId": "22690168-c83c-4c93-85f8-09a681b358bb",
            "targetNodeId": "9a8cd2d3-bb16-48cc-ae7c-d6f66b4376d5",
            "startPoint": {
                "x": 375,
                "y": 340
            },
            "endPoint": {
                "x": 745,
                "y": 350
            },
            "properties": {

            },
            "pointsList": [
                {
                    "x": 375,
                    "y": 340
                },
                {
                    "x": 480,
                    "y": 340
                },
                {
                    "x": 650,
                    "y": 350
                },
                {
                    "x": 745,
                    "y": 350
                }
            ],
            "sourceAnchorId": "22690168-c83c-4c93-85f8-09a681b358bb_right",
            "targetAnchorId": "9a8cd2d3-bb16-48cc-ae7c-d6f66b4376d5_left"
        },
        {
            "id": "45d83549-cb36-456e-8f3b-fe450d8afadf",
            "type": "app-edge",
            "sourceNodeId": "9a8cd2d3-bb16-48cc-ae7c-d6f66b4376d5",
            "targetNodeId": "57ef29d4-4409-4d6c-93f1-1f63a417b07a",
            "startPoint": {
                "x": 1025,
                "y": 350
            },
            "endPoint": {
                "x": 1205,
                "y": 340
            },
            "properties": {

            },
            "pointsList": [
                {
                    "x": 1025,
                    "y": 350
                },
                {
                    "x": 1130,
                    "y": 350
                },
                {
                    "x": 1110,
                    "y": 340
                },
                {
                    "x": 1205,
                    "y": 340
                }
            ],
            "sourceAnchorId": "9a8cd2d3-bb16-48cc-ae7c-d6f66b4376d5_right",
            "targetAnchorId": "57ef29d4-4409-4d6c-93f1-1f63a417b07a_left"
        }
    ]
}

    console.log('lf.value:', lf.value)
    console.log('data = ' + data)

    lf.value.render(data ? data : {});

    lf.value.graphModel.eventCenter.on('delete_edge', function (id_list) {
      id_list.forEach(function (id) {
        lf.value.deleteEdge(id);
      });
    });

    // setTimeout(() => {
      // lf.value?.fitView({
        // animation: true , 
        // padding: 50
      // })
      // lf.value.extension.dagre.layout();
    // }, 500)

  }
};

// TODO 待获取画布中心位置
const clickNode = (shapeItem) => {

  // const virtualRectSize = lf.value.graphModel.getVirtualRectSize() ; 
  // console.log('lf.value.graphModel.getVirtualRectSize() = ' + virtualRectSize) ; 
  // console.log('lf.value.graphModel.width = ' + lf.value.graphModel.width)
  // console.log('lf.value.graphModel.height = ' + lf.value.graphModel.height)

  // 清除所有选中的元素
  lf.value.clearSelectElements();

  // 获取虚拟矩形的中心位置
  const { virtualRectCenterPositionX, virtualRectCenterPositionY } = lf.value.graphModel.getVirtualRectSize();
  console.log('virtualRectCenterPositionX = ' + virtualRectCenterPositionX + ', virtualRectCenterPositionY = ' + virtualRectCenterPositionY);

  // 添加一个新的节点
  const newNode = lf.value.graphModel.addNode({
    type: shapeItem.type,
    properties: shapeItem.properties,
    x: lf.value.graphModel.width / 2 + virtualRectCenterPositionX/2 - 300 , 
    y: lf.value.graphModel.height / 2 + virtualRectCenterPositionY/2 - 300,
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