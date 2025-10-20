<template>
    <div class="dataset-page-contaier">
        <div class="search-container-panel">
            <el-row>
                <el-col :span="24">
                    <div class="feature-team-box" style="align-items: center; display: flex; justify-content: space-between;">
                        <div style="font-size: 16px;font-weight: bold;margin-left: 10px;">
                            我的知识库
                        </div>
                        <el-button type="primary" v-if="knowledgeBases.length > 0" icon="Plus" @click="handleCreate">
                            新建知识库
                        </el-button>
                    </div>
                </el-col>
            </el-row>
        </div>

        <div class="dataset-container-panel">
            <!-- 知识库列表 - 卡片式布局 -->
            <div v-loading="loading" class="knowledge-grid">
                <DatasetItem 
                    v-for="kb in knowledgeBases" 
                    :key="kb.id" 
                    :kb="kb" 
                    @view="handleView"
                    @edit="handleEdit"
                    @delete="handleDelete"
                />
            </div>

            <!-- 空状态 -->
            <div v-if="!loading && knowledgeBases.length === 0" class="empty-state">
                <el-empty description="请配置自己的知识库，当前暂无知识库数据" :image="ElEmpty.PRESENTED_IMAGE_SIMPLE">
                    <template #default>
                        <el-button type="primary" size="large" icon="Plus" @click="handleCreate">
                            新建知识库
                        </el-button>
                    </template>
                </el-empty>
            </div>

            <!-- 分页 -->
            <div v-if="total > 0 && !loading" class="pagination-container">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[8, 16, 24, 32]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handlePageChange"
                    @current-change="handlePageChange"
                />
            </div>
        </div>

        <!-- 新建/编辑知识库对话框 -->
        <el-dialog 
            v-model="dialogVisible" 
            :title="dialogType === 'create' ? '新建知识库' : '编辑知识库'"
            :width="600"
        >
            <br/>
            <el-alert title="此为高级用户场景" type="primary" show-icon />
            <br/>
            <el-form 
                ref="knowledgeForm" 
                size="large"
                :model="formData" 
                :rules="formRules" 
                label-position="right"
                label-width="120px"
            >
                <el-form-item label="知识库名称" prop="groupName">
                    <el-input v-model="formData.groupName" placeholder="请输入知识库名称" />
                </el-form-item>

                <el-form-item label="描述">
                    <el-input 
                        v-model="formData.description" 
                        placeholder="请输入知识库描述（可选）" 
                    />
                </el-form-item>
                
                <el-form-item label="向量模型" prop="embeddingModelId">
                    <LLMSelector :modelType="'vector_model'" :size="'large'" v-model="formData.embeddingModelId" />
                </el-form-item>
                
                <el-form-item label="OCR模型" prop="ocrModelId">
                    <LLMSelector :modelType="'ocr_model'" :size="'large'" v-model="formData.ocrModelId" />
                </el-form-item>
                
                <el-form-item label="文档识别模型" prop="documentRecognitionModelId">
                    <LLMSelector :modelType="'large_language_model'" :size="'large'" v-model="formData.documentRecognitionModelId" />
                </el-form-item>
                
            </el-form>
            
            <template #footer>
                <el-button size="large" @click="dialogVisible = false">取消</el-button>
                <el-button size="large" type="primary" @click="handleSave">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="Index">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox, ElEmpty } from 'element-plus';
import { getToken } from "@/utils/auth";

import LLMSelector from './LLMSelector'

// 导入API
import {
    listGroup,
    removeGroup,
    saveOrUpdateRuleGroup, 
} from '@/api/base/im/scene/productResearchDataset';

import DatasetItem from './item.vue'

// 获取路由实例
const { proxy } = getCurrentInstance();
const router = proxy.$router;

// 状态管理
const loading = ref(false);
const searchKeyword = ref('');
const statusFilter = ref('');
const sortType = ref('createdAt_desc');
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);
const knowledgeBases = ref([]);

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref('create'); // create 或 edit
const formData = reactive({
    id: null,
    // sceneId: null, // 假设从路由或全局状态获取当前场景ID
    groupType: 'document',
    groupName: '',
    groupSort: 1,
    vectorDatasetName: '',
    embeddingModelId: null,
    ocrModelId: null,
    documentRecognitionModelId: null,
    description: ''
});

// 表单验证规则
const formRules = reactive({
    groupName: [
        { required: true, message: '请输入知识库名称', trigger: 'blur' },
        { max: 128, message: '名称不能超过128个字符', trigger: 'blur' }
    ],
    groupType: [
        { required: true, message: '请选择知识库类型', trigger: 'change' }
    ],
    vectorDatasetName: [
        { required: true, message: '请输入向量数据集名称', trigger: 'blur' },
        { max: 128, message: '名称不能超过128个字符', trigger: 'blur' }
    ],
    groupSort: [
        { required: true, message: '请输入排序号', trigger: 'blur' }
    ],
    embeddingModelId: [
        { required: true, message: '请选择向量模型', trigger: 'change' }
    ]
});

