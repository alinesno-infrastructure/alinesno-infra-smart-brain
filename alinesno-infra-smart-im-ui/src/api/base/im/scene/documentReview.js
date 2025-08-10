import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReview/' ;

var managerUrl = {
  getScene: prefix +"getScene",
  getSceneResultList: prefix +"getSceneResultList",
  getAuditResultByRuleId: prefix +"getAuditResultByRuleId",
  getPreviewDocx: prefix +"getPreviewDocx",
  genAuditResult: prefix +"genAuditResult",
  downloadMarkDocx: prefix +"downloadMarkDocx",
}

// 下载文档
export function downloadMarkDocx(sceneId , taskId) {
  return request({
    url: managerUrl.downloadMarkDocx + '?sceneId=' + parseStrEmpty(sceneId) + '&taskId=' + parseStrEmpty(taskId),
    // responseType: 'blob', // 显式声明返回二进制流
    method: 'get'
  })
}

// 通过规则ID获取审核结果
export function getAuditResultByRuleId(ruleId , sceneId, taskId) {
  return request({
    url: managerUrl.getAuditResultByRuleId + '?ruleId=' + parseStrEmpty(ruleId) + '&sceneId=' + parseStrEmpty(sceneId) + '&taskId=' + parseStrEmpty(taskId),
    method: 'get'
  })
}

// 生成文档审核场景信息
export function genAuditResult(data) {
  return request({
    url: managerUrl.genAuditResult , 
    method: 'post',
    data: data
  })
}

// 获取预览文档
export function getPreviewDocx(taskId) {
  return request({
    url: managerUrl.getPreviewDocx + '?taskId=' + parseStrEmpty(taskId), 
    method: 'get'
  })
}

// 查询场景详细
export function getSceneResultList(id , taskId) {
  return request({
    url: managerUrl.getSceneResultList+ '?id=' + parseStrEmpty(id) + (taskId ? '&taskId=' + parseStrEmpty(taskId) : ''),
    method: 'get'
  })
}

// 查询场景详细
export function getScene(id , taskId) {
  return request({
    url: managerUrl.getScene + '?id=' + parseStrEmpty(id) + (taskId ? '&taskId=' + parseStrEmpty(taskId) : ''),
    method: 'get'
  })
}