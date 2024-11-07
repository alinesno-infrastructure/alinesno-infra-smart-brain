import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/screen/' ;
var chapterPrefix = '/api/infra/smart/assistant/screenChapter/' ;

var managerUrl = {
  createScreen: prefix + "saveOrUpdate" , 
  updateChapterEditor: prefix + "updateChapterEditor" , 
  getScreen: prefix +"getScreen",

  saveChapter: chapterPrefix +"saveChapters",
  chatRole: chapterPrefix +"chatRole",
  updateChapterContentEditor: chapterPrefix +"updateChapterContentEditor",
  getChapterByRole: chapterPrefix +"getChapterByRole",
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
export function updateChapterEditor(id , data , type) {
  return request({
    url: managerUrl.updateChapterEditor + "?id=" + parseStrEmpty(id) + "&type="+type+"&editors=" + data,
    method: 'post'
  })
}

// 创建场景
export function createScreen(data) {
  return request({
    url: managerUrl.createScreen,
    method: 'post',
    data: data
  })
}
