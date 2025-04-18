import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReview/audit/' ;
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
    saveOrUpdateAudit: prefix + "saveOrUpdateAudit",
    queryAudit: prefix + "queryAudit",
    downloadfile: prefix + "downloadfile"
}

// 查询审核清单
export function queryAudit(id) {
    return request({
        url: managerUrl.queryAudit + '?id=' + parseStrEmpty(id),
        method: 'get'
    })
}

// 保存或更新审核规则
export function saveOrUpdateAudit(data) {
  return request({
    url: managerUrl.saveOrUpdateAudit,
    method: 'post',
    data: data
  })
}

// 查询数据库列表
export function listReviewAudit(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询数据库详细
export function getReviewAudit(databaseId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(databaseId),
    method: 'get'
  })
}

// 新增数据库
export function addReviewAudit(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改数据库
export function updateReviewAudit(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除数据库
export function delReviewAudit(databaseId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(databaseId),
    method: 'delete'
  })
}

export function downloadFile(databaseId) {
    return request({
    })
}