import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 连接服务器
let sseSource ;

export function openSseConnect(channelId) {
    let baseURL = import.meta.env.VITE_APP_BASE_API;
    sseSource = new EventSource(baseURL + "/v1/api/infra/base/im/sse/openConn/" + parseStrEmpty(channelId));
    sseSource.onopen = function () {
        console.log("连接打开");
    }

    // 连接错误
    sseSource.onerror = function (err) {
        console.log("连接错误:", err);
        // handleCloseSse(channelId);
    }

    return sseSource;
}

// 关闭链接
export function handleCloseSse(channelId) {
    if(sseSource){
        sseSource.close()
    }
    return request({
        url: "/v1/api/infra/base/im/sse/closeConn/" + parseStrEmpty(channelId) , 
        method: 'get'
    })
}

// 关闭所有链接
export function handleCloseAllSse() {
    if(sseSource){
        sseSource.close()
    }
    return request({
        url: "/v1/api/infra/base/im/sse/closeAllConn" , 
        method: 'get'
    })
}