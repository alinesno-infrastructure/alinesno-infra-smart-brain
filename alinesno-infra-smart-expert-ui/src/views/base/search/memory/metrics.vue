<template>
  <div class="metrics-container">
    <!-- 时间范围选择器 -->
    <div class="time-filter">
      <el-radio-group v-model="timeRange" size="medium" @change="handleTimeRangeChange">
        <el-radio-button label="today">今天</el-radio-button>
        <el-radio-button label="7days">过去7天</el-radio-button>
        <el-radio-button label="1month">过去1个月</el-radio-button>
        <el-radio-button label="3months">过去3个月</el-radio-button>
        <el-radio-button label="12months">过去12个月</el-radio-button>
        <el-radio-button label="all">所有时间</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 指标卡片区域 -->
    <el-row :gutter="20" class="metric-cards">
      <el-col :span="8">
        <el-card shadow="never" class="metric-card">
          <div class="card-title">会话总数</div>
          <div class="card-value">{{ formatNumber(stats.sessions) }}</div>
          <div class="card-trend">
            <span :class="trendClass(stats.sessionsTrend)">{{ trendSymbol(stats.sessionsTrend) }} {{ Math.abs(stats.sessionsTrend) }}%</span> 较上周
          </div>
          <div class="card-chart" ref="sessionsChart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="never" class="metric-card">
          <div class="card-title">活跃用户数</div>
          <div class="card-value">{{ formatNumber(stats.activeUsers) }}</div>
          <div class="card-trend">
            <span :class="trendClass(stats.activeUsersTrend)">{{ trendSymbol(stats.activeUsersTrend) }} {{ Math.abs(stats.activeUsersTrend) }}%</span> 较上周
          </div>
          <div class="card-chart" ref="usersChart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="never" class="metric-card">
          <div class="card-title">平均会话互动数</div>
          <div class="card-value">{{ stats.avgInteractions.toFixed(1) }}</div>
          <div class="card-trend">
            <span :class="trendClass(stats.avgInteractionsTrend)">{{ trendSymbol(stats.avgInteractionsTrend) }} {{ Math.abs(stats.avgInteractionsTrend) }}%</span> 较上周
          </div>
          <div class="card-chart" ref="interactionsChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="metric-cards">
      <el-col :span="8">
        <el-card shadow="never" class="metric-card">
          <div class="card-title">Token输出速度</div>
          <div class="card-value">{{ stats.tokenSpeed }} <span class="unit">tokens/s</span></div>
          <div class="card-trend">
            <span :class="trendClass(stats.tokenSpeedTrend)">{{ trendSymbol(stats.tokenSpeedTrend) }} {{ stats.tokenSpeedTrend !== 0 ? Math.abs(stats.tokenSpeedTrend) + '%' : '持平' }}</span> 较上周
          </div>
          <div class="card-chart" ref="tokensChart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="never" class="metric-card">
          <div class="card-title">用户满意度</div>
          <div class="card-value">{{ (stats.satisfaction * 100).toFixed(1) }}%</div>
          <div class="card-trend">
            <span :class="trendClass(stats.satisfactionTrend)">{{ trendSymbol(stats.satisfactionTrend) }} {{ Math.abs(stats.satisfactionTrend) }}%</span> 较上周
          </div>
          <div class="card-chart" ref="satisfactionChart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="never" class="metric-card">
          <div class="card-title">费用消耗</div>
          <div class="card-value">${{ stats.cost.toFixed(2) }}</div>
          <div class="card-trend">
            <span :class="trendClass(stats.costTrend)">{{ trendSymbol(stats.costTrend) }} {{ Math.abs(stats.costTrend) }}%</span> 较上周
          </div>
          <div class="card-chart" ref="costChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 消息数据趋势图 -->
    <el-card shadow="never" class="main-chart">
      <div slot="header" class="chart-header">
        <span>消息数据趋势</span>
        <div class="chart-legend">
          <span><i class="incoming"></i> 接收消息</span>
          <span><i class="outgoing"></i> 发送消息</span>
        </div>
      </div>
      <div class="chart-container" ref="messageTrendChart"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const timeRange = ref('today')

