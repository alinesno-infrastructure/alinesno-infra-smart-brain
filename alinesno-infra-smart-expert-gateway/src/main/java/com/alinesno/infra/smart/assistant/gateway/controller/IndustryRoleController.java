package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IRoleChainService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "IndustryRole")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/role")
public class IndustryRoleController extends BaseController<IndustryRoleEntity, IIndustryRoleService> {

    @Autowired
    private IIndustryRoleService service;

    @Autowired
    private IIndustryRoleCatalogService catalogService ;

    @Autowired
    private IRoleChainService roleChainService ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path}")
    private String localPath  ;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        List<ConditionDto> condition =  page.getConditionList() ;

        String catalogId =  request.getParameter("industryCatalog") ;

        if(StringUtils.isNotBlank(catalogId)){
            ConditionDto dto = new ConditionDto() ;
            dto.setColumn("industry_catalog");
            dto.setValue(catalogId);

            condition.add(dto) ;
            page.setConditionList(condition);
        }

        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 运行角色流程
     * @return
     */
    @GetMapping("/listAllRole")
    public AjaxResult listAllRole(){

        LambdaQueryWrapper<IndustryRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.orderByDesc(IndustryRoleEntity::getAddTime) ;

        List<IndustryRoleEntity> roleEntityList = service.list(wrapper) ;

        return AjaxResult.success(roleEntityList) ;
    }

    /**
     * 运行角色流程
     * @return
     */
    @GetMapping("/runRoleChainByRoleId")
    public AjaxResult runRoleChainByRoleId(long roleId , @RequestParam("text") String executeOrder){

        Assert.hasLength(executeOrder , "执行命令为空.");

        Map<String , Object> params = new HashMap<>() ;
        params.put("label1" , executeOrder) ;

        service.runRoleChainByRoleId(params , roleId , null) ;

        return ok() ;
    }

    /**
     * 获取到角色的ID
     * @param roleId
     * @return
     */
    @GetMapping("/getRoleChainByChainId")
    public AjaxResult getRoleChainByChainId(String roleId){

        IndustryRoleEntity role = service.getById(roleId) ;
        RoleChainEntity roleChain = roleChainService.getById(role.getChainId()) ;

        return AjaxResult.success(roleChain==null?new RoleChainEntity():roleChain) ;
    }

    @GetMapping("/catalogTreeSelect")
    public AjaxResult catalogTreeSelect(){
        return AjaxResult.success("success" , catalogService.selectCatalogTreeList()) ;
    }

    /**
     * 显示图片
     * @return
     */
    @GetMapping("/displayImage/{imageId}")
    public ResponseEntity<byte[]> displayImage(@PathVariable("imageId") String imageId){

        byte[] byteBody = storageConsumer.download(imageId , progress -> {
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
        }) ;

        return new ResponseEntity<>(byteBody, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * 文件上传
     * @return
     */
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, String type , String updateSupport){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        if("img".equals(type)){  // 图片上传类型
            AjaxResult ajaxResult = storageConsumer.upload(targetFile , "qiniu-kodo" , progress -> {
                System.out.println("total bytes: " + progress.getTotalBytes());
                System.out.println("current bytes: " + progress.getCurrentBytes());
                System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
            }) ;

            log.debug("ajaxResult= {}" , ajaxResult);
            return ajaxResult ;
        }

        return AjaxResult.success();
    }

    /**
     * 保存角色工作流
     * @param entity
     * @return
     */
    @PostMapping("/saveRoleChainInfo")
    public AjaxResult saveRoleChainInfo(@RequestBody RoleChainEntity entity , String roleId){
        service.saveRoleChainInfo(entity , roleId) ;
        return ok() ;
    }

    @Override
    public IIndustryRoleService getFeign() {
        return this.service;
    }
}