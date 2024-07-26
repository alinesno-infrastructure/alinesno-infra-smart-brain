<template>
  <div>
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="queryParams['condition[roleName|like]']" placeholder="请输入角色名称" clearable style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="RoleList" @selection-change="handleSelectionChange">
          <el-table-column type="index" width="40" align="center"/>
          <el-table-column label="图标" align="center" width="60px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon">
                <img :src="'http://data.linesno.com/icons/circle/Delivery boy-' + ((scope.$index + 1)%5 + 1) + '.png'" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色名称" align="left" width="180" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #3b5998;">
                {{ scope.row.roleName }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                 {{ scope.row.roleLevel }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色描述" align="left" key="responsibilities" prop="responsibilities" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.responsibilities }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                会话次数: 12734 稳定率:98%
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属团队" align="center" width="150" key="responsibilities" prop="responsibilities" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                工程师团队
              </div>
            </template>
          </el-table-column>
          <el-table-column label="执行任务" align="center" width="110"  key="storagePath" prop="storagePath" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text bg icon="Position" :loading="runChainAgentLoadding" @click="handleChainAgent(scope.row)">执行</el-button>
            </template>
          </el-table-column>

        </el-table>
        <pagination
            v-show="total > 0"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
        />
      </el-col>
    </el-row>

  </div>
</template>

<script setup name="Role">
import {getToken} from "@/utils/auth";
import {
  listRole,
  delRole,
  getRole,
  updateRole,
  addRole,
  getRoleChainByChainId,
  saveRoleChainInfo,
  runRoleChainByRoleId,
} from "@/api/smart/assistant/role";

import {
  runChainAgent
} from '@/api/smart/assistant/robot'

import {
  addRoleChain , 
  updateRoleChain,
} from "@/api/smart/assistant/chain"

import {reactive} from "vue";

const props = defineProps({
  businessId: {
    type: [Number, String]
  }
});

const router = useRouter();
const {proxy} = getCurrentInstance();

const RoleList = ref([]);
const runChainAgentLoadding = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const chainOpen = ref(false);
const chainTitle = ref("");

// 列显隐信息
const columns = ref([
  {key: 0, label: `图标`, visible: true},
  {key: 1, label: `角色名称`, visible: true},
  {key: 2, label: `角色描述`, visible: true},
  {key: 3, label: `所属领域`, visible: true},
  {key: 4, label: `角色级别`, visible: true},
  {key: 5, label: `安全存储路径`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

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
    roleId: [{required: true, message: "应用编号不能为空", trigger: "blur"}],
    roleName: [{required: true, message: "角色名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "角色名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    responsibilities: [{required: true, message: "角色描述不能为空", trigger: "blur"}],
    domain: [{required: true, message: "所属领域不能为空", trigger: "blur"}],
    roleLevel: [{required: true, message: "角色级别不能为空", trigger: "blur"}],
    storagePath: [{required: true, message: "安全存储路径不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  },
  chainForm: {
    roleId: undefined,
  },
  chainRules: {
    chainName: [{required: true, message: "链路名称不能为空", trigger: "blur"}],
    elData: [{required: true, message: "链路流程不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules , chainForm , chainRules} = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    RoleList.value = res.rows;
    total.value = res.total;
  });
};


/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.roleName = undefined;
  queryParams.value.responsibilities = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 配置用户链路流程 */
function handleLangChain(row){
  const roleId = row.id || ids.value;

  getRoleChainByChainId(roleId).then(response => {
    chainForm.value = response.data;
    chainForm.value.roleId = roleId;

    chainOpen.value = true;
    chainTitle.value = "配置链路";
  });

}

/** 执行下一个节点机器人 */
function handleChainAgent(row){
  const roleId = row.id ; 
  const businessId = props.businessId ; 
  console.log('roleId = ' + roleId + ' , businessId = ' + businessId) ;

  runChainAgent(roleId , businessId).then(response => {
    proxy.$modal.msgSuccess("运行成功，请注意查收钉钉消息.");
  })

}

/** 执行条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

getList();
</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width:40px;
    height:40px;
  }
}
</style>