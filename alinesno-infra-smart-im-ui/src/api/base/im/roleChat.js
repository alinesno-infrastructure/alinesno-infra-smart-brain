import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/roleChat/' ;
var speechPrefix = '/api/infra/smart/assistant/speechRecognition/' ;  // 语音识别

var managerUrl = {
  getInfo: prefix +"getInfo",
  chatRole: prefix + "chatRole",
  uploadFile: prefix + "uploadFile",
  getRoleQuestionSuggestion: prefix + "getRoleQuestionSuggestion",
  messageFeedback: prefix + "messageFeedback",
  recognizeForm: speechPrefix + "recognizeFormData" ,
  recognize: speechPrefix + "recognize" ,
  playGenContent: speechPrefix + "playGenContent" ,
}

// 提交用户反馈
export function messageFeedback(data) {
  return request({
    url: managerUrl.messageFeedback , 
    method: 'post',
    data: data 
  })
}

// 获取角色智能问答建议
export function getRoleQuestionSuggestion(data) {
  return request({
    url: managerUrl.getRoleQuestionSuggestion , 
    method: 'post',
    data: data 
  })
}

// 获取语音模型语音请求
export function playGenContent(item) {
    return request({
        url: managerUrl.playGenContent , 
        method: 'post',
        data: item ,
        responseType: 'blob', // 显式声明返回二进制流
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'image/*' // 明确期望图片类型
        }
    })
}

// 文件上传接口
export function uploadFile(formData){
  return request({
      url: managerUrl.uploadFile , 
      method: 'post',
      data: formData,
      noCheckRepeatSubmit: true,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
  })
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
    method: 'post',
    data: data
  })
}
