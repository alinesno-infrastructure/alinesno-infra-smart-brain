<template>
    <div class="app-container review-page-contaier">
        <el-page-header @back="goBack" class="review-page-header">
            <template #content>
                <span class="text-large font-600 mr-3"> 审核规则库</span>
            </template>
        </el-page-header>

        <el-row :gutter="20">
            <el-col :span="5">
                <div class="review-group-add">
                    规则分组 
                    <el-button type="primary" text bg icon="Plus" @click="openAddGroupDialog">新建</el-button>
                </div>
                <el-scrollbar style="height:calc(100vh - 200px); width:100%">
                    <ul style="padding: 0px;margin:0px;">
                        <li v-for="(group, groupIndex) in ruleGroups" :key="groupIndex" 
                            class="group-item"
                            :class="{ 'group-selected': currentGroupIndex === groupIndex, 'group-hover': groupHoverIndex === groupIndex }"
                            @mouseenter="groupMouseEnter(groupIndex)" 
                            @mouseleave="groupMouseLeave(groupIndex)"
                            @click="selectGroup(groupIndex)">
                            {{ group.groupName }}
                            <div v-if="groupButtonVisible[groupIndex]" style="display: inline-block;">
                                <el-button type="primary" size="small" text bg @click="openEditGroupDialog(groupIndex)">编辑</el-button>
                                <el-button type="danger" size="small" text bg @click="deleteGroup(groupIndex)">删除</el-button>
                            </div>
                        </li>
                    </ul>
                </el-scrollbar>
            </el-col>
            <el-col :span="11">
                <div class="review-group-add">
                    审查规则
                    <span>
                        <el-button type="warning" bg text @click="importRuleDialog">批量规则</el-button>
                        <el-button type="primary" bg text @click="openAddRuleDialog">添加规则</el-button>
                        <!-- <el-button type="danger" bg text @click="batchDeleteRules">批量删除</el-button> -->
                    </span>
                </div>
                <el-scrollbar style="height:calc(100vh - 200px); width:100%">
                    <div style="display: flex;flex-direction: column;gap: 5px;">
                        <div v-for="(rule, ruleIndex) in currentGroup.rules" :key="ruleIndex" 
                             class="rule-item"
                             :class="{ 'rule-selected': currentItem.id === rule.id, 'rule-hover': ruleHoverIndex === ruleIndex }"
                             @mouseenter="ruleMouseEnter(ruleIndex)" 
                             @mouseleave="ruleMouseLeave(ruleIndex)"
                             @click="selectRuleItem(rule)">
                            <div>
                                <el-tag v-if="rule.riskLevel === 'low'" type="info">低风险</el-tag>
                                <el-tag v-if="rule.riskLevel ==='medium'" type="warning">中风险</el-tag>
                                <el-tag v-if="rule.riskLevel === 'high'" type="danger">高风险</el-tag>
                                &nbsp;
                                <span style="cursor: pointer;">{{ rule.ruleName }}</span>
                            </div>
                            <div>
                                <el-button type="danger" bg text icon="Remove" size="default" @click="removeRuleItem(rule.id)">删除规则</el-button>
                            </div>
                        </div>
                    </div> 
                </el-scrollbar>
            </el-col>
            <el-col :span="8">
                <div class="review-group-add">
                    规则详细
                    <el-button type="primary" icon="EditPen" bg text @click="batchDeleteRules">编辑</el-button>
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
                    <div style="display: flex;flex-direction: column;" v-if="currentItem?.reviewPosition">
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

        <!-- 添加分组对话框 -->
        <el-dialog v-model="addGroupDialogVisible" title="添加规则分组" width="700px">
            <el-input v-model="newGroupName" placeholder="请输入分组名称" size="large"></el-input>
            <template #footer>
                <el-button size="large" @click="addGroupDialogVisible = false">取消</el-button>
                <el-button size="large" type="primary" @click="saveGroup">保存</el-button>
            </template>
        </el-dialog>

        <!-- 编辑分组对话框 -->
        <el-dialog v-model="editGroupDialogVisible" title="编辑规则分组" width="700px">
            <el-input size="large" v-model="editedGroupName" placeholder="请输入新的分组名称"></el-input>
            <template #footer>
                <el-button size="large" @click="editGroupDialogVisible = false">取消</el-button>
                <el-button size="large" type="primary" @click="saveEditedGroup">保存修改</el-button>
            </template>
        </el-dialog>

        <!-- 添加规则对话框 -->
    <el-dialog v-model="addRuleDialogVisible" title="添加审核规则" width="1024px">
        <el-form :model="newRule" :rules="rules" ref="formRef" size="large" label-width="100px">
            <el-form-item label="规则名称" prop="ruleName">
                <el-input v-model="newRule.ruleName" placeholder="请输入审核规则名称"></el-input>
            </el-form-item>
            <el-form-item label="分组" prop="groupId">
                <el-select v-model="newRule.groupId" placeholder="请选择分组">
                    <el-option v-for="(group, index) in ruleGroups" :key="index" :label="group.groupName" :value="group.id"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="风险等级" prop="riskLevel">
                <el-radio-group v-model="newRule.riskLevel">
                    <el-radio v-for="(level, index) in riskLevels" :key="index" :label="level.value">{{ level.label }}</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="审查立场" prop="reviewPosition">
                <el-radio-group v-model="newRule.reviewPosition">
                    <el-radio v-for="(standpoint, index) in reviewPositions" :key="index" :label="standpoint.value">{{ standpoint.label }}</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="规则描述" prop="ruleContent">
                <el-input type="textarea" :rows="10" resize="none" v-model="newRule.ruleContent" placeholder="请输入规则描述"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button size="large" @click="addRuleDialogVisible = false">取消</el-button>
            <el-button size="large" type="primary" @click="submitForm">保存</el-button>
        </template>
    </el-dialog>

    <!-- 导入规则弹出框 -->
     <el-dialog v-model="upload.open" title="导入规则" width="700px">
        <el-upload ref="uploadRef" 
               :limit="1" 
               accept=".xlsx,.xls" 
               style="width:100%" 
               :headers="upload.headers"
               :action="upload.url" 
               :disabled="upload.isUploading" 
               :on-progress="handleFileUploadProgress" 
               :on-success="handleFileSuccess"
               :auto-upload="true" 
               drag>
               <el-icon class="el-icon--upload">
                  <upload-filled />
               </el-icon>
               <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
               <template #tip>
                  <div class="el-upload__tip text-center">
                     <span>支持 .excel 文件，导入文件不能超过10M</span>
                  </div>
               </template>
            </el-upload>
     </el-dialog>

    </div>
