package com.alinesno.infra.smart.assistant.role.service;

import com.alinesno.infra.smart.assistant.chain.IChainService;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.entity.RoleChainScriptEntity;
import com.yomahub.liteflow.core.ComponentInitializer;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.core.ScriptComponent;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Service
public class ChainServiceImpl implements IChainService {

    @Resource
    protected FlowExecutor flowExecutor;

    @Override
    public void reloadRule() {
        flowExecutor.reloadRule();
        log.debug("刷新规则数据.");
    }

    @Override
    public void checkScript(RoleChainScriptEntity entity) {

        // 处理验证脚本是否正确
        if(NodeTypeEnum.SCRIPT.getCode().equals(entity.getScriptType())){

            NodeComponent cmpInstance = null;
            try {
                cmpInstance = com.yomahub.liteflow.core.ScriptCommonComponent.class.getDeclaredConstructor().newInstance();

                NodeTypeEnum type = NodeTypeEnum.SCRIPT;
                String name = entity.getScriptName();
                String nodeId = entity.getScriptId() ;

                NodeComponent c = ComponentInitializer.loadInstance().initComponent(cmpInstance , type, name , nodeId) ;
                ((ScriptComponent)cmpInstance).loadScript(entity.getScriptData(), entity.getScriptLanguage());

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |  NoSuchMethodException e) {
                log.error("脚本异常:{}" , entity.getScriptData());

                // TODO 待处理和抛出异常脚本信息
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void executeRule(RoleChainEntity roleChain) {
        LiteflowResponse response = flowExecutor.execute2Resp(roleChain.getChainName() , "arg");
        log.debug("response = {}" , response);
    }
}
