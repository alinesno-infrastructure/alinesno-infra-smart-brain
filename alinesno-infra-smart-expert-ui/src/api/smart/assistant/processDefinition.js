import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/data/scheduler/processDefinition/' ;
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
    runOneTime: prefix + "runOneTime", // 立即执行一次job
    pauseTrigger: prefix + "pauseTrigger", // 暂停trigger
    resumeTrigger: prefix + "resumeTrigger", // 恢复trigger
    deleteProcessDefinition: prefix + "deleteProcessDefinition", // 删除job
    updateProcessDefinition: prefix + "updateProcessDefinition", // 更新job
    changeField: prefix + "changeField",
    listAllProcessDefinition: prefix + "listAllProcessDefinition",
    commitProcessDefinition: prefix + "commitProcessDefinition", // 保存任务
    catalogTreeSelect: prefix + "catalogTreeSelect",
    validateTask: prefix + "validateTask",
    getProcessDefinitionByDto: prefix + "getProcessDefinitionByDto",
    downloadfile: prefix + "downloadfile"
}

// 查询任务详细
export function getProcessDefinitionByDto(id){
  return request({
    url: managerUrl.getProcessDefinitionByDto + '?id=' + parseStrEmpty(id) ,
    method: 'get'
  })
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
export function commitProcessDefinition(data){
  return request({
    url: managerUrl.commitProcessDefinition ,
    method: 'post',
    data: data
  })
}

// 查询部门下拉树结构
export function catalogTreeSelect() {
  return request({
    url: managerUrl.catalogTreeSelect ,
    method: 'get'
  })
}

// 立即执行一次job
export function runOneTime(jobId){
  return request({
    url: managerUrl.runOneTime + "?jobId=" + parseStrEmpty(jobId),
    method: 'post',
  })
}

// 暂停trigger
export function pauseTrigger(jobId){
  return request({
    url: managerUrl.pauseTrigger + "?jobId=" + parseStrEmpty(jobId),
    method: 'post',
  })
}

// 恢复trigger
export function resumeTrigger(jobId){
  return request({
    url: managerUrl.resumeTrigger + "?jobId=" + parseStrEmpty(jobId),
    method: 'post',
  })
}

// 删除job
export function deleteProcessDefinition(jobId){
  return request({
    url: managerUrl.deleteProcessDefinition + "?jobId=" + parseStrEmpty(jobId),
    method: 'post',
  })
}

// 更新job
// export function updateProcessDefinition(data){
//   return request({
//     url: managerUrl.updateProcessDefinition ,
//     method: 'post',
//     data: data
//   })
// }

// 列出所有集成渠道
export function listAllProcessDefinition(){
  return request({
    url: managerUrl.listAllProcessDefinition ,
    method: 'get'
  })
}

// 修改字段
export function changStatusField(data){
  return request({
    url: managerUrl.changeField ,
    method: 'post',
    data: data
  })
}

// 查询数据库列表
export function listProcessDefinition(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询数据库详细
export function getProcessDefinition(databaseId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(databaseId),
    method: 'get'
  })
}

// 新增数据库
export function addProcessDefinition(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改数据库
// export function updateProcessDefinition(data) {
//   return request({
//     url: managerUrl.updateUrl ,
//     method: 'put',
//     data: data
//   })
// }

// 删除数据库
export function delProcessDefinition(databaseId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(databaseId),
    method: 'delete'
  })
}
