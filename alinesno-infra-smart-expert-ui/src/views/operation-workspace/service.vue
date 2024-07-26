<template>
  <div>
    
    <el-row class="acp-dashboard-panel" :gutter="20">

      <el-col class="panel-col" :span="19">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-file-waveform"></i> 专家集成服务</div>
          </div>
          <div class="panel-body acp-height-auto" style="padding: 5px">
            <div class="acp-app-list">
              <ul>
                <li class="app-items" v-for="item in apps" :key="item"
                  style="width:calc(25% - 10px);
                         height:80px;
                         border:1px solid #ebebeb;
                         margin:5px;
                         padding: 10px;
                         border-radius: 3px;">
                  <div class="app-icon">
                    <img :src="item.icon" :alt="item.name" />
                  </div>
                  <div class="app-info">
                    <div class="app-item-title">{{ item.name }}</div>
                    <div class="app-item desc">{{ item.desc }}</div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
          <div class="panel-footer">
            <div class="footer-link">
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="5">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-user-nurse"></i> 助手服务统计</div>
          </div>
          <div class="panel-body acp-height-auto">
            <ul class="panel-item-text">
              <li style="width:50%;padding:4px;border-bottom: 0px;" v-for="item in opertionAssets" :key="item.id">
                <div class="item-health-box">
                  <div class="item-health-title">{{ item.title }}</div>
                  <div class="item-health-count">{{ item.count }}</div>
                </div>
              </li>
            </ul>
          </div>
          <div class="panel-footer">
            <div class="footer-link">
            </div>
          </div>
        </div>
      </el-col>

    </el-row>
  </div>
</template>

<script setup>

const opertionAssets = ref([
  {id:'1' , title:'专家角色' , count:15} ,
  {id:'5' , title:'向量存储' , count:'445G'} ,
  {id:'3' , title:'数据渠道' , count:65} ,
  {id:'4' , title:'渠道统计' , count:85} ,
  {id:'6' , title:'业务服务' , count:145} ,
  {id:'7' , title:'接口调用' , count:40} ,
  {id:'2' , title:'存在知识库' , count:7649145} ,
])

const apps = ref([
  {"icon":"http://data.linesno.com/icons/circle/Funny Bunny-1.png","name":"技术总监","name_en":"CTO","desc":"负责执行和管理技术战略，领导技术团队以实现公司的技术目标和愿景。"},
  {"icon":"http://data.linesno.com/icons/circle/Delivery boy-2.png","name":"解决方案架构师","name_en":"Solution Architect","desc":"负责设计全面的解决方案架构，确保系统满足业务需求和技术要求。"},
  {"icon":"http://data.linesno.com/icons/circle/Delivery boy-1.png","name":"开发工程师","name_en":"Developer","desc":"负责编写、测试和维护软件代码，实现产品功能和特性。"},
  {"icon":"http://data.linesno.com/icons/circle/Delivery boy-2.png","name":"产品经理","name_en":"Product Manager","desc":"负责规划和执行产品策略，管理产品生命周期，满足市场需求。"},
  {"icon":"http://data.linesno.com/icons/circle/Delivery boy-3.png","name":"系统分析员","name_en":"Systems Analyst","desc":"负责研究和分析系统需求，协助设计和开发团队达成业务目标。"},
  {"icon":"http://data.linesno.com/icons/circle/Delivery boy-4.png","name":"项目经理","name_en":"Project Manager","desc":"负责规划和协调项目执行，确保项目按时交付、在预算内完成。"},
  {"icon":"http://data.linesno.com/icons/circle/Delivery boy-5.png","name":"技术文档编写人员","name_en":"Technical Writer","desc":"负责编写技术文档和指南，使用户理解和使用软件。"},
  {"icon":"http://data.linesno.com/icons/circle/Funny Bunny-1.png","name":"运维工程师","name_en":"DevOps Engineer","desc":"负责维护软件系统的稳定性和运行效率，实施自动化操作。"},
  {"icon":"http://data.linesno.com/icons/circle/Funny Bunny-2.png","name":"UX设计师","name_en":"UX Designer","desc":"负责设计和优化用户界面和用户体验，提升产品易用性。"},
  {"icon":"http://data.linesno.com/icons/circle/Funny Bunny-3.png","name":"测试工程师","name_en":"QA Engineer","desc":"负责确保软件质量，设计和执行测试策略、报告问题。"},
  {"icon":"http://data.linesno.com/icons/circle/Funny Bunny-4.png","name":"数据分析师","name_en":"Data Analyst","desc":"负责收集、分析和解释数据，提供关于产品和用户行为的见解。"},
]);

</script>

<style lang="scss" scoped>
.item-health-title{
  margin-bottom: 5px !important;
}
.item-health-count{
  margin-bottom: 5px;
}
</style>