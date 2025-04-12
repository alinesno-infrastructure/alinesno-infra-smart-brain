import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/chat/' ;
var managerUrl = {
  chatMessage : prefix +"chatMessage" ,
  chatAssistantContent: prefix +"chatAssistantContent" ,
  updateAssistantContent: prefix +"updateAssistantContent" ,
  runChainAgent: prefix +"runChainAgent" ,
  sendUserMessage: prefix +"sendUserMessage" ,
  getTaskNotice : prefix +"getTaskNotice " ,
  getChannelAgent: prefix +"getChannelAgent" ,
  getAllCatalog: prefix +"getAllCatalog" ,
  chanelSayHello: prefix +"chanelSayHello" ,
}

// 用户进入组织Hello消息
export function chanelSayHello(data , channelId){
  return request({
    url: managerUrl.chanelSayHello + "?channelId=" + parseStrEmpty(channelId) ,
    data: data ,
    method: 'post'
  })
}

// 获取任务实例完成通知
export function getAllCatalog(){
  return request({
    url: managerUrl.getAllCatalog , 
    method: 'get'
  })
}


// 获取任务实例完成通知
export function getChannelAgent(channelId){
  return request({
    url: managerUrl.getChannelAgent + "?channelId=" + parseStrEmpty(channelId), 
    method: 'get'
  })
}

// 获取任务实例完成通知
export function getTaskNotice(){
  return request({
    url: managerUrl.getTaskNotice , 
    method: 'get'
  })
}

// 客户端发送消息 
export function sendUserMessage(message, users , businessId, channelId , type , channelStreamId) {
  return request({
    url: managerUrl.sendUserMessage , 
    data: {
      message: message, 
      users: users,
      businessIds: businessId,
      channelId: channelId,
      type: type , 
      channelStreamId: channelStreamId
    },
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

// 添加Agent到当前的频道中 
export function addChainAgent(roleId , channelId) {
  return request({
    url: managerUrl.addChainAgent+ '?channelId=' + parseStrEmpty(channelId) + '&roleId=' + parseStrEmpty(roleId),
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
export function chatMessage(channelId) {
  return request({
    url: managerUrl.chatMessage + '?channelId=' + parseStrEmpty(channelId),
    method: 'get'
  })
}
