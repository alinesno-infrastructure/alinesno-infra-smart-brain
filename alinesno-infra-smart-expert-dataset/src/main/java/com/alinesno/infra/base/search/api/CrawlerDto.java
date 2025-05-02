package com.alinesno.infra.base.search.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 爬虫数据传输对象
 * 用于封装爬虫操作的相关数据
 */
@Data
public class CrawlerDto {

    /**
     * 数据集ID
     */
    @NotBlank(message = "datasetId不能为空")
    private String datasetId ;

    /**
     * 需要爬取的页面URL
     * 这是爬虫开始执行时需要的入口地址
     */
    @NotBlank(message = "url不能为空")
    private String url;

    /**
     * CSS选择器，用于精准定位页面元素
     * 通过指定的CSS选择器，爬虫可以准确地提取到所需的数据
     */
    private String cssSelector;

    /**
     * 频道流ID
     */
    @NotNull(message = "channelStreamId不能为空")
    private String channelStreamId ;

    /**
     * 最大爬取页面数
     */
    private Integer maxPage = 1000 ;

}
