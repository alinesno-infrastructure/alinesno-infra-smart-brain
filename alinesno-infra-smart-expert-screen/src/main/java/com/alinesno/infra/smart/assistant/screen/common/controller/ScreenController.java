package com.alinesno.infra.smart.assistant.screen.common.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.screen.common.service.IScreenService;
import com.alinesno.infra.smart.assistant.screen.core.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.core.enums.ScreenTypeEnums;
import com.alinesno.infra.smart.assistant.screen.core.utils.MarkdownToWord;
import com.alinesno.infra.smart.assistant.screen.core.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.screen.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.screen.core.dto.ChapterEditorDto;
import com.alinesno.infra.smart.assistant.screen.core.dto.LeaderUpdateDto;
import com.alinesno.infra.smart.assistant.screen.core.dto.ScreenDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
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
import java.util.List;
import java.util.Objects;

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
@RequestMapping("/api/infra/smart/assistant/screen")
public class ScreenController extends BaseController<ScreenEntity, IScreenService> {

    @Autowired
    private BaseSearchConsumer searchController ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IScreenService service;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private IChapterService chapterService;

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
     * 下载文件并返回文件下载
     * @param screenId
     * @return
     */
    @GetMapping("/uploadOss")
    public AjaxResult uploadOss(@RequestParam("screenId") long screenId) {

        String markdownContent = service.genMarkdownContent(screenId) ;
        String filename = IdUtil.fastSimpleUUID() ;

        log.debug("markdownContent = {}", markdownContent);
        Assert.notNull(markdownContent, "markdownContent为空") ;

        String filePath = MarkdownToWord.convertMdToDocx(markdownContent, filename) ;
        Assert.notNull(filePath, "文件路径为空") ;

        R<String> r = storageConsumer.uploadCallbackUrl(new File(filePath), "qiniu-kodo-pub") ;
//        , progress -> {
//            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");
//        }) ;

        String downloadUrl = r.getData() ;

        return AjaxResult.success("操作成功" , downloadUrl);
    }

    /**
     * 保存或更新屏幕场景
     *
     * @param screenDto 屏幕场景的DTO
     * @return 操作结果
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdate")
    public AjaxResult saveOrUpdate(@RequestBody @Validated ScreenDto screenDto) {
        log.debug("screenDto = {}", ToStringBuilder.reflectionToString(screenDto));

        // 判断ScreenType是否符合，不符合则报异常
        Assert.notNull(screenDto.getScreenType(), "screenType不能为空");
        Assert.notNull(ScreenTypeEnums.getByKey(screenDto.getScreenType())  , "screenType参数不正确");

        ScreenEntity screenEntity =service.saveScreen(screenDto);
        return AjaxResult.success("操作成功.", screenEntity.getId());
    }

    /**
     * 获取场景列表screenList
     */
    @DataPermissionQuery
    @GetMapping("/screenList")
    public AjaxResult screenList(PermissionQuery query) {

        LambdaQueryWrapper<ScreenEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(ScreenEntity.class) ;
        query.toWrapper(queryWrapper);
        queryWrapper.orderByDesc(ScreenEntity::getAddTime);

        List<ScreenEntity> list = service.list(queryWrapper);

        // 调整类型标识
        if(!list.isEmpty()){
            list.forEach(e -> {
                e.setFieldProp(ScreenTypeEnums.getByKey(e.getScreenType()) == null ?
                        ScreenTypeEnums.LARGE_TEXT.getName() :
                        Objects.requireNonNull(ScreenTypeEnums.getByKey(e.getScreenType())).getName()); ;
            });
        }

        return AjaxResult.success("操作成功", list);
    }

    /**
     * 更新章节编辑人员
     *
     * @return
     */
    @PostMapping("/updateChapterEditor")
    public AjaxResult updateChapterEditor(@RequestBody @Validated ChapterEditorDto dto) {

        long id = dto.getId();
        String editors = dto.getEditorsAsString() ;
        String type = dto.getType();

        log.debug("id = {}, editors = {}, type = {}", id, editors, type);

        // 添加为空判断处理
        if (editors == null || editors.isEmpty()) {
            return error("参数不能为空");
        }

        // 处理type类型
        Assert.isTrue(type.equals("chapter") || type.equals("content"), "参数类型不正确");

        ScreenEntity entity = service.getById(id);

        if(type.equals("chapter")){
            entity.setChapterEditor(editors);
        }else {
            entity.setContentEditor(editors);
        }

        service.updateById(entity);
        return ok();
    }

