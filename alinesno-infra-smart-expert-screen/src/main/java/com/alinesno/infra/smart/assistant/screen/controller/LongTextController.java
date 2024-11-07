package com.alinesno.infra.smart.assistant.screen.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.screen.dto.LongTextDto;
import com.alinesno.infra.smart.assistant.screen.dto.ScreenDto;
import com.alinesno.infra.smart.assistant.screen.entity.LongTextEntity;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.service.ILongTextService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/infra/smart/assistant/screenLongtext")
public class LongTextController extends BaseController<LongTextEntity, ILongTextService> {

    @Autowired
    private ILongTextService service;

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
     * 保存或更新屏幕场景
     * @param longTextDto 屏幕场景的DTO
     * @return 操作结果
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdate")
    public AjaxResult saveOrUpdate(@RequestBody LongTextDto longTextDto) {
        log.debug("screenDto = {}", ToStringBuilder.reflectionToString(longTextDto));

        LongTextEntity screenEntity = new LongTextEntity();
        BeanUtils.copyProperties(longTextDto, screenEntity);

        service.saveOrUpdate(screenEntity);

        return ok() ;
    }

    @Override
    public ILongTextService getFeign() {
        return this.service;
    }
}