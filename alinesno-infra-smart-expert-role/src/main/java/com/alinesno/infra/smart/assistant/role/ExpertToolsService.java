package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.im.enums.DocumentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 专家相关工具类实现
 */
@Slf4j
public abstract class ExpertToolsService {

    @NotNull
    protected static String generatorFileResponse(String fileType, String fileName , String fileLink) {

        String icon = DocumentTypeEnum.getIconByFileType(fileType);
        String escapedFileName = StringEscapeUtils.escapeHtml4(fileName);

        fileLink = StringUtils.isEmpty(fileLink) ? "#" : fileLink;

        String chatText = "<div style='color: #409EFF; padding: 3px;'>" +
                "<i class='" + icon + " aip-icon-word'></i>" +
                escapedFileName +
                " </div>" +
                " 已生成文件成功.<a href='"+fileLink+"'>打开</a>";

        log.debug("generatorUploadResponse:{}", chatText);

        return chatText;
    }

}
