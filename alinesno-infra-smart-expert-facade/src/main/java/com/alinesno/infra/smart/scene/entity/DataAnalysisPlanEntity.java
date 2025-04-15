package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 数据分析规划实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("data_analysis_plan")
public class DataAnalysisPlanEntity extends InfraBaseEntity {

    // 数据分析规划的名称、层级、排序等、编写要求、附加要求、父节点ID、是否为叶子节点
    @TableField
    @Column(name = "plan_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "数据分析规划的名称")
    private String planName;

    @TableField
    @Column(name = "plan_level", type = MySqlTypeConstant.INT, comment = "数据分析规划的层级")
    private Integer planLevel;

    @TableField
    @Column(name = "plan_sort", type = MySqlTypeConstant.INT, comment = "数据分析规划的排序")
    private Integer planSort;

    @TableField
    @Column(name = "plan_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "编写要求")
    private String planRequire;

    @TableField
    @Column(name = "plan_additional_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "附加要求")
    private String planAdditionalRequire;

    @TableField
    @Column(name = "parent_plan_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "父数据分析规划ID")
    private Long parentChapterId;

    @TableField
    @Column(name = "is_leaf", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为叶子节点")
    private Boolean isLeaf;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId ; // 关联的场景ID

    @TableField
    @Column(name = "data_analysis_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的长文本场景ID")
    private Long dataAnalysisSceneId ; // 关联的长文本场景ID

    // 编写人员
    @TableField
    @Column(name = "data_miner_engineer_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "数据分析规划编辑人员")
    private Long dataMinerEngineerId;

    @TableField
    @Column(name = "content", type = MySqlTypeConstant.LONGTEXT, comment = "本数据分析规划文本内容")
    private String content ;

    // 子组件
    @TableField(exist = false)
    private List<DataAnalysisPlanEntity> subtitles ;

}
