<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-manager">

        <el-row>
          <el-col :span="4">

            <div style="padding: 10px;font-weight: bold;font-size: 14px;">
              <i class="fa-solid fa-book-open-reader"></i> 题库类型
            </div>

            <!-- 题库列表 -->
            <div class="select-type-panel">
              <div v-for="(item, index) in questionBanks" :key="index" class="type-panel-box question-bank-box">
                <div class="type-title" style="margin-bottom:0px;">
                  <i :class="item.icon" /> {{ item.label }}
                </div> 
              </div>
            </div>

          </el-col>
          <el-col :span="20">

            <div style="margin-left:10px">
              <el-row :gutter="20" style="padding-bottom:30px;margin-top:20px;">
                <!--应用数据-->
                <el-col :span="24" :xs="24">
                  <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

                    <el-form-item label="角色名称" prop="roleName">
                      <el-input v-model="queryParams['condition[roleName|like]']" placeholder="请输入角色名称" clearable
                        style="width: 240px" @keyup.enter="handleQuery" />
                    </el-form-item>

                    <el-form-item label="状态" prop="roleName">
                      <el-segmented v-model="size" :options="sizeOptions" />
                    </el-form-item>

                    <el-form-item>
                      <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                      <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                    </el-form-item>
                  </el-form>

                  <el-table v-loading="loading" :data="StoreRoleList">
                    <el-table-column type="index" width="40" align="center" />
                    <el-table-column label="角色名称" align="left" key="roleName" prop="roleName" v-if="columns[1].visible"
                      :show-overflow-tooltip="true">
                      <template #default="scope">
                        <div style="display: flex;gap: 15px;align-items: center;">
                          <div style="font-size:1.8rem;color:#1d75b0">
                            <i class="fa-solid fa-file-word"></i>
                          </div>
                          <div style="display: flex;gap: 5px;flex-direction: column;">
                            <div style="font-size: 14px;font-weight: 500;display: flex;gap: 10px;align-items: center;">
                              {{ scope.row.roleName }}
                            </div>
                            <div style="font-size: 14px;display: flex;gap: 10px; color:#888; align-items: center;">
                              10题· 进行中·0人交卷
                            </div>
                          </div>

                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column label="起止时间" align="left" key="responsibilities" prop="responsibilities"
                      v-if="columns[2].visible" :show-overflow-tooltip="true">
                      <template #default="scope">
                        <div style="display: flex;flex-direction: row;gap: 10px;align-items: center;">
                          <el-button type="primary" text bg size="large">
                            2022-01-01 00:00:00
                          </el-button>-<el-button type="danger" text bg size="large" style="margin-left:0px;">
                            2022-01-01 00:00:00
                          </el-button>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column label="添加频道" align="center" width="440" key="storagePath" prop="storagePath"
                      v-if="columns[5].visible" :show-overflow-tooltip="true">
                      <template #default="scope">
                        <el-button type="primary" text bg :loading="runChainAgentLoading" size="large"
                          @click="handleShareMiniProgram(scope.row)">
                          <i class="fa-brands fa-weixin"></i> 小程序分享
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
                          @click="handleDelete(scope.row)">
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
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import FunctionList from './functionList'

import { storeRoleList } from '@/api/base/im/store'

const emit = defineEmits(['handleChainAgent']);

const router = useRouter();
const { proxy } = getCurrentInstance();
const dateRange = ref([]);

const StoreRoleList = ref([]);
const showSearch = ref(true);
const loading = ref(true);

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const runChainAgentLoadding = ref(false);

const size = ref('未考试')
const sizeOptions = ['未考试', '考试中', '已结束']

// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `角色名称`, visible: true },
  { key: 2, label: `角色描述`, visible: true },
  { key: 3, label: `所属领域`, visible: true },
  { key: 4, label: `角色级别`, visible: true },
  { key: 5, label: `安全存储路径`, visible: true },
  { key: 6, label: `应用目标`, visible: true },
  { key: 7, label: `创建时间`, visible: true },
  { key: 8, label: `编辑`, visible: true },

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    roleId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "角色名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    responsibilities: [{ required: true, message: "角色描述不能为空", trigger: "blur" }],
    domain: [{ required: true, message: "所属领域不能为空", trigger: "blur" }],
    roleLevel: [{ required: true, message: "角色级别不能为空", trigger: "blur" }],
    storagePath: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    target: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
  },
  chainForm: {
    roleId: undefined,
  },
  chainRules: {
    chainName: [{ required: true, message: "链路名称不能为空", trigger: "blur" }],
    elData: [{ required: true, message: "链路流程不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules, chainForm, chainRules } = toRefs(data);

// 模拟题库
const questionBanks = ref([
    {
        code: "basic-knowledge",
        label: "基础知识库",
        icon: "fa-solid fa-graduation-cap"
    },
    {
        code: "programming",
        label: "编程题库",
        icon: "fa-solid fa-code"
    },
    {
        code: "interview",
        label: "面试题库",
        icon: "fa-solid fa-briefcase"
    },
    {
        code: "certification",
        label: "认证题库",
        icon: "fa-solid fa-certificate"
    },
    {
        code: "aptitude",
        label: "能力倾向测试",
        icon: "fa-solid fa-brain"
    },
    {
        code: "language",
        label: "语言题库",
        icon: "fa-solid fa-language"
    },
    {
        code: "math",
        label: "数学题库",
        icon: "fa-solid fa-calculator"
    },
    {
        code: "general",
        label: "综合题库",
        icon: "fa-solid fa-question-circle"
    }
])


/** 查询角色列表 */
const handleStoreRoleList = () => {
  loading.value = true;
  storeRoleList(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    StoreRoleList.value = res.rows;
    total.value = res.total;
  });
};

/** 选择角色入频道 */
const handleChainAgent = (row) => {
  emit('handleChainAgent', row);
}

handleStoreRoleList();

</script>

<style lang="scss" scoped>
.exam-pager-main {
  margin: 0px;
}

.select-type-panel{

  width: 100%;
  text-align: left;
  // margin-left: 10px;
  // padding: 10px;
  font-size: 14px;

  .question-bank-box {
    font-weight: normal;
    background: #fafafa;
    border-radius: 5px;
    cursor: pointer;
    padding-left: 10px;

    &:hover {
      background: #e5e5e5;
    }
  }

  .type-panel-box {
    margin-bottom:10px;

    .type-title {
      font-weight: normal;
    }
  }

  .type-title {
    text-align: left;
    padding: 10px 5px;
    font-weight: bold;
    margin-bottom: 5px;
  }

  .type-item-list {
    column-count: 2;

    .item{
      display: flex;
      align-items: center;
      cursor: pointer;
      gap: 5px;
      padding: 6px;
      background: #f5f5f5;
      margin-bottom: 5px;
      border-radius: 5px;

      &:hover {
        background: #e5e5e5;
      }
    }
  }
}

</style>
