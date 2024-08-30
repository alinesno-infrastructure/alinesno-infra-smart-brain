import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 * 
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/brain/generateTask/' ;
var managerUrl = {
    datatables : prefix +"datatables" ,
    createUrl: prefix + 'add' ,
    saveUrl: prefix + 'save' ,
    updateUrl: prefix +"modify" ,
    statusUrl: prefix +"changeStatus" ,
    cleanUrl: prefix + "clean",
    detailUrl: prefix +"detail",
    removeUrl: prefix + "delete" ,
    exportUrl: prefix + "exportExcel",
    changeField: prefix + "changeField",
    downloadFile: prefix + "downloadFile",
    resetRetry: prefix + "resetRetry",
    getPromptContent: prefix + "getPromptContent"
}

// 获取PromptContent内容  
export function getPromptContent(taskId) {
  return request({
    url: managerUrl.getPromptContent+ '?taskId=' + parseStrEmpty(taskId),
    method: 'get'
  })
}

// 查询数据库列表
export function listTask(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询数据库详细
export function getTask(taskId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(taskId),
    method: 'get'
  })
}

// 新增数据库
export function addTask(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改数据库
export function updateTask(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除数据库
export function delTask(taskId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(taskId),
    method: 'delete'
  })
}

// 重置重试次数 
export function resetRetry(taskId) {
  return request({
    url: managerUrl.resetRetry + '?taskId=' + parseStrEmpty(taskId),
    method: 'get'
  })
}
