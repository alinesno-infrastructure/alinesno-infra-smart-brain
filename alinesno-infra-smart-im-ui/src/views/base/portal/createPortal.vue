<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择AI工作台"
    width="980"
    :before-close="handleClose"
  >
    <div 
      v-loading="loading"
      element-loading-text="Loading..."
      :element-loading-spinner="svg"
      element-loading-svg-view-box="-10, -10, 50, 50"
      element-loading-background="rgba(122, 122, 122, 0.8)"
    >

      <div style="padding: 5px;margin-bottom: 10px;">
        选择不同的工作台会形成不同的<b>AI智能体</b>工作组合，并且可以根据需求进行定制工作台。
      </div>

      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
        class="demo-ruleForm"
        :label-position="'top'"
        :size="formSize"
        status-icon>
        <el-form-item label="工作台名称" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入工作台名称"  />
        </el-form-item>

        <!-- 自定义工作台 -->
         <el-form-item label="类型" prop="type">
              <el-radio-group v-model="ruleForm.type" @change="handleWorkplaceTypeChanne">
                <el-radio v-for="item in workplaceTypesArr" 
                  style="margin-top: 10px;margin-bottom: 10px;" 
                  :key="item.id" 
                  :value="item.id"
                  :label="item.id" size="large">
                  <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                    <span style="font-size:15px;font-weight: bold;">
                      <i :class="item.icon"></i> {{ item.name }}
                    </span>
                    <span style="color:#a5a5a5">
                      {{ item.desc }}
                    </span>
                  </div>
                </el-radio>
              </el-radio-group>
            </el-form-item>

        <el-form-item label="工作台" prop="template" v-if="ruleForm.type === 1">
          <el-row style="width:100%">
            <el-col :span="6" v-for="(item, index) in workbenches" :key="index">
              <div
                :style="{
                  background: selectedTemplateId === item.id ? '#e5f6ff' : '#fff',
                  border: selectedTemplateId === item.id ? '1px solid #1890ff' : '1px solid rgba(6, 7, 9, 0.1)',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  margin: '10px',
                  display: 'flex',
                  'flex-direction': 'column'
                }"
                @click="selectTemplate(item.id)">
                <img v-if="item.backgroundImage" :src="imagePathByPath(item.backgroundImage)" style="width: 100%;border-radius: 7px 7px 0px 0px;height:100px" />
                <div style="display: flex;flex-direction: column;line-height: 1.4rem;gap: 5px;padding: 10px;">
                  <span style="font-size: 15px;color: #333;font-weight: bold;">
                    {{ item.name }}
                    <el-button v-if="item.status === 0" type="text" size="small">
                      集成中 
                    </el-button>
                  </span>
                  <div class="ellipsis-2lines" style="color: #a5a5a5;">
                    {{ item.description }}
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="选择智能体" prop="selectAgentId" v-if="ruleForm.type === 2">
          <el-scrollbar style="height:200px;width:100%;">
            <el-row>
                <el-col :span="6" v-for="(item , index) in orgRoleList" :key="index">
                    <AgentSelectCard v-model="ruleForm.selectAgentId"  
                      :agent="item"
                      :key="index" />
                </el-col>
                <el-col :span="6" v-for="(item , index) in publicRoleList" :key="index">
                    <AgentSelectCard v-model="ruleForm.selectAgentId"  
                      :agent="item"
                      :key="index" />
                </el-col>
            </el-row>
          </el-scrollbar> 
        </el-form-item>

        <el-form-item label="选择场景" prop="selectSceneId" v-if="ruleForm.type === 2">
            <el-scrollbar style="height:250px;width:100%;">
          <el-row>
            <el-col :span="4" v-for="(item , index) in sceneLists" :key="index">
              <SceneSelectCard 
                v-model="ruleForm.selectSceneId"  
                :scene="item"
                :key="index" />
            </el-col>
          </el-row>
          </el-scrollbar>
        </el-form-item>

      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button v-loading="submitLoading" type="primary" size="large" @click="handleSubmit">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import AgentSelectCard from './card/agentCard';
import SceneSelectCard from './card/sceneCard';

import { 
  getAgentStoreRole 
} from '@/api/base/im/store';

import {
    sceneListByPage
} from '@/api/base/im/scene';

import {
  listOrgWorkplace, 
  useWorkplace,
  customWorkplace,
} from "@/api/base/im/workplace"

