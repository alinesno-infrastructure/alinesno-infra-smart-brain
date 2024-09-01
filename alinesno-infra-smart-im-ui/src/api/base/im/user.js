import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/im/user/' ;
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
  addChainAgent: prefix + "addChainAgent",
  downloadfile: prefix + "downloadfile",
  getUserChainByChainId: prefix + "getUserChainByChainId",
  saveUserChainInfo: prefix + "saveUserChainInfo",
  runUserChainByUserId: prefix + "runUserChainByUserId",
  listAllUser: prefix + "listAllUser",
}

// 添加Agent到当前的频道中 
export function addChainAgent(roleId , channelId) {
  return request({
    url: managerUrl.addChainAgent+ '?channelId=' + parseStrEmpty(channelId) + '&roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 新增应用
export function addUserChain(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 获取到所有角色
export function listAllUser() {
  return request({
    url: managerUrl.listAllUser , 
    method: 'get'
  })
}

// 运行角色流程
export function runUserChainByUserId(roleId) {
  return request({
    url: managerUrl.runUserChainByUserId + '?roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 新增角色工作流
export function saveUserChainInfo(roleChain , roleId) {
  return request({
    url: managerUrl.saveUserChainInfo + '?roleId=' + roleId ,
    method: 'post',
    data: roleChain
  })
}

// 查询角色流程 
export function getUserChainByChainId(roleId) {
  return request({
    url: managerUrl.getUserChainByChainId + '?roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 查询应用列表
export function listUser(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getUser(UserId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(UserId),
    method: 'get'
  })
}

// 新增应用
export function addUser(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateUser(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delUser(UserId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(UserId),
    method: 'delete'
  })
}

// 应用密码重置
export function resetUserPwd(UserId, password) {
  const data = {
    UserId,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 应用状态修改
export function changeUserStatus(UserId, status) {
  const data = {
    UserId,
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