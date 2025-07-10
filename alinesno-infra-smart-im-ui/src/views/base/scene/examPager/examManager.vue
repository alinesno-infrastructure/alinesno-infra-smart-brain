<template>
  <div class="exam-pagercontainer">
    <el-container style="height:calc(100vh - 40px );background-color: #fff;">
      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-manager">
        <div class="search-container-panel">
          <el-row>
            <el-col :span="24">
              <div class="feature-team-box">
                <div style="gap: 12px;">
                  <h1
                    style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 36px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                    考试管理
                  </h1>
                </div>
                <div class="search-container-weDuEn">
                  <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索考试"
                    :suffix-icon="Search" />
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div>
          <el-row :gutter="20" style="padding-bottom:30px;margin-top:20px;">
            <el-col :span="24" :xs="24">
              <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                <el-form-item label="考试名称" prop="examName">
                  <el-input v-model="queryParams['condition[examName|like]']" placeholder="请输入考试名称" clearable
                    style="width: 240px" @keyup.enter="handleQuery" />
                </el-form-item>

                <el-form-item label="状态" prop="examStatus">
                  <el-segmented v-model="queryParams.examStatus" :options="statusOptions" @change="handleQuery" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
              </el-form>

              <el-table v-loading="loading" :data="examList">
                <el-table-column type="index" width="40" align="center" />
                <el-table-column label="考试名称" align="left" prop="examName" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <div style="display: flex;gap: 15px;align-items: center;">
                      <div style="font-size:1.8rem;color:#1d75b0">
                        <i class="fa-solid fa-file-word"></i>
                      </div>
                      <div style="display: flex;gap: 5px;flex-direction: column;">
                        <div style="font-size: 14px;font-weight: 500;display: flex;gap: 10px;align-items: center;">
                          <span @click="openExam(scope.row)" style="cursor: pointer;">
                            {{ scope.row.examName }}
                          </span>
                          <el-tag v-if="scope.row.examStatus === 0" type="info">未开始</el-tag>
                          <el-tag v-else-if="scope.row.examStatus === 1" type="warning">进行中</el-tag>
                          <el-tag v-else type="success">已结束</el-tag>
                        </div>
                        <div style="font-size: 14px;display: flex;gap: 10px; color:#888; align-items: center;">
                          总分: {{ scope.row.totalScore }}分 · 及格分: {{ scope.row.examPassScore }}分 · 版本: v{{ scope.row.examVersion }}
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="起止时间" align="center" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <div style="display: flex;flex-direction: row;gap: 10px;align-items: center;">
                      <el-button type="primary" text bg size="large">
                        {{ scope.row.startTime }}
                      </el-button>
                      <span style="color: #999;">至</span>
                      <el-button type="danger" text bg size="large">
                        {{ scope.row.endTime }}
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="450">
                  <template #default="scope">
                    <el-button type="primary" text bg :loading="runChainAgentLoading" size="large"
                      @click="handleShareMiniProgram(scope.row)">
                      <i class="fa-solid fa-qrcode"></i> 试卷码
                    </el-button>
                    <el-button type="success" text bg :loading="runChainAgentLoading" size="large"
                      @click="handleMarkExamPaper(scope.row)">
                      <i class="fa-solid fa-check-square"></i> 阅卷
                    </el-button>
                    <el-button type="warning" text bg :loading="runChainAgentLoading" size="large"
                      @click="handleSettings(scope.row)">
                      <i class="fa-solid fa-cog"></i> 设置
                    </el-button>
                    <el-button type="danger" text bg :loading="runChainAgentLoading" size="large"
                      :disabled="scope.row.examStatus === 1" @click="handleDelete(scope.row)">
                      <i class="fa-solid fa-trash"></i> 删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList" />
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>

    <!-- 试卷码弹窗 -->
    <el-dialog v-model="examCodeDialogVisible" title="试卷码" width="500px" center>
      <div style="text-align: center;">
        <div style="margin-bottom: 20px; font-size: 16px; color: #666;">
         复制【{{ currentExamName }}】试卷码
        </div>
        <div style="font-size: 42px; font-weight: bold; letter-spacing: 8px; 
            color: #1d75b0; margin: 20px 0; padding: 15px; background: #f5f7fa;
            border-radius: 8px; display: inline-block;">
          {{ currentExamCode }}
        </div>
        <div style="margin-top: 30px;">
          <el-button type="primary" @click="copyExamCode" size="large" text bg style="width: 120px;">
            <i class="fa-solid fa-copy"></i> 复制试卷码
          </el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import FunctionList from './functionList'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import {
  queryExamList,
  deleteExam
} from "@/api/base/im/scene/examInfoManager";

