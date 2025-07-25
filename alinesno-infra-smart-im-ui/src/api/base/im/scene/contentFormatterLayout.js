import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/contentFormatterLayout/' ;

var managerUrl = {
  selectOrgLayout: prefix +"selectOrgLayout",
  getLayoutTemplate: prefix +"getLayoutTemplate",
  previewLayoutTemplate: prefix +"previewLayoutTemplate",
}

// 查询界面排版数据
export function selectOrgLayout() {
  return request({
    url: managerUrl.selectOrgLayout,
    method: 'get'
  })
}

// 查询排版模板详情
export function getLayoutTemplate(id) {
  return request({
    url: managerUrl.getLayoutTemplate,
    method: 'get',
    params: { id }
  })
}

// 预览排版模板
export function previewLayoutTemplate(id) {
  return request({
    url: managerUrl.previewLayoutTemplate,
    method: 'get',
    params: { id }
  })
}