<template>

  <div class="app-container tpl-app">

    <div class="channel-container-panel">
      <el-row>
        <el-col :span="6" v-for="(item , index) in demeChannel" :key="index" style="padding:8px;">
          <div class="semi-card-container">
            <div class="semi-space cart-head-continer" style="gap: 16px;">
              <div class="cart-head-content">
                <div class="cart-head-content">
                  <span class="semi-avatar semi-avatar-square">
                    <img
                      :src="item.icon"
                      class="">
                  </span>
                </div>
              </div>
              <div class="semi-space info-container" style="gap: 6px;">
                <span class="semi-typography card-title">
                  <span>BO播客生成计划</span>
                </span>
                <div class="semi-space container-center" style="gap: 6px;">
                  <div class="semi-image avatar-oDHtb3" style="width: 14px; height: 14px;">
                    <img
                      src="https://p3-passport.byteacctimg.com/img/user-avatar/7a7e80566f20a6944faaaa3a010fbff4~300x300.image"
                      class="semi-image-img" width="14" height="14">
                  </div>
                  <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 2px;">
                    <span class="semi-typography text" style="max-width: 150px;"><span>韦恩W</span></span>
                  </div><span class="semi-typography text-nickname">
                    <span>@wayne2012</span>
                  </span>
                </div>
                <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;">
                  <span>
                    BO播客生成计划是一款基于语音识别和自然语言处理技术的对话机器人，用户可以通过它快速创建个性化的播客节目，提高节目制作效率和质量。同时，它还提供了丰富的音频特效和语音转文字功能，让用户的播客节目更具吸引力和可读性。
                    现在微信公众号文章链接、抖音分享、小红书分享（VLOG、PLOG）都支持
                  </span>
                </p>
                <div class="semi-space card-tag-list" style="gap: 4px;"></div>
              </div>
            </div>
            <div class="semi-divider semi-divider-horizontal"></div>
            <div class="semi-space" style="height: 20px; width: 100%; gap: 8px;">
              <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children" style="display: inline-flex;">
                <div class="semi-space card-statics" style="gap: 8px;">
                  <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 1.2K</span>
                  <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                  <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                </div>
              </div>
              <div class="platform-container-YOpW3B">
                <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;">
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 频道聊天 -->
    <el-dialog v-model="dialogVisible" :title="chatTitle" width="80%" :before-close="handleClose">
      <iframe :src="roleChatUri" class="role-chat-iframe"></iframe>
    </el-dialog>

  </div>

</template>

<script setup>

import {
  allMyChannel,
} from '@/api/base/im/channel'
import { ref } from 'vue';

const router = useRouter();
const loading = ref(false)

const publicChatChannel = ref([]);
const recChatChannel = ref([]);

const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

const filterRules = ref([
  {
    "name": "场景", "codeValue": "initializr.admin.project.template.screen", "items": [
      { "code": "screen_code_1", "name": "旅游预订" },
      { "code": "screen_code_2", "name": "在线购物" },
      { "code": "screen_code_3", "name": "社交媒体" },
      { "code": "screen_code_4", "name": "健身健康" },
      { "code": "screen_code_5", "name": "在线视频" }
    ]
  },
  {
    "name": "类型", "codeValue": "initializr.admin.project.template.type", "items": [
      { "code": "type_code_1", "name": "移动应用" },
      { "code": "type_code_2", "name": "网页应用" },
      { "code": "type_code_4", "name": "社交平台" },
      { "code": "type_code_5", "name": "健身应用" }
    ]
  }
]);

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

const data = reactive({
  form: {},
  queryParams: {
    total: 100,
    pageNum: 1,
    pageSize: 8,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    deptId: undefined
  },
});

const { queryParams } = toRefs(data);

function enterChannel(item) {
  router.push({
    path: '/chat',
    query: { 'channel': item.id }
  })
}

/** 查询所所有我在参与的频道 */
function handleAllMyChannel() {
  loading.value = true;
  allMyChannel().then(response => {
    let items = response.data;

    publicChatChannel.value = items; // .filter(item => item.channelType === '9');
    // recChatChannel.value = items.filter(item => item.channelType === '3');

    loading.value = false;
  })
}

/** 与单个频道发信息 */
function handleChannelChat(item) {
  roleChatUri.value = "/channelChat?channel=" + item.id;
  chatTitle.value = item.channelName;
  dialogVisible.value = true;
}

handleAllMyChannel();

</script>


<style scoped lang="scss">
.cart-head-continer {
  gap: 16px;
  align-items: flex-start;
  flex-direction: row;
  display: inline-flex;
}

.semi-space.card-statics {
    font-size: 13px;
    color: #a5a5a5;
    display: inline-flex;
    line-height: 30px;
    padding-top: 3px;
}

.semi-card-container {
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  margin-bottom: 16px;
  padding: 20px 20px 16px;
  background: #FFF;
  border: 1px solid rgba(6, 7, 9, 0.1);
  border-radius: 8px;

  .semi-divider {
    margin-top: 20px;
    margin-bottom: 16px;
    border-bottom: 0.5px solid #f0f0f5;
    display: flex;
    width: 100%;
    box-sizing: border-box;
    color: var(--semi-color-text-0);
    margin: 1px 0 1px 0;
  }

  .card-desc {
    width: 100%;
    height: 60px;
    margin-top: 0px;
    font-size: 14px;
    font-weight: 400;
    line-height: 20px;
    color: rgba(56, 55, 67, 0.8);
    -webkit-box-orient: vertical;
    display: -webkit-box;
    overflow: hidden;
  }

  .info-container {
    overflow: hidden;
    width: 100%;
    height: 136px;
    align-items: flex-start;
    flex-direction: column;
    display: inline-flex;

    .container-center {
      flex-shrink: 0;
      max-width: 100%;
      height: 18px;
      display: inline-flex;

      .avatar-oDHtb3 {
        overflow: hidden;
        border-radius: 0;
        border-radius: 12px;
        display: inline-flex;
      }

      .semi-space-align-center {
        align-items: flex-start;
        flex-direction: column;
        display: inline-flex;

        .text {
          flex: 1;
          font-size: 13px;
          font-weight: 400;
          line-height: 18px;
          color: #a5a5a5;
        }

      }

      .text-nickname {
        flex: 1;
        font-size: 13px;
        font-weight: 400;
        line-height: 18px;
        color: #a5a5a5;
      }

    }

  }

  .card-title {
    height: 24px;
    font-size: 18px;
    font-weight: 600;
    line-height: 24px;
    color: #1C1D23;
  }

  .semi-avatar-square {
    border-radius: 8px;
    width: 64px;
    height: 64px;
    border: 1px solid #f0f0f5;
    background: #f0f0f5;
    align-items: center;
    display: inline-flex;
    justify-content: center;
    overflow: hidden;
    position: relative;
    text-align: center;
    vertical-align: middle;
    white-space: nowrap;

    img {
      display: block;
      height: 100%;
      object-fit: cover;
      width: 100%;
    }

  }
}
</style>
