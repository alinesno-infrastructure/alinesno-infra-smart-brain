package com.alinesno.infra.base.search.gateway.provider;

import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/base/search/vectorSearch")
public class VectorSearchController {

    @Autowired
    private IVectorDatasetService datasetService ;

    /**
     * 处理搜索Milvus集合的HTTP POST请求，并返回最近邻居的ID列表。
     *
     * @param dto 要搜索的集合名称。
     * @return 包含最近邻居ID列表的ResponseEntity对象。
     */
    @PostMapping("/search")
    public R<List<DocumentVectorBean>> search(@RequestBody VectorSearchDto dto) {

        long datasetId = dto.getDatasetId() ;
        VectorDatasetEntity datasetEntity = datasetService.getById(datasetId) ;
        dto.setCollectionName(datasetEntity.getCollectionName());

        List<DocumentVectorBean> topksList = datasetService.search(dto);

        log.debug("topsList = {}" , topksList);

        return R.ok(topksList);

    }

    /**
     * 使用rerank排序处理
     */
    @PostMapping("/rerankSearch")
    public R<List<DocumentVectorBean>> rerankSearch(@RequestBody VectorSearchDto dto) {

        long datasetId = dto.getDatasetId() ;
        VectorDatasetEntity datasetEntity = datasetService.getById(datasetId) ;
        dto.setCollectionName(datasetEntity.getCollectionName());

        List<DocumentVectorBean> topksList = datasetService.rerankSearch(dto);
        return R.ok(topksList);

    }

    /**
     * 记忆关联模块(记忆片断信息+知识库信息)
     * @param dto
     * @return
     */
    @PostMapping("/roleMemory")
    public R<String> roleMemory(@RequestBody VectorSearchDto dto) {

        log.debug("memoryQueryDto = {}" , dto);

        long datasetId = dto.getDatasetId() ;
        String information = dto.getSearchText() ;

        List<DocumentVectorBean> topksList = datasetService.rerankSearch(dto);

        return R.ok("ok");
    }


}
