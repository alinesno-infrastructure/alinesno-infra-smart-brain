<template>
  <div>
    <!-- 视频配置抽屉 -->
    <el-drawer v-model="dialog" 
        size="40%" 
        title="视频全局配置"
        :before-close="handleClose"
        direction="rtl"
        class="demo-drawer">

      <div class="demo-drawer__content">
        <!-- 表单 -->
        <el-form :model="form" label-width="80px" class="custom-form">
          <!-- 多个视频标题 -->
          <el-row :gutter="20">
            <el-col :span="24">
              <div v-for="(title, index) in form.titles" :key="index" style="margin-bottom: 16px;">
                <el-form-item :label="index === 0 ? '视频标题' : ''">
                  <el-input v-model="form.titles[index]" style="width:calc(100% - 70px)" placeholder="请输入视频标题" />
                  <el-button type="danger" @click="removeTitle(index)" style="margin-left: 8px;" :disabled="form.titles.length <= 1">删除</el-button>
                </el-form-item>
              </div>
              <div style="margin-bottom:20px;text-align: right;">
                <el-button type="primary" icon="Plus" @click="addTitle">添加标题文案</el-button>
              </div>
            </el-col>
          </el-row>

          <!-- 视频描述 -->
          <el-form-item label="视频描述">
            <el-input v-model="form.description" type="textarea" :rows="3" resize="none" placeholder="请输入视频描述" />
          </el-form-item>

          <!-- 素材分类选择 -->
          <el-form-item label="素材分类">
            <el-select v-model="form.category" placeholder="请选择素材分类">
              <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>

          <!-- 口播模式选择 -->
          <el-form-item label="口播模式">
            <el-radio-group v-model="form.speechMode" @change="onSpeechModeChange">
              <el-radio label="global">全局口播模式</el-radio>
              <el-radio label="grouped">分组口播模式</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 全局口播文案 -->
          <el-row :gutter="20" v-if="form.speechMode === 'global'" style="margin-bottom: 16px;">
            <el-col :span="24">
              <div v-for="(speechText, index) in form.speechTextArray" :key="index" style="margin-bottom: 16px;">
                <el-form-item :label="index === 0 ? '口播文案' : ''">
                  <el-input v-model="form.speechTextArray[index]" style="width:calc(100% - 70px)" resize="none" type="textarea" :rows="1" placeholder="请输入口播文案" />
                  <el-button type="danger" @click="removeSpeechText(index)" style="margin-left: 8px;" :disabled="form.speechTextArray.length <= 1">删除</el-button>
                </el-form-item>
              </div>
              <div style="margin-bottom:20px;text-align: right;">
                <el-button type="primary" icon="Plus" @click="addSpeechText">添加口播文案</el-button>
              </div>
            </el-col>
          </el-row>

          <!-- 提交按钮 -->
          <div class="demo-drawer__footer">
            <el-button @click="cancelForm">取消</el-button>
            <el-button type="primary" :loading="loading" @click="onSubmit">
              {{ loading ? '提交中...' : '提交' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

// 抽屉显示状态
const dialog = ref(false)
const loading = ref(false)

// 表单数据
const form = reactive({
  titles: [''], // 初始化时至少有一个空标题
  description: '',
  category: '',
  speechMode: 'global', // 默认选择全局口播模式
  speechTextArray: [''] // 初始化时至少有一个空口播文案
})

// 素材分类选项
const categories = [
  { value: 'cityscape', label: '城市风貌' },
  { value: 'history', label: '历史底蕴' },
  { value: 'entertainment', label: '吃喝玩乐' }
]

// 关闭抽屉时触发
const handleClose = (done) => {
  if (loading.value) {
    return
  }
  dialog.value = false
}

// 打开配置面板
const openGlobalConfigPanel = () => {
  dialog.value = true
}

// 定义暴露的方法
defineExpose({
  openGlobalConfigPanel
})

// 添加新的标题输入框
const addTitle = () => {
  form.titles.push('')
}

// 删除指定索引的标题
const removeTitle = (index) => {
  form.titles.splice(index, 1)
}

// 添加新的口播文案输入框
const addSpeechText = () => {
  form.speechTextArray.push('')
}

// 删除指定索引的口播文案
const removeSpeechText = (index) => {
  form.speechTextArray.splice(index, 1)
}

// 当口播模式改变时重置相关字段
const onSpeechModeChange = (mode) => {
  if (mode !== 'global') {
    form.speechTextArray = ['']
  }
}

// 提交表单
const onSubmit = () => {
  // 模拟提交行为
  loading.value = true
  setTimeout(() => {
    loading.value = false
    dialog.value = false
    console.log('提交成功:', form)
  }, 2000)
}

// 取消表单
const cancelForm = () => {
  dialog.value = false
}
</script>

<style scoped>
.custom-form .el-form-item {
  margin-bottom: 20px;
}

/* .custom-form .el-button {
  margin-top: 8px;
} */

/* 调整添加按钮位置 */
.custom-form .el-button--primary {
  margin-right: 0;
  /* width: 100%; */
}

/* 调整删除按钮样式 */
.custom-form .el-button--danger {
  padding: 9px 15px;
}

.demo-drawer__footer {
  text-align: right;
  margin-top: 20px;
}
</style>