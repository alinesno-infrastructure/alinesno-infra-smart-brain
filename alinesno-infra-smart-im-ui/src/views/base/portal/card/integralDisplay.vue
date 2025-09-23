<template>
  <div class="integral-display">
    <!-- 顶部统计卡片区域 -->
    <el-row :gutter="24" class="statistic-row">
      <el-col v-for="(stat, index) in computedStatisticsData" :key="index" :span="8">
        <div class="statistic-card" :class="['card-0']">
          <div class="statistic-header">
            <el-icon class="stat-icon" :size="18">
              <component :is="stat.icon" />
            </el-icon>
            <span class="stat-title">{{ stat.title }}</span>
            <el-tooltip
              v-if="stat.tooltip"
              effect="light"
              :content="stat.tooltip"
              placement="top"
              :enter-delay="500"
            >
              <el-icon class="help-icon" :size="14">
                <QuestionFilled />
              </el-icon>
            </el-tooltip>
          </div>

          <div class="statistic-content">
            <el-statistic 
              :value="stat.value" 
              :formatter="stat.formatter || undefined"
              class="stat-value"
            />
          </div>

          <div class="statistic-footer">
            <div class="footer-item" :style="{ color: stat.footerColor || 'var(--el-text-color-secondary)' }">
              {{ stat.footerLabel }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 积分消耗明细区域 -->
    <div class="detail-section">
      <div class="section-header">
        <el-icon class="section-icon" :size="16">
          <List />
        </el-icon>
        <span class="section-title">积分消耗明细</span>
      </div>

      <div class="table-container">

              <el-table v-loading="loading" :data="tableData">
               <el-table-column type="index" width="50" align="center" />
               <el-table-column label="描述" align="left" key="description" prop="description"  :show-overflow-tooltip="false" />
               <el-table-column label="积分值" align="center" key="points" prop="points" width="130">
                  <template #default="scope">
                     <span :class="scope.row.points > 0 ? 'text-success' : 'text-danger'">
                        {{ scope.row.points > 0 ? '+' + scope.row.points : scope.row.points }}
                     </span>
                  </template>
               </el-table-column>
               <el-table-column label="积分类型" align="center" key="type" prop="type"  width="120">
                  <template #default="scope">
                     <el-button type="primary" text bg>{{ scope.row.typeDescription || scope.row.typeName || scope.row.type }}</el-button>
                  </template>
               </el-table-column>
               <el-table-column label="业务类型" align="center" key="bizType" prop="bizType"  width="150">
                  <template #default="scope">
                     <el-button type="primary" text bg>{{ scope.row.bizTypeDescription || scope.row.bizTypeName || scope.row.bizType }}</el-button>
                  </template>
               </el-table-column>
               <el-table-column label="消耗时间" align="center" key="addTime" prop="addTime"  width="230">
                  <template #default="scope">
                     <span>{{ parseTime(scope.row.addTime) }}</span>
                  </template>
               </el-table-column>

            </el-table>

      </div>

      <div class="pagination-container">
        <el-pagination 
          background 
          size="large"
          layout="prev, pager, next, ->, jumper, total" 
          :total="tableTotal"
          :page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { 
  Briefcase, Wallet, Clock, QuestionFilled, List
} from '@element-plus/icons-vue';

import {
    getPointsDetail,
} from "@/api/base/im/point"

// 首先定义getRemainingDays函数，确保在使用前完成初始化
const getRemainingDays = () => {
  const today = new Date();
  const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
  return Math.ceil((lastDayOfMonth - today) / (1000 * 60 * 60 * 24));
};

// 分页和查询相关变量
const orgIntegral = ref({
  packageName: 'AIP体验版',
  agentMaxConcurrency: 1,
  sceneMaxConcurrency: 1,
  points: 0,
  usedPoint:0 
})
const loading = ref(false);
const tableTotal = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const dateRange = ref([]);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  startTime: null,
  endTime: null
});

// 处理分页事件
const handleSizeChange = (val) => {
  console.log(`每页 ${val} 条`);
  queryParams.value.pageSize = val ;
};

