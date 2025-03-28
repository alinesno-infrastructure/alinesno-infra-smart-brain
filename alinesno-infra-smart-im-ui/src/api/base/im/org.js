import request from '@/utils/request'

const prefix = '/v1/api/infra/base/im/org'

// 创建组织
export function createOrg(data) {
  return request({
    url: prefix + '/createOrg' ,
    headers: {
      isEncrypt: true
    },
    method: 'post',
    data: data
  })
}

// 获取组织信息
export function getByOrg() {
  return request({
    url: prefix + '/getByOrg' ,
    headers: {
      isEncrypt: true
    },
    method: 'get'
  })
}

