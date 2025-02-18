package com.alinesno.infra.base.search.memory.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alinesno.infra.base.search.memory.BaseMemoryStore;
import com.alinesno.infra.base.search.memory.bean.*;
import com.alinesno.infra.base.search.memory.service.IAgentMemoryService;
import com.alinesno.infra.base.search.memory.utils.CodeBlockParser;
import com.alinesno.infra.base.search.memory.utils.InformationParser;
import com.alinesno.infra.base.search.memory.worker.ObservationWorker;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AgentMemoryServiceImpl类
 * @author luoxiaodong
 */
@Slf4j
@Service
public class AgentMemoryServiceImpl implements IAgentMemoryService {

    // 缓存每个chatScopeId的记忆数据
    private final Map<Long, List<AgentMemoryDto>> memoryCache = new ConcurrentHashMap<>();

    // 为每个chatScopeId提供锁对象，确保线程安全
    private final ConcurrentHashMap<Long, Object> scopeLocks = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskExecutor customTaskExecutor;

    // 最大记忆数据数量，默认为5
    @Value("${agent.memory.max-size:50}")
    private int maxMemorySize;

    @Autowired
    private BaseMemoryStore baseMemoryStore ;

    @Override
    public void addBatchMemoryData(List<AgentMemoryDto> dtos , AgentMemoryDto dtoBean) {
        // 获取到对话的人物信息
        Map<Long , String> agents = new HashMap<>();

        for(AgentMemoryDto am: dtos){
            if(am.getTargetRoleId() != dtoBean.getAgentId()){
                agents.put(am.getTargetRoleId() , am.getTargetRoleName()) ;
            }
        }

        for(Long agentId : agents.keySet()){
            // List<AgentMemoryDto> dataToProcess = dtos.stream().filter(dto -> dto.getTargetRoleId() == agentId).toList();

            customTaskExecutor.execute(() -> {
                parseMemoryData(agentId, agents.get(agentId) , dtos, dtoBean);
            });
        }
    }

    @Override
    public List<MemoryNodeWithScore> queryMemoryData(String agentId, String targetId , String memoryText) {
        try {
            Long agentIdLong = Long.parseLong(agentId);
            // 使用向量搜索获取相关记忆节点，按时间倒序取前20条
            return baseMemoryStore.searchByAgentAndVector(agentIdLong, Long.valueOf(targetId), memoryText, 20);
        } catch (NumberFormatException e) {
            log.error("Invalid agentId format: {}", agentId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取指定chatScopeId的锁对象。
     * @param chatScopeId 会话范围ID
     * @return 锁对象
     */
    private Object getLockForScope(long chatScopeId) {
        return scopeLocks.computeIfAbsent(chatScopeId, k -> new Object());
    }

    /**
     * 处理并清除指定chatScopeId的缓存数据。
     * @param chatScopeId 会话范围ID
     */
    private void processAndClear(long chatScopeId, String roleName , AgentMemoryDto dto) {
        List<AgentMemoryDto> dataToProcess = memoryCache.remove(chatScopeId);
        if (dataToProcess != null && !dataToProcess.isEmpty()) {
            customTaskExecutor.execute(() -> {
                parseMemoryData(chatScopeId, roleName, dataToProcess, dto);
            });
        }
    }

    /**
     * 解析记忆数据（异步执行）。
     *
     * @param agentId 会话范围ID
     * @param roleName    角色名称
     * @param dataList    待解析的数据列表
     * @param dtoBean     传递参数数据
     */
    public void parseMemoryData(long agentId , String roleName, List<AgentMemoryDto> dataList, AgentMemoryDto dtoBean) {
        // 实现解析逻辑，例如调用外部服务或处理数据
        System.out.println("Asynchronously parsing data for agentId " + agentId + ", size: " + dataList.size());

        StringBuilder memoryData = new StringBuilder();

        for (AgentMemoryDto dto : dataList) {
            memoryData
                    .append(dto.getSourceRoleName())
                    .append("对")
                    .append(dto.getTargetRoleName())
                    .append(":")
                    .append(dto.getData())
                    .append("\r\n");
        }
        log.debug("---->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
        log.debug("解析对{}的记忆:{}\r\n", roleName, memoryData);

        ObservationWorker observationWorker = SpringUtil.getBean(ObservationWorker.class);
        String content = observationWorker.parseMemory(roleName, memoryData.toString());

        log.debug("---->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
        log.debug("解析对{}的记忆内容:{}\r\n", roleName, content);

        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(content);

        List<InformationBean> beanList = InformationParser.parseContent(codeContentList.get(0).getContent());
        for (InformationBean entryBean : beanList) {
            log.debug("解析对{}记忆内容:{}", roleName, entryBean);
        }

        if(!CollectionUtils.isEmpty(beanList)){
            List<MemoryNode> nodes = getMemoryNodes(dtoBean, beanList , agentId , roleName);
            baseMemoryStore.batchInsert(nodes);
        }
    }

    @NotNull
    private static List<MemoryNode> getMemoryNodes(AgentMemoryDto dtoBean, List<InformationBean> beanList, long agentId, String roleName) {
        List<MemoryNode> nodes = new ArrayList<>() ;

        for (InformationBean entryBean : beanList) {
            MemoryNode memoryNode = new MemoryNode();

            // 设置记忆节点信息
            memoryNode.setAgentId(dtoBean.getAgentId());
            memoryNode.setAgentName(dtoBean.getAgentName());

            memoryNode.setTargetId(String.valueOf(agentId));
            memoryNode.setTargetName(roleName);

            String keywords = entryBean.getKeywords() ;
            String contentStr = entryBean.getKeyInformation() ;

            memoryNode.setKeys(keywords);
            memoryNode.setContent(contentStr);

            nodes.add(memoryNode);
        }
        return nodes;
    }

}