// 模拟数据
const stats = ref({
  sessions: 2458,
  sessionsTrend: 12.5,
  activeUsers: 1326,
  activeUsersTrend: 8.2,
  avgInteractions: 5.8,
  avgInteractionsTrend: -1.2,
  tokenSpeed: 156,
  tokenSpeedTrend: 0,
  satisfaction: 0.945,
  satisfactionTrend: 3.7,
  cost: 328.75,
  costTrend: 15.3
})

// 图表引用
const sessionsChart = ref(null)
const usersChart = ref(null)
const interactionsChart = ref(null)
const tokensChart = ref(null)
const satisfactionChart = ref(null)
const costChart = ref(null)
const messageTrendChart = ref(null)

let chartInstances = []

// 格式化数字
const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
}

// 趋势样式
const trendClass = (trend) => {
  if (trend > 0) return 'trend-up'
  if (trend < 0) return 'trend-down'
  return 'trend-neutral'
}

// 趋势符号
const trendSymbol = (trend) => {
  if (trend > 0) return '↑'
  if (trend < 0) return '↓'
  return '→'
}

// 根据时间范围生成日期标签
const generateDateLabels = (range) => {
  const now = new Date()
  const labels = []
  
  switch(range) {
    case 'today':
      for (let i = 0; i < 24; i++) {
        labels.push(`${i}:00`)
      }
      break
    case '7days':
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now)
        date.setDate(date.getDate() - i)
        labels.push(date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }))
      }
      break
    case '1month':
      for (let i = 3; i >= 0; i--) {
        const date = new Date(now)
        date.setDate(date.getDate() - i * 7)
        labels.push(`第${4-i}周`)
      }
      break
    case '3months':
      for (let i = 2; i >= 0; i--) {
        const date = new Date(now)
        date.setMonth(date.getMonth() - i)
        labels.push(date.toLocaleDateString('zh-CN', { month: 'long' }))
      }
      break
    case '12months':
      for (let i = 11; i >= 0; i--) {
        const date = new Date(now)
        date.setMonth(date.getMonth() - i)
        labels.push(date.toLocaleDateString('zh-CN', { month: 'short' }))
      }
      break
    case 'all':
      labels.push('2023', '2024', '2025')
      break
  }
  
  return labels
}

// 生成数据
const generateData = (range, base = 100, variation = 30) => {
  const labels = generateDateLabels(range)
  return labels.map((_, i) => 
    Math.round(base + Math.random() * variation * 0.8 - variation * 0.4 + Math.sin(i / labels.length * Math.PI * 2) * variation * 0.6)
  )
}

// 初始化小型趋势图
function initMiniChart(dom, data, color) {
  const chart = echarts.init(dom)
  chartInstances.push(chart)
  
  const option = {
 
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter: params => {
        return `
          <div style="font-weight:bold;margin-bottom:5px">${params[0].axisValue}</div>
          ${params.map(item => `
            <div style="display:flex;align-items:center;margin:3px 0">
              <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${item.color};margin-right:5px"></span>
              ${item.seriesName}: <strong>${item.value}</strong>
            </div>
          `).join('')}
        `
      }
    },
   grid: {
      top: 10,
      left: 0,
      right: 0,
      bottom: 10
    },
    xAxis: { 
      show: false,
      type: 'category',
      data: generateDateLabels(timeRange.value)
    },
    yAxis: { 
      show: false,
      min: Math.max(0, Math.min(...data) - 10)
    },
    series: [{
      data: data,
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: {
        width: 2,
        color: color
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: `${color}60` },
          { offset: 1, color: `${color}10` }
        ])
      }
    }]
  }
  
  chart.setOption(option)
  return chart
}

