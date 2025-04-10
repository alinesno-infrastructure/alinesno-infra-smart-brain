// 工作台

import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/v1/api/infra/base/im/workplace/';

var managerUrl = {
  isHasWorkplace: prefix + "isHasWorkplace",
  listOrgWorkplace: prefix + "listOrgWorkplace",
  getCurrentWorkplace: prefix + "getCurrentWorkplace",
  getWorkplaceItem: prefix + "getWorkplaceItem",
  useWorkplace: prefix + "useWorkplace"
}

export function getWorkplaceItem(workplaceId , type) {
  return request({
    url: managerUrl.getWorkplaceItem + '?workplaceId=' + parseStrEmpty(workplaceId) + '&type=' + type , 
    method: 'get'
  })
}

export function getCurrentWorkplace(workplaceId) {
  return request({
    url: managerUrl.getCurrentWorkplace + '?workplaceId=' + parseStrEmpty(workplaceId),
    method: 'get'
  });
}

// 查询是否有工作台
export function isHasWorkplace() {
  return request({
    url: managerUrl.isHasWorkplace,
    method: 'get'
  });
}

// 获取组织工作台
export function listOrgWorkplace() {
  return request({
    url: managerUrl.listOrgWorkplace,
    method: 'get'
  });
}

// 使用用户工作台
export function useWorkplace(data) {
  return request({
    url: managerUrl.useWorkplace,
    method: 'post',
    data: data 
  });
}