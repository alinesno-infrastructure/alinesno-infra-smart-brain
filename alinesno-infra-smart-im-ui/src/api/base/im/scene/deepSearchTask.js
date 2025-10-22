import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/deepSearchTask/' ;

var managerUrl = {
  pagerListByPage: prefix +"pagerListByPage",
  deleteById: prefix +"deleteById",
  submitDeepSearchTask: prefix +"submitDeepSearchTask",
  getTaskRecords: prefix +"getTaskRecords", // 新增
}

// 获取任务完整记录
export function getTaskRecords(taskId) {
  return request({
    url: managerUrl.getTaskRecords + '?taskId=' + parseStrEmpty(taskId),
    method: 'get',
  })
}

// 提交任务
export function submitDeepSearchTask(data) {
  return request({
    url: managerUrl.submitDeepSearchTask ,
    method: 'post',
    data: data
  })
}

// 分页查询任务列表
export function pagerListByPage(data , sceneId) {
  return request({
    url: managerUrl.pagerListByPage + '?sceneId=' + parseStrEmpty(sceneId) ,
    method: 'post',
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
