<template>
    <div class="integral-display">
           <el-row :gutter="16">
    <el-col :span="8">
      <div class="statistic-card">
        <el-statistic :value="98500">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              Daily active users
              <el-tooltip
                effect="dark"
                content="Number of users who logged into the product in one day"
                placement="top"
              >
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <div class="footer-item">
            <span>than yesterday</span>
            <span class="green">
              24%
              <el-icon>
                <CaretTop />
              </el-icon>
            </span>
          </div>
        </div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="statistic-card">
        <el-statistic :value="693700">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              Monthly Active Users
              <el-tooltip
                effect="dark"
                content="Number of users who logged into the product in one month"
                placement="top"
              >
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <div class="footer-item">
            <span>month on month</span>
            <span class="red">
              12%
              <el-icon>
                <CaretBottom />
              </el-icon>
            </span>
          </div>
        </div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="statistic-card">
        <el-statistic :value="72000" title="New transactions today">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              New transactions today
            </div>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <div class="footer-item">
            <span>than yesterday</span>
            <span class="green">
              16%
              <el-icon>
                <CaretTop />
              </el-icon>
            </span>
          </div>
           
        </div>
      </div>
    </el-col>
  </el-row>

  <div style="
    padding: 10px;
    margin-bottom: 10px;
    display: flex;
    flex-direction: column;
">

<div style="
    font-size: 15px;
    border-radius: 5px;
    padding: 5px 10px;
    background: var(--el-dialog-bg-color);;
">
    积分消耗明细
</div>

     <div style="
    margin-bottom: 10px;
    margin-top: 10px;
">
  <el-table :data="tableData" style="width: 100%">
  <el-table-column type="index" label="序号" width="60"  align="center" />
    <el-table-column prop="taskName" label="任务名称"  />
    <el-table-column prop="taskType" label="类型"  />
    <el-table-column prop="point" label="消耗积分" width="180" align="center" />
    <el-table-column prop="addTime" label="时间" align="center" />
  </el-table>

    </div>

    <div style="
    margin-top: 20px;margin-bottom: 20px;display: flex;justify-content: flex-end;
">

   <el-pagination background layout="prev, pager, next" :total="1000" />
     </div>
  </div>
 
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';


const tableData = ref([
    {"taskName":"产品需求调研","taskType":"场景","point":20,"addTime":"2024-05-01 09:30"},
    {"taskName":"用户反馈收集","taskType":"对话","point":15,"addTime":"2024-05-02 14:15"},
    {"taskName":"功能原型设计","taskType":"场景","point":30,"addTime":"2024-05-03 10:00"},
    {"taskName":"市场竞品分析","taskType":"场景","point":25,"addTime":"2024-05-04 16:40"},
    {"taskName":"客户问题解答","taskType":"对话","point":10,"addTime":"2024-05-05 11:20"},
    {"taskName":"项目进度汇报","taskType":"对话","point":18,"addTime":"2024-05-06 15:50"},
    {"taskName":"测试用例编写","taskType":"场景","point":22,"addTime":"2024-05-07 09:10"},
    {"taskName":"团队方案讨论","taskType":"对话","point":12,"addTime":"2024-05-08 13:30"},
]);
 
</script>

<style lang="scss">
.integral-display {
:global(h2#card-usage ~ .example .example-showcase) {
  background-color: var(--el-fill-color) !important;
}

.el-statistic {
  --el-statistic-content-font-size: 28px;
}

.statistic-card {
  height: 100%;
  padding: 10px 20px;
  border-radius: 4px;
  background-color: var(--el-bg-color-overlay);
}

.statistic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-top: 16px;
}

.statistic-footer .footer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistic-footer .footer-item span:last-child {
  display: inline-flex;
  align-items: center;
  margin-left: 4px;
}

.green {
  color: var(--el-color-success);
}
.red {
  color: var(--el-color-error);
}
}
</style>