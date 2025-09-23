import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 接口配置项
var prefix = '/v1/api/infra/base/im/point/' ;
var managerUrl = { 
  createOrder: prefix + "createOrder",
  queryOrderWithQrcode: prefix +"queryOrderWithQrcode",
  activePackages: prefix +"activePackages",
  getIntegral: prefix +"getIntegral",
  queryOrder: prefix +"queryOrder",
  pointsDetail: prefix +"pointsDetail",
}

// 获取组织积分
export function getIntegral() {
  return request({
    url: managerUrl.getIntegral,
    method: 'get',
    params: {
      userId: parseStrEmpty()
    }
  })
}

// 查询订单
export function queryOrder(orderId) {
  return request({
    url: managerUrl.queryOrder,
    method: 'get',
    params: {
      orderId: parseStrEmpty(orderId),
    }
  })
}

// 查询订单二维码
export function queryOrderWithQrcode(orderId , payWay) {
  return request({
    url: managerUrl.queryOrderWithQrcode + '?orderId=' + parseStrEmpty(orderId) + '&payWay=' + parseStrEmpty(payWay),
    method: 'get'
  })
}

// 用户下单
export function createOrder(data) {
  return request({
    url: managerUrl.createOrder,
    method: 'post',
    headers: {
      isEncrypt: true 
    },
    data: data
  })
}
 
// 获取所有积分套餐
export function activePackages() {
  return request({
    url: managerUrl.activePackages,
    method: 'get'
  })
}

// 获取积分明细（分页）
export function getPointsDetail(data) {
  return request({
    url: managerUrl.pointsDetail,
    method: 'post', 
    data: data 
  })
}