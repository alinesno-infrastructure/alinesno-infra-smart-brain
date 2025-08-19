package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 内容格式化布局配置，针对于不同场景的内容格式化
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_document")
public class ContentFormatterDocumentEntity extends InfraBaseEntity {

    // 文档名称、文档来源（new新建立|upload上传)、文档内容、最近保存时间
    @TableField
    @Column(name = "document_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "文档名称")
    private String documentName; // 文档名称

    @TableField
    @Column(name = "document_source", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文档来源")
    private String documentSource; // 文档来源

    @TableField
    @Column(name = "document_desc", type = MySqlTypeConstant.VARCHAR, comment = "文档介绍")
    private String documentDesc;

    @TableField
    @Column(name = "document_content", type = MySqlTypeConstant.LONGTEXT , comment = "文档内容")
    private String documentContent;

    @TableField
    @Column(name = "last_saved_time", type = MySqlTypeConstant.TIMESTAMP, comment = "最近保存时间")
    private Date lastSavedTime;

    @TableField
    @Column(name = "content_formatting_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的内容排版场景ID")
    private Long contentFormattingSceneId; // 关联的内容排版场景ID

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId; // 关联的场景ID

   @TableField
   @Column(name = "storage_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "关联的云存储ID")
   private String storageId ;

}