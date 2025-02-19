<template>

  <div class="app-container">

        <div class="page-header-container">
            <el-page-header @back="goBack">
                <template #content>
                    <div style="display: flex;gap: 10px;align-items: center;">
                    示例模板
                    </div>
                </template>
            </el-page-header>
        </div>

      <!-- 模板选择 -->
      <div class="template-header">
          <div class="vc-div div_l14lqa17 top-container" v-loading="loadingFilter">
              <div class="vc-div div_l14lqa16">
                  <div style="padding-bottom: 20px;font-weight: 550;">
                      <i class="el-icon-link"></i> 角色模板中心
                      <span style="font-size:13px;color:#a5a5a5">
                          角色模板会给出最新的角色示例和最佳实践
                      </span>
                      <div style="float:right">
                          <el-button type="primary" :disabled="loading" bg text icon="Refresh" @click="handleSyncTemplates()" size="mini">同步</el-button>
                      </div> 
                  </div>
                  <div class="vc-div div_l14lqa15">
                      <div class="vc-div div_l14lqa0k" v-for="(item, index) in filterRules"
                          :key="index">
                          <div class="vc-div div_l14lqa0h">
                              <div class="vc-div div_l14lqa0e tagMain">
                                  <div class="vc-text" title="">
                                      {{ item.name }}
                                  </div>
                              </div>
                              <div class="vc-div div_l14lqa0g">
                                  <div class="vc-div div_l14lqa0f tagNormal" v-for="(i, ind) in item.items"
                                      :key="ind">
                                      <div class="vc-text" title="">
                                          {{ i.name }}
                                      </div>
                                  </div>
                              </div>
                          </div>
                      </div>

                  </div>
              </div>
          </div>
      </div>

      <div class="gen-template-box" v-if="typeList.size == 0">
          <el-col :sm="24">
              <el-empty description="还没有创建模板,可以根据提示链接创建自己的工程模板">
                  <el-link type="primary" icon="el-icon-link">如何创建工程模板?</el-link>
              </el-empty>
          </el-col>
      </div>

      <!-- 模板内容 -->
      <div class="vc-div div_l14lqa1k tpl-container" v-loading="loading || loadingFilter">

          <div class="vc-div div_l14lqa1j tpl-item" v-for="(item, index) in typeList" :key="index">
              <div class="vc-div div_l14lqa1i">
                  <div class="vc-div div_l14lqa1c tpl-item-title">
                      <div class="vc-text text_l14lqa1a" style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 1;">
                         <i class="fa-solid fa-signature" style="font-size: 30px; color: rgb(211, 50, 51);"></i>
                          {{ item.roleName }}
                      </div>
                  </div>
                  <div class="vc-div tpl-item-description">
                      <div class="vc-text text_l14lqa1d" style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 2;">
                          {{ item.responsibilities }}
                      </div>
                  </div>
                  <div class="vc-div div_l14lqa1f tpl-item-tags">
                      <div style="font-size: 14px;color: #a9a9a9;padding: 5px 0px;">
                         <i class="el-icon-link"></i> 使用: <b>142</b>次
                      </div>
                  </div> 
                  <div class="vc-div tpl-item-footer">
                      <div class="vc-text text_l14lqa1g" :title="item.tempTeam">
                        <el-popconfirm width="170" @confirm="handleUseTemplate(item.id)" title="确定使用模板么?">
                            <template #reference>
                                <el-button text bg type="primary">使用模板</el-button>
                            </template>
                        </el-popconfirm>
                      </div>
                      <div class="vc-text" title="">
                          已启用 {{ item.installCount }}
                      </div>
                  </div>
              </div>
          </div>

      </div>

      <div style="margin-bottom: 0px;">
        <pagination v-show="total > 0" :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"/>
      </div>

  </div>

</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {
  listBuildProjectTemplate,
  getFilterTemplate,
  syncTemplates,
  useTemplate // 确保这个API是存在的，因为原始代码中并没有展示它的用途。
} from "@/api/smart/assistant/roleTemplate";
import { ElMessage } from 'element-plus';

const router = useRouter();

// 使用ref定义简单的响应式数据
// const selectDbId = ref('');
const loading = ref(true);
const loadingFilter = ref(true);
// const openProject = ref(false);
// const exportLoading = ref(false);
// const currentTemplateId = ref('');
// const ids = ref([]);
// const single = ref(true);
// const multiple = ref(true);
// const showSearch = ref(true);
const total = ref(0);
const typeList = ref([]);
// const title = ref("");
// const open = ref(false);
// const generatorFormVisible = ref(false);
// const pageSizes = ref([12, 24, 36, 60]);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 12
});
// const ruleForm = reactive({
//   screen: '',
//   industry: '',
//   type: '',
//   roleName: '',
//   tempTeam: '',
//   tempZip: '',
//   responsibilities: ''
// });

// const btnChangeEnable = ref(true);
// const genForm = reactive({
//   packagePath: '',
//   moduleName: '',
//   author: '',
//   createType: 'front'
// });

const filterRules = ref([
  {
    name: "场景",
    codeValue: "initializr.admin.project.template.screen",
    items: []
  },
  {
    name: "行业",
    codeValue: "initializr.admin.project.template.industry",
    items: []
  }
]);

// 表单校验规则
// const rules = reactive({
//   screen: { required: true, message: '请输入场景', trigger: 'blur' },
//   industry: { required: true, message: '请输入行业', trigger: 'blur' },
//   type: { required: true, message: '请选择类型', trigger: 'blur' },
//   roleName: { required: true, message: '请输入模板名称', trigger: 'blur' },
//   tempTeam: { required: true, message: '请输入团队信息', trigger: 'blur' },
//   tempZip: { required: true, message: '请上传模板', trigger: 'blur' },
//   responsibilities: { required: true, message: '请输入模板信息', trigger: 'blur' }
// });

// 定义方法
// const installTemplate = (item) => {
//   if (item.fieldProp === 'dev') {
//     ElMessage('还在建设开发中.');
//     return;
//   }

//   openProject.value = true;
//   currentTemplateId.value = item.id;
// };

const handleSyncTemplates= () => {
  loading.value = true;
  syncTemplates().then(() => {
    getList();
  }).finally(() => {
    loading.value = false;
  });
};

const handleUseTemplate = (id) => {
  loading.value = true;
  useTemplate(id).then((res) => {
    getList();
    ElMessage({
      message: '使用成功',
      type: 'success'
    });
    router.push({path:"/expert/smart/assistant/role/index"})
  }).finally(() => {
    loading.value = false;
  });
};

const getList = async () => {
  try {
    loading.value = true;
    const response = await listBuildProjectTemplate(queryParams);
    typeList.value = response.rows;
    total.value = response.total;
  } catch (err) {
    console.error(err);
  } finally {
    loading.value = false;
  }
};

/** 返回 */
function goBack() {
  router.back();
}

// 生命周期钩子
onMounted(() => {
  getList();

  getFilterTemplate().then(res => {
    filterRules.value = res.data;
    loadingFilter.value = false;
  });
});
</script>


<style scoped lang="scss">

.gen-template-box {
  width: 100%;
  text-align: center;
  float: left;
  margin-top: 100px;
}

.vc-text.text_l14lqa1e.active {
  color: #fff;
  background: #005bd5;
}

.page-header-container{
  margin-bottom: 20px;
}

</style>