const emit = defineEmits(['handleHasWorkplace']);

const sceneLists = ref([])
const publicRoleList = ref([])
const orgRoleList = ref([])

const dialogVisible = ref(false);
const loading = ref(false);
const submitLoading = ref(false);
const ruleFormRef = ref(null);
const ruleForm = ref({
  name: '',
  type: 1,
  template: ''
});
const formSize = ref('large');
const selectedTemplateId = ref(null);

const workbenches = ref([]);    

const workplaceTypesArr = ref([
    { "id": 1, "name": "行业工作台", "icon": "fa-solid fa-industry", "desc": "行业工作台提供通用的行业智能体和场景" },
    { "id": 2, "name": "自定义工作台", "icon": "fa-solid fa-wrench", "desc": "自定义工作台可自由选择智能体和场景" }
]);

const rules = ref({
  name: [
    { required: true, message: '请输入工作台名称', trigger: 'blur' },
    { min: 2, max: 20, message: '工作台名称长度应在2到20个字符之间', trigger: 'blur' }
  ],
  template: [
    { required: true, message: '请选择工作台模板', trigger: 'change' }
  ] , 
  selectSceneId: [
    { required: true, message: '请选择场景', trigger: 'change' }
  ],
  selectAgentId: [
    { required: true, message: '请选择智能体', trigger: 'change' }
  ]
});
const handleClose = (done) => {
  ElMessage.warning('请先选择工作台.');
};

const handleCancel = () => {
  dialogVisible.value = false;
};

const selectTemplate = (id) => {

  // 如果item的状态是1，则提示
  if (workbenches.value.find(item => item.id === id).status === 0) {
    ElMessage.warning('该工作台Agent智能体正在集成中，尚未开放...');
    return;
  }

  selectedTemplateId.value = id;
  ruleForm.value.template = id;

  // 获取到item的名称
  ruleForm.value.name = workbenches.value.find(item => item.id === id).name;
};

const handleSubmit = () => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      // 这里可以添加提交表单数据的逻辑，比如发送请求到后端
      submitLoading.value = true;

      if(ruleForm.value.type === 1){ // 使用模板
          const data = {
            workplaceName: ruleForm.value.name,
            workplaceId: selectedTemplateId.value
          }

          useWorkplace(data).then(res => {
            ElMessage.success('表单提交成功');
            submitLoading.value = false;
            dialogVisible.value = false;

            emit("handleHasWorkplace")
          }).catch(() => {
            submitLoading.value = false;
          })
      }else if(ruleForm.value.type === 2){  // 自定义工作台
        customWorkplace(ruleForm.value).then(res => {
            ElMessage.success('表单提交成功');
            submitLoading.value = false;
            dialogVisible.value = false;

            emit("handleHasWorkplace")
          }).catch(() => {
            submitLoading.value = false;
          })
      }

    } else {
      // ElMessage.error('表单验证失败，请检查输入');
      return false;
    }
  });
}

const handleListOrgWorkplace = () => {
  loading.value = true;
  listOrgWorkplace().then(res => {
    workbenches.value = res.data;
    loading.value = false;
  })
}

const openSelectWorkplace = () => {
  loading.value = true;
  dialogVisible.value = true;
  handleListOrgWorkplace();
  loading.value = false;
}

// 获取智能体角色列表
const handleAgentStoreRole = async () => {
    await getAgentStoreRole().then(response => {
        publicRoleList.value = response.data.publicRoleList || [];
        orgRoleList.value = response.data.orgRoleList || [];
    })
}

// 获取场景列表
const handleScreenList = async () => {
    await sceneListByPage().then(res => {
        sceneLists.value = res.data
    })
}

// 切换工作台类型
const handleWorkplaceTypeChanne = (type) => {
  console.log('type = ' + type);
  if(type == 2){
    loading.value = true;
    handleAgentStoreRole();
    handleScreenList();
    loading.value = false;
  }
}

// 提供给父类使用
defineExpose({
  openSelectWorkplace
})

</script>

<style lang="scss" scoped>
.ellipsis-2lines {
  display: -webkit-box;
  font-size: 13px;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 45px;
}

.item-footer-panel {
  font-size: 13px;
  color: rgb(165, 165, 165);
  display: flex;
  justify-content: space-between;
  margin-right: 15px;
  margin-left: 10px;
  margin-bottom: 10px;

  span{
    line-height: 25px;
  }
}
</style>    