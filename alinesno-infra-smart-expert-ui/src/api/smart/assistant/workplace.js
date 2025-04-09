import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/workplace/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'createWorkplace' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeWorkplace: prefix + "removeWorkplace" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile" ,
  createWorkplace: prefix + "createWorkplace" ,
  allMyWorkplace: prefix + "allMyWorkplace" ,
  allPublicWorkplace: prefix + "allPublicWorkplace" ,
  joinWorkplace: prefix + "joinWorkplace" ,
  getDefaultWorkplaceId: prefix + "getDefaultWorkplaceId" ,
  updateWorkplaceAgent: prefix + "updateWorkplaceAgent" ,
  closeWorkplaceSSE: "/v1/api/infra/base/im/sseWorkplaceTask/closeSseConnect" ,
  getFlowTaskNotice: "/v1/api/infra/base/im/chat/getFlowTaskNotice" ,
}

// 更新频道角色列表
export function updateWorkplaceAgent(channelId , rolesId) {
  const data = {
    channelId : channelId ,
    rolesId : rolesId
  }
  return request({
    url: managerUrl.updateWorkplaceAgent , 
    method: 'post',
    data: data
  })
}

// 获取任务实例完成通知
export function getFlowTaskNotice(){
  return request({
    url: managerUrl.getFlowTaskNotice , 
    method: 'get'
  })
}

// 关闭频道SSE
export function closeWorkplaceSSE(channel , type){
  return request({
    url: managerUrl.closeWorkplaceSSE + '?channel=' + channel + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultWorkplaceId(){
  return request({
    url: managerUrl.getDefaultWorkplaceId , 
    method: 'get'
  })
}

// 加入频道
export function joinWorkplace(channelId){
  return request({
    url: managerUrl.joinWorkplace + '?channelId=' + channelId , 
    method: 'get'
  })
}

// 新增频道
export function createWorkplace(data) {
  return request({
    url: managerUrl.createWorkplace ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listWorkplace(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicWorkplace() {
  return request({
    url: managerUrl.allPublicWorkplace, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyWorkplace() {
  return request({
    url: managerUrl.allMyWorkplace , 
    method: 'get'
  })
}

// 查询频道详细
export function getWorkplace(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addWorkplace(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateWorkplace(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeWorkplace(id) {
  return request({
    url: managerUrl.removeWorkplace+ '?channelId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delWorkplace(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetWorkplacePwd(id, password) {
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
export function changeWorkplaceStatus(id, status) {
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