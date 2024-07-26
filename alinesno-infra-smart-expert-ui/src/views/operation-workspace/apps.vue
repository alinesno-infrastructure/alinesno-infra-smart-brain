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
                <li class="app-items" style="width:100%" v-for="item in selectedPermissionScenarios" :key="item">
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
  { icon: 'fa-solid fa-shield-alt', title: '系统安全性提升', description: '通过灵活的权限管理和细粒度的权限控制。' },
  { icon: 'fa-solid fa-chart-line', title: '精准的权限管理', description: '支持不同角色和岗位的个性化权限设置，确保用户只能访问其具备权限的功能和资源。' },
  { icon: 'fa-solid fa-clipboard-list', title: '全面的日志记录', description: '提供完善的日志记录功能，帮助追踪和审计系统的使用情况。' },
  { icon: 'fa-solid fa-chart-bar', title: '实时应用监控', description: '强大的应用监控功能可以实时监控系统的运行状态和性能指标，。' },
  { icon: 'fa-solid fa-cogs', title: '灵活的企业定制', description: '允许企业进行主题配置和企业配置，提升用户体验，增强企业形象。' },
  { icon: 'fa-solid fa-users-cog', title: '高效的权限管理', description: '帮助企业和机构高效管理系统中的权限，提升系统安全性和用户体验。' }
];

const selectedPermissionScenarios = [
  { icon: 'fa-solid fa-building', name: '集团权限管理', description: '用于管理集团内部各个子公司或部门的权限。' },
  { icon: 'fa-solid fa-cogs', name: '系统级权限控制', description: '用于管理系统级别的权限，包括系统功能的权限设置，确保系统安全和稳定运行。' },
  { icon: 'fa-solid fa-users-cog', name: '角色权限分配', description: '用于将权限按角色进行分配和管理，提高系统的安全性和管理效率。' },
];

</script>