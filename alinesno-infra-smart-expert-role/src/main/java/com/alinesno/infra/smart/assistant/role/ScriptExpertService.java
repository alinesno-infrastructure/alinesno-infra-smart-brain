package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.context.ContextManager;
import com.alinesno.infra.smart.assistant.role.tools.ToolsUtil;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_SCRIPT)
public class ScriptExpertService extends ExpertService {

	@Override
	protected String handleRole(IndustryRoleEntity role,
								WorkflowExecutionEntity workflowExecution,
								MessageTaskInfo taskInfo) {

		String scriptText = role.getExecuteScript() ;

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		log.debug(scriptText);

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				scriptText) ;
		log.debug("handleRole output : {}", output);

		return output ;
	}

	@Override
	protected String handleModifyCall(IndustryRoleEntity role,
									  WorkflowExecutionEntity workflowExecution,
									  List<CodeContent> codeContentList,
									  MessageTaskInfo taskInfo) {

		String scriptText = role.getAuditScript() ;

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				scriptText) ;
		log.debug("handleRole output : {}", output);

		return output ;
	}

	@Override
	protected String handleFunctionCall(IndustryRoleEntity role,
										WorkflowExecutionEntity workflowExecution,
										List<CodeContent> codeContentList,
										MessageTaskInfo taskInfo) {

		String scriptText = role.getFunctionCallbackScript() ;

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		// 直接返回结果执行代码和内容
		workflowExecution.setGenContent(codeContentList.get(0).getContent());

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				scriptText) ;
		log.debug("handleRole output : {}", output);

		return output ;
	}

	/**
	 * 执行groovy脚本并返回执行结果
	 *
	 * @param role
	 * @param workflow
	 * @param taskInfo
	 * @param scriptText
	 * @return
	 */
	private String executeGroovyScript(IndustryRoleEntity role,
									   WorkflowExecutionEntity workflow ,
									   MessageTaskInfo taskInfo,
									   String scriptText) {

		ToolsUtil tools = new ToolsUtil() ;

		// 创建 Binding 对象，用于绑定变量到 Groovy 脚本
		Binding binding = new Binding();
		binding.setVariable("role", role);
		binding.setVariable("taskInfo", taskInfo);
		binding.setVariable("workflow", workflow);
		binding.setVariable("qianWenLLM", qianWenLLM);
		binding.setVariable("qianWenAuditLLM", qianWenAuditLLM);
		binding.setVariable("expertService", this);
		binding.setVariable("tools", tools);
		binding.setVariable("contextMap", ContextManager.getInstance());
		binding.setVariable("log", log);

		// 创建 GroovyShell 实例
		GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);

		// 执行 Groovy 脚本
		return String.valueOf(shell.evaluate(scriptText)) ;
	}
}