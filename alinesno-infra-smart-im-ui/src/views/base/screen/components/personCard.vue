<template>
  <el-card class="person-card" shadow="never">
    <el-row type="flex" justify="center" align="middle">
      <el-col :span="16" class="info-container">
        <p>{{ person.name }} &nbsp; {{ person.title }}</p>
        <el-row>
          <el-col :span="24">
            <div style="margin-bottom: 10px;">
              {{ person.skill }}
            </div>
          </el-col>
          <el-col :span="24">
            <el-link :underline="false" :href="person.emailLink" target="_blank">
              <el-icon><Message /></el-icon> {{ person.email }}
            </el-link>
          </el-col>
        </el-row>
      </el-col>
      <el-col :span="8" class="avatar-container">
        <el-avatar :size="100" :src="person.avatarSrc"></el-avatar>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>

// 定义人物信息
const person = reactive({
  avatarSrc: 'http://data.linesno.com/switch_header.png',
  name: '张三',
  title: '软件工程师',
  skill: '主要负责与客户进行沟通，解答客户关于产品的各种疑问，提供专业的咨询服务，以帮助客户更好地理解和使用产',
  email: 'zhangsan@example.com',
  phone: '123-456-7890'
});

// 计算属性
person.emailLink = computed(() => `mailto:${person.email}`);
person.phoneLink = computed(() => `tel:${person.phone}`);

function setPersonInfo(item){
  person.value = item ;
}

defineExpose({
  setPersonInfo
})

</script>

<style scoped lang="scss">
.person-card {
  margin: 0 auto;
  margin-bottom: 20px;

  .avatar-container {
    text-align: center;
  }

  .info-container {
    h2 {
      margin-top: 0;
    }

    p {
      margin: 5px 0 15px;
      font-size: 16px;
      color: #666;
    }

    .el-link {
      display: block;
      font-size: 14px;
      color: #409eff;

      .el-icon {
        margin-right: 5px;
      }
    }
  }
}
</style>