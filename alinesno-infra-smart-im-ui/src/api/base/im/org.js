import request from '@/utils/request'

const prefix = '/v1/api/infra/base/im/org'

// 创建组织
export function createOrg(data) {
  return request({
    url: prefix + '/createOrg' ,
    method: 'post',
    data: data
  })
}

