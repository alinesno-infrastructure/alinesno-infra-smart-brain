package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.base.BaseDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考试成绩数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamScoreDto extends BaseDto {

    private Long pagerId;

    private Long sceneId;

    private Long examInfoId;

    private Long userId;

    private String userName;

    private String userCode;

    private Integer score;

    private Integer isPass;

    private Integer usedTime;

    private LocalDateTime submitTime;

    private Long reviewerId;

    private String reviewerName;

    private LocalDateTime reviewTime;

    private String remark;

    private Integer isDeleted;

    private Integer version;

    private JSONObject answers; ;

    private JSONArray questions ;

    // 分析结果保存
    private String analysisResult ;

}
