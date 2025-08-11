import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/docReviewTaskManager/' ;

var managerUrl = {
  pagerListByPage: prefix +"pagerListByPage",
  getReviewTask: prefix +"getReviewTask",
  addTask: prefix + "addTask", 
  genReviewList: prefix +"genReviewList" , 
  deleteById: prefix + "deleteById", 
  saveDocumentContent: prefix + "saveDocumentContent" ,  // 保存文档内容 
  updateDocumentName: prefix + "updateDocumentName" // 更新任务名称
}

// 更新任务名称
export function updateDocumentName(data) {
  return request({
    url: managerUrl.updateDocumentName , 
    method: 'post' , 
    data: data
  })
}

// 保存文档内容
export function saveDocumentContent(data) {
  return request({
    url: managerUrl.saveDocumentContent , 
    method: 'post' , 
    data: data
  })
}

// 删除任务
export function deleteById(taskId) {
  return request({
    url: managerUrl.deleteById + '?id=' + parseStrEmpty(taskId),
    method: 'get',
  })
}


// 生成文档审核场景信息
export function genReviewList(data) {
  return request({
    url: managerUrl.genReviewList , 
    method: 'post' , 
    data: data
  })
}

// 获取到审核任务
export function getReviewTask(taskId) {
  return request({
    url: managerUrl.getReviewTask + '?taskId=' + parseStrEmpty(taskId),
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
