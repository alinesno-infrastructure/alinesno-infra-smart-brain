<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择AI工作台"
    width="1024"
    :before-close="handleClose"
  >
    <div>

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
        status-icon
      >
        <el-form-item label="工作台名称" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入工作台名称"  />
        </el-form-item>

        <el-form-item label="工作台模板" prop="template">
          <el-row>
            <el-col :span="6" v-for="(item, index) in workbenches" :key="index">
              <div
                :style="{
                  background: selectedTemplateId === item.id ? '#e5f6ff' : '#fff',
                  border: selectedTemplateId === item.id ? '1px solid #1890ff' : '1px solid rgba(6, 7, 9, 0.1)',
                  borderRadius: '5px',
                  cursor: 'pointer',
                  margin: '10px',
                  display: 'flex',
                  'flex-direction': 'column'
                }"
                @click="selectTemplate(item.id)"
              >
                <img :src="'http://data.linesno.com/icons/screen/'+(index+1)+'.webp'" style="width:100%" />
                <div style="display: flex;flex-direction: column;line-height: 1.4rem;gap: 5px;padding: 10px;">
                  <span
                    style="
                      font-size: 15px;
                      color: #333;
                      font-weight: bold;
                    "
                  >
                    {{ item.name }}
                    <el-button v-if="item.status === 0" type="text" size="small">
                      集成中 
                    </el-button>
                  </span>
                  <div
                    class="ellipsis-2lines"
                    style="
                      color: #a5a5a5;
                    "
                  >
                    {{ item.description }}
                  </div>
                </div>
                <!-- <div class="item-footer-panel" >
                  <span>
                    {{ item.team }} 
                  </span>
                  <span>
                    启用 {{ item.usageCount }} 
                  </span> 
                </div>
                  -->
              </div>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel" size="large">关闭</el-button>
        <el-button type="primary" text bg size="large" @click="handleSubmit">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const dialogVisible = ref(true);
const ruleFormRef = ref(null);
const ruleForm = ref({
  name: '',
  template: ''
});
const formSize = ref('large');
const selectedTemplateId = ref(null);

const workbenches = ref([
  {
    id: 99,
    name: "通用工作台",
    status: 1,
    description: "具备多种常用功能的通用型工作台，可用于不同场景下的任务管理、信息整合等操作",
    team: "来自AIP团队",
    usageCount: "8981"
  },
  {
    id: 1,
    name: "校园社团工作台",
    status: 1,
    description: "针对于校园社团快速AI化管理工作台，集成社团能力",
    team: "来自AIP团队",
    usageCount: "8981"
  },
  {
    id: 2,
    name: "产品运营工作台",
    status: 0,
    description: "助力电商商家高效运营的工作台，整合营销、订单管理等功能",
    team: "来自AIP团队",
    usageCount: "8981"
  },
  {
    id: 3,
    name: "企业办公工作台",
    status: 0,
    description: "为企业员工提供便捷办公服务的工作台，集成文档管理、流程审批等能力",
    team: "来自AIP团队",
    usageCount: "8981"
  },
  {
    id: 10,
    name: "媒体内容创作工作台",
    status: 0,
    description: "用于媒体从业者进行内容创作和发布的工作台，集成图文编辑、视频制作等能力",
    team: "来自AIP团队",
    usageCount: "8981"
  },
  {
    id: 11,
    name: "售前部门工作台",
    status: 0,
    description: "服务于售前部门的工作台，可进行客户需求分析、方案制定、报价管理等操作",
    team: "来自AIP团队",
    usageCount: "8981"
  }
]);    

const rules = ref({
  name: [
    { required: true, message: '请输入工作台名称', trigger: 'blur' },
    { min: 2, max: 20, message: '工作台名称长度应在2到20个字符之间', trigger: 'blur' }
  ],
  template: [
    { required: true, message: '请选择工作台模板', trigger: 'change' }
  ]
});

const handleClose = (done) => {
  dialogVisible.value = false;
  done();
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
};

const handleSubmit = () => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      // 这里可以添加提交表单数据的逻辑，比如发送请求到后端
      ElMessage.success('表单提交成功');
      dialogVisible.value = false;
    } else {
      // ElMessage.error('表单验证失败，请检查输入');
      return false;
    }
  });
};
</script>

<style lang="scss" scoped>
.ellipsis-2lines {
  display: -webkit-box;
  font-size: 13px;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
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