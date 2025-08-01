<template>
  <div class="app-container memory-display-container">
    <div class="memory-header"> 
      <div class="time-filter">
        <el-date-picker
          v-model="timeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="fetchMemoryData"
        />
      </div>
    </div>

    <div class="memory-content">
      <!-- 记忆概览 -->
      <div class="memory-overview">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <h3>实体数量</h3>
                <div class="stat-value">{{ stats.entities }}</div>
                <div class="stat-trend">较上期+12%</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <h3>事件数量</h3>
                <div class="stat-value">{{ stats.events }}</div>
                <div class="stat-trend">较上期+8%</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <h3>关系数量</h3>
                <div class="stat-value">{{ stats.relationships }}</div>
                <div class="stat-trend">较上期+15%</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <h3>记忆密度</h3>
                <div class="stat-value">{{ stats.density }}%</div>
                <el-progress :percentage="stats.density" :color="densityColor"></el-progress>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 记忆可视化 -->
      <div class="memory-visualization">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card shadow="hover">
              <div class="chart-container">
                <h3>记忆热力图</h3>
                <div ref="heatmapChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="chart-container">
                <h3>记忆类型分布</h3>
                <div ref="pieChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 记忆详情 -->
      <div class="memory-details">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="记忆实体" name="entities">
            <memory-entities :entities="memoryData.entities" @entity-selected="handleEntitySelected" />
          </el-tab-pane>
          <el-tab-pane label="记忆事件" name="events">
            <memory-events :events="memoryData.events" @event-selected="handleEventSelected" />
          </el-tab-pane>
          <el-tab-pane label="关系网络" name="relationships">
            <memory-relationships :relationships="memoryData.relationships" />
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 记忆图谱 -->
      <div class="memory-graph" v-if="selectedItem">
        <el-card shadow="hover">
          <div class="graph-container">
            <h3>记忆关系图: {{ selectedItem.name || selectedItem.title }}</h3>
            <div ref="graphChart" class="chart"></div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import * as echarts from 'echarts'
import MemoryEntities from './components/MemoryEntities.vue'
import MemoryEvents from './components/MemoryEvents.vue'
import MemoryRelationships from './components/MemoryRelationships.vue'

// 模拟数据
const mockMemoryData = {
  entities: [
    { id: 1, name: '用户_张三', type: '人物', frequency: 45, lastAccessed: '2023-05-15', tags: ['朋友', '同事'] },
    { id: 2, name: '项目_X', type: '项目', frequency: 32, lastAccessed: '2023-05-14', tags: ['工作', '重要'] },
    { id: 3, name: '咖啡厅', type: '地点', frequency: 28, lastAccessed: '2023-05-13', tags: ['日常', '最爱'] },
    { id: 4, name: '会议室_B', type: '地点', frequency: 18, lastAccessed: '2023-05-12', tags: ['工作'] },
    { id: 5, name: '王医生', type: '人物', frequency: 12, lastAccessed: '2023-05-10', tags: ['专家', '联系人'] }
  ],
  events: [
    { id: 1, title: '团队会议', type: '工作', date: '2023-05-15 10:00', duration: 60, participants: ['用户_张三', '王医生'], location: '会议室_B' },
    { id: 2, title: '咖啡休息', type: '个人', date: '2023-05-14 15:30', duration: 15, participants: ['用户_张三'], location: '咖啡厅' },
    { id: 3, title: '项目讨论', type: '工作', date: '2023-05-13 14:00', duration: 45, participants: ['用户_张三'], location: '会议室_B' },
    { id: 4, title: '午餐', type: '个人', date: '2023-05-12 12:30', duration: 30, participants: [], location: '咖啡厅' }
  ],
  relationships: [
    { from: '用户_张三', to: '项目_X', type: '参与项目', strength: 0.8 },
    { from: '用户_张三', to: '王医生', type: '咨询', strength: 0.6 },
    { from: '用户_张三', to: '咖啡厅', type: '常去', strength: 0.7 },
    { from: '项目_X', to: '会议室_B', type: '使用', strength: 0.5 }
  ]
}

