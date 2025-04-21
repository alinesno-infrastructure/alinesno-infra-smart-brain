import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReview/sceneInfo/' ;

var managerUrl = {
  initAgents: prefix +"initAgents",
  genReviewList: prefix +"genReviewList" , 
  getAuditList: prefix +"getAuditList" ,
  genReviewListByDataset: prefix +"genReviewListByDataset" ,

  genDocxReport: prefix +"genDocxReport",
  getPreviewUrl: prefix +"getPreviewUrl",
  getPreviewReportDocx: prefix +"getPreviewReportDocx",
}

// 获取预览文档
export function getPreviewReportDocx(storageId) {
  return request({
    url: managerUrl.getPreviewReportDocx + '?storageId=' + parseStrEmpty(storageId),
    // responseType: 'arraybuffer', // 显式声明返回二进制流
    responseType: 'blob', // 显式声明返回二进制流
    method: 'get'
  })
}

// 获取预览文档地址
export function getPreviewUrl(storageId) {
  return request({
    url: managerUrl.getPreviewUrl + '?storageId=' + parseStrEmpty(storageId),
    method: 'get'
  })
}

// 生成文档审核报告
export function genDocxReport(sceneId) {
  return request({
    url: managerUrl.genDocxReport + '?sceneId=' + parseStrEmpty(sceneId),
    method: 'get',
  })
}

// 生成文档审核场景信息
export function genReviewListByDataset(data) {
  return request({
    url: managerUrl.genReviewListByDataset , 
    method: 'post' , 
    data: data
  })
}

// 获取审核清单
export function getAuditList() {
  return request({
    url: managerUrl.getAuditList , 
    method: 'get' 
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

// 初始化Agent角色
export function initAgents(data) {
  return request({
    url: managerUrl.initAgents , 
    method: 'post' , 
    data: data
  })
}