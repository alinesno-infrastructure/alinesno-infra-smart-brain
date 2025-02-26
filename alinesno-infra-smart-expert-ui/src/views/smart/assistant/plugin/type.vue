<template>
  <div class="app-container">

   <div class="page-header-container" style="margin-bottom: 20px;">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="text-large font-600 mr-3"> 
               插件类型
            </span>
          </div>
        </template>
      </el-page-header>
   </div>

     <el-row :gutter="20">
        <!--应用数据-->
        <el-col :span="24" :xs="24">
           <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
              <el-form-item label="类型名称" prop="name">
                 <el-input v-model="queryParams.name" placeholder="请输入类型名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item>
                 <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                 <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
           </el-form>

           <el-row :gutter="10" class="mb8">

              <el-col :span="1.5">
                 <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
              </el-col>
              <el-col :span="1.5">
                 <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
              </el-col>
              <el-col :span="1.5">
                 <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
              </el-col>

              <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
           </el-row>

           <el-table v-loading="loading" :data="ToolTypeList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />

              <!-- 业务字段-->
              <el-table-column label="类型名称" align="left" key="name" prop="name" v-if="columns[0].visible">
                 <template #default="scope">
                     <i :class="scope.row.icon"></i>&nbsp;{{ scope.row.name }}
                 </template>
              </el-table-column>

              <!-- 操作字段  -->
              <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
                 <template #default="scope">
                    <el-tooltip content="修改" placement="top" v-if="scope.row.id !== 1">
                       <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                          v-hasPermi="['system:ToolType:edit']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                       <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                          v-hasPermi="['system:ToolType:remove']"></el-button>
                    </el-tooltip>
                 </template>

              </el-table-column>
           </el-table>
           <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
        </el-col>
     </el-row>

     <!-- 添加或修改应用配置对话框 -->
     <el-dialog :title="title" v-model="open"  width="600px" append-to-body>
        <el-form :model="form" :rules="rules" :label-position="'top'" ref="databaseRef" label-width="100px">
           <el-row>
              <el-col :span="24">
                 <el-form-item label="类型图标" prop="icon">
                     <!-- <el-input v-model="form.icon" placeholder="请输入类型图标" maxlength="128" /> -->
                     <el-radio-group v-model="form.icon">
                        <el-radio v-for="category in pluginCategories" :key="category.icon" :label="category.icon">
                           <!-- 这里可以根据需要添加图标显示，这里简单用文本显示 -->
                           <i :class="category.icon"></i> 
                        </el-radio>
                     </el-radio-group>
                 </el-form-item>
              </el-col>
              <el-col :span="24">
                 <el-form-item label="类型名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入类型名称" maxlength="50" />
                 </el-form-item>
              </el-col>
           </el-row>
        </el-form>
        <template #footer>
           <div class="dialog-footer">
              <el-button type="primary" @click="submitForm">确 定</el-button>
              <el-button @click="cancel">取 消</el-button>
           </div>
        </template>
     </el-dialog>
  </div>
</template>

<script setup name="ToolType">

import {
  listToolType,
  delToolType,
  getToolType,
  updateToolType,
  addToolType
} from "@/api/smart/assistant/toolType";

const router = useRouter();
const { proxy } = getCurrentInstance();

// 定义变量
const ToolTypeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const pluginCategories = ref([
  { icon: 'fa-solid fa-newspaper' },
  { icon: 'fa-solid fa-image' },
  { icon: 'fa-solid fa-screwdriver-wrench' },
  { icon: 'fa-solid fa-house-chimney' },
  { icon: 'fa-solid fa-magnifying-glass' },
  { icon: 'fa-solid fa-graduation-cap' },
  { icon: 'fa-solid fa-users' },
  { icon: 'fa-solid fa-gamepad' },
  { icon: 'fa-solid fa-money-bill-trend-up' },
  { icon: 'fa-solid fa-music' },
  { icon: 'fa-solid fa-film' },
  { icon: 'fa-solid fa-book' },
  { icon: 'fa-solid fa-tv' },
  { icon: 'fa-solid fa-car' },
  { icon: 'fa-solid fa-plane' },
  { icon: 'fa-solid fa-shopping-cart' },
  { icon: 'fa-solid fa-medkit' },
  { icon: 'fa-solid fa-pen-nib' },
  { icon: 'fa-solid fa-camera' },
  { icon: 'fa-solid fa-bus' }
]);

// 列显隐信息
const columns = ref([
  { key: 0, label: `类型名称`, visible: true },
  { key: 1, label: `类型图标`, visible: true }
]);

const data = reactive({
  form: {},
  queryParams: {
     pageNum: 1,
     pageSize: 10,
     name: undefined
  },
  rules: {
     name: [{ required: true, message: "类型名称不能为空", trigger: "blur" }],
     icon: [{ required: true, message: "类型图标不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listToolType(queryParams.value).then(res => {
     loading.value = false;
     ToolTypeList.value = res.rows;
     total.value = res.total;
  });
};

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
};
/** 删除按钮操作 */
function handleDelete(row) {
  const ToolTypeIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除类型编号为"' + ToolTypeIds + '"的数据项？').then(function () {
     return delToolType(ToolTypeIds);
  }).then(() => {
     getList();
     proxy.$modal.msgSuccess("删除成功");
  }).catch(() => { });
};

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 重置操作表单 */
function reset() {
  form.value = {
     id: undefined,
     name: undefined,
     icon: undefined
  };
  proxy.resetForm("databaseRef");
};
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
};

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加类型";
};

/** 返回 */
function goBack(){
   router.go(-1) ; 
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const ToolTypeId = row.id || ids.value;
  getToolType(ToolTypeId).then(response => {
     form.value = response.data;
     open.value = true;
     title.value = "修改类型";
  });
};

/** 查看项目告警空间 */
function handleToolTypeSpace(id){
  let path =  '/project/space/'
  router.push({ path: path + id });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["databaseRef"].validate(valid => {
     if (valid) {
        if (form.value.id != undefined) {
           updateToolType(form.value).then(response => {
              proxy.$modal.msgSuccess("修改成功");
              open.value = false;
              getList();
           });
        } else {
           addToolType(form.value).then(response => {
              proxy.$modal.msgSuccess("新增成功");
              open.value = false;
              getList();
           });
        }
     }
  });
};

getList();

</script>