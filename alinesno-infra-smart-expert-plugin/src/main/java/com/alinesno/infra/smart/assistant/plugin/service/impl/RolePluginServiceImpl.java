package com.alinesno.infra.smart.assistant.plugin.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.AccountRolePluginEntity;
import com.alinesno.infra.smart.assistant.entity.RolePluginEntity;
import com.alinesno.infra.smart.assistant.plugin.AssistantPluginProperties;
import com.alinesno.infra.smart.assistant.plugin.mapper.RolePluginMapper;
import com.alinesno.infra.smart.assistant.plugin.service.IAccountRolePluginService;
import com.alinesno.infra.smart.assistant.plugin.service.IRolePluginService;
import com.alinesno.infra.smart.assistant.plugin.loader.PluginLoader;
import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RolePluginServiceImpl extends IBaseServiceImpl<RolePluginEntity , RolePluginMapper> implements IRolePluginService {

    @Autowired
    private IAccountRolePluginService accountRolePluginService ;

    @Autowired
    private AssistantPluginProperties pluginProperties ;

//    @Autowired
//    private PluginImportBeanDefinitionRegistrar registrar ;

    @SneakyThrows
    @Override
    public void installPlugin(long accountId, long pluginId) {

        RolePluginEntity pluginEntity = getById(pluginId) ;

        log.debug("pluginProperties = {} , pluginEntity = {}" , pluginProperties , pluginEntity);

        String pluginUrl = pluginProperties.getCentral() + pluginEntity.getJarName();
        String localPath = pluginProperties.getPath() + "/" + pluginEntity.getJarName() ;

        // 1. 先下载插件到本地
        PluginLoader.downloadPlugin(pluginUrl , localPath);

        // 2. 动态加载插件
//        registrar.loadPlugin(localPath) ;

        // 添加用户注册插件
        AccountRolePluginEntity accountRolePluginEntity = new AccountRolePluginEntity() ;
        accountRolePluginEntity.setAccountId(accountId);
        accountRolePluginEntity.setPluginId(pluginId);

        accountRolePluginService.save(accountRolePluginEntity) ;
    }

    @SneakyThrows
    @Override
    public void reloadPlugin() {
        String pluginYamlUrl = pluginProperties.getCentral() + "plugins.yaml" ;

        URL url = new URL(pluginYamlUrl);
        String pluginYaml = IOUtils.toString(url, StandardCharsets.UTF_8);

        log.debug("pluginYaml = {}" , pluginYaml);

        List<PluginLoader.PluginInfo> pluginInfo = YAMLMapper.listFromYAML(pluginYaml , PluginLoader.PluginInfo.class) ;
        List<RolePluginEntity> entities = new ArrayList<>() ;

        for(PluginLoader.PluginInfo info : pluginInfo){
            RolePluginEntity rolePluginEntity = new RolePluginEntity();
            BeanUtils.copyProperties(info, rolePluginEntity);
            entities.add(rolePluginEntity) ;
        }

        this.saveBatch(entities) ;
    }

}
