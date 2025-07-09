package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.scene.entity.ExamScoreEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 考试成绩数据传输对象
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamScoreDto extends BaseDto {

    private Long pagerId;

    private Long sceneId;

    private Long examInfoId;

    private Long userId;

    private String userName;

    private String userCode;

    private JSONArray reviewResult ;  // 阅卷结果

    private String examStatus ;

    private Long score;

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
    private JSONObject analysisResult ;

    /**
     * 转换实体类为数据传输对象
     * @param examScore
     * @return
     */
    public static ExamScoreDto fromEntity(ExamScoreEntity examScore) {

        ExamScoreDto dto = new ExamScoreDto();
        BeanUtils.copyProperties(examScore, dto);
        dto.setScore(examScore.getScore());

        if(examScore.getReviewResult() != null){
            try{
                dto.setReviewResult(JSONArray.parseArray(examScore.getReviewResult()));  // 阅卷结果
            }catch(Exception e){
                log.warn("转换阅卷结果失败：{}", e.getMessage());
            }
        }

        if(examScore.getAnswers() != null){
            try{
                dto.setAnswers(JSONObject.parseObject(examScore.getAnswers()));
            }catch(Exception e){
                log.warn("转换答案失败：{}", e.getMessage());
            }
        }

        if(examScore.getQuestions() != null){
            try{
                dto.setQuestions(JSONArray.parseArray(examScore.getQuestions()));
            }catch(Exception e){
                log.warn("转换问题失败：{}", e.getMessage());
            }
        }

        if(examScore.getAnalysisResult() != null){
            try {
                dto.setAnalysisResult(JSONObject.parseObject(examScore.getAnalysisResult()));
            }catch(Exception e){
                log.warn("转换分析结果失败：{}", e.getMessage());
            }
        }

        return dto;
    }
}
