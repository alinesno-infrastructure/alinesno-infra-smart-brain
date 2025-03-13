import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/channelPublish/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'saveChannelPublish' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  channels: prefix + "channels",
  updateChannelConfig: prefix + "updateChannelConfig",
}

// 获取到所有渠道
export function getChannels(roleId){
  return request({
    url: managerUrl.channels + "?roleId=" + parseStrEmpty(roleId) ,
    method: 'get'
  })
}

// 修改字段
export function changStatusField(data){
  return request({
    url: managerUrl.changeField ,
    method: 'post',
    data: data
  })
}

// 查询应用列表
export function listChannelPublish(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询应用详细
export function getChannelPublish(id) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增应用
export function addChannelPublish(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 更新配置
export function updateChannelConfig(data) {
  return request({
    url: managerUrl.updateChannelConfig,
    method: 'post',
    data: data
  })
}

// 修改应用
export function updateChannelPublish(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除应用
export function delChannelPublish(id) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(id),
    method: 'delete'
  })
}
