<template>
  <div>
    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="30%" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls, .pdf, .md , .txt, .doc, .docx"
        :headers="upload.headers" :action="upload.url + '?sceneId=' + upload.sceneId" :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xls、xlsx格式文件。</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="User">

import { getToken } from "@/utils/auth";
import { onMounted } from 'vue';
import { getParam } from '@/utils/ruoyi'

const router = useRouter();
const { proxy } = getCurrentInstance();

// 定义派发事件
const emit = defineEmits(['handlePushResponseMessageList'])

/*** 用户导入参数 */
const upload = reactive({
  // 当前频道ID
  sceneId: null,
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "文档材料库上传",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/sceneData"
});

function handleOpenUpload(val) {
  upload.open = val;
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

  proxy.$modal.msgSuccess("上传成功.");

  // emit("handlePushResponseMessageList", response.data);
};
/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
};

onMounted(() => {
  upload.sceneId = getParam("sceneId");
})


defineExpose({
  handleOpenUpload,
})

</script>