<template>

  <div class="app-container">

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
                          <!--
                          <router-link to="/smart/assistant/roleTemplate/add">
                                <el-button type="primary" bg text icon="Plus" @click="addTemplates()" size="mini">添加模板</el-button>
                          </router-link>
                          -->
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

// const cancel = () => {
//   open.value = false;
//   reset();
// };

// const reset = () => {
//   Object.assign(ruleForm, {
//     id: null,
//     screen: null,
//     industry: null,
//     type: null,
//     roleName: null,
//     tempTeam: null,
//     tempZip: null,
//     responsibilities: null
//   });
//   // 假设有一个resetForm的方法在全局或组件内，这里调用它
//   // resetForm("form");
// };

// const handleQuery = () => {
//   queryParams.pageNum = 1;
//   getList();
// };

// const resetQuery = () => {
//   // resetForm("queryForm");
//   handleQuery();
// };

// const handleSelectionChange = (selection) => {
//   ids.value = selection.map(item => item.id);
//   single.value = selection.length !== 1;
//   multiple.value = !selection.length;
// };

// const handleAdd = () => {
//   reset();
//   open.value = true;
//   title.value = "添加数据库类型";
// };

// const handleGenCode = (row) => {
//   generatorFormVisible.value = true;
//   selectDbId.value = row.id;
//   Object.assign(genForm, {
//     author: row.author,
//     createType: row.createType,
//     packagePath: row.packagePath,
//     moduleName: row.moduleName
//   });
// };

// const handleUpdate = async (row) => {
//   reset();
//   const id = row.id || ids.value;
//   // 注意：这里使用的getCasLoginRecord可能是一个错误，因为它与上下文不符。请替换为正确的API调用。
//   const response = await getCasLoginRecord(id);
//   btnChangeEnable.value = false;
//   Object.assign(ruleForm, response.data);

//   open.value = true;
//   title.value = "修改数据库类型";
// };

// const submitGenForm = () => {
//   // 实现提交逻辑
// };

// const validateDburl = () => {
//   // $refs["form"]需要被重构为Composition API风格
//   // 这里假设有一个validate方法可以直接调用
//   // form.validate(valid => {
//   //   ...
//   // });
// };

// const submitForm = () => {
//   // $refs["ruleForm"].validate需要被重构为Composition API风格
//   // 这里假设有一个validate方法可以直接调用
//   // ruleForm.validate(valid => {
//   //   ...
//   // });
// };

// const addPlatformField = (row) => {
//   ElMessage('功能正在完善中.');

//   // ... 同步逻辑省略...
// };

// const handleSynchDb = (row) => {
//   // ... 同步逻辑省略...
// };

// const handleDelete = async (row) => {
//   const idsToDelete = row.id || ids.value;
//   await ElMessageBox.confirm('是否确认删除数据库类型的数据项?', "警告", {
//     confirmButtonText: "确定",
//     cancelButtonText: "取消",
//     type: "warning"
//   }).then(async () => {
//     // delCasLoginRecord应该被替换成实际的删除API
//     await delCasLoginRecord(idsToDelete);
//     await getList();
//     ElMessage.success("删除成功");
//   }).catch(() => {
//     ElMessage.info("已取消删除");
//   });
// };

// const handleExport = async () => {
//   await ElMessageBox.confirm('是否确认导出所有数据库类型数据项?', "警告", {
//     confirmButtonText: "确定",
//     cancelButtonText: "取消",
//     type: "warning"
//   }).then(async () => {
//     exportLoading.value = true;
//     const response = await exportCasLoginRecord(queryParams);
//     download(response.msg); // 假设有download函数可以处理下载逻辑
//     exportLoading.value = false;
//   }).catch(() => {
//     ElMessage.info("已取消导出");
//   });
// };

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

</style>


