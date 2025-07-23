package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * Office工具配置DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterOfficeConfigDto extends BaseDto  {

    @NotBlank(message = "工具名称不能为空")
    private String toolName;

    @NotBlank(message = "工具路径不能为空")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "请输入有效的URL地址")
    private String toolPath;

    private String requestToken;

    @NotBlank(message = "工具范围不能为空")
    private String dataScope;

    /**
     * 转换为Entity对象
     */
    public ContentFormatterOfficeConfigEntity toEntity() {
        ContentFormatterOfficeConfigEntity entity = new ContentFormatterOfficeConfigEntity();

        entity.setToolName(this.toolName);
        entity.setToolPath(this.toolPath);
        entity.setRequestToken(this.requestToken);
        entity.setDataScope(this.dataScope);

        // 权限配置
        entity.setOrgId(this.getOrgId());
        entity.setDepartmentId(this.getDepartmentId());
        entity.setOperatorId(this.getOperatorId());

        return entity;
    }

    /**
     * 从Entity对象创建DTO
     */
    public static ContentFormatterOfficeConfigDto fromEntity(ContentFormatterOfficeConfigEntity entity) {
        ContentFormatterOfficeConfigDto dto = new ContentFormatterOfficeConfigDto();
        // 使用BeanUtils
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}