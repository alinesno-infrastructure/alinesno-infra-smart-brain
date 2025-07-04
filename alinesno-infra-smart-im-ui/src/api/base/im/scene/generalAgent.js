import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/generalAgent/' ;
var prefixTask = '/api/infra/smart/assistant/scene/taskManager/' ;

var managerUrl = {
  // 大文本
  saveDataPlan: prefix +"saveDataPlan",
  processParagraph: prefix +"processParagraph",
  chatRole: prefix +"chatRole",
  getScene: prefix +"getScene",
  updateChapterEditor: prefix + "updateChapterEditor" , 
  chatRoleSync: prefix +"chatRoleSync",
  updateChapterContentEditor: prefix +"updateChapterContentEditor",
  getChapterByRole: prefix +"getChapterByRole",
  getChapterContent: prefix +"getChapterContent",
  uploadOss: prefix +"uploadOss",
  updateChapterContent: prefix +"updateChapterContent",
  dispatchAgent: prefix +"dispatchAgent",
  updateChapterPromptContent: prefix +"updateChapterPromptContent",
  updateSceneGenStatus: prefix +"updateSceneGenStatus",
  genDataReport: prefix +"genDataReport",
  initAgents: prefix + "initAgents",
  getPreviewDocx: prefix +"getPreviewDocx",
  getPreviewUrl: prefix + "getPreviewUrl",
  getGeneralAgentScene: prefix + "getGeneralAgentScene",

  pagerListByPage: prefixTask +"pagerListByPage",
  addTask: prefixTask + "addTask",
  deleteById: prefixTask + "deleteById",
}

// 通过id删除任务
export function deleteById(taskId){
  return request({
    url: managerUrl.deleteById + '?id=' + parseStrEmpty(taskId) ,
    method: 'get'
  })
}

// 添加任务addTask
export function addTask(data) {
  return request({
    url: managerUrl.addTask,
    method: 'post',
    data: data
  })
}

// getGeneralAgentScene
export function getGeneralAgentScene(sceneId , taskId) {
  return request({
    url: managerUrl.getGeneralAgentScene + '?sceneId=' + parseStrEmpty(sceneId) + "&taskId=" + parseStrEmpty(taskId),
    method: 'get'
  })
}

// 分页查询试卷
export function pagerListByPage(data , sceneId) {
  return request({
    url: managerUrl.pagerListByPage + '?sceneId=' + parseStrEmpty(sceneId),
    method: 'post',
    data: data
  })
}


// 获取预览文档地址
export function getPreviewUrl(storageId) {
  return request({
    url: managerUrl.getPreviewUrl + '?storageId=' + parseStrEmpty(storageId),
    method: 'get'
  })
}

// 获取预览文档
export function getPreviewDocx(storageId) {
  return request({
    url: managerUrl.getPreviewDocx + '?storageId=' + parseStrEmpty(storageId),
    // responseType: 'arraybuffer', // 显式声明返回二进制流
    responseType: 'blob', // 显式声明返回二进制流
    method: 'get'
  })
}

// 生成数据报表
export function genDataReport(sceneId , taskId) {
  return request({
    url: managerUrl.genDataReport + '?sceneId=' + parseStrEmpty(sceneId) + "&taskId=" + parseStrEmpty(taskId) , 
    method: 'get'
  })
}

// 更新场景状态
export function updateSceneGenStatus(sceneId , genStatus) {
  return request({
    url: managerUrl.updateSceneGenStatus + '?sceneId=' + parseStrEmpty(sceneId) + "&genStatus=" + genStatus , 
    method: 'get'
  })
}

// 更新prompt内容
export function updateChapterPromptContent(data) {
  return request({
    url: managerUrl.updateChapterPromptContent , 
    method: 'post',
    data: data
  })
}

// 分配Agent角色到每个章节
export function dispatchAgent(sceneId , taskId){
  return request({
    url: managerUrl.dispatchAgent + '?sceneId=' + parseStrEmpty(sceneId) + "&taskId=" + parseStrEmpty(taskId) , 
    method: 'get'
  })
}

// 初始化Agent角色
export function initAgents(data){
  return request({
    url: managerUrl.initAgents , 
    method: 'post',
    data: data
  })
}


// 查询场景详细
export function getScene(id) {
  return request({
    url: managerUrl.getScene + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 获取到支持场景
export function supportScreen() {
  return request({
    url: managerUrl.supportScreen,
    method: 'get'
  })
}

// 运行任务
export function executeScreenTask(data,uId) {
  return request({
    url: managerUrl.executeScreenTask + "?uId=" + parseStrEmpty(uId) , 
    method: 'post',
    data: data
  })
}

// 运行场景计划
export function leaderPlan(data) {
  return request({
    url: managerUrl.leaderPlan , 
    method: 'post',
    data: data
  })
}

// 更新leader角色
export function updateLeaderRole(data) {
  return request({
    url: managerUrl.updateLeaderRole,
    method: 'post',
    data: data
  })
}

// 上传文件到OSS
export function uploadOss(sceneId) {
  return request({
    url: managerUrl.uploadOss + '?sceneId=' + parseStrEmpty(sceneId),
    method: 'get',
  })
}

// 场景列表
export function screenList(query) {
  return request({
    url: managerUrl.screenList,
    method: 'get',
    params: query
  })
}

// 更新章节内容
export function updateChapterContent(data) {
  return request({
    url: managerUrl.updateChapterContent, 
    method: 'post',
    data: data
  })
}

// 内容片断修改
export function processParagraph(data) {
  return request({
    url: managerUrl.processParagraph, 
    headers: {
      isEncrypt: false 
    },
    method: 'post',
    data: data
  })
}

// 查询章节内容
export function getChapterContent(id) {
  return request({
    url: managerUrl.getChapterContent + '?chapterId=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 查询章节详细
export function getChapterByRole(roleId , sceneId) {
  return request({
    url: managerUrl.getChapterByRole + '?roleId=' + parseStrEmpty(roleId) + "&sceneId="+ parseStrEmpty(sceneId),
    method: 'get'
  })
}

// 更新编辑人员
export function updateChapterContentEditor(data) {
  return request({
    url: managerUrl.updateChapterContentEditor,
    method: 'post',
    data: data
  })
}

export function chatRoleSync(data) {
  return request({
    url: managerUrl.chatRoleSync , 
    method: 'post',
    data: data
  })
}

// 与Role聊天
export function chatRole(data) {
  return request({
    url: managerUrl.chatRole , 
    method: 'post',
    data: data
  })
}

// 保存章节
export function saveDataPlan(data , sceneId , taskId) {
  return request({
    url: managerUrl.saveDataPlan + "?sceneId=" + parseStrEmpty(sceneId) + "&taskId=" + parseStrEmpty(taskId) ,
    method: 'post',
    data: data
  })
}


// 查询场景详细
export function getScreen(id) {
  return request({
    url: managerUrl.getScreen + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 更新章节编辑人员
export function updateChapterEditor(id , editors , type) {

  let data = {
    id: parseStrEmpty(id),
    editors: editors,
    type: type
  }

  return request({
    url: managerUrl.updateChapterEditor , 
    headers: {
      isEncrypt: false 
    },
    method: 'post',
    data: data
  })

}

// 创建场景
export function createScreen(data) {
  return request({
    url: managerUrl.createScreen,
    headers: {
      isEncrypt: false 
    },
    method: 'post',
    data: data
  })
}
