package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能体平台全局配置实体类，用于与数据库表 agent_platform_global_config 进行映射
 * 该类继承自 InfraBaseEntity，包含了智能体平台全局配置的相关信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("global_config") // MyBatis-Plus 表名注解，指定对应的数据库表名
@Table(value = "global_config", comment = "智能体平台全局配置表，存储平台的各项全局配置信息")
public class GlobalConfigEntity extends InfraBaseEntity {

    /**
     * 界面主题风格，对应数据库表中的 theme_style 字段
     * 例如 'light' 表示浅色主题，'dark' 表示深色主题
     */
    @TableField(value = "theme_style") // MyBatis-Plus 字段注解，指定该属性对应数据库表中的字段名
    @Column(value = "theme_style", type = MySqlTypeConstant.VARCHAR , comment = "界面主题风格，如 light 为浅色主题，dark 为深色主题")
    private String themeStyle;

    /**
     * 团队 logo 图片标识，对应数据库表中的 logo_img 字段
     * 用于存储团队 logo 图片的相关标识信息
     */
    @TableField(value = "logo_img")
    @Column(value = "logo_img", type = MySqlTypeConstant.VARCHAR, comment = "团队 logo 图片标识，用于关联团队 logo 图片信息")
    private String logoImg;

    /**
     * 产品名称，对应数据库表中的 product_name 字段
     * 用于标识智能体平台的产品名称
     */
    @TableField(value = "product_name")
    @Column(value = "product_name", type = MySqlTypeConstant.VARCHAR, comment = "智能体平台的产品名称")
    private String productName;

    /**
     * 默认大语言模型标识，对应数据库表中的 default_large_model 字段
     * 用于指定智能体平台使用的默认大语言模型
     */
    @TableField(value = "default_large_model")
    @Column(value = "default_large_model", type = MySqlTypeConstant.VARCHAR, comment = "默认大语言模型标识，指定平台使用的默认大语言模型")
    private String defaultLargeModel;

    /**
     * 默认图片模型标识，对应数据库表中的 default_image_model 字段
     * 用于指定智能体平台使用的默认图片生成模型
     */
    @TableField(value = "default_image_model")
    @Column(value = "default_image_model", type = MySqlTypeConstant.VARCHAR, comment = "默认图片模型标识，指定平台使用的默认图片生成模型")
    private String defaultImageModel;

    /**
     * 频道最多人数，对应数据库表中的 max_channel_people 字段
     * 限制每个频道可容纳的最多人数
     */
    @TableField(value = "max_channel_people")
    @Column(value = "max_channel_people", type = MySqlTypeConstant.INT, comment = "频道最多可容纳的人数")
    private Integer maxChannelPeople;

    /**
     * 是否显示服务，对应数据库表中的 is_display 字段
     * 例如 'none' 表示不显示，'show' 表示显示
     */
    @TableField(value = "display_service")
    @Column(value = "display_service", type = MySqlTypeConstant.VARCHAR , comment = "是否显示服务，none 为不显示，show 为显示")
    private String displayService;

    /**
     * 大语言模型提示词，对应数据库表中的 llm_prompt 字段
     * 用于向大语言模型提供输入提示
     */
    @TableField(value = "llm_prompt")
    @Column(value = "llm_prompt", type = MySqlTypeConstant.TEXT , comment = "大语言模型的输入提示词")
    private String llmPrompt;

    /**
     * 图片模型提示词，对应数据库表中的 image_prompt 字段
     * 用于向图片生成模型提供输入提示
     */
    @TableField(value = "image_prompt")
    @Column(value = "image_prompt", type = MySqlTypeConstant.TEXT , comment = "图片生成模型的输入提示词")
    private String imagePrompt;

    /**
     * 工作台地址，对应数据库表中的 studio_url 字段
     */
    @TableField(value = "studio_url")
    @Column(value = "studio_url", type = MySqlTypeConstant.VARCHAR , comment = "工作台地址")
    private String studioUrl ;
}    