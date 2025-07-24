<template>
  <div style="padding-top:0px;">
    <el-row class="acp-dashboard-panel" :gutter="20">
      <el-col class="panel-col"  :span="19" style="padding:0px !important">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-link"></i> Agent运营团队</div>
          </div>
        <div class="data-card">
            <ul class="count-list summary-panel">
              <li class="count-data bg-red" v-for="(item , index) in runCountArr" :key="index">
                <i :class="getRandomIcon()"></i>
                <span class="label-tip">
                  <a target="_blank" :href="item.link">
                    <img :src="imagePath(item.roleAvatar)" style="width:40px;height:40px;float:left; border-radius: 50%" />
                    <div style="float: left;margin-top: 3px;margin-left: 10px;width: calc(100% - 50px);">
                      {{ item.roleName }} 
                      <br/>
                      <span class="label" style="font-size: 13px;line-height: 13px;font-weight: lighter;">
                        {{ truncateString(item.responsibilities,18) }}
                      </span>
                    </div>
                  </a>
                </span>
              </li>
              <li class="count-data bg-red" style="background: #409eff57;border: 1px solid #409eff40;">
                <i :class="getRandomIcon()"></i>
                <span class="label-tip">
                  <!-- <router-link to="/dashboard/smart/assistant/roleTemplate/index"> -->
                  <router-link to="/expert/smart/assistant/role/index">
                    <div class="app-icon" style="margin-top: 0px;width: 40px;height:40px;float:left;border-radius: 8px;font-size: 15px;">
                        <i class="fa-solid fa-plus" style="top:5px" />
                      </div>
                    <div style="float: left;margin-top: 3px;margin-left: 10px;width: calc(100% - 50px);">
                       添加角色
                      <br/>
                      <span class="label" style="font-size: 13px;line-height: 13px;font-weight: lighter;">
                       添加用户自定义角色
                      </span>
                    </div>
                  </router-link>
                </span>
              </li>
            </ul>
        </div>
      </el-col>

      <el-col class="panel-col"  :span="5" style="padding:0px !important">
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
              </div>
            </el-col>

    </el-row>
  </div>
</template>

<script setup>

import { 
  getNewestRole 
} from '@/api/smart/assistant/dashboard.js'

const runCountArr = ref([]);

// 随机图标 
const icons = ref([
    {icon:'fa-solid fa-file-shield'},
    {icon:'fas fa-shipping-fast'},
    {icon:'fa-solid fa-feather-pointed'},
    {icon:'fas fa-train'},
    {icon:'fas fa-server'},
    {icon:'fas fa-shipping-fast'},
    {icon:'fa-solid fa-feather-pointed'},
    {icon:'fas fa-pencil-ruler'} 
]);


const opertionAssets = ref([
  {id:'1' , title:'专家角色' , count:15} ,
  {id:'5' , title:'向量存储' , count:'445G'} ,
  {id:'3' , title:'数据渠道' , count:65} ,
  {id:'4' , title:'渠道统计' , count:85} ,
  {id:'6' , title:'业务服务' , count:145} ,
])

/** 获取最新角色 */
function handleNewestRole(){
  getNewestRole().then(response => {
    runCountArr.value = response.data;
  });
}

// 编写一个方法来随机选择一个icon
function getRandomIcon() {
  console.log('icons length = ' + icons.value.length) ;

  const randomIndex = Math.floor(Math.random() * icons.value.length);
  console.log('icons[randomIndex] = ' + randomIndex) ;

  return icons.value[randomIndex].icon;
}

handleNewestRole() ;

</script>

<style scoped lang="scss">

.panel-col{
    border: 0px !important;
}
.chart {
  height: 400px;
}

.homeHeader {
  position: relative;
  box-sizing: border-box;
  padding: 24px 24px 0;
  background-color: #fff;
  box-shadow: 0 1px 0 0 #e3e4e6;
  z-index: 1;
}

.homeHeader h1 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #333;
  line-height: 24px;
}

.widget-title {
  font-size: 14px;
  color: #333;
  line-height: 24px;
  font-weight: 400;
  //    float: left;
  width: 100%;
}

.function-btn{
  width: 100%;
  float: left;
  margin-bottom: 10px;
  margin-left: 0px;
}

.app-icon {
  width: 32px;
  height: 32px;
  position: relative;
}

.el-row {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}

