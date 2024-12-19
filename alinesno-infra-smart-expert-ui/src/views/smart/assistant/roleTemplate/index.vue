<template>

  <div class="app-container">

      <!-- 模板选择 -->
      <div class="template-header">
          <div class="vc-div div_l14lqa17 top-container" v-loading="loadingFilter">
              <div class="vc-div div_l14lqa16">
                  <div style="padding-bottom: 20px;font-weight: 550;">
                      <i class="el-icon-link"></i> 角色模板中心
                      <span style="font-size:13px;color:#a5a5a5">
                          角色模板中心会自动上传到你的项目git基线
                      </span>
                      <div style="float:right">
                          <el-button type="primary" bg text icon="Refresh" @click="syncTemplates()" size="mini">同步</el-button>
                      </div>
                  </div>
                  <div class="vc-div div_l14lqa15">
                      <div class="vc-div div_l14lqa0k" style="margin-bottom:10px" v-for="(item, index) in filterRules"
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

      <div class="gen-template-box" v-if="typeList.length == 0">
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
                         <i class="fa-solid fa-signature" style="font-size: 30px; color: rgb(211, 50, 51);"></i> {{ item.roleName }}
                      </div>
                  </div>
                  <div class="vc-div tpl-item-description">
                      <div class="vc-text text_l14lqa1d" style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 2;">
                          {{ item.responsibilities }}
                      </div>
                  </div>
                  <div class="vc-div div_l14lqa1f tpl-item-tags">
                      <el-button type="danger" text>
                         <i class="el-icon-link"></i> 工龄: <b>42</b>工时 
                      </el-button>
                  </div> 
                  <div class="vc-div tpl-item-footer">
                      <div class="vc-text text_l14lqa1g" :title="item.tempTeam">
                        <el-button text bg type="primary">使用模板</el-button>
                      </div>
                      <div class="vc-text" title="">
                          已启用 {{ item.installCount }}
                      </div>
                  </div>
              </div>
          </div>

      </div>

      <div style="margin-bottom: 60px;">
        <pagination v-show="total > 0" :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"/>
      </div>

  </div>

</template>

<script>
import {
  listBuildProjectTemplate,
  getFilterTemplate ,
  syncTemplates
} from "@/api/smart/assistant/roleTemplate";

