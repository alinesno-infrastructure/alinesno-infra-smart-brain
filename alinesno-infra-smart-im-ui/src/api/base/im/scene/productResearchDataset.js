import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";
import { get } from '@vueuse/core';

/**
 * 数据库接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/projectKnowledge/' ;
var groupPrefix = '/api/infra/smart/assistant/scene/projectKnowledgeGroup/' ;

var managerUrl = {
  // 分组接口
  listGroup: groupPrefix + "listGroup" ,
  saveOrUpdateRuleGroup : groupPrefix + "saveOrUpdateRuleGroup" ,
  removeGroup: groupPrefix + "deleteGroup" ,
  listLlmMode: groupPrefix + "listLlmMode" ,
  getGroupById: groupPrefix + "getGroupById" ,

  getAllArticleTemplateType: groupPrefix +"getAllArticleTemplate",
  getTemplateByType: groupPrefix +"getTemplateByType",
  getTemplateDetail: groupPrefix +"getTemplateDetail",

  // 文件接口
  saveOrUpdateRule: prefix + "saveOrUpdateRule" ,
  listRuleByIds: prefix + "listRuleByIds" ,
  listKnowledge: prefix + 'datatables',
  removeRule: prefix + "delete" ,
  renameKnowledge: prefix + "renameKnowledge" , 
  deleteKnowledge: prefix + "deleteKnowledge"
}

/**
 * 重命名文件
 * @param {Object} data 重命名参数
 * @returns {Promise} 重命名Promise
 */ 
export function renameKnowledge(data) {
    return request({
        url: managerUrl.renameKnowledge , 
        method: 'post',
        params: data // 或者用 data: data
    });
}

/**
 * 删除文件
 * @param {Object} data 删除参数
 * @returns {Promise} 删除Promise
 */
export function deleteKnowledge(data) {
    return request({
        url: managerUrl.deleteKnowledge, 
        method: 'delete',
        params: data
    });
}

/**
 * 查询考生列表
 * @param {Object} query 查询参数
 * @returns {Promise} 请求Promise
 */
export function listKnowledge(query) {
  return request({
    url: managerUrl.listKnowledge,
    method: 'post',
    params: query
  })
}

// 通过分组id获取规则详细信息
export function getGroupById(groupId) {
  return request({
    url: managerUrl.getGroupById + '?groupId=' + parseStrEmpty(groupId) ,
    method: 'get'
  })
}

// 列出所有模型列表
export function listAllLlmModel(modelType) {
    return request({
        url: managerUrl.listLlmMode+'?modelType=' + parseStrEmpty(modelType) ,
        method: 'get'
    })
}

// 获取模板详情
export function getTemplateDetail(templateId) {
  return request({
    url: managerUrl.getTemplateDetail + '?templateId=' + parseStrEmpty(templateId),
    method: 'get'
  })
}

// 获取模型类型
 export function getAllArticleTemplateType(sceneId) {
  return request({
    url: managerUrl.getAllArticleTemplateType + '?sceneId=' + parseStrEmpty(sceneId) , 
    method: 'get'
  })
}

// 获取getTemplateByType
 export function getTemplateByType(groupId , sceneId) {
  return request({
    url: managerUrl.getTemplateByType + '?groupId=' + parseStrEmpty(groupId) + '&sceneId=' + parseStrEmpty(sceneId) ,
    method: 'get'
  })
}

// 通过ids查询出规则列表
export function listRuleByIds(ids) {
  return request({
    url: managerUrl.listRuleByIds + "?idsStr=" + ids ,
    method: 'get'
  })
}

// 删除规则
export function removeRule(id) {
  return request({
    url: managerUrl.removeRule + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 添加或者更新规则信息
export function saveOrUpdateRule(data) {
  return request({
    url: managerUrl.saveOrUpdateRule ,
    method: 'post',
    data: data
  })
}

// 通过id删除分组
export function removeGroup(id) {
  return request({
    url: managerUrl.removeGroup + '?id=' + parseStrEmpty(id),
    method: 'delete'
  })
}

// 添加或者更新分组
export function saveOrUpdateRuleGroup(data) {
  return request({
    url: managerUrl.saveOrUpdateRuleGroup ,
    method: 'post',
    data: data
  })
}

// 查询所有分组列表
export function listGroup(params) {
  return request({
    url: managerUrl.listGroup , 
    method: 'get' , 
    params: params
  })
}