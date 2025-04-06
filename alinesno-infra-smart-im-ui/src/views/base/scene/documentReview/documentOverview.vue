<template>
    <div class="contract-review-container">
        <div class="review-methods-container">
            <div class="review-methods-title">审查方式</div>
            <div class="form-container">
                <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="120px" :label-position="'top'" size="large">
                    <el-form-item label="合同类型" prop="contractType">
                        <el-select v-model="ruleForm.selectedScenario">
                            <el-option v-for="item in contractTypeList" :key="item.scenarioId" :label="item.title"
                                :value="item.scenarioId"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="选择审查立场" prop="reviewPosition">
                        <el-radio-group v-model="ruleForm.reviewPosition">
                            <el-radio label="甲方立场">甲方立场</el-radio>
                            <el-radio label="乙方立场">乙方立场</el-radio>
                            <el-radio label="中立立场">中立立场</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="选择审查清单">
                        <el-radio-group v-model="ruleForm.reviewListOption">
                            <el-radio value="aigen" label="AI智能生成" border>AI智能生成</el-radio>
                            <el-radio value="dataset" label="从知识库选择" border>
                                从知识库选择
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="选择审查清单" prop="reviewListOption" >
                        <el-select placeholder="选择审查清单" v-model="ruleForm.selectedScenario" :disabled="ruleForm.reviewListOption!== 'dataset'">
                            <el-option v-for="item in scenarioList" :key="item.scenarioId" :label="item.title"
                                :value="item.scenarioId"></el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
        </div>

        <div class="contract-overview-container">
            <div class="contract-overview-title">合同概览</div>
            <div class="contract-info-container">
                <div class="contract-info-item">
                    <div class="contract-info-label">合同总金额</div>
                    <div class="contract-info-value">40万元（按费率计算，最终以实际支付为准）</div>
                </div>
                <div class="contract-info-item">
                    <div class="contract-info-label">履行期限</div>
                    <div class="contract-info-value">730日历天（自签订合同至交工验收后1年）</div>
                </div>
                <div class="contract-info-item">
                    <div class="contract-info-label">内容概览</div>
                    <div class="contract-info-value">
                        该合同约定，甲方委托乙方对2024年房山区普通公路路面和桥梁养护（中修）工程、维护及应急抢险工程进行勘察设计。工作内容包括工程勘察、交通量调查、施工图设计及后续服务。
                        合同总价为40万元，按费率形式报价，费用依据相关文件规定计取。
                        合同履行期为730日历天，含施工图设计阶段（30天内完成）及施工现场配合服务阶段。双方需遵守廉政合同，并明确违约责任与争议解决方式。
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

const formSize = ref('default');
const ruleFormRef = ref();
// 定义表单数据
const ruleForm = reactive({
    contractType: '',
    reviewPosition: '',
    reviewListOption: '',
    selectedScenario: '',
});