const timeRange = ref([new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), new Date()])
const memoryData = ref({ entities: [], events: [], relationships: [] })
const stats = ref({
  entities: 0,
  events: 0,
  relationships: 0,
  density: 0
})
const activeTab = ref('entities')
const selectedItem = ref(null)
const heatmapChart = ref(null)
const pieChart = ref(null)
const graphChart = ref(null)

const densityColor = computed(() => {
  const density = stats.value.density
  if (density < 30) return '#f56c6c'
  if (density < 70) return '#e6a23c'
  return '#67c23a'
})

onMounted(() => {
  fetchMemoryData()
})

const fetchMemoryData = () => {
  // Simulate API call
  setTimeout(() => {
    memoryData.value = mockMemoryData
    updateStats()
    renderCharts()
  }, 500)
}

const updateStats = () => {
  stats.value = {
    entities: memoryData.value.entities.length,
    events: memoryData.value.events.length,
    relationships: memoryData.value.relationships.length,
    density: Math.min(100, Math.floor(memoryData.value.entities.length * 3 + memoryData.value.events.length * 2))
  }
}

const renderCharts = () => {
  renderHeatmap()
  renderPieChart()
  if (selectedItem.value) {
    renderGraph()
  }
}

const renderHeatmap = () => {
  const chart = echarts.init(heatmapChart.value)
  
  // Generate mock heatmap data
  const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  const hours = Array.from({ length: 24 }, (_, i) => i)
  const data = []
  
  // Generate random data for demonstration
  for (let i = 0; i < 7; i++) {
    for (let j = 0; j < 24; j++) {
      data.push([j, i, Math.floor(Math.random() * 10)])
    }
  }
  
  const option = {
    tooltip: {
      position: 'top',
      formatter: function (params) {
        return `Memory Activity: ${params.value[2]}<br>${days[params.value[1]]} ${params.value[0]}:00`
      }
    },
    grid: {
      left: '0%',
      top: '1%',
      right: '0%',
      bottom: '40',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: hours,
      splitArea: {
        show: true
      }
    },
    yAxis: {
      type: 'category',
      data: days,
      splitArea: {
        show: true
      }
    },
    visualMap: {
      min: 0,
      max: 10,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '0%',
      inRange: {
        color: ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4', '#313695']
      }
    },
    series: [{
      name: 'Memory Activity',
      type: 'heatmap',
      data: data,
      label: {
        show: false
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const renderPieChart = () => {
  const chart = echarts.init(pieChart.value)
  
  // Group entities by type
  const typeCounts = memoryData.value.entities.reduce((acc, entity) => {
    acc[entity.type] = (acc[entity.type] || 0) + 1
    return acc
  }, {})
  
  const data = Object.entries(typeCounts).map(([name, value]) => ({
    name,
    value
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: Object.keys(typeCounts)
    },
    series: [
      {
        name: 'Memory Types',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const renderGraph = () => {
  if (!selectedItem.value) return
  
  const chart = echarts.init(graphChart.value)
  
  // Prepare graph data based on selected item
  let nodes = []
  let links = []
  
  if (activeTab.value === 'entities') {
    // Find related entities and relationships
    const entity = selectedItem.value
    nodes.push({
      id: entity.name,
      name: entity.name,
      symbolSize: 30,
      category: entity.type,
      itemStyle: { color: getNodeColor(entity.type) }
    })
    
    // Find relationships involving this entity
    memoryData.value.relationships.forEach(rel => {
      if (rel.from === entity.name || rel.to === entity.name) {
        const otherEntity = rel.from === entity.name ? rel.to : rel.from
        
        // Add other entity if not already added
        if (!nodes.find(n => n.id === otherEntity)) {
          const entityData = memoryData.value.entities.find(e => e.name === otherEntity)
          nodes.push({
            id: otherEntity,
            name: otherEntity,
            symbolSize: 25,
            category: entityData?.type || 'unknown',
            itemStyle: { color: getNodeColor(entityData?.type || 'unknown') }
          })
        }
        
        links.push({
          source: rel.from,
          target: rel.to,
          name: rel.type,
          lineStyle: { width: rel.strength * 5 }
        })
      }
    })
  } else if (activeTab.value === 'events') {
    const event = selectedItem.value
    nodes.push({
      id: event.title,
      name: event.title,
      symbolSize: 30,
      category: 'event',
      itemStyle: { color: getNodeColor('event') }
    })
    
    // Add participants and location
    event.participants.forEach(participant => {
      nodes.push({
        id: participant,
        name: participant,
        symbolSize: 25,
        category: 'person',
        itemStyle: { color: getNodeColor('person') }
      })
      links.push({
        source: event.title,
        target: participant,
        name: 'participated'
      })
    })
    
    if (event.location) {
      nodes.push({
        id: event.location,
        name: event.location,
        symbolSize: 25,
        category: 'location',
        itemStyle: { color: getNodeColor('location') }
      })
      links.push({
        source: event.title,
        target: event.location,
        name: 'occurred_at'
      })
    }
  }
  
  const categories = [...new Set(nodes.map(node => node.category))].map(cat => ({
    name: cat
  }))
  
  const option = {
    title: {
      text: 'Memory Relationships',
      subtext: 'Interactive Graph',
      top: 'top',
      left: 'center'
    },
    tooltip: {},
    legend: [{
      data: categories.map(a => a.name),
      selectedMode: 'single',
      orient: 'vertical',
      right: 10,
      top: 'center'
    }],
    animationDuration: 1500,
    animationEasingUpdate: 'quinticInOut',
    series: [{
      name: 'Memory Graph',
      type: 'graph',
      layout: 'force',
      data: nodes,
      links: links,
      categories: categories,
      roam: true,
      label: {
        show: true,
        position: 'right'
      },
      force: {
        repulsion: 100,
        edgeLength: [50, 150]
      },
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 3
        }
      },
      lineStyle: {
        curveness: 0.2
      }
    }]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const getNodeColor = (type) => {
  const colors = {
    person: '#5470c6',
    location: '#91cc75',
    project: '#fac858',
    event: '#ee6666',
    unknown: '#73c0de'
  }
  return colors[type] || '#73c0de'
}

const handleEntitySelected = (entity) => {
  selectedItem.value = entity
  nextTick(() => {
    renderGraph()
  })
}

const handleEventSelected = (event) => {
  selectedItem.value = event
  nextTick(() => {
    renderGraph()
  })
}

watch(activeTab, (newTab) => {
  selectedItem.value = null
})
</script>

<style lang="scss" scoped>
.memory-display-container {
  padding: 0px;
  
  .memory-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: #333;
    }
  }
  
  .memory-content {
    .memory-overview {
      margin-bottom: 20px;
      
      .stat-card {
         text-align: center;
          display: flex;
          flex-direction: row;
          align-items: center;
          gap: 10px; 
        
        h3 {
          margin: 0px;
          font-size: 16px;
          color: #666;
        }
        
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          margin-bottom: 5px;
        }
        
        .stat-trend {
          font-size: 12px;
          color: #999;
        }
      }
    }
    
    .memory-visualization {
      margin-bottom: 20px;
      
      .chart-container {
        height: 200px;
        
        h3 {
          margin: 0 0 10px 0;
          font-size: 16px;
        }
        
        .chart {
          width: 100%;
          height: 100%;
        }
      }
    }
    
    .memory-details {
      margin-bottom: 20px;
    }
    
    .memory-graph {
      .graph-container {
        height: 500px;
        
        h3 {
          margin: 0 0 10px 0;
          font-size: 16px;
        }
        
        .chart {
          width: 100%;
          height: calc(100% - 30px);
        }
      }
    }
  }
}
</style>