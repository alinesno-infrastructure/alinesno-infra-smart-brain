import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 数据库接口操作
 *
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReview/rule/' ;
var groupPrefix = '/api/infra/smart/assistant/scene/documentReview/ruleGroup/' ;

var managerUrl = {
  // 分组接口
  listGroup: groupPrefix + "listGroup" ,
  saveOrUpdateRuleGroup : groupPrefix + "saveOrUpdateRuleGroup" ,
  removeGroup: groupPrefix + "deleteGroup" ,

  // 规则接口
  saveOrUpdateRule: prefix + "saveOrUpdateRule" ,
  listRuleByIds: prefix + "listRuleByIds" ,
  removeRule: prefix + "delete" ,
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
export function listGroup() {
  return request({
    url: managerUrl.listGroup,
    method: 'get'
  })
}