import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/base/im/messageReference' ;
var managerUrl = {
   getByMessageId: prefix + '/getByMessageId' , 
   exportWord: prefix + '/exportWord'
}

// 查询引用
export function getByMessageId(messageId){
  return request({
    url: managerUrl.getByMessageId + '?messageId=' + messageId , 
    method: 'get'
  })
}

// 导出消息成word
export function exportWord(content , messageId, documentName) {
  return request({
    url: managerUrl.exportWord ,
    method: 'post' ,
    data: {
      content: content ,
      messageId: messageId,
      documentName: documentName
    },
    // 关键：告诉 axios 后端返回二进制
    responseType: 'blob'
  })
}
