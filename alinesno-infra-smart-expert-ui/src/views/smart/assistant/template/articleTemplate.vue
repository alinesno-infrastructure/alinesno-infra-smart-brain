<template>
    <div class="app-container review-page-contaier">

        <el-page-header @back="goBack" class="review-page-header">
            <template #content>
                <span class="text-large font-600 mr-3"> 文章模板管理</span>
            </template>
        </el-page-header>

    <el-row :gutter="20">
      <!-- 模板分类  -->
      <el-col :span="3" :xs="24">
        <div class="plugin-catalog">
          <div class="catalog-title">
            <i class="fa-solid fa-computer"></i> 模板分类
          </div>
          <div class="catalog-content">
            <div 
              v-for="(category, index) in pluginCategories" 
              :key="index" 
              @click="handleCategoryClick(category.code)"
              class="catalog-item" :class="{ active: category.code === currentArticleTemplateType }">
              <i :class="category.icon"></i> {{ category.label }}
            </div>
          </div>
        </div>
      </el-col>

      <!--模板数据-->
      <el-col :span="21" :xs="24">
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
          <!-- 
         <el-col :span="1.5">
            <el-button type="warning" plain icon="Plus" @click="handleManagerType">类型管理</el-button>
          </el-col> 
          -->

          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
              v-hasPermi="['system:ArticleTemplate:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
              v-hasPermi="['system:ArticleTemplate:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
                  <el-button
                     type="warning"
                     plain
                     icon="Upload"
                     @click="handleImport"
                     v-hasPermi="['system:user:import']"
                  >导入</el-button>
               </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="ArticleTemplateList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="图标" align="center" width="50px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon">
                <img :src="imagePath(scope.row.icon)" style="border-radius: 50%;" />
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

          <el-table-column label="模板范围" align="center" key="articleTemplateType" width="150" prop="articleTemplatePermission" v-if="columns[2].visible">
            <template #default="scope">
              <el-button text bg type="primary">
                <span v-if="scope.row.articleTemplatePermission == 'person'">私有</span>
                <span v-if="scope.row.articleTemplatePermission == 'org'">组织</span>
                <span v-if="scope.row.articleTemplatePermission == 'public'">公开</span>
              </el-button>
            </template>
          </el-table-column>

          <el-table-column label="配置" align="center" width="300" key="target" prop="target" v-if="columns[6].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                <!-- 
                <el-button type="primary" text @click="configArticleTemplateScript(scope.row)">
                  <i class="fa-solid fa-screwdriver-wrench"></i>输入配置
                </el-button>
                <el-button type="danger" text @click="configArticleTemplateScript(scope.row)">
                  <i class="fa-solid fa-hammer"></i>润色配置
                </el-button> 
                -->

                <el-button type="primary" text @click="configArticleTemplateInput(scope.row)">
                  <i class="fa-solid fa-screwdriver-wrench"></i>输入配置
                </el-button>
                <el-button type="danger" text @click="configArticleTemplateOutput(scope.row)">
                  <i class="fa-solid fa-hammer"></i>润色配置
                </el-button>
              </div>
            </template>
          </el-table-column> 

          <el-table-column label="状态" align="center" width="100" key="hasStatus" prop="hasStatus"
            v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-switch  v-model="scope.row.hasStatus" :active-value="0" :inactive-value="1"
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
                  v-hasPermi="['system:ArticleTemplate:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                  v-hasPermi="['system:ArticleTemplate:remove']"></el-button>
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
      <el-form :model="form" :rules="rules" ref="ArticleTemplateRef" label-width="100px">
        <el-row>
          <el-col :span="24" class="editor-after-div">
            <el-form-item label="图标" prop="icon">
              <el-upload 
                :file-list="imageUrl" 
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
        <el-row>
          <el-col :span="24">
            <el-form-item label="模板类型" prop="articleTemplateType">
              <el-radio-group v-model="form.articleTemplateType">
                <el-radio v-for="item in pluginCategories" :key="item.code" :label="item.code">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="权限" prop="articleTemplatePermission">
              <el-radio-group v-model="form.articleTemplatePermission" size="large">
                <el-radio v-for="option in dataScopeOptions" :key="option.value" :value="option.value" :label="option.label">
                    <el-tooltip class="box-item" effect="dark" :content="option.desc" placement="top-start">
                  <div style="display: flex;align-items: center;gap: 6px;">
                      {{ option.text }} <el-icon><QuestionFilled /></el-icon>
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
        <el-row>
          <el-col :span="24">
            <el-form-item label="系统提示词" prop="prompt">
              <el-input type="textarea" v-model="form.systemPrompt" :rows="4" placeholder="模板指令信息" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="用户提示词" prop="prompt">
              <el-input type="textarea" v-model="form.prompt" :rows="4" placeholder="模板指令信息" />
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

    <!-- 类型管理 -->
    <el-dialog v-model="managerTypeOpen" :title="模板类型管理" >
      <ManagerType />
    </el-dialog>

      <!-- 模板导入对话框 -->
      <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body :before-close="handleClose">
         <el-upload
            ref="uploadRef"
            :limit="1"
            accept=".xlsx, .xls"
            :headers="upload.headers"
            :action="upload.url + '?updateSupport=' + upload.updateSupport"
            :disabled="upload.isUploading"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :auto-upload="false"
            drag
         >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
               <div class="el-upload__tip text-center">
                  <!-- <div class="el-upload__tip">
                     <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
                  </div> -->
                  <span>仅允许导入xls、xlsx格式文件。</span>
                  <!-- <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link> -->
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

      <!-- 输出配置对话框 -->
       <!-- 输出配置对话框 -->
  <el-dialog v-model="outputConfigOpen" title="输出配置管理" width="900px" append-to-body>
    <el-tabs v-model="outputActiveTab" :tab-position="'left'">
      <!-- 风格设置 -->
      <el-tab-pane label="风格设置" name="styles">
        <div class="config-toolbar">
          <el-button type="primary" @click="addConfigItem('styles')" >
            <el-icon><Plus /></el-icon>添加风格
          </el-button>
        </div>
        <el-table :data="outputConfig.styles.options" border>
          <el-table-column prop="name" label="风格名称" />
          <el-table-column prop="key" label="唯一标识" width="200" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button  @click="editConfigItem('styles', scope.$index)">编辑</el-button>
              <el-button  type="danger" @click="removeConfigItem('styles', scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 时间设置 -->
      <el-tab-pane label="时间设置" name="time">
        <div class="config-toolbar">
          <el-button type="primary" @click="addConfigItem('time')" >
            <el-icon><Plus /></el-icon>添加时间选项
          </el-button>
        </div>
        <el-table :data="outputConfig.time.options" border>
          <el-table-column prop="name" label="选项名称" />
          <el-table-column prop="key" label="唯一标识" width="200" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button  @click="editConfigItem('time', scope.$index)">编辑</el-button>
              <el-button  type="danger" @click="removeConfigItem('time', scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 挂钩设置 -->
      <el-tab-pane label="挂钩设置" name="hooks">
        <div class="config-toolbar">
          <el-button type="primary" @click="addConfigItem('hooks')" >
            <el-icon><Plus /></el-icon>添加挂钩选项
          </el-button>
        </div>
        <el-table :data="outputConfig.hooks.options" border>
          <el-table-column prop="name" label="选项名称" />
          <el-table-column prop="key" label="唯一标识" width="200" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button  @click="editConfigItem('hooks', scope.$index)">编辑</el-button>
              <el-button  type="danger" @click="removeConfigItem('hooks', scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 配置项编辑对话框 -->
    <el-dialog v-model="itemEditDialogVisible" :title="itemEditDialogTitle" width="500px" append-to-body>
      <el-form :model="currentEditItem" label-width="80px" size="">
        <el-form-item label="名称" required>
          <el-input v-model="currentEditItem.name" placeholder="请输入显示名称" />
        </el-form-item>
        <el-form-item label="标识" required>
          <el-input v-model="currentEditItem.key" placeholder="请输入唯一标识" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfigItem">保存</el-button>
      </template>
    </el-dialog>

    <template #footer>
      <el-button @click="outputConfigOpen = false">取消</el-button>
      <el-button type="primary" @click="saveOutputConfig">保存配置</el-button>
    </template>
  </el-dialog>

  <!-- 输入配置对话框 -->
  <el-dialog v-model="inputConfigOpen" title="输入配置管理" width="900px" append-to-body>
    <div class="config-toolbar">
      <el-button type="primary" @click="addInputField" >
        <el-icon><Plus /></el-icon>添加字段
      </el-button>
    </div>
    
    <el-table :data="inputConfig.fields" border style="width:100%;">
      <el-table-column prop="title" label="字段标题" />
      <el-table-column prop="field" label="字段名" />
      <el-table-column prop="type" label="字段类型" width="100">
        <template #default="scope">
          <el-tag>{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="required" label="必填" width="80" align="center" >
        <template #default="scope">
          <el-tag :type="scope.row.required ? 'success' : 'info'">
            {{ scope.row.required ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isShow" label="显示" width="80" align="center" >
        <template #default="scope">
          <el-tag :type="scope.row.isShow ? 'success' : 'info'">
            {{ scope.row.isShow ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button  @click="editInputField(scope.$index)">编辑</el-button>
          <el-button  type="danger" @click="removeInputField(scope.$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 输入字段编辑对话框 -->
    <el-dialog v-model="inputFieldDialogVisible" :title="inputFieldDialogTitle" width="600px" append-to-body>
      <el-form :model="currentEditField" label-width="100px">
        <el-form-item label="字段标题" required>
          <el-input v-model="currentEditField.title" placeholder="如：课程主题" />
        </el-form-item>
        <el-form-item label="字段名" required>
          <el-input v-model="currentEditField.field" placeholder="英文标识，如：course_title" />
        </el-form-item>
        <el-form-item label="字段类型" required>
          <el-select v-model="currentEditField.type" placeholder="请选择字段类型">
            <el-option label="文本输入" value="input" />
            <el-option label="下拉选择" value="select" />
            <el-option label="多行文本" value="textarea" />
          </el-select>
        </el-form-item>
        <el-form-item label="占位文本">
          <el-input v-model="currentEditField.placeholder" placeholder="输入提示信息" />
        </el-form-item>
        <el-form-item label="最大长度">
          <el-input-number v-model="currentEditField.maxLength" :min="1" :max="5000" />
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="currentEditField.required" />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-switch v-model="currentEditField.isShow" />
        </el-form-item>
        
        <!-- 选项配置（当类型为select时显示） -->
        <el-form-item v-if="currentEditField.type === 'select'" label="选项配置">
          <div class="option-list">
            <div v-for="(option, idx) in currentEditField.options" :key="idx" class="option-item">
              <el-input v-model="option.label" placeholder="显示文本" style="width: 200px; margin-right: 10px;" />
              <el-input v-model="option.value" placeholder="值" style="width: 200px; margin-right: 10px;" />
              <el-button type="danger" circle @click="removeOption(idx)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button @click="addOption" type="primary" plain >
              <el-icon><Plus /></el-icon>添加选项
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="inputFieldDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveInputField">保存</el-button>
      </template>
    </el-dialog>

    <template #footer>
      <el-button @click="inputConfigOpen = false">取消</el-button>
      <el-button type="primary" @click="saveInputConfig">保存配置</el-button>
    </template>
  </el-dialog>

  </div>
</template>

<script setup name="ArticleTemplate">
import { getToken } from "@/utils/auth";
import {
  listArticleTemplate,
  delArticleTemplate,
  getArticleTemplate,
  changStatusField,
  updateArticleTemplate,
  addArticleTemplate,
  getAllArticleTemplate
} from "@/api/smart/scene/articleTemplate";

// import {
//   getAllArticleTemplateType,
// } from "@/api/smart/assistant/articleTemplateType";

// import ManagerType from './type'

import { reactive, ref, computed } from "vue";
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from "vue-router";

const router = useRouter();
const { proxy } = getCurrentInstance();

const ArticleTemplateList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const currentArticleTemplateType = ref('')
const imageUrl = ref([]);



// 类型管理
const managerTypeOpen = ref(false);

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
  // 设置上传的请求图像
  headerUrl: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/articleGenerate/importData",
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
    articleTemplateType: [{ required: true, message: "模板类型不能为空", trigger: "blur" }],
    articleTemplatePermission: [{ required: true, message: "数据范围不能为空", trigger: "blur" }],
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
  { code: 'fa-solid fa-newspaper', label: '新闻阅读' },
  { code: 'fa-solid fa-image', label: '图像' },
  { code: 'fa-solid fa-screwdriver-wrench', label: '实用模板' },
  { code: 'fa-solid fa-house-chimney', label: '便利生活' },
  { code: 'fa-solid fa-magnifying-glass', label: '网页搜索' },
  { code: 'fa-solid fa-graduation-cap', label: '科学与教育' },
  { code: 'fa-solid fa-users', label: '社交' },
  { code: 'fa-solid fa-gamepad', label: '游戏与娱乐' },
  { code: 'fa-solid fa-money-bill-trend-up', label: '金融与商业' }
]);


// 输出配置相关状态
const outputConfigOpen = ref(false);
const outputActiveTab = ref('styles')
const itemEditDialogVisible = ref(false)
const itemEditDialogTitle = ref('')
const currentTemplateId = ref(null);
const currentEditItem = reactive({
  name: '',
  key: ''
})
let currentEditContext = null // 当前编辑的上下文（类型和索引）

// 输入配置相关状态
const inputConfigOpen = ref(false)
const inputFieldDialogVisible = ref(false)
const inputFieldDialogTitle = ref('')
const currentEditField = reactive({
  title: '',
  field: '',
  type: 'input',
  placeholder: '',
  maxLength: 5000,
  required: true,
  isShow: true,
  options: []
})
let currentEditFieldIndex = null

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

// 输出配置项操作
const addConfigItem = (type) => {
  currentEditContext = { type, index: -1 }
  currentEditItem.name = ''
  currentEditItem.key = ''
  itemEditDialogTitle.value = `添加${getTypeName(type)}项`
  itemEditDialogVisible.value = true
}

const editConfigItem = (type, index) => {
  currentEditContext = { type, index }
  const item = outputConfig.value[type].options[index]
  currentEditItem.name = item.name
  currentEditItem.key = item.key
  itemEditDialogTitle.value = `编辑${getTypeName(type)}项`
  itemEditDialogVisible.value = true
}

const removeConfigItem = async (type, index) => {
  try {
    await ElMessageBox.confirm('确定要删除此配置项吗？', '提示', {
      type: 'warning'
    })
    outputConfig.value[type].options.splice(index, 1)
    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

const saveConfigItem = () => {
  if (!currentEditItem.name || !currentEditItem.key) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const { type, index } = currentEditContext
  if (index === -1) {
    // 新增
    outputConfig.value[type].options.push({
      name: currentEditItem.name,
      key: currentEditItem.key
    })
  } else {
    // 编辑
    outputConfig.value[type].options[index] = {
      name: currentEditItem.name,
      key: currentEditItem.key
    }
  }
  
  itemEditDialogVisible.value = false
  ElMessage.success('保存成功')
}

const getTypeName = (type) => {
  const map = {
    styles: '风格',
    time: '时间',
    hooks: '挂钩'
  }
  return map[type] || ''
}

// 输入字段操作
const addInputField = () => {
  currentEditFieldIndex = -1
  Object.assign(currentEditField, {
    title: '',
    field: '',
    type: 'input',
    placeholder: '',
    maxLength: 5000,
    required: true,
    isShow: true,
    options: []
  })
  inputFieldDialogTitle.value = '添加输入字段'
  inputFieldDialogVisible.value = true
}

const editInputField = (index) => {
  currentEditFieldIndex = index
  const field = inputConfig.value.fields[index]
  Object.assign(currentEditField, {
    title: field.title,
    field: field.field,
    type: field.type,
    placeholder: field.placeholder || '',
    maxLength: field.maxLength || 5000,
    required: field.required !== false,
    isShow: field.isShow !== false,
    options: field.options ? [...field.options] : []
  })
  inputFieldDialogTitle.value = '编辑输入字段'
  inputFieldDialogVisible.value = true
}

const removeInputField = async (index) => {
  try {
    await ElMessageBox.confirm('确定要删除此输入字段吗？', '提示', {
      type: 'warning'
    })
    inputConfig.value.fields.splice(index, 1)
    ElMessage.success('删除成功')
  } catch {
    // 用户取消
  }
}

const addOption = () => {
  currentEditField.options.push({
    label: '',
    value: ''
  })
}

const removeOption = (index) => {
  currentEditField.options.splice(index, 1)
}

const saveInputField = () => {
  if (!currentEditField.title || !currentEditField.field) {
    ElMessage.warning('请填写标题和字段名')
    return
  }
  
  if (currentEditField.type === 'select' && currentEditField.options.length === 0) {
    ElMessage.warning('请至少添加一个选项')
    return
  }
  
  const fieldData = {
    title: currentEditField.title,
    field: currentEditField.field,
    type: currentEditField.type,
    placeholder: currentEditField.placeholder,
    maxLength: currentEditField.maxLength,
    required: currentEditField.required,
    isShow: currentEditField.isShow
  }
  
  if (currentEditField.type === 'select') {
    fieldData.options = currentEditField.options.map(opt => ({
      label: opt.label,
      value: opt.value
    }))
  }
  
  if (currentEditFieldIndex === -1) {
    // 新增
    inputConfig.value.fields.push(fieldData)
  } else {
    // 编辑
    inputConfig.value.fields[currentEditFieldIndex] = fieldData
  }
  
  inputFieldDialogVisible.value = false
  ElMessage.success('保存成功')
}

// 保存配置到服务器
const saveOutputConfig = async () => {
  const config = {
    styles: outputConfig.value.styles.options,
    time: outputConfig.value.time.options,
    hooks: outputConfig.value.hooks.options
  }
  
  try {
    await updateArticleTemplate({
      id: currentTemplateId.value,
      resultConfig: JSON.stringify(config)
    })
    ElMessage.success('输出配置保存成功')
    outputConfigOpen.value = false
    getList()
  } catch (error) {
    ElMessage.error('保存失败: ' + error.message)
  }
}

const saveInputConfig = async () => {
  try {
    await updateArticleTemplate({
      id: currentTemplateId.value,
      config: JSON.stringify(inputConfig.value.fields)
    })
    ElMessage.success('输入配置保存成功')
    inputConfigOpen.value = false
    getList()
  } catch (error) {
    ElMessage.error('保存失败: ' + error.message)
  }
}

// 暴露配置方法给模板
const configArticleTemplateOutput = (row) => {
  initConfigData(row)
  outputConfigOpen.value = true
}

const configArticleTemplateInput = (row) => {
  initConfigData(row)
  inputConfigOpen.value = true
}// Add these new refs and functions

// 处理分类项点击事件
const handleCategoryClick = (category) => {
  console.log(`点击了分类：${category}`);
  queryParams.value.articleTemplateType = category;
  currentArticleTemplateType.value = category;
  getList();
};

// 返回
const goBack = () => {
  router.push({ path: "/global/config" });
};

/** 查询模板列表 */
function getList() {
  loading.value = true;
  listArticleTemplate(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    ArticleTemplateList.value = res.rows;
    total.value = res.total;
  });
};

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.icon = response.data;
  console.log('form.icon = ' + form.value.icon);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

/** 类型管理 */
function handleManagerType(){
  router.push({ path: "/template/smart/assistant/plugin/type" });
}

/** 配置角色脚本 */
function configArticleTemplateScript(row) {
  router.push({ path: "/expert/smart/assistant/tool/script", query: { toolId: row.id } });
}

/** 获取模板类型名称 */
const getPluginTypeName = computed(() => {
  return (key) => {
    const pluginType = pluginCategories.value.find(type => type.label === key);
    return pluginType ? pluginType.label : '未知类型';
  };
});

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
    return delArticleTemplate(applicationIds);
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
  proxy.resetForm("ArticleTemplateRef");
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
  getArticleTemplate(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改模板";

    const item = {
      url: upload.display + response.data.icon ,
      uid: id // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item) ; 
  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ArticleTemplateRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateArticleTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addArticleTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

function getArticleTemplateTypeList() {
  getAllArticleTemplate().then(response => {
    pluginCategories.value = response.data;
  });
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "文章模板导入";
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

  const responseData = response.data ;
  
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

getList();
getArticleTemplateTypeList();

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width: 35px;
    height: 35px;
    border-radius: 50%;
  }
}

.plugin-catalog {
  background: #fafafa;
  height: calc(99vh - 100px);
  border-radius: 2px;
  padding: 9px;

  .catalog-title {
    padding: 10px;
    font-size: 14px;
    font-weight: bold;
    color: #444;
    margin-bottom: 5px;
  }

  .catalog-content {
    .catalog-item {
      padding: 12px 10px;
      color: #443;
      cursor: pointer;
      margin-bottom: 4px;
      border-radius: 4px;
      font-size: 13px;

      &:hover {
        background: #e9eaf3;
      }
    }
    .active {
      background: #e9eaf3;
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