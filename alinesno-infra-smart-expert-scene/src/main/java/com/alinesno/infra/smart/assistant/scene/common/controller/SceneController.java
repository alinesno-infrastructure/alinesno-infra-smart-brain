package com.alinesno.infra.smart.assistant.scene.common.controller;

import cn.hutool.core.io.FileTypeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.ModelDataScopeOptions;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.enums.FrequentTypeEnums;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.im.service.IAgentStoreService;
import com.alinesno.infra.smart.im.service.IFrequentAgentService;
import com.alinesno.infra.smart.scene.dto.*;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene")
public class SceneController extends BaseController<SceneEntity, ISceneService> {

    @Autowired
    private BaseSearchConsumer searchController ;

    @Autowired
    private IFrequentAgentService frequentAgentService ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private ISceneService service;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private IChapterService chapterService;

    @Autowired
    private IAgentSceneService agentSceneService ;

    @Autowired
    private IAgentStoreService agentStoreService ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 通过场景id和角色类型获取到角色列表
     */
    @GetMapping("/getRoleBySceneIdAndAgentType")
    public AjaxResult getRoleBySceneIdAndAgentType(@RequestParam("sceneId") long sceneId,
                                                   @RequestParam("agentTypeId") long agentTypeId) {
        log.debug("sceneId = {} , agentType = {}" , sceneId , agentTypeId);

        long orgId = CurrentAccountJwt.get().getOrgId() ;
        List<IndustryRoleEntity> list = agentSceneService.getRoleBySceneIdAndAgentType(sceneId, agentTypeId , orgId);

        return AjaxResult.success("操作成功", list);
    }

    /**
     * 获取到到场景列表
     */
    @GetMapping("/supportAllScene")
    public AjaxResult supportAllScene() {
        // 获取所有预定义的场景类型
        List<SceneInfoDto> allScenes = SceneEnum.getList();
       return AjaxResult.success("操作成功", allScenes);
    }

    /**
     * 获取支持的场景列表
     */
    @GetMapping("/supportScene")
    public AjaxResult supportScene() {
        // 获取所有预定义的场景类型
        List<SceneInfoDto> allScenes = SceneEnum.getList();

        LambdaQueryWrapper<SceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(List.of(SceneEntity::getSceneType))  // 使用 List 形式
                .and(qw -> qw.eq(SceneEntity::getSceneScope, ModelDataScopeOptions.PUBLIC.getValue())
                        .or()
                        .eq(SceneEntity::getSceneScope, ModelDataScopeOptions.ORG.getValue())
                        .eq(SceneEntity::getOrgId, CurrentAccountJwt.get().getOrgId()))
                .groupBy(SceneEntity::getSceneType);

        List<SceneEntity> orgSceneTypes = service.list(wrapper);

        // 提取出实际存在的场景类型值
        Set<String> existingTypes = new HashSet<>();
        for (SceneEntity scene : orgSceneTypes) {
            existingTypes.add(scene.getSceneType());
        }

        // 过滤出同时存在于预定义列表和实际数据库中的场景类型
        List<SceneInfoDto> supportedScenes = new ArrayList<>();
        for (SceneInfoDto scene : allScenes) {
            if (existingTypes.contains(scene.getCode())) {
                supportedScenes.add(scene);
            }
        }

        return AjaxResult.success("操作成功", supportedScenes);
    }

    /**
     * 获取到场景数据范围
     */
    @GetMapping("/getSceneScope")
    public AjaxResult getSceneScope() {
        return AjaxResult.success("操作成功", SceneScopeType.getAllSceneTypes());
    }


    /**
     * 保存或更新屏幕场景
     *
     * @param sceneDto 屏幕场景的DTO
     * @return 操作结果
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdate")
    public AjaxResult saveOrUpdate(@RequestBody @Validated SceneDto sceneDto) {
        log.debug("sceneDto = {}", ToStringBuilder.reflectionToString(sceneDto));

        // 判断SceneType是否符合，不符合则报异常
        Assert.notNull(sceneDto.getSceneType(), "场景类型不能为空");
        Assert.isTrue(SceneEnum.hasCode(sceneDto.getSceneType())  , "场景类型参数不正确");

        SceneEntity sceneEntity =service.saveScene(sceneDto);
        return AjaxResult.success("操作成功.", sceneEntity.getId());
    }

    /**
     * 保存场景
     */
    @DataPermissionSave
    @PostMapping("/createScene")
    public AjaxResult createScene(@RequestBody @Validated SceneDto sceneDto) {
        return this.saveOrUpdate(sceneDto);
    }

