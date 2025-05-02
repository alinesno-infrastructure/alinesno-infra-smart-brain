<template>
    <div class="app-container">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3" style="font-size: 16px;"> [测试数据集] Web站点同步</span>
        </template>
      </el-page-header>
      <div class="step-container">
        <el-steps :active="activeStep" simple finish-status="success" class="step-container-panel">
          <el-step title="站点配置"></el-step>
          <el-step title="内容同步"></el-step>
        </el-steps>
      </div>
      <el-row>
        <el-col :span="10">
          <div class="step-content" style="width: 100%;margin: auto;">
            <div style="
                margin: 20px 5px;
                text-align: center;
                display: flex;
                flex-direction: column;
                gap: 20px;
                line-height: 25px;">
              <div style="
                  font-size: 25px;
                  font-weight: bold;
              ">站点信息配置</div>
              <div style="
                  font-size: 14px;
                  text-align: left;
                  color: #666;
              ">Web站点同步功能允许你填写一个网站的根地址，系统会自动深度抓取相关的网页进行知识库训练。仅会抓取静态的网站，以项目文档、博客为主</div>
            </div>
            <el-form :model="formData" :rules="rules" 
                     label-position="top"
                     size="large" ref="formRef" label-width="120px">
              <el-form-item label="网站根地址" prop="rootUrl">
                <el-input v-model="formData.rootUrl" placeholder="请输入网站根地址"></el-input>
              </el-form-item>
              <el-form-item label="CSS内容选择器(可选)" prop="cssSelector">
                <el-input v-model="formData.cssSelector" placeholder="请输入CSS内容选择器 body div.content"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button style="width: 100%;" 
                  :loading="webImportLoading"
                  type="primary" 
                  class="next-button" @click="submitForm">确认站点同步</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        <el-col :span="14">
          <div class="crew-status-content" style="width: 100%;margin: auto;margin-top: 40px;padding-left: 40px;font-size: 13px;">
            <el-scrollbar class="log-scrollbar" ref="scrollbarRef">
              <div class="log-container" ref="innerRef">
                <div v-for="(log, index) in logs" :key="index" class="log-item">{{ log }}</div>
              </div>
            </el-scrollbar>
          </div>
        </el-col>
      </el-row>
    </div>
  </template>
  
<script setup name="Index">
import { getCurrentInstance, ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElScrollbar , ElLoading } from 'element-plus';

import { openSseConnect, handleCloseSse } from "@/api/smart/assistant/chatsse";

import { crawler } from '@/api/base/search/datasetKnowledge';
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();
const channelStreamId = ref(snowflake.generate());
const chatStreamLoading = ref(false);

const { proxy } = getCurrentInstance();
const router = useRouter();
const activeStep = ref(0);
const formRef = ref(null);

const scrollbarRef = ref(null);
const innerRef = ref(null); // 滚动条的处理_starter
let fullscreenLoading = null ; 

const webImportLoading = ref(false);

const formData = reactive({
  rootUrl: '',
  cssSelector: ''
});

const rules = {
  rootUrl: [
    { required: true, message: '请输入网站根地址', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        try {
          new URL(value);
          callback();
        } catch (error) {
          callback(new Error('请输入有效的URL地址'));
        }
      },
      trigger: 'blur'
    }
  ],
  // cssSelector: [
  //   { required: true, message: '请输入CSS内容选择器', trigger: 'blur' }
  // ]
};

// 日志数组
const logs = ref([]);

// 返回上一页
const goBack = () => {
  router.back();
};

// 提交表单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {

      const data = {
        datasetId: proxy.$route.query.datasetId,
        url: formData.rootUrl,
        channelStreamId: channelStreamId.value,
        cssSelector: formData.cssSelector
      };

      webImportLoading.value = true;

      fullscreenLoading = ElLoading.service({
        lock: true,
        text: '正在同步数据中 ...',
        background: 'rgba(0, 0, 0, 0.7)',
      })

      crawler(data).then(res => {
        console.log('res = ' + res);
        webImportLoading.value = false;
      }).catch(() => {
        webImportLoading.value = false;
      })

      // 模拟添加日志
      logs.value.push(`网站根地址: ${formData.rootUrl}, CSS内容选择器: ${formData.cssSelector}`);
      // 这里可以添加实际的表单提交逻辑，比如发送请求到服务器
    } else {
      console.log('表单校验不通过');
      logs.value.push('表单校验不通过');
    }
  });
};

function initChatBoxScroll() {

  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight);
  })

}

/** 连接sse */
function handleSseConnect(channelStreamId) {
  nextTick(() => {
    if (channelStreamId) {

      let sseSource = openSseConnect(channelStreamId);
      // 接收到数据
      sseSource.onmessage = function (event) {

        if (!event.data.includes('[DONE]')) {
          let resData = event.data;
          if (resData != 'ping') {  // 非心跳消息
            const data = resData;
            pushResponseMessageList(data);
          }
        } else if(event.data.includes('[DONE]')) {
          console.log('消息接收结束.')
          webImportLoading.value = false ;
          if(fullscreenLoading){
            fullscreenLoading.close() ;
          }
        }

      }
    }
  })
}

function pushResponseMessageList(data) {
  logs.value.push(data);
  if (logs.value.length > 20) {
    logs.value.shift(); // 移除最早的元素
  }
  initChatBoxScroll();
}

onMounted(() => {
  handleSseConnect(channelStreamId.value)
})

onBeforeUnmount(() => {
  handleCloseSse(channelStreamId.value).then(res => {
    console.log('关闭sse连接成功:' + channelId)
  })
});

</script>
  
<style scoped lang="scss">
.step-container {
  background: #F7F8FA;
  padding: 5px;
  margin-top: 20px;
  border-radius: 10px;

  .step-container-panel {
    max-width: 900px;
    margin: auto;
  }
}

.step-content {
  background-color: #fff;
  padding: 20px 0px;
  border-radius: 8px;
}

.upload-container {
  margin-bottom: 15px;
}

.file-list {
  list-style-type: none;
  padding: 0;
  margin-top: 20px;
  margin-bottom: 20px;

  li {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    font-size: 14px;
    color: #333;

    i {
      margin-right: 8px;
      color: #666;
    }
  }
}

.next-button,
.upload-button,
.prev-button {
  margin-top: 15px;
  // margin-right: 15px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
}

.radio-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;

  .el-radio {
    margin-bottom: 8px;
  }
}

.step-content-title {
  font-size: 14px;
  padding: 10px 0px;
}

.custom-inputs {
  display: flex;
  flex-direction: column;

  .el-input {
    margin-bottom: 15px;
  }
}

.progress-bar {
  margin-top: 15px;
}

// 新增日志部分暗黑风格样式
.log-scrollbar {
  height: calc(100vh - 250px);
  overflow-y: auto;
  background-color: #1e1e1e;
  border-radius: 5px;
}

.log-container {
  display: flex;
  flex-direction: column;
  color: #fff;
  padding: 10px;
  height: 100%;
  border-radius: 5px;
}

.log-item {
  margin: 2px 0;
  padding: 5px;
  background-color: #3e3e3e;
  border-radius: 5px;
}
</style>