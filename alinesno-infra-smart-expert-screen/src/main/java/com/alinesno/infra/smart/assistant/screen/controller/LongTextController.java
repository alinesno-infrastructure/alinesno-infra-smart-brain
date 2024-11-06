package com.alinesno.infra.smart.assistant.screen.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.service.IScreenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/infra/smart/assistant/screenLongtext")
public class LongTextController extends BaseController<ScreenEntity, IScreenService> {

    @Autowired
    private IScreenService service;

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

//    /**
//     * 文件上传
//     * @return
//     */
//    @DataPermissionSave
//    @SneakyThrows
//    @PostMapping("/importData")
//    public AjaxResult importData(@RequestPart("file") MultipartFile file, ScreenEntity templateEntity){
//
//        // 新生成的文件名称
//        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
//        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;
//
//        // 复制文件
//        File targetFile = new File(localPath , newFileName);
//        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
//
//        String fileType = FileTypeUtil.getType(targetFile);
//        FileTypeEnums constants = FileTypeEnums.getByValue(fileSuffix.toLowerCase()) ;
//        assert constants != null;
//
//        log.debug("fileType = {} , constants = {}" , fileType , constants);
//
//        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());
//
//        R<String> r = storageConsumer.upload(targetFile , "qiniu-kodo" , progress -> {
//            System.out.println("total bytes: " + progress.getTotalBytes());
//            System.out.println("current bytes: " + progress.getCurrentBytes());
//            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
//        }) ;
//
//        // 保存模板信息到数据库
//        templateEntity.setScreenName(file.getOriginalFilename());
//        templateEntity.setScreenDesc("模板描述");
//        templateEntity.setScreenKey(IdUtil.nanoId(8));
//        templateEntity.setCallCount(0);
//        templateEntity.setScreenParams("{}");
//        templateEntity.setStorageFileId(r.getData());
//        templateEntity.setScreenType(constants.getValue());
//
//        service.save(templateEntity);
//
//        log.debug("ajaxResult= {}" , r);
//        return AjaxResult.success("上传成功." , r.getData()) ;
//
//    }


    @Override
    public IScreenService getFeign() {
        return this.service;
    }
}