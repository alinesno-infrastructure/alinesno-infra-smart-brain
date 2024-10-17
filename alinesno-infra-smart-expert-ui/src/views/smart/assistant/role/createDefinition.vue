<template>
  <div class="app-container">
    <el-page-header @back="goBack" content="配置任务编排"></el-page-header>

    <el-row>
      <el-col :span="14" v-loading="loading" element-loading-text="任务正在生成中，请勿刷新 ..." :element-loading-spinner="svg">
        <div class="form-container">
          <div class="designer-wrap">

            <div style="position: absolute;top: -30px;z-index: 1000;right: 20px;">
              <el-button type="primary" size="large" bg text @click="submitProcessDefinition('commit')">
                <i class="fa-solid fa-paper-plane"></i>&nbsp;提交流程
              </el-button>
              <el-button type="danger" size="large" bg text @click="submitProcessDefinition('validate')">
                <i class="fa-solid fa-meteor"></i>&nbsp;验证流程
              </el-button>
            </div>

            <div class="designer-content-box" :style="{ height: readable ? '100vh' : 'calc(100vh - 50px)' }">
              <div class="flow-design-wrap">
                <div id="flow-design" class="flow-design-container" :style="zoomStyle">
                  <div id="flow-design-content" class="flow-design-content">
                    <FlowStartNode :node="nodeData" />

                    <FlowNode :node="item" :readable="readable" v-for="(item, index) in nodeDataArr" :key="index" />

                    <FlowEndNode :node="nodeData" :readable="readable" />
                  </div>
                </div>
                <FlowHelper />
                <FlowTips />
                <!-- <FlowZoom /> -->
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="output-result-box" style="padding:20px;">
          <el-card shadow="never" style="height:calc(100vh - 200px);padding:0px !important">
            <template #header>
              <div class="card-header">
                <span>执行结果</span>
              </div>
            </template>

            <el-skeleton v-if="!genContent" :rows="10" />
            <el-scrollbar v-else style="height: calc(-320px + 100vh); padding: 20px;">
              <div v-html="markdown.render(genContent)"></div>
            </el-scrollbar>

          </el-card>
        </div>
      </el-col>
    </el-row>


  </div>
</template>

<script setup name="createProcessDefinition">

const router = useRouter();
const route = useRoute();
const { proxy } = getCurrentInstance();

import { ElLoading } from 'element-plus'
import flowNodeStore from '@/store/modules/flowNode'

import { getTaskDefinition, commitProcessDefinition, updateProcessDefinition } from '@/api/smart/assistant/taskDefinition'

import FlowHelper from './common/FlowHelper';
import FlowTips from './common/FlowTips';
import FlowNode from './common/FlowNode/index';
import FlowStartNode from './common/FlowNode/Start';
import FlowEndNode from './common/FlowNode/End';
import MarkdownIt from 'markdown-it';

const markdown = new MarkdownIt()
const currentRoleId = ref(null)
const roleId = route.query.roleId
const nodeDataArr = ref(flowNodeStore().nodes);

/** 返回 */
function goBack() {
  router.push({ path: '/expert/smart/assistant/role/index' });
}

/**
 * 提交流程流程定义
 */
function submitProcessDefinition(type) {
  let nodes = flowNodeStore().getAllNodes();
  currentRoleId.value = router.currentRoute.value.query.roleId;

  console.log('roleId = ' + currentRoleId.value)
  console.log('submitProcessDefinition:' + JSON.stringify(nodes))

  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  let data = {
    taskFlow: nodes,
    roleId: currentRoleId.value,
  }

  commitProcessDefinition(data, type).then(response => {
    console.log(response);
    proxy.$modal.msgSuccess("流程提交成功");
    loading.close();
  }).catch(e => {
    loading.close();
  })
}

/** 初始化数据 */
onMounted(() => {
  console.log('onMounted');

  if (roleId) {

    getTaskDefinition(roleId).then(res => {
      // 使用 forEach 循环遍历 data 数组
      res.data.forEach(item => {
        if (item.type != 0) {
          flowNodeStore().setNode(item);
        }
      });

    })
  }
})

</script>
