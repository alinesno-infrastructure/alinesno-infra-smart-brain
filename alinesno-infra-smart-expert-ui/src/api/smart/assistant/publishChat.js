import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/publishChat/' ;
var managerUrl = {
  getShareInfo : prefix +"getShareInfo" ,
  chatShareRole: prefix + 'chatShareRole' ,
  shareRecognize: prefix + 'shareRecognize' ,
}

// 语音识别
export function shareRecognize(data) {
  return request({
    url: managerUrl.shareRecognize ,
    method: 'post',
    data: data
  })
}

// 查询应用详细
export function getShareInfo(shareId) {
  return request({
    url: managerUrl.getShareInfo + '?shareId=' + parseStrEmpty(shareId),
    method: 'get'
  })
}

// 与Role聊天
export function chatShareRole(data , shareId) {
  return request({
    url: managerUrl.chatShareRole + '?shareId=' + parseStrEmpty(shareId),
    headers: {
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}