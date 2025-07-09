<template>
  <ExamContainerPanel>
    <el-row>
      <el-col :span="4">
        <div style="padding: 10px;font-weight: bold;font-size: 14px; display: flex; justify-content: space-between; align-items: center;">
          <div>
            <i class="fa-solid fa-book-open-reader"></i> 考生管理
          </div>
          <el-button type="primary" size="large" text bg @click="showAddGroupDialog">
            <i class="fa-solid fa-plus"></i> 添加分组
          </el-button>
        </div>

        <!-- 考生分组 -->
        <div class="select-type-panel">
          <div v-for="(item, index) in examineeGroup" :key="index" class="type-panel-box question-bank-box">
            <div class="type-title" style="margin-bottom:0px; display: flex; justify-content: space-between; align-items: center;">
              <div @click="selectGroup(item)">
                <i :class="item.icon" /> {{ item.label }}
              </div>
              <el-dropdown trigger="click" @command="handleGroupCommand($event, item)">
                <span class="el-dropdown-link">
                  <i class="fa-solid fa-ellipsis-vertical"></i>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">修改分组</el-dropdown-item>
                    <el-dropdown-item command="delete">删除分组</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

      </el-col>
      <el-col :span="20">
        <div style="margin-left:10px">
          <el-row :gutter="20" style="padding-bottom:30px;margin-top:5px;">
            <!--应用数据-->
            <el-col :span="24" :xs="24">
              <el-form :model="queryParams" size="large" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

                <el-form-item label="考生名称" prop="roleName">
                  <el-input v-model="queryParams['condition[roleName|like]']" placeholder="请输入角色名称" clearable
                    style="width: 240px" @keyup.enter="handleQuery" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
              </el-form>

              <el-table v-loading="loading" :data="StoreRoleList">
                <el-table-column type="index" width="40" align="center" />
                <el-table-column label="考生名称" align="left" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="手机号" align="left" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="加入时间" align="left" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="考试次数" align="left" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="所属分组" align="left" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true"></el-table-column>

                <el-table-column label="添加频道" align="center" width="240" key="storagePath" prop="storagePath"
                  v-if="columns[5].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <el-button type="warning" text bg :loading="runChainAgentLoading" size="large"
                      @click="handleSettings(scope.row)">
                      <i class="fa-solid fa-cog"></i> 分组配置
                    </el-button>
                    <el-button type="danger" text bg :loading="runChainAgentLoading" size="large"
                      @click="handleDelete(scope.row)">
                      <i class="fa-solid fa-trash"></i> 删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList" />
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>

    <!-- 添加/编辑分组对话框 -->
    <el-dialog v-model="groupDialogVisible" :title="isEditGroup ? '修改分组' : '添加分组'" width="30%">
      <el-form :model="groupForm" size="large" :rules="groupRules" ref="groupFormRef" label-width="100px">
        <el-form-item label="图标" prop="icon">
          <el-input v-model="groupForm.icon" placeholder="请输入图标类名，如：fa-solid fa-user">
            <template #append>
              <i :class="groupForm.icon || 'fa-solid fa-user'" style="margin-right: 5px;"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="分组名称" prop="label">
          <el-input v-model="groupForm.label" placeholder="请输入分组名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="groupDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGroup">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </ExamContainerPanel>
</template>

<script setup>
import ExamContainerPanel from "./common/examContainer";
import { storeRoleList } from '@/api/base/im/store'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter();
const { proxy } = getCurrentInstance();
const dateRange = ref([]);

const StoreRoleList = ref([]);
const showSearch = ref(true);
const loading = ref(true);
const total = ref(0);

const size = ref('未考试')
const sizeOptions = ['未考试', '考试中', '已结束']

const examineeGroup = ref([
    {
        code: "basic-knowledge",
        label: "默认分组",
        icon: "fa-solid fa-graduation-cap"
    },
])

// 分组对话框相关
const groupDialogVisible = ref(false)
const isEditGroup = ref(false)
const currentGroupIndex = ref(null)
const groupForm = ref({
  label: '',
  icon: 'fa-solid fa-user',
  code: ''
})
const groupFormRef = ref(null)
const groupRules = {
  label: [{ required: true, message: '分组名称不能为空', trigger: 'blur' }],
  icon: [{ required: true, message: '请选择图标', trigger: 'blur' }]
}

// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `角色名称`, visible: true },
  { key: 2, label: `角色描述`, visible: true },
  { key: 3, label: `所属领域`, visible: true },
  { key: 4, label: `角色级别`, visible: true },
  { key: 5, label: `安全存储路径`, visible: true },
  { key: 6, label: `应用目标`, visible: true },
  { key: 7, label: `创建时间`, visible: true },
  { key: 8, label: `编辑`, visible: true },
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    roleId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "角色名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    responsibilities: [{ required: true, message: "角色描述不能为空", trigger: "blur" }],
    domain: [{ required: true, message: "所属领域不能为空", trigger: "blur" }],
    roleLevel: [{ required: true, message: "角色级别不能为空", trigger: "blur" }],
    storagePath: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    target: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
  },
  chainForm: {
    roleId: undefined,
  },
  chainRules: {
    chainName: [{ required: true, message: "链路名称不能为空", trigger: "blur" }],
    elData: [{ required: true, message: "链路流程不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules, chainForm, chainRules } = toRefs(data);

// 显示添加分组对话框
const showAddGroupDialog = () => {
  groupForm.value = {
    label: '',
    icon: 'fa-solid fa-user',
    code: ''
  }
  isEditGroup.value = false
  groupDialogVisible.value = true
}

// 处理分组操作命令
const handleGroupCommand = (command, group) => {
  const index = examineeGroup.value.findIndex(item => item.code === group.code)
  if (index === -1) return
  
  if (command === 'edit') {
    editGroup(index)
  } else if (command === 'delete') {
    deleteGroup(index)
  }
}

// 编辑分组
const editGroup = (index) => {
  currentGroupIndex.value = index
  groupForm.value = { ...examineeGroup.value[index] }
  isEditGroup.value = true
  groupDialogVisible.value = true
}

// 删除分组
const deleteGroup = (index) => {
  ElMessageBox.confirm('确定要删除这个分组吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    examineeGroup.value.splice(index, 1)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 用户取消删除
  })
}

// 保存分组
const saveGroup = () => {
  groupFormRef.value.validate(valid => {
    if (valid) {
      if (isEditGroup.value) {
        // 编辑现有分组
        examineeGroup.value[currentGroupIndex.value] = {
          ...groupForm.value,
          code: groupForm.value.code || generateGroupCode(groupForm.value.label)
        }
        ElMessage.success('修改成功')
      } else {
        // 添加新分组
        examineeGroup.value.push({
          ...groupForm.value,
          code: generateGroupCode(groupForm.value.label)
        })
        ElMessage.success('添加成功')
      }
      groupDialogVisible.value = false
    }
  })
}

// 选择分组
const selectGroup = (group) => {
  // 这里可以添加选择分组后的逻辑
  console.log('Selected group:', group)
}

// 生成分组code
const generateGroupCode = (label) => {
  return label.replace(/[\s\u4e00-\u9fa5]/g, '').toLowerCase() + '-' + Date.now()
}

/** 查询角色列表 */
const handleStoreRoleList = () => {
  loading.value = true;
  storeRoleList(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    StoreRoleList.value = res.rows;
    total.value = res.total;
  });
};

/** 选择角色入频道 */
const handleChainAgent = (row) => {
  emit('handleChainAgent', row);
}

handleStoreRoleList();

</script>

<style lang="scss" scoped>
.select-type-panel {
  width: 100%;
  text-align: left;
  font-size: 14px;

  .question-bank-box {
    font-weight: normal;
    background: #fafafa;
    border-radius: 5px;
    cursor: pointer;
    padding: 0 10px;

    &:hover {
      background: #e5e5e5;
    }
  }

  .type-panel-box {
    margin-bottom:10px;

    .type-title {
      font-weight: normal;
    }
  }

  .type-title {
    text-align: left;
    padding: 15px 5px;
    font-weight: bold;
    margin-bottom: 5px;
  }

  .type-item-list {
    column-count: 2;

    .item{
      display: flex;
      align-items: center;
      cursor: pointer;
      gap: 5px;
      padding: 6px;
      background: #f5f5f5;
      margin-bottom: 5px;
      border-radius: 5px;

      &:hover {
        background: #e5e5e5;
      }
    }
  }
}

.el-dropdown-link {
  cursor: pointer;
  padding: 0 5px;
  color: #666;
  
  &:hover {
    color: #409EFF;
  }
}
</style>