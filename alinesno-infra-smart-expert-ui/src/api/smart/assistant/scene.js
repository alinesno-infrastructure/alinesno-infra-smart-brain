import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'createScene' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  removeScene: prefix + "removeScene" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadFile: prefix + "downloadFile" ,
  createScene: prefix + "createScene" ,
  allMyScene: prefix + "allMyScene" ,
  allPublicScene: prefix + "allPublicScene" ,
  joinScene: prefix + "joinScene" ,
  getDefaultSceneId: prefix + "getDefaultSceneId" ,
  updateSceneAgent: prefix + "updateSceneAgent" ,
  supportScene: prefix + "supportScene" ,
  getSceneScope: prefix + "getSceneScope" ,
  getRoleBySceneIdAndAgentType: prefix +"getRoleBySceneIdAndAgentType",
  updateSceneAgents: prefix +"updateSceneAgents",
  listAllScene: prefix + "listAllScene" ,
  listPublicScene: prefix + "listPublicScene" ,
  updateGreetingQuestion: prefix + "updateGreetingQuestion" ,
  supportAllScene: prefix +"supportAllScene",
  updateSceneConfigData: prefix + "updateSceneConfigData",

  closeSceneSSE: "/v1/api/infra/base/im/sseSceneTask/closeSseConnect" ,
  getFlowTaskNotice: "/v1/api/infra/base/im/chat/getFlowTaskNotice" ,
}

// 新增频道
export function updateSceneConfigData(data) {
  return request({
    url: managerUrl.updateSceneConfigData ,
    method: 'post',
    data: data
  })
}

// 获取所有场景列表
export function supportAllScene() {
  return request({
    url: managerUrl.supportAllScene,
    method: 'get'
  })
}

// 更新场景开场白
export function updateGreetingQuestion(sceneId , greetingQuestion) {
  const data = {
    sceneId : sceneId ,
    greetingQuestion : greetingQuestion
  }
  return request({
    url: managerUrl.updateGreetingQuestion , 
    method: 'post',
    data: data
  })
}

// 更新场景Agent
export function updateSceneAgents(sceneId , sceneTypeId , sceneTypeCode , data) {
  const data2 = {
    sceneId : sceneId ,
    sceneTypeId : sceneTypeId ,
    sceneTypeCode : sceneTypeCode ,
    agents: data
  }

  return request({
    url: managerUrl.updateSceneAgents , 
    method: 'post',
    data: data2
  })
}

// 通过场景id和角色类型获取到角色列表
export function getRoleBySceneIdAndAgentType(sceneId , agentTypeId) {
  return request({
    url: managerUrl.getRoleBySceneIdAndAgentType + "?sceneId=" + parseStrEmpty(sceneId) + "&agentTypeId=" + parseStrEmpty(agentTypeId),
    method: 'get'
  })
}

// 获取到所有场景列表
export function listAllScene() {
  return request({
    url: managerUrl.listAllScene ,
    method: 'get'
  })
}

// 获取到所有公共场景列表
export function listPublicScene() {
  return request({
    url: managerUrl.listPublicScene ,
    method: 'get'
  })
}

// 获取场景范围
export function getSceneScope() {
  return request({
    url: managerUrl.getSceneScope ,
    method: 'get'
  })
}

// 支持的场景列表
export function supportScene(){
  return request({
    url: managerUrl.supportScene , 
    method: 'get'
  })
}

// 更新频道角色列表
export function updateSceneAgent(sceneId , rolesId) {
  const data = {
    sceneId : sceneId ,
    rolesId : rolesId
  }
  return request({
    url: managerUrl.updateSceneAgent , 
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
export function closeSceneSSE(scene , type){
  return request({
    url: managerUrl.closeSceneSSE + '?scene=' + scene + '&type=' + type, 
    method: 'get'
  })
}

// 获取默认频道 
export function getDefaultSceneId(){
  return request({
    url: managerUrl.getDefaultSceneId , 
    method: 'get'
  })
}

// 加入频道
export function joinScene(sceneId){
  return request({
    url: managerUrl.joinScene + '?sceneId=' + sceneId , 
    method: 'get'
  })
}

// 新增频道
export function createScene(data) {
  return request({
    url: managerUrl.createScene ,
    method: 'post',
    data: data
  })
}

// 查询频道列表
export function listScene(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询所有频道详细
export function allPublicScene() {
  return request({
    url: managerUrl.allPublicScene, 
    method: 'get'
  })
}

// 查询频道详细
export function allMyScene() {
  return request({
    url: managerUrl.allMyScene , 
    method: 'get'
  })
}

// 查询频道详细
export function getScene(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增频道
export function addScene(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}



// 修改频道
export function updateScene(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除频道
export function removeScene(id) {
  return request({
    url: managerUrl.removeScene+ '?sceneId=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 删除频道
export function delScene(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 频道密码重置
export function resetScenePwd(id, password) {
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
export function changeSceneStatus(id, status) {
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