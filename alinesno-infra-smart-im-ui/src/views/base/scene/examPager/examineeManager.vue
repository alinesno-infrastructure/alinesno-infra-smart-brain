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

          <div v-for="(item, index) in examineeGroup" 
            :key="index" 
            :class="{ 'selected': item.isSelected }"
            @click="selectGroup(item)"
            class="type-panel-box question-bank-box">
            <div class="type-title">
              <div>
                <i :class="item.icon?item.icon:'fa-solid fa-graduation-cap'" /> {{ item.groupName }}
              </div>
              <el-dropdown v-if="!item.isSpecial" trigger="click" @command="handleGroupCommand($event, item)">
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
              <el-form :model="queryParams" size="default" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

                <el-form-item label="考生名称" prop="name">
                  <el-input v-model="queryParams.name" placeholder="请输入考生名称" clearable
                    style="width: 240px" @keyup.enter="handleQuery" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>

                <el-form-item style="float:right">
                  <el-button type="primary" icon="Plus" plain @click="showAddExamineeDialog">新增</el-button>
                  <el-button type="warning" icon="Upload" plain @click="showImportDialog">导入</el-button>
                  <el-button type="danger" icon="Delete" plain @click="handleBatchDelete" :disabled="!selectedIds.length">批量删除</el-button>
                </el-form-item>

              </el-form>

              <el-table 
                  v-loading="loading" 
                  :data="examineeList"
                  @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column type="index" width="60" align="center" label="序号" />
                <el-table-column label="考生编号" align="left" prop="examineeId" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="考生名称" align="left" prop="name" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="手机号" align="left" prop="phone" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="加入时间" align="center" prop="addTime" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="考试次数" align="center" prop="examCount" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <el-tag v-if="scope.row.examCount > 0" type="success">{{ scope.row.examCount }}</el-tag>
                    <el-tag v-else>0</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="所属分组" align="center" prop="groupName" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <!-- 通过分组id获取到分组名称 -->
                    <el-button text bg v-if="scope.row.groupId" type="success">{{ getGroupById(scope.row.groupId)?.groupName }}</el-button>
                    <el-button text bg v-else>未分组</el-button>
                  </template>
                </el-table-column>

                <el-table-column label="操作" align="center" width="240">
                  <template #default="scope">
                    <el-button type="primary" text bg size="large" @click="handleEdit(scope.row)">
                      <i class="fa-solid fa-pen-to-square"></i> 编辑
                    </el-button>
                    <el-button type="danger" text bg size="large" @click="handleDelete(scope.row)">
                      <i class="fa-solid fa-trash"></i> 删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <pagination
                  v-show="total > 0"
                  :total="total"
                  v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize"
                  @pagination="handleListExaminee"
              />

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
        <el-form-item label="分组名称" prop="groupName">
          <el-input v-model="groupForm.groupName" placeholder="请输入分组名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="groupDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGroup">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加考生对话框 -->
    <el-dialog v-model="examineeDialogVisible" :title="examineeDialogTitle" width="40%">
      <el-form :model="examineeForm" size="large" :rules="examineeRules" ref="examineeFormRef" label-width="100px">
        <el-form-item label="所属分组" prop="groupId">
          <el-select v-model="examineeForm.groupId" placeholder="请选择分组" style="width:100%">
            <el-option 
              v-for="group in examineeGroup.filter(g => !g.isSpecial)" 
              :key="group.id" 
              :label="group.groupName" 
              :value="group.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考生编号" prop="examineeId">
          <el-input v-model="examineeForm.examineeId" placeholder="请输入考生编号/学号"></el-input>
        </el-form-item>
        <el-form-item label="考生姓名" prop="name">
          <el-input v-model="examineeForm.name" placeholder="请输入考生姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="examineeForm.phone" placeholder="请输入手机号码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="examineeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveExaminee">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量导入考生对话框 -->
    <el-dialog v-model="importDialogVisible" title="批量导入考生" width="50%">
      <el-form size="large" label-width="100px">
        <el-form-item label="所属分组">
          <el-select v-model="importForm.groupId" placeholder="请选择分组" style="width:100%">
            <el-option 
            v-for="group in examineeGroup.filter(g => !g.isSpecial)" 
            :key="group.id" :label="group.groupName" :value="group.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考生数据">
          <el-input 
            v-model="importForm.data" 
            type="textarea" 
            :rows="10" 
            placeholder="请输入考生数据，格式为：学号|姓名|手机号码，每行一个考生，例如：
123456|张三|12345678901
234567|李四|12345678901"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImport">导入</el-button>
        </span>
      </template>
    </el-dialog>
  </ExamContainerPanel>
</template>

<script setup>
import ExamContainerPanel from "./common/examContainer";
import { ElMessage, ElMessageBox , ElLoading  } from 'element-plus'

import { 
  listExamineeGroup ,
  addExamineeGroup,
  deleteExamineeGroup,
  updateExamineeGroup,

  listExaminee ,
  addExaminee,
  updateExaminee,
  importExaminee ,
  deleteExaminee,
  deleteExamineeBatch
} from '@/api/base/im/scene/examPaperExaminee'

