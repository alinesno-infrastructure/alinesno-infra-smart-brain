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
])

const apps = ref([]);

</script>

<style lang="scss" scoped>
.item-health-title{
  margin-bottom: 5px !important;
}
.item-health-count{
  margin-bottom: 5px;
}
</style>