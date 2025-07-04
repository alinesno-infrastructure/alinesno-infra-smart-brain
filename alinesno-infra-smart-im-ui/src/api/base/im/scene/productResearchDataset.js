import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

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

  getAllArticleTemplateType: groupPrefix +"getAllArticleTemplate",
  getTemplateByType: groupPrefix +"getTemplateByType",
  getTemplateDetail: groupPrefix +"getTemplateDetail",

  // 规则接口
  saveOrUpdateRule: prefix + "saveOrUpdateRule" ,
  listRuleByIds: prefix + "listRuleByIds" ,
  removeRule: prefix + "delete" ,
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
export function listGroup(sceneId) {
  return request({
    url: managerUrl.listGroup + '?sceneId=' + parseStrEmpty(sceneId),
    method: 'get'
  })
}