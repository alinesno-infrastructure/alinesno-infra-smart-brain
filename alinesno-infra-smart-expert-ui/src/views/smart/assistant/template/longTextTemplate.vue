<template>
  <div class="app-container review-page-contaier">

    <el-page-header @back="goBack" class="review-page-header">
      <template #content>
        <span class="text-large font-600 mr-3"> 长文档模板管理</span>
      </template>
    </el-page-header>

    <el-row :gutter="20">
      <!-- 模板分类  -->
      <el-col :span="4" :xs="24">

        <div class="plugin-catalog">
          <div class="catalog-title">
            <span>
              <i class="fa-solid fa-computer"></i> 模板分类
            </span>
            <div class="catalog-actions">
              <el-button size="default" text bg type="primary" plain @click="handleManageCategories" class="manage-btn">
                <i class="el-icon-setting"></i> 管理分类
              </el-button>
            </div>
          </div>
          <div class="catalog-content">
            <div v-for="(category, index) in pluginCategories" :key="index" @click="handleCategoryClick(category)"
              class="catalog-item" :class="{ active: category.id === currentLongTextTemplateType }">
              <span>
                <i :class="category.icon"></i> {{ category.name }}
              </span>
              <div class="item-actions">
                <el-button size="mini" type="text" @click.stop="handleEditCategory(category)" icon="el-icon-edit">编辑</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- <div class="plugin-catalog">
          <div class="catalog-title">
            <i class="fa-solid fa-computer"></i> 模板分类
          </div>
          <div class="catalog-content">
            <div v-for="(category, index) in pluginCategories" :key="index" @click="handleCategoryClick(category.code)"
              class="catalog-item" :class="{ active: category.code === currentLongTextTemplateType }">
              <i :class="category.icon"></i> {{ category.label }}
            </div>
          </div>
        </div> -->
      </el-col>

      <!--模板数据-->
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="模板名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入模板名称" clearable style="width: 240px"
              @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="模板类型" prop="pluginType" label-width="100px">
            <el-input v-model="queryParams['condition[pluginType|like]']" placeholder="请输入模板类型" clearable
              style="width: 240px" @keyup.enter="handleQuery" />
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
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
              v-hasPermi="['system:LongTextTemplate:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
              v-hasPermi="['system:LongTextTemplate:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Upload" @click="handleImport"
              v-hasPermi="['system:user:import']">导入</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="LongTextTemplateList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="图标" align="center" width="50px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon">
                <img :src="imagePath(scope.row.icon)" style="border-radius: 10px;" />
                 <!-- <i v-if="scope.row.icon" :class="scope.row.icon"></i>
                 <i v-else class="fa-solid fa-file-word"></i> -->
              </div>
            </template>
          </el-table-column>
          <el-table-column label="模板名称" align="left" key="name" width="250" prop="name" v-if="columns[1].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="模板描述" align="left" key="description" prop="description" v-if="columns[5].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.description }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="模板范围" align="center" key="longTextTemplateType" width="150"
            prop="longTextTemplatePermission" v-if="columns[2].visible">
            <template #default="scope">
              <el-button text bg type="primary">
                <span v-if="scope.row.longTextTemplatePermission == 'person'">私有</span>
                <span v-if="scope.row.longTextTemplatePermission == 'org'">组织</span>
                <span v-if="scope.row.longTextTemplatePermission == 'public'">公开</span>
              </el-button>
            </template>
          </el-table-column>

          <el-table-column label="配置" align="center" width="300" key="target" prop="target" v-if="columns[6].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                <!-- 
                <el-button type="primary" text @click="configLongTextTemplateInput(scope.row)">
                  <i class="fa-solid fa-screwdriver-wrench"></i>
                </el-button> 
                -->
                <el-button type="danger" text @click="configLongTextTemplateOutput(scope.row)">
                  <i class="fa-solid fa-hammer"></i>指令配置
                </el-button>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="状态" align="center" width="100" key="hasStatus" prop="hasStatus"
            v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-switch v-model="scope.row.hasStatus" :active-value="0" :inactive-value="1"
                @change="handleChangStatusField('hasStatus', scope.row.hasStatus, scope.row.id)" />
            </template>
          </el-table-column>
          <el-table-column label="更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width"
            v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:LongTextTemplate:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                  v-hasPermi="['system:LongTextTemplate:remove']"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改模板配置对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="LongTextTemplateRef" label-width="100px">
        <el-row>
          <el-col :span="24" class="editor-after-div">
            <el-form-item label="图标" prop="icon">
              <el-upload 
                :file-list="imageUrl" 
                multiple
                :action="upload.headerUrl + '?type=img&updateSupport=' + upload.updateSupport"
                list-type="picture-card" 
                :auto-upload="true" 
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload" 
                :headers="upload.headers" 
                :disabled="upload.isUploading"
                :on-progress="handleFileUploadProgress">
                <el-icon class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row> 
        <!-- In your template form dialog -->
        <el-row>
          <el-col :span="24">
            <el-form-item label="模板分类" prop="longTextTemplateType">
              <el-select v-model="form.longTextTemplateType" placeholder="请选择模板分类" style="width: 100%">
                <el-option v-for="item in pluginCategories" :key="item.id" :label="item.name" :value="item.id">
                  <span style="float: left">
                    <i :class="item.icon"></i>
                  </span>
                  <span style="float: left; margin-left:10px ; color: #444; font-size: 14px">
                    {{ item.name }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="权限" prop="longTextTemplatePermission">
              <el-radio-group v-model="form.longTextTemplatePermission" size="large">
                <el-radio v-for="option in dataScopeOptions" :key="option.value" :value="option.value"
                  :label="option.label">
                  <el-tooltip class="box-item" effect="dark" :content="option.desc" placement="top-start">
                    <div style="display: flex;align-items: center;gap: 6px;">
                      {{ option.text }} <el-icon>
                        <QuestionFilled />
                      </el-icon>
                    </div>
                  </el-tooltip>
                </el-radio>
              </el-radio-group>

            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模板名称" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" placeholder="模板描述信息" maxlength="250" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 
        <el-row>
          <el-col :span="24">
            <el-form-item label="系统提示词" prop="prompt">
              <el-input type="textarea" v-model="form.systemPrompt" :rows="4" placeholder="模板指令信息" />
            </el-form-item>
          </el-col>
        </el-row> 
        -->
        <el-row>
          <el-col :span="24">
            <el-form-item label="文档要求" prop="prompt">
              <el-input type="textarea" v-model="form.prompt" resize="none" :rows="8" placeholder="模板指令信息" />
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

    <!-- 模板导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body :before-close="handleClose">
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport" 
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xls、xlsx格式文件。</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="upload.isUploading" @click="submitFileForm">确 定</el-button>
          <el-button :disabled="upload.isUploading" @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Add this after the import dialog in your template -->
    <!-- Category Management Dialog -->
    <el-dialog :title="categoryDialog.title" v-model="categoryDialog.visible" width="800px" append-to-body>
      <el-table :data="categoryList" v-loading="categoryLoading">
        <el-table-column prop="icon" align="center" label="分类图标" width="80">
          <template #default="scope">
            <i :class="scope.row.icon"></i>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称"></el-table-column>
        <el-table-column prop="sort" align="center" label="排序" width="100"></el-table-column>
        <el-table-column label="操作" align="center" width="190">
          <template #default="scope">
            <el-button @click="handleEditCategory(scope.row)">编辑</el-button>
            <el-button type="danger" @click="handleDeleteCategory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="categoryDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleAddCategory">新增分类</el-button>
      </template>
    </el-dialog>

    <!-- Category Edit Dialog -->
    <el-dialog :title="categoryEditDialog.title" v-model="categoryEditDialog.visible" width="500px" append-to-body>
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name"></el-input>
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-input v-model="categoryForm.icon">
            <template #prepend>
              <i :class="categoryForm.icon"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort"></el-input-number>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="categoryEditDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitCategoryForm">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="LongTextTemplate">
import { getToken } from "@/utils/auth";
import {
  listLongTextTemplate,
  delLongTextTemplate,
  getLongTextTemplate,
  changStatusField,
  updateLongTextTemplate,
  addLongTextTemplate,
  getAllLongTextTemplate
} from "@/api/smart/scene/longTextTemplate";

import {
  getAllLongTextTemplateGroup,
  getLongTextTemplateGroup,
  addLongTextTemplateGroup,
  updateLongTextTemplateGroup,
  delLongTextTemplateGroup,
} from "@/api/smart/scene/longTextTemplateGroup";

import { reactive, ref, computed } from "vue";
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from "vue-router";

const router = useRouter();
const { proxy } = getCurrentInstance();

const LongTextTemplateList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const currentLongTextTemplateType = ref('')
const imageUrl = ref([]);

// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `模板名称`, visible: true },
  { key: 2, label: `模板类型`, visible: true },
  { key: 3, label: `使用场景`, visible: true },
  { key: 4, label: `状态`, visible: true },
  { key: 5, label: `模板描述`, visible: true },
  { key: 6, label: `模板目标`, visible: true },
  { key: 7, label: `创建时间`, visible: true },
  { key: 8, label: `编辑`, visible: true },
]);

