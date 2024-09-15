package com.alinesno.infra.smart.assistant.initialize.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.PluginEntity;
import com.alinesno.infra.smart.assistant.initialize.IAssistantInitService;
import com.alinesno.infra.smart.assistant.initialize.utils.ExpertListCreator;
import com.alinesno.infra.smart.assistant.plugin.AssistantPluginProperties;
import com.alinesno.infra.smart.assistant.plugin.loader.PluginLoader;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IPluginService;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.enums.ChannelType;
import com.alinesno.infra.smart.im.service.IChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 智能助手初始化服务实现类
 */
@Slf4j
@Component
public class AssistantInitServiceImpl implements IAssistantInitService {

    @Autowired
    private AssistantPluginProperties assistantPluginProperties;

    @Autowired
    private IChannelService agentChannelService ;

    @Autowired
    private IIndustryRoleCatalogService industryRoleCatalogService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Override
    public void expertCatalog() {

        List<IndustryRoleCatalogEntity> allEntities = new ArrayList<>();

        // 创建根节点
        IndustryRoleCatalogEntity root = createExpertEntity("示例团队", "用于演示和集成示例", 0L, null);
        root.setParentId(0L);
        root.setId(9526L);

        // 技术演示团队
        IndustryRoleCatalogEntity techResearchDept = createExpertEntity("示例团队", "用于演示和集成示例，包括开发示例，单智能体和多智能体的协作", root.getId(), root);
        techResearchDept.setId(9527L);

        // AIP运营团队
        IndustryRoleCatalogEntity demandAnalysisTeam = createExpertEntity("AIP运营团队", "用于AIP产品的运营管理，包括文档、资讯、备份、管理方案等一套运营团队", root.getId(), root);
        demandAnalysisTeam.setId(9528L);

        allEntities.add(root);
        allEntities.add(techResearchDept);
        allEntities.add(demandAnalysisTeam);

        industryRoleCatalogService.saveOrUpdateBatch(allEntities) ;
    }

    public static IndustryRoleCatalogEntity createExpertEntity(String name, String desc, Long parentId, IndustryRoleCatalogEntity parent) {
        IndustryRoleCatalogEntity entity = new IndustryRoleCatalogEntity();

        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setName(name);
        entity.setDescription(desc);
        entity.setParentId(parentId);

        if (parent != null) {
            parent.getChildren().add(entity);
        }

        return entity;
    }

    @Override
    public void expert() {
        List<IndustryRoleEntity> allEntities = ExpertListCreator.createExpertList() ;
        industryRoleService.batchCreateRole(allEntities) ;
    }

    @Override
    public void initPlugin() {

        log.debug("assistantPluginProperties = {}", assistantPluginProperties) ;

        List<PluginLoader.PluginInfo> list = PluginLoader.loadPlugin(assistantPluginProperties.getCentral() , assistantPluginProperties.getPath());
        savePluginToDb(list);
    }

    // 保存插件信息到数据库
    private void savePluginToDb(List<PluginLoader.PluginInfo> pluginInfoList) {

        IPluginService service = SpringUtils.getBean(IPluginService.class) ;

        ClassLoader classLoader = getClass().getClassLoader() ;

        List<PluginEntity> pluginEntities = new ArrayList<>() ;

        AtomicLong id = new AtomicLong(1L);
        pluginInfoList.forEach(pluginInfo -> {

            File directory = new File(assistantPluginProperties.getPath());
            File[] jarFiles = directory.listFiles((dir, name) -> name.endsWith(".jar"));

            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
                    try (JarFile jf = new JarFile(jarFile)) {
                        Enumeration<JarEntry> entries = jf.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = entries.nextElement();

                            if (jarEntry.getName().endsWith(".class")) {
                                String className = jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6);
                                log.debug("--->>>> className = {}" , className);

//                                Class<?> classes = classLoader.loadClass(className) ;
//                                for (Annotation annotation : classes.getAnnotations()) {
//                                    log.debug("--->>>> annotation = {}" , annotation);
//                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            PluginEntity entity = new PluginEntity() ;

            entity.setId(id.getAndIncrement());
            entity.setAuthor(pluginInfo.getAuthor());
            entity.setDescription(pluginInfo.getDesc());
            entity.setJarName(pluginInfo.getJarName());
            entity.setLocalPath(pluginInfo.getLocalPath());
            entity.setName(pluginInfo.getName());

            pluginEntities.add(entity) ;
        });

        service.saveOrUpdateBatch(pluginEntities);
        log.debug("保存插件信息到数据库:{}" , pluginEntities.size());
    }

    @Override
    public void initChannel() {
        // 生成推荐频道列表
        List<ChannelEntity> recommendChannels = Arrays.asList(
                // 考核题目频道
                createChannel("团队考核题目", "团队人员考核、面试题目生成频道", "1808349647059738625"),

                // AIP产品运营
                createChannel("AIP产品运营", "AIP产品运营频道，包括产品文档、资讯、备份、管理方案等一套运营团队", "1808349728060137473"),

                // AIP自动化运维
                createChannel("AIP自动化运维", "AIP自动化运维频道，包括自动化测试、自动化部署、自动化监控等一套运营团队", "1808350041093627906"),

                // 论文编写频道
                createChannel("毕业论文编写", "项目团队及相关大学生毕业论文编写频道", "1808349751040729090")
        );

        log.debug("保存推荐频道信息到数据库:{}" , recommendChannels.size());

        agentChannelService.batchCreateChannel(recommendChannels) ;

        // 添加示例角色用户
        agentChannelService.jobChannel(1808349384961875969L, 1808349647059738625L);
        agentChannelService.jobChannel(1808350003370057729L, 1808349647059738625L);

        agentChannelService.jobChannel(1808349417669058562L, 1808349751040729090L);
        agentChannelService.jobChannel(1808349453119315970L, 1808349751040729090L);
        agentChannelService.jobChannel(1808350003370057729L, 1808349751040729090L);

        // 初始化AIP团队的运营频道
        agentChannelService.jobChannel(1808349647059738625L, 1808349728060137473L);

        agentChannelService.jobChannel(1808350238808924161L, 1808350041093627906L);
        agentChannelService.jobChannel(1808350041093627906L, 1808350041093627906L);
        agentChannelService.jobChannel(1808350152938938369L, 1808350041093627906L);

    }

    /**
     * 创建频道实体
     *
     * @param name        频道名称
     * @param description 描述
     * @param icon 标题
     * @return 创建的频道实体
     */
    private static ChannelEntity createChannel(String name, String description, String icon) {
        ChannelEntity channel = new ChannelEntity();

        channel.setId(Long.valueOf(icon));
        channel.setChannelName(name);
        channel.setChannelId(IdUtil.nanoId(8));
        channel.setChannelDesc(description);
        channel.setChannelType(ChannelType.PUBLIC_CHANNEL.getValue());
        channel.setIcon(icon);

        return channel;
    }

}
