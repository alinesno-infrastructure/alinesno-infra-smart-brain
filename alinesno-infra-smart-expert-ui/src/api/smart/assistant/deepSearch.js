import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/deepSearch/' ;  // 角色

var managerUrl = {
  getOutputPreviewUrl: prefix +"getOutputPreviewUrl",
  getOutputMarkdownContent: prefix +"getOutputMarkdownContent"
}

// 获取输出预览
export function getOutputPreviewUrl(storageId) {
  return request({
    url: managerUrl.getOutputPreviewUrl + "?storageId=" + storageId,
    method: 'get'
  })
}

// 获取到markdown内容 
export function getOutputMarkdownContent(storageId) {
  return request({
    url: managerUrl.getOutputMarkdownContent + "?storageId=" + storageId,
    method: 'get'
  })
}