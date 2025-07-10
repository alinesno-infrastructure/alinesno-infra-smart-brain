import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/examinee/';
var groupPrefix = '/api/infra/smart/assistant/scene/examineeGroup/';

var managerUrl = {
  // 分组列表/添加/删除/修改
  listExamineeGroup: groupPrefix + 'listExamineeGroup',
  addExamineeGroup: groupPrefix + 'addExamineeGroup',
  deleteExamineeGroup: groupPrefix + 'deleteExamineeGroup',
  updateExamineeGroup: groupPrefix + 'updateExamineeGroup',

  // 考生管理(添加/导入/删除/修改/分组配置)
  listExaminee: prefix + 'datatables',
  addExaminee: prefix + 'addExaminee',
  importExaminee: prefix + 'importExaminee',
  deleteExaminee: prefix + 'deleteExaminee',
  updateExaminee: prefix + 'updateExaminee',
  configExamineeGroup: prefix + 'configExamineeGroup',
  deleteExamineeBatch: prefix + 'deleteExamineeBatch',
}

// 批量删除考生
export function deleteExamineeBatch(ids) {
  return request({
    url: managerUrl.deleteExamineeBatch,
    method: 'delete',
    params: { ids: ids }
  })
}

// 考生分组管理

/**
 * 查询考生分组列表
 * @returns {Promise} 请求Promise
 */
export function listExamineeGroup(query) {
  return request({
    url: managerUrl.listExamineeGroup,
    method: 'post' , 
    params: query
  })
}

/**
 * 添加考生分组
 * @param {Object} data 分组数据
 * @returns {Promise} 请求Promise
 */
export function addExamineeGroup(data) {
  return request({
    url: managerUrl.addExamineeGroup,
    method: 'post',
    data: data
  })
}

/**
 * 删除考生分组
 * @param {String|Number} id 分组ID
 * @returns {Promise} 请求Promise
 */
export function deleteExamineeGroup(id) {
  return request({
    url: managerUrl.deleteExamineeGroup + '?id=' + parseStrEmpty(id),
    method: 'delete'
  })
}

/**
 * 修改考生分组
 * @param {Object} data 分组数据
 * @returns {Promise} 请求Promise
 */
export function updateExamineeGroup(data) {
  return request({
    url: managerUrl.updateExamineeGroup,
    method: 'put',
    data: data
  })
}

// 考生管理

/**
 * 查询考生列表
 * @param {Object} query 查询参数
 * @returns {Promise} 请求Promise
 */
export function listExaminee(query) {
  return request({
    url: managerUrl.listExaminee,
    method: 'post',
    params: query
  })
}

/**
 * 添加考生
 * @param {Object} data 考生数据
 * @returns {Promise} 请求Promise
 */
export function addExaminee(data) {
  return request({
    url: managerUrl.addExaminee,
    method: 'post',
    data: data
  })
}

/**
 * 导入考生
 * @param {Object} data 导入数据
 * @returns {Promise} 请求Promise
 */
export function importExaminee(data) {
  return request({
    url: managerUrl.importExaminee,
    method: 'post',
    data: data
  })
}

/**
 * 删除考生
 * @param {String|Number} id 考生ID
 * @returns {Promise} 请求Promise
 */
export function deleteExaminee(id) {
  return request({
    url: managerUrl.deleteExaminee + '?id=' + parseStrEmpty(id),
    method: 'delete'
  })
}

/**
 * 修改考生信息
 * @param {Object} data 考生数据
 * @returns {Promise} 请求Promise
 */
export function updateExaminee(data) {
  return request({
    url: managerUrl.updateExaminee,
    method: 'put',
    data: data
  })
}

/**
 * 配置考生分组
 * @param {Object} data 分组配置数据
 * @returns {Promise} 请求Promise
 */
export function configExamineeGroup(data) {
  return request({
    url: managerUrl.configExamineeGroup,
    method: 'post',
    data: data
  })
}