export default {
  name: "Type",
  // components: {
  //     projectTemplate
  // },
  data() {
      return {
          // 选中数据库ID
          selectDbId: '',
          // 遮罩层
          loading: true,
          // 查询条件
          loadingFilter: true,
          // 是否显示应用
          openProject: false ,
          // 导出遮罩层
          exportLoading: false,
          // 当前选择的模板
          currentTemplateId: '' ,
          // 选中数组
          ids: [],
          // 非单个禁用
          single: true,
          // 非多个禁用
          multiple: true,
          // 显示搜索条件
          showSearch: true,
          // 总条数
          total: 0,
          // 数据库类型表格数据
          typeList: [],
          // 弹出层标题
          title: "",
          // 是否显示弹出层
          open: false,
          // 构建信息表单
          generatorFormVisible: false,
          // 重新定时pageSizes
          pageSizes:[12, 24, 36, 60] ,
          // 查询参数
          queryParams: {
              pageNum: 1,
              pageSize: 12
          },
          // 表单参数
          ruleForm: {
              screen: '',
              industry: '',
              type: '',
              roleName: '',
              tempTeam: '',
              tempZip: '',
              responsibilities: ''
          },
          // 验证是否通过
          btnChangeEnable: true,
          genForm: {
              packagePath: '',
              moduleName: '',
              author: '',
              createType: 'front'
          },
          filterRules: [
              {
                  "name": "场景",
                  "codeValue": "initializr.admin.project.template.screen",
                  "items": []
              },
              {
                  "name": "行业",
                  "codeValue": "initializr.admin.project.template.industry",
                  "items": []
              }
          ],
          // 表单校验
          rules: {
              screen: { required: true, message: '请输入场景', trigger: 'blur' },
              industry: { required: true, message: '请输入行业', trigger: 'blur' },
              type: { required: true, message: '请选择类型', trigger: 'blur' },
              roleName: { required: true, message: '请输入模板名称', trigger: 'blur' },
              tempTeam: { required: true, message: '请输入团队信息', trigger: 'blur' },
              tempZip: { required: true, message: '请上传模板', trigger: 'blur' },
              responsibilities: { required: true, message: '请输入模板信息', trigger: 'blur' },
          }
      }
          ;
  },
  created() {
      this.getList();

      getFilterTemplate().then(res => {
          this.filterRules = res.data;
          this.loadingFilter = false;
      })
  },
  methods: {
      // 安装模板
      installTemplate(item){
          if(item.fieldProp === 'dev'){
              this.$message('还在建设开发中.');
              return ;
          }

          this.openProject = true ;
          this.currentTemplateId = item.id ;
      },
      // 同步仓库
      syncTemplates(){
          this.loading = true ;
          syncTemplates().then(res => {
              console.log('res = ' + res) ;
              this.getList() ;
          })
      } ,
      /** 查询数据库类型列表 */
      getList() {
          this.loading = true;
          listBuildProjectTemplate(this.queryParams).then(response => {
              this.typeList = response.rows;
              this.total = response.total;
              this.loading = false;
          }).catch(err => {
              this.loading = false;
          });
      },
      // 取消按钮
      cancel() {
          this.open = false;
          this.reset();
      },
      // 表单重置
      reset() {
          this.ruleForm = {
              id: null ,
              screen: null ,
              industry: null ,
              type: null ,
              roleName: null,
              tempTeam: null,
              tempZip: null,
              responsibilities: null
          };
          this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
          this.queryParams.pageNum = 1;
          this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
          this.resetForm("queryForm");
          this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
          this.ids = selection.map(item => item.id)
          this.single = selection.length !== 1
          this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
          this.reset();
          this.open = true;
          this.title = "添加数据库类型";
      },
      // 数据库生成代码
      handleGenCode(row) {
          this.generatorFormVisible = true
          this.selectDbId = row.id
          this.genForm.author = row.author;
          this.genForm.createType = row.createType;
          this.genForm.packagePath = row.packagePath;
          this.genForm.moduleName = row.moduleName;
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
          this.reset();
          const id = row.id || this.ids
          getCasLoginRecord(id).then(response => {
              this.btnChangeEnable = false;
              this.form = response.data;

              this.open = true;
              this.title = "修改数据库类型";
          });
      },
      /** 提交数据库代码生成按钮 */
      submitGenForm() {
      },
      // 验证数据库
      validateDburl() {
          this.$refs["form"].validate(valid => {
              if (valid) {

                  if (!this.form.dbType) {
                      this.form.dbType = 'MySQL';
                  }

                  checkDbConfig(this.form).then(resp => {
                      if (resp.data.accepted) {
                          this.msgSuccess("数据库连接成功");
                          this.btnChangeEnable = false;
                      } else {
                          this.msgError(resp.msg)
                      }
                  })

              }
          });
      },
      /** 提交按钮 */
      submitForm() {
          this.$refs["ruleForm"].validate(valid => {
              if (valid) {

              }
          });
      },
      /** 添加平台字段 */
      addPlatformField(row) {

          this.$message('功能正在完善中.');

          return;

          const dbName = row.dbName;
          const id = row.id;

          this.$confirm('确认要添加平台字段到"' + dbName + '"库吗？', "提示", {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
          }).then(() => {
              const loading = this.$loading({
                  lock: true,
                  text: '正在同步中,大概1分钟,请稍等...',
                  spinner: 'el-icon-loading',
                  background: 'rgba(0, 0, 0, 0.7)'
              });
              platformFieldTODb(id).then(response => {
                  loading.close();
                  this.$message({
                      type: 'success',
                      message: '同步成功!'
                  });
              });
          }).catch(() => {
              loading.close();
              this.$message({
                  type: 'info',
                  message: '已取消同步'
              });
          });

      },
      /** 同步数据库操作 */
      handleSynchDb(row) {

          const dbName = row.dbName;
          const id = row.id;

          this.$confirm('确认要强制同步"' + dbName + '"表结构吗？', "提示", {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
          }).then(() => {
              const loading = this.$loading({
                  lock: true,
                  text: '正在同步中,大概1分钟,请稍等...',
                  spinner: 'el-icon-loading',
                  background: 'rgba(0, 0, 0, 0.7)'
              });
              syncDataBase(id).then(response => {
                  loading.close();
                  this.$message({
                      type: 'success',
                      message: '同步成功!'
                  });
              });
          }).catch(() => {
              debugger
              loading.close();
              this.$message({
                  type: 'info',
                  message: '已取消同步'
              });
          });

      },
      /** 删除按钮操作 */
      handleDelete(row) {
          const ids = row.id || this.ids;
          this.$confirm('是否确认删除数据库类型的数据项?', "警告", {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning"
          }).then(function () {
              return delCasLoginRecord(ids);
          }).then(() => {
              this.getList();
              this.msgSuccess("删除成功");
          })
      },
      /** 导出按钮操作 */
      handleExport() {
          const queryParams = this.queryParams;
          this.$confirm('是否确认导出所有数据库类型数据项?', "警告", {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning"
          }).then(() => {
              this.exportLoading = true;
              return exportCasLoginRecord(queryParams);
          }).then(response => {
              this.download(response.msg);
              this.exportLoading = false;
          })
      }
  }
};
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


