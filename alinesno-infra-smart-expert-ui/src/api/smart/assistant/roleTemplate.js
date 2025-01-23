import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 插件接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/assistant/roleTemplate/' ;
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
  uploadProjectTemplate: prefix + "uploadProjectTemplate",
  getFilterTemplate: prefix + "getFilterTemplate",
  databaseList: prefix + "databaseList",
  syncTemplates: prefix + "syncTemplates",
  useTemplate: prefix + "useTemplate",
}

// 使用模板
export function useTemplate(templateId) {
  let data = {
    id: parseStrEmpty(templateId)
  }
  return request({
    url: managerUrl.useTemplate ,
    method: 'post',
    data: data
  })
}



// 列出项目模板
export function listBuildProjectTemplate(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 上传项目模板
export function uploadProjectTemplate(data) {
return request({
  url: managerUrl.uploadProjectTemplate,
  method: 'post',
  data: data ,
})
}

// 同步仓库
export function syncTemplates() {
return request({
  url: managerUrl.syncTemplates,
  method: 'get',
})
}

// 查询用户管理列表
export function getFilterTemplate() {
return request({
  url: managerUrl.getFilterTemplate,
  method: 'post',
})
}

// 查询插件列表
export function listPlugin(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询插件详细
export function getPlugin(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增插件
export function addPlugin(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改插件
export function updatePlugin(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除插件
export function delPlugin(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}