const router = useRouter();
const route = useRoute();
const { proxy } = getCurrentInstance();
const dateRange = ref([]);

const sceneId = ref(route.query.sceneId)
console.log('sceneId = ' + sceneId.value);

const examineeList = ref([]);
const showSearch = ref(true);
const loading = ref(true);
const total = ref(0);

const examineeGroup = ref([])

const examineeDialogTitle = ref('添加考生')

// 添加响应式变量
const selectedIds = ref([]) // 存储选中的考生ID

// 分组对话框相关
const groupDialogVisible = ref(false)
const isEditGroup = ref(false)
const groupForm = ref({
  groupName: '',
  sceneId: sceneId.value,
  icon: 'fa-solid fa-user',
})
const groupFormRef = ref(null)
const groupRules = {
  groupName: [{ required: true, message: '分组名称不能为空', trigger: 'blur' }],
  icon: [{ required: true, message: '请选择图标', trigger: 'blur' }]
}

// 考生对话框相关
const examineeDialogVisible = ref(false)
const examineeForm = ref({
  examineeId: '',
  name: '',
  phone: '',
  groupId: null,
  sceneId: sceneId.value
})
const examineeFormRef = ref(null)
const examineeRules = {
  examineeId: [{ required: true, message: '考生编号不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '考生姓名不能为空', trigger: 'blur' }],
  groupId: [{ required: true, message: '请选择分组', trigger: 'change' }]
}

// 批量导入相关
const importDialogVisible = ref(false)
const importForm = ref({
  groupId: null,
  data: ''
})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sceneId: sceneId.value,
    name: undefined,
    groupId: undefined
  }
});

const { queryParams } = toRefs(data);

// 显示添加分组对话框
const showAddGroupDialog = () => {
  groupForm.value = {
    groupName: '',
    icon: 'fa-solid fa-user',
    sceneId: sceneId.value
  }
  isEditGroup.value = false
  groupDialogVisible.value = true
}

// 显示添加考生对话框
const showAddExamineeDialog = () => {
  examineeForm.value = {
    examineeId: '',
    name: '',
    phone: '',
    groupId: null,
    sceneId: sceneId.value
  }
  examineeDialogVisible.value = true
}

// 显示批量导入对话框
const showImportDialog = () => {
  importForm.value = {
    groupId: null,
    data: ''
  }
  importDialogVisible.value = true
}

