package com.alinesno.infra.smart.assistant.role;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.alinesno.infra.smart.im.enums.DocumentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 专家相关工具类实现
 * 提供处理文件响应等辅助功能
 */
@Slf4j
public abstract class ExpertToolsService {

    @Autowired
    protected IVectorDatasetService vectorDatasetService;

    /**
     * 创建一条消息，指定发送者角色和消息内容
     *
     * @param role 发送消息的角色，不会为null
     * @param content 消息的内容，不能为null
     * @return 返回创建的消息对象
     * @throws IllegalArgumentException 如果content为null，则抛出此异常
     */
    protected Message createMessage(Role role, String content) {
        // 检查内容是否为null，为null则抛出异常
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        // 使用Message的构建者模式创建并返回消息对象
        return Message.builder()
                .role(role.getValue())
                .content(content)
                .build();
    }

    /**
     * 创建一条用户消息，指定消息内容
     *
     * @param content 消息的内容，不能为null
     * @return 返回创建的用户消息对象
     */
    protected Message ofUser(String content) {
        // 调用createMessage方法创建用户角色的消息
        return createMessage(Role.USER, content);
    }

    /**
     * 创建一条助手消息，指定消息内容
     *
     * @param content 消息的内容，不能为null
     * @return 返回创建的助手消息对象
     */
    protected Message ofAssistant(String content) {
        // 调用createMessage方法创建助手角色的消息
        return createMessage(Role.ASSISTANT, content);
    }

    /**
     * 创建一条系统消息，指定消息内容
     * @param content
     * @return
     */
    protected Message ofSystem(String content) {
        // 调用createMessage方法创建系统角色的消息
        return createMessage(Role.SYSTEM, content);
    }


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

    /**
     * 频道知识库搜索
     */
    public String searchKnowledgeContent(String content , String datasetIds){
        List<DocumentVectorBean> result = searchChannelKnowledgeBase(content , datasetIds) ;
        StringBuilder sb = new StringBuilder();
        if(!CollectionUtils.isEmpty(result)){
            for(DocumentVectorBean bean : result){
                sb.append(bean.getDocument_content()).append("\n");
            }
        }
        return sb.toString() ;
    }



    /**
     * 频道知识库搜索
     */
    public List<DocumentVectorBean> searchChannelKnowledgeBase(String content , String datasetIds){

        // 关联多个知识库的处理
        List<Long> datasetIdArr = JSONObject.parseArray(datasetIds , Long.class) ;
        VectorSearchDto dto = new VectorSearchDto() ;

        dto.setSearchText(content) ;
        dto.setTopK(10) ;

        return vectorDatasetService.searchMultiDataset(dto ,datasetIdArr);
    }


}
