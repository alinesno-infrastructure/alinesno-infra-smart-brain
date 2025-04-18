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
          <el-col v-for="(item, index) in globalConfigs" :span="8">
            <router-link :to="item.link">
              <div :key="index" class="config-item" >
                <div class="config-icon">
                  <i :class="item.icon" />
                </div>
                <div class="config-info">
                  <span class="config-name">{{ item.configName }}</span>
                  <span class="config-desc">{{ item.configDesc }}</span>
                  <!-- <el-link :href="item.link" target="_blank" class="config-link">进入配置</el-link> -->
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
          <el-col v-for="(item, index) in modelConfigs" :span="8">
            <router-link :to="item.link">
              <div :key="index" class="config-item">
                <div class="config-icon">
                  <i :class="item.icon" />
                </div>
                <div class="config-info">
                  <span class="config-name">{{ item.configName }}</span>
                  <span class="config-desc">{{ item.configDesc }}</span>
                  <!-- <el-link :href="item.link" target="_blank" class="config-link">进入配置</el-link> -->
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
  {
    id: 1,
    configName: "运行监控查看",
    configDesc: "系统可能会集成多个厂家的大模型以满足不同的业务需求" , 
    icon: "fa-solid fa-tv",
    link: "/monitor/smart/assistant/analyse/index"
  }
];

const modelConfigs = [
  {
    id: 3,
    configName: "模板配置",
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
  }
];
</script>

<style lang="scss" scoped>
.global-config-container {
  margin: auto;
  width: 90%;
  max-width: 1500px;

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
      font-size: 15px;
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
        color: #3b5998;
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
          color: #007bff;
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