    /**
     * 获取场景列表sceneList
     */
    @DataPermissionQuery
    @GetMapping("/sceneList")
    public AjaxResult sceneList(PermissionQuery query) {

        LambdaQueryWrapper<SceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(SceneEntity.class) ;
        query.toWrapper(queryWrapper);
        queryWrapper.orderByDesc(SceneEntity::getAddTime);

        List<SceneEntity> list = service.list(queryWrapper);

        // 调整类型标识
        if(!list.isEmpty()){
            list.forEach(e -> {
                e.setFieldProp(
                        SceneEnum.getByCode(e.getSceneType()).isPresent() ?
                                JSON.toJSONString(SceneEnum.getByCode(e.getSceneType()).get().getSceneInfo()):null
                );
            });
        }

        return AjaxResult.success("操作成功", list);
    }

    /**
     * 获取公共场景列表
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/allPublicScene")
    public AjaxResult allPublicScene(PermissionQuery query) {
        List<SceneEntity> list = service.allPublicScene() ;
        return AjaxResult.success("操作成功", list);
    }

    /**
     * listAllScene 获取场景列表
     */
    @DataPermissionQuery
    @GetMapping("/listAllScene")
    public AjaxResult listAllScene(PermissionQuery query) {

        SceneQueryDto page = new SceneQueryDto() ;
        page.setPageSize(1000);
        page.setPageNum(0);

        List<SceneResponseDto> list = service.sceneListByPage(query , page).getRecords() ;
        return AjaxResult.success("操作成功", list);
    }

    /**
     * 获取场景列表sceneList
     */
    @DataPermissionQuery
    @GetMapping("/sceneListByPage")
    public AjaxResult sceneListByPage(SceneQueryDto page , PermissionQuery query) {

        List<SceneResponseDto> list = service.sceneListByPage(query , page).getRecords() ;

        // 调整类型标识
        if(!list.isEmpty()){
            list.forEach(e -> {
                e.setFieldProp(
                        SceneEnum.getByCode(e.getSceneType()).isPresent() ?
                                JSON.toJSONString(SceneEnum.getByCode(e.getSceneType()).get().getSceneInfo()):null
                );
            });
        }

        return AjaxResult.success("操作成功", list);
    }

    /**
     * 更新场景Leader角色
     */
    @PostMapping("/updateLeaderRole")
    public AjaxResult updateLeaderRole(@RequestBody @Validated LeaderUpdateDto dto) {

        SceneEntity entity = service.getById(dto.getSceneId()) ;

        if(dto.getType().equals("leader")){

            Assert.hasLength(dto.getLeaderId(), "leaderId不能为空");
            entity.setLeaderRole(dto.getLeaderId());

            service.update(entity);

        }else if(dto.getType().equals("worker")){
            List<String> workerIds = dto.getWorkerIds() ;

            // 如果不为空，则以逗号分隔并保存
            if (!CollectionUtils.isEmpty(workerIds)) {
                entity.setWorkerRole(String.join(",", workerIds));
            }else{
                entity.setWorkerRole(null); // 清空worker
            }

            service.update(entity);
        }

        return ok() ;
    }

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @GetMapping("/getScene")
    public AjaxResult getScene(@RequestParam("id") long id) {

        Assert.isTrue(id > 0, "参数不能为空");

        SceneEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的屏幕实体");
        }

        SceneDto dto = new SceneDto();
        BeanUtils.copyProperties(entity, dto);

        if(StringUtils.isNotEmpty(entity.getGreetingQuestion())){
            try{
                dto.setGreetingQuestion(JSONArray.parseArray(entity.getGreetingQuestion(), String.class));
            }catch (Exception e){
                dto.setGreetingQuestion(new ArrayList<>());
            }
        }

        // 记录到用户常用场景里面
        if(CurrentAccountJwt.get() != null){
            frequentAgentService.addFrequentAgent(CurrentAccountJwt.getUserId(), entity.getId(), FrequentTypeEnums.SCENE) ;
        }

