import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

// 应用模板
const prefix = '/api/infra/smart/assistant/scene/generalAgentTemplate/'

// 应用分组
const prefixGroup = '/api/infra/smart/assistant/scene/generalAgentTemplateGroup/'

var managerUrl = {
  datatables : prefix +"datatables" ,
  getTemplateDetail : prefix +"getTemplateDetail" ,
  getTemplateByType : prefix +"getTemplateByType" ,
  saveTemplate : prefix +"saveTemplate" , 
  updateTemplate : prefix +"updateTemplate" , 
  removeUrl: prefix + "delete" ,
  updateTemplateConfig: prefix + "updateTemplateConfig",

  deleteTemplateGroup: prefixGroup +"deleteTemplateGroup" , 
  addTemplateGroup: prefixGroup +"saveTemplateGroup" , 
  updateTemplateGroup: prefixGroup +"updateTemplateGroup" , 
  getAllTemplateGroup: prefixGroup +"getAllTemplateGroup" , 
}

// 更新模板
export function updateTemplateConfig(data) {
  return request({
    url: managerUrl.updateTemplateConfig ,
    method: 'post',
    data: data
  })
}

// 查询应用列表
export function listTemplate(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getTemplate(id) {
  return request({
    url: managerUrl.getTemplateDetail + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增应用
export function addTemplate(data) {
  return request({
    url: managerUrl.saveTemplate ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateTemplate(data) {
  return request({
    url: managerUrl.updateTemplate ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delTemplate(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}


// 查询模板分组
export function deleteTemplateGroup(id) {
  return request({
    url: managerUrl.deleteTemplateGroup + '?id=' + parseStrEmpty(id) ,
    method: 'delete' 
  })
}

// 查询模板分组
export function getAllTemplateGroup() {
  return request({
    url: managerUrl.getAllTemplateGroup ,
    method: 'get' 
  })
}

// 新增模板分组
export function addTemplateGroup(data) {
  return request({
    url: managerUrl.addTemplateGroup ,
    method: 'post',
    data: data
  })
}

// 修改模板分组
export function updateTemplateGroup(data) {
  return request({
    url: managerUrl.updateTemplateGroup ,
    method: 'put',
    data: data
  })
}