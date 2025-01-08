import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/workflow/' ;
var managerUrl = {
  detailUrl: prefix +"detail",
  updateContent: prefix + "updateContent",
  processParagraph: prefix +"processParagraph",
}

// 查询应用详细
export function getMessage(messageId) {
  return request({
    url: managerUrl.detailUrl + '?messageId=' + parseStrEmpty(messageId),
    method: 'get'
  })
}

// 更新内容
export function updateContent(data) {
  return request({
    url: managerUrl.updateContent,
    method: 'post',
    headers: {
      isEncrypt: true 
    },
    data: data
  })
}

// 内容片断修改
export function processParagraph(data) {
  return request({
    url: managerUrl.processParagraph, 
    headers: {
      isEncrypt: true 
    },
    method: 'post',
    data: data
  })
}