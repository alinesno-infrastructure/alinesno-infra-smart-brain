import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/articleGenerate/' ;
var templatePrefix = '/api/infra/smart/assistant/scene/articleTemplate/' ; 

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
  getArticleById: prefix + "getArticleById" ,
  updateArticle: prefix + "updateArticle" ,

  getAllArticleTemplateType: templatePrefix +"getAllArticleTemplate",
  getTemplateByType: templatePrefix +"getTemplateByType",
}

// 更新文章内容
export function updateArticle(data) {
  return request({
    url: managerUrl.updateArticle,
    method: 'post',
    data: data
  })
}

// 通过id获取文章
export function getArticleById(id) {
  return request({
    url: managerUrl.getArticleById + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 获取模型类型
 export function getAllArticleTemplateType() {
  return request({
    url: managerUrl.getAllArticleTemplateType , 
    method: 'get'
  })
}

// 获取getTemplateByType
 export function getTemplateByType(typeCode) {
  return request({
    url: managerUrl.getTemplateByType + '?typeCode=' + parseStrEmpty(typeCode),
    method: 'get'
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
export function pagerListByPage(data) {
  return request({
    url: managerUrl.pagerListByPage,
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

//// 获取到支持场景
//export function supportScreen() {
//  return request({
//    url: managerUrl.supportScreen,
//    method: 'get'
//  })
//}

//// 运行任务
//export function executeScreenTask(data,uId) {
//  return request({
//    url: managerUrl.executeScreenTask + "?uId=" + parseStrEmpty(uId) ,
//    method: 'post',
//    data: data
//  })
//}
//
//// 运行场景计划
//export function leaderPlan(data) {
//  return request({
//    url: managerUrl.leaderPlan ,
//    method: 'post',
//    data: data
//  })
//}
//
//// 更新leader角色
//export function updateLeaderRole(data) {
//  return request({
//    url: managerUrl.updateLeaderRole,
//    method: 'post',
//    data: data
//  })
//}
//
//// 上传文件到OSS
//export function uploadOss(sceneId) {
//  return request({
//    url: managerUrl.uploadOss + '?sceneId=' + parseStrEmpty(sceneId),
//    method: 'get',
//  })
//}
//
//// 场景列表
//export function screenList(query) {
//  return request({
//    url: managerUrl.screenList,
//    method: 'get',
//    params: query
//  })
//}
//
//// 更新章节内容
//export function updateChapterContent(data) {
//  return request({
//    url: managerUrl.updateChapterContent,
//    method: 'post',
//    data: data
//  })
//}
//
//// 内容片断修改
//export function processParagraph(data) {
//  return request({
//    url: managerUrl.processParagraph,
//    headers: {
//      isEncrypt: false
//    },
//    method: 'post',
//    data: data
//  })
//}
//
//// 查询章节内容
//export function getChapterContent(id) {
//  return request({
//    url: managerUrl.getChapterContent + '?chapterId=' + parseStrEmpty(id),
//    method: 'get'
//  })
//}
//
//// 查询章节详细
//export function getChapterByRole(roleId , sceneId) {
//  return request({
//    url: managerUrl.getChapterByRole + '?roleId=' + parseStrEmpty(roleId) + "&sceneId="+ parseStrEmpty(sceneId),
//    method: 'get'
//  })
//}
//
//// 更新编辑人员
//export function updateChapterContentEditor(data) {
//  return request({
//    url: managerUrl.updateChapterContentEditor,
//    method: 'post',
//    data: data
//  })
//}
//
//export function chatRoleSync(data) {
//  return request({
//    url: managerUrl.chatRoleSync ,
//    method: 'post',
//    data: data
//  })
//}
//
//// 与Role聊天
//export function chatRole(data) {
//  return request({
//    url: managerUrl.chatRole ,
//    method: 'post',
//    data: data
//  })
//}
//
//// 保存章节
//export function saveDataPlan(data , sceneId) {
//  return request({
//    url: managerUrl.saveDataPlan + "?sceneId=" + parseStrEmpty(sceneId) ,
//    method: 'post',
//    data: data
//  })
//}
//
//
//// 查询场景详细
//export function getScreen(id) {
//  return request({
//    url: managerUrl.getScreen + '?id=' + parseStrEmpty(id),
//    method: 'get'
//  })
//}
//
//// 更新章节编辑人员
//export function updateChapterEditor(id , editors , type) {
//
//  let data = {
//    id: parseStrEmpty(id),
//    editors: editors,
//    type: type
//  }
//
//  return request({
//    url: managerUrl.updateChapterEditor ,
//    headers: {
//      isEncrypt: false
//    },
//    method: 'post',
//    data: data
//  })
//
//}
//
//// 创建场景
//export function createScreen(data) {
//  return request({
//    url: managerUrl.createScreen,
//    headers: {
//      isEncrypt: false
//    },
//    method: 'post',
//    data: data
//  })
//}
