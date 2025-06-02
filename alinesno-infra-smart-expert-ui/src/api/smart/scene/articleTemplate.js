import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/articleTemplate/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  toolSelection: prefix +"toolSelection" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'saveArticleTemplate' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile",
  validateArticleTemplateScript: prefix + "validateArticleTemplateScript",
  updateArticleTemplateScript: prefix + "updateArticleTemplateScript",
  getAllArticleTemplate: prefix + "getAllArticleTemplate",
}

// 获取到所有的工具列表
export function getAllArticleTemplate() {
  return request({
    url: managerUrl.getAllArticleTemplate ,
    method: 'get'
  })
}

// 验证脚本任务
export function validateArticleTemplateScript(data) {
  return request({
    url: managerUrl.validateArticleTemplateScript ,
    method: 'post',
    data: data
  })
}

// 提交脚本任务
export function updateArticleTemplateScript(data) {
  return request({
    url: managerUrl.updateArticleTemplateScript , 
    method: 'post',
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

// 查询应用列表
export function toolSelection(query) {
  return request({
    url: managerUrl.toolSelection,
    method: 'post',
    params: query
  })
}

// 查询应用列表
export function listArticleTemplate(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getArticleTemplate(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增应用
export function addArticleTemplate(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateArticleTemplate(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delArticleTemplate(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 应用密码重置
export function resetArticleTemplatePwd(id, password) {
  const data = {
    id,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 应用状态修改
export function changeArticleTemplateStatus(id, status) {
  const data = {
    id,
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