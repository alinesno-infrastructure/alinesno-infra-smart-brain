package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景实体类,针对于具体的业务场景配置管理
 */
@EqualsAndHashCode(callSuper = true)
@TableName("scene")
@Data
public class SceneEntity extends InfraBaseEntity {

    // 场景名称、场景Banner图片、场景描述、场景使用次数、场景文档类型、场景知识库ID
    @TableField
    @Column(name = "scene_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "场景名称")
    private String sceneName;

    @TableField
    @Column(name = "scene_banner", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "场景Banner图片")
    private String sceneBanner;

    @TableField
    @Column(name = "scene_scope", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "场景数据范围")
    private String sceneScope ;

    @TableField
    @Column(name = "scene_desc", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "场景描述")
    private String sceneDesc;

    @TableField
    @Column(name = "scene_count", type = MySqlTypeConstant.INT, comment = "场景使用次数")
    private Integer sceneCount;

    @TableField
    @Column(name = "doc_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "场景文档类型")
    private String docType;

    @TableField
    @Column(name = "knowledge_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "场景知识库ID")
    private String knowledgeId;

    @TableField
    @Column(name = "scene_type", type = MySqlTypeConstant.VARCHAR, length = 32 , comment = "场景类型")
    private String sceneType ;

    @TableField
    @Column(name = "knowledge_type", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "知识库类型")
    private String knowledgeType ;

    @TableField
    @Column(name = "sort_order", type = MySqlTypeConstant.INT, comment = "排序")
    private Integer sortOrder;

    @TableField
    @Column(name = "is_recommend", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为推荐")
    private Boolean isRecommend;

    @TableField("greeting_question")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 512)
    @ColumnComment("开场白问题")
    private String greetingQuestion ; // 开场白问题

    // 管理者场景
    // --->>>>>>>>>>>>>>> Leader人员和工作人员都以|号进行分割_start ---->>>>>>>>>>>>>
    @TableField
    @Column(name = "leader_role", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "管理者")
    private String leaderRole;

    @TableField
    @Column(name = "worker_role", type = MySqlTypeConstant.VARCHAR, length = 512, comment = "工作人员")
    private String workerRole ;
    // --->>>>>>>>>>>>>>> Leader人员和工作人员都以|号进行分割_end ---->>>>>>>>>>>>>

    @TableField
    @Column(name = "config_data", type = MySqlTypeConstant.TEXT , comment = "配置数据(以json数据结构保存)")
    private String configData ;

}
