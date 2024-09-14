package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.im.enums.DocumentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 专家相关工具类实现
 * 提供处理文件响应等辅助功能
 */
@Slf4j
public abstract class ExpertToolsService {

    /**
     * 生成文件响应的HTML字符串
     *
     * @param fileType 文件类型，用于确定图标
     * @param fileName 文件名，将进行HTML转义以确保安全
     * @param fileLink 文件链接，若为空则默认为"#"（不跳转）
     * @return 返回格式化的HTML字符串，包括文件图标、文件名和下载链接
     *
     * 该方法通过文件类型获取对应的图标，对文件名进行HTML转义以防止XSS攻击，
     * 并根据文件链接是否为空，生成相应的跳转链接。最后组合成一个格式化的HTML字符串，
     * 用于展示文件生成成功的提示信息以及提供文件下载的功能。
     */
    @NotNull
    protected static String generatorFileResponse(String fileType, String fileName , String fileLink) {

        // 根据文件类型获取对应的图标
        String icon = DocumentTypeEnum.getIconByFileType(fileType);
        // 对文件名进行HTML转义，防止XSS攻击
        String escapedFileName = StringEscapeUtils.escapeHtml4(fileName);

        // 若文件链接为空，则默认为"#"
        fileLink = StringUtils.isEmpty(fileLink) ? "#" : fileLink;

        // 构造返回的HTML字符串，包括图标、文件名和跳转链接
        String chatText = "<div style='color: #409EFF; padding: 3px;'>" +
                "<i class='" + icon + " aip-icon-word'></i>" +
                escapedFileName +
                " </div>" +
                " 已生成文件成功.<a target='_blank' href='"+fileLink+"'>打开</a>";

        // 记录生成的文件响应信息
        log.debug("generatorUploadResponse:{}", chatText);

        // 返回格式化的HTML字符串
        return chatText;
    }

}
