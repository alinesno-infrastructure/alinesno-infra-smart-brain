import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/im/message/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'save' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeMessage: prefix + "removeMessage" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile" ,
  createMessage: prefix + "createMessage" ,
  allMyMessage: prefix + "allMyMessage" ,
  allPublicMessage: prefix + "allPublicMessage" ,
  joinMessage: prefix + "joinMessage" ,
  getDefaultMessageId: prefix + "getDefaultMessageId" ,
  closeMessageSSE: "/v1/api/infra/base/im/sseMessageTask/closeSseConnect" ,
  getFlowTaskNotice: "/v1/api/infra/base/im/chat/getFlowTaskNotice" ,
}

// 获取任务实例完成通知
export function getFlowTaskNotice(){
  return request({
    url: managerUrl.getFlowTaskNotice , 
    method: 'get'
  })
}

// 关闭频道SSE
export function closeMessageSSE(Message , type){
  return request({
    url: managerUrl.closeMessageSSE + '?Message=' + Message + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultMessageId(){
  return request({
    url: managerUrl.getDefaultMessageId , 
    method: 'get'
  })
}

// 加入频道
export function joinMessage(MessageId){
  return request({
    url: managerUrl.joinMessage + '?MessageId=' + MessageId , 
    method: 'get'
  })
}

// 新增频道
export function createMessage(data) {
  return request({
    url: managerUrl.createMessage ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listMessage(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicMessage() {
  return request({
    url: managerUrl.allPublicMessage, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyMessage() {
  return request({
    url: managerUrl.allMyMessage , 
    method: 'get'
  })
}

// 查询频道详细
export function getMessage(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addMessage(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateMessage(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeMessage(id) {
  return request({
    url: managerUrl.removeMessage+ '?MessageId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delMessage(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetMessagePwd(id, password) {
  const data = {
    id,
    password
  }
  return request({
    url: '/api/infra/base/starter/application/resetPwd',
    method: 'put',
    data: data
  })
}

// 频道状态修改
export function changeMessageStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/api/infra/base/starter/application/changeStatus',
    method: 'put',
    data: data
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/api/infra/base/starter/application/deptTree',
    method: 'get'
  })
}