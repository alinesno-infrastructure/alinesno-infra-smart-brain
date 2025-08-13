<template>
    <div class="dataset-page-contaier">

        <div class="search-container-panel">
            <el-row>
                <el-col :span="24">
                    <div class="feature-team-box" style="align-items: center;">
                        <div style="font-size: 16px;font-weight: bold;margin-left: 10px;">
                            企业知识库
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <el-row :gutter="20">
            <el-col :span="4">
                <div class="siderbar-type-list" style="

">
                    <div class="review-group-add" style="margin-top:0px;">
                        分类
                        <el-button type="primary" text bg icon="Plus" @click="openAddGroupDialog">新建</el-button>
                    </div>
                    <el-scrollbar style="height:calc(100vh - 200px); width:100%">
                        <ul style="padding: 0px;margin:0px;">
                            <li v-for="(group, groupIndex) in ruleGroups" :key="groupIndex" class="group-item"
                                :class="{ 'group-selected': currentGroupIndex === groupIndex, 'group-hover': groupHoverIndex === groupIndex }"
                                @mouseenter="groupMouseEnter(groupIndex)" @mouseleave="groupMouseLeave(groupIndex)"
                                @click="selectGroup(groupIndex, group)">

                                <div style="display: flex;flex-direction: column;gap: 5px;">
                                    <span class="filename"> {{ group.groupName }} </span>
                                    <span style="font-size:13px;color:#a5a5a5;font-weight: lighter;"> 业务知识库，集成配置管理 </span>
                                </div>
                               

                                <div v-if="groupButtonVisible[groupIndex]" style="display: inline-block;">
                                    <el-button type="primary" size="small" text bg
                                        @click="openEditGroupDialog(groupIndex)">编辑</el-button>
                                    <el-button type="danger" style="margin-left:0px;" size="small" text bg
                                        @click="deleteGroup(groupIndex)">删除</el-button>
                                </div>
                            </li>
                        </ul>
                    </el-scrollbar>
                </div>
            </el-col>
            <el-col :span="20">
                <div class="review-group-add">
                    文档列表
                    <span>
                        <el-button type="primary" size="large" bg text @click="importRuleDialog">
                            <el-icon>
                                <Upload />
                            </el-icon> 导入知识库
                        </el-button>
                        <!-- <el-button type="danger" bg text @click="batchDeleteRules">批量删除</el-button> -->
                    </span>
                </div>
                <el-scrollbar style="height:calc(100vh - 200px); width:100%">

                    <div v-if="currentGroup.rules.length == 0" style="margin-top: 10vh;">
                        <el-empty description="当前未生成项目检索的规划，你可以选择自定义的知识库来进行检索，生成项目检索的规划" />
                    </div>

                    <div class="main-container-list">
                        <div v-for="(rule, ruleIndex) in currentGroup.rules" :key="ruleIndex" class="rule-item"
                            :class="{ 'rule-selected': currentItem.id === rule.id, 'rule-hover': ruleHoverIndex === ruleIndex }"
                            @mouseenter="ruleMouseEnter(ruleIndex)" @mouseleave="ruleMouseLeave(ruleIndex)"
                            @click="selectRuleItem(rule)">
                            <div class="rule-item-content">
                                <span class="rule-item-icon"> 
                                    <i :class="`fa-solid ${getFileIcon(rule.docName)}`"></i>
                                </span>
                                <div style="cursor: pointer;display: flex;gap: 7px;flex-direction: column;line-height: 1.3rem;">
                                    <span class="filename">
                                        {{ rule.docName }}
                                    </span>
                                    <span style="font-size:13px;color:#a5a5a5;font-weight: lighter;">
                                        纳瓦尔宝典由埃里克·乔根森编著。本书围绕财富与幸福两大主题，集结纳瓦尔智慧。书中指出积累财富
                                    </span>
                                    <div class="file-list-footer"> 
                                    <el-button type="info" bg text size="small" >
                                        时间:2024-12-21 12:21:33  
                                        </el-button>
                                             
                                <el-button type="info" bg text icon="Remove" size="small"
                                    @click="removeRuleItem(rule.id)">删除文档</el-button>
                            
                                    </div>
                                </div>
                            </div>
                            
                        </div>
 
                    </div>
                </el-scrollbar>
            </el-col>

        </el-row>

        <!-- 添加分组对话框 -->
        <el-dialog v-model="addGroupDialogVisible" title="添加分类分组" width="700px">
            <!--分组类型(文档库document|数据库database)-->
            <div class="item-field">
                <el-radio-group v-model="newGroupType">
                    <el-radio label="document">文档库</el-radio>
                    <el-radio disabled label="database">数据库</el-radio>
                </el-radio-group>
            </div>
            <div class="item-field">
                <el-input v-model="newGroupName" placeholder="请输入分组名称" size="large"></el-input>
            </div>
            <template #footer>
                <el-button size="large" @click="addGroupDialogVisible = false">取消</el-button>
                <el-button size="large" type="primary" @click="saveGroup">保存</el-button>
            </template>
        </el-dialog>

        <!-- 编辑分组对话框 -->
        <el-dialog v-model="editGroupDialogVisible" title="编辑分类分组" width="700px">
            <div class="item-field">
                <el-radio-group v-model="editeGroupType">
                    <el-radio label="document">文档库</el-radio>
                    <el-radio disabled label="database">数据库</el-radio>
                </el-radio-group>
            </div>
            <div class="item-field">
                <el-input size="large" v-model="editedGroupName" placeholder="请输入新的分组名称"></el-input>
            </div>
            <template #footer>
                <el-button size="large" @click="editGroupDialogVisible = false">取消</el-button>
                <el-button size="large" type="primary" @click="saveEditedGroup">保存修改</el-button>
            </template>
        </el-dialog>

        <!-- 导入文档弹出框 -->
        <el-dialog v-model="upload.open" title="导入文档" width="700px" :before-close="closeUploadDialog" >
            <el-upload ref="uploadRef" :limit="1"
                accept=".xlsx,.xls,.csv,.txt,.pdf,.doc,.docx,.ppt,.pptx,.png,.jpeg,.jpg" style="width:100%"
                :headers="upload.headers" :action="upload.url + '?groupId=' + currentGroup.id + '&sceneId=' + sceneId"
                :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess"
                :auto-upload="true" drag>
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
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus';
import { getToken } from "@/utils/auth";

