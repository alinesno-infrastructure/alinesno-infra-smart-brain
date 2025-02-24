import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/llmModel/';
var managerUrl = {
    datatables: prefix + "datatables",
    createUrl: prefix + 'add',
    saveUrl: prefix + 'save',
    updateUrl: prefix + "modify",
    statusUrl: prefix + "changeStatus",
    cleanUrl: prefix + "clean",
    detailUrl: prefix + "detail",
    removeUrl: prefix + "delete",
    exportUrl: prefix + "exportExcel",
    changeField: prefix + "changeField",
    downloadFile: prefix + "downloadFile",
    allModelProvidersInfo: prefix + "getAllModelProvidersInfo", 
    allModelTypesInfo: prefix + "getAllModelTypesInfo" ,
    listLlmMode: prefix + "listLlmMode",
    testLlmModel: prefix + "testLlmModel"
};

// 测试模型
export function testLlmModel(data) {
    return request({
        url: managerUrl.testLlmModel,
        method: 'post',
        data: data
    })
}

// 列出所有模型列表
export function listAllLlmModel() {
    return request({
        url: managerUrl.listLlmMode ,
        method: 'get'
    })
}

// 查询模型列表
export function listLlmModel(query) {
    return request({
        url: managerUrl.datatables,
        method: 'post',
        params: query
    })
}

// 查询模型详细
export function getLlmModel(LlmModelId) {
    return request({
        url: managerUrl.detailUrl + '/' + parseStrEmpty(LlmModelId),
        method: 'get'
    })
}

// 新增模型
export function addLlmModel(data) {
    return request({
        url: managerUrl.saveUrl,
        method: 'post',
        data: data
    })
}

// 修改模型
export function updateLlmModel(data) {
    return request({
        url: managerUrl.updateUrl,
        method: 'put',
        data: data
    })
}

// 删除模型
export function delLlmModel(LlmModelId) {
    return request({
        url: managerUrl.removeUrl + '/' + parseStrEmpty(LlmModelId),
        method: 'delete'
    })
}

// 模型密码重置
export function resetLlmModelPwd(LlmModelId, password) {
    const data = {
        LlmModelId,
        password
    }
    return request({
        url: '/api/infra/base/starter/application/resetPwd',
        method: 'put',
        data: data
    })
}

// 模型状态修改
export function changeLlmModelStatus(LlmModelId, status) {
    const data = {
        LlmModelId,
        status
    }
    return request({
        url: '/api/infra/base/starter/application/changeStatus',
        method: 'put',
        data: data
    })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
    return request({
        url: '/api/infra/base/starter/application/deptTree',
        method: 'get'
    })
}

// 获取所有大模型提供商信息
export function getAllModelProvidersInfo() {
    return request({
        url: managerUrl.allModelProvidersInfo,
        method: 'get'
    });
}

// 获取所有模型类型信息
export function getAllModelTypesInfo() {
    return request({
        url: managerUrl.allModelTypesInfo,
        method: 'get'
    });
}