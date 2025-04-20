import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReader/sceneInfo/' ;

var managerUrl = {
  initAgents: prefix +"initAgents",
  getContractType: prefix +"getContractType",
}

// 获取合同类型
export function getContractType() {
  return request({
    url: managerUrl.getContractType , 
    method: 'get' , 
  })
}

// 初始化Agent角色
export function initAgents(data) {
  return request({
    url: managerUrl.initAgents , 
    method: 'post' , 
    data: data
  })
}