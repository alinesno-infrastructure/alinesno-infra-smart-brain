import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 * 
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/base/search/datasetKnowledge/' ;
var managerUrl = {
    datatables : prefix +"datatables" ,
    createUrl: prefix + 'add' ,
    knowledgeDetail: prefix + 'knowledgeDetail' ,
    saveUrl: prefix + 'save' ,
    updateUrl: prefix +"modify" ,
    statusUrl: prefix +"changeStatus" ,
    cleanUrl: prefix + "clean",
    detailUrl: prefix +"detail",
    removeUrl: prefix + "delete" ,
    exportUrl: prefix + "exportExcel",
    changeField: prefix + "changeField",
    downloadfile: prefix + "downloadfile",
    queryTmpFileByDatasetId: prefix + "queryTmpFileByDatasetId",
    uploadTmpFileByDatasetId: prefix + "uploadTmpFileByDatasetId",
    crawler: prefix + "crawler",
    queryDocumentPage: prefix + "queryDocumentPage",
}

export function queryDocumentPage(params) {
  return request({
    url: managerUrl.queryDocumentPage,
    method: 'get',
    params: params  
  })
}

export function knowledgeDetail(id) {
  return request({
    url: managerUrl.knowledgeDetail + '?id=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 爬取数据
export function crawler(data) {
  return request({
    url: managerUrl.crawler ,
    method: 'post',
    data: data
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

// 上传解析文件
export function uploadTmpFileByDatasetId(data) {
  return request({
    url: managerUrl.uploadTmpFileByDatasetId ,
    method: 'post',
    data: data
  })
}

// 获取数据集未导入文件
export function queryTmpFileByDatasetId(id) {
  return request({
    url: managerUrl.queryTmpFileByDatasetId + '?datasetId=' + parseStrEmpty(id),
    method: 'get'
  })
}

// 查询数据库列表
export function listDatasetKnowledge(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询数据库详细
export function getDatasetKnowledge(databaseId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(databaseId),
    method: 'get'
  })
}

// 新增数据库
export function addDatasetKnowledge(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改数据库
export function updateDatasetKnowledge(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除数据库
export function delDatasetKnowledge(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}
