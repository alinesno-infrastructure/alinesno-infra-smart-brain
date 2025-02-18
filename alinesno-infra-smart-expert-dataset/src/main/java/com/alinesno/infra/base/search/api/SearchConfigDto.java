package com.alinesno.infra.base.search.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SearchConfigDTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class SearchConfigDto extends BaseDto {

    private String resource;

    private String llmUrl;

    private String apiKey;

    private String baseModel;

    private String vectorModelType;

    private String customVectorModelUrl;

    private String esIndexUrl;

    private String indexDbUsername;

    private String indexDbPassword;

    private String vectorDbUrl ;

    private String vectorDbType;

    private String vectorDbUsername;

    private String vectorDbPassword;

}