package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;
import org.springframework.data.domain.Example;

import java.util.List;

/**
 * WelcomeConfigData 类
 */
@Data
public class WelcomeConfigData {

    /**
     * 是否启用
     */
    private String welcomeEnabled ;

    /**
     *  功能
     */
    private List<Feature> features ;

    /**
     * 示例
     */
    private List<Example> examples ;

    /**
     * 提示
     */
    private List<String> tips ;

    @Data
    public static class Example {
        private String label ; // 标题
        private String image ;  // 图片地址
        private int sort ; // 排序
    }

    @Data
    public static class Feature {
        private String title;  // 标题
        private String description ;  // 描述
        private String icon ;  // 图标
    }

}
