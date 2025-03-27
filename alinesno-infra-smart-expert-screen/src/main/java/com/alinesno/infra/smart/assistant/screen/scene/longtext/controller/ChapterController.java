package com.alinesno.infra.smart.assistant.screen.scene.longtext.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.screen.core.dto.*;
import com.alinesno.infra.smart.assistant.screen.core.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.screen.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/infra/smart/assistant/screenChapter")
public class ChapterController extends BaseController<ChapterEntity, IChapterService> {

    @Autowired
    private IChapterService service;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
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
     * 接收JSON数据并保存到数据库
     * @param nodes JSON格式的章节列表
     * @return 操作结果
     */
    @PostMapping("/saveChapters")
    public AjaxResult saveChapters(@RequestBody List<TreeNodeDto> nodes, @RequestParam("screenId") long screenId) {

        if(nodes == null || nodes.isEmpty()){
            return error("参数错误") ;
        }

        service.saveChaptersWithHierarchy(nodes, null, 1 , screenId);
        return ok() ;
    }

    /**
     * 更新用户编辑章节，即每个章节需要指定编辑人员
     */
    @PostMapping("/updateChapterContentEditor")
    public AjaxResult updateChapterContentEditor(@RequestBody @Validated ChatContentEditDto dto) {
        service.updateChapterEditor(dto);
        return ok() ;
    }

    /**
     * 生成章节内容
     */
    @SneakyThrows
    @PostMapping("/chatRoleSync")
    public AjaxResult chatRoleSync(@RequestBody @Validated ChapterGenFormDto dto) {

        log.debug("dto = {}", dto);

        long chapterId = dto.getChapterId() ;
        ChapterEntity chapterEntity = service.getById(chapterId) ;
        Long roleId = chapterEntity.getChapterEditor() ;

        if(roleId != null){
            MessageTaskInfo taskInfo = new MessageTaskInfo() ;

            taskInfo.setRoleId(roleId);
            taskInfo.setChannelId(dto.getScreenId());
            taskInfo.setScreenId(dto.getScreenId());
            taskInfo.setText("章节标题:"+dto.getChapterTitle() + ",要求:" + dto.getChapterDescription());

            WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
            log.debug("chatRole = {}" , genContent);

            // 更新章节内容
            chapterEntity.setContent(genContent.getGenContent());
            service.update(chapterEntity);

            return AjaxResult.success("生成成功",genContent.getGenContent()) ;
        }

        return AjaxResult.success("操作成功" , "此章节未指定编辑人员");
    }

    /**
     * 通过ID查询章节内容
     */
    @GetMapping("/getChapterContent")
    public AjaxResult getChapterContent(@RequestParam("chapterId") long chapterId) {
        ChapterEntity chapterEntity = service.getById(chapterId) ;
        return AjaxResult.success("操作成功" , chapterEntity.getContent()) ;
    }

    /**
     * 更新章节内容
     */
    @PostMapping("/updateChapterContent")
    public AjaxResult updateChapterContent(@RequestBody @Validated ChatContentDto dto) {

        ChapterEntity chapterEntity = service.getById(dto.getId()) ;
        chapterEntity.setContent(dto.getContent());
        service.update(chapterEntity);

        return AjaxResult.success("操作成功") ;
    }

    /**
     * 对文章某段的处理，包括扩写、重写、润色。
     */
    @PostMapping("/processParagraph")
    public AjaxResult processParagraph(@RequestBody @Validated ParagraphProcessRequestDTO dto) {
        log.debug("ParagraphProcessRequestDTO = {}" , dto) ;

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(dto.getRoleId());
        taskInfo.setScreenId(dto.getScreenId());
        taskInfo.setText(dto.getAction() +":"+dto.getRequirement()+ ":"+ dto.getModifyContent());
        taskInfo.setModify(true);
        taskInfo.setModifyPreBusinessId(false); ;

        Map<String , Object> paramMap = new java.util.HashMap<>(Map.of("modifyContent", dto.getModifyContent()));
        paramMap.put("action" , dto.getAction()) ;
        paramMap.put("requirement" , dto.getRequirement()) ;

        taskInfo.setParams(paramMap);

        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("chatRole = {}" , genContent);

        return AjaxResult.success("操作成功" , genContent.getGenContent()) ;
    }

    /**
     * 与角色交互，获取到内容结果
     */
    @SneakyThrows
    @PostMapping("/chatRole")
    public AjaxResult chatRole(@RequestBody @Validated ChatRoleDto chatRole) {

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(chatRole.getRoleId());
        taskInfo.setChannelId(chatRole.getScreenId());
        taskInfo.setScreenId(chatRole.getScreenId());
        taskInfo.setText(chatRole.getMessage());

        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("chatRole = {}" , chatRole);

        // 解析得到代码内容
        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
            String codeContent = genContent.getCodeContent().get(0).getContent() ;
            JSONArray dataObject = JSONArray.parseArray(codeContent) ;

            // 验证是否可以正常解析json
            try{
                List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
                log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(nodeDtos));
            }catch (Exception e){
                throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成.") ;
            }

            return AjaxResult.success("操作成功" , dataObject) ;
        }

        return AjaxResult.success("操作成功" , genContent) ;
    }

    /**
     * 获取章节的树结构
     * @return 章节的树结构
     */
    @SneakyThrows
    @GetMapping("/getChapterTree")
    public AjaxResult getChapterTree(@RequestParam("screenId") long screenId) {
        List<TreeNodeDto> tree = service.getChapterTree(screenId);
        return AjaxResult.success(tree);

    }

    /**
     * 获取到角色的编辑章节
     * @return
     */
    @GetMapping("/getChapterByRole")
    public AjaxResult getChapterByRole(@RequestParam("roleId") long roleId ,
                                       @RequestParam("screenId") long screenId) {

        LambdaQueryWrapper<ChapterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterEntity::getScreenId, screenId);
        wrapper.eq(ChapterEntity::getChapterEditor, roleId);

        List<ChapterEntity> allChapters = service.list(wrapper);

        return AjaxResult.success("操作成功." , allChapters) ;
    }

    @Override
    public IChapterService getFeign() {
        return this.service;
    }
}