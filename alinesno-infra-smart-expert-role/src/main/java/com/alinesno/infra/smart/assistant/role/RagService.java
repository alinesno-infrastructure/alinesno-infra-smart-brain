package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * RagService 知识库角色
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_RAG)
public class RagService extends ReActExpertService {

}
