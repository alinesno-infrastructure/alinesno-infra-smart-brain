import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/globalConfig/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'saveGlobalConfig' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile",
  getByOrg: prefix + "getByOrg"
}

// 通过机构查询应用
export function getByOrg() {
  return request({
    url: managerUrl.getByOrg , 
    method: 'get'
  })
} 

// 查询应用列表
export function listGlobalConfig(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getGlobalConfig(GlobalConfigId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(GlobalConfigId),
    method: 'get'
  })
}

// 新增应用
export function addGlobalConfig(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateGlobalConfig(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delGlobalConfig(GlobalConfigId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(GlobalConfigId),
    method: 'delete'
  })
}

// 应用密码重置
export function resetGlobalConfigPwd(GlobalConfigId, password) {
  const data = {
    GlobalConfigId,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 应用状态修改
export function changeGlobalConfigStatus(GlobalConfigId, status) {
  const data = {
    GlobalConfigId,
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