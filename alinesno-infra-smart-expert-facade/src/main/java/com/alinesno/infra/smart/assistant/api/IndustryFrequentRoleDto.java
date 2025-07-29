package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 频频角色数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IndustryFrequentRoleDto extends BaseDto {

    private String avator; // 角色头像
    private String name ; // 角色名称
    private String type ; // 场景或者角色

    private String sceneType ; // 场景类型
    private Integer clickCount;

}
