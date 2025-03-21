package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    protected CloudStorageConsumer cloudStorageConsumer;

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

    /**
     * 测试录音识别
     * @return 语音识别结果
     */
    @PostMapping("/testRecognition")
    public AjaxResult testRecognition(
            @RequestParam("modelType") String modelType,
            @RequestParam("providerCode") String providerCode,
            @RequestParam("apiUrl") String apiUrl,
            @RequestParam("apiKey") String apiKey,
            @RequestParam("model") String model,

            @RequestParam("act") String act,
            @RequestParam("prompt") String prompt,
            @RequestParam("duration") String duration,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return AjaxResult.error("文件为空");
        }

        // 获取临时目录
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 生成唯一的文件名
        String fileName = IdUtil.fastSimpleUUID() + "_" + file.getOriginalFilename();
        fileName = fileName.replaceFirst("[.][^.]+$", ".wav"); // 确保文件名为 .wav 格式
        Path filePath = tempDir.resolve(fileName);

        try {
            // 将 MultipartFile 转换为 File
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);

            // 读取音频文件
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(tempFile);
            AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

            // 保存为 WAV 格式
            AudioSystem.write(audioInputStream, targetType, filePath.toFile());
            log.debug("文件保存路径: " + filePath);

            // 关闭音频流
            audioInputStream.close();

            // 删除临时文件
            tempFile.delete();
        } catch (Exception e) {
            log.error("文件转换为 WAV 格式失败" , e);
            return AjaxResult.error("文件转换为 WAV 格式失败: " + e.getMessage());
        }

        // 输出保存目录
        System.out.println("文件保存目录: " + filePath);

        // 打印其它参数
        log.debug("act: " + act);
        log.debug("prompt: " + prompt);
        log.debug("duration: " + duration);

        TestLlmModelDto dto = new TestLlmModelDto() ;

        R<String> r = cloudStorageConsumer.uploadCallbackUrl(filePath.toFile().getAbsoluteFile() , "qiniu-kodo-pub");

        List<String> audioList = new ArrayList<>();
        audioList.add(r.getData());

        dto.setAudioList(audioList);
        dto.setModelType(modelType);
        dto.setProviderCode(providerCode);
        dto.setApiUrl(apiUrl);
        dto.setApiKey(apiKey);
        dto.setModel(model);

        String result = service.testLlmModel(dto) ;
        return AjaxResult.success("操作成功" , result) ;
    }

    @PostMapping("/getSpeech")
    public ResponseEntity<Resource> getSpeech(@RequestBody @Validated TestLlmModelDto dto) {
        return getResourceResponseEntity(dto);
    }

    @NotNull
    private ResponseEntity<Resource> getResourceResponseEntity(TestLlmModelDto dto) {
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

    /**
     * 根据模型ID获取语音
     *
     * @param modelId
     * @return
     */
    @GetMapping("/getSpeechByModelId")
    public ResponseEntity<Resource> getSpeechByModelId(String modelId , String voice) {
        LlmModelEntity modelEntity = service.getById(modelId) ;

        TestLlmModelDto dto = new TestLlmModelDto() ;
        dto.setModelType(modelEntity.getModelType());
        dto.setProviderCode(modelEntity.getProviderCode());
        dto.setApiUrl(modelEntity.getApiUrl());
        dto.setApiKey(modelEntity.getApiKey());
        dto.setModel(modelEntity.getModel());
        dto.setTestChannelId(modelEntity.getId());
        dto.setSecretKey(modelEntity.getSecretKey());
        dto.setVoice(voice);

        return getResourceResponseEntity(dto);
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

    /**
     * 获取模型声音 getVoiceModelSpeech
     * @return
     */
    @GetMapping("/getVoiceModelSpeech")
    public AjaxResult getVoiceModelSpeech(String modelId) {
        Map<String , Object> voiceSpeech = service.getVoiceModelSpeech(modelId) ;
        return AjaxResult.success(voiceSpeech) ;
    }


    @Override
    public ILlmModelService getFeign() {
        return this.service;
    }
}