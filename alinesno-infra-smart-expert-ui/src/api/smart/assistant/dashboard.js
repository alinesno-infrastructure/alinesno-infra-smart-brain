import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/dashboard/' ;
var managerUrl = {
  getNewestRole : prefix +"getNewestRole" ,
}

// 查询应用详细
export function getNewestRole() {
  return request({
    url: managerUrl.getNewestRole , 
    method: 'get'
  })
}