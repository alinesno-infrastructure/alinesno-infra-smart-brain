<template>
  <div class="app-container tpl-app">
    <el-row :gutter="20">

      <!--应用数据-->
      <el-col :span="24" :xs="20">

        <section>
          <h2 class="section-title" style="margin-top: 5px;margin-left: 10px;font-size: 20px;">
            <i class="type.banner" /> 智能体市场
            <span style="font-size: 13px;color: #777;margin-left:10px;">类似于人才市场，团队向人才市场选择合适的人进行复制.</span>
          </h2>
          <div class="section-body" v-loading="loading">

            <el-form :model="queryParams" style="padding-left:10px;" ref="queryRef" :inline="true" v-show="showSearch"
              label-width="68px">

              <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="queryParams['condition[roleName|like]']" placeholder="请输入角色名称" clearable
                  style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <div style="font-size: 20px;font-weight: 500;padding: 10px;" v-if="PushOrgRoleList">
              <i class="fa-solid fa-user-ninja"></i> 推送角色
              <span style="font-size: 13px;font-weight: normal;">
                特定组织推送过来的角色，不对外公开
              </span>
            </div>

            <el-row>
              <el-col :span="6" v-for="(item, i) in PushOrgRoleList" :key="i" style="padding:8px;">
                <div class="semi-card-container" @click="handleRoleChat(item)">
                  <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row-reverse;">

                    <div class="semi-space info-container" style="gap: 6px;height:100px">
                      <span class="semi-typography card-title">
                        <span>
                          {{ item.roleName }}
                        </span>
                      </span>
                      <div class="semi-space container-center" style="gap: 6px;">
                        <div class="semi-image avatar-oDHtb3" style="width: 14px; height: 14px;">
                          <img src="http://data.linesno.com/switch_header.png" class="semi-image-img" style="border-radius: 50%;">
                        </div>
                        <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 2px;">
                          <span class="semi-typography text" style="max-width: 150px;"><span>韦恩W</span></span>
                        </div>
                      </div>
                      <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;margin-bottom:0px">
                        <span>
                          {{ truncateString(item.responsibilities, 100) }}
                        </span>
                      </p>
                      <div class="semi-space card-tag-list" style="gap: 4px;"></div>
                    </div>

                    <div class="cart-head-content">
                      <div class="cart-head-content">
                        <span class="semi-avatar semi-avatar-square" style="border-radius: 50%;">
                          <img :src="imagePathByPath(item.roleAvatar)">
                        </span>
                      </div>
                    </div>

                  </div>

                  <div class="semi-divider semi-divider-horizontal"></div>

                  <div class="semi-space"
                    style="width: 100%;gap: 8px;display: flex;justify-content: space-between;align-items: center;">
                    <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children"
                      style="display: inline-flex;">
                      <div class="semi-space card-statics" style="gap: 8px;">
                        <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 1.2K</span>
                        <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                        <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                      </div>
                    </div>

                    <div class="platform-container-YOpW3B">
                      <div class="semi-space semi-space-align-center semi-space-horizontal"
                        style="gap: 4px;display: flex;color: #1d75b0;align-items: center;">
                        <span v-if="item.roleType == 'single_role'">
                          <i class="fa-solid fa-user-ninja"></i>
                        </span>
                        <span v-if="item.roleType == 'collaborative_role'">
                          <i class="fa-solid fa-user-tag"></i>
                        </span>
                        <span v-if="item.roleType == 'scenario_role'">
                          <i class="fa-solid fa-user-secret"></i>
                        </span>
                        <el-button type="primary" @click="handleEmployRole(item.id , true)" text bg><i
                            class="fa-solid fa-link"></i> 复制</el-button>
                      </div>
                    </div>

                  </div>

                </div>
              </el-col>
            </el-row>

            <div style="font-size: 20px;font-weight: 500;padding: 10px;">
              <i class="fa-solid fa-route"></i> 公共角色
              <span style="font-size: 13px;font-weight: normal;">
                对外所有组织都可以看到的角色
              </span>
            </div>

            <el-row>
              <el-col :span="6" v-for="(item, i) in RoleList" :key="i" style="padding:8px;">
                <div class="semi-card-container" @click="handleRoleChat(item)">
                  <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row-reverse;">

                    <div class="semi-space info-container" style="gap: 6px;height:100px">
                      <span class="semi-typography card-title">
                        <span>
                          {{ item.roleName }}
                        </span>
                      </span>
                      <div class="semi-space container-center" style="gap: 6px;">
                        <div class="semi-image avatar-oDHtb3" style="width: 14px; height: 14px;">
                          <img src="http://data.linesno.com/switch_header.png" class="semi-image-img" style="border-radius: 50%;">
                        </div>
                        <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 2px;">
                          <span class="semi-typography text" style="max-width: 150px;"><span>韦恩W</span></span>
                        </div>
                      </div>
                      <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;margin-bottom:0px">
                        <span>
                          {{ truncateString(item.responsibilities, 100) }}
                        </span>
                      </p>
                      <div class="semi-space card-tag-list" style="gap: 4px;"></div>
                    </div>

                    <div class="cart-head-content">
                      <div class="cart-head-content">
                        <span class="semi-avatar semi-avatar-square" style="border-radius: 50%;">
                          <img :src="imagePathByPath(item.roleAvatar)">
                        </span>
                      </div>
                    </div>

                  </div>

                  <div class="semi-divider semi-divider-horizontal"></div>

                  <div class="semi-space"
                    style="width: 100%;gap: 8px;display: flex;justify-content: space-between;align-items: center;">
                    <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children"
                      style="display: inline-flex;">
                      <div class="semi-space card-statics" style="gap: 8px;">
                        <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 1.2K</span>
                        <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                        <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                      </div>
                    </div>

                    <div class="platform-container-YOpW3B">
                      <div class="semi-space semi-space-align-center semi-space-horizontal"
                        style="gap: 4px;display: flex;color: #1d75b0;align-items: center;">
                        <span v-if="item.roleType == 'single_role'">
                          <i class="fa-solid fa-user-ninja"></i>
                        </span>
                        <span v-if="item.roleType == 'collaborative_role'">
                          <i class="fa-solid fa-user-tag"></i>
                        </span>
                        <span v-if="item.roleType == 'scenario_role'">
                          <i class="fa-solid fa-user-secret"></i>
                        </span>
                        <el-button type="primary" @click="handleEmployRole(item.id , false)" text bg>
                          <i class="fa-solid fa-clone"></i> &nbsp; 复制
                        </el-button>
                      </div>
                    </div>

                  </div>

                </div>
              </el-col>
            </el-row>

          </div>
        </section>


        <pagination style="background: #fafafa;" v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

  </div>