    /**
     * 更新章节内容编辑人员
     *
     * @return
     */
    @PostMapping("/updateContentEditor")
    public AjaxResult updateContentEditor(@RequestParam("id") long id, @RequestParam("editors") String editors) {

        // 添加为空判断处理
        if (editors == null || editors.isEmpty()) {
            return error("参数不能为空");
        }

        ScreenEntity entity = service.getById(id);
        entity.setContentEditor(editors);

        service.updateById(entity);
        return ok();
    }

    /**
     * 更新场景Leader角色
     */
    @PostMapping("/updateLeaderRole")
    public AjaxResult updateLeaderRole(@RequestBody @Validated LeaderUpdateDto dto) {

        ScreenEntity entity = service.getById(dto.getScreenId()) ;

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
    @GetMapping("/getScreen")
    public AjaxResult getScreen(@RequestParam("id") long id) {

        Assert.isTrue(id > 0, "参数不能为空");

        ScreenEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的屏幕实体");
        }

        ScreenDto dto = new ScreenDto();
        BeanUtils.copyProperties(entity, dto);


        if(entity.getScreenType().equals(ScreenTypeEnums.LARGE_TEXT.getKey())){
            // 查询出当前的章节编辑人员
            dto.setChapterEditors(RoleUtils.getEditors(roleService , entity.getChapterEditor()));

            // 查询出当前的内容编辑人员
            dto.setContentEditors(RoleUtils.getEditors(roleService, entity.getContentEditor()));

            // 章节树信息
            dto.setChapterTree(chapterService.getChapterTree(entity.getId()));

        }else if(entity.getScreenType().equals(ScreenTypeEnums.LEADER_MODEL.getKey())){

            // 管理者角色
            if(entity.getLeaderRole() != null){
                IndustryRoleEntity role = roleService.getById(entity.getLeaderRole());
                IndustryRoleDto dto1 = new IndustryRoleDto();
                BeanUtils.copyProperties(role, dto1);
                dto.setLeader(dto1);
            }

            if(entity.getWorkerRole() != null && !entity.getWorkerRole().isEmpty()){
                dto.setWorkers(RoleUtils.getEditors(roleService, entity.getWorkerRole()));
            }
        } else if(entity.getScreenType().equals(ScreenTypeEnums.EXAM.getKey())){  // 考试场景

        }


        return AjaxResult.success("操作成功.", dto);
    }


    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/screenData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult screenData(@RequestPart("file") MultipartFile file, long screenId) throws Exception {

        // localPath文件夹路径是否存在，不存在则创建
        FileUtils.forceMkdir(new File(localPath)) ;

        File tmpFile = new File(localPath , Objects.requireNonNull(file.getOriginalFilename())) ;
        file.transferTo(tmpFile);

        String fileType = FileTypeUtil.getType(tmpFile) ;

        // 获取原始文件名
        String fileName = file.getOriginalFilename();

        // 上传到知识库角色
        ScreenEntity screenEntity = service.getById(screenId) ;
        String datasetId = screenEntity.getKnowledgeId() ;

        R<String> result = searchController.datasetUpload(tmpFile.getAbsolutePath() , datasetId , progress -> {
            log.debug("total bytes: " + progress.getTotalBytes());   // 文件大小
            log.debug("current bytes: " + progress.getCurrentBytes());   // 已上传字节数
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比

            if (progress.isDone()) {   // 是否上传完成
                log.debug("--------   Upload Completed!   --------");

                // 拼接到知识库类型，先判断之前是否有同一类型的，如果包含则不往上拼接
                String knowledgeType = screenEntity.getKnowledgeType() ;
                if(knowledgeType == null || !knowledgeType.contains(fileType)){
                    knowledgeType = (knowledgeType==null?"":knowledgeType+ "|") + fileType ;
                    screenEntity.setKnowledgeType(knowledgeType);
                }
                service.updateById(screenEntity) ;
            }

        }) ;
        log.debug("upload file result = {}" , result);

        FileUtils.forceDelete(tmpFile);

        return AjaxResult.success("上传成功") ;
    }


    @Override
    public IScreenService getFeign() {
        return this.service;
    }
}