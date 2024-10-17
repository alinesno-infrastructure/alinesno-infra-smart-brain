<template>
  <div class="after-node-btn" @click="visible = true">
    <a-popover placement="right" v-model="visible">
      <template #content>
        <a-menu mode="vertical" class="flow-ant-menu-vertical">
          <a-menu-item key="7" @click="addType(7)">
            <img :src="parallelIcon" class="anticon" />
            <span>任务节点</span>
          </a-menu-item>
          <a-menu-item key="9" @click="addType(9)">
            <img :src="branchIcon2" class="anticon" />
            <span>通知回调</span>
          </a-menu-item>
        </a-menu>
      </template>
      <img :src="plusIcon" />
    </a-popover>
  </div>
</template>

<script setup>

import {
  plusIcon,
  approverIcon2,
  branchIcon2,
  parallelIcon,
  ccIcon2,
  writeIcon2,
  noticeIcon2,
  webhookIcon2
} from '@/utils/flowMixin';

// import { v4 as uuidv4 } from 'uuid';

import flowNodeStore from '@/store/modules/flowNode'

const props = defineProps({
  node: {
    type: Object,
    default: () => ({
      addable: true,
    }),
  },
  nodeType: {
    type: Number,
    default: 1,
  },
  id: {
    type: String,
    default: '',
  },
  readable: {
    type: Boolean,
    default: false,
  },
});

const visible = ref(true)

/** 添加节点 */
function addType(type) {

  console.log('type = ' + type);

  let addNode = addApproverNode(type);
  const nodeType = props.nodeType;
  const currNode = props.node;
  const id = props.id;

  flowNodeStore().addNode({addNode, currNode, nodeType, id});
}

/**
 * 获取节点类型信息 
 * @param {*} type 
 */
function getTypeInfo(type) {
    let name = '';
    let icon = '';

    switch (type) {
      case 7:
        name = 'Groovy操作';
        icon = parallelIcon;
        break;
      case 9:
        name = '通知回调';
        icon = branchIcon2;
        break;
      default:
        console.error(`未知类型: ${type}`);
        return null;
    }

    return { name:name, icon:icon };
}

/**
 * 添加审批节点 ||
 */
function addApproverNode(type) {
  return {
    id: '1' , // uuidv4(),
    name: getTypeInfo(type).name , // type == 1 ? '审批人' : '办理人',
    type: type,
    // 流程节点状态(用于只读模式, 0:未进行 1:进行中  2:已完成)
    status: -1,
    // 流程基础配置属性
    attr: {
      // 审批类型
      approvalMethod: 1,
      // 审批方式
      approvalMode: 1,
      // 审批人与发起人为同一人时
      sameMode: 2,
      // 审批人为空时
      noHander: 4,
    },
  };
}
</script>
