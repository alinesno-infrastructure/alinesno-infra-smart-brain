<template>

  <div >

    <div class="channel-container-panel"  v-loading="fullscreenLoading">

      <el-row>

        <el-col :span="6" v-for="(item, index) in publicChatChannel" :key="index" style="padding:8px;">
          <div class="semi-card-container" @click="enterChannel(item)">
            <div class="semi-space cart-head-continer" style="gap: 16px;">
              <div class="cart-head-content">
                <div class="cart-head-content">
                  <span class="semi-avatar semi-avatar-square">
                    <img :src="imagePath(item)" class="">
                  </span>
                </div>
              </div>
              <div class="semi-space info-container" style="gap: 6px;">
                <span class="semi-typography card-title">
                  <span>
                    {{ item.channelName }}
                  </span>
                </span>
                <div class="semi-space container-center" style="gap: 6px;">
                    <span class="semi-typography text" style="flex: 1;font-size: 13px;font-weight: 400;line-height: 18px;color: #a5a5a5;">
                      <i class="fa-solid fa-ribbon"></i>
                      {{ item.orgName }}
                    </span>
                </div>
                <p class="semi-typography card-desc">
                  <span>
                    {{ truncateString(item.channelDesc, 65) }}
                  </span>
                </p>
                <div class="semi-space card-tag-list" style="gap: 4px;"></div>
              </div>
            </div>
            <div class="semi-divider semi-divider-horizontal"></div>
            <div class="semi-space" style="width: 100%; gap: 8px;display: flex; justify-content: space-between;">
              <div class="semi-space semi-space-align-center card-statics semi-space-horizontal" style="display: flex;align-items: center;gap: 10px;">

                <span class="semi-typography text-h6" v-if="item.channelType == 'public'" ><i class="fa-solid fa-globe"></i> 公共频道</span>
                <span class="semi-typography text-h6" v-if="item.channelType == 'private'"><i class="fa-solid fa-lock"></i> 私人频道</span>
                <span class="semi-typography text-h6" v-if="item.channelType == 'org'" ><i class="fa-solid fa-truck-plane"></i> 组织频道</span>

                <div class="semi-space card-statics" style="gap: 8px;">
                  <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                  <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                </div>
              </div>
              
              <div class="platform-container-YOpW3B">
                <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;" v-if="item.knowledgeType">

                  <!-- 包含则显示-->
                  <img v-if="item.knowledgeType.includes('docx')" src="http://data.linesno.com/dataset_icons/docx.webp" class="knowledge-type-icon" />
                  <img v-if="item.knowledgeType.includes('pdf')" src="http://data.linesno.com/dataset_icons/pdf.webp" class="knowledge-type-icon" />
                </div>
              </div>

            </div>
          </div>
        </el-col>

      </el-row>

      <el-row v-if="publicChatChannel.length == 0 && !fullscreenLoading">
          <el-col :span="24">
              <el-empty 
                  :image-size="400"
                  :image="learnLogo"
                  description="当前未创建团队，可以进入智能体人才市场选择智能体，或者自行创建团队。" />
          </el-col>
      </el-row>

    </div>

  </div>

</template>

<script setup>

import {
    getWorkplaceItem
} from "@/api/base/im/workplace"

import { ref } from 'vue';

const fullscreenLoading = ref(true)
import learnLogo from '@/assets/icons/data_03.svg';
const router = useRouter();
const publicChatChannel = ref([]);

function enterChannel(item) {
  router.push({
    path: '/chat',
    query: { 'channel': item.id }
  })
}

const handleGetWorkplaceItem = (workplaceId , type) => {
  getWorkplaceItem(workplaceId , type).then(response => {
      publicChatChannel.value = response.data || [] ;
      fullscreenLoading.value = false
  })
} 

// 对外暴露的方法 
defineExpose({
    handleGetWorkplaceItem
})

</script>

<style lang="scss" scoped>
// .right-container {
//   position: relative; // 确保子元素可以绝对定位在容器内

//   .bot-banner-bg {
//     width: 100%;
//     height: 100%;
//     box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.12);
//     border-radius: 3px;
//   }

//   .banner-info {
//     display: flex;
//     align-items: center;
//     gap: 16px;
//     position: absolute;
//     bottom: 0;
//     left: 0;
//     width: 100%;
//     padding: 5px;
//     background: linear-gradient(0deg, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0) 100%);
//     flex-direction: row;

//     .avatar {
//       width: 64px;
//       height: 64px;
//       background: #f0f0f5;
//       border-radius: 8px;

//       img {
//         display: block;
//         height: 100%;
//         object-fit: cover;
//         width: 100%;
//         border-radius: 8px;
//       }
//     }

//     .info-text {
//       overflow: hidden;
//       flex: 1;
//       align-items: flex-start;
//       display: flex;
//       flex-direction: column;

//       .category {
//         font-size: 10px;
//         font-weight: 400;
//         line-height: 12px;
//         color: #FFF;
//         white-space: nowrap;
//         text-overflow: ellipsis;
//         margin: 0;
//       }

//       .title {
//         font-size: 18px;
//         font-weight: 600;
//         line-height: 24px;
//         color: #FFF;
//         margin: 0;
//       }

//       .author-info {
//         display: flex;
//         align-items: center;
//         gap: 4px;
//         flex-shrink: 0;
//         max-width: 100%;
//         height: 18px;
//         color: rgba(255, 255, 255, 0.39);

//         .semi-image {
//           width: 14px;
//           height: 14px;
//           overflow: hidden;
//           border-radius: 12px;

//           img {
//             width: 14px;
//             height: 14px;
//           }
//         }

//         .author-name,
//         .at-name {
//           font-size: 12px;
//           font-weight: 400;
//           line-height: 18px;
//           flex: 1;
//         }
//       }
//     }
//   }
// }

.card-container {
  margin-left: 10px;
  cursor: pointer;
  align-items: flex-start;
  width: 100%;
  height: 270px;
  padding: 16px 0;
  display: inline-flex;
  flex-direction: column;

  .card-header {
    height: 28px;

    img {
      height: 28px;
    }
  }

  h1 {
    line-clamp: 2;
    max-height: 112px;
    margin-bottom: 8px;
    font-size: 40px;
    font-weight: 700;
    line-height: 48px;
    color: #1D1C23;
  }

  p {
    height: 24px;
    margin-bottom: 28px;
    font-size: 16px;
    font-weight: 400;
    line-height: 24px;
    color: rgba(29, 28, 35, 0.6);
  }

}

.knowledge-type-icon{
  width:20px;
  height:20px;
  margin-top:10px
}
</style>