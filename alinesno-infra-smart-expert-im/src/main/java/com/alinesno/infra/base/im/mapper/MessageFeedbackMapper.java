package com.alinesno.infra.base.im.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.im.dto.RoleFeedbackStat;
import com.alinesno.infra.smart.im.entity.MessageFeedbackEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MessageFeedbackMapper extends IBaseMapper<MessageFeedbackEntity> {

    /**
     * 统计每个角色的反馈数量（like/dislike）
     * @param roleIds 角色ID列表
     * @return 统计结果（角色ID → 喜欢/不喜欢数量）
     */
    @Select("<script>" +
            "SELECT " +
            "    m.role_id as roleId, " +
            "    SUM(CASE WHEN mf.feel = 1 THEN 1 ELSE 0 END) as likeCount, " +
            "    SUM(CASE WHEN mf.feel = 0 THEN 1 ELSE 0 END) as dislikeCount " +
            "FROM " +
            "    message m " +
            "LEFT JOIN " +
            "    message_feedback mf ON m.id = mf.message_id " +
            "WHERE " +
            "    m.role_id IN " +
            "    <foreach item='item' collection='roleIds' open='(' separator=',' close=')'> " +
            "        #{item} " +
            "    </foreach> " +
            "GROUP BY " +
            "    m.role_id " +
            "</script>")
    List<RoleFeedbackStat> countFeedbackByRoleIds(@Param("roleIds") List<Long> roleIds);
}