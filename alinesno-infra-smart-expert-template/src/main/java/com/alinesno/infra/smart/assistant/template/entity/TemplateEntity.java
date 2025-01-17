package com.alinesno.infra.smart.assistant.template.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName("templates")
@Data
public class TemplateEntity extends InfraBaseEntity {

    // 模板名称
    @Column(name = "template_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "模板名称")
    private String templateName;

    // 模板描述
    @Column(name = "template_desc", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "模板描述")
    private String templateDesc;

    // 模板标识
    @Column(name = "template_key", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "模板标识")
    private String templateKey;

    // 调用次数
    @Column(name = "call_count", type = MySqlTypeConstant.INT, comment = "调用次数")
    private Integer callCount;

    // json参数
    @Column(name = "template_params", type = MySqlTypeConstant.TEXT, comment = "json参数")
    private String templateParams;

    // 文件存储ID
    @Column(name = "storage_file_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "文件存储ID")
    private String storageFileId;

    // 模板文件类型
    @Column(name = "template_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "模板类型")
    private String templateType;
}
