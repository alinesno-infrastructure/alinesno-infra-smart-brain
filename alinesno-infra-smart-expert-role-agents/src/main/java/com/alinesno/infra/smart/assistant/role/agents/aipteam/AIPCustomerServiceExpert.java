package com.alinesno.infra.smart.assistant.role.agents.aipteam;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorDto;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AIP业务咨询专家
 */
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT + "pRFDrSJH")
public class AIPCustomerServiceExpert extends ExpertService {

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                WorkflowExecutionEntity workflowExecutionEntity,
                                MessageTaskInfo taskInfo) {

        String message = clearMessage(taskInfo.getText()) ;

//        String gentContent = qianWenLLM.processFile(promptMessages(message, role)) ;
//        log.debug("result:{}", gentContent);

        log.debug("--->>>> record.getId() = {}" , taskInfo.getWorkflowRecordId());
        processStream(role , promptMessages(message, role) , taskInfo) ;

        return "任务生成完成.";
    }

    protected String handleFunctionCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecutionEntity,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        String link = "https://www.kdocs.cn/l/cqZbwSTUGZNz";
        String fileName = "AIP智能体平台产品手册_V1.0" ;

        return generatorFileResponse("pptx", fileName , link) ;
    }

    /**
     * 获取到PromptMessage信息列表
     * @param message 用户咨询信息
     * @return
     */
    public List<PromptMessage> promptMessages(String message , IndustryRoleEntity role){

        String promptContent = role.getPromptContent() ;

        VectorSearchDto vectorSearchDto = new VectorSearchDto(Long.parseLong(role.getKnowledgeId()) , message ,2);
        R<List<DocumentVectorDto>> r = searchConsumer.datasetSearch(vectorSearchDto) ;
        log.debug("result:{}", r);

        String promptDoc;
        if(r.getData() != null && !r.getData().isEmpty()){
            promptDoc = r.getData().get(0).getDocument_content() ; // r.getData().stream().map(DocumentVectorDto::getDocument_content).reduce((a, b) -> a + "\n" + b).get();
        }else {
            promptDoc = "软件智能体平台（以下简称新基设)，全称alinesno-infrastructure-platform（简称AIP)，通过建设软件智能体平台，推动业务自动化转型和创新的发展，提高竞争力、降低成本、支持创新和业务拓展，以及提升团队协作效率\n" +
                    "\n" +
                    "什么叫智能体平台\n" +
                    "软件智能体平台是指针对现代软件开发和运行需求而建设的智能体平台系统，包括开发工具、测试环境、运行平台和协作工具等。\n" +
                    "\n" +
                    "它以技术创新和信息网络为基础，旨在支持软件行业的数字转型、智能升级和融合创新。软件智能体平台的建设涉及多个领域，包括云计算、基设架构、容器化、持续集成和部署、大数据处理、人工智能等。" ;
        }

        return parseMessage(promptContent , message , promptDoc);
    }

}