const handleCurrentChange = (val) => {
  console.log(`当前页: ${val}`);
  pageNum.value = val ;
  fetchPointsDetail();
};

// 格式化积分显示（千分位分隔）
const formatPoint = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

// 使用计算属性替代ref，确保数据响应式更新
const computedStatisticsData = computed(() =>[
  {
    title: '账户类型',
    value: orgIntegral.value.packageName,
    icon: Briefcase,
    tooltip: '当前账户开通的服务套餐类型',
    footerLabel: `会话并发 ${orgIntegral.value.agentMaxConcurrency} | 场景并发 ${orgIntegral.value.sceneMaxConcurrency}`,
    footerColor: 'var(--el-text-color-regular)'
  },
  {
    title: '当前剩余积分',
    value: orgIntegral.value.points,
    icon: Wallet,
    tooltip: '账户中当前可使用的积分余额，可用于场景调用或对话交互',
    footerLabel: '积分长久有效期',
    footerColor: 'var(--el-color-warning)',
    formatter: formatPoint
  },
  {
    title: '累计消耗积分',
    value: orgIntegral.value.usedPoint,
    icon: Clock,
    tooltip: '以来账户累计消耗的积分总量',
    footerLabel: `账户累计消耗的积分总量`,
    footerColor: 'var(--el-color-success)',
    formatter: formatPoint
  }
]);

// 表格数据
const tableData = ref([]);

// 查询积分明细
const fetchPointsDetail = async () => {
  loading.value = true;
  try {
    // 设置当前分页参数
    queryParams.value.pageNum = pageNum.value;
    queryParams.value.pageSize = pageSize.value;
    
    // 调用POST接口
    getPointsDetail(queryParams.value).then(response => {
      tableData.value = response.rows || [];
      tableTotal.value = response.total || 0;
    })

  } catch (error) {
    console.error('获取积分明细失败:', error);
  } finally {
    loading.value = false;
  }
};

// 设置组织积分数据
const setOrgIntegral = (data) => {
  orgIntegral.value = data;
  fetchPointsDetail();
};

defineExpose({
  setOrgIntegral
})

</script>

<style lang="scss" scoped>
.integral-display {

  .statistic-row {
    margin-bottom: 24px;
  }

  .statistic-card {
    height: 100%;
    padding: 10px;
    border-radius: 12px;
    background-color: var(--el-bg-color);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 4px;
    }

    &.card-0::before {
      background: linear-gradient(90deg, #1d75b0, #1d75b0);
    }
    &.card-1::before {
      background: linear-gradient(90deg, #67c23a, #85ce61);
    }
    &.card-2::before {
      background: linear-gradient(90deg, #e6a23c, #f3c87a);
    }
  }

  .statistic-header {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    .stat-icon {
      margin-right: 8px;
      color: var(--el-color-primary);
    }

    .stat-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-regular);
    }

    .help-icon {
      margin-left: auto;
      color: var(--el-text-color-placeholder);
      cursor: help;
    }
  }

  .statistic-content {
    margin-bottom: 10px;

    .stat-value {
      --el-statistic-content-font-size: 32px;
      --el-statistic-content-font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .statistic-footer {
    font-size: 14px;
    padding-top: 5px;
  }

  .detail-section {
    background-color: var(--el-bg-color);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }

  .section-header {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 8px;

    .section-icon {
      margin-right: 8px;
      color: var(--el-color-primary);
    }

    .section-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-regular);
    }
  }

  .table-container {
    margin-bottom: 20px;
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .consume-point {
    color: var(--el-color-error);
    font-weight: 500;
  }
}

@media (max-width: 1200px) {
  .el-col-span-8 {
    flex: 0 0 50%;
    max-width: 50%;
  }
}

@media (max-width: 768px) {
  .el-col-span-8 {
    flex: 0 0 100%;
    max-width: 100%;
  }

  .integral-display {
    padding: 10px;
  }

  .statistic-card {
    padding: 16px;
  }

  .statistic-content .stat-value {
    --el-statistic-content-font-size: 24px;
  }
}
</style>