</template>

<script setup name="Role">

// import { getToken } from "@/utils/auth";

import {
  employRole,
  listMarketRole
} from "@/api/base/im/role";

// saveRoleChainInfo,

// import KnowledgeDataset from '@/views/smart/assistant/role/knowledge/parseDataset'
// import ExecuteScriptPanel from '@/views/smart/assistant/role/executeScriptPanel'

// import {
//   addRoleChain , 
//   updateRoleChain,
// } from "@/api/smart/assistant/chain"

// import PromptEditor from "./editor.vue"
// import { ElMessage } from 'element-plus'

import { ElLoading } from 'element-plus'
import { reactive } from "vue";

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_Role_sex } = proxy.useDict("sys_normal_disable", "sys_Role_sex");

const RoleList = ref([]);
const PushOrgRoleList = ref([]);

// const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
// const ids = ref([]);
// const single = ref(true);
// const multiple = ref(true);
const total = ref(0);
// const title = ref("");
const dateRange = ref([]);
// const imageUrl = ref([])

// const knowledgeDatasetRef = ref(null)
// const knowDrawerDialog = ref(false)
// const knowTitle = ref("")
// const knowRoleAvatar = ref("")

// 执行脚本
// const executeScriptRef = ref(null)
// const executeScriptDialog = ref(false)
// const executeScriptTitle = ref("")

