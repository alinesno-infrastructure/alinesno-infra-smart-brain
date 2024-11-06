package com.alinesno.infra.smart.assistant.screen.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景实体类,针对于具体的业务场景配置管理
 */
@EqualsAndHashCode(callSuper = true)
@TableName("screen")
@Data
public class ScreenEntity extends InfraBaseEntity {

    // 场景名称、场景Banner图片、场景描述、场景使用次数、场景文档类型、场景知识库ID
    @Column(name = "screen_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "场景名称")
    private String screenName;

    @Column(name = "screen_banner", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "场景Banner图片")
    private String screenBanner;

    @Column(name = "screen_desc", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "场景描述")
    private String screenDesc;

    @Column(name = "screen_count", type = MySqlTypeConstant.INT, comment = "场景使用次数")
    private Integer screenCount;

    @Column(name = "doc_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "场景文档类型")
    private String docType;

    @Column(name = "knowledge_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "场景知识库ID")
    private String knowledgeId;

    @Column(name = "sort_order", type = MySqlTypeConstant.INT, comment = "排序")
    private Integer sortOrder;

    @Column(name = "is_recommend", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为推荐")
    private Boolean isRecommend;

    // 章节编辑人员、内容编辑人员，都以|号进行分割
    @Column(name = "chapter_editor", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "章节编辑人员")
    private String chapterEditor;

    @Column(name = "content_editor", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "内容编辑人员")
    private String contentEditor;

}
