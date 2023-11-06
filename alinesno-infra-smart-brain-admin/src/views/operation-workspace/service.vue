<template>
  <div>
    <el-row class="acp-dashboard-panel" :gutter="20">
      <el-col class="panel-col" :span="19">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-file-waveform"></i> 从常用模板开始</div>
          </div>
          <div class="panel-body acp-height-auto" >
            <div class="acp-app-list">
              <ul>
                <li class="app-items acp-template-item" v-for="item in apps" :key="item">
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
            <div class="header-title"><i class="fa-solid fa-user-nurse"></i> 自动生成概览</div>
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
  {id:'1' , title:'我的生成文档' , count:45} ,
  {id:'6' , title:'项目集成情况' , count:45} ,
  {id:'2' , title:'自定义Prompt' , count:145} ,
  {id:'3' , title:'导入知识库' , count:65} ,
  {id:'4' , title:'任务生成完成' , count:85} ,
  {id:'5' , title:'任务生成异常' , count:45} ,
  {id:'7' , title:'AI调用次数' , count:14865745} ,
])

const apps = ref([
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'需求文档生成' , desc:'需求分析生成和下载'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/9da5a168cf8194c8ee5ed192a443d563-674375b53bc8ae94f48cfdb5c81e8363.svg' , name:'项目实施进度计划' , desc:'项目实施进度计划开始'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/a5ffe5487f62ef75d8e5cf78c18525a5-d4867f9d4adcd749f0c5aff987232847.svg' , name:'项目实施方案' , desc:'项目落地实施方案'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'项目实施进度计划' , desc:'项目进度计划模板'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/9da5a168cf8194c8ee5ed192a443d563-674375b53bc8ae94f48cfdb5c81e8363.svg' , name:'产品功能设计列表' , desc:'项目和产品功能设计文档'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/a5ffe5487f62ef75d8e5cf78c18525a5-d4867f9d4adcd749f0c5aff987232847.svg' , name:'数据库设计说明' , desc:'初始化数据库文档结构'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/6e9e16955bd76c1d3a9996e0d6d3ad15-04cfc8c62f597690680d948b366980cf.svg' , name:'产品组织结构设计' , desc:'项目和产品研发组织架构'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'项目进度简报' , desc:'项目进度模板'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'安装维护手册' , desc:'项目部署维护文档'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/3da5e8169d2f1426f99fbef54575fe96-6382cb2dfdd2f74c99bc8a64a338358e.svg' , name:'培训文档手册' , desc:'平台培训和项目培训文档'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/6e9e16955bd76c1d3a9996e0d6d3ad15-04cfc8c62f597690680d948b366980cf.svg' , name:'产品使用手册' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'产品介绍文档' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/a5ffe5487f62ef75d8e5cf78c18525a5-d4867f9d4adcd749f0c5aff987232847.svg' , name:'K8S部署脚本生成' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/3da5e8169d2f1426f99fbef54575fe96-6382cb2dfdd2f74c99bc8a64a338358e.svg' , name:'产品说明文档' , desc:'With Route 53 (3 分钟)'},
]);

</script>

<style lang="scss" scoped>
.item-health-title{
  margin-bottom: 5px !important;
}
.item-health-count{
  margin-bottom: 5px;
}

.acp-template-item{
  width: calc(20% - 10px) !important;
  margin-right: 10px;
  cursor: pointer;
  border: 1px solid #eaeded !important;
  border-radius: 3px;
  padding: 15px !important;
  margin-bottom: 10px ;
}
</style>