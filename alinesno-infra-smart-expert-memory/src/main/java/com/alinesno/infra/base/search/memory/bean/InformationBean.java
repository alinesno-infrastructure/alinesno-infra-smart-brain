package com.alinesno.infra.base.search.memory.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

/**
 * 用于存储解析后的信息实体。
 */
@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationBean {

    @JSONField(name = "reflection")
    public String reflection;

    @JSONField(name = "sentence_id")
    public String sentenceId;

    @JSONField(name = "time_info")
    public String timeInfo;

    @JSONField(name = "key_information")
    public String keyInformation;

    @JSONField(name = "keywords")
    public String keywords;

    @JSONField(name = "timestamp")
    private String timestamp ;

}