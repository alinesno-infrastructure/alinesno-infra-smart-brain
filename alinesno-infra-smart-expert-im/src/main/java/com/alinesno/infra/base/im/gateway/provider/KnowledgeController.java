package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.io.FileTypeUtil;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

/**
 * 文档控制层
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/knowledge")
public class KnowledgeController {

    @Autowired
    private BaseSearchConsumer searchController ;

    @Autowired
    private IChannelService channelService ;

    @Autowired
    private ITaskService taskService ;

    @Autowired
    private IMessageService messageService ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult importData(@RequestPart("file") MultipartFile file, long channelId) throws Exception {

        // localPath文件夹路径是否存在，不存在则创建
        FileUtils.forceMkdir(new File(localPath)) ;

        File tmpFile = new File(localPath , Objects.requireNonNull(file.getOriginalFilename())) ;
        file.transferTo(tmpFile);

        String fileType = FileTypeUtil.getType(tmpFile) ;

        // 获取原始文件名
        String fileName = file.getOriginalFilename();
        ChatMessageDto personDto = AgentUtils.getUploadChatMessageDto(fileName , fileType) ;

        // 上传到知识库角色
        ChannelEntity channelEntity = channelService.getById(channelId) ;
        String datasetId = channelEntity.getKnowledgeId() ;

        R<String> result = searchController.datasetUpload(tmpFile.getAbsolutePath() , datasetId , progress -> {
            log.debug("total bytes: " + progress.getTotalBytes());   // 文件大小
            log.debug("current bytes: " + progress.getCurrentBytes());   // 已上传字节数
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比

            if (progress.isDone()) {   // 是否上传完成
                log.debug("--------   Upload Completed!   --------");

                // 拼接到知识库类型，先判断之前是否有同一类型的，如果包含则不往上拼接
                String knowledgeType = channelEntity.getKnowledgeType() ;
                if(knowledgeType == null || !knowledgeType.contains(fileType)){
                    knowledgeType = (knowledgeType==null?"":knowledgeType+ "|") + fileType ;
                    channelEntity.setKnowledgeType(knowledgeType);
                }
                channelService.updateById(channelEntity) ;

                // 保存消息实体
                messageService.saveChatMessage(personDto , channelId) ;
            }

        }) ;
        log.debug("upload file result = {}" , result);

        FileUtils.forceDelete(tmpFile);

        return AjaxResult.success(personDto) ;
    }



}
