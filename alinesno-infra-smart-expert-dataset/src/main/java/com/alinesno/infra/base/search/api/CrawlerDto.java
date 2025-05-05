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
    private String datasetId;

    /**
     * 爬虫的入口URL
     */
    private String baseUrl;

    /**
     * CSS选择器，用于精准定位页面元素
     * 通过指定的CSS选择器，爬虫可以准确地提取到所需的数据
     */
    private String cssSelector;

    /**
     * 频道流ID
     */
    @NotNull(message = "channelStreamId不能为空")
    private String channelStreamId;

    /**
     * 最大爬取页面数
     */
    private Integer maxPage = 10;

    /**
     * 是否允许扩散全站
     */
    private boolean allowSpread = false;

    /**
     * 最大爬取深度
     */
    private Integer maxDepth = 3;

    /**
     * 是否爬取 PDF 文件类型
     */
    private boolean crawlPdf = true;

    /**
     * 是否爬取 Word 文件类型（docx/doc）
     */
    private boolean crawlWord = true;

    /**
     * 是否爬取 Excel 文件类型（xlsx/xls）
     */
    private boolean crawlExcel = true;
}