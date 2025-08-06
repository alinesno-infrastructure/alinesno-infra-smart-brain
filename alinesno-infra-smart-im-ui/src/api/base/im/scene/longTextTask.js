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
  getMarkdownContent: prefix + "getMarkdownContent"
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