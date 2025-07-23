import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

const prefix = '/api/infra/smart/assistant/scene/contentFormatterOfficeConfig/'

// 获取配置详情
export function getContentFormatterOfficeConfig() {
  return request({
    url: prefix + 'getOfficeConfig',
    method: 'get'
  })
}

// 新增配置
export function addContentFormatterOfficeConfig(data) {
  return request({
    url: prefix + 'addOfficeConfig',
    method: 'post',
    data: data
  })
}

// 更新配置
export function updateContentFormatterOfficeConfig(data) {
  return request({
    url: prefix + 'updateOfficeConfig',
    method: 'put',
    data: data
  })
}

// 删除配置
export function deleteContentFormatterOfficeConfig(id) {
  return request({
    url: prefix + 'delete/' + parseStrEmpty(id),
    method: 'delete'
  })
}