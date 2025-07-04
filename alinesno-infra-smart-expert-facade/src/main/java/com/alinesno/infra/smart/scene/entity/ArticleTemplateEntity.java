package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName("article_templates")
@Data
public class ArticleTemplateEntity extends InfraBaseEntity {

    // 模板图标
    @ColumnComment("模板图标")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "icon")
    private String icon;

    @ColumnComment("模板名称")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "name")
    private String name;

    @ColumnComment("作者")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "author")
    private String author;

    @ColumnComment("描述信息")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "_description")
    private String description;

    @ColumnComment("模板类型")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "article_template_type")
    private String articleTemplateType;

    // 模板脚本(groovy)
    @ColumnComment("模板脚本")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "groovy_script")
    private String groovyScript;

    // ------>>>>>>>>>>>> 指标信息 ---->>>>>>>>>.
    // 是否上架
    @ColumnComment("是否上架")
    @ColumnType(value = MySqlTypeConstant.TINYINT, length = 1)
    @TableField(value = "is_online")
    private boolean isOnline;

    // 使用数量/成功次数/异常次数
    @ColumnComment("使用数量")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @TableField(value = "use_count")
    private Long useCount;

    @ColumnComment("异常数量")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @TableField(value = "error_count")
    private Long errorCount;

    @ColumnComment("成功数量")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @TableField(value = "success_count")
    private Long successCount;

    @ColumnComment("模板信息")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "article_template_info")
    private String articleTemplateInfo;

    @ColumnComment("模板全名")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "article_template_full_name")
    private String articleTemplateFullName;

    @ColumnComment("来源ID")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @TableField(value = "from_id")
    private Long fromId; // 来源ID

    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 16)
    @ColumnComment("模板权限")
    @TableField("article_template_permission")
    private String articleTemplatePermission;

    @ColumnComment("系统提示")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "system_prompt")
    private String systemPrompt;

    // prompt
    @ColumnComment("模板提示")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "prompt")
    private String prompt;

    @ColumnComment("结果配置")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "result_config")
    private String resultConfig;

    @ColumnComment("配置信息")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "config")
    private String config;

    public static ArticleTemplateEntity toPower(InfraBaseEntity entity) {
        ArticleTemplateEntity template = new ArticleTemplateEntity();

        template.setOrgId(entity.getOrgId());
        template.setOperatorId(entity.getOperatorId());
        template.setDepartmentId(entity.getDepartmentId());

        return template;
    }

}
