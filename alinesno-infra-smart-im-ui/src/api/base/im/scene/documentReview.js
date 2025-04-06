import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReview/' ;

var managerUrl = {
  getScene: prefix +"getScene",
  getPreviewDocx: prefix +"getPreviewDocx",
}

// 获取预览文档
export function getPreviewDocx(sceneId) {
  return request({
    url: managerUrl.getPreviewDocx + '?sceneId=' + parseStrEmpty(sceneId),
    responseType: 'arraybuffer', // 显式声明返回二进制流
    method: 'get'
  })
}

// 查询场景详细
export function getScene(id) {
  return request({
    url: managerUrl.getScene + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}