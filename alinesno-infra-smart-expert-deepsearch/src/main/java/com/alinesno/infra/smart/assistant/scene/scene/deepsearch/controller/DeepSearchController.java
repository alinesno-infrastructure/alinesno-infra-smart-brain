package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.GeneratorTaskNameUtil;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchSceneDto;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchSceneRequestDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.DeepSearchSceneEntity;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * 深度搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/deepSearch/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class DeepSearchController extends BaseController<DeepSearchSceneEntity, IDeepSearchSceneService> {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IDeepSearchSceneService service ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IDeepSearchTaskService taskService ;

    @Autowired
    private GeneratorTaskNameUtil generatorTaskNameUtil ;

    /**
     * 保存场景
     */
    @DataPermissionSave
    @PostMapping("/createScene")
    public AjaxResult createScene(@RequestBody @Validated SceneDto sceneDto) {
        log.debug("sceneDto = {}", ToStringBuilder.reflectionToString(sceneDto));

        // 判断SceneType是否符合，不符合则报异常
        Assert.notNull(sceneDto.getSceneType(), "场景类型不能为空");
        Assert.isTrue(SceneEnum.hasCode(sceneDto.getSceneType())  , "场景类型参数不正确");

        SceneEntity sceneEntity =sceneService.saveScene(sceneDto);
        return AjaxResult.success("操作成功.", sceneEntity.getId());
    }

    @DataPermissionQuery
    @PostMapping("/updateChapterPromptContent")
    public AjaxResult updateChapterPromptContent(@RequestBody @Validated DeepSearchSceneRequestDto dto , PermissionQuery query) {

        DeepSearchSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setPromptContent(dto.getPromptContent());

        service.updateById(sceneEntity);

        // 增加1个任务
        DeepSearchTaskEntity taskEntity = new DeepSearchTaskEntity();

        BeanUtil.copyProperties(query , taskEntity);
        taskEntity.setSceneId(dto.getSceneId());
        taskEntity.setDeepsearchSceneId(sceneEntity.getId());
        taskEntity.setGenStatus(0);
        taskEntity.setTaskStatus(TaskStatusEnum.NOT_RUN.getCode());
        taskEntity.setTaskName(dto.getPromptContent());
        taskEntity.setPromptContent(dto.getPromptContent());
        taskEntity.setChannelStreamId(dto.getChannelStreamId());
        taskEntity.setAttachments(dto.getAttachments());
        taskEntity.setSearchPlannerEngineer(sceneEntity.getSearchPlannerEngineer());

        taskService.save(taskEntity);

        // 动态生成任务名称
         generatorTaskNameUtil.generatorDeepSearchDocumentName(taskEntity , dto.getPromptContent());

        return AjaxResult.success("操作成功" , taskEntity.getId()) ;
    }

    /**
     * 通过id删除场景任务
     */

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

        DeepSearchSceneDto dto = new DeepSearchSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        DeepSearchSceneEntity deepSearchSceneEntity = service.getBySceneId(id, query);
        if(deepSearchSceneEntity != null){

            dto.setSearchPlannerEngineer(deepSearchSceneEntity.getSearchPlannerEngineer());
            dto.setSearchPlannerEngineers(RoleUtils.getEditors(roleService , String.valueOf(deepSearchSceneEntity.getSearchPlannerEngineer()))); // 查询出当前的数据分析编辑人员
        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 根据TaskID查询出DeepSearch场景
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getDeepsearchTaskById")
    public AjaxResult getDeepsearchTaskById(@RequestParam(required = true) Long taskId) {
        return AjaxResult.success(taskService.getById(taskId)) ;
    }

    /**
     * 获取存储文件预览链接
     * @return
     */
    @GetMapping("/getOutputPreviewUrl")
    public AjaxResult getStoragePreviewUrl(@RequestParam String storageId) {
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        return AjaxResult.success("操作成功" , previewUrl);
    }

    @GetMapping("/getOutputMarkdownContent")
    public AjaxResult getOutputMarkdownContent(@RequestParam String storageId) {
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(previewUrl);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 使用 IOUtils 读取内容，并指定 UTF-8 编码
                String markdownContent = IOUtils.toString(
                        response.getEntity().getContent(),
                        StandardCharsets.UTF_8
                );
                return AjaxResult.success("操作成功", markdownContent);
            } else {
                return AjaxResult.error("下载失败，HTTP状态码: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            return AjaxResult.error("请求异常: " + e.getMessage());
        } finally {
            httpGet.releaseConnection();
        }
    }

    @Override
    public IDeepSearchSceneService getFeign() {
        return this.service;
    }

}