// 编辑考生
const handleEdit = (row) => {
  examineeForm.value = {
    id: row.id, // 添加id字段用于更新
    examineeId: row.examineeId,
    name: row.name,
    phone: row.phone,
    groupId: row.groupId,
    sceneId: sceneId.value
  }
  examineeDialogVisible.value = true
  // 更新对话框标题
  examineeDialogTitle.value = '编辑考生'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 批量删除考生
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请至少选择一条数据')
    return
  }

  ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个考生吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {

    let loadingInstance;

    try {
      loadingInstance = ElLoading.service({
        lock: true,
        text: '正在删除中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      // 调用批量删除API
      const res = await deleteExamineeBatch(selectedIds.value.join(','))
      
      if (res.code === 200) {
        ElMessage.success(`成功删除${selectedIds.value.length}个考生`)
        selectedIds.value = [] // 清空选择
        await handleListExaminee()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除出错:', error)
      ElMessage.error('删除过程中发生错误')
    } finally {
      loadingInstance?.close()
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 处理分组操作命令
const handleGroupCommand = (command, group) => {

   if (group.isSpecial) {
    ElMessage.warning('不能操作"全部"分组')
    return
  }

  if (command === 'edit') {
    editGroup(group)
  } else if (command === 'delete') {
    deleteGroup(group)
  }
}

// 编辑分组
const editGroup = (group) => {
  groupForm.value = group
  isEditGroup.value = true
  groupDialogVisible.value = true
}

// 删除分组
const deleteGroup = (group) => {
  ElMessageBox.confirm('确定要删除['+group.groupName+']这个分组吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteExamineeGroup(group.id).then(res => {
      ElMessage.success('删除成功')
      handleListExamineeGroup();
    })
  }).catch(() => {
    // 用户取消删除
  })
}

// 保存分组
const saveGroup = () => {
  groupFormRef.value.validate(valid => {
    if (valid) {
      groupForm.value.sceneId = sceneId.value

      if (isEditGroup.value) {
        updateExamineeGroup(groupForm.value).then(res => {
          ElMessage.success('修改成功')
          groupDialogVisible.value = false
          handleListExamineeGroup();
        })
      } else {
        addExamineeGroup(groupForm.value).then(res => {
          ElMessage.success('添加成功')
          groupDialogVisible.value = false
          handleListExamineeGroup();
        })
      }
    }
  })
}

// // 保存考生
// const saveExaminee = () => {
//   examineeFormRef.value.validate(valid => {
//     if (valid) {
//       addExaminee(examineeForm.value).then(res => {
//         ElMessage.success('添加成功')
//         examineeDialogVisible.value = false
//         handleListExaminee();
//       })
//     }
//   })
// }

// 保存考生
const saveExaminee = () => {
  examineeFormRef.value.validate(valid => {
    if (valid) {
      if (examineeForm.value.id) {
        // 更新考生
        updateExaminee(examineeForm.value).then(res => {
          ElMessage.success('更新成功')
          examineeDialogVisible.value = false
          handleListExaminee();
        })
      } else {
        // 添加考生
        addExaminee(examineeForm.value).then(res => {
          ElMessage.success('添加成功')
          examineeDialogVisible.value = false
          handleListExaminee();
        })
      }
    }
  })
}

const getGroupById = (id) => {

   // 使用 find 方法替代 forEach
  const foundGroup = examineeGroup.value.find(item => item.id === id)
  console.log('Searching group by id:', id, 'Found:', foundGroup)
  return foundGroup || null

}


// 处理批量导入
const handleImport = async () => {

  let loadingInstance = null ;

  try {
    // 1. 表单验证
    if (!importForm.value.groupId) {
      ElMessage.warning('请选择分组')
      return
    }
    
    if (!importForm.value.data) {
      ElMessage.warning('请输入考生数据')
      return
    }

    // 2. 准备加载状态
    loadingInstance = ElLoading.service({
      lock: true,
      text: '正在导入考生数据...',
      background: 'rgba(0, 0, 0, 0.7)'
    })

    // 3. 解析并提交数据
    const res = await importExaminee({
      groupId: importForm.value.groupId,
      sceneId: sceneId.value,
      data: importForm.value.data
    })

    // 4. 处理响应
    if (res.code === 200) {
      if (res.data) {
        // 处理重复数据
        let errorMsg = '导入失败，发现重复数据：\n'
        
        if (res.data.inputDuplicates && res.data.inputDuplicates.length > 0) {
          errorMsg += `· 导入数据内部重复: ${res.data.inputDuplicates.join('、')}\n`
        }
        
        if (res.data.dbDuplicates && res.data.dbDuplicates.length > 0) {
          errorMsg += `· 与系统已有数据重复: ${res.data.dbDuplicates.join('、')}`
        }
        
        ElMessage.error(errorMsg)
        
        // 高亮显示重复行（假设有对应方法）
        highlightDuplicateRows(res.data)
      } else {
        ElMessage.success(res.msg)
        importDialogVisible.value = false
        await handleListExaminee()
      }
    } 
  } catch (error) {
    console.error('导入出错:', error)
    ElMessage.error('导入过程中发生错误')
  } finally {
    // 关闭加载状态
    loadingInstance?.close()
  }
}

// 高亮显示重复行（示例方法）
const highlightDuplicateRows = (duplicates) => {
  // 这里可以实现文本域中高亮显示重复行的逻辑
  // 例如使用mark.js库或其他方式标记重复内容
  console.log('需要高亮的重复数据:', duplicates)
}

// 选择分组
const selectGroup = (group) => {
  examineeGroup.value.forEach(item => {
    item.isSelected = item.id === group.id
  })

  // 如果是"全部"分组，查询时不带groupId参数
  queryParams.value.groupId = group.isSpecial ? undefined : group.id
  handleListExaminee()
}

// 删除考生
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除考生[${row.name}]吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteExaminee(row.id).then(res => {
      ElMessage.success('删除成功')
      handleListExaminee()
    })
  })
}

const handleQuery = () => { 
  handleListExaminee();
}

const resetQuery = () => { 
  queryParams.value.name = null;
  queryParams.value.groupId = null;
  handleListExaminee();
}

/** 查询考生列表 */
const handleListExaminee= async () => {
  loading.value = true;
  await listExaminee(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    examineeList.value = res.rows;
    total.value = res.total;
  });
};

// 查询所有分组
const handleListExamineeGroup= async() => {
  await listExamineeGroup({sceneId: sceneId.value}).then(res => {
    examineeGroup.value = res.data.map(item => ({
      ...item,
      isSelected: false,
      isSpecial: false  // 标记是否为特殊分组
    }))
    
    if (examineeGroup.value.length > 0) {

      // 添加全部分组到列表中，位置默认选择第一个
      examineeGroup.value.unshift({
        id: null,
        groupName: '全部',
        icon: 'fa-solid fa-users',
        isSelected: true,
        isSpecial: true  // 标记为特殊分组
      })

      selectGroup(examineeGroup.value[0])
    }

  })
}

onMounted(async() => {
  await handleListExamineeGroup()
  // await handleListExaminee()
})

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
    border-left: 3px solid #fafafa;  // Blue left border as an indicator
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
    margin-bottom:0px; 
    display: flex; 
    justify-content: space-between; 
    align-items: center;
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

.selected {
  background: #e6f7ff !important; 
  border-left: 3px solid #1d75b0 !important;
  color: #1d75b0;
  
  .el-dropdown-link {
    color: #1d75b0;             
  }
  
  &:hover {
    background: #e6f7ff !important; 
  }
}

/* 添加在style部分 */
.el-button--danger[plain] {
  &:not(:disabled) {
    background-color: #fff6f6;
    border-color: #f89898;
    color: #f56c6c;
    
    &:hover {
      background-color: #fef0f0;
    }
  }
}
</style>