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
  { icon: 'fa-solid fa-desktop', title: '直观的仪表板', description: '便于用户快速了解项目的整体状态和关键指标。' },
  { icon: 'fa-solid fa-user-tag', title: '精细的专家管理', description: '确保每个专家能充分发挥其专业领域的能力。' },
  { icon: 'fa-solid fa-code', title: '可编程的工作流', description: '实现自动化任务处理和监控，提高效率与准确性。' },
  { icon: 'fa-solid fa-file-import', title: '智能的Prompt管理', description: '编辑和组织Prompt模板，以适应各种应用场景。' },
  { icon: 'fa-solid fa-server', title: '强大的监控能力', description: '实时监控系统API调用及性能' },
  { icon: 'fa-solid fa-robot', title: '先进的Agent技术', description: '利用Agent技术自动执行任务，支持复杂的业务逻辑' }
];

const selectedUseScenarios = [
  { icon: 'fa-solid fa-lightbulb', name: '创意写作辅助', description: '利用高级Prompt技术，帮助用户快速生成高质量的文章。' },
  { icon: 'fa-solid fa-briefcase', name: '商业报告生成', description: '通过定义特定的Prompt模板，自动化生成各种商业报告。' },
  { icon: 'fa-solid fa-clipboard-list', name: '任务自动化', description: '借助Agent技术，自动执行日常任务，如数据整理。' },
];

</script>