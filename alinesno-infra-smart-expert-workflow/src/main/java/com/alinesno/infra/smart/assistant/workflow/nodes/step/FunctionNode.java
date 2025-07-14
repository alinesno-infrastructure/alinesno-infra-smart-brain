// FunctionNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.context.ContextManager;
import com.alinesno.infra.smart.assistant.role.tools.ToolsUtil;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.FunctionNodeData;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 该类表示脚本功能节点，继承自 AbstractFlowNode 类。
 * 允许使用 Groovy 脚本进行编辑开发，以实现一些自定义的功能和逻辑。
 * 在工作流中，当需要执行特定的脚本逻辑时，会使用该节点。
 */
@Slf4j
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "function")
@EqualsAndHashCode(callSuper = true)
public class FunctionNode extends AbstractFlowNode {

    @Autowired
    protected IMessageService messageService;

    /**
     * 构造函数，初始化节点类型为 "function"。
     */
    public FunctionNode() {
        setType("function");
    }

    @SneakyThrows
    @Override
    protected void handleNode() {
        log.debug("node type = {} output = {}" , node.getType() , output) ;
        log.debug("FunctionNodeData = {}" , getNodeData());

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {


            // 上一个任务节点不为空，执行任务并记录
            List<CodeContent> codeContentLis = null ;
            if(workflowExecution != null){
                String gentContent = workflowExecution.getContent();
                codeContentLis = CodeBlockParser.parseCodeBlocks(gentContent);
            }

            String scriptText = getNodeData().getRawScript() ;

            String nodeOutput = executeGroovyScript(role ,
                    workflowExecution ,
                    taskInfo ,
                    codeContentLis ,
                    scriptText) ;

            log.debug("handleNode nodeOutput : {}", nodeOutput);
            eventNodeMessage(nodeOutput);

            // 生成任务结果
            return nodeOutput;
        });
        String nodeOutput =  future.get() ;

        log.debug("message = {}" , nodeOutput);
        output.put(node.getStepName()+".result" ,nodeOutput);

        if(node.isPrint() && StringUtils.isNotEmpty(nodeOutput)){  // 是否为返回内容，如果是则输出消息
            eventMessageCallbackMessage(nodeOutput);
        }

    }

    /**
     * 获取节点数据，用于执行脚本逻辑。
     * @return
     */
    private FunctionNodeData getNodeData(){
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data")) ;
        return JSONObject.parseObject(nodeDataJson , FunctionNodeData.class) ;
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
                                       String scriptText) {

        if(StringUtils.isEmpty(scriptText)){
            return "角色脚本执行失败:脚本为空" ;
        }

        // TODO 待处理Groovy脚本安全的问题

        ToolsUtil tools = new ToolsUtil() ;

        // 创建 Binding 对象，用于绑定变量到 Groovy 脚本
        Binding binding = new Binding();
        binding.setVariable("role", role); // 角色信息
        binding.setVariable("taskInfo", taskInfo); // 任务信息
        binding.setVariable("workflow", workflow); // 执行流程节点
        binding.setVariable("qianWenLLM", flowExpertService.getQianWenLLM()); // 文本和图片生成
//        binding.setVariable("agentFlexLLM", flowExpertService.getAgentFlexLLM()); // 多模型适配生成
        binding.setVariable("qianWenAuditLLM", flowExpertService.getQianWenAuditLLM());  // 语音生成
        binding.setVariable("templateService", flowExpertService.getTemplateService()); // 模板引擎
        binding.setVariable("expertService", flowExpertService);  // 操作服务
        binding.setVariable("secretKey",flowExpertService.getSecretKey());  // 操作服务
        binding.setVariable("channelInfo",flowExpertService.getChannelInfo(taskInfo.getChannelId()));  // 操作服务
//        binding.setVariable("screenInfo",flowExpertService.getScreenInfo(taskInfo.getSceneId()));  // 操作服务
        binding.setVariable("tools", tools); // 工具类

        binding.setVariable("codeContent", codeContentList == null || codeContentList.isEmpty() ? null : codeContentList.get(0));  // 生成 代码

        binding.setVariable("contextMap", ContextManager.getInstance());
        binding.setVariable("log", log); // 日志输出

        // 创建 GroovyShell 实例
        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);

        // 执行 Groovy 脚本
        try{
            return String.valueOf(shell.evaluate(scriptText)) ;
        }catch (Exception e){
            return "角色脚本执行失败:" + e.getMessage() ;
        }
    }

}