package com.alinesno.infra.smart.point.service;

/**
 * 用户积分实现接口
 */
public interface IAccountPointService {

    /**
     * 是否开启积分
     * @return
     */
    boolean isOpenPoint();

    /**
     * 开始会话
     */
    void startSession(long userId , long orgId , long roleId);

    /**
     * 结束会话
     */
    void endSession(long userId , long orgId , long roleId);

    /**
     * 获取指定组织的活跃会话数
     */
    public long getOrgSessionCount(long orgId) ;

    /**
     * 开始场景任务
     */
    void startSceneTask(long userId , long orgId  , long taskId);

    /**
     * 结束场景任务
     */
    void endSceneTask(long userId , long orgId  , long taskId);

    /**
     * 获取指定组织的场景任务数
     */
    public long getOrgSceneTaskCount(long orgId) ;

}
