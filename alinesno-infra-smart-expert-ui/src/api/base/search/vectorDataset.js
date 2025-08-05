import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/search/vectorDataset/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  toolSelection: prefix +"toolSelection" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'save' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "deleteDataset" ,
  exportUrl: prefix + "exportExcel",
  search: prefix + "search",
  changeField: prefix + "changeField",
  downloadfile: prefix + "downloadfile",
  catalogManifestTreeSelect: prefix + "catalogManifestTreeSelect",
  updateDatasetConfig: prefix + "updateDatasetConfig"
}

// 查询数据集列表
export function toolSelection(query) {
  return request({
    url: managerUrl.toolSelection ,
    method: 'post',
    params: query
  })
}

// 更新数据集配置
export function updateDatasetConfig(data) {
  return request({
    url: managerUrl.updateDatasetConfig,
    method: 'post',
    data: data
  })
}

// 查询清单下拉树结构
export function catalogManifestTreeSelect() {
  return request({
      url: managerUrl.catalogManifestTreeSelect ,
      method: 'get'
  })
}

// 搜索
export function getSearch(data) {
  return request({
    url: managerUrl.search ,
    method: 'post',
    data: data 
  })
}

// 查询应用列表
export function listDataset(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getDataset(datasetId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(datasetId),
    method: 'get'
  })
}

// 新增应用
export function addDataset(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateDataset(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delDataset(datasetId) {
  return request({
    url: managerUrl.removeUrl + '?datasetId=' + parseStrEmpty(datasetId),
    method: 'delete'
  })
}

// 应用密码重置
export function resetDatasetPwd(datasetId, password) {
  const data = {
    datasetId,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 应用状态修改
export function changeDatasetStatus(datasetId, status) {
  const data = {
    datasetId,
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