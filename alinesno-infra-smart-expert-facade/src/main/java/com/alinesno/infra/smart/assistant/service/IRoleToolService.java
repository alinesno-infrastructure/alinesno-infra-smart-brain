//package com.alinesno.infra.smart.assistant.service;
//
//import com.alinesno.infra.common.facade.services.IBaseService;
//import com.alinesno.infra.smart.assistant.entity.RoleToolEntity;
//import com.alinesno.infra.smart.assistant.entity.ToolEntity;
//
//import java.util.List;
//
///**
// * 应用构建Service接口
// *
// * @version 1.0.0
// * @author luoxiaodong
// */
//public interface IRoleToolService extends IBaseService<RoleToolEntity> {
//
//    /**
//     * 查询用户插件
//     */
//    List<ToolEntity> findTools(Long roleId);
//
//    /**
//     * 更新用户插件
//     */
//    boolean updateRoleTools(Long roleId, List<Long> tools);
//
//}