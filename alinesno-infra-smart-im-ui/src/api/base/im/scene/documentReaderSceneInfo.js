import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/documentReader/sceneInfo/' ;

var managerUrl = {
  initAgents: prefix +"initAgents",
  // genReviewList: prefix +"genReviewList"
}

// 生成文档审核场景信息
// export function genReviewList(data) {
//   return request({
//     url: managerUrl.genReviewList , 
//     method: 'post' , 
//     data: data
//   })
// }

// 初始化Agent角色
export function initAgents(data) {
  return request({
    url: managerUrl.initAgents , 
    method: 'post' , 
    data: data
  })
}