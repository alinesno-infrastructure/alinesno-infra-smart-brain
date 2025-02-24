package com.alinesno.infra.smart.assistant.template.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.enums.RoleTypeEnums;
import com.alinesno.infra.smart.assistant.enums.ToolTypeEnums;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.assistant.template.dto.RoleTemplateDto;
import com.alinesno.infra.smart.assistant.template.dto.RoleToolInfo;
import com.alinesno.infra.smart.assistant.template.entity.RoleTemplateEntity;
import com.alinesno.infra.smart.assistant.template.mapper.RoleTemplateMapper;
import com.alinesno.infra.smart.assistant.template.service.IRoleTemplateService;
import com.alinesno.infra.smart.assistant.template.utils.RandomCharGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 项目模块 服务实现类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class RoleTemplateServiceImpl extends IBaseServiceImpl<RoleTemplateEntity, RoleTemplateMapper> implements IRoleTemplateService {

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private IToolService toolService;

//    @Autowired
//    private IRoleToolService roleToolService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    /**
     * 同步用户插件
     *
     * @param accountId 用户账号ID
     * @param gitUrl    模板仓库地址
     */
    public void syncRoleTemplate(Long accountId, String gitUrl) throws IOException {
        // 创建临时文件来存储下载的ZIP文件
        File tempZipFile = File.createTempFile("template-", ".zip");
        try {
            // 下载ZIP文件到临时文件
            FileUtils.copyURLToFile(new URL(gitUrl), tempZipFile);

            // 创建临时目录来解压缩ZIP文件
            Path tempDir = Files.createTempDirectory("template-extract-");

            List<RoleTemplateEntity> roleTemplateList = new ArrayList<>();

            try (ZipArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(tempZipFile))) {
                ZipArchiveEntry entry;
                while ((entry = zis.getNextZipEntry()) != null) {
                    if (!entry.isDirectory()) {
                        Path destPath = tempDir.resolve(entry.getName());
                        Files.createDirectories(destPath.getParent());
                        Files.copy(zis, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                File tempDirFile = tempDir.toFile();
                for (File subFile : Objects.requireNonNull(tempDirFile.listFiles())) {
                    log.debug("解析角色路径：" + subFile.getAbsolutePath());

                    String infoJsonPath = subFile.getAbsolutePath() + "/info.json";  // 角色信息

                    if (!new File(infoJsonPath).exists()) {  // 不存在则直接跳过
                        continue;
                    }


                    String perceptionGroovyPath = subFile.getAbsolutePath() + "/perception.groovy";
                    String executeGroovyPath = subFile.getAbsolutePath() + "/execute.groovy";
                    String auditGroovyPath = subFile.getAbsolutePath() + "/audit.groovy";
                    String functionGroovyPath = subFile.getAbsolutePath() + "/function.groovy";

                    RoleTemplateEntity roleTemplateEntity = new RoleTemplateEntity();
                    roleTemplateEntity.setFieldProp("platform");

                    RoleTemplateDto roleTemplateDto = JSON.parseObject(FileUtils.readFileToString(new File(infoJsonPath), "UTF-8"), RoleTemplateDto.class);

                    roleTemplateEntity.setRoleName(roleTemplateDto.getName());
                    roleTemplateEntity.setRoleScriptType(roleTemplateDto.getType());
                    roleTemplateEntity.setBackstory(roleTemplateDto.getDesc());
                    roleTemplateEntity.setGreeting(roleTemplateDto.getOpeningLine());

//                    String abilitiesStr = roleTemplateDto.getAbilities().stream()
//                            .filter(Objects::nonNull) // 可选：过滤掉null值
//                            .collect(Collectors.joining(", "));

                    roleTemplateEntity.setResponsibilities(roleTemplateDto.getDesc());

                    if (roleTemplateDto.getType() == null) {
                        log.warn("角色{}类型为空，跳过解析：{}", roleTemplateDto.getName(), subFile.getAbsolutePath());
                        continue;
                    }

                    // 角色头像处理
                    String avatarPath = subFile.getAbsolutePath() + "/avatar.jpeg";
                    File targetFile = new File(avatarPath) ;
                    R<String> r = storageConsumer.upload(targetFile) ;

                    roleTemplateEntity.setRoleAvatar(r.getData());

                    // 不同角色模板的解析
                    if (roleTemplateDto.getType().equals("Script")) {

                        String perceptionGroovy = FileUtils.readFileToString(new File(perceptionGroovyPath), "UTF-8");
                        String executeGroovy = FileUtils.readFileToString(new File(executeGroovyPath), "UTF-8");
                        String auditGroovy = FileUtils.readFileToString(new File(auditGroovyPath), "UTF-8");
                        String functionGroovy = FileUtils.readFileToString(new File(functionGroovyPath), "UTF-8");

                        roleTemplateEntity.setPerceptionScript(perceptionGroovy);
                        roleTemplateEntity.setExecuteScript(executeGroovy);
                        roleTemplateEntity.setAuditScript(auditGroovy);
                        roleTemplateEntity.setFunctionCallbackScript(functionGroovy);

                        roleTemplateEntity.setScriptType("script");

                    } else if (roleTemplateDto.getType().equals("ReAct")) {

                        String backgroundPath = subFile.getAbsolutePath() + "/background.md";
                        String toolIconPath = subFile.getAbsolutePath() + "/tool_icon.jpeg";
                        String background = FileUtils.readFileToString(new File(backgroundPath), "UTF-8");

                        File toolTargetFile = new File(toolIconPath) ;
                        R<String> toolR = storageConsumer.upload(toolTargetFile) ;

                        List<RoleToolInfo> roleTools = roleTemplateDto.getTools();
                        List<RoleToolInfo> newRoleTools = new ArrayList<>() ; // roleTemplateDto.getTools();
                        if (roleTools != null && !roleTools.isEmpty()) {

                            for (RoleToolInfo roleToolInfo : roleTools) {
                                String toolPath = subFile.getAbsolutePath() + "/tool_" + roleToolInfo.getCode() + ".groovy";
                                String toolScript =FileUtils.readFileToString(new File(toolPath), "UTF-8") ;
                                if(StringUtils.isNotEmpty(toolScript)){
                                    roleToolInfo.setScript(toolScript);
                                    roleToolInfo.setIcon(toolR.getData());
                                    newRoleTools.add(roleToolInfo);
                                }
                            }

                            roleTemplateEntity.setTools(JSON.toJSONString(newRoleTools));
                        }

                        roleTemplateEntity.setBackstory(background);
                        roleTemplateEntity.setScriptType("react");
                    }

                    roleTemplateList.add(roleTemplateEntity);

                    log.debug("解析角色：" + roleTemplateDto);

                }

            } finally {
                // 删除解压缩后的文件夹
                FileUtils.deleteDirectory(tempDir.toFile());
            }

            this.remove(new LambdaQueryWrapper<RoleTemplateEntity>().eq(RoleTemplateEntity::getFieldProp, "platform"));
            this.saveBatch(roleTemplateList);

        } finally {
            // 删除下载的ZIP文件
            if (tempZipFile.exists()) {
                if (!tempZipFile.delete()) {
                    tempZipFile.deleteOnExit(); // 如果不能立即删除，则安排在JVM退出时删除
                }
            }
        }
    }

    @Override
    public String useTemplate(RoleTemplateEntity roleTemplateEntity) {

        Long orgId = roleTemplateEntity.getOrgId();
        Long operatorId = roleTemplateEntity.getOperatorId();
        Long departmentId = roleTemplateEntity.getDepartmentId();

        roleTemplateEntity = this.getById(roleTemplateEntity.getId());
        Assert.notNull(roleTemplateEntity, "角色模板不存在");

        IndustryRoleEntity roleEntity = new IndustryRoleEntity();
        roleEntity.setRoleType(RoleTypeEnums.SCENARIO_ROLE.getKey());

        IIndustryRoleCatalogService industryRoleCatalogService = SpringUtils.getBean(IIndustryRoleCatalogService.class);
        IndustryRoleCatalogEntity entity = new IndustryRoleCatalogEntity() ;

        // TODO 待优化
        entity.setOrgId(orgId) ; // roleTemplateEntity.getOrgId());
        entity.setOperatorId(operatorId) ; // roleTemplateEntity.getOperatorId()) ;
        entity.setDepartmentId(departmentId) ; // roleTemplateEntity.getDepartmentId());

        IndustryRoleCatalogEntity defaultCatalog = industryRoleCatalogService.getDefaultCatalog(entity);

        // 保存信息的配置
        roleEntity.setOrgId(orgId) ; // roleTemplateEntity.getOrgId());
        roleEntity.setOperatorId(operatorId) ; // roleTemplateEntity.getOperatorId()) ;
        roleEntity.setDepartmentId(departmentId) ; // roleTemplateEntity.getDepartmentId());

        roleEntity.setRoleName(roleTemplateEntity.getRoleName()+ "_" + RandomCharGenerator.generateRandomString(5));
        roleEntity.setResponsibilities(roleTemplateEntity.getResponsibilities());
        roleEntity.setBackstory(roleTemplateEntity.getBackstory());
        roleEntity.setGreeting(roleTemplateEntity.getGreeting());
        roleEntity.setScriptType(roleTemplateEntity.getScriptType());
        roleEntity.setRoleAvatar(roleTemplateEntity.getRoleAvatar());
        roleEntity.setIndustryCatalog(defaultCatalog.getId());  // 添加到默认团队中

        if (roleTemplateEntity.getScriptType().equals("script")) {
            roleEntity.setChainId(AssistantConstants.PREFIX_ASSISTANT_SCRIPT);
            roleEntity.setExecuteScript(roleTemplateEntity.getExecuteScript());
            roleEntity.setAuditScript(roleTemplateEntity.getAuditScript());
            roleEntity.setFunctionCallbackScript(roleTemplateEntity.getFunctionCallbackScript());
        } else if(roleTemplateEntity.getScriptType().equals("react")){
            // 处理工具类
            roleEntity.setChainId(AssistantConstants.PREFIX_ASSISTANT_REACT);
        }

        // 保存角色信息
        industryRoleService.createRole(roleEntity);

        if (roleTemplateEntity.getScriptType().equals("react")) {
            // 添加角色工具类
            List<RoleToolInfo> roleTools = JSON.parseArray(roleTemplateEntity.getTools(), RoleToolInfo.class);

            List<ToolEntity> toolEntityList = getToolRoleTools(roleTools, roleEntity) ; // roleTemplateEntity.getOrgId());
            toolService.saveBatch(toolEntityList);
            // TODO 更新角色工具
//            roleToolService.updateRoleTools(roleEntity.getId(), toolEntityList.stream().map(ToolEntity::getId).collect(Collectors.toList()));
        }

        return String.valueOf(roleEntity.getId()) ;
    }

    @NotNull
    private static List<ToolEntity> getToolRoleTools(List<RoleToolInfo> roleTools, IndustryRoleEntity roleEntity) {
        List<ToolEntity> toolEntityList = new ArrayList<>();
        for (RoleToolInfo roleToolInfo : roleTools) {
            ToolEntity toolEntity = new ToolEntity();

            toolEntity.setIcon(roleToolInfo.getIcon());
            toolEntity.setName(roleToolInfo.getName() + "_" + roleEntity.getRoleName()); // fix:处理同名角色的问题
            toolEntity.setDescription(roleToolInfo.getDescription());
            toolEntity.setGroovyScript(roleToolInfo.getScript());
            toolEntity.setToolType(ToolTypeEnums.UTILITY_TOOLS.getKey());
            toolEntity.setToolFullName(roleToolInfo.getCode());

            toolEntity.setOrgId(roleEntity.getOrgId());
            toolEntity.setDepartmentId(roleEntity.getDepartmentId());
            toolEntity.setOperatorId(roleEntity.getOperatorId());

            String toolInfo = ToolExecutor.getToolInfo(roleToolInfo.getScript());
            toolEntity.setToolInfo(toolInfo) ;

            toolEntityList.add(toolEntity);
        }
        return toolEntityList;
    }

}
