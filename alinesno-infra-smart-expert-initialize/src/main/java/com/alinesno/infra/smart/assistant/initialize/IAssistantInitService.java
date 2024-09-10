package com.alinesno.infra.smart.assistant.initialize;

/**
 * 初始化用户信息
 */
public interface IAssistantInitService {

    /**
     * 初始化指令分类
     */
    void promptCatalog() ;

    /**
     * 初始化指令
     */
    void prompt() ;

    /**
     * 初始化角色分类
     */
    void expertCatalog() ;

    /**
     * 初始化角色
     */
    void expert() ;

    /**
     * 初始化插件
     */
    void initPlugin() ;

    /**
     * 初始化渠道
     */
    void initChannel();
}
