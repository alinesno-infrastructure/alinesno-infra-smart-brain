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
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  search: prefix + "search",
  changeField: prefix + "changeField",
  downloadfile: prefix + "downloadfile"
}

// 查询数据集列表
export function toolSelection(query) {
  return request({
    url: managerUrl.toolSelection ,
    method: 'post',
    params: query
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

// 查询数据集列表
export function listDataset(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询数据集详细
export function getDataset(DatasetId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(DatasetId),
    method: 'get'
  })
}

// 新增数据集
export function addDataset(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改数据集
export function updateDataset(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除数据集
export function delDataset(DatasetId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(DatasetId),
    method: 'delete'
  })
}

// 数据集密码重置
export function resetDatasetPwd(DatasetId, password) {
  const data = {
    DatasetId,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 数据集状态修改
export function changeDatasetStatus(DatasetId, status) {
  const data = {
    DatasetId,
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