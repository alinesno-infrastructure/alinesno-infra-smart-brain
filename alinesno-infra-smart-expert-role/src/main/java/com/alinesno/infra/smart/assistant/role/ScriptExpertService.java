package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.context.ContextManager;
import com.alinesno.infra.smart.assistant.role.tools.ToolsUtil;
import com.alinesno.infra.smart.assistant.role.utils.CodeBlockParser;
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

		log.debug("workflowExecution = {}" , workflowExecution);

		String scriptText = role.getExecuteScript() ;

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		// 上一个任务节点不为空，执行任务并记录
		List<CodeContent> codeContentLis = null ;
		if(workflowExecution != null){
			String gentContent = workflowExecution.getGenContent();
			codeContentLis = CodeBlockParser.parseCodeBlocks(gentContent);
		}

		log.debug(scriptText);

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				codeContentLis ,
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
				codeContentList ,
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

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				codeContentList,
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
	 * @param codeContentList
	 * @param scriptText
	 * @return
	 */
	private String executeGroovyScript(IndustryRoleEntity role,
									   WorkflowExecutionEntity workflow ,
									   MessageTaskInfo taskInfo,
									   List<CodeContent> codeContentList,
									   String scriptText) {

		ToolsUtil tools = new ToolsUtil() ;

		// 创建 Binding 对象，用于绑定变量到 Groovy 脚本
		Binding binding = new Binding();
		binding.setVariable("role", role); // 角色信息
		binding.setVariable("taskInfo", taskInfo); // 任务信息
		binding.setVariable("workflow", workflow); // 执行流程节点
		binding.setVariable("qianWenLLM", qianWenLLM); // 文本和图片生成
		binding.setVariable("qianWenAuditLLM", qianWenAuditLLM);  // 语音生成
		binding.setVariable("templateService", getTemplateService()); // 模板引擎
		binding.setVariable("expertService", this);  // 操作服务
		binding.setVariable("channelInfo", getChannelInfo(taskInfo.getChannelId()));  // 操作服务
		binding.setVariable("tools", tools); // 工具类

		binding.setVariable("codeContent", codeContentList == null || codeContentList.isEmpty() ? null : codeContentList.get(0));  // 生成 代码

		binding.setVariable("contextMap", ContextManager.getInstance());
		binding.setVariable("log", log); // 日志输出

		// 创建 GroovyShell 实例
		GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);

		// 执行 Groovy 脚本
		return String.valueOf(shell.evaluate(scriptText)) ;
	}

}