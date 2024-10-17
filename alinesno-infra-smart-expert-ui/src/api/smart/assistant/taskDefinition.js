import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/assistant/taskDefinition/' ;
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
    getTaskDefinition: prefix + "getTaskDefinition",
    validateTask: prefix + "validateTask",
    updateProcessDefinition: prefix + "updateProcessDefinition", // 更新job
    commitProcessDefinition: prefix + "commitProcessDefinition", // 保存任务
    downloadFile: prefix + "downloadFile"
}

// 校验任务
export function validateTask(data){
  return request({
    url: managerUrl.validateTask ,
    method: 'post',
    data: data
  })
}

// 更新任务
export function updateProcessDefinition(data){
  return request({
    url: managerUrl.updateProcessDefinition,
    method: 'post',
    data: data
  })
}

// 保存任务
export function commitProcessDefinition(data , type){
  return request({
    url: managerUrl.commitProcessDefinition + '?type=' + parseStrEmpty(type) ,
    method: 'post',
    data: data
  })
}

// 获取到任务列表
export function getTaskDefinition(roleId) {
  return request({
    url: managerUrl.getTaskDefinition+ '?roleId=' + parseStrEmpty(roleId) ,
    method: 'get'
  })
}

// 查询操作日志列表
export function list(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 删除操作日志
export function delTaskDefinition(operId) {
  return request({
    url: managerUrl.cleanUrl + '/' + operId ,
    method: 'delete'
  })
}

// 清空操作日志
export function cleanTaskDefinition() {
  return request({
    url: managerUrl.cleanUrl ,
    method: 'delete'
  })
}
