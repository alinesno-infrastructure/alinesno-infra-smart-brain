import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/role/' ;  // 角色

var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'createRole' ,
  updateUrl: prefix +"modify" ,
  modifyInfo: prefix +"modifyInfo" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  changeSaleField: prefix + "changeSaleField",
  downloadFile: prefix + "downloadFile",
  getRoleChainByChainId: prefix + "getRoleChainByChainId",
  saveRoleChainInfo: prefix + "saveRoleChainInfo",
  catalogTreeSelect: prefix + "catalogTreeSelect",
  runRoleChainByRoleId: prefix + "runRoleChainByRoleId",
  updatePromptContent: prefix + "updatePromptContent",
  listAllRole: prefix + "listAllRole",
  validateRoleScript: prefix + "validateRoleScript",
  updateRoleScript: prefix + "updateRoleScript",
  recommended: prefix + "recommended",
  getRoleWithTool: prefix + "getRoleWithTool",
  saveRoleWithTool: prefix + "saveRoleWithTool",
  validateReActRole: prefix + "validateReActRole",
  findOrg: prefix + "findOrg",
  confirmPushOrg: prefix + "confirmPushOrg",
  saveRoleWithReActConfig: prefix + "saveRoleWithReActConfig",
  saveRoleWithDeepSearchConfig: prefix + "saveRoleWithDeepSearchConfig",
  saveRoleWithWorkflowConfig: prefix + "saveRoleWithWorkflowConfig",
  saveRoleWithScriptConfig: prefix + "saveRoleWithScriptConfig",
  updateFlowConfig: prefix + "updateFlowConfig",
  publishRole: prefix + "publishRole",
  listPublicRole: prefix + "listPublicRole",
  updateWelcomeConfig: prefix + "updateWelcomeConfig",
  getAgentLevel: prefix + "getLevel",
}

// 获取到所有的角色等级
export function getAgentLevel() {
  return request({
    url: managerUrl.getAgentLevel ,
    method: 'get'
  })
}

// 更新欢迎界面配置
export function updateWelcomeConfig(data) {
  return request({
    url: managerUrl.updateWelcomeConfig,
    method: 'post',
    data: data
  })
}

// 列出所有公共商店角色
export function listPublicRole() {
  return request({
    url: managerUrl.listPublicRole ,
    method: 'get'
  })
}

// 发布角色
export function publishRole(roleId) {
  return request({
    url: managerUrl.publishRole + '?roleId=' + parseStrEmpty(roleId) ,
    method: 'post',
    data: data
  })
}

// 更新流程角色配置
export function updateFlowConfig(data , roleId) {
  return request({
    url: managerUrl.updateFlowConfig + '?roleId=' + parseStrEmpty(roleId) ,
    method: 'post',
    data: data
  })
}

// 保存角色脚本
export function saveRoleWithScriptConfig(data) {
  return request({
    url: managerUrl.saveRoleWithScriptConfig,
    method: 'post',
    data: data
  })
}

// 保存Workflow角色配置
export function saveRoleWithWorkflowConfig(data) {
  return request({
    url: managerUrl.saveRoleWithWorkflowConfig,
    method: 'post',
    data: data
  })
}

// 保存ReAct角色配置
export function saveRoleWithReActConfig(data) {
  return request({
    url: managerUrl.saveRoleWithReActConfig,
    method: 'post',
    data: data
  })
}

// 保存DeepSearch角色配置
export function saveRoleWithDeepSearchConfig(data) {
  return request({
    url: managerUrl.saveRoleWithDeepSearchConfig,
    method: 'post',
    data: data
  })
}

// 列出推荐角色
// export function listPushOrgRole() {
//   return request({
//     url: managerUrl.listPushOrgRole,
//     method: 'get'
//   })
// }

// 确认推送组织
export function confirmPushOrg(data) {
  return request({
    url: managerUrl.confirmPushOrg ,
    method: 'post',
    data: data
  })
}

// 通过id查询组织
export function findOrg(roleId) {
  return request({
    url: managerUrl.findOrg + '?orgId=' + parseStrEmpty(roleId) ,
    method: 'get'
  })
}

// 验证ReAct角色
export function validateReActRole(data) {
  return request({
    url: managerUrl.validateReActRole ,
    method: 'post',
    data: data
  })
}

// 保存用户信息和工具信息
export function saveRoleWithTool(data) {
  return request({
    url: managerUrl.saveRoleWithTool ,
    method: 'post',
    data: data
  })
}

// 获取到角色和工具
export function getRoleWithTool(roleId) {
  return request({
    url: managerUrl.getRoleWithTool + '?roleId=' + parseStrEmpty(roleId) ,
    method: 'get'
  })
}

// 推荐频道角色
export function recommended(roleId) {
  return request({
    url: managerUrl.recommended+'?roleId=' + parseStrEmpty(roleId) ,
    method: 'get'
  })
}

// 验证脚本任务
export function validateRoleScript(data) {
  return request({
    url: managerUrl.validateRoleScript ,
    method: 'post',
    data: data
  })
}

// 提交脚本任务
export function updateRoleScript(data) {
  return request({
    url: managerUrl.updateRoleScript , 
    method: 'post',
    data: data
  })
}

// 更新角色PromptContent
export function updatePromptContent(data , id) {
  return request({
    url: managerUrl.updatePromptContent + "?roleId=" + parseStrEmpty(id) , 
    method: 'post',
    data: data
  })
}

// 查询部门下拉树结构
export function catalogTreeSelect() {
  return request({
    url: managerUrl.catalogTreeSelect , 
    method: 'get'
  })
}

// 获取到所有角色
export function listAllRole() {
  return request({
    url: managerUrl.listAllRole , 
    method: 'get'
  })
}

// 运行角色流程
export function runRoleChainByRoleId(roleId , text) {
  return request({
    url: managerUrl.runRoleChainByRoleId + '?roleId=' + parseStrEmpty(roleId) + '&text=' + parseStrEmpty(text),
    method: 'get'
  })
}

// 新增角色工作流
export function saveRoleChainInfo(roleChain , roleId) {
  return request({
    url: managerUrl.saveRoleChainInfo + '?roleId=' + roleId ,
    method: 'post',
    data: roleChain
  })
}

// 查询角色流程 
export function getRoleChainByChainId(roleId) {
  return request({
    url: managerUrl.getRoleChainByChainId + '?roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 查询应用列表
export function listRole(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getRole(RoleId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(RoleId),
    method: 'get'
  })
}

// 新增应用
export function addRole(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateRole(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

export function modifyInfo(data) {
  return request({
    url: managerUrl.modifyInfo ,
    method: 'post',
    data: data
  })
}

// 删除应用
export function delRole(RoleId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(RoleId),
    method: 'delete'
  })
}

// 应用密码重置
export function resetRolePwd(RoleId, password) {
  const data = {
    RoleId,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}


// 修改字段
export function changStatusField(data){
  return request({
    url: managerUrl.changeField ,
    method: 'post',
    data: data
  })
}

// 是否可出售
export function changeSaleField(data){
  return request({
    url: managerUrl.changeSaleField,
    method: 'post',
    data: data
  })
}


// 应用状态修改
export function changeRoleStatus(RoleId, status) {
  const data = {
    RoleId,
    status
  }
  return request({
    url: '/api/infra/base/starter/application/changeStatus',
    method: 'put',
    data: data
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/api/infra/base/starter/application/deptTree',
    method: 'get'
  })
}