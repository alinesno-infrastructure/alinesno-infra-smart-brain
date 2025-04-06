import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReview/sceneInfo/' ;

var managerUrl = {
  initAgents: prefix +"initAgents"
}

// 初始化Agent角色
export function initAgents(data) {
  return request({
    url: managerUrl.initAgents , 
    method: 'post' , 
    data: data
  })
}