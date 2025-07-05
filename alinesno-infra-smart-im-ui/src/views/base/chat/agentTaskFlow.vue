<template>
    <div style="margin: -10px;margin-top: -20px;">
        <!-- <div>
            频道运行任务列表
        </div> -->
        <el-scrollbar height="500" ref="scrollbarRef" >
            <div ref="innerRef">

                <div class="task-item-message" v-for="(item , index) in tableData" :key="index">
                    <div>
                        <div class="task-status-job-result">
                            <span class="task-status-name task-status-icon">
                                <i v-if="item.taskType == 1" class="fa-solid fa-truck-fast"></i> 
                                <i v-if="item.taskType == 4" class="fa-solid fa-list-ol"></i>
                                <i v-if="item.taskType == 5" class="fa-solid fa-file-waveform"></i>
                            </span>
                            <span class="task-status-name">
                                {{ item.taskName }} 
                            </span>
                            {{ item.assistantContent }}
                            <span class="task-status-job-usageTime">
                                {{ item.businessId }} - {{ item.usageTime }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>

<script setup>

import { nextTick } from 'vue'
import { getParam } from '@/utils/ruoyi'

import {
  // closeChannelSSE  ,
  getFlowTaskNotice
} from "@/api/base/im/channel";

// 滚动条的处理_starter
const innerRef = ref(null);
const scrollbarRef = ref(null);
let evtSource = ref(null) ; 

// 任务类型taskType: 1消息结果|2思考任务|3排除状态|4循环中|5状态通知
const tableData = ref([
  {
    taskStatus: 'completed',
    taskType:'1',
    businessId: 'B001',
    taskName: '财务报告数据分析',
    assistantContent: '我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈业务拓展战略规划遇到了一些意料之外的挑战，需要全面重新评估市场情况、资源配置和风险缓解策略。尽管遇到了挫折，但我们学到了宝贵的经验教训。',
    usageTime: '2小时',
  }
])

// const initEventSource = () =>{
//   nextTick(() => {

//     if (typeof (EventSource) !== 'undefined') {

//       let ssePath = import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/sseChannelTask/createSseConnect?type=task" ;
//       evtSource = new EventSource(ssePath , { withCredentials: false }) // 后端接口，要配置允许跨域属性

//       // 与事件源的连接刚打开时触发
//       evtSource.onopen = function(e){
//         console.log(e);
//       }

//       // 当从事件源接收到数据时触发
//       evtSource.onmessage = function(e){
//         console.log(e.data);

//         if(e.data){
//           // 插入测试数据
//           tableData.value.splice(tableData.value.length, 0, JSON.parse(e.data)) ; 

//           // 保留最后100条数据，删除多余数据
//           if (tableData.value.length > 20) {
//               tableData.value.splice(0, tableData.value.length - 20);
//           }
//         }

//         initChatBoxScroll();
//       }
//       // 与事件源的连接无法打开时触发
//       evtSource.onerror = function(e){
//         console.log(e);
//         evtSource.close(); // 关闭连接
//       }
//       // 也可以侦听命名事件，即自定义的事件
//       evtSource.addEventListener('msg', function(e) {
//         console.log(e.data)
//       })
//     } else {
//       console.log('当前浏览器不支持使用EventSource接收服务器推送事件!');
//     }

//   })
  
// }

function initChatBoxScroll() {
  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight) ; 
  })
}

// onMounted(() => {
//   // 在组件加载完成后执行的代码
//   console.log('Component mounted');
//   initEventSource() ;
// });

// onBeforeUnmount(() => {
//   // 在组件卸载之前执行的代码
//   console.log('Component about to be unmounted');
//   evtSource.close(); // 关闭连接

//   let channelId = getParam("channel");
//   console.log('channelId = ' + channelId) ; 

//   closeChannelSSE(channelId).then(resp => {
//     console.log('close channel = ' + channelId) ;
//   })

// });

/** 获取到当前执行中的任务消息 */
function handleFlowTaskNotice(){

  const channelId = getParam("channel");

  getFlowTaskNotice().then(response => {

    const data = response.data ;
    console.log('data = ' + data) ;

    if(data && data.length > 0){
      for(let i = 0 ; i < data.length ; i ++){
        const messageChannelId = data[i].channelId ; 

          // 插入测试数据
          tableData.value.splice(tableData.value.length, 0, data[i]) ; 

          // 保留最后100条数据，删除多余数据
          // if (tableData.value.length > 20) {
          //     tableData.value.splice(0, tableData.value.length - 20);
          // }

        // if(parseInt(channelId) == messageChannelId){

        //   // 插入测试数据
        //   tableData.value.splice(tableData.value.length, 0, data[i]) ; 

        //   // 保留最后100条数据，删除多余数据
        //   if (tableData.value.length > 20) {
        //       tableData.value.splice(0, tableData.value.length - 20);
        //   }
        // }

      }

      initChatBoxScroll();
    }

  })
}


/** 获取定时任务服务 */
let timer = null;
onMounted(() => {
  //  timer = setInterval(() => {
  //    handleFlowTaskNotice() ;
  //  }, 5*1000);
})

/** 任务实例销毁 */
onBeforeUnmount(() => {
  //  clearInterval(timer)
   timer = null;
})

</script>

<style lang="scss" scoped>

.task-item-message{
    background: #f5f5f5;
    padding: 10px;
    line-height: 1.5rem;
    border-radius: 5px;
    margin-bottom: 5px;

    span.task-status-job-usageTime {
        font-size: 13px;
        color: #F56C6C;
        font-weight: bold;
    }
    span.task-status-icon{
        color: #145799 !important;
    }

    span.task-status-name{
        color: #F56C6C ;
        font-weight: bold;
        margin-left: 2px;
        margin-right: 2px;
        font-size: 14px;
    }
}

</style>