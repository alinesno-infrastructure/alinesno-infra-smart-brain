import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

/**
 * 流程操作
 * 
 * @author luoxiaodong
 * @since 1.0.0
 */

// 接口配置项
var prefix = '/api/infra/smart/assistant/flow/' ;
var managerUrl = {
    processAndSave: prefix + "processAndSave",  // 流程保存和解析
    publishFlow: prefix + "publish",  // 发布工作流
    getLatestPublishedFlow: prefix + "latestPublished",  // 获取最新版本已发布流程
    getUnpublishedFlow: prefix + "unpublished",  // 获取未发布流程
    publishedFlow: prefix + "published", // 发布流程 
    getLatestFlow: prefix + "latest"  // 获取最新流程
}

// 发布流程
export function publishedFlow(flowId) {
    return request({
        url: managerUrl.publishedFlow + "?flowId=" + parseStrEmpty(flowId),
        method: 'get'
    })
}

// 流程保存和解析
export function processAndSave(data, roleId) {
    return request({
        url: managerUrl.processAndSave + "?roleId=" + parseStrEmpty(roleId),
        method: 'post',
        data: data
    })
}

// 发布工作流
export function publishFlow(roleId) {
    return request({
        url: managerUrl.publishFlow + "?roleId=" + parseStrEmpty(roleId),
        method: 'post'
    })
}

// 获取最新流程
export function getLatestFlow(roleId) {
    return request({
        url: managerUrl.getLatestFlow+ "?roleId=" + parseStrEmpty(roleId),
        method: 'get'
    })
}

// 获取最新版本的已发布流程
export function getLatestPublishedFlow(roleId) {
    return request({
        url: managerUrl.getLatestPublishedFlow + "?roleId=" + parseStrEmpty(roleId),
        method: 'get'
    })
}

// 获取指定角色的未发布流程
export function getUnpublishedFlow(roleId) {
    return request({
        url: managerUrl.getUnpublishedFlow + "?roleId=" + parseStrEmpty(roleId),
        method: 'get'
    })
}