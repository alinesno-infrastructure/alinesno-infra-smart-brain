import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/screen/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'createScreen' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeScreen: prefix + "removeScreen" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile" ,
  createScreen: prefix + "createScreen" ,
  allMyScreen: prefix + "allMyScreen" ,
  allPublicScreen: prefix + "allPublicScreen" ,
  joinScreen: prefix + "joinScreen" ,
  getDefaultScreenId: prefix + "getDefaultScreenId" ,
  updateScreenAgent: prefix + "updateScreenAgent" ,
  closeScreenSSE: "/v1/api/infra/base/im/sseScreenTask/closeSseConnect" ,
  getFlowTaskNotice: "/v1/api/infra/base/im/chat/getFlowTaskNotice" ,
}

// 更新频道角色列表
export function updateScreenAgent(screenId , rolesId) {
  const data = {
    screenId : screenId ,
    rolesId : rolesId
  }
  return request({
    url: managerUrl.updateScreenAgent , 
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
export function closeScreenSSE(screen , type){
  return request({
    url: managerUrl.closeScreenSSE + '?screen=' + screen + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultScreenId(){
  return request({
    url: managerUrl.getDefaultScreenId , 
    method: 'get'
  })
}

// 加入频道
export function joinScreen(screenId){
  return request({
    url: managerUrl.joinScreen + '?screenId=' + screenId , 
    method: 'get'
  })
}

// 新增频道
export function createScreen(data) {
  return request({
    url: managerUrl.createScreen ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listScreen(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicScreen() {
  return request({
    url: managerUrl.allPublicScreen, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyScreen() {
  return request({
    url: managerUrl.allMyScreen , 
    method: 'get'
  })
}

// 查询频道详细
export function getScreen(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addScreen(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateScreen(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeScreen(id) {
  return request({
    url: managerUrl.removeScreen+ '?screenId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delScreen(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetScreenPwd(id, password) {
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
export function changeScreenStatus(id, status) {
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