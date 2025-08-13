import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/productResearchScene/' ;

// 任务接口前缀
var prefixTask = '/api/infra/smart/assistant/scene/projectTask/' ;

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
  chatPromptContent: prefix +"chatPromptContent",
  updateSceneGenStatus: prefix +"updateSceneGenStatus",
  genDataReport: prefix +"genDataReport",
  initAgents: prefix + "initAgents",
  getPreviewDocx: prefix +"getPreviewDocx",
  getPreviewUrl: prefix + "getPreviewUrl",
  createScene: prefix + "createScene" , 
  listAllDeepsearchScene: prefix + "listAllDeepsearchScene" , 
  getOutputPreviewUrl: prefix +"getOutputPreviewUrl",
  getOutputMarkdownContent: prefix +"getOutputMarkdownContent",
  getQuestionTypes: prefix +"getQuestionTypes",
  getQuestionCategoryList: prefix +"getQuestionCategoryList",
  savePagerQuestion: prefix +"savePagerQuestion",
  pagerListByPage: prefix +"pagerListByPage",
  getPagerDetail: prefix +"getPagerDetail",
  updatePagerQuestion: prefix +"updatePagerQuestion",
  savePPTOutline: prefix +"savePPTOutline",
  importGitProject: prefix + "importGitProject",

  addTask: prefixTask + "addTask",
  getTaskById: prefixTask + "detail",
  executeTask: prefixTask +"executeTask",
  saveChapter: prefixTask +"saveChapters",
  chatEditorRole: prefixTask + "chatEditorRole",
  updateTaskName: prefixTask + "updateTaskName" , 
  deleteTaskById: prefixTask + "deleteTaskById"
}

// 文章编辑角色
export function chatEditorRole(data) {
  return request({
    url: managerUrl.chatEditorRole,
    method: 'post',
    data: data
  })
}

// 获取角色
export function updateTaskName(data) {
  return request({
    url: managerUrl.updateTaskName,
    method: 'post' , 
    data: data
  })
}

// 删除任务
export function deleteTaskById(taskId) {
  return request({
    url: managerUrl.deleteTaskById + "?taskId=" + parseStrEmpty(taskId) , 
    method: 'delete' 
  })
}

// 执行任务
export function executeTask(data) {
  return request({
    url: managerUrl.executeTask, 
    method: 'post' , 
    data: data
  })
}

// 保存章节
export function saveChapter(data , sceneId) {
  return request({
    url: managerUrl.saveChapter + "?sceneId=" + parseStrEmpty(sceneId) ,
    method: 'post',
    data: data
  })
}

// 通过id获取到任务详情
export function getTaskById(id) {
  return request({
    url: managerUrl.getTaskById + '/' + parseStrEmpty(id),
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

// 导入项目
export function importGitProject(data) {
  return request({
    url: managerUrl.importGitProject,
    method: 'post',
    data: data
  })
}

// savePPTOutline
export function savePPTOutline(sceneId , outline , pptId , pptConfig , promptText) {

  const data = {
    sceneId: sceneId ,
    outline: outline,
    pptId: pptId,
    pptConfig: pptConfig ,
    promptText: promptText
  }

  return request({
    url: managerUrl.savePPTOutline + "?sceneId=" + parseStrEmpty(sceneId) ,
    method: 'post',
    data: data
  })
}


// 更新试卷问题
export function updatePagerQuestion(data) {
  return request({
    url: managerUrl.updatePagerQuestion,
    method: 'post',
    data: data
  })
}

// 获取试卷详情
export function getPagerDetail(id) {
  return request({
    url: managerUrl.getPagerDetail + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
} 

// 分页查询试卷
export function pagerListByPage(sceneId , data) {
  return request({
    url: managerUrl.pagerListByPage + '?sceneId=' + parseStrEmpty(sceneId) ,
    method: 'post',
    data: data
  })
}

// 保存试卷问题类型 
export function savePagerQuestion(data) {
  return request({
    url: managerUrl.savePagerQuestion,
    method: 'post',
    data: data
  })
}

// 获取问题类型
export function getQuestionCategoryList() {
  return request({
    url: managerUrl.getQuestionCategoryList,
    method: 'get'
  })
}

// 获取问题类型
export function getQuestionTypes() {
  return request({
    url: managerUrl.getQuestionTypes,
    method: 'get'
  })
}

// 获取输出预览
export function getOutputPreviewUrl(storageId) {
  return request({
    url: managerUrl.getOutputPreviewUrl + "?storageId=" + storageId,
    method: 'get'
  })
}

// 获取到markdown内容 
export function getOutputMarkdownContent(storageId) {
  return request({
    url: managerUrl.getOutputMarkdownContent + "?storageId=" + storageId,
    method: 'get'
  })
}

// 列出所有的场景下的所有DeepSearch
export function listAllDeepsearchScene(sceneId) {
  return request({
    url: managerUrl.listAllDeepsearchScene + '?sceneId=' + parseStrEmpty(sceneId) ,
    method: 'get'
  })
}

// 创建场景
export function createScene(data) {
  return request({
    url: managerUrl.createScene,
    // headers: {
    //   isEncrypt: true
    // },
    method: 'post',
    data: data
  })
}


//// 获取预览文档地址
//export function getPreviewUrl(storageId) {
//  return request({
//    url: managerUrl.getPreviewUrl + '?storageId=' + parseStrEmpty(storageId),
//    method: 'get'
//  })
//}
//
//// 获取预览文档
//export function getPreviewDocx(storageId) {
//  return request({
//    url: managerUrl.getPreviewDocx + '?storageId=' + parseStrEmpty(storageId),
//    // responseType: 'arraybuffer', // 显式声明返回二进制流
//    responseType: 'blob', // 显式声明返回二进制流
//    method: 'get'
//  })
//}

// 生成数据报表
export function genDataReport(sceneId) {
  return request({
    url: managerUrl.genDataReport + '?sceneId=' + parseStrEmpty(sceneId) ,
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
export function chatPromptContent(data) {
 return request({
   url: managerUrl.chatPromptContent ,
   method: 'post',
   data: data
 })
}

//// 分配Agent角色到每个章节
//export function dispatchAgent(sceneId){
//  return request({
//    url: managerUrl.dispatchAgent + '?sceneId=' + parseStrEmpty(sceneId) ,
//    method: 'get'
//  })
//}

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