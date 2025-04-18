package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRuleGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.ExcelParserToMapPrinter;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.enums.ReviewPositionEnums;
import com.alinesno.infra.smart.scene.enums.RiskLevelEnums;
import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
@RequestMapping("/api/infra/smart/assistant/scene/documentReview/rule")
public class DocReviewRuleController extends BaseController<DocReviewRulesEntity, IDocReviewRulesService> {

    @Autowired
    private IDocReviewRulesService service;

    @Autowired
    private IDocReviewRuleGroupService  ruleGroupService;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 新增加规则
     * @return
     */
    @DataPermissionScope
    @PostMapping("/saveOrUpdateRule")
    public AjaxResult addRule(@RequestBody @Validated DocReviewRulesDto dto) {

        Assert.isTrue(RiskLevelEnums.contains(dto.getRiskLevel()) , "风险级别错误");
        Assert.isTrue(ReviewPositionEnums.contains(dto.getReviewPosition()) , "审核立场错误");

        log.debug("新增规则：{}", ToStringBuilder.reflectionToString(dto));

        DocReviewRulesEntity entity = new DocReviewRulesEntity() ;
        BeanUtils.copyProperties(dto , entity) ;

        service.saveOrUpdate(entity);

        return ok() ;
    }


    /**
     * 文件上传
     * @return
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, PermissionQuery query) {

        String originalFilename = file.getOriginalFilename();

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath, newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        Map<String, List<Map<String, Object>>> result = ExcelParserToMapPrinter.parseExcelToMap(targetFile.getAbsolutePath());
        ExcelParserToMapPrinter.printMap(result);

        ruleGroupService.importExcelData(result , query) ;

        return AjaxResult.success("审核规则导入成功");
    }

        /**
         * 通过ids查询出规则列表
         * @return
         */
    @DataPermissionQuery
    @GetMapping("/listRuleByIds")
    public AjaxResult listRuleByIds(@RequestParam String idsStr) {

        // 使用 Stream API 分割字符串并转换为 Long 类型
        List<Long> idsList = Arrays.stream(idsStr.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return AjaxResult.success(service.listByIds(idsList)) ;
    }

    @Override
    public IDocReviewRulesService getFeign() {
        return this.service;
    }
}