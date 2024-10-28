<template>
    <el-scrollbar height="100vh">

        <div class="container-main" style="padding-bottom:50px;background-color: #fff;">
            <div class="tpl-app" v-loading="fullscreenLoading">

                
    <div class="search-container-panel">
      <el-row>
        <el-col :span="24">
          <div class="feature-header-xqbyQk" style="    
    position: relative;
    display: grid;
    grid-template-columns: 1fr minmax(auto, 480px) 1fr;
    gap: 12px;
    width: 100%;">
            <div style="gap: 12px;">
              <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                智能体市场</h1>
            </div>
            <div class="search-container-weDuEn">
              <el-input
      v-model="input1"
      style="width: 400px"
      size="large"
      placeholder="搜索智能体"
      :suffix-icon="Search"
    />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

            <div class="header">
                <span style="font-size: 13px;margin-left:10px;color: #a5a5a5;">这里包含所有需要运营的能力服务列表</span>
            </div>

            <div class="popular border-bottom">
                <div class="popular-item" 
                    v-for="(item , index) in demoProductList"
                    :key="index"
                    >
                    <img :alt="item.name" 
                        class="popular-item__banner" 
                        :src="'http://data.linesno.com/banner/' + (index%4 + 1) + '.png'" />
                    <h1 class="popular-item__title">
                        {{ item.name }} 
                    </h1>
                    <div class="popular-item__desc" style="text-align: left"> 
                        {{ truncateString(item.description,20) }}
                    </div>
                </div>
            </div>
            

                <section v-for="(type, index) in productList" :key="index">
                    <h2 class="section-title" style="margin-left:10px">
                        <i :class="type.banner" /> {{ type.name }}
                    </h2>
                    <div class="section-body">

                        <el-row>
                            <el-col :span="6" v-for="(item, i) in type.agents" :key="i" style="padding:8px;">
                                <div class="semi-card-container"  @click="handleRoleChat(item)">
                                    <div class="semi-space cart-head-continer" style="gap: 16px;">
                                        <div class="cart-head-content">
                                            <div class="cart-head-content">
                                                <span class="semi-avatar semi-avatar-square" style="border-radius: 50%;">
                                                    <img :src="imagePathByPath(item.roleAvatar)">
                                                </span>
                                            </div>
                                        </div>
                                        <div class="semi-space info-container" style="gap: 6px;height:100px">


                                            <span class="semi-typography card-title">
                                                <span>
                                                    {{ item.roleName }}
                                                </span>
                                            </span>
                                            <div class="semi-space container-center" style="gap: 6px;">
                                                <div class="semi-image avatar-oDHtb3"
                                                    style="width: 14px; height: 14px;">
                                                    <img src="https://p3-passport.byteacctimg.com/img/user-avatar/7a7e80566f20a6944faaaa3a010fbff4~300x300.image"
                                                        class="semi-image-img" style="border-radius: 50%;">
                                                </div>
                                                <div class="semi-space semi-space-align-center semi-space-horizontal"
                                                    style="gap: 2px;">
                                                    <span class="semi-typography text"
                                                        style="max-width: 150px;"><span>韦恩W</span></span>
                                                </div>
                                            </div>
                                            <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;margin-bottom:0px">
                                                <span>
                                                    {{ truncateString(item.responsibilities,100) }}
                                                </span>
                                            </p>
                                            <div class="semi-space card-tag-list" style="gap: 4px;"></div>
                                        </div>
                                    </div>

                                    <!-- <div class="semi-divider semi-divider-horizontal"></div> -->
                                    <div class="semi-space">
                                        <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children" style="display: inline-flex;">
                                            <div class="semi-space card-statics" style="gap: 8px;">
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 1.2K</span>
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                                                <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            </el-col>
                        </el-row>

                        <!--
                        <div class="app-item border-bottom export-item" v-for="(item, i) in type.agents" :key="i">
                            <div class="app-item__icon wk wk-icon-user">
                                <img :src="imagePathByPath(item.roleAvatar)"
                                    style="width:45px;height:45px;border-radius: 5px" />
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
                                    {{ truncateString(item.responsibilities, 20) }}
                                </div>
                            </div>
                        </div>
                        -->
                        
                    </div>
                </section>
            </div>

            <el-dialog v-model="dialogVisible" width="860" :before-close="handleClose">
                <iframe :src="roleChatUri" class="role-chat-iframe"></iframe>
            </el-dialog>

        </div>
    </el-scrollbar>
