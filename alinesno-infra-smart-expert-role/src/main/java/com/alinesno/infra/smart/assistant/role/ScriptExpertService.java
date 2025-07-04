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
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.*;

/**
 * 脚本编辑方式
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_SCRIPT)
public class ScriptExpertService extends ReActExpertService {

	// 新增：Groovy脚本执行线程池 (静态共享)
	private static final ExecutorService GROOVY_SCRIPT_EXECUTOR = new ThreadPoolExecutor(
			4,  // 核心线程数
			16, // 最大线程数
			60, // 空闲线程存活时间
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(100), // 任务队列容量
			new ThreadPoolExecutor.CallerRunsPolicy() // 饱和策略
	);

	@Autowired
	private ModelAdapterLLM modelAdapterLLM ;

	@Autowired
	private ReActServiceTool reActServiceTool  ;

	@Override
	protected String handleRole(IndustryRoleEntity role,
								MessageEntity workflowExecution,
								MessageTaskInfo taskInfo) {

		String datasetKnowledgeDocument = getDatasetKnowledgeDocument(role, workflowExecution, taskInfo);

		reActServiceTool.setRole(role);
		reActServiceTool.setTaskInfo(taskInfo);

		String scriptText = role.getExecuteScript() ;

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		// 上一个任务节点不为空，执行任务并记录
		List<CodeContent> codeContentLis = null ;
		if(workflowExecution != null){
			String gentContent = workflowExecution.getContent();
			codeContentLis = CodeBlockParser.parseCodeBlocks(gentContent);
		}

		log.trace("角色脚本:{}" , scriptText);

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				codeContentLis ,
				scriptText ,
				datasetKnowledgeDocument) ;
		log.debug("handleRole output : {}", output);

		return output ;
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
	protected String handleModifyCall(IndustryRoleEntity role,
									  MessageEntity workflowExecution,
									  List<CodeContent> codeContentList,
									  MessageTaskInfo taskInfo) {

		String scriptText = role.getAuditScript() ;
		String datasetKnowledgeDocument = getDatasetKnowledgeDocument(role, workflowExecution, taskInfo);

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				codeContentList ,
				scriptText ,
				datasetKnowledgeDocument) ;
		log.debug("handleRole output : {}", output);

		return output ;
	}

	@Override
	protected String handleFunctionCall(IndustryRoleEntity role,
										MessageEntity workflowExecution,
										List<CodeContent> codeContentList,
										MessageTaskInfo taskInfo) {

		String scriptText = role.getFunctionCallbackScript() ;
		String datasetKnowledgeDocument = getDatasetKnowledgeDocument(role, workflowExecution, taskInfo);

		if(scriptText == null || scriptText.isEmpty()){
			return "scriptText is null or empty" ;
		}

		String output = executeGroovyScript(role ,
				workflowExecution ,
				taskInfo ,
				codeContentList,
				scriptText ,
				datasetKnowledgeDocument) ;
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
									   MessageEntity workflow ,
									   MessageTaskInfo taskInfo,
									   List<CodeContent> codeContentList,
									   String scriptText ,
									   String datasetKnowledgeDocument) {

		// 清空流程步骤信息
		taskInfo.setFlowStep(null);

		modelAdapterLLM.setRole(role);
		modelAdapterLLM.setTaskInfo(taskInfo);

		// TODO 待处理Groovy脚本安全的问题
		ToolsUtil tools = new ToolsUtil() ;

		// 创建 Binding 对象，用于绑定变量到 Groovy 脚本
		Binding binding = new Binding();
		binding.setVariable("role", role); // 角色信息
		binding.setVariable("taskInfo", taskInfo); // 任务信息
		binding.setVariable("workflow", workflow); // 执行流程节点
		binding.setVariable("datasetKnowledgeDocument", datasetKnowledgeDocument); // 上下文内容

		binding.setVariable("qianWenLLM", qianWenLLM); // 文本和图片生成
		binding.setVariable("modelAdapterLLM", modelAdapterLLM); // 文本和图片生成
		binding.setVariable("agentFlexLLM", agentFlexLLM); // 多模型适配生成
		binding.setVariable("qianWenAuditLLM", qianWenAuditLLM);  // 语音生成
		binding.setVariable("templateService", getTemplateService()); // 模板引擎
		binding.setVariable("expertService", this);  // 操作服务
		binding.setVariable("secretKey", getSecretKey());  // 操作服务
		binding.setVariable("channelInfo", getChannelInfo(taskInfo.getChannelId()));  // 操作服务
		binding.setVariable("tools", tools); // 工具类

		binding.setVariable("codeContent", codeContentList == null || codeContentList.isEmpty() ? null : codeContentList.get(0));  // 生成 代码

		binding.setVariable("contextMap", ContextManager.getInstance());
		binding.setVariable("log", log); // 日志输出

		// 创建可中断的Groovy执行任务
		Callable<String> groovyTask = () -> {
			GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
			try {
				return String.valueOf(shell.evaluate(scriptText));
			} catch (Exception e) {
				log.error("Groovy脚本执行异常", e);
				return "角色脚本执行失败:" + e.getMessage();
			}
		};

		// 提交任务并设置超时
		Future<String> future = GROOVY_SCRIPT_EXECUTOR.submit(groovyTask);
		try {
			return future.get(10, TimeUnit.MINUTES); // 设置10分钟超时
		} catch (TimeoutException e) {
			future.cancel(true); // 中断执行
			log.warn("Groovy脚本执行超时: role={}", role.getId());
			return "角色脚本执行超时，已中断";
		} catch (Exception e) {
			log.error("任务提交异常", e);
			return "脚本执行系统异常: " + e.getMessage();
		}
	}

}