        return AjaxResult.success("操作成功.", dto);
    }


    /**
     * 更新场景开场白
     */
    @DataPermissionSave
    @PostMapping("/updateGreetingQuestion")
    public AjaxResult updateGreetingQuestion(@RequestBody @Validated SceneGreetingQuestionDto dto) {

        SceneEntity entity = service.getById(dto.getSceneId());
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        entity.setGreetingQuestion(JSONArray.toJSONString(dto.getGreetingQuestion()));
        service.update(entity);

        return AjaxResult.success("操作成功.");
    }


    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/sceneData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult sceneData(@RequestPart("file") MultipartFile file, long sceneId) throws Exception {

        // localPath文件夹路径是否存在，不存在则创建
        FileUtils.forceMkdir(new File(localPath)) ;

        File tmpFile = new File(localPath , Objects.requireNonNull(file.getOriginalFilename())) ;
        file.transferTo(tmpFile);

        String fileType = FileTypeUtil.getType(tmpFile) ;

        // 获取原始文件名
        String fileName = file.getOriginalFilename();

        // 上传到知识库角色
        SceneEntity sceneEntity = service.getById(sceneId) ;
        String datasetId = sceneEntity.getKnowledgeId() ;

        R<String> result = searchController.datasetUpload(tmpFile.getAbsolutePath() , datasetId , progress -> {
            log.debug("total bytes: " + progress.getTotalBytes());   // 文件大小
            log.debug("current bytes: " + progress.getCurrentBytes());   // 已上传字节数
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比

            if (progress.isDone()) {   // 是否上传完成
                log.debug("--------   Upload Completed!   --------");

                // 拼接到知识库类型，先判断之前是否有同一类型的，如果包含则不往上拼接
                String knowledgeType = sceneEntity.getKnowledgeType() ;
                if(knowledgeType == null || !knowledgeType.contains(fileType)){
                    knowledgeType = (knowledgeType==null?"":knowledgeType+ "|") + fileType ;
                    sceneEntity.setKnowledgeType(knowledgeType);
                }
                service.updateById(sceneEntity) ;
            }

        }) ;
        log.debug("upload file result = {}" , result);

        FileUtils.forceDelete(tmpFile);

        return AjaxResult.success("上传成功") ;
    }

    /**
     * 更新场景的Agents智能体工程师
     * @return
     */
    @DataPermissionSave
    @PostMapping("/updateSceneAgents")
    public AjaxResult updateSceneAgents(@Validated @RequestBody UpdateSceneAgentDto dto){
        log.debug("dto = {}" , dto);

        service.updateSceneAgents(dto);

        return AjaxResult.success();
    }

    /**
     * 获取场景的Agents智能体工程师
     * @return
     */
    @DataPermissionSave
    @PostMapping("/getRoleList")
    public AjaxResult getRoleList(@Validated @RequestBody RoleListRequestDto dto){
        log.debug("dto = {}" , dto);
        return AjaxResult.success(service.getRoleList(dto));
    }

    /**
     * 获取到系统推荐角色
     * @return
     */
    @GetMapping("/getRecommendRole")
    public AjaxResult getRecommendRole(){

        long orgId = CurrentAccountJwt.get().getOrgId() ;

        // 查询当前组织推荐角色
        IndustryRoleEntity industryRoleEntity = roleService.getRecommendRole(orgId) ;

        if(industryRoleEntity == null){  // 如果当前组织没有推荐角色，则从商店中找到公共推荐的角色
            industryRoleEntity = agentStoreService.getRecommendRole() ;
        }

        return AjaxResult.success(industryRoleEntity) ;
    }

    /**
     * 更新场景配置 updateSceneConfigData
     * @return
     */
    @DataPermissionSave
    @PostMapping("/updateSceneConfigData")
    public AjaxResult updateSceneConfigData(@Validated @RequestBody UpdateSceneConfigDto dto){

        SceneEntity entity = service.getById(dto.getSceneId());
        entity.setConfigData(dto.getConfig().toJSONString());
        service.updateById(entity);

        return AjaxResult.success();
    }

    @Override
    public ISceneService getFeign() {
        return this.service;
    }
}