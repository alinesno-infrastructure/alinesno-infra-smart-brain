package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.SmartDocumentConsumer;
import com.alinesno.infra.smart.im.dto.MessageExportWordDto;
import com.alinesno.infra.smart.im.entity.MessageReferenceEntity;
import com.alinesno.infra.smart.im.service.IMessageReferenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 消息引用控制
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/base/im/messageReference")
public class MessageReferenceController {

    @Autowired
    private IMessageReferenceService messageReferenceService;

    @Autowired
    private SmartDocumentConsumer smartDocumentConsumer;

    /**
     * 通过消息获取到引用
     */
    @RequestMapping("/getByMessageId")
    public AjaxResult getByMessageId(@RequestParam String messageId) {

        LambdaQueryWrapper<MessageReferenceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageReferenceEntity::getMessageId, messageId);

        List<MessageReferenceEntity> messageReferences = messageReferenceService.list(queryWrapper);

        return AjaxResult.success("查询成功." , messageReferences);
    }


    /**
     * 导出word文档
     */
    @PostMapping("/exportWord")
    public ResponseEntity<byte[]> exportWord(@RequestBody @Validated MessageExportWordDto dto) throws Exception {

        String content = dto.getContent();

        // 创建临时 markdown 文件并写入内容（使用 UTF-8）
        File file = File.createTempFile("markdown_", ".md");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
        }

        // 调用转换器，返回 docx 二进制
        byte[] documentBytes = smartDocumentConsumer.markdownToDocx(file);

        // 构造文件名
        String rawName = Optional.ofNullable(dto.getDocumentName()).filter(s -> !s.trim().isEmpty()).orElse("document");
        if (!rawName.toLowerCase().endsWith(".docx")) {
            rawName = rawName + ".docx";
        }

        // 对文件名做 URL 编码，兼容中文
        String encodedFileName = URLEncoder.encode(rawName, StandardCharsets.UTF_8).replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        // 推荐使用 filename* 指定编码：
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
        headers.setContentLength(documentBytes.length);

        // 清理临时文件并返回
        try {
            return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
