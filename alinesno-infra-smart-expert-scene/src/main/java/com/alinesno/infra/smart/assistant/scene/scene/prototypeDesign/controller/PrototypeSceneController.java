package com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.dto.PrototypeChapterDto;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.prompt.PrototypePromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.service.IPrototypeSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.PrototypeSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
@RequestMapping("/api/infra/smart/assistant/scene/prototypeDesign")
public class PrototypeSceneController extends BaseController<PrototypeSceneEntity, IPrototypeSceneService> {

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private IPrototypeSceneService service;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IIndustryRoleService roleService ;

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

        PrototypeChapterDto dto = new PrototypeChapterDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        PrototypeSceneEntity examPagerSceneEntity = service.getBySceneId(id, query);
        if(examPagerSceneEntity != null){

            dto.setRequirementAnalyzer(examPagerSceneEntity.getRequirementAnalyzer());
            dto.setRequirementAnalyzerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getRequirementAnalyzer())));

            dto.setPrototypeDesigner(examPagerSceneEntity.getPrototypeDesigner());
            dto.setPrototypeDesignerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getPrototypeDesigner())));

        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 聊天提示内容
     * @param dto
     * @param query
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/chatPromptContent")
    public AjaxResult chatPromptContent(@RequestBody @Validated ArticleGeneratorDTO dto , PermissionQuery query) {

        log.debug("dto = {}" , dto);

        PrototypeSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long requirementAnalyzer = entity.getRequirementAnalyzer() ;

        MessageTaskInfo taskInfo = dto.toPowerMessageTaskInfo() ;

        // 引用附件不为空，则引入和解析附件
        if(!CollectionUtils.isEmpty(dto.getAttachments())){
            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(dto.getAttachments());
            taskInfo.setAttachments(attachmentList);
        }

        // 以下为示例模板
        String designTemplate = """
                1.作为产品经理规划这些界面
                2. 作为设计师思考这些原型界面的设计
                3. 使用 HTML 在一个界面上生成所有的原型界面，可以使用 FontAwesome 等开源图标库，让原型显得更精美和接近真实我希望这些界面是需要能直接拿去进行开发的，以下为模板
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>CloudPulse - Modern SaaS Platform</title>
                  <script src="https://cdn.tailwindcss.com"></script>
                  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" rel="stylesheet">
                  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.8/dist/chart.umd.min.js"></script>
                 \s
                  <!-- Tailwind Configuration -->
                  <script>
                    tailwind.config = {
                      theme: {
                        extend: {
                          colors: {
                            primary: '#165DFF',
                            secondary: '#36CFC9',
                            accent: '#722ED1',
                            dark: '#1D2129',
                            'dark-light': '#4E5969',
                            light: '#F2F3F5',
                            'light-dark': '#C9CDD4'
                          },
                          fontFamily: {
                            inter: ['Inter', 'sans-serif'],
                          },
                        },
                      }
                    }
                  </script>
                 \s
                  <style type="text/tailwindcss">
                    @layer utilities {
                      .content-auto {
                        content-visibility: auto;
                      }
                      .text-shadow {
                        text-shadow: 0 2px 4px rgba(0,0,0,0.1);
                      }
                      .bg-gradient-blue {
                        background: linear-gradient(135deg, #165DFF 0%, #722ED1 100%);
                      }
                      .transition-custom {
                        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                      }
                      .hover-scale {
                        transition: transform 0.3s ease;
                      }
                      .hover-scale:hover {
                        transform: scale(1.03);
                      }
                      .card-shadow {
                        box-shadow: 0 10px 30px -5px rgba(0, 0, 0, 0.1);
                      }
                      .nav-shadow {
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
                      }
                    }
                  </style>
                 \s
                  <!-- Preload Inter Font -->
                  <link rel="preconnect" href="https://fonts.googleapis.com">
                  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
                </head>
                    <body>
                    </body>
                </html>
                """;

        String promptText = PrototypePromptHandle.generatorPrompt(dto , designTemplate) ;

        taskInfo.setRoleId(requirementAnalyzer);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);

        // 优先获取到结果内容
        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;
//        log.debug("genContent = {}", genContent.getGenContent());

        return AjaxResult.success("操作成功" , taskInfo.getFullContent()) ;
    }

    @Override
    public IPrototypeSceneService getFeign() {
        return this.service;
    }
}