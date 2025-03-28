import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/longText/' ;

var managerUrl = {
  // 大文本
  saveChapter: prefix +"saveChapters",
  processParagraph: prefix +"processParagraph",
  chatRole: prefix +"chatRole",
  getScene: prefix +"getScene",
  updateChapterEditor: prefix + "updateChapterEditor" , 
  chatRoleSync: prefix +"chatRoleSync",
  updateChapterContentEditor: prefix +"updateChapterContentEditor",
  getChapterByRole: prefix +"getChapterByRole",
  getChapterContent: prefix +"getChapterContent",
  updateChapterContent: prefix +"updateChapterContent",
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
export function uploadOss(screenId) {
  return request({
    url: managerUrl.uploadOss + '?screenId=' + parseStrEmpty(screenId),
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
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}

// 查询章节内容
export function getChapterContent(id) {
  return request({
    url: chapterPrefix + 'getChapterContent?chapterId=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 查询章节详细
export function getChapterByRole(roleId , screenId) {
  return request({
    url: managerUrl.getChapterByRole + '?roleId=' + parseStrEmpty(roleId) + "&screenId="+ parseStrEmpty(screenId),
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
export function saveChapter(data , screenId) {
  return request({
    url: managerUrl.saveChapter + "?screenId=" + parseStrEmpty(screenId) ,
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
      isEncrypt: true 
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
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}
