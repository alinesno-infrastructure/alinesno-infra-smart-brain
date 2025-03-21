import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/publishChat/' ;
var managerUrl = {
  getShareInfo : prefix +"getShareInfo" ,
  chatShareRole: prefix + 'chatShareRole' ,
  playShareGenContent: prefix + 'playShareGenContent' ,
  shareRecognize: prefix + 'shareRecognize' ,
}

// 获取语音模型语音请求
export function playShareGenContent(item , shareId) {
    return request({
        url: managerUrl.playShareGenContent + '?shareId=' + parseStrEmpty(shareId) , 
        method: 'post',
        data: item ,
        responseType: 'blob', // 显式声明返回二进制流
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'image/*' // 明确期望图片类型
        }
    })
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