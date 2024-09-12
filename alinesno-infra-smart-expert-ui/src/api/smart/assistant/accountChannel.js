import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/im/accountChannel/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'save' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeAccountChannel: prefix + "removeAccountChannel" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile" ,
  createAccountChannel: prefix + "createAccountChannel" ,
  allMyAccountChannel: prefix + "allMyAccountChannel" ,
  allPublicAccountChannel: prefix + "allPublicAccountChannel" ,
  joinAccountChannel: prefix + "joinAccountChannel" ,
  getDefaultAccountChannelId: prefix + "getDefaultAccountChannelId" ,
  closeAccountChannelSSE: "/v1/api/infra/base/im/sseAccountChannelTask/closeSseConnect" ,
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
export function closeAccountChannelSSE(channel , type){
  return request({
    url: managerUrl.closeAccountChannelSSE + '?channel=' + channel + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultAccountChannelId(){
  return request({
    url: managerUrl.getDefaultAccountChannelId , 
    method: 'get'
  })
}

// 加入频道
export function joinAccountChannel(channelId){
  return request({
    url: managerUrl.joinAccountChannel + '?channelId=' + channelId , 
    method: 'get'
  })
}

// 新增频道
export function createAccountChannel(data) {
  return request({
    url: managerUrl.createAccountChannel ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listAccountChannel(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicAccountChannel() {
  return request({
    url: managerUrl.allPublicAccountChannel, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyAccountChannel() {
  return request({
    url: managerUrl.allMyAccountChannel , 
    method: 'get'
  })
}

// 查询频道详细
export function getAccountChannel(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addAccountChannel(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateAccountChannel(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeAccountChannel(id) {
  return request({
    url: managerUrl.removeAccountChannel+ '?channelId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delAccountChannel(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetAccountChannelPwd(id, password) {
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
export function changeAccountChannelStatus(id, status) {
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