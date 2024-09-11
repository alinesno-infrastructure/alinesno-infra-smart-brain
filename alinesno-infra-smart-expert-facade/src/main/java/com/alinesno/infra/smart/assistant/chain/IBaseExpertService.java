package com.alinesno.infra.smart.assistant.chain;

import java.util.Map;

// 创建 Expert 接口
public interface IBaseExpertService {

    /**
     * 专家运行
     *
     * @param params
     * @param chainName
     * @param chainId
     */
    public void processExpert(Map<String, Object> params, String chainName , Long chainId) ;

}