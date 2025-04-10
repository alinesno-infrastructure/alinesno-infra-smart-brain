<template>
  <div>
    <el-dialog
      v-model="dialogVisible"
      width="800px"
      title="请选择智能体"
      @close="handleClose"
    >
      <template #default>
        <div class="dialog-description">
          由多个智能体协作，请根据需求选择大纲生成智能体和章节工程师，并输入内容生成相关信息。
        </div>
        <el-form ref="formRef" :model="formData" :rules="rules" size="large" :label-position="'top'">

          <!-- 文档分析助手 -->
          <el-form-item label="选择文档分析专员" prop="analysisAgentEngineer">
            <el-scrollbar class="scrollbar">
              <div class="custom-radio-group" >
                <div
                  v-for="engineer in analysisAgentEngineers"
                  :key="engineer.id"
                  class="custom-radio"
                  :class="{ 'selected': formData.analysisAgentEngineer === engineer.id }"
                  @click="selectOutlineEngineer(engineer.id)"
                >
                  <div class="engineer-info">
                    <img
                      :src="imagePathByPath(engineer.roleAvatar)"
                      alt="工程师头像"
                      class="engineer-avatar"
                    />
                    <div class="engineer-details">
                      <span class="engineer-name">
                        {{ engineer.roleName }}
                      </span>
                      <span class="engineer-responsibilities">
                        {{ engineer.responsibilities }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </el-form-item>


          <!-- 文档审核助手 -->
          <el-form-item label="选择文档审核专员" prop="logicReviewerEngineer">
            <el-scrollbar class="scrollbar">
              <div class="custom-radio-group" >
                <div
                  v-for="engineer in logicReviewerEngineers"
                  :key="engineer.id"
                  class="custom-radio"
                  :class="{ 'selected': formData.logicReviewerEngineer === engineer.id }"
                  @click="selectChapterEngineer(engineer.id)"
                >
                  <div class="engineer-info">
                    <img
                      :src="imagePathByPath(engineer.roleAvatar)"
                      alt="工程师头像"
                      class="engineer-avatar"
                    />
                    <div class="engineer-details">
                      <span class="engineer-name">
                        {{ engineer.roleName }}
                      </span>
                      <span class="engineer-responsibilities">
                        {{ engineer.responsibilities }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </el-form-item>

        </el-form>
      </template>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false">取消</el-button>
          <el-button size="large" type="primary" @click="handleConfirm">确认选择</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';

// import { ElDialog, ElButton, ElInput, ElForm, ElFormItem, ElScrollbar, ElUpload } from 'element-plus';
// import { listAll } from '@/api/base/im/user';

import { initAgents } from '@/api/base/im/scene/documentReviewSceneInfo';

const emit = defineEmits(['openChatBox' , 'handleGetScene']) ; 

// const agentList = ref([]);
const dialogVisible = ref(false);
const formRef = ref(null);

const analysisAgentEngineers = ref([]);
const logicReviewerEngineers = ref([]);

const formData = ref({
  analysisAgentEngineer: null,
  logicReviewerEngineer: null,
  contentInput: ''
});

const currentSceneInfo = ref({});
const uploadRef = ref();

const submitUpload = () => {
  uploadRef.value.submit();
};

const rules = ref({
  analysisAgentEngineer: [
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
  logicReviewerEngineer: [
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

const handleConfirm = async() => {

  const valid = await new Promise((resolve) => {
      formRef.value.validate((valid) => {
          resolve(valid);
      });
  });

  if (valid) {
      console.log('点击了确定按钮');
      console.log('选中的大纲设计工程师:', formData.value.analysisAgentEngineer);
      console.log('选中的章节工程师:', formData.value.logicReviewerEngineer);
      console.log('内容生成输入:', formData.value.contentInput);
      dialogVisible.value = false;

      formData.value.sceneId = currentSceneInfo.value.id ;
      
      const response = await initAgents(formData.value) ;
      console.log('response = ' + response)

      emit('handleGetScene')
      // emit('openChatBox' , formData.value.analysisAgentEngineer , formData.value.contentInput)

  } else {
      console.log('表单校验不通过');
  }

}

const selectOutlineEngineer = (id) => {
  formData.value.analysisAgentEngineer = id;
};

const selectChapterEngineer = (id) => {
  formData.value.logicReviewerEngineer = id;
};

// /** 根据场景id和类型获取到角色信息 */
// const handleRoleBySceneIdAndAgentType = async () => {
//   console.log('sceneId = ' + currentSceneInfo.value.sceneId + ' , agents = ' + currentSceneInfo.value.agents);

//   if (currentSceneInfo.value.sceneId && currentSceneInfo.value.agents) {
//     for (let i = 0; i < currentSceneInfo.value.agents.length; i++) {
//       let item = currentSceneInfo.value.agents[i];

//       const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
//       console.log('chapterEditor response = ' + response.data);

//       if (item.code === 'analysisAgent') {
//         const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
//         console.log('analysisAgent response = ' + response);
//         analysisAgentEngineers.value = response.data;
//       } else if (item.code === 'logicReviewer') {
//         const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
//         console.log('logicReviewer response = ' + response);
//         logicReviewerEngineers.value = response.data;
//       }

//     }
//   }
// };

/** 打开弹出窗口 */
const handleOpen = (sceneInfo , analysisAgentEngineersVal , logicReviewerEngineersVal) => {
  dialogVisible.value = true;
  currentSceneInfo.value = sceneInfo;
  // handleRoleBySceneIdAndAgentType();

  analysisAgentEngineers.value = analysisAgentEngineersVal ;
  logicReviewerEngineers.value = logicReviewerEngineersVal ;
};

// nextTick(() => {
//   handleOpen();
// })

defineExpose({
  handleOpen
});
</script>

<style lang="scss" scoped>
$border-color: rgb(233, 235, 242) ; // #4c4d4f;
$selected-border-color: #f5f5f5;
$selected-bg-color: #262727;

.dialog-description {
  padding: 20px 0px;
  padding-top: 10px;
  font-size: 15px;
}

.scrollbar {
  max-height: 150px;
  width: 100%;
}

.custom-radio-group {
  display: flex;
  gap: 10px;
  width: 100%;
  justify-content: flex-start;
  flex-direction: row;

  .custom-radio {
    cursor: pointer;
    padding: 10px;
    border: 1px solid $border-color;
    border-radius: 5px;
    margin-bottom: 10px;
    width: 33%;

    &.selected {
      border-color: $selected-border-color;
      background-color: $selected-border-color;
    }

    .engineer-info {
      display: flex;
      align-items: center;

      .engineer-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        margin-right: 10px;
      }

      .engineer-details {
        display: flex;
        flex-direction: column;
        line-height: 25px;

        .engineer-name {
          font-size: 15px;
          font-weight: bold;
        }

        .engineer-responsibilities {
          font-size: 13px;
          color: rgb(85, 85, 85);
          line-height: 20px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
}

.dialog-footer {
  margin-bottom: 20px;
}
</style>    