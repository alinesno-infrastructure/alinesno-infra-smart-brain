import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/smart/assistant/dingtalk/robot/' ;
var managerUrl = {
  chatMessage : prefix +"chatMessage" ,
  chatAssistantContent: prefix +"chatAssistantContent" ,
  updateAssistantContent: prefix +"updateAssistantContent" ,
  runChainAgent: prefix +"runChainAgent" ,
  sendUserMessage: prefix +"sendUserMessage" ,
}

// 客户端发送消息 
export function sendUserMessage(data) {
  return request({
    url: managerUrl.sendUserMessage , 
    data: data,
    method: 'post'
  })
}

// 更新获取到的内容 
export function updateAssistantContent(businessId , content) {
  const data = {
    businessId: businessId , 
    genContent: content
  }

  return request({
    url: managerUrl.updateAssistantContent+ '?businessId=' + parseStrEmpty(businessId),
    data: data,
    method: 'post'
  })
}

// 执行下一个节点任务 
export function runChainAgent(roleId , businessId) {
  return request({
    url: managerUrl.runChainAgent+ '?businessId=' + parseStrEmpty(businessId) + '&roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 获取到内容 
export function chatAssistantContent(businessId) {
  return request({
    url: managerUrl.chatAssistantContent+ '?businessId=' + parseStrEmpty(businessId),
    method: 'get'
  })
}

// 运行角色流程
export function chatMessage(businessId) {
  return request({
    url: managerUrl.chatMessage + '?businessId=' + parseStrEmpty(businessId),
    method: 'get'
  })
}
