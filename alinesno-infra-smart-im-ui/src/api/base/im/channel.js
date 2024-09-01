import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/im/channel/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'save' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeChannel: prefix + "removeChannel" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadfile: prefix + "downloadfile" , 
  createChannel: prefix + "createChannel" ,
  allMyChannel: prefix + "allMyChannel" ,
  allPublicChannel: prefix + "allPublicChannel" ,
  joinChannel: prefix + "joinChannel" ,
  getDefaultChannelId: prefix + "getDefaultChannelId" ,
  closeChannelSSE: "/v1/api/infra/base/im/sseChannelTask/closeSseConnect" ,
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
export function closeChannelSSE(channel , type){
  return request({
    url: managerUrl.closeChannelSSE + '?channel=' + channel + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultChannelId(){
  return request({
    url: managerUrl.getDefaultChannelId , 
    method: 'get'
  })
}

// 加入频道
export function joinChannel(channelId){
  return request({
    url: managerUrl.joinChannel + '?channelId=' + channelId , 
    method: 'get'
  })
}

// 新增频道
export function createChannel(data) {
  return request({
    url: managerUrl.createChannel ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listChannel(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicChannel() {
  return request({
    url: managerUrl.allPublicChannel, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyChannel() {
  return request({
    url: managerUrl.allMyChannel , 
    method: 'get'
  })
}

// 查询频道详细
export function getChannel(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addChannel(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateChannel(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeChannel(id) {
  return request({
    url: managerUrl.removeChannel+ '?channelId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delChannel(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetChannelPwd(id, password) {
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
export function changeChannelStatus(id, status) {
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