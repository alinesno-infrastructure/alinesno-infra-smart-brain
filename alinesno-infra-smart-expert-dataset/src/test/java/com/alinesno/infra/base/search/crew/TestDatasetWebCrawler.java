package com.alinesno.infra.base.search.crew;

import com.alinesno.infra.base.search.api.CrawlerDto;

import java.util.List;

public class TestDatasetWebCrawler {

    public static void main(String[] args) {

        CrawlerDto dto = new CrawlerDto();

        String cssSelector = null ;

        dto.setChannelStreamId("123");
        dto.setAllowSpread(false);
        dto.setMaxPage(20);
        dto.setBaseUrl("http://portal.infra.linesno.com");
        dto.setCssSelector(cssSelector);

        List<WebContentInfo> result = new DatasetWebCrawler().parseWebsite(dto.getBaseUrl(), dto.getCssSelector(), dto  , null);
        for (WebContentInfo webContentInfo : result) {
            System.out.println(webContentInfo.getTitle());
            System.out.println(webContentInfo.getContent());
            System.out.println("----------------------------------------");
        }
    }

}
