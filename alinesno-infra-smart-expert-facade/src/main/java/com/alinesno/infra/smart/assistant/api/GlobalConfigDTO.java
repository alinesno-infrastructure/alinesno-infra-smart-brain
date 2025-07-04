package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.entity.GlobalConfigEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * 智能体平台全局配置数据传输对象类
 * 用于在不同层之间传输智能体平台全局配置的相关数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalConfigDTO extends BaseDto {

    /**
     * 产品名称
     * 用于标识智能体平台的产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /**
     * 界面主题风格
     * 可以是深色主题或浅色主题等
     */
    @NotBlank(message = "界面主题风格不能为空")
    private String themeStyle;

    /**
     * 团队 logo 图片标识
     * 用于存储团队 logo 图片的相关标识信息
     */
    @NotBlank(message = "团队 logo 图片标识不能为空")
    private String logoImg;

    /**
     * 是否显示服务
     * 用于控制是否显示服务相关内容
     */
    @NotBlank(message = "是否显示服务信息不能为空")
    private String displayService;

    /**
     * 默认生成模型
     * 指定智能体平台的默认生成模型
     */
//    @NotBlank(message = "默认生成模型不能为空")
    private String defaultLargeModel;

    /**
     * 默认图片模型
     * 指定智能体平台的默认图片生成模型
     */
//    @NotBlank(message = "默认图片模型不能为空")
    private String defaultImageModel;

    /**
     * 频道最多人数
     * 限制每个频道可容纳的最多人数
     */
    @NotNull(message = "频道最多人数不能为空")
    @Min(value = 1, message = "频道最多人数不能小于 1")
    @Max(value = 1000, message = "频道最多人数不能大于 1000")
    private Integer maxChannelPeople;

    /**
     * 工作台地址
     */
    @NotBlank(message = "工作台地址不能为空")
    private String studioUrl;

    /**
     * 默认的 DTO 对象
     * @return
     */
    public static GlobalConfigDTO defaultDto() {
        GlobalConfigDTO dto = new GlobalConfigDTO();

       dto.setProductName("AIP智能体平台");
       dto.setThemeStyle("light");
       dto.setDisplayService("display");
       dto.setMaxChannelPeople(10);

        return dto ;
    }

    /**
     * 将 DTO 转换为实体类
     * @return GlobalConfigEntity 实体对象
     */
    public GlobalConfigEntity toEntity() {
        GlobalConfigEntity entity = new GlobalConfigEntity();

        BeanUtils.copyProperties(this, entity);

        entity.setProductName(this.productName);
        entity.setThemeStyle(this.themeStyle);
        entity.setLogoImg(this.logoImg);
        entity.setDisplayService(this.displayService);
        entity.setDefaultLargeModel(this.defaultLargeModel);
        entity.setDefaultImageModel(this.defaultImageModel);
        entity.setMaxChannelPeople(this.maxChannelPeople);
        entity.setStudioUrl(this.studioUrl);

        return entity;
    }
}