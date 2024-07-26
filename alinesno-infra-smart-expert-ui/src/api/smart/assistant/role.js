import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/role/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'save' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadfile: prefix + "downloadfile",
  getRoleChainByChainId: prefix + "getRoleChainByChainId",
  saveRoleChainInfo: prefix + "saveRoleChainInfo",
  catalogTreeSelect: prefix + "catalogTreeSelect",
  runRoleChainByRoleId: prefix + "runRoleChainByRoleId",
  listAllRole: prefix + "listAllRole",
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