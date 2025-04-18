<template>
  <div class="app-container" style="background-color: #fff;padding-top:10px;">
        <el-page-header @back="goBack" class="review-page-header">
            <template #content>
                <span class="text-large font-600 mr-3"> 新增加审核清单</span>
            </template>
        </el-page-header>
    <div>

    <el-row :gutter="20">
        <el-col :span="24">

            <div style="text-align: right;">
                <el-button type="primary" @click="addRules">添加规则</el-button>
                <el-button type="primary" @click="submitForm">提交</el-button>
            </div>

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
                    <el-row style="width:100%">
                        <el-col :span="12">
                    <el-scrollbar style="height:calc(100vh - 300px); width:100%">

                        <div style="display: flex;flex-direction: column;gap: 5px;">
                            <div v-for="(item , index) in currentRules" :key="index" style="display: flex;gap: 10px;padding-right:20px; justify-content: space-between;">
                                <div>
                                    <el-tag v-if="item.riskLevel === 'low'" type="info">低风险</el-tag>
                                    <el-tag v-if="item.riskLevel === 'medium'" type="warning">中风险</el-tag>
                                    <el-tag v-if="item.riskLevel === 'high'" type="danger">高风险</el-tag>
                                    &nbsp;
                                    <span @click="selectRuleItem(item)" style="cursor: pointer;">{{ item.description }}</span>
                                </div>
                                <div>
                                    <el-button type="danger" bg text icon="Remove" size="default" @click="removeRuleItem">删除规则</el-button>
                                </div>
                            </div>
                        </div> 
                    </el-scrollbar>
                        </el-col>
                        <el-col :span="12" style="padding-left: 20px;padding-right:20px;display: flex;flex-direction: column;gap: 15px;">
                            <div>
                                规则详细
                            </div>
                            <div style="display: flex;flex-direction: column;">
                                <span style="border-radius: 5px;padding: 2px 10px;background: #f5f5f5;font-weight: bold;">
                                    规则名称 
                                </span>
                                <span>
                                    {{ currentItem.description }}
                                </span>
                            </div>
                            <div style="display: flex;flex-direction: column;">
                                <span style="border-radius: 5px;padding: 2px 10px;background: #f5f5f5;font-weight: bold;">
                                    风险等级
                                </span>
                                <span>
                                    <el-tag v-if="currentItem.riskLevel === 'low'" type="info">低风险</el-tag>
                                    <el-tag v-if="currentItem.riskLevel === 'medium'" type="warning">中风险</el-tag>
                                    <el-tag v-if="currentItem.riskLevel === 'high'" type="danger">高风险</el-tag>
                                </span>
                            </div>
                            <div style="display: flex;flex-direction: column;">
                                <span style="border-radius: 5px;padding: 2px 10px;background: #f5f5f5;font-weight: bold;">
                                    规则描述
                                </span>
                                <span>
                                    {{ currentItem.ruleDetail}}
                                    技术秘密成果使用权、转让权及收益分配由双方约定；无明确约定按《民法典》处理，均可使用和转让，直至专利授权前。委托开发者交付前不得转第三方。
                                </span>
                            </div>
                        </el-col>
                    </el-row>

                </el-form-item>
            </el-form>
        </el-col>
    </el-row>


    </div>
    <el-dialog v-model="dialogVisible" title="添加规则" width="800px" :close-on-click-modal="false">
        <div>
            <el-tree-select
                v-model="value"
                :data="treeRuleData"
                size="large"
                :render-after-expand="false"
                show-checkbox
                style="width: 100%"
            />
        </div>

        <!-- 添加提交按钮 -->
        <div style="text-align: right;margin-top:20px;">
            <el-button type="primary" @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">提交</el-button>
        </div>
    </el-dialog>
    </div>

</template>

<script setup>
import { ref, reactive, toRefs } from "vue";

const currentItem = ref({});
const dialogVisible = ref(false);

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

// 树节点
const treeRuleData= ref([])

const goBack = () => {
    window.history.back();
}

const selectRuleItem = (item) => {
    currentItem.value = item;
}

const addRules = () => {
    dialogVisible.value = true;
}

</script>

<style lang="scss" scoped>
.review-page-header{
    margin-bottom: 10px;
    margin-top: 10px;
}
</style>