package com.alinesno.infra.smart.assistant.role.tools;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.ParserDataBean;
import com.alinesno.infra.smart.assistant.role.utils.AttachmentReaderUtils;
import com.alinesno.infra.smart.assistant.role.utils.OutsideDatasetUtil;
import com.alinesno.infra.smart.assistant.role.utils.ParserDataUtils;
import com.alinesno.infra.smart.assistant.role.utils.TokenUtils;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageReferenceDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.enums.FileType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Scope("prototype")
@Component
public class ReActServiceTool {

    @Autowired
    private AttachmentReaderUtils attachmentReaderUtils; ;

    @Autowired
    private OutsideDatasetUtil outsideDatasetUtil ;

    // 设置当前执行任务的信息
    private IndustryRoleEntity role ;

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    private MessageTaskInfo taskInfo ;

    /**
     * 处理并合并文档内容
     *
     * @param goal
     * @param datasetKnowledgeDocumentList
     * @param workflowMessage
     * @param taskInfo
     * @param historyPrompt
     * @param oneChatId
     * @param datasetMap
     * @return
     */
    public String handleDocumentContent(String goal,
                                         List<DocumentVectorBean> datasetKnowledgeDocumentList,
                                         MessageEntity workflowMessage,
                                         MessageTaskInfo taskInfo,
                                         HistoriesPrompt historyPrompt,
                                         String oneChatId,
                                         List<ParserDataBean> datasetMap) {

        StringBuilder sb = new StringBuilder();

        // 如果上一个节点有内容，则自动的获取到上一个节点的结果做为知识库内容的一部分
        if(workflowMessage != null && StringUtils.hasLength(workflowMessage.getContent())){
            sb.append(String.format(AgentConstants.Slices.PRE_CONTENT_WITH_HEADER  , taskInfo.getPreBusinessId()));
            sb.append(workflowMessage.getContent()).append("\n");

            datasetMap.add(new ParserDataBean(
                    String.format(AgentConstants.Slices.PRE_CONTENT , taskInfo.getPreBusinessId()) ,
                    datasetMap.size() + 1 ,
                    FileType.MESSAGE.getCode() ,
                    workflowMessage.getContent())) ;
        }

        // 添加附件解析 attachments(比如文件或者图片之类的)
        if(!CollectionUtils.isEmpty(taskInfo.getAttachments())){
            IndustryRoleDto industryRoleDto = IndustryRoleDto.fromEntity(taskInfo.getRoleDto()) ;

            if (industryRoleDto.isUploadStatus()) {
                UploadData uploadData = industryRoleDto.getUploadData();
                int maxTokenLength = uploadData.getMaxLength();

                List<FileAttachmentDto> newAttachments = attachmentReaderUtils.readAttachmentList(
                        taskInfo.getAttachments(),
                        industryRoleDto.getUploadData(),
                        taskInfo,
                        taskInfo.getRoleDto(),
                        oneChatId);

                StringBuilder treatmentSb = new StringBuilder();
                int currentTokenCount = 0;

                for (FileAttachmentDto fileAttachmentDto : newAttachments) {
                    String processChatId = IdUtil.getSnowflakeNextIdStr();

                    sb.append(AgentConstants.Slices.REFERENCE_CONTENT);

                    // 构建元信息字符串并计算token
                    String metaInfo = String.format("文件名称:%s\n文件类型:%s\n文件大小:%s\n",
                            fileAttachmentDto.getFileName(),
                            fileAttachmentDto.getFileType(),
                            fileAttachmentDto.getLength());
                    int metaTokenCount = TokenUtils.estimateTokenCount(metaInfo);

                    // 检查当前token是否已超过maxTokenLength
                    if (currentTokenCount + metaTokenCount >= maxTokenLength) {
                        log.info("文件[{}]因内容token长度已达限制(maxTokenLength={})，跳过内容解析",  fileAttachmentDto.getFileName(), maxTokenLength);
                        eventStepMessage("文件[" + fileAttachmentDto.getFileName() + "]因元信息token长度已达限制，跳过内容解析",  AgentConstants.STEP_FINISH, processChatId, taskInfo);
                        continue;
                    }

                    treatmentSb.append(metaInfo);
                    currentTokenCount += metaTokenCount;

                    // 处理文件内容
                    String fileContent = fileAttachmentDto.getFileContent();
                    int contentTokenCount = TokenUtils.estimateTokenCount(fileContent);

                    if (currentTokenCount + contentTokenCount > maxTokenLength) {
                        // 计算还能添加多少token
                        int remainingTokens = maxTokenLength - currentTokenCount;
                        if (remainingTokens > 0) {
                            // 根据token数量截取内容
                            fileContent = TokenUtils.truncateByTokenCount(fileContent, remainingTokens);
                            treatmentSb.append("文件内容:").append(fileContent).append("...[内容已截断]\n");
                            eventStepMessage("文件[" + fileAttachmentDto.getFileName() + "]因token长度已达限制，内容已截断", AgentConstants.STEP_FINISH, processChatId, taskInfo);
                        } else {
                            log.info("文件[{}]因元信息token长度已达限制(maxTokenLength={})，跳过内容添加",  fileAttachmentDto.getFileName(), maxTokenLength);
                            eventStepMessage("文件[" + fileAttachmentDto.getFileName() + "]因元信息token长度已达限制，跳过内容添加",  AgentConstants.STEP_FINISH, processChatId, taskInfo);
                            continue;
                        }
                    } else {
                        treatmentSb.append("文件内容:").append(fileContent).append("\n");
                    }

                    // 更新当前token计数
                    currentTokenCount += TokenUtils.estimateTokenCount(fileContent);

                    // 将附件内容同步放到用户消息历史中
                    historyPrompt.addMessage(new HumanMessage(treatmentSb.toString()));
                    datasetMap.add(new ParserDataBean(
                            fileAttachmentDto.getFileName(),
                            datasetMap.size() + 1 ,
                            FileType.inferFromFileName(fileAttachmentDto.getFileType()).getCode() ,
                            fileContent
                            ));
                }

                sb.append(treatmentSb);
            }
        }

        // 添加知识库内容
        if(!CollectionUtils.isEmpty(datasetKnowledgeDocumentList)){
            sb.append(AgentConstants.Slices.DATASET_CONTENT) ;
            for(DocumentVectorBean bean : datasetKnowledgeDocumentList){
                sb.append(bean.getDocument_content()).append("\n");

                datasetMap.add(new ParserDataBean(
                        bean.getDocument_title() + "#" +  bean.getId() ,
                        datasetMap.size() + 1 ,
                        FileType.DOCX.getCode() ,
                        bean.getDocument_content()
                )) ;
            }
        }

        return sb.toString() ;
    }

