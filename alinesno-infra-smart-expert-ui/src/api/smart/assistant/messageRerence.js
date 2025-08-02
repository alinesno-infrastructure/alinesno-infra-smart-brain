import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/im/messageReference' ;
var managerUrl = {
   getByMessageId: prefix + '/getByMessageId'
}

// 查询引用
export function getByMessageId(messageId){
  return request({
    url: managerUrl.getByMessageId + '?messageId=' + messageId , 
    method: 'get'
  })
}
 