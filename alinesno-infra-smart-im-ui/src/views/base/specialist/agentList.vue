<template>
  <el-scrollbar height="100vh">

  <div class="container-main" style="padding-bottom:50px;background-color: #fff;">
      <div class="tpl-app app-main-content" v-loading="fullscreenLoading" style="max-width: 1240px;">

            <section class="section" v-for="(type, index) in productList" :key="index">
                <h2 class="section-title">
                    <i :class="type.banner" /> {{ type.name }}
                </h2>
                <br/>
                <div class="section-body">
                    <div class="app-item border-bottom export-item" v-for="(item, i) in type.agents" :key="i">
                        <div class="app-item__icon wk wk-icon-user">
                          <img :src="imagePath(item)" style="width:45px;height:45px;border-radius: 5px"/>
                        </div>
                        <div class="content">
                            <h3 class="app-item__title" style="margin-top:5px;margin-bottom:5px;">
                                {{ item.roleName }}
                                <a class="cf-service-nav-item-label" style="margin-left:10px;margin-right:10px;float:right" @click="saveCollectProduct(item)" :title="item.name">
                                    <el-button type="primary" text bg>
                                       <i class="fa-solid fa-location-arrow"></i>&nbsp;咨询
                                    </el-button>
                                </a>
                            </h3>
                            <div class="app-item__desc">
                               {{ truncateString(item.responsibilities,20) }} 
                            </div>
                        </div>
               
                    </div>
                </div>
            </section>

      </div>
  </div>
  </el-scrollbar>
</template>

<script setup name="ServiceList">

import { ElLoading } from 'element-plus'

const productList = ref([]);
const fullscreenLoading = ref(false);
import { getAllCatalog } from "@/api/base/im/robot";

function handleGetProductList() {

    const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
    })
  getAllCatalog().then(response => {
        console.log('response = ' + response);
        productList.value = response.data ;
        loading.close() ;
  });
};

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.roleAvatar){
    roleAvatar = row.roleAvatar ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

function openService(item){
  var link = item.linkPath ;
  // 使用 window.open 在当前窗口打开链接
  window.open(link, '_self');
}

// 获取产品列表
handleGetProductList() ;


</script>

<style lang="scss" scoped>
.tpl-app {
  overflow: auto;
  color: #172b4d;
  background-color: #fff;
  padding: 10px;
  margin: 10px;
  width: 90%;
  margin: auto;

  .header {
      font-size: 24px;
      margin-left: 0px;
  }

  .popular {
      display: flex;
      align-content: center;
      justify-content: space-between;
      width: calc(100% - 20px);
      padding-bottom: 24px;
      margin-top: 24px;

      .popular-item {
          width: calc(20% - 20px);
          padding-right: 20px;
          cursor: pointer;
          text-align: center;
          margin-left: 15px;
      }

      .popular-item__banner {
          width: 50%;
          border-radius: 4px;
      }

      .popular-item__title {
          margin: 20px 0 6px;
          font-size: 20px;

          i {
              font-size: 40px;
              color: #3b5998;
              margin-right: 10px;
          }
      }

      .popular-item__desc {
          font-size: 13px;
          color: #6b778c;
      }
  }

  .section {
      padding: 0px 20px 30px;

      &:last-child {
          border-bottom: 0 none;
      }

      .section-title {
          margin-left: -20px;
          font-size: 20px;
          margin-bottom: 0px;
      }

      .section-body {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          flex-wrap: wrap;
          width: 100%;
          margin: 0 -2.5%;

          .app-item {
              width: calc(33% - 20px);
              padding: 20px 0;
              margin: 0 10px;

              .content {
                  -webkit-box-flex: 1;
                  -ms-flex: 1;
                  flex: 1;
                  overflow: hidden;
              }
          }

          .app-item__icon {
              display: flex;
              align-items: center;
              justify-content: center;
              margin-top: 0px;
              width: 48px;
              height: 48px;
              margin-right: 5px;
              font-size: 35px;
              background-color: #fff;
              border-radius: 4px;
          }

          .app-item__control {
              margin-left: 15px;

              .plain-btn {
                  padding: 8px 12px;
                  color: #3b5998;
                  font-size: 13px;
                  cursor: pointer;
                  border: 1px solid #3b5998;
                  border-radius: 4px;
              }
          }

          .app-item__title {
              margin-bottom: 0px;
              font-size: 16px;
          }

          .app-item__desc {
              overflow: hidden;
              font-size: 13px;
              color: #6b778c;
              text-overflow: ellipsis;
              white-space: nowrap;
          }
      }
  }
}

.border-bottom {
  border-bottom: 1px solid #dfe1e6;
}

img {
  border-style: none;
}

.tpl-app .section .section-body .app-item,
.tpl-app .section .section-body {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.wk {
  font-family: wk !important;
  font-size: 16px;
  font-style: normal;
}

span.product-text-label {
  color: #3b5998;
  font-size: .7rem !important;
  border: 1px solid #3b5998;
  padding: 2px 5px;
  border-radius: 3px;
}

span.normal {
  color: #fe892c;
  border: 1px solid #fe892c;
}

span.design {
  color: #d5d5d5 !important;
  border: 1px solid #d5d5d5 !important;
}

.export-item {
    border-bottom: 0px;
    background: #fafafa;
    padding-bottom: 20px;
    border-radius: 5px !important;
    padding-left: 20px !important;
    margin-bottom: 10px !important;
}

</style>