// const chainOpen = ref(false);
// const chainTitle = ref("");
// const promptTitle = ref("");
// const currentPrompt = ref("");
// const currentPromptContent = ref([]);
// const promptOpen = ref(false);

// const deptName = ref("");
// const deptOptions = ref(undefined);

// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);

// /*** 应用导入参数 */
// const upload = reactive({
//   // 是否显示弹出层（应用导入）
//   open: false,
//   // 弹出层标题（应用导入）
//   title: "",
//   // 是否禁用上传
//   isUploading: false,
//   // 是否更新已经存在的应用数据
//   updateSupport: 0,
//   // 设置上传的请求头部
//   headers: { Authorization: "Bearer " + getToken() },
//   // 上传的地址
//   url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
//   // 显示地址
//   display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
// });
// // 列显隐信息
// const columns = ref([
//   { key: 0, label: `图标`, visible: true },
//   { key: 1, label: `角色名称`, visible: true },
//   { key: 2, label: `角色描述`, visible: true },
//   { key: 3, label: `所属领域`, visible: true },
//   { key: 4, label: `角色级别`, visible: true },
//   { key: 5, label: `安全存储路径`, visible: true },
//   { key: 6, label: `应用目标`, visible: true },
//   { key: 7, label: `创建时间`, visible: true },
//   { key: 8, label: `编辑`, visible: true },

// ]);

// const roleTypes = ref([
//   { key: 'single_role', name: '单角色', description: '自己单独完成一个聊天，可流式输出或者同步输出' },
//   { key: 'collaborative_role', name: '协作角色', description: '与其它角色协作才可以完成一个工作，可流式输出或者同步输出' },
//   { key: 'scenario_role', name: '场景角色', description: '与其它角色协作才可以完成一个工作，只能同步输出' },
//   { key: 'combined_role', name: '组合角色', description: '结合了协作与场景角色的特点，既可以在协作中发挥作用，也可以适应特定场景，支持流式输出和同步输出' }
// ]);

const data = reactive({
  form: {
    roleAvatar: null,
    scriptType: 'script'
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    industryCatalog: undefined
  },
  rules: {
    roleId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
    roleType: [{ required: true, message: "应用类型不能为空", trigger: "blur" }],
    scriptType: [{ required: true, message: "脚本类型不能为空", trigger: "blur" }],
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
    industryCatalog: [{ required: true, message: "角色类型不能为空", trigger: "blur" }],
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

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listMarketRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    RoleList.value = res.rows;

    PushOrgRoleList.value = res.pushRows; 

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


/** 重置操作表单 */
function reset() {
  form.value = {
    roleId: undefined,
    roleName: undefined,
    responsibilities: undefined,
    domain: undefined,
    roleLevel: undefined,
    storagePath: undefined,
    target: undefined,
  };
  proxy.resetForm("RoleRef");
};

/** 复制角色 */
function handleEmployRole(roleId , isPush) {

  const loading = ElLoading.service({
    lock: true,
    text: '复制手续办理中.....',
    background: 'rgba(0, 0, 0, 0.2)',
  })

  employRole(roleId , isPush).then(res => {
    loading.close();

    proxy.$modal.msgSuccess("角色复制成功，可以在频道中添加角色！");
    // router.push({ path: '/agentList'})

  }).catch(e => {
    loading.close();
  });
}

getList();

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width: 40px;
    height: 40px;
    border-radius: 8px;
  }
}

.editor-after-div {
  .el-upload {
    width: 56px;
    height: 56px;
    text-align: center;
    line-height: 56px;
  }

  .el-upload-list__item-thumbnail {
    width: 56px;
    height: 56px;
  }

  .el-upload-list__item {
    width: 56px;
    height: 56px;
  }
}
</style>