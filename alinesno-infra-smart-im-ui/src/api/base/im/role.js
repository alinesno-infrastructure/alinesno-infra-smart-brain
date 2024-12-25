import request from '@/utils/request'


// 接口配置项
var prefix = '/api/infra/smart/assistant/role/' ;
var managerUrl = {
  marketDatatables : prefix +"marketDatatables" ,
  employRole: prefix +"employRole" ,
}

// 录用角色
export function employRole(roleId , isPush) {
  return request({
    url: managerUrl.employRole + '?roleId=' + roleId + '&isPush=' + isPush ,
    method: 'get',
  })
}

// 查询应用列表
export function listMarketRole(query) {
  return request({
    url: managerUrl.marketDatatables,
    method: 'post',
    params: query
  })
}