import {
    listGroup,
    removeGroup,
    saveOrUpdateRuleGroup,
    removeRule,
    saveOrUpdateRule
} from '@/api/base/im/scene/productResearchDataset';

const importRuleDialogVisible = ref(false);
const { proxy } = getCurrentInstance();

const currentGroupId = ref(null)

// 分类分组
const ruleGroups = ref([
    {
        groupName: '未配置',
        rules: []
    }
]);

// 当前选中的分组
const currentGroup = ref(ruleGroups.value[0]);

// 新分类对象
const newRule = ref({
    ruleName: '',
    groupId: null,
    ruleContent: '',
    riskLevel: '',
    reviewPosition: ''
});
const sceneId = ref(proxy.$route.query.sceneId)

/*** 应用导入参数 */
const upload = reactive({
    // 是否显示弹出层（应用导入）
    open: false,
    // 弹出层标题（应用导入）
    title: "",
    // 是否更新已经存在的应用数据
    updateSupport: 0,
    // 设置上传的请求头部
    headers: { Authorization: "Bearer " + getToken() },
    // 上传的地址
    url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/projectKnowledge/importData"
});

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

// 表单校验分类
const rules = ref({
    ruleName: [
        { required: true, message: '请输入审核分类名称', trigger: 'blur' }
    ],
    groupId: [
        { required: true, message: '请选择分组', trigger: 'change' }
    ],
    ruleContent: [
        { required: true, message: '请选择分类描述', trigger: 'change' }
    ],
    riskLevel: [
        { required: true, message: '请选择风险等级', trigger: 'change' }
    ],
    reviewPosition: [
        { required: true, message: '请选择审查立场', trigger: 'change' }
    ]
});
  
// 当前选中的分类
const currentItem = ref({});

// 当前选中的分组索引
const currentGroupIndex = ref(0);

// 添加分组对话框可见性
const addGroupDialogVisible = ref(false);

// 新分组名称
const newGroupType = ref('document');
const newGroupName = ref('');

// 编辑分组对话框可见性
const editGroupDialogVisible = ref(false);

// 编辑中的分组名称
const editeGroupType = ref('document');
const editedGroupName = ref('');

// 添加分类对话框可见性
const addRuleDialogVisible = ref(false);

// 控制分组按钮显示的数组
const groupButtonVisible = ref(new Array(ruleGroups.value.length).fill(false));

// 鼠标悬停在分组上的索引
const groupHoverIndex = ref(-1);

// 鼠标悬停在分类上的索引
const ruleHoverIndex = ref(-1);

const goBack = () => {
    window.history.back();
}

