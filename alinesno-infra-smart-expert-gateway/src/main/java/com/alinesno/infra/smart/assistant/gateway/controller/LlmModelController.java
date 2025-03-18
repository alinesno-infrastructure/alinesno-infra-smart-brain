package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.lettuce.core.output.ByteArrayOutput;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "LlmModel")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/llmModel")
public class LlmModelController extends BaseController<LlmModelEntity, ILlmModelService> {

    @Autowired
    private ILlmModelService service;

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
        TableDataInfo list =  this.toPage(model, this.getFeign(), page);

        if(!CollectionUtils.isEmpty(list.getRows())){
            List<LlmModelEntity> rows = (List<LlmModelEntity>) list.getRows();
            for(LlmModelEntity entity : rows) {
                entity.setFieldProp(ModelTypeEnums.getDisplayNameByCode(entity.getModelType())) ;
            }
            list.setRows(rows);
        }

        return list ;
    }

    @DataPermissionSave
    @Override
    public AjaxResult save(Model model, @RequestBody LlmModelEntity entity) throws Exception {
        log.debug("entity = {}", ToStringBuilder.reflectionToString(entity));
        return super.save(model, entity);
    }

    /**
     * 获取所有大模型提供商信息
     * @return 包含所有大模型提供商信息的列表
     */
    @GetMapping("/getAllModelProvidersInfo")
    public AjaxResult getAllModelProvidersInfo() {
        List<Map<String, Object>> list =  LlmModelProviderEnums.getAllModelProvidersInfo();
        return AjaxResult.success(list);
    }

    @GetMapping("/getAllModelTypesInfo")
    public AjaxResult getAllModelTypesInfo() {
        List<Map<String, Object>> modelTypesInfo = ModelTypeEnums.getAllModelTypesInfo();
        return AjaxResult.success(modelTypesInfo);
    }

    /**
     * 列出所有配置的大模型
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listLlmMode")
    public AjaxResult listLlmMode(PermissionQuery query , String modelType) {

        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(LlmModelEntity.class) ;
        query.toWrapper(queryWrapper);

        if(StringUtils.isNotEmpty(modelType)){  // 根据类型筛选
            queryWrapper.eq(LlmModelEntity::getModelType , modelType) ;
        }

        List<LlmModelEntity> allLlmMode = service.list(queryWrapper);

        if(!CollectionUtils.isEmpty(allLlmMode)){
            for(LlmModelEntity entity : allLlmMode) {
                entity.setFieldProp(ModelTypeEnums.getDisplayNameByCode(entity.getModelType())) ;
            }
        }

        return AjaxResult.success(allLlmMode) ;
    }

    /**
     * 模型测试
     * @return
     */
    @PostMapping("/testLlmModel")
    public AjaxResult testLlmModel(@RequestBody @Validated TestLlmModelDto dto) {
        log.debug("dto = {}" , dto);
        String result = service.testLlmModel(dto) ;
        return AjaxResult.success("操作成功" , result) ;
    }

    @PostMapping("/getSpeech")
    public ResponseEntity<Resource> getSpeech(@RequestBody @Validated TestLlmModelDto dto) {

        String filePath = service.testLlmModel(dto) ;
        log.debug("getSpeech filePath = {}" , filePath);

        try {
            // 本地录音文件路径
            File audioFile = new File(filePath) ;

            // 确保文件存在
            if (!audioFile.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            FileInputStream fileInputStream = new FileInputStream(audioFile);
            InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(audioFile.length());
            headers.setContentDispositionFormData("attachment", audioFile.getName());

            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getGenerateImage")
    public ResponseEntity<byte[]> getGenerateImage(@RequestBody @Validated TestLlmModelDto dto) {

        String filePath = service.testLlmModel(dto) ;
        log.debug("getGenerateImage filePath = {}" , filePath);

        try {
            // 指定图片文件路径，这里需要根据实际情况修改
            File imageFile = new File(filePath) ;
            if (!imageFile.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // 读取图片文件
            BufferedImage image = ImageIO.read(imageFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error generating image" ,e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ILlmModelService getFeign() {
        return this.service;
    }
}