package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectResearchSceneDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectManagerService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.ExamQuestionTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import io.swagger.annotations.Api;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 项目跟进场景管理
 */
@Slf4j
@Api(tags = "项目跟进场景管理")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/productResearchScene")
public class ProjectResearchSceneController extends BaseController<ProjectResearchSceneEntity, IProjectResearchSceneService> {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IProjectManagerService examProjectService ;

    @Autowired
    private IProjectResearchSceneService service ;

    @Autowired
    private IIndustryRoleService roleService ;

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getScene")
    public AjaxResult getScene(Long id , PermissionQuery query) {

        SceneEntity entity = sceneService.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        ProjectResearchSceneDTO dto = new ProjectResearchSceneDTO();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        ProjectResearchSceneEntity examProjectSceneEntity = service.getBySceneId(id, query);
        if(examProjectSceneEntity != null){

            dto.setProcessCollectorEngineer(examProjectSceneEntity.getProcessCollectorEngineer());
            dto.setProcessCollectorEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examProjectSceneEntity.getProcessCollectorEngineer())));

            dto.setProgressAnalyzerEngineer(examProjectSceneEntity.getProgressAnalyzerEngineer());
            dto.setProgressAnalyzerEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examProjectSceneEntity.getProgressAnalyzerEngineer())));

        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/uploadAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult sceneData(@RequestPart("file") MultipartFile file) throws Exception {

        // 新生成的文件名称
        String fileName = file.getOriginalFilename() ;
        String fileSuffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile) ;
        return AjaxResult.success("上传成功" , r.getData()) ;
    }

    /**
     * 获取所有题型信息的Map列表
     */
    @GetMapping("/getQuestionTypes")
    public AjaxResult getQuestionTypes() {
        List<Map<String, Object>> questionTypes = ExamQuestionTypeEnum.getAllTypesList();
        return AjaxResult.success("操作成功", questionTypes);
    }

    /**
     * 获取所有题型信息的Map列表
     */
    @GetMapping("/getQuestionCategoryList")
    public AjaxResult getQuestionCategoryList() {
        List<Map<String, Object>> questionTypes = ExamQuestionTypeEnum.getCategoryList();
        return AjaxResult.success("操作成功", questionTypes);
    }

    /**
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
        List<ProjectManagerEntity> list = examProjectService.pagerListByPage(page, query);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 获取项目详情(getProjectDetail)
     */
    @GetMapping("/getProjectDetail")
    public AjaxResult getProjectDetail(Long id) {
        ProjectManagerDTO entity = examProjectService.getProjectDetail(id);
        return AjaxResult.success("操作成功." ,entity);
    }

    /**
     * 导入项目 importGitProject
     */
    @DataPermissionSave
    @PostMapping("/importGitProject")
     public AjaxResult importGitProject(@RequestBody @Valid GitInfoDto gitInfoDto) {

        // 判断当前组织下面是否已经存在仓库
        if (examProjectService.isExistGitProject(gitInfoDto.getOrgId(), gitInfoDto.getGitUrl())) {
             return AjaxResult.error("当前组织已经存在该仓库，请勿重复导入");
        }

        // 判断当前仓库是否在导入中
         if (examProjectService.isImportingGitProject(gitInfoDto.getOrgId() , gitInfoDto.getGitUrl())) {
             return AjaxResult.error("当前仓库正在导入中，请勿重复导入");
         }

        ProjectManagerDTO entity = examProjectService.importGitProject(gitInfoDto);
        return AjaxResult.success("操作成功." ,entity);
    }

}