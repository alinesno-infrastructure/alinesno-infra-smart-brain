import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/longTextTask/' ;

var managerUrl = {
  pagerListByPage: prefix +"pagerListByPage",
  getLongTextTask: prefix +"getLongTextTask",
  addTask: prefix + "addTask",
  updateTaskGenStatus: prefix + "updateTaskGenStatus",
  deleteById: prefix + "deleteById",
  submitTask: prefix + "submitTask",
  submitChapterTask: prefix + "submitChapterTask",
  updateTaskTitle: prefix + "updateTaskTitle", 
  getMarkdownContent: prefix + "getMarkdownContent",
  chatEditorRole: prefix + "chatEditorRole" ,
  generateImages: prefix + "generateImages" ,
  takeOver: prefix + "takeOver",
  stopTask: prefix + "stopTask",
}  

// 人工接管任务
export function takeOver(taskId) {
  return request({
    url: managerUrl.takeOver + "?taskId=" + taskId,
    method: 'get'
  })
}

// 停止生成任务
export function stopTask(taskId) {
    return request({
    url: managerUrl.stopTask + "?taskId=" + taskId,
    method: 'get'
  })
}

// 生成图片
export function generateImages(data) {
  return request({
    url: managerUrl.generateImages,
    method: 'post',
    data: data
  })
}

// 文章编辑角色
export function chatEditorRole(data) {
  return request({
    url: managerUrl.chatEditorRole,
    method: 'post',
    data: data
  })
}

// 获取任务的所有markdown内容
export function getMarkdownContent(sceneId , taskId) {
  return request({
    url: managerUrl.getMarkdownContent + '?sceneId=' + parseStrEmpty(sceneId) + '&taskId=' + parseStrEmpty(taskId),
    method: 'get',
  })
}

// 更新标题任务
export function updateTaskTitle(newTitle , taskId) {
  return request({
    url: managerUrl.updateTaskTitle + '?taskTitle=' + parseStrEmpty(newTitle) + '&taskId=' + parseStrEmpty(taskId),
    method: 'get',
  })
}

// 提交生成章节任务
export function submitChapterTask(taskId , channelStreamId) {
  return request({
    url: managerUrl.submitChapterTask + "?taskId=" + taskId + "&channelStreamId=" + channelStreamId,
    method: 'get'
  })
}

// 提交任务
export function submitTask(taskId , channelStreamId) {
  return request({
    url: managerUrl.submitTask + '?taskId=' + parseStrEmpty(taskId) + '&channelStreamId=' + parseStrEmpty(channelStreamId),
    method: 'get'
  })
}

// 删除任务
export function deleteById(taskId) {
  return request({
    url: managerUrl.deleteById + '?id=' + parseStrEmpty(taskId),
    method: 'get',
  })
}

// 获取到审核任务
export function getLongTextTask(taskId) {
  return request({
    url: managerUrl.getLongTextTask+ '?taskId=' + parseStrEmpty(taskId),
    method: 'get',
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

// 分页查询试卷
export function pagerListByPage(data , sceneId) {
  return request({
    url: managerUrl.pagerListByPage + '?sceneId=' + parseStrEmpty(sceneId),
    method: 'post',
    data: data
  })
}

// 更新场景状态
export function updateTaskGenStatus(taskId, genStatus) {
  return request({
    url: managerUrl.updateTaskGenStatus + '?taskId=' + parseStrEmpty(taskId) + "&genStatus=" + genStatus , 
    method: 'get'
  })
}