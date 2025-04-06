<template>
    <div class="review-result-container">
        <div style="display: flex;flex-direction: row;justify-content: space-between;">
            <div>
                <el-button type="primary" text bg>
                    <i class="fa-solid fa-download"></i> &nbsp; 导出审核报告
                </el-button>
                <el-button type="primary" text bg>
                    <i class="fa-solid fa-file-word"></i> &nbsp; 导出标注文档
                </el-button>
            </div>
            <div>
                <el-checkbox-group v-model="checkList">
                    <el-checkbox type="danger" v-for="item in checkboxOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-checkbox-group>
            </div>
        </div>
        <div>
            <div style="margin-top:20px;">
                <div>
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 210px);padding-right:0px">
                        <div v-for="(item, index) in filteredCheckResultList" :key="index" class="check-result-item" @click="handleCheckItemClick(item)">
                            <div class="check-result-box" style="display: flex;gap: 5px;align-items: center;">
                                <span>
                                    {{ index + 1 }}
                                </span>
                                <span>
                                    {{ item.ruleTitle }}
                                </span>
                            </div>
                            <el-tag :type="getTagType(item)" effect="dark">
                                {{ item.checkRuleResult.length }}
                            </el-tag>
                        </div>
                    </el-scrollbar>
                </div>
            </div>
        </div>

        <!-- 规则检查结果列表并操作面板 -->
        <RuleCheckResultPanel ref="ruleCheckResultPanelRef" />

    </div>
</template>

<script setup>

import RuleCheckResultPanel from './ruleCheckResultPanel.vue'

const ruleTitle = ref('');

const ruleCheckResultPanelRef = ref(null);

const checkboxOptions = [
    { "label": "高风险", "value": "high" },
    { "label": "中风险", "value": "medium" },
    { "label": "低风险", "value": "low" },
    { "label": "通过", "value": "passed" }
];

const checkList = ref([]);

const filteredCheckResultList = computed(() => {
    if (checkList.value.length === 0) {
        return checkResultList.value;
    } else {
        return checkResultList.value.filter(item => checkList.value.includes(item.riskLevel));
    }
});

