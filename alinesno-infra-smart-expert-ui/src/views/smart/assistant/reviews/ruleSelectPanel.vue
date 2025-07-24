<template>
    <div style="background-color: #fff;padding-top:0px;">

        <el-row :gutter="20">
            <el-col :span="5">
                <div class="review-group-add">
                    规则分组 
                </div>
                <el-scrollbar style="height:calc(90vh - 300px); width:100%">
                    <ul style="padding: 0px;margin:0px;">
                        <li v-for="(group, groupIndex) in ruleGroups" :key="groupIndex" 
                            class="group-item"
                            :class="{ 'group-selected': currentGroupIndex === groupIndex, 'group-hover': groupHoverIndex === groupIndex }"
                            @click="selectGroup(groupIndex)">
                            {{ group.groupName }}
                        </li>
                    </ul>
                </el-scrollbar>
            </el-col>
            <el-col :span="11">
                <div class="review-group-add">
                    审查规则
                </div>
                <el-scrollbar style="height:calc(90vh - 300px); width:100%">
                    <div style="display: flex;flex-direction: column;gap: 5px;">
                        <div v-for="(rule, ruleIndex) in currentGroup.rules" :key="ruleIndex" 
                             class="rule-item"
                             :class="{ 'rule-selected': selectedRuleIds.includes(rule.id), 'rule-hover': ruleHoverIndex === ruleIndex }">
                            <el-checkbox v-model="selectedRuleIds" :label="rule.id">
                                <span @click="selectRuleItem(rule)" class="rule-item-content">
                                    <el-tag v-if="rule.riskLevel === 'low'" type="info">低风险</el-tag>
                                    <el-tag v-if="rule.riskLevel ==='medium'" type="warning">中风险</el-tag>
                                    <el-tag v-if="rule.riskLevel === 'high'" type="danger">高风险</el-tag>

                                    <!-- 超过长度，自动换行-->
                                    <span style="cursor: pointer; word-break: break-all; white-space: normal;">{{ rule.ruleName }}</span>
                                </span>
                            </el-checkbox>
                        </div>
                    </div> 
                </el-scrollbar>
            </el-col>
            <el-col :span="8">
                <div class="review-group-add">
                    规则详细
                </div>
                <div class="review-item-detail" >
                    <div style="display: flex;flex-direction: column;">
                        <span class="rule-item-detail-title">
                            规则名称 
                        </span>
                        <span class="rule-item-detail">
                            {{ currentItem.ruleName }}
                        </span>
                    </div>
                    <div style="display: flex;flex-direction: column;">
                        <span class="rule-item-detail-title">
                            风险等级
                        </span>
                        <span class="rule-item-detail">
                            <el-tag v-if="currentItem.riskLevel === 'low'" type="info">低风险</el-tag>
                            <el-tag v-if="currentItem.riskLevel ==='medium'" type="warning">中风险</el-tag>
                            <el-tag v-if="currentItem.riskLevel === 'high'" type="danger">高风险</el-tag>
                        </span>
                    </div>
                    <div style="display: flex;flex-direction: column;">
                        <span class="rule-item-detail-title">
                            审查立场
                        </span>
                        <span class="rule-item-detail">
                            <span v-if="currentItem && currentItem.reviewPosition === 'party_a'" type="text">甲方立场</span>
                            <span v-if="currentItem && currentItem.reviewPosition === 'party_b'" type="text">乙方立场</span>
                            <span v-if="currentItem && currentItem.reviewPosition === 'neutral'" type="text">中立</span>
                        </span>
                    </div>
                    <div style="display: flex;flex-direction: column;">
                        <span class="rule-item-detail-title">
                            规则描述
                        </span>
                        <span class="rule-item-detail">
                            {{ currentItem.ruleContent }}
                        </span>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 添加提交按钮 -->
        <div style="text-align: right;margin-bottom:20px;">
            <el-button type="primary" size="large" @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" size="large" @click="getSelectRuleIds()">确认 (已选择 {{ selectedRuleIds.length }} 项)</el-button>
        </div>

    </div>
</template>

<script setup name="Index">
import { ref } from 'vue';
import { ElLoading , ElMessage , ElMessageBox } from 'element-plus';

import { 
    listGroup ,
    listRuleByIds
 } from '@/api/smart/assistant/reviewRulus';

 const emit = defineEmits(['setSelectRuleIds']);

// 规则分组
const ruleGroups = ref([
    {
        groupName: '未配置',
        rules: []
    }
]);

const currentItem = ref({});
const currentGroup = ref(ruleGroups.value[0]);
const currentGroupIndex = ref(0);
const groupHoverIndex = ref(-1);
const ruleHoverIndex = ref(-1);
const selectedRuleIds = ref([]);

// 选择规则项
const selectRuleItem = (rule) => {
    currentItem.value = rule;
    ruleHoverIndex.value = -1;
}

// 选择分组
const selectGroup = (index) => {
    currentGroupIndex.value = index;
    currentGroup.value = ruleGroups.value[index];
    groupHoverIndex.value = -1;

    if(currentGroup.value.rules){
        currentItem.value = currentGroup.value.rules[0];
    }
};

// 列表分组
const handleListGroup = () => {
 
    const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.7)',
    })

    listGroup().then(res => {
        console.log(res.data)
        loading.close();
        if(res.data.length != 0){
            ruleGroups.value = res.data;
            currentGroup.value = ruleGroups.value[0] ;

            if(currentGroup.value.rules){
                currentItem.value = currentGroup.value.rules[0];
            }
        }
    })
};

const getSelectRuleIds = () => {
    // 这里可以通过 selectedRuleIds.value 获取所有选择的规则 id
    console.log('选择的规则 id 列表:', selectedRuleIds.value);

    listRuleByIds(selectedRuleIds.value).then(res => {
        emit('setSelectRuleIds', res.data);
    })

}

handleListGroup();

</script>    

<style lang="scss" scoped>
.review-page-header{
    margin-bottom: 10px;
    margin-top: 10px;
}



.group-item {
    list-style: none;
    padding: 10px;
    height: 40px;
    border-radius: 5px;
    display: flex;
    align-items: center;
    margin-bottom: 5px;
    color: rgba(0, 0, 0, 0.88);
    font-size: 14px;
    line-height: 18px;
    cursor: pointer;
    justify-content: space-between;

    &:hover {
        background-color: #e6f7ff;
        color: #1d75b0; 
        font-size: 14px;
        font-weight: bold;
    }
}

.review-group-add {
    margin: 10px;
    font-size: 15px;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #f5f5f5;
    padding-bottom: 10px;
}

.group-selected {
    background-color: #e6f7ff;
    color: #1d75b0; 
    font-size: 14px;
    font-weight: bold;
}

.rule-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-direction: row;
    font-size: 14px;
    padding: 5px;
    border-radius: 3px;

    &:hover {
        background-color: #f0f9ff;
        color: #1d75b0; 
        font-size: 14px;
        font-weight: bold;
    }

    .rule-item-content {
        display: flex;
        align-items: center;
        gap: 10px;
        line-height: 1.2rem;
    }

}

.review-item-detail {
    display: flex;
    gap: 10px;
    flex-direction: column;
    line-height: 2rem;
    font-size: 14px;
    padding: 15px;

    .rule-item-detail-title {
        border-radius: 5px;
        padding: 5px 15px;
        background: #f5f5f5;
        font-weight: bold;
    }

    .rule-item-detail {
        padding-left: 5px;
    }

}

.rule-selected {
    background-color: #e6f7ff;
}

</style>