</template>

<script setup name="Index">
import { ref } from 'vue';
import { ElMessage , ElLoading , ElMessageBox } from 'element-plus';
import { getToken } from "@/utils/auth";

import { 
    listGroup , 
    removeGroup , 
    saveOrUpdateRuleGroup, 
    removeRule ,
    saveOrUpdateRule
 } from '@/api/smart/assistant/reviewRulus';

const importRuleDialogVisible = ref(false);
const { proxy } = getCurrentInstance();

/*** 应用导入参数 */
const upload = reactive({
  // 是否显示弹出层（应用导入）
  open: false,
  // 弹出层标题（应用导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的应用数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url:
    import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/documentReview/rule/importData",
});

// 规则分组
const ruleGroups = ref([
    {
        groupName: '未配置',
        rules: []
    }
]);

// 表单引用
const formRef = ref(null);

// 风险等级数组
const riskLevels = ref([
    { label: '低风险', value: 'low' },
    { label: '中风险', value: 'medium' },
    { label: '高风险', value: 'high' }
]);

// 审查立场数组
const reviewPositions = ref([
    { label: '甲方立场', value: 'party_a' },
    { label: '乙方立场', value: 'party_b' },
    { label: '中立立场', value: 'neutral' }
]);

// 新规则对象
const newRule = ref({
    ruleName: '',
    groupId: null,
    ruleContent: '',
    riskLevel: '',
    reviewPosition: ''
});

// 表单校验规则
const rules = ref({
    ruleName: [
        { required: true, message: '请输入审核规则名称', trigger: 'blur' }
    ],
    groupId: [
        { required: true, message: '请选择分组', trigger: 'change' }
    ],
    ruleContent: [
        { required: true, message: '请选择规则描述', trigger: 'change' }
    ],
    riskLevel: [
        { required: true, message: '请选择风险等级', trigger: 'change' }
    ],
    reviewPosition: [
        { required: true, message: '请选择审查立场', trigger: 'change' }
    ]
});

// 当前选中的规则
const currentItem = ref({});

// 当前选中的分组
const currentGroup = ref(ruleGroups.value[0]);

// 当前选中的分组索引
const currentGroupIndex = ref(0);

// 添加分组对话框可见性
const addGroupDialogVisible = ref(false);

// 新分组名称
const newGroupName = ref('');

// 编辑分组对话框可见性
const editGroupDialogVisible = ref(false);

// 编辑中的分组名称
const editedGroupName = ref('');

// 添加规则对话框可见性
const addRuleDialogVisible = ref(false);

// 控制分组按钮显示的数组
const groupButtonVisible = ref(new Array(ruleGroups.value.length).fill(false));

// 鼠标悬停在分组上的索引
const groupHoverIndex = ref(-1);

// 鼠标悬停在规则上的索引
const ruleHoverIndex = ref(-1);