const closeUploadDialog = () => {
    if(upload.isUploading){
        ElMessage.warning('正在上传中，请稍后...')
        return ;
    }
    upload.open = false;
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

// 打开添加分类对话框
const openAddRuleDialog = () => {
    addRuleDialogVisible.value = true;
};

// 保存新分组
const saveGroup = () => {
    const data = {
        groupType: newGroupType.value,
        groupName: newGroupName.value,
        sceneId: sceneId.value,
        groupSort: 1
    }
    saveOrUpdateRuleGroup(data).then(res => {
        ElMessage.success('保存成功')
        addGroupDialogVisible.value = false;

        newGroupName.value = '';
        newGroupType.value = 'document';
        handleListGroup();
    })

}

const importRuleDialog = () => {

    // 如果groupId为空，则提示选择分组
    // if (!currentGroup.value.id) {
    //     ElMessage.warning('请选择分组');
    //     return;
    // }

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
            id: ruleGroups.value[index].id,
            groupType: editeGroupType.value,
            sceneId: sceneId.value,
            groupName: editedGroupName.value
        }

        saveOrUpdateRuleGroup(data).then(res => {
            editGroupDialogVisible.value = false;

            editedGroupName.value = '';
            editeGroupType.value = 'document';
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

// 保存新分类
const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            saveOrUpdateRule(newRule.value).then(res => {

                addRuleDialogVisible.value = false;
                newRule.value = {
                    ruleName: '',
                    ruleContent: '',
                    riskLevel: '',
                    reviewPosition: ''
                };

                handleListGroup();
            })

            ElMessage.success('分类保存成功');
        } else {
            ElMessage.error('请填写完整信息');
        }
    });
};

// 保存新分类
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

// 删除分类
const deleteRule = (groupIndex, ruleIndex) => {
    ruleGroups.value[groupIndex].rules.splice(ruleIndex, 1);
}

// 批量删除分类
const batchDeleteRules = () => {
    // 这里可以添加批量删除的逻辑，例如弹出确认框等
    console.log('批量删除文档');
}

// 选择分类项
const selectRuleItem = (rule) => {
    currentItem.value = rule;
    ruleHoverIndex.value = -1;
}

// 移除分类项
const removeRuleItem = (id) => {
    // 做一下确认
    ElMessageBox.confirm('确定删除该文档吗？', '提示', {
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
const selectGroup = (index, group) => {
    currentGroupIndex.value = index;
    currentGroup.value = ruleGroups.value[index];
    groupHoverIndex.value = -1;

    // newRule.value.groupId = currentGroup.value.id;
    currentGroupId.value = group.id;
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

// 分类鼠标进入事件
const ruleMouseEnter = (index) => {
    ruleHoverIndex.value = index;
};

// 分类鼠标离开事件
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
        if (res.data.length != 0) {
            ruleGroups.value = res.data;
            currentGroup.value = ruleGroups.value[currentGroupIndex.value ? currentGroupIndex.value : 0];

            if (currentGroup.value.rules) {
                currentItem.value = currentGroup.value.rules[currentGroupIndex.value ? currentGroupIndex.value : 0];
            }
        }
    })
};

handleListGroup();

</script>

<style lang="scss" scoped>
.dataset-page-contaier {
    padding: 20px 10px ;
    background-color: #fafafa ;

    .rule-item-content {
        display: flex;
        align-items: center;
        gap: 5px;
        line-height: 1rem;
        flex-direction: row;
        align-content: center;
        padding-top: 5px;
        paddingn-bottom: 5px;

        .rule-item-icon {
            width: 30px;
            height: 30px;
            flex-shrink: 0;
            color: #1d75b0;
            font-size: 25px;
        }

    }


}

.group-item {
    list-style: none;
    padding: 10px;
    height: auto;
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
    margin-left: 0px;
    margin-right: 0px;
    font-size: 15px;
    font-weight: bold;

    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #f5f5f5;
    padding: 5px 10px;
    background: #f5f5f5;
    border-radius: 8px;
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
    border-radius: 8px;
    width: calc(25% - 15px); 
    background: #fff;
    padding: 10px;
        border: 1px solid rgba(6, 7, 9, 0.1);
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

.main-container-list {
    display: flex; gap: 20px; flex-flow: wrap;
    margin-top:10px;
}


    .siderbar-type-list{
        padding: 10px;
    border-radius: 10px;
    margin-top: 10px; 
    background-color: #fff;
    }

    .file-list-footer {
    font-size: 12px; color: rgb(165, 165, 165); font-weight: lighter; margin-top: 5px; border-top: 1px solid rgb(245, 245, 245); padding-top: 10px;
    }

.group-hover {
    background-color: #f0f9ff;
}

.rule-selected {
    // background-color: #e6f7ff;
}

.rule-hover {
    background-color: #f0f9ff;
}

.item-field {
    padding: 10px;
}

.filename {  
  }
</style>