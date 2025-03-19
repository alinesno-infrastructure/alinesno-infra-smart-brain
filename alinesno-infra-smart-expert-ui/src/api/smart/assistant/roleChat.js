import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/roleChat/' ;
var speechPrefix = '/api/infra/smart/assistant/speechRecognition/' ;  // 语音识别

var managerUrl = {
  getInfo: prefix +"getInfo",
  chatRole: prefix + "chatRole",

  recognizeForm: speechPrefix + "recognizeFormData" ,
  recognize: speechPrefix + "recognize" ,
}

// 语音识别
export function recognizeForm(audioFormData) {
  return request({
    url: managerUrl.recognizeForm ,
    method: 'post',
    headers: {
        'Content-Type': 'multipart/form-data'
    },
    data: audioFormData  
  })
}

// 语音识别
export function recognize(data) {
  return request({
    url: managerUrl.recognize ,
    method: 'post',
    // headers: {
    //     'Content-Type': 'multipart/form-data'
    // },
    data: data
  })
}

// 查询应用详细
export function getInfo(roleId) {
  return request({
    url: managerUrl.getInfo + '?roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 与Role聊天 
export function chatRole(data , roleId) {
  return request({
    url: managerUrl.chatRole + '?roleId=' + parseStrEmpty(roleId),
    headers: {
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}
