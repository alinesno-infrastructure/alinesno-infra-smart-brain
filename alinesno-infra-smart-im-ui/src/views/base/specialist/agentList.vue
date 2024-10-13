<template>
  <el-scrollbar height="100vh">

  <div class="container-main" style="padding-bottom:50px;background-color: #fff;">
      <div class="tpl-app app-main-content" v-loading="fullscreenLoading" style="max-width: 1240px;">

            <!-- <div class="header">
                自动化Agent列表
                <span style="font-size: 13px;color: #a5a5a5;">这里包含所有需要运营的Agent服务列表，你可以理解成一个Agent就是一个员工</span>
            </div>
            <div class="popular">
                <div class="popular-item"
                    v-for="(item , index) in productList"
                    :key="index"
                    >
                    <img :alt="item.name"
                        class="popular-item__banner"
                        style="width:90%"
                        :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index+5)+ '.png'" />
                    <h1 class="popular-item__title" style="font-size:15px">
                        {{ item.name }}
                    </h1>
                    <div class="popular-item__desc">
                        基础服务模块建设能力
                    </div>
                </div>
            </div>   -->

            <section class="section" v-for="(type, index) in productList" :key="index">
                <h2 class="section-title">
                    <i :class="type.banner" /> {{ type.name }}
                </h2>
                <div class="section-body">
                    <div class="app-item border-bottom" v-for="(item, i) in type.agents" :key="i">
                        <div class="app-item__icon wk wk-icon-user">
                          <!-- <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (i+1 + (index+1) * 1)+ '.png'" style="width:45px;height:45px;border-radius: 5px" /> -->
                          <img :src="imagePath(item)" style="width:45px;height:45px;border-radius: 5px"/>
                        </div>
                        <div class="content">
                            <h3 class="app-item__title">
                                {{ item.roleName }}
                                <a class="cf-service-nav-item-label" style="margin-left:10px"
                                    @click="saveCollectProduct(item)" :title="item.name">
                                    <el-button type="text" size="medium"><i class="fa-regular fa-star"></i> 收藏</el-button>
                                </a>

                            </h3>
                            <div class="app-item__desc">
                                业务基础服务规划和建设，描述待补充
                            </div>
                        </div>
                        <!--
                        <div class="app-item__control">
                            <a class="cf-service-nav-item-label" @click="openService(item)" :title="item.name">
                                <el-button type="text" size="medium" icon="el-icon-link">集成Agent</el-button>
                            </a>
                        </div>
                        -->
                    </div>
                </div>
            </section>

      </div>
  </div>
  </el-scrollbar>
</template>

<script setup name="ServiceList">

import { ElLoading } from 'element-plus'