const checkResultList = ref([
    {
        "ruleTitle": "简要审查合同的主要条款",
        "ruleSequence": "1",
        "riskLevel": "high",
        "checkRuleResult": [
            {
                "chapter": "第一章",
                "originalText": "这是原始文本的内容",
                "suggestedTexts": [
                    { "id": 1, "text": "修改建议1" },
                    { "id": 2, "text": "修改建议2" },
                    { "id": 3, "text": "修改建议3" }
                ],
                "reason": "这里是修改的理由"
            },
            {
                "chapter": "第一章",
                "originalText": "另一处原始文本内容",
                "suggestedTexts": [
                    { "id": 4, "text": "针对此内容的修改建议1" }
                ],
                "reason": "该部分文本存在表述不清问题"
            }
        ],
        "author": "张三"
    },
    {
        "ruleTitle": "审查合同双方的权利义务是否对等",
        "ruleSequence": "2",
        "riskLevel": "high",
        "checkRuleResult": [
            {
                "chapter": "第二章",
                "originalText": "双方权利义务表述模糊不清",
                "suggestedTexts": [
                    { "id": 5, "text": "明确甲方拥有的具体权利和应履行的义务" },
                    { "id": 6, "text": "详细列出乙方的权利与义务，避免歧义" }
                ],
                "reason": "合同双方权利义务对等是合同公平性的基础，模糊表述可能导致纠纷"
            }
        ],
        "author": "李四"
    },
    {
        "ruleTitle": "检查合同的违约责任条款",
        "ruleSequence": "3",
        "riskLevel": "high",
        "checkRuleResult": [
            {
                "chapter": "第三章",
                "originalText": "违约责任约定不明确",
                "suggestedTexts": [
                    { "id": 7, "text": "明确违约情形及对应的具体赔偿金额或计算方式" },
                    { "id": 8, "text": "补充不可抗力因素下的违约责任说明" },
                    { "id": 9, "text": "细化双方在部分特殊情况下的违约责任" },
                    { "id": 10, "text": "增加对延迟交付的违约责任界定" }
                ],
                "reason": "违约责任条款不明确会降低合同的约束性，增加违约风险"
            },
            {
                "chapter": "第三章",
                "originalText": "违约赔偿金额计算方式不合理",
                "suggestedTexts": [
                    { "id": 11, "text": "采用行业通行的赔偿计算模型" }
                ],
                "reason": "现有计算方式可能对一方造成过度损失"
            }
        ],
        "author": "王五"
    },
    {
        "ruleTitle": "审核合同的标的描述",
        "ruleSequence": "4",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第一章",
                "originalText": "标的描述过于简略",
                "suggestedTexts": [
                    { "id": 12, "text": "详细说明标的的规格、型号、质量标准等" },
                    { "id": 13, "text": "增加标的的数量、交付时间等关键信息" }
                ],
                "reason": "标的描述不清晰可能导致双方对合同内容的理解不一致"
            }
        ],
        "author": "赵六"
    },
    {
        "ruleTitle": "审查合同的争议解决条款",
        "ruleSequence": "5",
        "riskLevel": "high",
        "checkRuleResult": [
            {
                "chapter": "第四章",
                "originalText": "争议解决方式表述不完整",
                "suggestedTexts": [
                    { "id": 14, "text": "明确约定是通过仲裁还是诉讼解决争议" },
                    { "id": 15, "text": "如果选择仲裁，补充仲裁机构和仲裁规则" },
                    { "id": 16, "text": "若选择诉讼，写明具体的管辖法院" },
                    { "id": 17, "text": "增加争议解决过程中的调解环节说明" }
                ],
                "reason": "争议解决条款不明确会增加纠纷解决的成本和不确定性"
            },
            {
                "chapter": "第四章",
                "originalText": "仲裁条款缺乏对仲裁员选择的规定",
                "suggestedTexts": [
                    { "id": 18, "text": "明确双方选择仲裁员的方式和范围" }
                ],
                "reason": "保障仲裁的公正性和双方权益"
            },
            {
                "chapter": "第四章",
                "originalText": "诉讼管辖法院选择可能不利于外地一方",
                "suggestedTexts": [
                    { "id": 19, "text": "协商选择更中立的管辖法院" }
                ],
                "reason": "平衡双方在争议解决中的地位"
            }
        ],
        "author": "孙七"
    },
    {
        "ruleTitle": "检查合同的生效条款",
        "ruleSequence": "6",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第五章",
                "originalText": "生效条件未明确",
                "suggestedTexts": [
                    { "id": 20, "text": "写明合同自双方签字（或盖章）之日起生效" }
                ],
                "reason": "生效条款不明确会影响合同的法律效力和执行时间"
            }
        ],
        "author": "周八"
    },
    {
        "ruleTitle": "审查合同的保密条款",
        "ruleSequence": "7",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第六章",
                "originalText": "保密范围界定不清晰",
                "suggestedTexts": [
                    { "id": 21, "text": "详细列出需要保密的信息类型和范围" },
                    { "id": 22, "text": "明确保密期限和保密义务的解除条件" },
                    { "id": 23, "text": "增加违反保密条款的违约责任" }
                ],
                "reason": "保密范围不清晰可能导致保密责任不明确，增加信息泄露风险"
            },
            {
                "chapter": "第六章",
                "originalText": "保密义务主体未明确",
                "suggestedTexts": [
                    { "id": 24, "text": "确定哪些人员和部门承担保密义务" }
                ],
                "reason": "确保保密责任落实到位"
            }
        ],
        "author": "吴九"
    },
    {
        "ruleTitle": "检查合同的价款或报酬条款",
        "ruleSequence": "8",
        "riskLevel": "high",
        "checkRuleResult": [
            {
                "chapter": "第三章",
                "originalText": "价款支付方式不明确",
                "suggestedTexts": [
                    { "id": 25, "text": "明确价款的支付时间、支付次数和支付方式" },
                    { "id": 26, "text": "补充价款调整的条件和方式" },
                    { "id": 27, "text": "规定逾期支付价款的违约责任" }
                ],
                "reason": "价款支付方式不明确会影响合同的履行和双方的利益"
            }
        ],
        "author": "郑十"
    },
    {
        "ruleTitle": "审查合同的履行期限条款",
        "ruleSequence": "9",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第二章",
                "originalText": "履行期限表述模糊",
                "suggestedTexts": [
                    { "id": 28, "text": "明确起始日期和结束日期，精确到具体日期" },
                    { "id": 29, "text": "补充履行期限内的关键时间节点和里程碑" },
                    { "id": 30, "text": "说明履行期限的变更条件和程序" }
                ],
                "reason": "履行期限模糊会导致双方对合同履行时间的理解不一致，影响合同执行"
            },
            {
                "chapter": "第二章",
                "originalText": "履行期限缺乏弹性条款",
                "suggestedTexts": [
                    { "id": 31, "text": "增加因不可抗力等原因导致的履行期限顺延规定" }
                ],
                "reason": "应对可能出现的意外情况"
            }
        ],
        "author": "陈十一"
    },
    {
        "ruleTitle": "检查合同的通知与送达条款",
        "ruleSequence": "10",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第七章",
                "originalText": "通知方式和送达地址未明确",
                "suggestedTexts": [
                    { "id": 32, "text": "明确通知的方式，如书面通知、电子邮件等" },
                    { "id": 33, "text": "写明双方的送达地址和联系方式，并约定变更通知方式" },
                    { "id": 34, "text": "规定通知送达的生效时间和视为送达的情形" }
                ],
                "reason": "通知与送达条款不明确会影响合同履行过程中的信息传递和沟通"
            }
        ],
        "author": "杨十二"
    },
    {
        "ruleTitle": "审查合同的知识产权条款",
        "ruleSequence": "11",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第八章",
                "originalText": "知识产权归属未明确",
                "suggestedTexts": [
                    { "id": 35, "text": "明确合同涉及的知识产权归属方" },
                    { "id": 36, "text": "规定知识产权的使用范围和限制条件" },
                    { "id": 37, "text": "补充知识产权侵权责任的承担方式" }
                ],
                "reason": "知识产权归属不明确可能导致知识产权纠纷，影响合同双方的利益"
            },
            {
                "chapter": "第八章",
                "originalText": "知识产权授权方式未提及",
                "suggestedTexts": [
                    { "id": 38, "text": "明确知识产权的授权形式，如独占许可、普通许可等" }
                ],
                "reason": "规范知识产权使用权限"
            }
        ],
        "author": "刘十三"
    },
    {
        "ruleTitle": "检查合同的免责条款",
        "ruleSequence": "12",
        "riskLevel": "high",
        "checkRuleResult": [
            {
                "chapter": "第五章",
                "originalText": "免责条款不合理且不明确",
                "suggestedTexts": [
                    { "id": 39, "text": "明确免责的具体情形和范围，避免不合理免责" },
                    { "id": 40, "text": "补充免责情形下双方的权利和义务" },
                    { "id": 41, "text": "说明免责条款的适用条件和限制" }
                ],
                "reason": "不合理且不明确的免责条款可能导致合同无效或引发纠纷"
            }
        ],
        "author": "黄十四"
    },
    {
        "ruleTitle": "审查合同的合同变更与转让条款",
        "ruleSequence": "13",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第四章",
                "originalText": "合同变更和转让条件不清晰",
                "suggestedTexts": [
                    { "id": 42, "text": "明确合同变更的程序和条件，如双方协商一致等" },
                    { "id": 43, "text": "规定合同转让的限制条件和通知义务" },
                    { "id": 44, "text": "补充合同变更或转让后双方权利义务的变化" }
                ],
                "reason": "合同变更与转让条款不清晰会增加合同履行的不确定性和风险"
            }
        ],
        "author": "许十五"
    },
    {
        "ruleTitle": "检查合同的附件条款",
        "ruleSequence": "14",
        "riskLevel": "medium",
        "checkRuleResult": [
            {
                "chapter": "第九章",
                "originalText": "附件内容和效力未明确",
                "suggestedTexts": [
                    { "id": 45, "text": "详细列出附件的名称和内容，明确附件与主合同的关系" },
                    { "id": 46, "text": "规定附件的生效条件和修改程序" },
                    { "id": 47, "text": "说明附件与主合同不一致时的处理原则" }
                ],
                "reason": "附件内容和效力不明确会影响合同的完整性和执行性"
            }
        ],
        "author": "马十六"
    },
    {
        "ruleTitle": "审查合同的语言文字条款",
        "ruleSequence": "15",
        "riskLevel": "low",
        "checkRuleResult": [
            {
                "chapter": "第一章",
                "originalText": "合同语言表述存在歧义",
                "suggestedTexts": [
                    { "id": 48, "text": "修改表述，使语言更加准确、清晰，避免歧义" },
                    { "id": 49, "text": "统一合同中的术语和用词，确保含义一致" },
                    { "id": 50, "text": "对容易引起歧义的条款进行解释和说明" }
                ],
                "reason": "合同语言表述有歧义可能导致双方对合同内容的理解不同，引发纠纷"
            }
        ],
        "author": "胡十七"
    }
]);

// 获取标签颜色
const getTagType = (item) => {
    switch (item.riskLevel) {
        case 'low':
            return 'info';
        case 'medium':
            return 'warning';
        case 'high':
            return 'danger';
        default:
            return 'success';
    }
}

// // 通过风险等级做过滤 
// const checkByRiskLevel = (riskLevel) => {
//     const checkList =  checkResult.value.filter(item => item.riskLevel === riskLevel);

// }

// 点击规则
const handleCheckItemClick = (item) => {
    console.log(item.ruleTitle);
    
    nextTick(() => {
        ruleCheckResultPanelRef.value.openDrawerAndSettings(item);
    });
    
}

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';

.review-result-container {
    margin-top: 20px;

    .check-result-item {
        padding: 10px;
        background: #262727;
        border-radius: 5px;
        margin-bottom: 10px;
        font-size: 14px;
        display: flex;
        justify-content: space-between;
    }

}
    .check-result-item{
        cursor: pointer;

        &:hover{
            background: #3a3a3a;
        }
    }
</style>
