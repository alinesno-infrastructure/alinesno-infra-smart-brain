<template>
  <div class="tpl-app">

    <el-page-header @back="goBack" class="review-page-header">
        <template #content>
            <span class="text-large font-600 mr-3"> 招投标书文档审核 </span>
        </template>
    </el-page-header>

    <el-row :gutter="20">

      <!--应用数据-->
      <el-col :span="24" :xs="20">

        <section>
          <h2 class="section-title" style="margin-top: 5px;margin-left: 10px;font-size: 18px;">
            <i class="type.banner" /> 文档审核规则库
            <span style="font-size: 13px;color: #777;margin-left:10px;">自定义审核规则管理，针对于团队自定义审核场景规则库</span>


          </h2>
          <div class="section-body" v-loading="loading">

            <el-form :model="queryParams" style="padding-left:10px;" ref="queryRef" :inline="true" v-show="showSearch"
              label-width="68px">

              <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable
                  style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                <el-button type="danger" icon="Plus" @click="handleAdd">新增</el-button>
              </el-form-item>
            </el-form>

            <el-row>
              <el-col :span="6" v-for="(item, i) in RoleList" :key="i" style="padding:8px;">
                <div class="semi-card-container" @click="handleRoleChat(item)">
                  <div class="semi-space cart-head-continer" style="gap: 16px;flex-direction: row-reverse;">

                    <div class="semi-space info-container" style="gap: 6px;height:80px">
                      <span class="semi-typography card-title">
                        <span>
                          {{ item.roleName }}
                        </span>
                      </span>
                      <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;margin-bottom:0px">
                        <span>
                          {{ truncateString(item.responsibilities, 100) }}
                        </span>
                      </p>
                      <div class="semi-space card-tag-list" style="gap: 4px;"></div>
                    </div>

                    <div class="cart-head-content">
                      <div class="cart-head-content">
                        <i class="fa-solid fa-signature" />
                      </div>
                    </div>

                  </div>

                  <div class="semi-divider semi-divider-horizontal"></div>

                  <div class="semi-space"
                    style="width: 100%;gap: 8px;display: flex;justify-content: space-between;align-items: center;">
                    <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children"
                      style="display: inline-flex;">
                      <div class="semi-space card-statics" style="gap: 8px;">
                        <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 20条</span>
                      </div>
                    </div>

                    <div class="platform-container-YOpW3B">
                      <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;display: flex;color: #1d75b0;align-items: center;">
                        <el-button type="primary" size="small" @click="handleEmployRole(item.id , false)" text bg>
                          <i class="fa-solid fa-clone"></i> &nbsp; 详情
                        </el-button>
                        <el-button type="primary" size="small" @click="handleEmployRole(item.id , false)" text bg>
                          <i class="fa-solid fa-paper-plane"></i> &nbsp; 发起 
                        </el-button>
                      </div>
                    </div>

                  </div>

                </div>
              </el-col>
            </el-row>

          </div>
        </section>

        <pagination style="background: #fafafa;padding-top:0px;" 
            :total="total" 
            v-show="total > 0" 
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize" 
            @pagination="getList" />

      </el-col>
    </el-row>

        <el-dialog v-model="open" :title="title" width="1024px" append-to-body>
            <!-- 添加或修改用户配置对话框 -->
            <el-form :model="form" 
                :rules="rules" 
                ref="formRef" 
                :label-position="'top'" 
                size="large" 
                label-width="120px">
                <el-form-item label="审查清单名称" prop="roleName">
                    <el-input v-model="form.ruleName" placeholder="请输入审查清单名称" clearable />
                </el-form-item>
                <el-form-item label="清单规则列表" prop="responsibilities">
                    <el-scrollbar height="400px" style="width:100%">

                        <div style="display: flex;flex-direction: column;gap: 5px;">
                            <div v-for="(item , index) in currentRules" :key="index" style="display: flex;gap: 10px;padding-right:20px; justify-content: space-between;">
                                <div>
                                    <el-tag v-if="item.riskLevel === 'low'" type="info">低风险</el-tag>
                                    <el-tag v-if="item.riskLevel === 'medium'" type="warning">中风险</el-tag>
                                    <el-tag v-if="item.riskLevel === 'high'" type="danger">高风险</el-tag>
                                    &nbsp;
                                    <span>{{ item.description }}</span>
                                </div>
                                <div>
                                    <el-button type="danger" bg text icon="Remove" size="default" @click="removeRuleItem">删除规则</el-button>
                                </div>
                            </div>
                        </div> 
                    </el-scrollbar>

                </el-form-item>
            </el-form>
            <!-- 提交与取消按钮-->
            <template #footer>
                <div class="dialog-footer">
                <el-button size="large" @click="open = false">取消</el-button>
                <el-button type="warning" size="large" @click="drawer = true">添加规则</el-button>
                <el-button size="large" type="primary" @click="handleSubmit">确认</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog
          v-model="drawer"
          title="I am the title"
          :direction="'left'"
          append-to-body
          :before-close="handleCloseDrawer">
          <span>Hi, there!</span>
        </el-dialog>

   </div>
