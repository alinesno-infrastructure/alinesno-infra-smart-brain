import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/contentFormatter/' ;

var managerUrl = {
  getScene: prefix +"getScene",
  updateSceneGenStatus: prefix +"updateSceneGenStatus",
  updateChapterPromptContent: prefix +"updateChapterPromptContent",
  getTemplates: prefix + "getTemplates" , 
  reviewChatRoleSync: prefix +"reviewChatRoleSync",
  reviewChatRoleSingleSync: prefix +"reviewChatRoleSingleSync",
  chatRoleSync: prefix +"chatRoleSync",
  getPreviewDocxPreviewUrl: prefix +"getPreviewDocxPreviewUrl",
  getPreviewDocx: prefix +"getPreviewDocxByPdf",
  getReviewRules: prefix + 'getReviewRules' , 
  reviewCheckExpert: prefix +"reviewCheckExpert"
}

export function reviewCheckExpert(data) {
  return request({
    url: managerUrl.reviewCheckExpert , 
    method: 'post',
    data: data
  })
}

// 获取检查列表
export function getReviewRules() {
  return request({
    url: managerUrl.getReviewRules, 
    method: 'get'
  })
}

// 单条内容检查 
export function reviewChatRoleSingleSync(data) {
  return request({
    url: managerUrl.reviewChatRoleSingleSync, 
    method: 'post',
    data: data
  })
}


// 内容检查
export function reviewChatRoleSync(data) {
  return request({
    url: managerUrl.reviewChatRoleSync , 
    method: 'post',
    data: data
  })
}

// 获取预览下载地址 
export function getPreviewDocxPreviewUrl(storageId) {
  return request({
    url: managerUrl.getPreviewDocxPreviewUrl + '?storageId=' + parseStrEmpty(storageId),
    method: 'get'
  })
}

// 角色聊天
export function chatRoleSync(data) {
  return request({
    url: managerUrl.chatRoleSync , 
    method: 'post',
    data: data
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

// 获取到模板列表
export function getTemplates() {
  return request({
    url: managerUrl.getTemplates , 
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

// 查询场景详细
export function getScene(id) {
  return request({
    url: managerUrl.getScene + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}