/*** 模板导入参数 */
const upload = reactive({
  // 是否显示弹出层（模板导入）
  open: false,
  // 弹出层标题（模板导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的模板数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 头像上传的地址
  headerUrl: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/longTextTemplate/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    toolName: undefined,
    name: undefined,
    pluginType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    id: [{ required: true, message: "模板编号不能为空", trigger: "blur" }],
    name: [{ required: true, message: "模板名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "模板名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    icon: [{ required: true, message: "图标不能为空", trigger: "blur" }],
    pluginType: [{ required: true, message: "模板类型不能为空", trigger: "blur" }],
    scene: [{ required: true, message: "使用场景不能为空", trigger: "blur" }],
    hasStatus: [{ required: true, message: "状态不能为空", trigger: "blur" }],
    description: [{ required: true, message: "模板描述不能为空", trigger: "blur" }],
    prompt: [{ required: true, message: "模板Prompt不能为空", trigger: "blur" }],
    longTextTemplateType: [{ required: true, message: "模板类型不能为空", trigger: "blur" }],
    longTextTemplatePermission: [{ required: true, message: "数据范围不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

// 组织信息
const dataScopeOptions = ref([
  { value: 'person', label: 'person', text: '私有', desc: '此数据仅你个人可见和使用' },
  { value: 'org', label: 'org', text: '组织', desc: '此数据可在组织内部共享和使用' },
  { value: 'public', label: 'public', text: '公开', desc: '此数据可被所有人访问和查看' }
]);

// 定义模板分类数组，每个元素包含 code 和 label
const pluginCategories = ref([
  // { code: 'fa-solid fa-newspaper', label: '新闻阅读' },
  // { code: 'fa-solid fa-image', label: '图像' },
  // { code: 'fa-solid fa-screwdriver-wrench', label: '实用模板' },
  // { code: 'fa-solid fa-house-chimney', label: '便利生活' },
  // { code: 'fa-solid fa-magnifying-glass', label: '网页搜索' },
  // { code: 'fa-solid fa-graduation-cap', label: '科学与教育' },
  // { code: 'fa-solid fa-users', label: '社交' },
  // { code: 'fa-solid fa-gamepad', label: '游戏与娱乐' },
  // { code: 'fa-solid fa-money-bill-trend-up', label: '金融与商业' }
]);

// 输出配置相关状态
const outputConfigOpen = ref(false);
const currentTemplateId = ref(null);

// 输入配置相关状态
const inputConfigOpen = ref(false)
const outputConfig = ref({
  styles: {
    selected: '默认',
    options: []
  },
  time: {
    selected: '默认',
    options: []
  },
  hooks: {
    selected: '默认',
    options: []
  }
});

const inputConfig = ref({
  fields: []
});

// 初始化配置数据
const initConfigData = (row) => {
  currentTemplateId.value = row.id

  // 初始化输出配置
  try {
    const config = JSON.parse(row.resultConfig || '{}')
    outputConfig.value = {
      styles: {
        selected: '默认',
        options: config.styles || []
      },
      time: {
        selected: '默认',
        options: config.time || []
      },
      hooks: {
        selected: '默认',
        options: config.hooks || []
      }
    }
  } catch (e) {
    console.error('解析输出配置失败:', e)
    outputConfig.value = {
      styles: { options: [{ name: '默认', key: '默认' }] },
      time: { options: [{ name: '默认', key: '默认' }] },
      hooks: { options: [{ name: '默认', key: '默认' }] }
    }
  }

  // 初始化输入配置
  try {
    const config = JSON.parse(row.config || '[]')
    inputConfig.value.fields = config.map(field => ({
      ...field,
      options: field.options || []
    }))
  } catch (e) {
    console.error('解析输入配置失败:', e)
    inputConfig.value.fields = [{
      title: '默认字段',
      field: 'default_field',
      type: 'input',
      placeholder: '请输入内容',
      maxLength: 5000,
      required: true,
      isShow: true,
      options: []
    }]
  }
}

// 暴露配置方法给模板
const configLongTextTemplateOutput = (row) => {
  initConfigData(row)
  outputConfigOpen.value = true
}

const configLongTextTemplateInput = (row) => {
  initConfigData(row)
  inputConfigOpen.value = true
}// Add these new refs and functions

// 处理分类项点击事件
// const handleCategoryClick = (category) => {
//   console.log(`点击了分类：${category}`);
//   queryParams.value.longTextTemplateType = category;
//   currentLongTextTemplateType.value = category;
//   getList();
// };

// 返回
const goBack = () => {
  router.push({ path: "/global/config" });
};

/** 查询模板列表 */
function getList() {
  loading.value = true;
  listLongTextTemplate(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    LongTextTemplateList.value = res.rows;
    total.value = res.total;
  });
};

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.icon = response.data;
  console.log('form.icon = ' + form.value.icon);
  upload.isUploading = false ;
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.name = undefined;
  queryParams.value.pluginType = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const applicationIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除模板编号为"' + applicationIds + '"的数据项？').then(function () {
    return delLongTextTemplate(applicationIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 修改状态 */
const handleChangStatusField = async (field, value, id) => {
  const res = await changStatusField({
    field: field,
    value: value ? 1 : 0,
    id: id
  }).catch(() => { })
  if (res && res.code == 200) {
    getList();
  }
}

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
    pluginType: undefined,
    scene: undefined,
    hasStatus: undefined,
    description: undefined,
    target: undefined,
  };
  proxy.resetForm("LongTextTemplateRef");
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
  title.value = "添加模板";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getLongTextTemplate(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改模板";

    const item = {
      url: upload.display + response.data.icon,
      uid: id // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item);
  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["LongTextTemplateRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateLongTextTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addLongTextTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

function getLongTextTemplateTypeList() {
  getAllLongTextTemplate().then(response => {
    pluginCategories.value = response.data;
  });
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "长文档模板导入";
  upload.open = true;
};

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);

  const responseData = response.data;

  // 构建导入结果信息
  let message = '';
  if (responseData.failedCount > 0) {
    message += `<div class="import-summary">
                  <p>导入成功: ${responseData.successCount} 条</p>
                  <p class="text-danger">导入失败: ${responseData.failedCount} 条</p>
                </div>
                <div class="failed-list mt-3">
                  <h4 class="mb-2">失败详情</h4>
                  <ul class="list-group">`;

    responseData.failedList.forEach(item => {
      message += `<li class="list-group-item list-group-item-danger">
                    <div class="font-weight-bold">${item.title}</div>
                    <div class="text-muted">原因: ${item.failReason.reason} - ${item.failReason.detail}</div>
                  </li>`;
    });

    message += `</ul></div>`;
  } else {
    message += `<div class="text-success">全部 ${responseData.successCount} 条数据导入成功</div>`;
  }

  proxy.$alert(`<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>${message}</div>`,
    "导入结果",
    { dangerouslyUseHTMLString: true });

  getList();
  getLongTextTemplateTypeList();
};

const handleClose = (done) => {
  // 如果正在上传文件，则阻止关闭对话框
  if (upload.isUploading) {
    proxy.$modal.msgError("正在上传文件，请稍后再试");
    return;
  }
}

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
};

// Add these to your data section
const categoryDialog = reactive({
  visible: false,
  title: '模板分类管理'
});

const categoryEditDialog = reactive({
  visible: false,
  title: '编辑分类'
});

const categoryForm = reactive({
  id: undefined,
  name: '',
  icon: 'fa-solid fa-folder',
  sort: 0
});

const categoryList = ref([]);
const categoryLoading = ref(false);

// Add these methods
const handleManageCategories = async () => {
  categoryDialog.visible = true;
  await loadCategories();
};

const loadCategories = async () => {
  categoryLoading.value = true;
  try {
    const res = await getAllLongTextTemplateGroup();
    categoryList.value = res.data;
    // Also update the pluginCategories for the sidebar
    pluginCategories.value = res.data.map(item => ({
      icon: item.icon,
      name: item.name,
      sort: item.sort,
      id: item.id
    }));
  } finally {
    categoryLoading.value = false;
  }
};

const handleAddCategory = () => {
  categoryEditDialog.title = '新增分类';
  categoryEditDialog.visible = true;
  Object.assign(categoryForm, {
    id: undefined,
    name: '',
    icon: 'fa-solid fa-folder',
    sort: 0
  });
};

const handleEditCategory = (row) => {
  categoryEditDialog.title = '编辑分类';
  categoryEditDialog.visible = true;

  console.log('row = ' + JSON.stringify(row))

  Object.assign(categoryForm, {
    id: row.id,
    name: row.name ,
    icon: row.icon ,
    sort: row.sort
  });

  console.log('categoryForm = ' + JSON.stringify(categoryForm))
};

const handleDeleteCategory = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类 "${row.name}" 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await delLongTextTemplateGroup(row.id);
    ElMessage.success('删除成功');
    await loadCategories();
  } catch (error) {
    // User canceled
  }
};

const submitCategoryForm = async () => {
  try {
    if (categoryForm.id) {
      await updateLongTextTemplateGroup(categoryForm);
    } else {
      await addLongTextTemplateGroup(categoryForm);
    }
    ElMessage.success('保存成功');
    categoryEditDialog.visible = false;
    await loadCategories();
  } catch (error) {
    ElMessage.error(error.message || '操作失败');
  }
};

// Update the handleCategoryClick method to use category ID
const handleCategoryClick = (category) => {
  currentLongTextTemplateType.value = category.id;
  queryParams.value.longTextTemplateType = category.id;
  getList();
};

// Update the template categories initialization
const initTemplateCategories = async () => {
  try {
    await loadCategories();
  } catch (error) {
    console.error('加载分类失败:', error);
    // Fallback to default categories
    pluginCategories.value = [
      // { code: 'fa-solid fa-newspaper', label: '新闻阅读', id: 'news' },
      // { code: 'fa-solid fa-image', label: '图像', id: 'image' },
      // ... other default categories
    ];
  }
};

// Call this instead of getLongTextTemplateTypeList() in your mounted/created hook
initTemplateCategories();

getList();
getLongTextTemplateTypeList();

</script>

<style lang="scss" scoped>
.role-icon {
  i {
    font-size: 20px;
  }
  img {
    width: 35px;
    height: 35px;
    border-radius: 10px;
  }
}

.plugin-catalog {
  .catalog-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px;
    font-size: 14px;
    font-weight: bold;
    color: #444;
    margin-bottom: 5px;

    .catalog-actions {
      .manage-btn {
        padding: 5px 10px;

        i {
          margin-right: 5px;
        }
      }
    }
  }

  .catalog-content {
    .catalog-item {
      position: relative;
      padding: 12px 10px;
      color: #443;
      cursor: pointer;
      margin-bottom: 4px;
      border-radius: 4px;
      font-size: 14px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 10px;
      flex-direction: row;

      &:hover {
        background: #e9eaf3;

        .item-actions {
          display: flex;
        }
      }

      .item-actions {
        display: none;

        .el-button {
          padding: 0;
          margin-left: 5px;
        }
      }
    }

    .active {
      background: #e9eaf3;

      .item-actions {
        display: flex;
      }
    }
  }
}

/* Add these styles for the configuration dialogs */
.config-toolbar {
  margin-bottom: 15px;
}

.option-list {
  border: 1px solid #eee;
  padding: 10px;
  border-radius: 4px;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
</style>