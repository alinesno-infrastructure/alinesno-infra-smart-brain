import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/longTextTemplateGroup/' ;
var managerUrl = {
  updateUrl: prefix +"updateLongTextTemplateGroup" ,
  saveUrl: prefix + 'saveLongTextTemplateGroup' ,
  detailUrl: prefix +"detail",
  getAllLongTextTemplateGroup: prefix + "getAllLongTextTemplateGroup",
  removeUrl: prefix + "delete" ,
}

// 获取到所有的工具列表
export function getAllLongTextTemplateGroup() {
  return request({
    url: managerUrl.getAllLongTextTemplateGroup ,
    method: 'get'
  })
}

// 查询应用详细
export function getLongTextTemplateGroup(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增应用
export function addLongTextTemplateGroup(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateLongTextTemplateGroup(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delLongTextTemplateGroup(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}
