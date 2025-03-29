package com.alinesno.infra.smart.assistant.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.entity.AgentStoreEntity;
import org.apache.ibatis.annotations.Select;

public interface AgentStoreMapper extends IBaseMapper<AgentStoreEntity> {

    /**
     * 获取商店角色并且是用户推荐状态的角色，结果按时间倒序排列
     * @return
     */
    @Select("SELECT ir.* FROM agent_store as as1 " +
            "JOIN industry_role as ir ON as1.agent_id = ir.id " +
            "WHERE ir.recommended_role = 1 " +
            "ORDER BY ir.add_time DESC " +
            "LIMIT 1")
    IndustryRoleEntity getRecommendRole();

}
