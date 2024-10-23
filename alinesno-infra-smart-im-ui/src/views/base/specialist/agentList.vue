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

const router = useRouter();
const productList = ref([])
const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

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

const demeChannel = ref([
    {
        "channelName": "BO播客生成计划",
        "channelDesc": "BO播客生成计划是一款基于语音识别和自然语言处理技术的对话机器人，用户可以通过它快速创建个性化的播客节目，提高节目制作效率和质量。同时，它还提供了丰富的音频特效和语音转文字功能，让用户的播客节目更具吸引力和可读性。现在微信公众号文章链接、抖音分享、小红书分享（VLOG、PLOG）都支持。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8a954c89f3104df3b404087f050e5108~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=TxPNdBsD%2Bc0Eb0yenxSmC5hgT2I%3D"
    },
    {
        "channelName": "奇幻小冒险",
        "channelDesc": "在一个遥远的国度里，隐藏着一个被大自然宠爱的村庄。这里的每一天都充满了新的冒险和温馨的故事。而你的旅程，就从这里开始……",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/2f36541186224f47863996fd44f81f9a~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=ezd4tmzPBVdE%2FKRGZnRHiGYUmuc%3D"
    },
    {
        "channelName": "文生视频✨提示专家",
        "channelDesc": "全面支持可灵、清影、海螺、通义、Vidu、筑梦、Sora、即梦等文生视频大模型，一帧一画皆是惊喜！👀 让灵感瞬间成影！",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/f65a8eee94384a41a6cd03f0ca06149f~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=U4jDFQbyYIgN%2Fy8tFSzCq29wm0U%3D"
    },
    {
        "channelName": "奇幻小冒险",
        "channelDesc": "在一个遥远的国度里，隐藏着一个被大自然宠爱的村庄。这里的每一天都充满了新的冒险和温馨的故事。而你的旅程，就从这里开始……",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/2f36541186224f47863996fd44f81f9a~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=ezd4tmzPBVdE%2FKRGZnRHiGYUmuc%3D"
    },
    {
        "channelName": "文生视频✨提示专家",
        "channelDesc": "全面支持可灵、清影、海螺、通义、Vidu、筑梦、Sora、即梦等文生视频大模型，一帧一画皆是惊喜！👀 让灵感瞬间成影！",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/f65a8eee94384a41a6cd03f0ca06149f~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=U4jDFQbyYIgN%2Fy8tFSzCq29wm0U%3D"
    },
    {
        "channelName": "合成新元素",
        "channelDesc": "通过对话的方式玩一个类似「涂鸦上帝」的元素合成游戏。初始元素是 💧 水、🔥 火、🌬️ 风、🌍 土，你可以通过不断的自由组合，来随机生成新的物质。试试看谁能把「🥽 Vision Pro」生成出来？",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8a954c89f3104df3b404087f050e5108~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=TxPNdBsD%2Bc0Eb0yenxSmC5hgT2I%3D"
    },
    {
        "channelName": "认真看一百本书",
        "channelDesc": "一款专门为阅读爱好者设计的AI读书工具。只需输入书名，AI便能迅速整理出书籍的核心内容与相关背景资料，并以简洁明了的卡片形式展示，使阅读和学习变得更加高效便捷。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/59ba56af5a8d4bbcb51f76247a40c754~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=YvhkYVJ93ndlW%2FJsZmOc1SuYHAE%3D"
    },
    {
        "channelName": "漫画生成：橘猫漫画家",
        "channelDesc": "一个能够一键帮你制作猫咪漫画的bot，欢迎来到“橘猫漫画家”，您的口袋里的漫画工作室！每一个关于橘猫的想法都能变成生动的漫画。记住，也许我们每个人都是一只橘猫。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8d2b7705ea1f458e8290f618e02f580d~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=IZkhM%2FvHrr2gpkF%2BPQO5ea9dYuY%3D"
    },
    {
        "channelName": "专业英语翻译",
        "channelDesc": "能翻译所有英语字，词，文章，论文等。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/e5e9baa528b945e6b2e8a1c3dda5775b~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=DtNyNMVpLHpXMTDr868xaq%2BRfKs%3D"
    },
    {
        "channelName": "合成新元素",
        "channelDesc": "通过对话的方式玩一个类似「涂鸦上帝」的元素合成游戏。初始元素是 💧 水、🔥 火、🌬️ 风、🌍 土，你可以通过不断的自由组合，来随机生成新的物质。试试看谁能把「🥽 Vision Pro」生成出来？",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8a954c89f3104df3b404087f050e5108~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=TxPNdBsD%2Bc0Eb0yenxSmC5hgT2I%3D"
    },
    {
        "channelName": "认真看一百本书",
        "channelDesc": "一款专门为阅读爱好者设计的AI读书工具。只需输入书名，AI便能迅速整理出书籍的核心内容与相关背景资料，并以简洁明了的卡片形式展示，使阅读和学习变得更加高效便捷。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/59ba56af5a8d4bbcb51f76247a40c754~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=YvhkYVJ93ndlW%2FJsZmOc1SuYHAE%3D"
    },
    {
        "channelName": "漫画生成：橘猫漫画家",
        "channelDesc": "一个能够一键帮你制作猫咪漫画的bot，欢迎来到“橘猫漫画家”，您的口袋里的漫画工作室！每一个关于橘猫的想法都能变成生动的漫画。记住，也许我们每个人都是一只橘猫。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8d2b7705ea1f458e8290f618e02f580d~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=IZkhM%2FvHrr2gpkF%2BPQO5ea9dYuY%3D"
    },
    {
        "channelName": "专业英语翻译",
        "channelDesc": "能翻译所有英语字，词，文章，论文等。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/e5e9baa528b945e6b2e8a1c3dda5775b~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=DtNyNMVpLHpXMTDr868xaq%2BRfKs%3D"
    },
    {
        "channelName": "奇幻小冒险",
        "channelDesc": "在一个遥远的国度里，隐藏着一个被大自然宠爱的村庄。这里的每一天都充满了新的冒险和温馨的故事。而你的旅程，就从这里开始……",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/2f36541186224f47863996fd44f81f9a~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=ezd4tmzPBVdE%2FKRGZnRHiGYUmuc%3D"
    },
    {
        "channelName": "文生视频✨提示专家",
        "channelDesc": "全面支持可灵、清影、海螺、通义、Vidu、筑梦、Sora、即梦等文生视频大模型，一帧一画皆是惊喜！👀 让灵感瞬间成影！",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/f65a8eee94384a41a6cd03f0ca06149f~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=U4jDFQbyYIgN%2Fy8tFSzCq29wm0U%3D"
    },
    {
        "channelName": "合成新元素",
        "channelDesc": "通过对话的方式玩一个类似「涂鸦上帝」的元素合成游戏。初始元素是 💧 水、🔥 火、🌬️ 风、🌍 土，你可以通过不断的自由组合，来随机生成新的物质。试试看谁能把「🥽 Vision Pro」生成出来？",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8a954c89f3104df3b404087f050e5108~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=TxPNdBsD%2Bc0Eb0yenxSmC5hgT2I%3D"
    },
    {
        "channelName": "认真看一百本书",
        "channelDesc": "一款专门为阅读爱好者设计的AI读书工具。只需输入书名，AI便能迅速整理出书籍的核心内容与相关背景资料，并以简洁明了的卡片形式展示，使阅读和学习变得更加高效便捷。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/59ba56af5a8d4bbcb51f76247a40c754~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=YvhkYVJ93ndlW%2FJsZmOc1SuYHAE%3D"
    },
    {
        "channelName": "漫画生成：橘猫漫画家",
        "channelDesc": "一个能够一键帮你制作猫咪漫画的bot，欢迎来到“橘猫漫画家”，您的口袋里的漫画工作室！每一个关于橘猫的想法都能变成生动的漫画。记住，也许我们每个人都是一只橘猫。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8d2b7705ea1f458e8290f618e02f580d~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=IZkhM%2FvHrr2gpkF%2BPQO5ea9dYuY%3D"
    },
    {
        "channelName": "专业英语翻译",
        "channelDesc": "能翻译所有英语字，词，文章，论文等。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/e5e9baa528b945e6b2e8a1c3dda5775b~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=DtNyNMVpLHpXMTDr868xaq%2BRfKs%3D"
    },
    {
        "channelName": "奇幻小冒险",
        "channelDesc": "在一个遥远的国度里，隐藏着一个被大自然宠爱的村庄。这里的每一天都充满了新的冒险和温馨的故事。而你的旅程，就从这里开始……",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/2f36541186224f47863996fd44f81f9a~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=ezd4tmzPBVdE%2FKRGZnRHiGYUmuc%3D"
    },
    {
        "channelName": "文生视频✨提示专家",
        "channelDesc": "全面支持可灵、清影、海螺、通义、Vidu、筑梦、Sora、即梦等文生视频大模型，一帧一画皆是惊喜！👀 让灵感瞬间成影！",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/f65a8eee94384a41a6cd03f0ca06149f~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=U4jDFQbyYIgN%2Fy8tFSzCq29wm0U%3D"
    },
    {
        "channelName": "合成新元素",
        "channelDesc": "通过对话的方式玩一个类似「涂鸦上帝」的元素合成游戏。初始元素是 💧 水、🔥 火、🌬️ 风、🌍 土，你可以通过不断的自由组合，来随机生成新的物质。试试看谁能把「🥽 Vision Pro」生成出来？",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8a954c89f3104df3b404087f050e5108~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=TxPNdBsD%2Bc0Eb0yenxSmC5hgT2I%3D"
    },
    {
        "channelName": "认真看一百本书",
        "channelDesc": "一款专门为阅读爱好者设计的AI读书工具。只需输入书名，AI便能迅速整理出书籍的核心内容与相关背景资料，并以简洁明了的卡片形式展示，使阅读和学习变得更加高效便捷。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/59ba56af5a8d4bbcb51f76247a40c754~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=YvhkYVJ93ndlW%2FJsZmOc1SuYHAE%3D"
    },
    {
        "channelName": "漫画生成：橘猫漫画家",
        "channelDesc": "一个能够一键帮你制作猫咪漫画的bot，欢迎来到“橘猫漫画家”，您的口袋里的漫画工作室！每一个关于橘猫的想法都能变成生动的漫画。记住，也许我们每个人都是一只橘猫。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/8d2b7705ea1f458e8290f618e02f580d~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=IZkhM%2FvHrr2gpkF%2BPQO5ea9dYuY%3D"
    },
    {
        "channelName": "专业英语翻译",
        "channelDesc": "能翻译所有英语字，词，文章，论文等。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/e5e9baa528b945e6b2e8a1c3dda5775b~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=DtNyNMVpLHpXMTDr868xaq%2BRfKs%3D"
    },
    {
        "channelName": "万能英语助手",
        "channelDesc": "擅长：单词记忆和学习、口语对话训练、英语知识讲解、翻译。",
        "icon": "https://p26-flow-product-sign.byteimg.com/tos-cn-i-13w3uml6bg/4792a3611e0c47d2b62dcf6eeafe16e5~tplv-13w3uml6bg-resize:128:128.image?rk3s=2e2596fd&x-expires=1731712214&x-signature=0FwgEHo%2BZ3wG8mQta6SvigGeJpg%3D"
    }]
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
        query: { 'roleId': item.id }
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
