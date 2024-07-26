import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/api/infra/smart/assistant/workflowNodeExecution/' ;
var managerUrl = {
  datatables : prefix +"datatables" ,
  createUrl: prefix + 'add' ,
  saveUrl: prefix + 'save' ,
  updateUrl: prefix +"modify" ,
  statusUrl: prefix +"changeStatus" ,
  cleanUrl: prefix + "clean",
  detailUrl: prefix +"detail",
  removeUrl: prefix + "delete" ,
  exportUrl: prefix + "exportExcel",
  changeField: prefix + "changeField",
  downloadfile: prefix + "downloadfile",
  getNodeList: prefix + "getNodeList",
}

// 查询流程节点列表
export function getNodeList(query) {
  return request({
    url: managerUrl.getNodeList ,
    method: 'post',
    params: query
  })
}

// 查询流程节点列表
export function listNodeExcection(query) {
  return request({
    url: managerUrl.datatables ,
    method: 'post',
    params: query
  })
}

// 查询流程节点详细
export function getNodeExcection(NodeExcectionId) {
  return request({
    url: managerUrl.detailUrl + '/' + parseStrEmpty(NodeExcectionId),
    method: 'get'
  })
}

// 新增流程节点
export function addNodeExcection(data) {
  return request({
    url: managerUrl.saveUrl ,
    method: 'post',
    data: data
  })
}

// 修改流程节点
export function updateNodeExcection(data) {
  return request({
    url: managerUrl.updateUrl ,
    method: 'put',
    data: data
  })
}

// 删除流程节点
export function delNodeExcection(NodeExcectionId) {
  return request({
    url: managerUrl.removeUrl + '/' + parseStrEmpty(NodeExcectionId),
    method: 'delete'
  })
}

// 流程节点密码重置
export function resetNodeExcectionPwd(NodeExcectionId, password) {
  const data = {
    NodeExcectionId,
    password
  }
  return request({
    url: '/api/infra/base/starter/NodeExcection/resetPwd',
    method: 'put',
    data: data
  })
}

// 流程节点状态修改
export function changeNodeExcectionStatus(NodeExcectionId, status) {
  const data = {
    NodeExcectionId,
    status
  }
  return request({
    url: '/api/infra/base/starter/NodeExcection/changeStatus',
    method: 'put',
    data: data
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/api/infra/base/starter/NodeExcection/deptTree',
    method: 'get'
  })
}