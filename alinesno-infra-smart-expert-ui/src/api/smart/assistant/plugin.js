import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/plugin/' ;
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
  downloadfile: prefix + "downloadfile"
}

// 查询应用列表
export function listPlugin(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getPlugin(PluginId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(PluginId),
    method: 'get'
  })
}

// 新增应用
export function addPlugin(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updatePlugin(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delPlugin(PluginId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(PluginId),
    method: 'delete'
  })
}

// 应用密码重置
export function resetPluginPwd(PluginId, password) {
  const data = {
    PluginId,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 应用状态修改
export function changePluginStatus(PluginId, status) {
  const data = {
    PluginId,
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