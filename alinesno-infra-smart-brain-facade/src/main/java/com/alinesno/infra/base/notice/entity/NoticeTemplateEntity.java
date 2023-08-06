package com.alinesno.infra.base.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;

/**
 * 通知模板表
 */
@TableName("notice_template") // 指定数据库表名
public class NoticeTemplateEntity extends InfraBaseEntity {

    // 模板代码
    @TableField("template_code")
    private String templateCode;

    // 模板名称
    @TableField("template_name")
    private String templateName;

    // 模板内容
    @TableField("template_content")
    private String templateContent;

    // 模板类型
    @TableField("template_type")
    private String templateType;

    // 业务类型
    @TableField("business_type")
    private String businessType;

    // 模板所属的渠道
    @TableField("channel")
    private String channel;

    // 第三方模板的关联ID
    @TableField("third_party_template_id")
    private String thirdPartyTemplateId;

    // 省略了构造函数、getter 和 setter 方法

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getThirdPartyTemplateId() {
        return thirdPartyTemplateId;
    }

    public void setThirdPartyTemplateId(String thirdPartyTemplateId) {
        this.thirdPartyTemplateId = thirdPartyTemplateId;
    }
}
