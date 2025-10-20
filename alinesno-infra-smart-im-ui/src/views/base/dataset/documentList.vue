<template>
    <div class="document-list-container">
        <el-page-header @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3">
                    {{ props.currentDatasetGroup?.groupName }}
                </span>
            </template>
            <template #extra>
                <div class="flex items-center">
                    <el-button type="primary" class="ml-2" text bg @click="importDocumentDialog">
                        <!-- 使用 font awesome6 图标 -->
                        <i class="fa-solid fa-file-import"></i>&nbsp;导入文档
                    </el-button>
                    <el-button type="warning" text bg class="ml-2" @click="handleRefresh()">
                        <!-- 使用 font awesome6 图标 -->
                        <i class="fa-solid fa-arrows-rotate"></i> &nbsp; 刷新
                    </el-button>
                </div>
            </template>
        </el-page-header>
        <!-- 文档列表面板 -->
        <div class="document-list-panel">

            <el-table v-if="refreshTable" v-loading="loading" :data="datasetList">

                <el-table-column prop="name" label="名称">
                    <template #default="scope">
                        <FileTypeIcon :item="scope.row" />
                        {{ scope.row.docName }}
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="处理模式" width="120">
                    <template #default="scope">
                        分块存储
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="orderNum" label="数据量" width="100">
                    <template #default="scope">
                        {{ scope.row.chunkCount? scope.row.chunkCount: '-' }}
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="level" label="创建/更新时间" width="200">
                    <template #default="scope">
                        {{ parseTime(scope.row.updateTime) }} 
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="hasStatus" label="状态" width="120">
                    <template #default="scope">
                        <el-button type="danger" text bg v-if="scope.row.vectorStatus == 0" :loading="true">
                            未就绪
                        </el-button>
                        <el-button type="success" text bg v-if="scope.row.vectorStatus == 1" icon="Check">
                            已就绪
                        </el-button>
                        <!-- 异常是2 -->
                         <el-button type="danger" text bg v-if="scope.row.vectorStatus == 2" icon="Error">
                            异常
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button type="text" icon="EditPen" @click="handleUpdate(scope.row)">重命名</el-button>
                        <el-button type="text" icon="Delete" @click="handleDelete(scope.row)" >删除</el-button>
                    </template>
                </el-table-column> 
            </el-table>

            <pagination v-show="total > 0" 
                :total="total" 
                v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" 
                @pagination="getList" />

        </div>

        <!-- 导入文档弹出框 -->
        <el-dialog v-model="upload.open" title="导入文档" width="700px" :before-close="closeUploadDialog" >
            <el-upload ref="uploadRef" :limit="1"
                accept=".xlsx,.xls,.csv,.txt,.pdf,.doc,.docx,.ppt,.pptx,.md" style="width:100%"
                :headers="upload.headers" :action="upload.url + '?groupId=' + props.currentDatasetGroup?.id"
                :disabled="upload.isUploading" 
                :on-progress="handleFileUploadProgress" 
                :on-success="handleFileSuccess"
                :on-error="handleError"
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

        <!-- 重命名弹窗 -->
        <el-dialog v-model="renameDialog.open" title="重命名" width="400px" :before-close="closeRenameDialog">
            <div>
                <el-input v-model="renameDialog.newName" type="textarea" :rows="3" resize="none" placeholder="请输入新名称"></el-input>
            </div>
            <template #footer>
                <el-button @click="closeRenameDialog">取消</el-button>
                <el-button type="primary" @click="confirmRename">确定</el-button>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>

import { ElMessage, ElLoading, ElMessageBox } from 'element-plus';
import { getToken } from "@/utils/auth";

import FileTypeIcon from './fileIcon.vue';

import {
    listKnowledge,
    renameKnowledge,
    deleteKnowledge
} from '@/api/base/im/scene/productResearchDataset';
import { nextTick } from 'vue';

// 获取路由实例
const { proxy } = getCurrentInstance();
const router = useRouter()
const route = useRoute()

const renameDialog = reactive({
    open: false,
    id: null,
    newName: ''
});

const loading = ref(true);
const refreshTable = ref(true);
const total = ref(0);
const datasetList = ref([]);
const groupId = ref(route.query.id)

const props = defineProps({
    currentDatasetGroup: {
        type: Object,
        default: null
    }
})

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

const data = reactive({
   queryParams: {
      pageNum: 1,
      pageSize: 10,
   },
});

const { queryParams } = toRefs(data);

/** 查询应用列表 */
function getList() {
   loading.value = false ;

   queryParams.value.groupId = groupId.value ; 

   listKnowledge(proxy.addDateRange(queryParams.value)).then(res => {
      loading.value = false;
      datasetList.value = res.rows || [];
      total.value = res.total;
   });
};

const goBack = () => {
    router.push(`/dataset`);
}

const handleRefresh = () => { 
    getList();
    ElMessage.success('刷新成功')
}

const closeUploadDialog = () => {
    if(upload.isUploading){
        ElMessage.warning('正在上传中，请稍后...')
        return ;
    }
    upload.open = false;
}

const importDocumentDialog = () => {
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
    getList();
};

/** 文件上传失败时的钩子 */
const handleError = (err, file, fileList) => {
    upload.isUploading = false;
    proxy.$message.error("文件上传失败");
}

const closeRenameDialog = () => {
    renameDialog.open = false;
    renameDialog.id = null;
    renameDialog.newName = '';
};

const handleUpdate = (row) => {
    renameDialog.open = true;
    renameDialog.id = row.id;
    renameDialog.newName = row.docName || '';
};

// 确定重命名
const confirmRename = () => {
    if (!renameDialog.newName || !renameDialog.newName.trim()) {
        ElMessage.warning('请输入新名称');
        return;
    }
    renameKnowledge({ id: renameDialog.id, newName: renameDialog.newName.trim() })
        .then(res => {
            ElMessage.success('重命名成功');
            closeRenameDialog();
            getList();
        })
        .catch(err => {
            ElMessage.error('重命名失败: ' + (err?.message || err));
        });
};

// 删除文档
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除文档 "${row.docName}" 吗？此操作会同时删除向量数据，且不可恢复！`,
        '确认删除',
        { type: 'warning' }
    ).then(() => {
        deleteKnowledge({ id: row.id }).then(res => {
            ElMessage.success('删除成功');
            getList();
        }).catch(err => {
            ElMessage.error('删除失败: ' + (err?.message || err));
        });
    }).catch(() => {
        // 取消
    });
};

nextTick(() => { 
    getList();
});


</script>

<style scoped lang="scss">
.document-list-container {
    padding: 20px;
    background-color: #fff;
    margin-right: 10px;
    height: calc(100vh - 60px);
}

.document-list-panel {
    margin-top: 20px;
}

</style>