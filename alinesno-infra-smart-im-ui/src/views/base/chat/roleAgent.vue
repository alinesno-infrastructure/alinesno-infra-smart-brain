<template>
  <div>
    <el-tabs :tab-position="'left'" class="demo-tabs" style="margin-top:20px;" v-model="currentTab">
      <el-tab-pane name="myTeam" label="我的团队">

          <el-row :gutter="20" style="padding-bottom:30px">
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

              <el-table v-loading="loading" :data="UserList" @selection-change="handleSelectionChange">
                <el-table-column type="index" width="50" align="center"/>
                <el-table-column label="角色名称" align="left" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <div style="font-size: 14px;font-weight: 500;display: flex;gap: 10px;align-items: center;">
                      <img :src="imagePathByPath(scope.row.roleAvatar)" style="width:40px;height:40px;border-radius: 50%"/>
                      {{ scope.row.roleName }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="角色描述" align="left" key="responsibilities" prop="responsibilities" v-if="columns[2].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <div>
                      {{ scope.row.responsibilities }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="所属团队" align="center" width="200" key="responsibilities" prop="responsibilities" v-if="columns[2].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <div>
                      AIP研发团队({{ scope.row.industryCatalog }})
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="添加频道" align="center" width="120"  key="storagePath" prop="storagePath" v-if="columns[5].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <el-button type="primary" text bg icon="Position" :loading="runChainAgentLoadding" @click="handleChainAgent(scope.row)">选择</el-button>
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

      </el-tab-pane>

      <el-tab-pane name="storeAgent" label="商店角色">
        <RoleAgentStore @handleChainAgent="handleChainAgent" />
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script setup name="User">

// import {getToken} from "@/utils/auth";

import RoleAgentStore from './roleAgentStore'

import {
  listUser,
  // delUser,
  // getUser,
  // updateUser,
  // addUser,
  getUserChainByChainId,
  // saveUserChainInfo,
  addChainAgent ,
  // runUserChainByUserId,
} from "@/api/base/im/user";

// import {
//   addChainAgent 
// } from '@/api/base/im/robot'

// import {
//   addUserChain , 
//   // updateUserChain,
// } from "@/api/base/im/user"

import {reactive} from "vue";

const props = defineProps({
  channelId: {
    type: [Number, String]
  }
});

const router = useRouter();
const {proxy} = getCurrentInstance();

const UserList = ref([]);
const runChainAgentLoadding = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);
// const title = ref("");

const chainOpen = ref(false);
const chainTitle = ref("");

const currentTab = ref('myTeam')

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
  listUser(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    UserList.value = res.rows;
    total.value = res.total;

    // 如果UserList为空，则tabs默认选择第二个tabs
    if(UserList.value.length == 0){
      currentTab.value = 'storeAgent' ; 
    }

  });

};

/** 显示图片 */
// function imagePath(row){
//   let roleAvatar = '1746435800232665090' ; 
//   if(row.roleAvatar){
//     roleAvatar = row.roleAvatar ; 
//   }
//   return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
// }

/** 搜索按钮操作 */
function handleQuery() {
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

  getUserChainByChainId(roleId).then(response => {
    chainForm.value = response.data;
    chainForm.value.roleId = roleId;

    chainOpen.value = true;
    chainTitle.value = "配置链路";
  });

}

/** 执行下一个节点机器人 */
function handleChainAgent(row){
  const roleId = row.id ; 
  const channelId = props.channelId ; 

  addChainAgent(roleId , channelId).then(response => {
    proxy.$modal.msgSuccess("添加成功.");
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
    width:30px;
    height:30px;
  }
}
</style>