// 知识库选择列表数据
const contractTypeList = ref([
    { "scenarioId": "-1", "title": "自定义场景" },
    { "scenarioId": "37", "title": "买卖合同" },
    { "scenarioId": "40", "title": "供用电、水、气、热力合同" },
    { "scenarioId": "1", "title": "赠与合同" },
    { "scenarioId": "2", "title": "借款合同" },
    { "scenarioId": "4", "title": "租赁合同" },
    { "scenarioId": "7", "title": "融资租赁合同" },
    { "scenarioId": "9", "title": "承揽合同" },
    { "scenarioId": "13", "title": "建设工程合同" },
    { "scenarioId": "17", "title": "运输合同" },
    { "scenarioId": "18", "title": "技术合同" },
    { "scenarioId": "30", "title": "保管合同" },
    { "scenarioId": "31", "title": "仓储合同" },
    { "scenarioId": "32", "title": "委托合同" },
    { "scenarioId": "34", "title": "行纪合同" },
    { "scenarioId": "35", "title": "中介合同" },
    { "scenarioId": "33", "title": "物业服务合同" },
    { "scenarioId": "41", "title": "劳务合同" },
    { "scenarioId": "8", "title": "保理合同" },
    { "scenarioId": "36", "title": "合伙合同" },
    { "scenarioId": "73", "title": "其他" },
    { "scenarioId": "3", "title": "民间借贷合同" },
    { "scenarioId": "5", "title": "一般房屋租赁合同" },
    { "scenarioId": "6", "title": "建材租赁合同" },
    { "scenarioId": "10", "title": "个人劳务承揽(兼职、顾问)协议" },
    { "scenarioId": "11", "title": "培训服务期协议" },
    { "scenarioId": "12", "title": "劳务外包合同" },
    { "scenarioId": "25", "title": "咨询服务合同" },
    { "scenarioId": "14", "title": "建设工程设计合同" },
    { "scenarioId": "15", "title": "建设工程施工合同" },
    { "scenarioId": "16", "title": "建设工程监理合同" },
    { "scenarioId": "19", "title": "技术开发合同" },
    { "scenarioId": "20", "title": "软件开发服务协议" },
    { "scenarioId": "21", "title": "软件技术开发合同" },
    { "scenarioId": "22", "title": "技术转让合同" },
    { "scenarioId": "23", "title": "注册商标转让合同" },
    { "scenarioId": "24", "title": "著作权转让合同" },
    { "scenarioId": "26", "title": "技术服务合同" },
    { "scenarioId": "27", "title": "技术许可合同" },
    { "scenarioId": "28", "title": "注册商标许可使用合同" },
    { "scenarioId": "29", "title": "著作权许可使用合同" },
    { "scenarioId": "38", "title": "建材买卖合同" },
    { "scenarioId": "39", "title": "IT软件买卖合同" },
    { "scenarioId": "42", "title": "公司股权金融类型" },
    { "scenarioId": "43", "title": "股权代持合同" },
    { "scenarioId": "44", "title": "招商引资合同" },
    { "scenarioId": "45", "title": "私募股权投资增资协议" },
    { "scenarioId": "46", "title": "私募股权投资股东协议" },
    { "scenarioId": "47", "title": "特许经营(许可加盟)合同" },
    { "scenarioId": "48", "title": "股东出资协议" },
    { "scenarioId": "49", "title": "股权转让合同" },
    { "scenarioId": "50", "title": "有限合伙企业合伙协议" },
    { "scenarioId": "51", "title": "普通合伙企业合伙协议" },
    { "scenarioId": "72", "title": "IP委托制作(视频、音频)类合同" }
]);

// 知识库选择列表数据，优化场景名称
const scenarioList = ref([
    { "scenarioId": "-1", "title": "自定义标书合同审核场景" },
    { "scenarioId": "37", "title": "常规销售标书合同审核知识库" },
    { "scenarioId": "40", "title": "能源供应标书合同审核知识库" },
    { "scenarioId": "1", "title": "赠与类标书合同审核知识库" },
    { "scenarioId": "2", "title": "借款标书合同审核知识库" },
]);

// 定义表单验证规则
const rules = reactive({
    contractType: [
        { required: true, message: '请输入合同类型', trigger: 'blur' }
    ],
    reviewPosition: [
        { required: true, message: '请选择审查立场', trigger: 'change' }
    ],
    reviewListOption: [
        { required: true, message: '请选择审查清单选项', trigger: 'change' },
        {
            validator: (rule, value, callback) => {
                if (value === '从知识库选择' && !ruleForm.selectedScenario) {
                    callback(new Error('从知识库选择时，请选择具体的列表项'));
                } else {
                    callback();
                }
            },
            trigger: 'change'
        }
    ],
    selectedScenario: [
        {
            validator: (rule, value, callback) => {
                if (ruleForm.reviewListOption === '从知识库选择' && !value) {
                    callback(new Error('请选择具体的场景'));
                } else {
                    callback();
                }
            },
            trigger: 'change'
        }
    ]
});

// 提交表单方法
const submitForm = async () => {
    const formEl = ruleFormRef.value;
    if (!formEl) return;
    await formEl.validate((valid) => {
        if (valid) {
            console.log('submit!', ruleForm);
        } else {
            console.log('error submit!');
        }
    });
};

// 重置表单方法
const resetForm = () => {
    const formEl = ruleFormRef.value;
    if (!formEl) return;
    formEl.resetFields();
};
</script>

<style lang="scss" scoped>
    .contract-review-container {
        .review-methods-container {
            padding: 10px;
            background: #262727;
            border-radius: 5px;
            margin-top: 10px;
            .review-methods-title {
                font-size: 14px;
                background: #1d1e1f;
                border-radius: 5px;
                padding: 10px;
            }
            .form-container {
                padding: 10px;
                border-radius: 5px;
            }
        }
        .contract-overview-container {
            padding: 10px;
            background: #262727;
            border-radius: 5px;
            margin-top: 10px;
            .contract-overview-title {
                font-size: 14px;
                background: #1d1e1f;
                border-radius: 5px;
                padding: 10px;
            }
            .contract-info-container {
                .contract-info-item {
                    padding: 10px;
                    border-radius: 5px;
                    margin-top: 15px;
                    font-size: 14px;
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                    .contract-info-label {
                        font-weight: bold;
                    }
                }
            }
        }
    }
</style>