    /**
     * 处理知识库引用的问题
     * @param taskInfo
     * @param datasetKnowledgeDocument
     */
    public void handleReferenceArticle(MessageTaskInfo taskInfo, List<DocumentVectorBean> datasetKnowledgeDocument) {
        if(datasetKnowledgeDocument != null && !datasetKnowledgeDocument.isEmpty()){
            List<MessageReferenceDto> contentReferenceArticle = new ArrayList<>();

            for (DocumentVectorBean documentVectorBean : datasetKnowledgeDocument) {
                MessageReferenceDto messageReferenceDto = new MessageReferenceDto();

                messageReferenceDto.setId(documentVectorBean.getId()+"");
                messageReferenceDto.setDocumentName(documentVectorBean.getDocument_title());
                messageReferenceDto.setDocumentUrl(documentVectorBean.getSourceUrl());
                contentReferenceArticle.add(messageReferenceDto) ;
            }

            taskInfo.setContentReferenceArticle(contentReferenceArticle);
        }
    }


    /**
     * 获取总token
     * @param aiMessage
     * @return
     */
    public int getTotalToken(AiMessage aiMessage) {
        try{
            return aiMessage.getTotalTokens();
        }catch(Exception e){
            return 0 ;
        }
    }

    /**
     * 流程节点消息
     * @param stepMessage
     * @param status
     */
    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo , String stepContent) {

        // stepMessage的最大字符长度不能超过50个字，超过则截取并显示省略号
        if(stepMessage.length() > 50){
            stepMessage = stepMessage.substring(0, 50) + "...";
        }

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage(stepMessage) ;
        stepDto.setStepId(stepId) ;
        stepDto.setStatus(status);
        stepDto.setPrint(false);

        if(StringUtils.hasLength(stepContent)){
            // 如果stepContent的内容超过2048个字符，则进行截取
            if(stepContent.length() > 2048){
                stepContent = stepContent.substring(0, 2048);
            }
            stepDto.setFlowChatText(stepContent);
        }

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                taskInfo.getRoleDto(),
                taskInfo,
                taskInfo.getTraceBusId()
        );

    }

    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo) {
        eventStepMessage(stepMessage, status, stepId, taskInfo, null);
    }

    /**
     * 获取知识库
     *
     * @param goal
     * @param workflowMessage
     * @param taskInfo
     * @param datasetKnowledgeDocumentList
     * @param oneChatId
     * @param historyPrompt
     * @return
     */
    @NotNull
    public String getDatasetKnowledgeDocument(String goal,
                                                 MessageEntity workflowMessage,
                                                 MessageTaskInfo taskInfo,
                                                 List<DocumentVectorBean> datasetKnowledgeDocumentList,
                                                 String oneChatId,
                                                 HistoriesPrompt historyPrompt) {

        String datasetKnowledgeDocument = "" ;

        // 解析前端自带知识库内容
        if(StringUtils.hasLength(taskInfo.getCollectionIndexName())){

            String preKnowledgeProcess = "解析前端["+taskInfo.getCollectionIndexLabel()+"]知识库" ;
            this.eventStepMessage(preKnowledgeProcess, AgentConstants.STEP_START , oneChatId, taskInfo) ;

            List<ParserDataBean> indexDatasetMap = new ArrayList<>();

            datasetKnowledgeDocument = outsideDatasetUtil.search(taskInfo.getCollectionIndexName() , goal , indexDatasetMap) ;
            taskInfo.setDatasetMap(indexDatasetMap);

            this.eventStepMessage(preKnowledgeProcess , AgentConstants.STEP_FINISH, oneChatId, taskInfo, ParserDataUtils.generateParsedItemsHTML(indexDatasetMap)) ;
        }

        if(!CollectionUtils.isEmpty(datasetKnowledgeDocumentList) || !CollectionUtils.isEmpty(taskInfo.getAttachments()) || workflowMessage != null){

            String preKnowledgeProcess = "解析知识库" + (taskInfo.getAttachments() == null ? "" : "和" + taskInfo.getAttachments().size() + "个附件")  ;

            if(workflowMessage != null){
                preKnowledgeProcess += "和" + workflowMessage.getId() + "消息" ;
            }

            eventStepMessage(preKnowledgeProcess, AgentConstants.STEP_START , oneChatId, taskInfo) ;

            List<ParserDataBean> datasetMap = new ArrayList<>();

            datasetKnowledgeDocument = handleDocumentContent(goal ,
                    datasetKnowledgeDocumentList,
                    workflowMessage,
                    taskInfo ,
                    historyPrompt ,
                    oneChatId ,
                    datasetMap) ;
            taskInfo.setDatasetMap(datasetMap);

            eventStepMessage(preKnowledgeProcess , AgentConstants.STEP_FINISH, oneChatId, taskInfo, ParserDataUtils.generateParsedItemsHTML(datasetMap)) ;
        }

        // 设置消息引用为空（fix:规避重复生成引用的问题)
        taskInfo.setFlowStep(null);

        return datasetKnowledgeDocument;
    }

}
