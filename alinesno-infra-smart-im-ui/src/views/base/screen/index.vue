<template>

  <div class="app-container tpl-app">
    <div class="search-container-panel">
      <el-row>
        <el-col :span="24">
          <div class="feature-header-xqbyQk feature-team-box">
            <div style="gap: 12px;">
              <h1 style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                场景市场
              </h1>
            </div>
            <div class="search-container-weDuEn">
              <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索场景" :suffix-icon="Search" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="channel-container-panel" style="margin-top:30px">
      <el-row>

        <el-col :span="6" v-for="(item, index) in screenList" :key="index" style="padding:8px;">
            <div class="screen-card-container" @click="enterScreen(item)">
              <article class="screen-card">
                  <div class="screen-image-container">
                      <img :src="item.background_image_url" class="screen-card-image">
                  </div>
                  <div class="screen-card-content">
                      <div class="screen-header">
                          <span class="screen-title">{{ item.title }}</span>
                          <div class="screen-tag">长文本</div>
                      </div>
                      <div class="screen-author-info">
                          <img src="http://data.linesno.com/switch_header.png" alt="Author Avatar" class="screen-avatar">
                          <span class="screen-name">罗小东</span>
                          <span class="screen-username">@Easton</span>
                      </div>
                      <div class="screen-description">
                        {{ item.description }}
                      </div>
                      <div class="screen-footer">
                          <div class="screen-price">免费</div>
                          <div class="screen-stats">
                              <span>{{ item.usage_count }}</span>
                              <span>使用</span>
                          </div>
                      </div>
                  </div>
              </article>
          </div>
        </el-col>
      </el-row>
    </div>

  </div>

</template>

<script setup>
const router = useRouter();

const screenList = ref([
    {
        "title": "毕业论文撰写与指导",
        "author": "李文博远",
        "description": "提供从选题到定稿的全程指导，帮助学生顺利完成毕业论文。",
        "usage_count": 542,
        "background_image_url": "http://data.linesno.com/screen_icons/1.png"
    },
    {
        "title": "政府招标标书编制",
        "author": "陈思明",
        "description": "针对政府招标项目，提供专业的标书撰写服务，提高中标率。",
        "usage_count": 328,
        "background_image_url": "http://data.linesno.com/screen_icons/2.png"
    },
    {
        "title": "企业年度报告设计",
        "author": "王海燕",
        "description": "为大中型企业定制年度财务与业务发展报告，展示公司成就。",
        "usage_count": 412,
        "background_image_url": "http://data.linesno.com/screen_icons/3.png"
    },
    {
        "title": "创业公司商业计划书",
        "author": "张伟",
        "description": "为初创企业提供全面的商业计划书撰写支持，助力融资成功。",
        "usage_count": 297,
        "background_image_url": "http://data.linesno.com/screen_icons/4.png"
    },
    {
        "title": "市场研究与分析报告",
        "author": "赵静",
        "description": "根据客户需求，进行深入市场分析，提供详尽的调研报告。",
        "usage_count": 356,
        "background_image_url": "http://data.linesno.com/screen_icons/5.png"
    },
    {
        "title": "法律文件起草与审核",
        "author": "刘强",
        "description": "专业律师团队提供的法律文书编写服务，确保每一份文件的合法性。",
        "usage_count": 189,
        "background_image_url": "http://data.linesno.com/screen_icons/6.png"
    },
    {
        "title": "科研项目申请材料",
        "author": "周敏",
        "description": "协助科研人员准备项目申请书，提升申请成功率。",
        "usage_count": 245,
        "background_image_url": "http://data.linesno.com/screen_icons/7.png"
    },
    {
        "title": "个人简历优化服务",
        "author": "吴晓东",
        "description": "提供个性化的简历设计与优化建议，助您脱颖而出。",
        "usage_count": 165,
        "background_image_url": "http://data.linesno.com/screen_icons/3.png"
    },
    {
        "title": "营销策略制定",
        "author": "郑晓琳",
        "description": "帮助企业制定有效的市场营销策略，扩大品牌影响力。",
        "usage_count": 302,
        "background_image_url": "http://data.linesno.com/screen_icons/7.png"
    },
    {
        "title": "产品说明书编写",
        "author": "黄小明",
        "description": "为企业产品提供详细、清晰的说明书编写服务。",
        "usage_count": 221,
        "background_image_url": "http://data.linesno.com/screen_icons/8.png"
    },
    {
        "title": "合同审查与谈判",
        "author": "李华",
        "description": "提供专业的合同审查及谈判支持，保障合作双方权益。",
        "usage_count": 198,
        "background_image_url": "http://data.linesno.com/screen_icons/1.png"
    },
    {
        "title": "网站内容创作",
        "author": "孙莉",
        "description": "为网站提供高质量的文章写作与编辑服务，增加用户粘性。",
        "usage_count": 403,
        "background_image_url": "http://data.linesno.com/screen_icons/3.png"
    },
    {
        "title": "社交媒体管理",
        "author": "朱丽叶",
        "description": "提供社交媒体账户的日常管理和内容更新服务。",
        "usage_count": 387,
        "background_image_url": "http://data.linesno.com/screen_icons/9.png"
    },
]) 

