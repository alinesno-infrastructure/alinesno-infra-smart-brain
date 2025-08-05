package com.alinesno.infra.base.search.gateway.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.search.api.SearchUpdateConfigDto;
import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.service.IAssetCatalogService;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @since 2023-09-30
 */
@Slf4j
@Api(tags = "VectorDataset")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/base/search/vectorDataset")
public class VectorDatasetController extends BaseController<VectorDatasetEntity, IVectorDatasetService> {

    @Autowired
    private IVectorDatasetService service;

    @Autowired
    private IAssetCatalogService catalogService ;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 获取工具选择器的数据
     * @param page
     * @return
     */
    @DataPermissionQuery
    @ResponseBody
    @PostMapping("/toolSelection")
    public TableDataInfo toolSelection(DatatablesPageBean page , PermissionQuery query) {
        return service.toolSelection(page , query) ; // . this.toPage(model, this.getFeign(), page);
    }

    /**
     * 处理搜索Milvus集合的HTTP POST请求，并返回最近邻居的ID列表。
     *
     * @param dto 要搜索的集合名称。
     * @return 包含最近邻居ID列表的ResponseEntity对象。
     */
    @PostMapping("/search")
    public R<List<DocumentVectorBean>> search(@RequestBody @Valid VectorSearchDto dto) {

        long datasetId = dto.getDatasetId() ;
        VectorDatasetEntity datasetEntity = service.getById(datasetId) ;
        dto.setCollectionName(datasetEntity.getCollectionName());

        List<DocumentVectorBean> topksList = service.search(dto);

        log.debug("topsList = {}" , topksList);

        return R.ok(topksList);

    }

    @DataPermissionSave
    @Override
    public AjaxResult save(Model model, @RequestBody VectorDatasetEntity entity) throws Exception {

        // 生成唯一的标识
        String collectionName = IdUtil.nanoId(8) ;

        long currentUserId = CurrentAccountJwt.getUserId();
        log.debug("currentUserId = {}" , currentUserId);

        entity.setDatasetSize(0);
        entity.setAccessPermission("person");
        entity.setOwnerId(currentUserId);
        entity.setCollectionName(collectionName);

        return super.save(model, entity);
    }

    /**
     * 更新数据集搜索配置
     * @param configDto
     * @return
     */
    @PostMapping("/updateDatasetConfig")
    public AjaxResult updateDatasetConfig(@RequestBody SearchUpdateConfigDto configDto){
        service.updateDatasetConfig(configDto);
        return AjaxResult.success("success") ;
    }

    @DataPermissionQuery
    @GetMapping("/catalogManifestTreeSelect")
    public AjaxResult catalogManifestTreeSelect(PermissionQuery query){
        String type = "dataset" ;
        return AjaxResult.success("success" , catalogService.catalogManifestTreeSelect(query , type)) ;
    }

    /**
     * 删除数据集
     * @return
     */
    @DeleteMapping("/deleteDataset")
    public AjaxResult deleteDataset(@RequestParam String datasetId){
        service.deleteDataset(datasetId);
        return AjaxResult.success("success") ;
    }

    @Override
    public IVectorDatasetService getFeign() {
        return this.service;
    }
}