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
@TableName("general_agent_task")
public class GeneralAgentTaskEntity extends InfraBaseEntity {

    // 任务名称,所属场景，所属GeneralAgentSceneId、任务描述、任务状态、任务开始时间、任务结束时间
    @TableField
    @Column(name = "task_name", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "任务名称")
    private String taskName;

    @TableField
    @Column(name = "task_prompt", type = MySqlTypeConstant.VARCHAR, length = 512, comment = "任务提示")
    private String taskPrompt ;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "general_agent_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "通用助手场景ID")
    private Long generalAgentSceneId;

    @TableField
    @Column(name = "task_description", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "任务描述")
    private String taskDescription;

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
    @Column(name = "channel_stream_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "频道流ID")
    private String channelStreamId ;

    @TableField
    @Column(name = "outline", type = MySqlTypeConstant.LONGTEXT , comment = "任务执行大纲")
    private String outline ;

    @TableField
    @Column(name = "current_chapter_id", type = MySqlTypeConstant.BIGINT, length = 32 , comment = "当前章节ID")
    private Long currentChapterId ;

    @TableField
    @Column(name = "current_chapter_label", type = MySqlTypeConstant.TEXT, comment = "当前章节标签")
    private String currentChapterLabel ;


    // --->>>>>>>>>>>> 任务状态_start ---->>>>>>>>>>>>
    @TableField
    @Column(name = "gen_content_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成内容状态")
    private String genContentStatus ;

    @TableField
    @Column(name = "gen_plan_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成规划状态")
    private String genPlanStatus;

    @TableField
    @Column(name = "gen_result_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成规划状态")
    private String genResultStatus ;
    // --->>>>>>>>>>>> 任务状态_end ---->>>>>>>>>>>>
}
