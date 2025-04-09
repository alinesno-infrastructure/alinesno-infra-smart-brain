import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/workplaceItem/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'createWorkplaceItem' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeWorkplaceItem: prefix + "removeWorkplaceItem" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile" ,
  createWorkplaceItem: prefix + "createWorkplaceItem" ,
  allMyWorkplaceItem: prefix + "allMyWorkplaceItem" ,
  allPublicWorkplaceItem: prefix + "allPublicWorkplaceItem" ,
  joinWorkplaceItem: prefix + "joinWorkplaceItem" ,
  getDefaultWorkplaceItemId: prefix + "getDefaultWorkplaceItemId" ,
  updateWorkplaceItemAgent: prefix + "updateWorkplaceItemAgent" ,
  closeWorkplaceItemSSE: "/v1/api/infra/base/im/sseWorkplaceItemTask/closeSseConnect" ,
  getFlowTaskNotice: "/v1/api/infra/base/im/chat/getFlowTaskNotice" ,
}

// 更新频道角色列表
export function updateWorkplaceItemAgent(channelId , rolesId) {
  const data = {
    channelId : channelId ,
    rolesId : rolesId
  }
  return request({
    url: managerUrl.updateWorkplaceItemAgent , 
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
export function closeWorkplaceItemSSE(channel , type){
  return request({
    url: managerUrl.closeWorkplaceItemSSE + '?channel=' + channel + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultWorkplaceItemId(){
  return request({
    url: managerUrl.getDefaultWorkplaceItemId , 
    method: 'get'
  })
}

// 加入频道
export function joinWorkplaceItem(channelId){
  return request({
    url: managerUrl.joinWorkplaceItem + '?channelId=' + channelId , 
    method: 'get'
  })
}

// 新增频道
export function createWorkplaceItem(data) {
  return request({
    url: managerUrl.createWorkplaceItem ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listWorkplaceItem(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicWorkplaceItem() {
  return request({
    url: managerUrl.allPublicWorkplaceItem, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyWorkplaceItem() {
  return request({
    url: managerUrl.allMyWorkplaceItem , 
    method: 'get'
  })
}

// 查询频道详细
export function getWorkplaceItem(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addWorkplaceItem(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateWorkplaceItem(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeWorkplaceItem(id) {
  return request({
    url: managerUrl.removeWorkplaceItem+ '?channelId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delWorkplaceItem(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetWorkplaceItemPwd(id, password) {
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
export function changeWorkplaceItemStatus(id, status) {
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