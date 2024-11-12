package com.alinesno.infra.smart.assistant.screen.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField
    @Column(name = "screen_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "场景名称")
    private String screenName;

    @TableField
    @Column(name = "screen_banner", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "场景Banner图片")
    private String screenBanner;

    @TableField
    @Column(name = "screen_desc", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "场景描述")
    private String screenDesc;

    @TableField
    @Column(name = "screen_count", type = MySqlTypeConstant.INT, comment = "场景使用次数")
    private Integer screenCount;

    @TableField
    @Column(name = "doc_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "场景文档类型")
    private String docType;

    @TableField
    @Column(name = "knowledge_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "场景知识库ID")
    private String knowledgeId;

    @TableField
    @Column(name = "screen_type", type = MySqlTypeConstant.VARCHAR, length = 16 , comment = "场景类型")
    private String screenType ;

    @TableField
    @Column(name = "knowledge_type", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "知识库类型")
    private String knowledgeType ;

    @TableField
    @Column(name = "sort_order", type = MySqlTypeConstant.INT, comment = "排序")
    private Integer sortOrder;

    @TableField
    @Column(name = "is_recommend", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为推荐")
    private Boolean isRecommend;

    // TODO 待优化，以宽表思路处理
    // 大文本场景
    // --->>>>>>>>>>>>>>> 章节编辑人员、内容编辑人员，都以|号进行分割_start ---->>>>>>>>>>>>>
    @TableField
    @Column(name = "chapter_editor", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "章节编辑人员")
    private String chapterEditor;

    @TableField
    @Column(name = "content_editor", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "内容编辑人员")
    private String contentEditor;
    // --->>>>>>>>>>>>>>> 章节编辑人员、内容编辑人员，都以|号进行分割_end ---->>>>>>>>>>>>>

    // 管理者场景
    // --->>>>>>>>>>>>>>> Leader人员和工作人员都以|号进行分割_start ---->>>>>>>>>>>>>
    @TableField
    @Column(name = "leader_role", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "管理者")
    private String leaderRole;

    @TableField
    @Column(name = "worker_role", type = MySqlTypeConstant.VARCHAR, length = 512, comment = "工作人员")
    private String workerRole ;
    // --->>>>>>>>>>>>>>> Leader人员和工作人员都以|号进行分割_end ---->>>>>>>>>>>>>

}
