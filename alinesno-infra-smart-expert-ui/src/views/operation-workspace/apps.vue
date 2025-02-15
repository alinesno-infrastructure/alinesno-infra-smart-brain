<template>
  <div>
    <el-row class="acp-dashboard-panel" :gutter="20">
      <el-col class="panel-col" :span="19">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-link"></i> 业务最新巡检</div>
          </div>
          <div class="panel-body" style="height: auto;position: relative;">
              <div class="direct-box">
                <ul>
                  <li class="box-item" v-for="item in advantages" :key="item">
                    <div class="dire-panel">
                      <div class="panel-title">
                        <i class="dire-panel-icon" :class="item.icon" :alt="item.name" />
                        {{ item.title }}
                      </div>
                      <div class="panel-describe">
                        {{ item.description }}
                      </div>
                      <!-- <div class="panel-tip">访问链接</div> -->
                    </div>
                  </li>
                </ul>
              </div>
          </div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-link"></i> 应用场景</div>
          </div>
          <div class="panel-body" style="height: auto;position: relative;">
            <div class="acp-app-list">
              <ul>
                <li class="app-items" style="width:100%" v-for="item in selectedUseScenarios" :key="item">
                  <div class="app-info">
                    <div class="app-item-title">
                        <i :class="item.icon" :alt="item.name" />
                        {{ item.name }}
                    </div>
                    <div class="app-item desc">{{ item.description }}</div>
                  </div>
                </li>
              </ul>
            </div>
          </div>

        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>

const advantages = [
  { 
    "icon": "fa-solid fa-desktop", 
    "title": "直观的仪表板", 
    "description": "提供清晰的项目概览和关键指标，便于快速决策和团队同步，提升整体效率。" 
  },
  { 
    "icon": "fa-solid fa-user-tag", 
    "title": "精细的专家管理", 
    "description": "优化专家资源分配，最大化专业能力利用，促进知识交流与协作，提高项目质量。" 
  },
  { 
    "icon": "fa-solid fa-code", 
    "title": "可编程的工作流", 
    "description": "支持自动化任务处理，提高效率与准确性，简化复杂操作，适应多样化需求。" 
  },
  { 
    "icon": "fa-solid fa-file-import", 
    "title": "智能的Prompt管理", 
    "description": "灵活编辑和组织Prompt模板，适应多变的应用场景，提升输出质量和用户满意度。" 
  },
  { 
    "icon": "fa-solid fa-server", 
    "title": "强大的监控能力", 
    "description": "实时监控系统API调用及性能，确保服务稳定和及时问题解决，增强系统可靠性。" 
  },
  { 
    "icon": "fa-solid fa-robot", 
    "title": "先进的Agent技术", 
    "description": "自动执行复杂业务逻辑，提升响应速度和灵活性，支持紧急事件响应，优化业务流程。" 
  }
];

const selectedUseScenarios = [
  { 
    "icon": "fa-solid fa-lightbulb", 
    "name": "创意写作辅助", 
    "description": "利用高级Prompt技术，帮助用户快速生成高质量的文章，并提供定制化的建议。" 
  },
  { 
    "icon": "fa-solid fa-briefcase", 
    "name": "商业报告生成", 
    "description": "通过定义特定的Prompt模板，自动化生成各种商业报告，确保数据准确性和专业性，提升决策效率。" 
  },
  { 
    "icon": "fa-solid fa-clipboard-list", 
    "name": "任务自动化", 
    "description": "借助Agent技术，自动执行日常任务，提高工作效率和准确性，减少人为错误。" 
  }
];

</script>