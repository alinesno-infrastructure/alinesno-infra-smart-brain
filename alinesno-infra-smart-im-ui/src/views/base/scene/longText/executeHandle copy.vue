<template>
  <div>
    <el-dialog
      v-model="dialogVisible"
      width="1000px"
      title="Agent内容生成对话框"
      @close="handleClose"
    >
      <template #default>

        <div style="padding: 20px 0px;padding-top: 10px;font-size: 15px;">
          由多个智能体协作，请根据需求选择大纲生成智能体和章节工程师，并输入内容生成相关信息。
        </div>

        <el-form ref="formRef" :model="formData" :rules="rules" size="large" :label-position="'top'">
          <el-form-item label="选择大纲设计专员" prop="outlineEngineer">
            <el-scrollbar height="150px">
            <el-radio-group v-model="formData.outlineEngineer">
              <el-radio
                v-for="engineer in outlineEngineers"
                border
                class="longtext-el-radio"
                :key="engineer.id"
                :value="engineer.id"
                :label="engineer.id"
              >
              <div style="display: flex;align-items: center;">

                <img
                  :src="imagePathByPath(engineer.roleAvatar)" 
                  alt="工程师头像"
                  style="width: 30px; height: 30px; border-radius: 50%; margin-right: 10px"
                />
                <div>

                <span>
                {{ engineer.roleName }}
                </span>
                </div>

              </div>
              </el-radio>
            </el-radio-group>
            </el-scrollbar>
          </el-form-item>
          <el-form-item label="选择章节工程师" prop="chapterEngineer">
            <el-scrollbar height="150px">
            <el-radio-group v-model="formData.chapterEngineer">
              <el-radio
                v-for="engineer in chapterEngineers"
                border
                class="longtext-el-radio"
                :key="engineer.id"
                :value="engineer.id"
                :label="engineer.id"
              >
              <div style="display: flex;align-items: center;">
                <img
                  :src="imagePathByPath(engineer.roleAvatar)" 
                  alt="工程师头像"
                  style="width: 30px; height: 30px; border-radius: 50%; margin-right: 10px"
                />
                {{ engineer.roleName }}
              </div>
              </el-radio>
            </el-radio-group>
            </el-scrollbar>
          </el-form-item>
          <el-form-item label="内容生成输入" prop="contentInput">
            <el-input v-model="formData.contentInput" placeholder="请输入内容生成的相关信息"></el-input>
          </el-form-item>
          <el-form-item label="内容上传" prop="contentInput">
            <el-upload
    ref="uploadRef"
    class="upload-demo"
    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
    :auto-upload="false"
  >
    <template #trigger>
      <el-button type="primary">选择参考文献</el-button>
    </template>

    <template #tip>
      <div class="el-upload__tip">
        jpg/png files with a size less than 500kb
      </div>
    </template>
  </el-upload>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <div style="margin-bottom:20px;">
          <el-button size="large" @click="dialogVisible = false">取消</el-button>
          <el-button size="large" type="primary" @click="handleConfirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElDialog, ElRadioGroup, ElRadio, ElButton, ElInput, ElForm, ElFormItem } from 'element-plus';
import { listAll } from '@/api/base/im/user';
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';

const agentList = ref([])
const dialogVisible = ref(false);
const formRef = ref(null);

const outlineEngineers = ref([]);
const chapterEngineers = ref([]);

const formData = ref({
  outlineEngineer: null,
  chapterEngineer: null,
  contentInput: ''
});

const currentSceneInfo = ref({})
const uploadRef = ref()

const submitUpload = () => {
  uploadRef.value.submit()
}

const rules = ref({
  outlineEngineer: [
    {
      validator: (rule, value, callback) => {
        if (value === null) {
          callback(new Error('请选择一名大纲设计工程师'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  chapterEngineer: [
    {
      validator: (rule, value, callback) => {
        if (value === null) {
          callback(new Error('请选择一名章节工程师'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  contentInput: [
    {
      validator: (rule, value, callback) => {
        if (value.trim() === '') {
          callback(new Error('内容生成输入不能为空'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

const handleClose = () => {
  console.log('对话框已关闭');
};

const handleConfirm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      console.log('点击了确定按钮');
      console.log('选中的大纲设计工程师:', formData.value.outlineEngineer);
      console.log('选中的章节工程师:', formData.value.chapterEngineer);
      console.log('内容生成输入:', formData.value.contentInput);
      dialogVisible.value = false;
    } else {
      console.log('表单校验不通过');
    }
  });
};

/** 根据场景id和类型获取到角色信息 */
const handleRoleBySceneIdAndAgentType = async () => {
  console.log('sceneId = ' + currentSceneInfo.value.sceneId + ' , agents = ' + currentSceneInfo.value.agents) ;

  if(currentSceneInfo.value.sceneId && currentSceneInfo.value.agents){

    for(let i = 0; i < currentSceneInfo.value.agents.length; i++){

      let item = currentSceneInfo.value.agents[i]

      if(item.code === 'chapterEditor'){

        const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id) ; 
        console.log('chapterEditor response = ' + response)
        outlineEngineers.value = response.data ; 

      }else if(item.code === 'contentEditor'){

        const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id) ; 
        console.log('contentEditor response = ' + response)
        chapterEngineers.value = response.data ; 

      }

    }
  }

}

/** 打开弹出窗口 */
const handleOpen = (sceneInfo) => {
    dialogVisible.value = true ;
    currentSceneInfo.value = sceneInfo ;
    // handleListAllRole() ; 
    handleRoleBySceneIdAndAgentType() ;
}

// nextTick(() => {
//   handleOpen() ;
// })

defineExpose({
  handleOpen
})

</script>

<style lang="scss" scoped>
/* 这里可以添加自定义样式 */
.longtext-el-radio{
  width: calc(33% - 30px);
  margin-bottom: 10px;
  margin-right: 15px;
  margin-left: 15px;
}
</style>    