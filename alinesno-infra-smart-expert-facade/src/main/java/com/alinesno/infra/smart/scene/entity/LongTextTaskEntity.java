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
 * 数据分析规划实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("long_text_task")
public class LongTextTaskEntity extends InfraBaseEntity {

    // 任务名称,所属场景，所属GeneralAgentSceneId、任务描述、任务状态、任务开始时间、任务结束时间
    @TableField
    @Column(name = "task_name", type = MySqlTypeConstant.TEXT, comment = "任务名称")
    private String taskName;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "long_text_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "长文本助手场景ID")
    private Long LongTextSceneId ;

    @TableField
    @Column(name = "task_description", type = MySqlTypeConstant.TEXT, comment = "任务描述")
    private String taskDescription;

    @TableField
    @Column(name = "task_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "任务状态(not_run未运行|completed已运行|running运行中|generating生成中|cancelling取消中|cancelled已取消|failed运行失败)")
    private String taskStatus;

    @TableField
    @Column(name = "task_start_time", type = MySqlTypeConstant.DATETIME, comment = "任务开始时间")
    private Date taskStartTime;

    @TableField
    @Column(name = "task_end_time", type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date taskEndTime;

    @TableField
    @Column(name = "selected_template_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "已选择的模板ID")
    private String selectedTemplateId ;

    @TableField
    @Column(name = "attachments", type = MySqlTypeConstant.VARCHAR, comment = "附件ID号，以逗号进行分隔")
    private String attachments;

    @TableField
    @Column(name = "gen_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成状态(1生成菜单|0未生成)")
    private Integer genStatus = 0;

    @TableField
    @Column(name = "channel_stream_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "频道流ID")
    private String channelStreamId ;

    @TableField
    @Column(name = "outline", type = MySqlTypeConstant.LONGTEXT , comment = "任务执行大纲")
    private String outline ;

    @TableField
    @Column(name = "chapter_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "章节生成状态(not_run未生成|completed已生成|running生成中|generating生成中|cancelled已取消|failed生成失败)")
    private String chapterStatus ;

    @TableField
    @Column(name = "current_chapter_id", type = MySqlTypeConstant.BIGINT, length = 32 , comment = "当前章节ID")
    private Long currentChapterId ;

    @TableField
    @Column(name = "current_chapter_label", type = MySqlTypeConstant.TEXT, comment = "当前章节标签")
    private String currentChapterLabel ;

    // 生成章节限流
    @TableField
    @Column(name = "gen_chapter_limit", type = MySqlTypeConstant.INT, length = 32, comment = "生成章节限流(大于0则表示每分钟限流次数|0不限流)")
    private Integer genChapterLimit = 10;


}