.el-col {
  border-radius: 2px;
  background: #fff;
  padding: 20px !important;
  padding-left: 20px !important;
  padding-right: 20px !important;
  border: var(--card-border-width, 1px) var(--card-border-style, solid) var(--card-border-color, #e3e4e6);
}

.el-col-8 {
  width: calc(33.3333333333% - 10px);
  margin-left: 10px;
}

.widget-bulletin-list .item {
  height: 32px;
  line-height: 32px;
  white-space: nowrap;
  -o-text-overflow: ellipsis;
  text-overflow: ellipsis;
  overflow: hidden;
  font-size: 12px;

  a {
    color: #555;
  }
}

.product_list_li:nth-child(3),
.product_list_li:nth-child(6) {
  border-right: 0px;
}

.product_list_li {
  width: 30% !important;
  padding: 16px !important;
  border-radius: 0px !important;
  border-right: 1px solid #eee;
  font-size: 12px;
  height: 90px;
  overflow: hidden;

  .text-title {
    font-size: 16px;
    color: #333;
    cursor: pointer;
    letter-spacing: 0;
    line-height: 24px;

    span.recom-label {
      color: #005bd4;
      font-weight: 700;
      border: 1px solid #005bd4;
      border-radius: 3px;
      margin-left: 10px;
      padding: 2px;
      position: absolute;
      line-height: 18px;
      font-size: 8px;
    }
  }

  .text-descript {
    margin-top: 8px;
    font-size: 13px;
    color: #333;
    letter-spacing: 0.37px;
    line-height: 20px;
  }
}

.widget-bulletin-list {
  .line {
    position: absolute;
    left: -16px;
    right: -16px;
    height: 1px;
    background-color: #e3e4e6;
  }

  .pin {
    position: relative;
    margin-bottom: 15px;
    margin-top: 15px;

    .msg {
      padding: 8px 12px;
      line-height: 18px;
      color: #333;
      font-size: 12px;
      background-color: #eff3f8;
      -webkit-border-radius: 2px;
      -moz-border-radius: 2px;
      border-radius: 2px;
      margin-bottom: 16px;
    }
  }
}

.bg-purple-dark {
  background: #99a9bf;
}

.bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #e5e9f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}

.next-col-4 {
  -ms-flex: 0 0 16.66667%;
  -webkit-flex: 0 0 16.66667%;
  flex: 0 0 16.66667%;
  width: 16.66667%;
  max-width: 16.66667%;
  float: left;
}

.subtitle {
  font-weight: 500;
  font-size: 12px;
  color: #666;
  line-height: 20px;
  margin: 16px 0 8px;
}

.thm-uglier {
  list-style: none;
  margin: 0;
  padding: 0;
}

.product_list {
  a {
    display: flex;
    align-items: center;
    padding: 0 16px;
    background-color: #f9f9f9;
    width: 100%;
    line-height: 36px;
    text-decoration: none;
    color: #333;
    transition: all 250ms linear;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.product_list>li {
  padding: 0 8px 8px 0;
  overflow: hidden;
  flex: none;
  float: left;
  border-radius: 4px;

  a {
    line-height: 42px;

    i {
      display: none;
      border: 1px solid #dedede;
      border-radius: 14px;
      width: 28px;
      height: 28px;
      text-align: center;
    }
  }

  .WidgetHomeProductMy-product_name-TNtW6 {
    flex: 1;
    margin-left: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.h3-title {
  font-size: 16px;
  font-weight: 500;
  margin-top: 0px;
  margin-bottom: 10px;
  padding-left: 16px;
}

.text-icons {
  float: right;
  margin-right: 10px;
  font-size: 16px;
  font-weight: 200;
  color: #ccc;
}

li.product_list_li.count-li {
  width: calc(50% - 16px) !important;
  height: 160px;
  border-right: 0px;
  border-radius: 5px !important;
  margin: 0px 0px 16px 16px !important;
}

.count-text-button>button {
  padding: 0 16px;
  height: 32px;
  line-height: 30px;
  font-size: 12px;
  border-width: 1px;
  border-style: solid;
  background-color: #005bd4;
  border-color: transparent;
  margin-top: 20px;
  color: #fff;
  border-radius: 3px;
  cursor: pointer;
}

.el-main,
.el-aside {
  padding: 0px;
  background: #fafafa !important;
}

.el-aside {
  padding: 20px 10px;
}

.secondary {
  font-weight: 500;
  margin-left: 5px;
  padding-left: 0;
}

.data-card {
  background: #fff;
  border-radius: 2px;
  float: left;
  width: 100%;
  margin-bottom: 15px;

  .title {
    margin-top: 0px;
    margin-bottom: 10px;
    font-size: 16px;
    font-weight: 500;
  }

.bg-purple-dark {
  background: #99a9bf;
}

.bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #e5e9f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}

  ul.count-list {
    list-style: none;
    float: left;
    padding: 0px;
    margin: 0px;
    width: 100%;

    li.count-data {
      float: left;
      background: #f7f9fa;
      border-color: #f7f9fa;
      flex: 1;
      position: relative;
      text-align: left;
      margin-right: 8px;
      padding: 10px 10px;
      width: calc(33% - 16px);
      text-decoration: none;
      overflow: hidden;
      border-width: 0px;
      border-style: solid;
      border-radius: 8px;
      transition: all 0.1s linear;
      height: 60px;
      margin-top: 6px;
      margin-left: 10px;

      i{
        float: right;
        position: absolute;
        right: 10px;
        font-size: 30px;
        opacity: .4;
        top: 15px;
        color: #005bd5;
      }

      span.label-tip {
        width: 100%;
        float: left;
        font-size: 15px;
        font-weight: 550;
        color: #1d75b0;
      }

      span.label {
        color: #333;
        // display: block;
        background: none;
        line-height: 36px;
        font-size: 20px;
      }
    }
  }
}

.summary-panel {
  .el-col {
    border-radius: 5px;
    border-width: 0px;
  }

  i,
  .task-num {
    color: #fff;
  }

  .bg-red {
    background: #f25643;
  }

  .bg-green {
    background: #3296fa;
  }

  .bg-orange {
    background: #fe892c;
  }
}

</style>