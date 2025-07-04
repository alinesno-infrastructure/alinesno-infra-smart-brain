<template>
  <div class="org-management">
    <el-container style="height: 100vh;">
      <el-main>
        <el-form :model="orgForm"
              label-position="top"
              ref="orgFormRef"
              :rules="rules"
              label-width="80px"
              class="form-container join-form">
          <el-alert title="加入现有组织可以使用组织智能体." type="success" show-icon />
          <h2>组织信息设置</h2>
          <el-form-item label="是否有组织">
            <el-radio-group v-model="orgForm.type">
              <el-radio value="0" :label="0">新建组织</el-radio>
              <el-radio value="1" :label="1">加入组织</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="组织图标" v-if="orgForm.type == 0" prop="logoUrl">
            <!--
            <el-upload
                class="avatar-uploader"
                :headers="upload.headers"
                :action="upload.url"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="orgForm.logoUrl" :src="orgForm.logoUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            -->
            <el-radio-group v-model="orgForm.logoUrl" v-if="orgForm.type == 0" prop="logoUrl">
              <el-radio v-for="item in icons"
                :value="item.icon"
                :key="item.icon"
                :label="item.icon"
                >
                <i :class="item.icon"></i>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <!--
          <el-form-item label="图标" prop="icon">
            <el-radio-group v-model="form.icon">
              <el-radio v-for="item in icons"
                :value="item.icon"
                :key="item.icon"
                :label="item.icon"
                >
                <i :class="item.icon"></i>
              </el-radio>
            </el-radio-group>
          </el-form-item>
          -->

          <el-form-item label="组织ID" prop="joinDoorplateNumber" v-if="orgForm.type == 1">
            <el-input v-model="orgForm.joinDoorplateNumber" placeholder="请输入组织ID"></el-input>
          </el-form-item>
          <el-form-item label="名称" prop="orgName" v-if="orgForm.type == 0">
            <el-input v-model="orgForm.orgName" placeholder="请输入组织名称"></el-input>
          </el-form-item>
          <el-form-item label="描述" prop="remark" v-if="orgForm.type == 0">
            <el-input type="textarea" v-model="orgForm.remark" :rows="2" resize="none" placeholder="请输入组织描述"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitOrgInfo">提交</el-button>
          </el-form-item>
        </el-form>

      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createOrg } from '@/api/base/im/org';

const { proxy } = getCurrentInstance();

const orgFormRef = ref(null);
// const imageUrl = ref('')
// const uploadPath = ref('#')

// /*** 用户导入参数 */
// const upload = reactive({
//   // 是否显示弹出层（用户导入）
//   open: false,
//   // 弹出层标题（用户导入）
//   title: "",
//   // 是否禁用上传
//   isUploading: false,
//   // 是否更新已经存在的用户数据
//   updateSupport: 0,
//   // 设置上传的请求头部
//   headers: { Authorization: "Bearer " + getToken() },
//   // 上传的地址
//   url: import.meta.env.VITE_APP_BASE_API + "/system/user/importData"
// });

const icons = ref([
  { id: 1, icon: 'fa-solid fa-charging-station'} ,
  { id: 1, icon: 'fa-solid fa-truck'} ,
  { id: 2, icon: 'fa-solid fa-paper-plane'} ,
  { id: 2, icon: 'fa-solid fa-ship'} ,
  { id: 3, icon: 'fa-solid fa-chart-column'},
  { id: 4, icon: 'fa-solid fa-server'},
  { id: 5, icon: 'fa-solid fa-box-open'},
  { id: 8, icon: 'fa-solid fa-file-invoice-dollar'},
  { id: 9, icon: 'fa-solid fa-user-tie'},
]);

const orgForm = ref({
  type: 0 ,
  logoUrl: '',
  joinDoorplateNumber: '' ,
  orgName: '',
  remark: ''
});

// 定义验证规则
const rules = ref({
  logoUrl: [
    { required: true, message: '请上传组织图标', trigger: 'blur' }
  ],
  joinDoorplateNumber: [
    { required: true, message: '请输入组织ID', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  orgName: [
    { required: true, message: '请输入组织名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  remark: [
    { required: true, message: '请输入组织描述', trigger: 'blur' },
    { min: 1, max: 200, message: '长度在 1 到 200 个字符', trigger: 'blur' }
  ]
});

// const handleAvatarSuccess = (res, file) => {
//   orgForm.value.logoUrl = URL.createObjectURL(file.raw);
// };

// const beforeAvatarUpload = (file) => {
//   const isLt2M = file.size / 1024 / 1024 < 2;

//   if (!isLt2M) {
//     ElMessage.error('上传头像图片大小不能超过 2MB!');
//   }

//   return isLt2M;
// };

const submitOrgInfo = () => {
  // 使用 ref 获取表单实例并调用 validate 方法
  proxy.$refs.orgFormRef.validate(valid => {
    if (valid) {
      console.log(orgForm.value);
      createOrg(orgForm.value).then(res => {
        console.log('res = ' + res);
        ElMessage.success('组织信息提交成功！');
        window.location.href = '/'
      })
    } else {
      return false;
    }
  });
};

const joinExistingOrg = () => {
  // 这里添加处理加入现有组织的逻辑
  console.log(joinForm.value);
  ElMessage.success(`正在尝试加入组织 ID: ${joinForm.value.id}`);
};
</script>

<style scoped lang="scss">
.org-management {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-color: #fafafa;
}

.form-container {
  width: 400px; /* 调整宽度以适应您的需求 */
  padding: 20px;
  margin:auto;
  background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
  border-radius: 8px;
  border: 1px solid rgb(228, 231, 237);
  // box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.join-form {
  margin-top: 100px;
}

.avatar-uploader {
  border: 1px dashed rgb(205, 208, 214);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 80px;
  height: 80px;
  display: block;
}
</style>