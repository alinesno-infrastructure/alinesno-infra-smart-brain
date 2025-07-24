<template>
  <div class="app-container global-config-container">
    <div class="global-section-title">
      全局配置管理
    </div>
    <div class="config-setion">
      <div class="section-title">
        全局配置
      </div>
      <div>
        <el-row :gutter="20">
          <el-col v-for="(item, index) in globalConfigs" :key="index" :span="8">
            <router-link :to="item.link">
              <div :key="index" class="config-item" >
                <div class="config-icon">
                  <i :class="item.icon" />
                </div>
                <div class="config-info">
                  <span class="config-name">
                    {{ item.configName }}  
                    <el-tag type="danger" size="small" v-if="item.status">集成中</el-tag>
                  </span>
                  <span class="config-desc">{{ item.configDesc }}</span>
                </div>
              </div>
            </router-link>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="config-setion">
      <div class="section-title">
        场景配置
      </div>
      <div>
        <el-row :gutter="20">
          <el-col v-for="(item, index) in modelConfigs" :span="8" :key="index">
            <router-link :to="item.link">
              <div :key="index" class="config-item">
                <div class="config-icon">
                  <i :class="item.icon" />
                </div>
                <div class="config-info">
                  <span class="config-name">
                    {{ item.configName }}  
                    <el-tag type="danger" size="small" v-if="item.status">集成中</el-tag>
                  </span>
                  <span class="config-desc">{{ item.configDesc }}</span>
                </div>
              </div>
            </router-link>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup name="Index">
const globalConfigs = [
  {
    id: 1,
    configName: "团队配置",
    configDesc: "填写公司名称来明确团队所属的组织，还可以进行其他与团队标识相关的设置。",
    icon: "fa-solid fa-users",
    link: "/smart/assistant/config/globalConfig"
  },
  {
    id: 1,
    configName: "大模型配置",
    configDesc: "系统可能会集成多个厂家的大模型以满足不同的业务需求" , 
    icon: "fa-solid fa-microchip",
    link: "/smart/assistant/llmModel/index"
  },
  {
    id: 2,
    configName: "密钥配置",
    configDesc: "此配置项允许你设置全局密码，这些密码会被应用到系统脚本的各个相关环节。",
    icon: "fa-solid fa-key",
    link: "/template/smart/assistant/secret/index"
  },
  // 数据资产配置
  {
    id: 9,
    configName: "数据资产配置",
    configDesc: "此配置项用于管理和优化数据资产相关的参数，包括数据存储、分类、权限及备份策略等。",
    icon: "fa-solid fa-database",
    status: 1,
    link: "" 
  },
  {
    id: 5,
    configName: "数据私有化",
    configDesc: "此配置项可用于设置数据存储的私有化方案，确保数据的安全性和隔离性，防止数据泄露和非法访问。",
    icon: "fa-solid fa-lock",
    status: 1,
    link: "" 
  }
];

const modelConfigs = [
  {
    id: 3,
    configName: "输出模板配置",
    configDesc: "系统中存在多种类型的文件需要遵循一定的格式和规范，通过此配置项" , 
    icon: "fa-solid fa-file",
    link: "/template/smart/assistant/template/index"
  },
  {
    id: 4,
    configName: "文档审核清单",
    configDesc: "此配置项允许你配置审核场景和对应的业务审核清单。",
    icon: "fa-solid fa-list-check",
    link: "/expert/smart/assistant/reviews/index"
  },
  {
    id: 5,
    configName: "审核规则库",
    configDesc: "为了保证审核工作你可以在这个配置项中对审核规则进行管理" , 
    icon: "fa-solid fa-ruler-combined",
    link: "/expert/smart/assistant/reviews/ruleList"
  },
  {
    id: 6,
    configName: "MCP服务配置",
    configDesc: "此配置项用于对MCP服务相关的参数和选项进行设置，以确保MCP服务的正常运行和优化使用。",
    icon: "fa-solid fa-server",
    status: 0 ,
    link: "/smart/assistant/config/mcpConfig"
  }, 
  // 文章模板配置
  {
    id: 7,
    configName: "文章模板配置",
    configDesc: "此配置项用于对文章模板相关的参数和选项进行设置，以确保文章模板的正常运行和优化使用。",
    icon: "fa-solid fa-file-lines",
    status: 0 ,
    link: "/smart/assistant/template/articleTemplate"
  },
  // 长文档板配置
  {
    id: 8,
    configName: "长文档模板配置",
    configDesc: "此配置项用于对长文档模板相关的参数和选项进行设置，以确保文章模板的正常运行和优化使用。",
    icon: "fa-solid fa-file-word",
    status: 0 ,
    link: "/smart/assistant/template/longTextTemplate"
  } , 
  // 通用智能体场景配置
  {
    id: 10,
    configName: "通用智能体配置",
    configDesc: "此配置项用于设置和管理通用智能体在不同应用场景下的行为模式、交互逻辑和功能参数。",
    icon: "fa-solid fa-robot",
    status: 0,
    link: "/smart/assistant/config/generalAgent"
  },
  // 智能公文配置
  {
      id: 9,
      configName: "智能文档配置",
      configDesc: "此配置项用于设置文档生成、格式校验和排版相关的参数，确保公文处理的规范性和高效性。",
      icon: "fa-solid fa-file-contract",
      status: 0,
      link: "/smart/assistant/config/officialDocument"
  },
];
</script>

<style lang="scss" scoped>
.global-config-container {
  margin: auto;
  width: 90%;
  max-width: 1450px;

  .global-section-title {
    font-size: 22px;
    padding-bottom: 20px;
    color: #14141f;
  }

 .config-setion {
    margin-bottom: 20px;

   .section-title {
      padding-top: 20px;
      margin-bottom: 20px;
      border-top: 1px solid rgb(244, 244, 248);
      font-size: 18px;
      font-weight: bold;
    }

   .config-item {
      display: flex;
      padding: 15px;
      border-radius: 8px;
      margin-top: 10px;
      margin-bottom: 10px;
      align-items: center;
      background-color: #fafafa;
      cursor: pointer;
      gap: 10px;

      &:hover {
        background-color: #f5f5f5;
      }

     .config-icon {
        font-size: 26px;
        background: rgba(175,175,207,.225);
        padding: 10px;
        width: 55px;
        height: 55px;
        color: #1d75b0;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }

     .config-info {
        display: flex;
        flex-direction: column;
        gap: 10px;
        width: calc(100% - 60px);

       .config-name {
          font-size: 15px;
        }

       .config-desc {
          font-size: 15px;
          line-height: 1.2rem;
          color: #888;
        }

       .config-link {
          font-size: 15px;
          color: #145799;
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }
}
</style>    