/** 进入长文本编辑界面 */
function enterScreen(item) {
    // 跳转至详情页
    router.push({
        path: '/screen/longText',
        query: { 'screenId': item.usage_count}
    })
}

</script>

<style lang="scss" scoped>
.screen-card-container {
    display: flex;
    flex-grow: 1;
    border-radius: 8px;
}

.screen-card {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    overflow: hidden;
    padding: 12px 12px 16px;
    border: 1px solid rgba(6, 7, 9, 0.1) ;
    border-radius: 8px;
    background-color: #fff ; 
    cursor: pointer;
    transition: box-shadow 0.3s;

    &:hover {
        box-shadow: var(--coz-shadow-large);
    }

    .screen-image-container {
        position: relative;
        width: 100%;
        height: 140px;
        border-radius: 4px;
        overflow: hidden;

        .screen-card-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
        }
    }

    .screen-card-content {
        margin-top: 8px;
        padding: 0 4px;
        flex-grow: 1;
        display: flex;
        flex-direction: column;

        .screen-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 8px 0px;

            .screen-title {
                font-weight: 500;
                font-size: 16px;
                line-height: 22px;
                color: var(--coz-fg-primary);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .screen-tag {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                padding: 2px 4px;
                border-radius: 4px;
                background-color: var(--coz-tag-primary);
                color: var(--coz-fg-primary);
                font-weight: 500;
                font-size: 12px;
                line-height: 16px;
                min-height: 20px;
                white-space: nowrap;
                user-select: none;

                svg {
                    width: 1em;
                    height: 1em;
                    fill: currentColor;
                }

                span {
                    margin-left: 2px;
                }
            }
        }

        .screen-author-info {
            display: flex;
            align-items: center;
            gap: 4px;
            margin-top: 4px;

            .screen-avatar {
                width: 14px;
                height: 14px;
                border-radius: 50%;
            }

            .screen-name, .screen-username {
                font-weight: 500;
                font-size: 12px;
                line-height: 16px;
                color: var(--coz-fg-primary);
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
        }

        .screen-description {
            margin-top: 8px;
            margin-bottom: 8px;
            font-size: 14px;
            line-height: 20px;
            color: var(--coz-fg-secondary);
            display: -webkit-box;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .screen-footer {
            margin-top: 4px;
            display: flex;
            align-items: center;
            justify-content: space-between;

            .screen-price {
                font-weight: 500;
                font-size: 16px;
                line-height: 22px;
                color: var(--coz-fg-primary);
            }

            .screen-stats {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                line-height: 16px;
                color: var(--coz-fg-secondary);
            }
        }
    }
}
</style>