// 初始化主趋势图
function initMainChart(dom) {
  const chart = echarts.init(dom)
  chartInstances.push(chart)
  
  const incomingData = generateData(timeRange.value, 100, 50)
  const outgoingData = generateData(timeRange.value, 120, 60)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter: params => {
        return `
          <div style="font-weight:bold;margin-bottom:5px">${params[0].axisValue}</div>
          ${params.map(item => `
            <div style="display:flex;align-items:center;margin:3px 0">
              <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${item.color};margin-right:5px"></span>
              ${item.seriesName}: <strong>${item.value}</strong>
            </div>
          `).join('')}
        `
      }
    },
    grid: {
      left: '0%',
      right: '0%',
      bottom: '0%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: generateDateLabels(timeRange.value),
    
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      axisLabel: {
        color: '#909399'
      },
      splitLine: {
        lineStyle: {
          color: '#f0f2f5'
        }
      }
    },
    series: [
      {
        name: '接收消息',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 2
        },
        showSymbol: false,
        color: '#3a4de9',
        areaStyle: {
          opacity: 0.1,
          color: '#3a4de9'
        },
        data: incomingData
      },
      {
        name: '发送消息',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 2
        },
        showSymbol: false,
        color: '#00d3ff',
        areaStyle: {
          opacity: 0.1,
          color: '#00d3ff'
        },
        data: outgoingData
      }
    ]
  }
  
  chart.setOption(option)
  return chart
}

// 处理时间范围变化
const handleTimeRangeChange = () => {
  refreshAllCharts()
}

// 刷新所有图表
const refreshAllCharts = () => {
  // 先销毁所有图表
  chartInstances.forEach(chart => chart.dispose())
  chartInstances = []
  
  // 重新初始化图表
  initMiniChart(sessionsChart.value, generateData(timeRange.value, 200, 50), '#3a4de9')
  initMiniChart(usersChart.value, generateData(timeRange.value, 100, 30), '#00d3ff')
  initMiniChart(interactionsChart.value, generateData(timeRange.value, 5, 2), '#f56c6c')
  initMiniChart(tokensChart.value, generateData(timeRange.value, 150, 20), '#67c23a')
  initMiniChart(satisfactionChart.value, generateData(timeRange.value, 90, 5), '#e6a23c')
  initMiniChart(costChart.value, generateData(timeRange.value, 300, 50), '#f56c6c')
  
  initMainChart(messageTrendChart.value)
}

onMounted(() => {
  refreshAllCharts()
})

onBeforeUnmount(() => {
  chartInstances.forEach(chart => chart.dispose())
  chartInstances = []
})
</script>

<style lang="scss" scoped>
.metrics-container {
  padding: 0px; 
}

.time-filter {
  margin-bottom: 20px; 
  margin-top: 10px;
  background: white;
  border-radius: 4px;
}

.metric-cards {
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.metric-card {
  position: relative;
  height: 160px;
  border-radius: 4px;
  
  .card-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
  }
  
  .card-value {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .card-trend {
    font-size: 12px;
    color: #909399;
    
    .trend-up {
      color: #67c23a;
    }
    
    .trend-down {
      color: #f56c6c;
    }
    
    .trend-neutral {
      color: #909399;
    }
  }
  
  .card-chart {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
  }
  
  .unit {
    font-size: 14px;
    font-weight: normal;
    color: #909399;
  }
}

.main-chart {
  margin-top: 20px;
  
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .chart-legend {
    font-size: 12px;
    color: #909399;
    
    span {
      margin-left: 15px;
      
      i {
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        margin-right: 5px;
        
        &.incoming {
          background-color: #3a4de9;
        }
        
        &.outgoing {
          background-color: #00d3ff;
        }
      }
    }
  }
  
  .chart-container {
    height: 280px;
  }
}

@media (max-width: 1200px) {
  .metric-card {
    margin-bottom: 20px;
  }
}
</style>