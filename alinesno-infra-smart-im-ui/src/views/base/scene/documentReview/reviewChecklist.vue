<template>
    <div class="review-checklist-container">
        <div class="header">
            审查清单：智能生成
        </div>
        <div class="rule-section">
            <div class="rule-title">
                全部规则({{ contractReviewList.length }})
            </div>
            <el-scrollbar class="scrollable-area">
                <div class="checkbox-group-container">
                    <el-checkbox-group v-model="checkList" style="display: flex;flex-direction: column;">
                        <el-checkbox v-for="item in contractReviewList" :key="item.id" :label="item.label" size="large" />
                    </el-checkbox-group>
                </div>
            </el-scrollbar>
            <div class="button-section">
                <el-button type="primary" text bg size="large">自定义审核规则</el-button>
                <el-button type="primary" text bg size="large">重新发起审核</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
const contractReviewList = ref([
    { id: 1, label: '在合同风险负担及通知义务条款中，技术开发合同风险负担及通知义务' },
    { id: 2, label: '在合同解除条款中，审查解除条件' },
    { id: 3, label: '在合同违约责任条款中，审查违约责任是否明确、违约金标准是否适当' },
    { id: 4, label: '在合同争议解决条款中，争议解决的方式必须明确不能既约定诉讼又约定仲裁' },
    { id: 5, label: '在合同争议解决条款中，争议解决机构应当明确' },
    { id: 6, label: '是否有送达与通知条款' },
    { id: 7, label: '在合同形式与生效条款中，合同生效与签订日期审查' },
    { id: 8, label: '在合同主体条款中，审查合同当事人的名称（姓名）、住所等基本信息' },
    { id: 9, label: '在合同法律引用条款中，确保引用法律文件名称的准确性和有效性' },
    { id: 10, label: '在合同开发内容条款中，开发内容明确性审查' },
    { id: 11, label: '在合同承揽方式条款中，明确承揽方式' },
    { id: 12, label: '在合同项目期限条款中，项目时间管理审查' },
    { id: 13, label: '在合同费用及支付条款中，费用支付条款审查' },
    { id: 14, label: '在合同交付验收条款中，明确成果交付与验收流程' },
    { id: 15, label: '在合同知识产权条款中，知识产权归属' },
    { id: 16, label: '在合同开发合同的技术成果归属条款中，技术秘密成果归属与分享' },
    { id: 17, label: '在合同售后维护条款中，运维及培训责任审查' },
    { id: 18, label: '在合同网络与数据安全条款中，审查网络安全事项' },
    { id: 19, label: '在合同委托人义务条款中，审查委托人的义务' },
    { id: 20, label: '在合同研究开发人义务条款中，审查研究开发人的义务' }
]);

const checkList = ref([]);
</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';
</style>