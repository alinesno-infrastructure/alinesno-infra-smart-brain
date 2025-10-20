<template>
  <div class="dataset-edit-container">
    <el-row>
      <el-col :span="19">
        <DocumentList :currentDatasetGroup="currentDatasetGroup" />
      </el-col>
      <el-col :span="5">
        <EditRightPanel :currentDatasetGroup="currentDatasetGroup" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>

import EditRightPanel from './editRightPanel.vue';
import DocumentList from './documentList.vue';

// 导入API
import {
    getGroupById,
} from '@/api/base/im/scene/productResearchDataset';

// 获取路由实例
const { proxy } = getCurrentInstance();
const router = useRouter()
const route = useRoute()

// 获取到groupId 
const groupId = ref(route.query.id) ;
const currentDatasetGroup = ref(null)

const handleGetGroupById = () => {
  getGroupById(groupId.value).then(res => {
    console.log(res)
    currentDatasetGroup.value = res.data ;
  });
}

handleGetGroupById() ;

</script>

<style scoped lang="scss">
.dataset-edit-container {
    padding: 10px;
    background: #fafafa;
    height: calc(100vh - 50px);
}
</style>