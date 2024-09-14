import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/roleChat/' ;
var managerUrl = {
  getInfo: prefix +"getInfo",
  chatRole: prefix + "chatRole"
}

// 查询应用详细
export function getInfo(roleId) {
  return request({
    url: managerUrl.getInfo + '?roleId=' + parseStrEmpty(roleId),
    method: 'get'
  })
}

// 与Role聊天 
export function chatRole(data , roleId) {
  return request({
    url: managerUrl.chatRole + '?roleId=' + parseStrEmpty(roleId),
    method: 'post',
    data: data
  })
}
