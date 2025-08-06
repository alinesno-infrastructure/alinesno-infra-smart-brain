package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.context.ContextManager;
import com.alinesno.infra.smart.assistant.role.llm.ModelAdapterLLM;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.assistant.role.tools.ToolsUtil;
import com.alinesno.infra.smart.assistant.role.utils.FutureDebugger;
import com.alinesno.infra.smart.assistant.role.utils.GroovySandbox;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 脚本编辑方式
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_SCRIPT)
public class ScriptExpertService extends ReActExpertService {

	@Autowired
	private ModelAdapterLLM modelAdapterLLM ;

	@Autowired
	private ReActServiceTool reActServiceTool  ;


	@Value("${alinesno.infra.smart.brain.qianwen.key:}")
	private String apiKey;

	@SneakyThrows
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	protected CompletableFuture<String> handleRole(IndustryRoleEntity role,
												   MessageEntity workflowExecution,
												   MessageTaskInfo taskInfo) {

		String datasetKnowledgeDocument = getDatasetKnowledgeDocument(role, workflowExecution, taskInfo);

		reActServiceTool.setRole(role);
		reActServiceTool.setTaskInfo(taskInfo);

		String scriptText = role.getExecuteScript() ;

		if (scriptText == null || scriptText.isEmpty()) {
			return CompletableFuture.completedFuture("scriptText is null or empty");
		}

		// 上一个任务节点不为空，执行任务并记录
		List<CodeContent> codeContentLis = null ;
		if(workflowExecution != null){
			String gentContent = workflowExecution.getContent();
			codeContentLis = CodeBlockParser.parseCodeBlocks(gentContent);
		}

		log.trace("角色脚本:{}" , scriptText);

		CompletableFuture<String> future = executeGroovyScriptAsync(role ,
				workflowExecution ,
				taskInfo ,
				codeContentLis ,
				scriptText ,
				datasetKnowledgeDocument) ;
		log.debug("handleRole output : {}", future);

		return future ;
	}

	private String getDatasetKnowledgeDocument(IndustryRoleEntity role, MessageEntity workflowExecution, MessageTaskInfo taskInfo) {
		log.debug("workflowExecution = {}" , workflowExecution);
		String goal = clearMessage(taskInfo.getText()) ; // 目标
		String queryText = StringUtils.hasLength(taskInfo.getQueryText()) ? taskInfo.getQueryText() : goal ;

		HistoriesPrompt historyPrompt = new HistoriesPrompt();
		historyPrompt.setMaxAttachedMessageCount(maxHistory);
		historyPrompt.setHistoryMessageTruncateEnable(false);

		List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(queryText , role.getKnowledgeBaseIds()) ;
		reActServiceTool.handleReferenceArticle(taskInfo, datasetKnowledgeDocumentList) ;

		String oneChatId = IdUtil.getSnowflakeNextIdStr() ;
        return reActServiceTool.getDatasetKnowledgeDocument(queryText , workflowExecution, taskInfo, datasetKnowledgeDocumentList, oneChatId, historyPrompt);
	}

	@Override
	protected CompletableFuture<String> handleModifyCall(IndustryRoleEntity role,
									  MessageEntity workflowExecution,
									  List<CodeContent> codeContentList,
									  MessageTaskInfo taskInfo) {

		String scriptText = role.getAuditScript() ;
		String datasetKnowledgeDocument = getDatasetKnowledgeDocument(role, workflowExecution, taskInfo);

		if (scriptText == null || scriptText.isEmpty()) {
			return CompletableFuture.completedFuture("scriptText is null or empty");
		}

		CompletableFuture<String> output = executeGroovyScriptAsync(role ,
				workflowExecution ,
				taskInfo ,
				codeContentList ,
				scriptText ,
				datasetKnowledgeDocument) ;
		log.debug("handleRole output : {}", output);

		return output ;
	}

	@Override
	protected CompletableFuture<String> handleFunctionCall(IndustryRoleEntity role,
														   MessageEntity workflowExecution,
														   List<CodeContent> codeContentList,
														   MessageTaskInfo taskInfo) {

		String scriptText = role.getFunctionCallbackScript() ;
		String datasetKnowledgeDocument = getDatasetKnowledgeDocument(role, workflowExecution, taskInfo);

		if (scriptText == null || scriptText.isEmpty()) {
			return CompletableFuture.completedFuture("scriptText is null or empty");
		}

		CompletableFuture<String> output = executeGroovyScriptAsync(role ,
				workflowExecution ,
				taskInfo ,
				codeContentList,
				scriptText ,
				datasetKnowledgeDocument) ;
		log.debug("handleRole output : {}", output);

		return output ;
	}

	@NotNull
	private GroovyShell getGroovyShell(IndustryRoleEntity role,
									   MessageEntity workflow,
									   MessageTaskInfo taskInfo,
									   List<CodeContent> codeContentList,
									   String datasetKnowledgeDocument) {

		ToolsUtil tools = new ToolsUtil() ;

		// 创建 Binding 对象，用于绑定变量到 Groovy 脚本
		Binding binding = new Binding();
		binding.setVariable("role", role); // 角色信息
		binding.setVariable("taskInfo", taskInfo); // 任务信息
		binding.setVariable("workflow", workflow); // 执行流程节点
		binding.setVariable("datasetKnowledgeDocument", datasetKnowledgeDocument); // 上下文内容

		binding.setVariable("qianWenLLM", qianWenLLM); // 文本和图片生成
		binding.setVariable("modelAdapterLLM", modelAdapterLLM); // 文本和图片生成
		binding.setVariable("qianWenAuditLLM", qianWenAuditLLM);  // 语音生成
		binding.setVariable("templateService", getTemplateService()); // 模板引擎
		binding.setVariable("expertService", this);  // 操作服务
		binding.setVariable("secretKey", getSecretKey());  // 操作服务
		binding.setVariable("channelInfo", getChannelInfo(taskInfo.getChannelId()));  // 操作服务
		binding.setVariable("tools", tools); // 工具类
		binding.setVariable("chatThreadPool", getChatThreadPool()); // 工具类
		binding.setVariable("codeContent", codeContentList == null || codeContentList.isEmpty() ? null : codeContentList.get(0));  // 生成 代码
		binding.setVariable("contextMap", ContextManager.getInstance());
		binding.setVariable("log", log); // 日志输出
		binding.setVariable("completableFuture", CompletableFuture.class);

		CompilerConfiguration config = GroovySandbox.createSecureCompilerConfiguration();
        return new GroovyShell(binding , config);
	}

	// 将同步执行改为异步回调
	private CompletableFuture<String> executeGroovyScriptAsync(IndustryRoleEntity role,
									   MessageEntity workflow ,
									   MessageTaskInfo taskInfo,
									   List<CodeContent> codeContentList,
									   String scriptText ,
									   String datasetKnowledgeDocument) {

		GroovyShell shell = getGroovyShell(role, workflow, taskInfo, codeContentList, datasetKnowledgeDocument);

		// 执行脚本并返回Future
		Object result = shell.evaluate(scriptText);

		if (result instanceof CompletableFuture<?>) {
			@SuppressWarnings("unchecked")  // 明确告诉编译器这是安全的
			CompletableFuture<String> futureResult = (CompletableFuture<String>) result;

			FutureDebugger.printDependents(futureResult);

			return futureResult;
		}else{
			// 直接抛出异常(中文)
			throw new RuntimeException("脚本执行结果对象不兼容，请检查脚本是否正确！");
		}

	}

}