const productList = ref([
        {
            "id": "1742481140373094402",
            "icon": null,
            "name": "人才培养团队",
            "orderNum": 0,
            "description": "人才培养团队",
            "agents": [
                {
                    "id": "1740825184617562114",
                    "roleAvatar": "1808349617330511874",
                    "roleName": "培训题目设计专家",
                    "industry": null,
                    "responsibilities": "培训题目设计专家，用于考核题目设计",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1744725316424617986",
                    "roleAvatar": "1808350127865389057",
                    "roleName": "学习内容推荐专家",
                    "industry": null,
                    "responsibilities": "学习内容推荐专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1742481225890758657",
            "icon": null,
            "name": "需求分析团队",
            "orderNum": 0,
            "description": "需求分析团队",
            "agents": [
                {
                    "id": "1740825311486869506",
                    "roleAvatar": "1808349647059738625",
                    "roleName": "需求编写分析师",
                    "industry": null,
                    "responsibilities": "需求拆分和编写",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1742372214781972481",
                    "roleAvatar": "1808349774163927042",
                    "roleName": "需求分析(需求文档分析专家)",
                    "industry": null,
                    "responsibilities": "需求分析_需求文档分析专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1742372307912298497",
                    "roleAvatar": "1808349808288784386",
                    "roleName": "需求分析(项目介绍分析专家)",
                    "industry": null,
                    "responsibilities": "需求分析_项目介绍分析专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1742372385792135169",
                    "roleAvatar": "1808349839242747906",
                    "roleName": "需求分析(项目功能分析专家)",
                    "industry": null,
                    "responsibilities": "需求分析_项目功能分析专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1742372831223025666",
                    "roleAvatar": "1808349886575468546",
                    "roleName": "需求分析(项目功能细化分析专家)",
                    "industry": null,
                    "responsibilities": "需求分析_项目功能细化分析专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1742372920440066049",
                    "roleAvatar": "1808350238808924161",
                    "roleName": "需求分析(项目非功能性需求)",
                    "industry": null,
                    "responsibilities": "需求分析_项目非功能性需求",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1747032557731115010",
                    "roleAvatar": "1808349453119315970",
                    "roleName": "测试插件角色",
                    "industry": null,
                    "responsibilities": "测试插件角色",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1742481263224258561",
            "icon": null,
            "name": "技术工程师团队",
            "orderNum": 0,
            "description": "工程师团队",
            "agents": [
                {
                    "id": "1745650056242315265",
                    "roleAvatar": "1808349957312405505",
                    "roleName": "测试工程师(接口生成)",
                    "industry": null,
                    "responsibilities": "测试工程师(接口生成)",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1745650199318413313",
                    "roleAvatar": "1808349978535583746",
                    "roleName": "测试工程师(用例配置)",
                    "industry": null,
                    "responsibilities": "测试工程师(用例配置)",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1745651485807919106",
                    "roleAvatar": "1808349555338698754",
                    "roleName": "SpringBoot接口编写工程师",
                    "industry": null,
                    "responsibilities": "根据API接口使用SpringBoot编写Controller层接口代码",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1742481317049761794",
            "icon": null,
            "name": "数据工程师团队",
            "orderNum": 0,
            "description": "数据工程师团队",
            "agents": [
                {
                    "id": "1746667490946031617",
                    "roleAvatar": "1808349483804844034",
                    "roleName": "数据库设计(实体设计专家)",
                    "industry": null,
                    "responsibilities": "数据库设计(实体设计专家)",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1746667628854747137",
                    "roleAvatar": "1808349526427361282",
                    "roleName": "数据库设计(实体模型专家)",
                    "industry": null,
                    "responsibilities": "数据库设计(实体模型专家)",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1742481439657656321",
            "icon": null,
            "name": "自动化运维团队",
            "orderNum": 0,
            "description": "自动化运维团队",
            "agents": [
                {
                    "id": "1745650550398435330",
                    "roleAvatar": "1808349924122877953",
                    "roleName": "K8S脚本生成工程师",
                    "industry": null,
                    "responsibilities": "生成k8s脚本和部署服务脚本",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1745651138595045377",
                    "roleAvatar": "1808349584682049538",
                    "roleName": "Ansible自动化工程师",
                    "industry": null,
                    "responsibilities": "实现Ansible自动化脚本处理",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1748622225583022081",
                    "roleAvatar": "1808349384961875969",
                    "roleName": "K8S问题排查专家",
                    "industry": null,
                    "responsibilities": "针对于K8S问题排查并给出合理建议",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1748914940729217025",
                    "roleAvatar": "1808349307635687426",
                    "roleName": "K8S报告生成Agent",
                    "industry": null,
                    "responsibilities": "生成K8S报告内容，与其它平台进行交互",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1742481532649570306",
            "icon": null,
            "name": "市场营销团队",
            "orderNum": 0,
            "description": "市场营销团队",
            "agents": [
                {
                    "id": "1744724806304976898",
                    "roleAvatar": "1808350214041559041",
                    "roleName": "软文编辑(目录大纲)",
                    "industry": null,
                    "responsibilities": "软文推广(目录大纲)编写",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1744724952044457986",
                    "roleAvatar": "1808350176838082561",
                    "roleName": "软文编辑(文案编写)",
                    "industry": null,
                    "responsibilities": "软文推广(文案编写)",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1747618454610849794",
                    "roleAvatar": "1808349417669058562",
                    "roleName": "软文编辑(集成内容平台)",
                    "industry": null,
                    "responsibilities": "集成CMS平台服务，集成内容平台",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1749971600547717121",
                    "roleAvatar": "1808076798197686273",
                    "roleName": "软文编辑(SEO专家)",
                    "industry": null,
                    "responsibilities": "用于文案的SEO描述",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1742481773473923074",
            "icon": null,
            "name": "产品服务团队",
            "orderNum": 0,
            "description": "产品服务团队",
            "agents": [
                {
                    "id": "1744726709210370050",
                    "roleAvatar": "1808350041093627906",
                    "roleName": "产品客户服务专家",
                    "industry": null,
                    "responsibilities": "产品客户服务专家，用于产品问答",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        },
        {
            "id": "1744655683264892929",
            "icon": null,
            "name": "技术架构师团队",
            "orderNum": 0,
            "description": "用于架构师相关事项管理",
            "agents": [
                {
                    "id": "1740825027536683010",
                    "roleAvatar": "1808349692358221826",
                    "roleName": "数据库设计专家",
                    "industry": null,
                    "responsibilities": "数据库设计专家，用于数据库设计",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1741645078294921218",
                    "roleAvatar": "1808349728060137473",
                    "roleName": "服务工程生成专家",
                    "industry": null,
                    "responsibilities": "用于服务工程生成专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1741646329840406530",
                    "roleAvatar": "1808349751040729090",
                    "roleName": "项目菜单生成专家",
                    "industry": null,
                    "responsibilities": "用于平台菜单生成专家",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1744726036628557825",
                    "roleAvatar": "1808350152938938369",
                    "roleName": "代码生成专家",
                    "industry": null,
                    "responsibilities": "根据数据库生成代码结构",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                },
                {
                    "id": "1744726462035841026",
                    "roleAvatar": "1808350003370057729",
                    "roleName": "开发编码规范专家",
                    "industry": null,
                    "responsibilities": "开发技术规范说明",
                    "skills": null,
                    "experience": null,
                    "roleLevel": "高级工程师",
                    "salaryRange": null,
                    "education": null,
                    "otherAttributes": null,
                    "chainId": null,
                    "promptId": null
                }
            ]
        }
    ]);
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
// handleGetProductList() ;


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
              margin-top: 15px;
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
</style>
