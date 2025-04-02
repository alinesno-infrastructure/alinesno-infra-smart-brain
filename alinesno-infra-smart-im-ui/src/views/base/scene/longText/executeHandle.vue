<template>
  <div>
    <el-dialog
      v-model="dialogVisible"
      width="1000px"
      title="Agent内容生成对话框"
      @close="handleClose"
    >
      <template #default>
        <div class="dialog-description">
          由多个智能体协作，请根据需求选择大纲生成智能体和章节工程师，并输入内容生成相关信息。
        </div>
        <el-form ref="formRef" :model="formData" :rules="rules" size="large" :label-position="'top'">
          <el-form-item label="选择大纲设计专员" prop="outlineEngineer">
            <el-scrollbar class="scrollbar">
              <div class="custom-radio-group" >
                <div
                  v-for="engineer in outlineEngineers"
                  :key="engineer.id"
                  class="custom-radio"
                  :class="{ 'selected': formData.outlineEngineer === engineer.id }"
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
          <el-form-item label="选择章节工程师" prop="chapterEngineer">
            <el-scrollbar class="scrollbar">
              <div class="custom-radio-group" >
                <div
                  v-for="engineer in chapterEngineers"
                  :key="engineer.id"
                  class="custom-radio"
                  :class="{ 'selected': formData.chapterEngineer === engineer.id }"
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
        <div class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false">取消</el-button>
          <el-button size="large" type="primary" @click="handleConfirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElDialog, ElButton, ElInput, ElForm, ElFormItem, ElScrollbar, ElUpload } from 'element-plus';
import { listAll } from '@/api/base/im/user';
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';

const agentList = ref([]);
const dialogVisible = ref(false);
const formRef = ref(null);

const outlineEngineers = ref([]);
const chapterEngineers = ref([]);

const formData = ref({
  outlineEngineer: null,
  chapterEngineer: null,
  contentInput: ''
});

const currentSceneInfo = ref({});
const uploadRef = ref();

const submitUpload = () => {
  uploadRef.value.submit();
};

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

const selectOutlineEngineer = (id) => {
  formData.value.outlineEngineer = id;
};

const selectChapterEngineer = (id) => {
  formData.value.chapterEngineer = id;
};

/** 根据场景id和类型获取到角色信息 */
const handleRoleBySceneIdAndAgentType = async () => {
  console.log('sceneId = ' + currentSceneInfo.value.sceneId + ' , agents = ' + currentSceneInfo.value.agents);

  if (currentSceneInfo.value.sceneId && currentSceneInfo.value.agents) {
    for (let i = 0; i < currentSceneInfo.value.agents.length; i++) {
      let item = currentSceneInfo.value.agents[i];

      if (item.code === 'chapterEditor') {
        const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
        console.log('chapterEditor response = ' + response);
        outlineEngineers.value = response.data;
      } else if (item.code === 'contentEditor') {
        const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
        console.log('contentEditor response = ' + response);
        chapterEngineers.value = response.data;
      }
    }
  }
};

/** 打开弹出窗口 */
const handleOpen = (sceneInfo) => {
  dialogVisible.value = true;
  currentSceneInfo.value = sceneInfo;
  handleRoleBySceneIdAndAgentType();
};

// nextTick(() => {
//   handleOpen();
// })

defineExpose({
  handleOpen
});
</script>

<style lang="scss" scoped>
$border-color: #4c4d4f;
$selected-border-color: #262727;
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
      background-color: $selected-bg-color;
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