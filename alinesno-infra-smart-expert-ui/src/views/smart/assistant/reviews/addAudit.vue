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
                        <el-button type="warning" size="large" @click="addRules">添加规则</el-button>
                        <el-button type="primary" :loading="commitLoading" size="large" @click="submitForm">提交</el-button>
                    </div>

                    <!-- 添加或修改用户配置对话框 -->
                    <el-form :model="form" :rules="rules" ref="formRef" :label-position="'top'" size="large" label-width="120px">

                        <el-row>
                            <el-col :span="12">
                                <el-form-item label="审查清单名称" prop="auditName">
                                    <el-input v-model="form.auditName" style="width:80%" placeholder="请输入审查清单名称" clearable />
                                </el-form-item>
                            </el-col>

                            <el-col :span="12">
                                <el-form-item label="数据范围" prop="dataScope">
                                    <el-radio-group v-model="form.dataScope">
                                        <el-radio v-for="item in workplaceTypesArr"
                                            style="margin-top: 10px;margin-bottom: 10px;" :key="item.id" :value="item.id" :label="item.id" size="large">
                                            <div
                                                style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                                                <span style="font-size:15px;font-weight: bold;">
                                                    <i :class="item.icon"></i> {{ item.name }}
                                                </span>
                                                <span style="color:#a5a5a5">
                                                    {{ item.desc }}
                                                </span>
                                            </div>
                                        </el-radio>
                                    </el-radio-group>
                                </el-form-item>

                            </el-col>
                        </el-row>

                        <el-form-item label="审核规则列表" prop="auditLists">
                            <el-row style="width:100%">
                                <el-col :span="12">
                                    <el-scrollbar style="height:calc(100vh - 350px); width:100%">

                                        <div style="display: flex;flex-direction: column;gap: 5px;">
                                            <div class="rule-item" v-for="(item, index) in currentRules" :key="index">
                                                <div>
                                                    {{ index  + 1 }}
                                                    <el-tag v-if="item.riskLevel === 'low'" type="info">低风险</el-tag>
                                                    <el-tag v-if="item.riskLevel === 'medium'" type="warning">中风险</el-tag>
                                                    <el-tag v-if="item.riskLevel === 'high'" type="danger">高风险</el-tag>
                                                    &nbsp;
                                                    <span @click="selectRuleItem(item)"
                                                        style="color:#606266; cursor: pointer;">{{ item.ruleName }}</span>
                                                </div>
                                            </div>
                                            <div v-if="currentRules.length == 0">
                                                <el-empty :image-size="100" description="还没有添加规则">
                                                    <span style="color:#606266">
                                                        点击右上方“添加规则”，从已有规则中选择并添加
                                                    </span>
                                                </el-empty>
                                            </div>
                                        </div>
                                    </el-scrollbar>
                                </el-col>
                                <el-col v-if="currentItem.ruleName" :span="12" class="rule-item-panel">
                                    <div class="rule-item-detail-title">
                                        规则详细
                                        <el-button type="danger" bg text icon="Remove" size="default" @click="removeRuleItem">删除规则</el-button>
                                    </div>
                                    <div style="display: flex;flex-direction: column;">
                                        <span
                                            style="border-radius: 5px;padding: 2px 10px;background: #f5f5f5;font-weight: bold;">
                                            规则名称
                                        </span>
                                        <span>
                                            {{ currentItem.ruleName}}
                                        </span>
                                    </div>
                                    <div style="display: flex;flex-direction: column;">
                                        <span
                                            style="border-radius: 5px;padding: 2px 10px;background: #f5f5f5;font-weight: bold;">
                                            风险等级
                                        </span>
                                        <span>
                                            <el-tag v-if="currentItem.riskLevel === 'low'" type="info">低风险</el-tag>
                                            <el-tag v-if="currentItem.riskLevel === 'medium'"
                                                type="warning">中风险</el-tag>
                                            <el-tag v-if="currentItem.riskLevel === 'high'" type="danger">高风险</el-tag>
                                        </span>
                                    </div>
                                    <div style="display: flex;flex-direction: column;">
                                        <span
                                            style="border-radius: 5px;padding: 2px 10px;background: #f5f5f5;font-weight: bold;">
                                            规则描述
                                        </span>
                                        <span>
                                            {{ currentItem.ruleContent }}
                                        </span>
                                    </div>
                                </el-col>
                            </el-row>

                        </el-form-item>
                    </el-form>
                </el-col>
            </el-row>


        </div>
        <el-dialog v-model="dialogVisible" title="添加规则" width="80%" :close-on-click-modal="false">
            <RuleSelectPanel ref="ruleSelectPanelRef" @setSelectRuleIds="setSelectRuleIds" />
        </el-dialog>
    </div>

