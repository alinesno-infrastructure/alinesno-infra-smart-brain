import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

const prefix = '/api/infra/smart/assistant/scene/generalAgentTemplate/'
const prefixGroup = '/api/infra/smart/assistant/scene/generalAgentTemplateGroup/'

var managerUrl = {
  datatables : prefix +"datatables" ,

  deleteTemplateGroup: prefixGroup +"deleteTemplateGroup" , 
  addTemplateGroup: prefixGroup +"saveTemplateGroup" , 
  updateTemplateGroup: prefixGroup +"updateTemplateGroup" , 
  getAllTemplateGroup: prefixGroup +"getAllTemplateGroup" , 
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