<template>
  <el-scrollbar height="100vh">

  <div class="container-main tpl-app" style="padding-bottom:50px;background-color: #fff;">
      <div class="tpl-app app-main-content" v-loading="fullscreenLoading" style="max-width: 1240px;">

            <section class="section" v-for="(type, index) in productList" :key="index">
                <h2 class="section-title">
                    <i :class="type.banner" /> {{ type.name }}
                </h2>
                <br/>
                <div class="section-body">
                    <div class="app-item border-bottom export-item" v-for="(item, i) in type.agents" :key="i">
                        <div class="app-item__icon wk wk-icon-user">
                          <img :src="imagePathByPath(item.roleAvatar)" style="width:45px;height:45px;border-radius: 5px"/>
                        </div>
                        <div class="content">
                            <h3 class="app-item__title" style="margin-top:5px;margin-bottom:5px;">
                                {{ item.roleName }}
                                <a class="cf-service-nav-item-label" style="margin-left:10px;margin-right:10px;float:right" :title="item.name">
                                    <el-button type="primary" text bg @click="handleRoleChat(item)">
                                       <i class="fa-solid fa-location-arrow"></i>&nbsp; 咨询
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

      <!-- 频道聊天 -->
      <el-dialog v-model="dialogVisible" width="860" :before-close="handleClose">
        <iframe :src="roleChatUri" class="role-chat-iframe"></iframe>
      </el-dialog>

  </div>
  </el-scrollbar>
</template>

<script setup name="ServiceList">

import { ElLoading } from 'element-plus'
import { getAllCatalog } from "@/api/base/im/robot";

const productList = ref([])
const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

/**  获取产品列表 */
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
}

/** 与单个Role发信息 */
function handleRoleChat(item){
    roleChatUri.value = "/agentChat?roleId=" + item.id;
    chatTitle.value = item.roleName ;
    dialogVisible.value = true ;
}

/** 关闭对话框 */
function handleClose(){
    dialogVisible.value = false ;
}

handleGetProductList() ;

</script>
