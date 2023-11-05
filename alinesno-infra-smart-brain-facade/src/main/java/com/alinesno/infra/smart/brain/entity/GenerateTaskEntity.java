package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * 生成的任务和内容
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("generate_task")
public class GenerateTaskEntity extends InfraBaseEntity {

    @TableField
    private String businessId ; // 业务ID

    @TableField
    private String promptId ; // GPT角色ID

    @TableField
    private String systemContent ;  // GPT角色设定

    @TableField
    private String userContent ;  // GPT用户信息

    @TableField
    private String assistantContent ;  // 生成内容

    @TableField
    private int taskStatus ;  // 0排队中/1运行中/2已完成/9失败

    public void copyFrom(GenerateTaskEntity other) {
        try {
            BeanUtils.copyProperties(other, this);
        } catch (Exception e) {
            throw new RuntimeException("Error copying properties", e);
        }
    }
}
