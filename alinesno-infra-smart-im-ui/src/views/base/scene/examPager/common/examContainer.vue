<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-manager">
        <!-- 考生列表-->
        <slot></slot>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import FunctionList from '../functionList'

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
// const questionBanks = ref([
//     {
//         code: "basic-knowledge",
//         label: "基础知识库",
//         icon: "fa-solid fa-graduation-cap"
//     },
//     {
//         code: "programming",
//         label: "编程题库",
//         icon: "fa-solid fa-code"
//     },
//     {
//         code: "interview",
//         label: "面试题库",
//         icon: "fa-solid fa-briefcase"
//     },
//     {
//         code: "certification",
//         label: "认证题库",
//         icon: "fa-solid fa-certificate"
//     },
//     {
//         code: "aptitude",
//         label: "能力倾向测试",
//         icon: "fa-solid fa-brain"
//     },
//     {
//         code: "language",
//         label: "语言题库",
//         icon: "fa-solid fa-language"
//     },
//     {
//         code: "math",
//         label: "数学题库",
//         icon: "fa-solid fa-calculator"
//     },
//     {
//         code: "general",
//         label: "综合题库",
//         icon: "fa-solid fa-question-circle"
//     }
// ])


// /** 查询角色列表 */
// const handleStoreRoleList = () => {
//   loading.value = true;
//   storeRoleList(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
//     loading.value = false;
//     StoreRoleList.value = res.rows;
//     total.value = res.total;
//   });
// };

/** 选择角色入频道 */
// const handleChainAgent = (row) => {
//   emit('handleChainAgent', row);
// }

// handleStoreRoleList();

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