// 控制弹窗显示
const examCodeDialogVisible = ref(false)

// 当前试卷码
const currentExamCode = ref('')
const currentExamName = ref('')

const emit = defineEmits(['handleChainAgent']);

const router = useRouter();
const route = useRoute();
const { proxy } = getCurrentInstance();
const dateRange = ref([]);

const sceneId = ref(route.query.sceneId)

const examList = ref([]);
const showSearch = ref(true);
const loading = ref(true);

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const runChainAgentLoading = ref(false);

const statusOptions = [
  { label: '全部', value: null },
  { label: '未开始', value: 0 },
  { label: '进行中', value: 1 },
  { label: '已结束', value: 2 }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    'condition[examName|like]': undefined,
    examStatus: null,
    sceneId: sceneId.value
  },
  rules: {
    examName: [{ required: true, message: "考试名称不能为空", trigger: "blur" }],
    startTime: [{ required: true, message: "开始时间不能为空", trigger: "blur" }],
    endTime: [{ required: true, message: "结束时间不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询考试列表 */
function getList() {
  loading.value = true;
  queryExamList(queryParams.value).then(res => {
    loading.value = false;
    examList.value = res.rows;
    total.value = res.total;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.examStatus = null;
  handleQuery();
}

// function handleShareMiniProgram(row) {
//   // 处理小程序分享逻辑
//   console.log('分享考试:', row);
// }

function handleMarkExamPaper(row) {
  // 检查考试状态 (2 = 已结束)
  // if (row.examStatus !== 2) {
  //   ElMessage.warning('考试尚未结束，请等待考试结束后再阅卷！')
  //   return
  // }

  // 这里添加实际的阅卷逻辑
  console.log('开始阅卷:', row)
  // 例如跳转到阅卷页面
  router.push({
    path: '/scene/examPager/examMakring',
    query: { examId: row.id , sceneId: sceneId.value }
  })

}

function handleSettings(row) {
  // 处理设置逻辑
  console.log('设置:', row);
}

// 打开考试试卷
function openExam(row) {
  console.log('row = ' + JSON.stringify(row))
  const routeData = router.resolve({
    path: '/scene/examPager/onlineExamPager/' + row.id 
  })
  window.open(routeData.href, '_blank')
}

function handleDelete(row) {
  if (row.examStatus === 1) {
    ElMessage.warning('考试进行中，无法删除！')
    return
  }

  ElMessageBox.confirm(
    `确定要删除考试 "${row.examName}" 吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
    }
  ).then(() => {
    // Call your delete API here
    deleteExam(row.id).then(() => {
      ElMessage.success('删除成功')
      getList() // Refresh the list
    }).catch(err => {
      ElMessage.error('删除失败: ' + err.message)
    })

    // For now, just show a success message
    ElMessage.success('模拟删除成功')
    getList()
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 处理分享小程序/显示试卷码
function handleShareMiniProgram(row) {
  // 实际项目中这里应该调用API获取试卷码
  // 这里模拟生成一个6位随机数作为试卷码
  currentExamCode.value = row.examCode ; // generateExamCode(row.id)
  currentExamName.value = row.examName
  examCodeDialogVisible.value = true
}



// 复制试卷码
function copyExamCode() {
  // 创建临时textarea元素
  const textarea = document.createElement('textarea')
  textarea.value = currentExamCode.value
  textarea.style.position = 'fixed'  // 防止页面滚动
  document.body.appendChild(textarea)
  textarea.select()
  
  try {
    // 执行复制命令
    const successful = document.execCommand('copy')
    if(successful) {
      ElMessage.success('试卷码已复制到剪贴板')
    } else {
      ElMessage.warning('复制失败，请手动复制')
    }
  } catch (err) {
    ElMessage.error('复制操作不支持')
  }
  
  // 移除临时元素
  document.body.removeChild(textarea)
}

getList();
</script>

<style lang="scss" scoped>
.exam-pagercontainer {
  height: 100%;

  .exam-pager-aside {
    background-color: #f5f7fa;
    border-right: 1px solid #e6e6e6;
  }

  .exam-pager-manager {
    padding: 20px;

    .search-container-panel {
      margin-bottom: 20px;

      .feature-team-box {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  }
}
</style>