package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文档审核任务数据传输对象
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class DocReviewTaskDto extends BaseDto {
    private String taskName;
    private Long sceneId;
    private Long documentReviewSceneId;
    private Long channelStreamId ;
    private String taskDescription;
    private String taskStatus;
    private Date taskStartTime;
    private Date taskEndTime;
    private String documentId;
    private String documentName;
    private String reviewListOption ;
    private Long auditId ;
    private String reviewList;
    private String reviewListKnowledgeBase;
    private String reviewResult;
    private String contractOverview;
    private String contractType;
    private String reviewPosition;
    private String contractMetadata;
    private List<Map<String , String>> contractMetadataMap ;

    private String reviewGenStatus;
    private String resultGenStatus ;
    private String documentParseStatus ;
    private String currentStepInfo ;
    private String reviewRules ;
    private List<Long> reviewRuleList;

    public static DocReviewTaskDto fromEntity(DocReviewTaskEntity entity) {
        DocReviewTaskDto dto = new DocReviewTaskDto();
        BeanUtil.copyProperties(entity, dto);

        if(entity.getContractMetadata() != null) {
            try {
                List<Map<String, String>> metadataMap = JSON.parseObject(
                        entity.getContractMetadata(),
                        new TypeReference<>() {}
                );
                dto.setContractMetadataMap(metadataMap);
            } catch (Exception e) {
                log.error("Error parsing contract metadata", e);
            }
        }

        if(StringUtils.isNotEmpty(entity.getReviewRules())) {
            // 以逗号作为分隔符，获取所有的规则ID
            String[] ruleIds = entity.getReviewRules().split(",");
            List<Long> ruleIdList = Arrays.stream(ruleIds)
                    .map(String::trim)  // 去除前后空格
                    .filter(StringUtils::isNotEmpty)  // 过滤空字符串
                    .map(Long::valueOf)  // 转换为Long类型
                    .collect(Collectors.toList());
            dto.setReviewRuleList(ruleIdList);
        }

        return dto;
    }

}
