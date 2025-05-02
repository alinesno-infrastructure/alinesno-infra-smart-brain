package com.alinesno.infra.base.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alinesno.infra.base.search.api.CrawlerDto;
import com.alinesno.infra.base.search.crew.WebContentInfo;
import com.alinesno.infra.base.search.crew.WebCrawler;
import com.alinesno.infra.base.search.entity.DatasetKnowledgeEntity;
import com.alinesno.infra.base.search.enums.AutoImportStatusEnums;
import com.alinesno.infra.base.search.enums.DocumentStatusEnums;
import com.alinesno.infra.base.search.enums.FileTypeEnums;
import com.alinesno.infra.base.search.service.ICrawlerService;
import com.alinesno.infra.base.search.service.IDatasetKnowledgeService;
import com.alinesno.infra.common.core.cache.RedisUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CrawlerServiceImpl implements ICrawlerService {

    @Autowired
    private IDatasetKnowledgeService datasetKnowledgeService;

    private static final String REDIS_PREFIX = "aip:crawler:";

    @Override
    public void parseWebsite(CrawlerDto dto, PermissionQuery query) {
        String url = dto.getUrl();
        // 构建带有前缀的缓存键
        String cacheKey = REDIS_PREFIX + url;
        // 检查URL是否在缓存中，表示正在爬取
        if (RedisUtils.hasKey(cacheKey)) {
            throw new RuntimeException("当前URL正在爬取中，不能重复添加");
        }
        // 将带有前缀的URL添加到缓存中，表示开始爬取
        RedisUtils.setCacheObject(cacheKey, true);

        try {
            String cssSelector = dto.getCssSelector();
            List<WebContentInfo> webContentInfoList = WebCrawler.parseWebsite(url, cssSelector , dto);

            log.debug("webContentInfoList = {}" , webContentInfoList.size()) ;

            if (CollectionUtils.isNotEmpty(webContentInfoList)) {
                for (WebContentInfo info : webContentInfoList) {
                    // 数据先插入到DatasetKnowledge中
                    String datasetId = dto.getDatasetId();
                    String fileName = info.getTitle();

                    DatasetKnowledgeEntity e = new DatasetKnowledgeEntity();
                    CopyOptions copier = CopyOptions.create().ignoreNullValue();
                    BeanUtil.copyProperties(query, e, copier);

                    e.setDatasetId(Long.valueOf(datasetId));
                    e.setDocumentName(fileName);
                    e.setFilePath(info.getTitle() + "." + FileTypeEnums.TXT.getValue());
                    e.setHasUploaded(DocumentStatusEnums.UPLOADED.getCode());

                    long fileSizeInBytes = info.getContent().length();
                    double fileSizeInKB = (double) fileSizeInBytes / 1024;
                    e.setFileSize(String.valueOf(fileSizeInKB));
                    e.setDocumentContent(info.getContent());

                    e.setFieldProp(info.getType());
                    e.setFileType(FileTypeEnums.TXT.getValue());

                    // 后台会自动导入
                    e.setStatus(DocumentStatusEnums.IMPORT_NOT_COMPLETED.getCode());
                    e.setHasAutoImport(AutoImportStatusEnums.AUTO_IMPORT.getCode());

                    datasetKnowledgeService.save(e);
                }
            }

        } finally {
            // 爬取完成后，从缓存中移除带有前缀的URL
            RedisUtils.deleteObject(cacheKey);
        }
    }
}