// 获取表单引用
const knowledgeForm = ref(null);

// 获取知识库列表数据
const fetchKnowledgeBases = async () => {
    loading.value = true;
    try {
        // 构建查询参数
        const params = {
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            keyword: searchKeyword.value,
            status: statusFilter.value,
            sort: sortType.value,
            // 假设场景ID从路由参数获取
            // sceneId: router.currentRoute.value.params.sceneId
        };
        
        // 调用API获取数据
        const response = await listGroup(params);
        
        if (response.code === 200 && response.data) {
            knowledgeBases.value = response.data || [];
            total.value = response.total || 0;
        } else {
            ElMessage.error('获取知识库列表失败: ' + (response.msg || '未知错误'));
        }
    } catch (error) {
        ElMessage.error('获取知识库列表失败');
        console.error(error);
    } finally {
        loading.value = false;
    }
};

// 搜索和筛选
const handleSearch = () => {
    currentPage.value = 1; // 重置到第一页
    fetchKnowledgeBases();
};

// 查看知识库
const handleView = (id) => {
    router.push(`/datasetEdit?id=${id}`);
};

// 分页变化
const handlePageChange = () => {
    fetchKnowledgeBases();
};

// 新建知识库
const handleCreate = () => {
    dialogType.value = 'create';
    // 重置表单数据
    Object.assign(formData, {
        id: null,
        groupName: '',
        groupType: 'document',
        groupSort: 1,
        vectorDatasetName: '',
        embeddingModelId: null,
        ocrModelId: null,
        documentRecognitionModelId: null,
        description: '',
        // 设置场景ID
        sceneId: router.currentRoute.value.params.sceneId
    });
    dialogVisible.value = true;
};

// 编辑知识库
const handleEdit = (kb) => {
    dialogType.value = 'edit';
    // 填充表单数据
    Object.assign(formData, {
        id: kb.id,
        sceneId: kb.sceneId,
        groupName: kb.groupName,
        groupType: kb.groupType,
        groupSort: kb.groupSort || 1,
        vectorDatasetName: kb.vectorDatasetName,
        embeddingModelId: kb.embeddingModelId,
        ocrModelId: kb.ocrModelId,
        documentRecognitionModelId: kb.documentRecognitionModelId,
        description: kb.description || ''
    });
    dialogVisible.value = true;
};

// 保存知识库
const handleSave = async () => {
    // 表单验证
    const valid = await knowledgeForm.value.validate();
    if (!valid) return;
    
    try {
        // 调用保存API
        const response = await saveOrUpdateRuleGroup(formData);
        
        if (response.code === 200) {
            ElMessage.success(dialogType.value === 'create' ? '知识库创建成功' : '知识库更新成功');
            dialogVisible.value = false;
            // 重新加载数据
            fetchKnowledgeBases();
        } else {
            ElMessage.error((dialogType.value === 'create' ? '创建失败' : '更新失败') + ': ' + (response.msg || '未知错误'));
        }
    } catch (error) {
        ElMessage.error(dialogType.value === 'create' ? '创建知识库失败' : '更新知识库失败');
        console.error(error);
    }
};

// 删除知识库（父组件收到子组件 emit 的 id 后直接调用删除接口）
const handleDelete = async (id) => {
    try {
        const response = await removeGroup(id);
        if (response.code === 200) {
            ElMessage.success('删除成功');
            fetchKnowledgeBases();
        } else {
            ElMessage.error('删除失败: ' + (response.msg || '未知错误'));
        }
    } catch (error) {
        ElMessage.error('删除知识库失败');
        console.error(error);
    }
};

// 页面挂载时获取数据
onMounted(() => {
    fetchKnowledgeBases();
});
</script>

<style lang="scss" scoped>
.dataset-page-contaier {
    padding: 20px 10px;
    background-color: #fafafa;

    .rule-item-content {
        display: flex;
        align-items: center;
        gap: 5px;
        line-height: 1rem;
        flex-direction: row;
        align-content: center;
        padding-top: 5px;
        padding-bottom: 5px;

        .rule-item-icon {
            width: 30px;
            height: 30px;
            flex-shrink: 0;
            color: #1d75b0;
            font-size: 25px;
        }
    }
    
    .pagination-container {
        display: flex;
        justify-content: center;
        margin-top: 20px;
    }
}

.knowledge-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 20px;
    margin-bottom: 10px;
    margin-top: 10px;
    padding: 10px;
}
</style>
