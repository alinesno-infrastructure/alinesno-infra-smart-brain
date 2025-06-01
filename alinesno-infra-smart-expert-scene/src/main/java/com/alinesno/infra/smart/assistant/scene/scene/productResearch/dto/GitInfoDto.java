package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Git信息传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GitInfoDto extends BaseDto {

     /**
     * 场景ID
     */
    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    /**
     * Git仓库地址，不能为空
     */
    @NotBlank(message = "Git地址不能为空")
    private String gitUrl;

    /**
     * 项目名称
     */
     @NotBlank(message = "项目名称不能为空")
    private String projectName ;

     /**
     * 渠道ID
     */
     @NotBlank(message = "渠道ID不能为空")
    private String channelStreamId ;

    /**
     * 日志渠道ID
     */
     @NotBlank(message = "日志渠道ID不能为空")
     private String logChannelStreamId ;

    /**
     * 是否需要认证
     */
    @NotNull(message = "是否认证不能为空")
    private Boolean isAuth;

    /**
     * Git用户名，当需要认证时不能为空
     */
    @NotBlank(message = "Git用户名不能为空", groups = AuthRequired.class)
    private String gitUsername;

    /**
     * Git密码，当需要认证时不能为空
     */
    @NotBlank(message = "Git密码不能为空", groups = AuthRequired.class)
    private String gitPassword;

    /**
     * 定义需要认证时的校验组
     */
    public interface AuthRequired {

    }
}