import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/taskManager/' ;

var managerUrl = {
  pagerListByPage: prefix +"pagerListByPage",
  getGeneralAgentTask: prefix +"getGeneralAgentTask",
  addTask: prefix + "addTask",
  updateTaskGenStatus: prefix + "updateTaskGenStatus",
  deleteById: prefix + "deleteById",
  submitTask: prefix + "submitTask",
  submitChapterTask: prefix + "submitChapterTask",
  updateTaskStatus: prefix + "updateTaskStatus",

  chatEditorRole: prefix + "chatEditorRole" ,
}

// 文章编辑角色
export function chatEditorRole(data) {
  return request({
    url: managerUrl.chatEditorRole,
    method: 'post',
    data: data
  })
}

// 更新任务状态
export function updateTaskStatus(taskId){
  return request({
    url: managerUrl.updateTaskStatus + "?taskId=" + taskId , 
    method: 'get'
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
export function getGeneralAgentTask(taskId) {
  return request({
    url: managerUrl.getGeneralAgentTask+ '?taskId=' + parseStrEmpty(taskId),
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