</template>

<script setup name="Role">

import {
  employRole,
  listMarketRole
} from "@/api/base/im/role";

import { ElLoading } from 'element-plus'
import { reactive } from "vue";

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const RoleList = ref([]);
const PushOrgRoleList = ref([]);

const drawer = ref(false);

const sceneId = ref(route.query.sceneId)
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref([]);

const open = ref(false);
const title = ref("增加规则库");

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

const currentRules = ref([
    {
        "id": 1,
        "description": "在合同风险负担及通知义务条款中，技术开发合同风险负担及通知义务",
        "riskLevel": "medium"
    },
    {
        "id": 2,
        "description": "在合同解除条款中，审查解除条件",
        "riskLevel": "high"
    },
    {
        "id": 3,
        "description": "在合同违约责任条款中，审查违约责任是否明确、违约金标准是否适当",
        "riskLevel": "high"
    },
    {
        "id": 4,
        "description": "在合同争议解决条款中，争议解决的方式必须明确不能既约定诉讼又约定仲裁",
        "riskLevel": "medium"
    },
    {
        "id": 5,
        "description": "在合同争议解决条款中，争议解决机构应当明确",
        "riskLevel": "medium"
    },
    {
        "id": 6,
        "description": "是否有送达与通知条款",
        "riskLevel": "medium"
    },
    {
        "id": 7,
        "description": "在合同形式与生效条款中，合同生效与签订日期审查",
        "riskLevel": "medium"
    },
    {
        "id": 8,
        "description": "在合同主体条款中，审查合同当事人的名称（姓名）、住所等基本信息",
        "riskLevel": "medium"
    },
    {
        "id": 9,
        "description": "在合同法律引用条款中，确保引用法律文件名称的准确性和有效性",
        "riskLevel": "medium"
    },
    {
        "id": 10,
        "description": "在合同开发内容条款中，开发内容明确性审查",
        "riskLevel": "high"
    },
    {
        "id": 11,
        "description": "在合同承揽方式条款中，明确承揽方式",
        "riskLevel": "high"
    },
    {
        "id": 12,
        "description": "在合同项目期限条款中，项目时间管理审查",
        "riskLevel": "high"
    },
    {
        "id": 13,
        "description": "在合同费用及支付条款中，费用支付条款审查",
        "riskLevel": "high"
    },
    {
        "id": 14,
        "description": "在合同交付验收条款中，明确成果交付与验收流程",
        "riskLevel": "low"
    },
    {
        "id": 15,
        "description": "在合同知识产权条款中，知识产权归属",
        "riskLevel": "low"
    },
    {
        "id": 16,
        "description": "在合同开发合同的技术成果归属条款中，技术秘密成果归属与分享",
        "riskLevel": "medium"
    },
    {
        "id": 17,
        "description": "在合同售后维护条款中，运维及培训责任审查",
        "riskLevel": "high"
    },
    {
        "id": 18,
        "description": "在合同网络与数据安全条款中，审查网络安全事项",
        "riskLevel": "low"
    },
    {
        "id": 19,
        "description": "在合同委托人义务条款中，审查委托人的义务",
        "riskLevel": "low"
    },
    {
        "id": 20,
        "description": "在合同研究开发人义务条款中，审查研究开发人的义务",
        "riskLevel": "high"
    }
]);

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

const goBack = () => {
  router.go(-1);
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

const handleAdd = () => {
  // reset();
  // open.value = true;
  // title.value = "增加规则库";
  router.push({
      path: '/scene/documentReview/addDataset',
      query: {
          sceneId: sceneId.value
      }
  })
};

// /** 复制角色 */
// function handleEmployRole(roleId , isPush) {

//   const loading = ElLoading.service({
//     lock: true,
//     text: '复制手续办理中.....',
//     background: 'rgba(0, 0, 0, 0.2)',
//   })

//   employRole(roleId , isPush).then(res => {
//     loading.close();
//     proxy.$modal.msgSuccess("角色复制成功，可以在频道中添加角色！");
//   }).catch(e => {
//     loading.close();
//   });
// }

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

.semi-card-container{
    margin-bottom: 0px;
}

.review-page-header {
    margin-bottom: 10px;
    margin-top: 10px;
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

.cart-head-content {
    font-size: 30px;
}
</style>