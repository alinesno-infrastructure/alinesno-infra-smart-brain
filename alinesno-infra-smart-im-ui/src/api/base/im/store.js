import request from '@/utils/request'

const prefix = '/v1/api/infra/base/im/store'

const managerUrl = {
    getAgentStoreType: prefix + '/getAgentStoreType',
    getAgentStoreRole: prefix + '/getAgentStoreRole',
    storeRoleList: prefix + '/storeRoleList'
}

// 查询商店角色列表 
export function storeRoleList(query) {
    return request({
      url: managerUrl.storeRoleList ,
      method: 'post',
      params: query
    })
  }

// 获取所有商店类型
export function getAgentStoreType() {
    return request({
        url: managerUrl.getAgentStoreType,
        headers: {
            isEncrypt: true
        },
        method: 'get'
    })
}

// 获取所有商店里面的角色
export function getAgentStoreRole(query) {
    return request({
        url: managerUrl.getAgentStoreRole,
        headers: {
            isEncrypt: false
        },
        method: 'get',
        params: query
    })
}
    