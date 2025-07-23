package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.base.BaseDto;
import com.dtflys.forest.annotation.JSONBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文档模板DTO，用于接收文档模板配置信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentTemplateDTO extends BaseDto {
    /**
     * 模板名称
     */
    @NotNull(message = "模板名称不能为空")
    private String name;
    
    /**
     * 模板描述
     */
    @NotNull(message = "模板描述不能为空")
    private String description;
    
    /**
     * 各级标题样式配置
     */
    @NotNull(message = "各级标题样式不能为空")
    private JSONArray headings;
    
    /**
     * 正文样式配置
     */
    @NotNull(message = "正文样式不能为空")
    private JSONObject body;
    
    /**
     * 页边距配置
     */
    @NotNull(message = "页边距不能为空")
    private JSONObject margin;

    /**
     * 页边距预设
     */
    @NotNull(message = "页边距预设")
    private String selectedMarginPreset ;
    
    /**
     * 页面尺寸（如"A4"）
     */
    @NotNull(message = "页面尺寸不能为空")
    private String pageSize;
    
    /**
     * 页眉高度（单位：px）
     */
    @NotNull(message = "页眉高度不能为空")
    private Integer headerHeight;
    
    /**
     * 页脚高度（单位：px）
     */
    @NotNull(message = "页脚高度不能为空")
    private Integer footerHeight;
    
    /**
     * 封面HTML内容
     */
    @NotNull(message = "封面HTML不能为空")
    private String coverHtml;
    
    /**
     * 页眉HTML内容
     */
    @NotNull(message = "页眉HTML不能为空")
    private String headerHtml;
    
    /**
     * 页脚HTML内容
     */
    @NotNull(message = "页脚HTML不能为空")
    private String footerHtml;
    
    /**
     * 正式文档页眉HTML内容
     */
    @NotNull(message = "正式文档页眉HTML不能为空")
    private String officialHeaderHtml;
    
    /**
     * 正式文档页眉高度（单位：px）
     */
    @NotNull(message = "正式文档页眉高度不能为空")
    private Integer officialHeaderHeight;
    
    /**
     * 页眉线颜色
     */
    @NotNull(message = "页眉线颜色不能为空")
    private String headerLineColor;
    
    /**
     * 页眉线宽度（单位：px）
     */
    @NotNull(message = "页眉线宽度不能为空")
    private Integer headerLineWidth;

    /**
     * 转换为内容格式化布局配置
     * @return
     */
    public ContentFormatterLayoutDto toContentFormatterLayoutDto(){
        ContentFormatterLayoutDto dto = new ContentFormatterLayoutDto();

        dto.setName(this.getName());
        dto.setLayoutDesc(this.getDescription());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("headings", this.getHeadings());
        jsonObject.put("body", this.getBody());
        jsonObject.put("margin", this.getMargin());
        jsonObject.put("selectedMarginPreset", this.getSelectedMarginPreset());
        jsonObject.put("pageSize", this.getPageSize());
        jsonObject.put("headerHeight", this.getHeaderHeight());
        jsonObject.put("footerHeight", this.getFooterHeight());
        jsonObject.put("coverHtml", this.getCoverHtml());
        jsonObject.put("headerHtml", this.getHeaderHtml());
        jsonObject.put("footerHtml", this.getFooterHtml());
        jsonObject.put("officialHeaderHtml", this.getOfficialHeaderHtml());
        jsonObject.put("officialHeaderHeight", this.getOfficialHeaderHeight());
        jsonObject.put("headerLineColor", this.getHeaderLineColor());
        jsonObject.put("headerLineWidth", this.getHeaderLineWidth());

        if(!jsonObject.isEmpty()){
            dto.setLayoutConfig(jsonObject.toJSONString());
        }

        dto.setSort(1);
        dto.setGroupId(1L);

        // 处理权限
        dto.setOrgId(this.getOrgId());
        dto.setOperatorId(this.getOperatorId());
        dto.setDepartmentId(this.getDepartmentId());
        dto.setId(this.getId());

        return dto ;
    }

    /**
     * 从ContentFormatterLayoutDto转换成DocumentTemplateDTO
     */
    public static DocumentTemplateDTO fromContentFormatterLayoutDto(ContentFormatterLayoutDto dto) {
        DocumentTemplateDTO templateDTO = new DocumentTemplateDTO();
        templateDTO.setName(dto.getName());
        templateDTO.setDescription(dto.getLayoutDesc());

        // 解析布局配置JSON字符串
        String layoutConfig = dto.getLayoutConfig();
        if (layoutConfig != null && !layoutConfig.isEmpty()) {
            JSONObject jsonObject = JSONObject.parseObject(layoutConfig);

            templateDTO.setHeadings(jsonObject.getJSONArray("headings"));
            templateDTO.setBody(jsonObject.getJSONObject("body"));
            templateDTO.setMargin(jsonObject.getJSONObject("margin"));
            templateDTO.setSelectedMarginPreset(jsonObject.getString("selectedMarginPreset"));
            templateDTO.setPageSize(jsonObject.getString("pageSize"));
            templateDTO.setHeaderHeight(jsonObject.getInteger("headerHeight"));
            templateDTO.setFooterHeight(jsonObject.getInteger("footerHeight"));
            templateDTO.setCoverHtml(jsonObject.getString("coverHtml"));
            templateDTO.setHeaderHtml(jsonObject.getString("headerHtml"));
            templateDTO.setFooterHtml(jsonObject.getString("footerHtml"));
            templateDTO.setOfficialHeaderHtml(jsonObject.getString("officialHeaderHtml"));
            templateDTO.setOfficialHeaderHeight(jsonObject.getInteger("officialHeaderHeight"));
            templateDTO.setHeaderLineColor(jsonObject.getString("headerLineColor"));
            templateDTO.setHeaderLineWidth(jsonObject.getInteger("headerLineWidth"));
        }

        templateDTO.setOrgId(dto.getOrgId());
        templateDTO.setOperatorId(dto.getOperatorId());
        templateDTO.setDepartmentId(dto.getDepartmentId());
        templateDTO.setId(dto.getId());

        return templateDTO;
    }

}