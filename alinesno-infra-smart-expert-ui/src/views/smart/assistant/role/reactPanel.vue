<template>
  <div class="app-container">
     <el-page-header @back="goBack" :content="'['+ currentRole.roleName +']角色推理配置'"></el-page-header>
  </div>
</template>

<script setup>

const currentRole = ref({
    roleName:''
});
const currentRoleId = ref(null)

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId;
}

nextTick(() => {
    getRoleInfo();
})

</script>

<style scoped lang="scss">
</style>
