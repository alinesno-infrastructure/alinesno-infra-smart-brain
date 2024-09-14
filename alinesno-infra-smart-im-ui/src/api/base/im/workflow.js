import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/workflow/' ;
var managerUrl = {
  detailUrl: prefix +"detail",
  updateContent: prefix + "updateContent"
}

// 查询应用详细
export function getMessage(businessId) {
  return request({
    url: managerUrl.detailUrl + '?businessId=' + parseStrEmpty(businessId),
    method: 'get'
  })
}

// 更新内容
export function updateContent(data) {
  return request({
    url: managerUrl.updateContent,
    method: 'post',
    data: data
  })
}