const goBack = () => {
    window.history.back();
}

// 打开添加分组对话框
const openAddGroupDialog = () => {
    addGroupDialogVisible.value = true;
};

// 打开编辑分组对话框
const openEditGroupDialog = (index) => {
    editGroupDialogVisible.value = true;
    editedGroupName.value = ruleGroups.value[index].groupName;
};

// 打开添加规则对话框
const openAddRuleDialog = () => {
    addRuleDialogVisible.value = true;
};

// 保存新分组
const saveGroup = () => {
    const data = {
        groupName: newGroupName.value,
        groupSort: 1 
    }
    saveOrUpdateRuleGroup(data).then(res => {
        ElMessage.success('保存成功')
        addGroupDialogVisible.value = false;

        newGroupName.value = '';
        handleListGroup();
    })

}

const importRuleDialog = () => {
    upload.open = true;
}


/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 0px 0;'>" +
      response.msg +
    "</div>",
    "导入结果",
    { dangerouslyUseHTMLString: true }
  );
  handleListGroup();
};

// 保存编辑后的分组
const saveEditedGroup = () => {
    if (editedGroupName.value) {
        const index = currentGroupIndex.value;
        const data = {
            id: ruleGroups.value[index].id , 
            groupName: editedGroupName.value
        }

        saveOrUpdateRuleGroup(data).then(res => {
            editGroupDialogVisible.value = false;

            editedGroupName.value = '';
            handleListGroup();
        })

    }
}

// 删除分组
const deleteGroup = (groupIndex) => {

    // 确认是否删除
    ElMessageBox.confirm('确定删除该分组吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        // 删除分组
        removeGroup(ruleGroups.value[groupIndex].id).then(res => {
            ElMessage.success('删除成功')

            handleListGroup();
        })
    }).catch(() => { });


}

// 保存新规则
const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            saveOrUpdateRule(newRule.value).then(res => {

                addRuleDialogVisible.value = false;
                newRule.value = {
                    ruleName: '',
                    // groupId: null,
                    ruleContent: '',
                    riskLevel: '',
                    reviewPosition: ''
                };

                handleListGroup();
            })

            ElMessage.success('规则保存成功');
        } else {
            ElMessage.error('请填写完整信息');
        }
    });
};

// 保存新规则
const saveRule = () => {
    if (newRule.value.ruleContent && newRule.value.riskLevel && newRule.value.reviewPosition) {
        const selectedGroupIndex = newRule.value.groupIndex;
        ruleGroups.value[selectedGroupIndex].rules.push({
           ...newRule.value,
            id: Date.now() // 简单生成唯一id
        });
        addRuleDialogVisible.value = false;
        newRule.value = {
            groupId: 0,
            ruleContent: '',
            riskLevel: '',
            reviewPosition: ''
        };
    }
}

// 删除规则
const deleteRule = (groupIndex, ruleIndex) => {
    ruleGroups.value[groupIndex].rules.splice(ruleIndex, 1);
}

// 批量删除规则
const batchDeleteRules = () => {
    // 这里可以添加批量删除的逻辑，例如弹出确认框等
    console.log('批量删除规则');
}

// 选择规则项
const selectRuleItem = (rule) => {
    currentItem.value = rule;
    ruleHoverIndex.value = -1;
}

// 移除规则项
const removeRuleItem = (id) => {
    // 做一下确认
    ElMessageBox.confirm('确定删除该规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        removeRule(id).then(() => {
            ElMessage.success('删除成功');
            handleListGroup();
        })
    }).catch(() => {
        ElMessage.info('已取消删除');
    });

}

// 选择分组
const selectGroup = (index) => {
    currentGroupIndex.value = index;
    currentGroup.value = ruleGroups.value[index];
    groupHoverIndex.value = -1;

    newRule.value.groupId = currentGroup.value.id;
};

// 分组鼠标进入事件
const groupMouseEnter = (index) => {
    groupButtonVisible.value[index] = true;
    groupHoverIndex.value = index;
};

// 分组鼠标离开事件
const groupMouseLeave = (index) => {
    groupButtonVisible.value[index] = false;
    groupHoverIndex.value = -1;
};

// 规则鼠标进入事件
const ruleMouseEnter = (index) => {
    ruleHoverIndex.value = index;
};

// 规则鼠标离开事件
const ruleMouseLeave = (index) => {
    ruleHoverIndex.value = -1;
};

// 控制分组按钮显示/隐藏
const showGroupButtons = (index, show) => {
    groupButtonVisible.value[index] = show;
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

handleListGroup();

</script>

<style lang="scss" scoped>



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

.group-hover {
    background-color: #f0f9ff;
}

.rule-selected {
    background-color: #e6f7ff;
}

.rule-hover {
    background-color: #f0f9ff;
}
</style>