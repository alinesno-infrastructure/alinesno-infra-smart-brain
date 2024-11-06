package com.alinesno.infra.smart.assistant.screen.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.screen.dto.ChapterDto;
import com.alinesno.infra.smart.assistant.screen.dto.TreeNodeDto;
import com.alinesno.infra.smart.assistant.screen.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.screen.service.IChapterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/screenChapter")
public class ChapterController extends BaseController<ChapterEntity, IChapterService> {

    @Autowired
    private IChapterService service;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 接收JSON数据并保存到数据库
     * @param chapters JSON格式的章节列表
     * @return 操作结果
     */
    @PostMapping("/saveChapters")
    public AjaxResult saveChapters(@RequestBody List<ChapterDto> chapters , @RequestParam("screenId") long screenId) {
        service.saveChaptersWithHierarchy(chapters, null, 1 , screenId);
        return ok() ;
    }

    /**
     * 获取章节的树结构
     * @return 章节的树结构
     */
    @GetMapping("/getChapterTree")
    public ResponseEntity<List<TreeNodeDto>> getChapterTree(@RequestParam("screenId") long screenId) {
        List<TreeNodeDto> tree = service.getChapterTree(screenId);
        return ResponseEntity.ok(tree);
    }

    @Override
    public IChapterService getFeign() {
        return this.service;
    }
}