</template>

<script setup name="ServiceList">

import { ElLoading } from 'element-plus'
import { getAllCatalog } from "@/api/base/im/robot";
import SnowflakeId from "snowflake-id";

const router = useRouter();
const productList = ref([])
const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

const snowflake = new SnowflakeId();

const demoProductList = ref(
    [
  {
    "name": "销售精英团队",
    "typeDescribe": "销售",
    "teamSize": 20,
    "mainResponsibilities": ["客户关系管理", "达成销售目标", "市场分析"],
    "description": "专注于B2B领域的销售活动，与关键客户建立长期合作关系，并推动公司产品的市场份额增长。"
  },
  {
    "name": "市场营销先锋队",
    "typeDescribe": "市场营销",
    "teamSize": 15,
    "mainResponsibilities": ["品牌建设", "数字营销", "内容创作"],
    "description": "通过创意营销策略提高品牌知名度，并利用多种渠道进行有效传播，以吸引潜在顾客并促进转化率。"
  },
  {
    "name": "客户服务之星",
    "typeDescribe": "客户服务",
    "teamSize": 30,
    "mainResponsibilities": ["解决客户问题", "提升客户满意度", "收集反馈"],
    "description": "致力于提供优质的客户服务体验，确保每一位客户的疑问都能得到及时有效的解答。"
  },
  {
    "name": "产品创新实验室",
    "typeDescribe": "产品研发",
    "teamSize": 25,
    "mainResponsibilities": ["新产品开发", "技术研究", "用户体验设计"],
    "description": "探索前沿科技，不断推出符合市场需求的新产品和服务，同时优化现有产品线。"
  },
  {
    "name": "运营支持小组",
    "typeDescribe": "运营管理",
    "teamSize": 18,
    "mainResponsibilities": ["流程优化", "项目管理", "资源调配"],
    "description": "确保日常运营顺畅高效地进行，协调跨部门合作，支持公司整体战略实施。"
  },
  {
    "name": "技术攻关小分队",
    "typeDescribe": "技术支持",
    "teamSize": 22,
    "mainResponsibilities": ["系统维护", "故障排除", "技术创新"],
    "description": "负责公司内部IT系统的稳定运行，解决技术难题，同时探索新技术以提高业务效率。"
  },
  {
    "name": "运营优化组",
    "typeDescribe": "运营优化",
    "teamSize": 20,
    "mainResponsibilities": ["数据分析", "流程改进", "绩效评估"],
    "description": "通过数据分析来识别运营中的瓶颈，提出改进建议并实施优化措施，以持续提升运营效率和效果。"
  }
]
)

/**  获取产品列表 */
function handleGetProductList() {
    const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
    })
    getAllCatalog().then(response => {
        console.log('response = ' + response);
        productList.value = response.data;
        loading.close();
    });
}

/** 与单个Role发信息 */
function handleRoleChat(item) {

    router.push({
        path: '/single/agentChat',
        query: { 'roleId': item.id , 'channelId': snowflake.generate() }
    })
    
    // roleChatUri.value = "/agentChat?roleId=" + item.id;
    // chatTitle.value = item.roleName;
    // dialogVisible.value = true;
}

/** 关闭对话框 */
function handleClose() {
    dialogVisible.value = false;
}

handleGetProductList();

</script>
