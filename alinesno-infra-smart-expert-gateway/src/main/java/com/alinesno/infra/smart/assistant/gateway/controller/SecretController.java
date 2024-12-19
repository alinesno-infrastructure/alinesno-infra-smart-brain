package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.SecretEntity;
import com.alinesno.infra.smart.assistant.service.ISecretService;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Api(tags = "Secret")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/secret")
public class SecretController extends BaseController<SecretEntity, ISecretService> {

    @Autowired
    private ISecretService service;

    /**
     * 获取ApplicationEntity的DataTables数据
     *
     * @param request HttpServletRequest对象
     * @param model   Model对象
     * @param page    DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        TableDataInfo table = this.toPage(model, this.getFeign(), page);

        // 屏蔽密钥信息
        if (table != null) {
            List<?> rows = table.getRows();
            if (rows != null) {
                List<SecretEntity> secretEntities = new ArrayList<>();
                for (Object row : rows) {
                    if (row instanceof SecretEntity secretEntity) {
                        secretEntity.setSecretValue("");
                        secretEntities.add(secretEntity);
                    }
                }
                table.setRows(secretEntities);
            }
        }

        return table;
    }

    @Override
    public AjaxResult detail(@PathVariable String id) {
        SecretEntity e = service.findById(id);
        e.setSecretValue("");
        return AjaxResult.success(e);
    }

    @DataPermissionSave
    @Override
    public AjaxResult save(Model model, @RequestBody SecretEntity entity) throws Exception {

        // 判断这个组织下的secret name是事重复
        long count = service.lambdaQuery().eq(SecretEntity::getSecretName, entity.getSecretName())
                .eq(SecretEntity::getOrgId, entity.getOrgId()).count();

        Assert.isTrue(count == 0, "该组织下已经存在该名称的密钥，请勿重复添加！");

        return super.save(model, entity);
    }

    @Override
    public ISecretService getFeign() {
        return this.service;
    }
}