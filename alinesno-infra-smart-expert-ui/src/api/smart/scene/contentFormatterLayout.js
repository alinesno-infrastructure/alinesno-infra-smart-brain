import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

const prefix = '/api/infra/smart/assistant/scene/contentFormatterLayout/'

// 查询排版模板列表
export function listLayoutTemplates(query) {
  return request({
    url: prefix + 'datatables',
    method: 'post',
    params: query
  })
}

// 查询排版模板详情
export function getLayoutTemplate(id) {
  return request({
    url: prefix + 'getLayoutTemplate?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增排版模板
export function addLayoutTemplate(data) {
  return request({
    url: prefix + 'addLayoutTemplate',
    method: 'post',
    data: data
  })
}

// 修改排版模板
export function updateLayoutTemplate(data) {
  return request({
    url: prefix + 'updateLayoutTemplate',
    method: 'put',
    data: data
  })
}

// 修改排版模板
export function updateLayoutInfoTemplate(data) {
  return request({
    url: prefix + 'updateLayoutInfoTemplate',
    method: 'put',
    data: data
  })
} 

// 删除排版模板
export function delLayoutTemplate(id) {
  return request({
    url: prefix + 'delLayoutTemplate?id=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 预览排版模板
export function previewLayoutTemplate(id) {
  return request({
    url: prefix + 'previewLayoutTemplate?id='  + parseStrEmpty(id),
    method: 'get'
  })
}