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
  useWorkplace: prefix + "useWorkplace",
  customWorkplace: prefix + "customWorkplace",
  setHomePage: prefix + "setHomePage",
  getHomePage: prefix + "getHomePage"
}

// 获取用户默认主页
export function getHomePage() {
  return request({
    url: managerUrl.getHomePage,
    method: 'get'
  })
}


// 设置主页
export function setHomePage(data) {
  return request({
    url: managerUrl.setHomePage,
    method: 'post',
    data: data 
  })
}

// 自定义工作台
export function customWorkplace(data) {
  return request({
    url: managerUrl.customWorkplace,
    method: 'post',
    data: data 
  });
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