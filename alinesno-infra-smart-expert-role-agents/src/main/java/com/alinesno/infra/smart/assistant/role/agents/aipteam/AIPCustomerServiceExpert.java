package com.alinesno.infra.smart.assistant.role.agents.aipteam;

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

        String message = taskInfo.getText() ;
        String promptContent = role.getPromptContent() ;

        // TODO 待查询知识库
        String promptDoc = "软件智能体平台（以下简称新基设)，全称alinesno-infrastructure-platform（简称AIP)，通过建设软件智能体平台，推动业务自动化转型和创新的发展，提高竞争力、降低成本、支持创新和业务拓展，以及提升团队协作效率\n" +
                "\n" +
                "什么叫智能体平台\n" +
                "软件智能体平台是指针对现代软件开发和运行需求而建设的智能体平台系统，包括开发工具、测试环境、运行平台和协作工具等。\n" +
                "\n" +
                "它以技术创新和信息网络为基础，旨在支持软件行业的数字转型、智能升级和融合创新。软件智能体平台的建设涉及多个领域，包括云计算、基设架构、容器化、持续集成和部署、大数据处理、人工智能等。" ;

        List<PromptMessage> promptMessages = parseMessage(promptContent , message , promptDoc) ;
        String gentContent = qianWenLLM.processFile(promptMessages) ;

        log.debug("result:{}", gentContent);

        return gentContent ;
    }

    protected String handleFunctionCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecutionEntity,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        String link = "https://www.kdocs.cn/l/cqZbwSTUGZNz";
        String fileName = "AIP智能体平台产品手册_V1.0" ;

        return generatorFileResponse("pptx", fileName , link) ;
    }

}
