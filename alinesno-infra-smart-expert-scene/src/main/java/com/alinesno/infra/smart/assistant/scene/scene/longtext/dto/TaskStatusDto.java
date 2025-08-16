package com.alinesno.infra.smart.assistant.scene.scene.longtext.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

/**
 * 描述：任务状态和进度信息
 * TaskStatusDto
 */
@Data
public class TaskStatusDto {

    private Long taskId;
    private String status;
    private int progress;

    // --->>>>>>>>>>>> 任务状态_start ---->>>>>>>>>>>>
    private String gentContentStatus ;
    private String genPlanStatus;
    private String genResultStatus ;
    // --->>>>>>>>>>>> 任务状态_end ---->>>>>>>>>>>>

}