</template>

<script setup>
import { ref, reactive, toRefs } from "vue";

import {
    saveOrUpdateAudit , 
    queryAudit
} from '@/api/smart/assistant/reviewAudit';

import RuleSelectPanel from './ruleSelectPanel.vue';

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const currentAuditId = ref(route.query.id);
const currentItem = ref({});
const dialogVisible = ref(false);
const formRef = ref(null);
const ruleSelectPanelRef = ref(null);

const workplaceTypesArr = [
    { "id": "public", "name": "公开", "icon": "fa-solid fa-globe", "desc": "公开审核清单只能选择公共智能体、公开频道、公开场景" },
    { "id": "org", "name": "组织", "icon": "fa-solid fa-truck-plane", "desc": "组织审核清单可以选择公共和组织内的智能体、频道和场景" }
];

const data = reactive({
    form: {},
    rules: {
        auditName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }, {
            min: 2,
            max: 20,
            message: "角色名称长度必须介于 2 和 20 之间",
            trigger: "blur"
        }],
        auditLists: [{ required: true, message: "审核规则不能为空", trigger: "blur" }],
        dataScope: [{ required: true, message: "数据范围不能为空", trigger: "blur" }]
    },
});

const { form, rules } = toRefs(data);

const currentRules = ref([]);
const commitLoading = ref(false)

const goBack = () => {
    window.history.back();
}

const setSelectRuleIds = (items) => {
    console.log('setSelectRule = ' + items);
    dialogVisible.value = false;
    currentRules.value = items;
    currentItem.value = items[0];

    // 只需要id
    form.value.auditLists = currentRules.value.map(item => item.id);
}

const selectRuleItem = (item) => {
    currentItem.value = item;
}

const addRules = () => {
    dialogVisible.value = true;
}

const submitForm = () => {
    proxy.$refs["formRef"].validate(valid => {
        if (valid) {

            commitLoading.value = true ;
            console.log("formData.value = " + JSON.stringify(form.value));

            form.value.id = currentAuditId.value;

            saveOrUpdateAudit(form.value).then(res => {

                if(currentAuditId.value){
                    proxy.$modal.msgSuccess("更新成功");
                }else{
                    proxy.$modal.msgSuccess("保存成功");
                }

                commitLoading.value = false;
                currentAuditId.value = res.data;
            })
        }
    })
};

const removeRuleItem = () => {
    proxy.$modal.confirm('确定删除[' +currentItem.value.ruleName + ']审核规则吗？').then(() => {
        // 从rules数组当中删除掉，以id为标识
        currentRules.value = currentRules.value.filter(rule => rule.id!== currentItem.value.id);
    })
};

const handleGetAudit = () => {
    console.log('curentAuditId = ' + currentAuditId.value);
    if(currentAuditId.value){
        queryAudit(currentAuditId.value).then(res => {
            form.value = res.data;
            setSelectRuleIds(res.data.rules);
        })
    }
}

handleGetAudit();

</script>

<style lang="scss" scoped>
.review-page-header {
    margin-bottom: 10px;
    margin-top: 10px;
}

.rule-item {
    display: flex;
    gap: 10px;
    padding-right:20px; 
    justify-content: space-between;

    &:hover {
        background-color: #e6f7ff;
        color: #1d75b0; 
        font-size: 14px;
        font-weight: bold;
    }

}

.rule-item-panel {
    padding-left: 20px;
    padding-right:20px;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .rule-item-detail-title{
        font-size: 14px;
        background: #f5f5f5;
        border-radius: 5px;
        color: #606266;
        font-weight: bold;
        padding: 0px 10px;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}


</style>