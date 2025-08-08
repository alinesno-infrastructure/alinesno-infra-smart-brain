package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public static DocReviewTaskDto fromEntity(DocReviewTaskEntity entity){
        DocReviewTaskDto dto = new DocReviewTaskDto();
        BeanUtil.copyProperties(entity , dto);

        if(entity.getContractMetadata() != null) {
            // Convert JSON string to List<Map>
            try {
                List<Map<String, String>> metadataMap = JSON.parseObject(
                        entity.getContractMetadata(),
                        new TypeReference<List<Map<String, String>>>() {}
                );
                dto.setContractMetadataMap(metadataMap);
            } catch (Exception e) {
                // Handle parsing exception appropriately
                log.error("Error parsing contract metadata", e);
            }
        }

        return dto ;
    }

}
