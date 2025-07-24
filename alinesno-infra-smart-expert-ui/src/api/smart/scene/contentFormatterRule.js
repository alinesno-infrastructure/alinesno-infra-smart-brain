import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

const prefix = '/api/infra/smart/assistant/scene/contentFormatterRule/'

// 查询规则列表
export function listRules(query) {
  return request({
    url: prefix + 'datatables',
    method: 'post',
    params: query
  })
}

// 查询规则详细
export function getRule(id) {
  return request({
    url: prefix + 'getRule?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增规则
export function addRule(data) {
  return request({
    url: prefix + 'addRule' ,
    method: 'post',
    data: data
  })
}

// 修改规则
export function updateRule(data) {
  return request({
    url: prefix + 'updateRule'  ,
    method: 'put',
    data: data
  })
}

// 删除规则
export function delRule(id) {
  return request({
    url: prefix + 'delRule?id=' + parseStrEmpty(id),
    method: 'delete'
  })
}