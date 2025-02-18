package com.alinesno.infra.base.search.memory.controller;

import com.alinesno.infra.base.search.memory.BaseMemoryStore;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/base/search/memory")
public class MemoryController extends SuperController {

    @Resource
    private BaseMemoryStore memoryStore;

    /**
     * 获取ApplicationEntity的DataTables数据
     *
     * @param request HttpServletRequest对象
     * @param page    DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, DatatablesPageBean page) {

        TableDataInfo tableDataInfo = new TableDataInfo();


        return tableDataInfo;
    }
}

