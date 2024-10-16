import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/data/scheduler/taskDefinition/' ;
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
    downloadFile: prefix + "downloadFile"
}

// 获取到任务列表
export function getTaskDefinition(processDefinitionId) {
  return request({
    url: managerUrl.getTaskDefinition+ '?processDefinitionId=' + parseStrEmpty(processDefinitionId) ,
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
