package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools.ExamPaperGenerator;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
@RequestMapping("/api/infra/smart/assistant/scene/examPagerManager")
public class ExamPagerController extends BaseController<ExamPagerEntity, IExamPagerService> {

    @Autowired
    private IExamPagerService service;

    @Autowired
    private ExamPaperGenerator examPaperGenerator;

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
     * 导出试卷为Word文档并直接下载
     * @param pagerId 试卷ID
     * @param showAnswer 是否显示答案
     * @return ResponseEntity 包含Word文件流
     */
    @GetMapping("/export/{pagerId}")
    public ResponseEntity<byte[]> exportPaperToWord(
            @PathVariable Long pagerId,
            @RequestParam(required = false, defaultValue = "false") boolean showAnswer) {

        // 1. 验证试卷是否存在
        ExamPagerEntity examPager = service.getById(pagerId);
        if (examPager == null) {
            throw new RuntimeException("试卷不存在，ID: " + pagerId);
        }

        // 2. 生成Word文档到内存
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 这里调用之前实现的生成逻辑
            examPaperGenerator.generateWordPaper(document, examPager, showAnswer);

            // 将文档写入输出流
            document.write(outputStream);

            // 3. 准备下载响应
            String fileName = URLEncoder.encode(examPager.getTitle() + ".docx", StandardCharsets.UTF_8);

            HttpHeaders headers =  new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());

        } catch (IOException e) {
            log.error("导出试卷失败", e);
            throw new RuntimeException("导出试卷失败", e);
        }
    }

    /**
     * 导出试卷为Word文档并返回下载URL
     * @param pagerId 试卷ID
     * @param showAnswer 是否显示答案
     * @return ResponseResult 包含下载URL
     */
    @GetMapping("/export/url/{pagerId}")
    public AjaxResult exportPaperAndGetUrl(
            @PathVariable Long pagerId,
            @RequestParam(required = false, defaultValue = "false") boolean showAnswer) {

        // 这里可以实现将文件保存到服务器，然后返回下载URL
        // 实际实现需要根据您的文件存储服务调整
        String fileUrl = "/path/to/generated/files/" + pagerId + ".docx";

        return AjaxResult.success(fileUrl);
    }

    @Override
    public IExamPagerService getFeign() {
        return this.service;
    }
}