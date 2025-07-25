import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

const prefix = '/api/infra/smart/assistant/scene/contentFormatterLayoutGroup/'

// 查询分组列表
export function listGroups(query) {
  return request({
    url: prefix + 'datatables',
    method: 'post',
    params: query
  })
}

// 查询分组详细
export function getGroup(id) {
  return request({
    url: prefix + 'getGroup?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增分组
export function addGroup(data) {
  return request({
    url: prefix + 'addGroup',
    method: 'post',
    data: data
  })
}

// 修改分组
export function updateGroup(data) {
  return request({
    url: prefix + 'updateGroup',
    method: 'put',
    data: data
  })
}

// 删除分组
export function delGroup(id , groupType) {
  return request({
    url: prefix + 'delGroup?id=' + parseStrEmpty(id) + '&groupType=' + parseStrEmpty(groupType),
    method: 'delete'
  })
}