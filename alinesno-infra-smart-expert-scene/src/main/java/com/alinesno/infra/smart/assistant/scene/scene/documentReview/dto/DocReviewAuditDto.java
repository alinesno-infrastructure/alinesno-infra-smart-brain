package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文档审核规则组数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocReviewAuditDto extends BaseDto {

    /**
     * 审核清单名称
     */
    @NotNull(message = "审核清单名称不能为空")
    private String auditName ;

    /**
     * 审核清单列表
     */
    @NotNull(message = "审核清单列表不能为空")
    private List<@NotNull String> auditLists ;

    /**
     * 数据范围
     */
    @NotNull(message = "数据范围不能为空")
    private String dataScope ;

    /**
     * 规则组所有列表
     */
    private List<DocReviewRulesDto> rules;

}
