package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamScoreDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.enums.ExamineeExamEnums;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamScoreService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools.ExamPaperGenerator;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamScoreEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.annotation.Request;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * 考试试卷管理
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

    @Autowired
    private IExamScoreService examScoreService ;

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
    @PostMapping("/export/{pagerId}")
    public ResponseEntity<byte[]> exportPaperToWord(
            @PathVariable Long pagerId,
            @RequestParam(required = false, defaultValue = "false") boolean showAnswer) {

        // 1. 验证试卷是否存在
        ExamPagerEntity examPager = service.getById(pagerId);
        if (examPager == null) {
            throw new RuntimeException("试卷不存在，ID: " + pagerId);
        }

        String filePath = null;
        try {
            filePath = examPaperGenerator.debugGenerateToFile(pagerId, showAnswer);
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            // 使用RFC 5987标准编码文件名（支持中文和特殊字符）
            String fileName = examPager.getTitle() + ".docx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 关键设置：使用RFC 5987标准
            headers.add("Content-Disposition",
                    "attachment; filename=\"" + fileName + "\";" +
                            "filename*=UTF-8''" + encodedFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IOException e) {
            log.error("导出试卷失败", e);
            throw new RuntimeException("导出试卷失败", e);
        } finally {
            // 5. 删除临时文件
            try {
                assert filePath != null;
                FileUtils.forceDeleteOnExit(new java.io.File(filePath));
            } catch (IOException e) {
                log.error("删除临时文件失败", e);
            }
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

    /**
     * 获取到考生阅卷列表，返回的是已经考试的列表
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/examineeList")
    public AjaxResult examineeList(PermissionQuery query ,
                                   @RequestParam Long examId ,
                                   @RequestParam String examStatus) {

        LambdaQueryWrapper<ExamScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamScoreEntity::getExamInfoId, examId);
        queryWrapper.eq(ExamScoreEntity::getOrgId , query.getOrgId()) ;

        switch (examStatus) {
            case "pending" ->   // 待阅卷
                    queryWrapper.notIn(ExamScoreEntity::getExamStatus, ExamineeExamEnums.REVIEW_END.getCode());   // 除了阅卷中的以外
            case "marked" ->  // 已阅卷
                    queryWrapper.in(ExamScoreEntity::getExamStatus, ExamineeExamEnums.REVIEW_END.getCode());   // 阅卷结束
            case "ranking" ->  // 成绩排行榜
                    queryWrapper.orderByDesc(ExamScoreEntity::getScore); // 通过score分数排序
        }

        // 按时间倒序
        queryWrapper.orderByDesc(ExamScoreEntity::getSubmitTime);

        List<ExamScoreEntity> examineeList = examScoreService.list(queryWrapper) ;

        List<ExamScoreDto> examineeListDto = examineeList.stream().map(examScore -> {

            ExamScoreDto dto = new ExamScoreDto();
            BeanUtils.copyProperties(examScore, dto);
            dto.setScore(examScore.getScore());

            dto.setReviewResult(JSONArray.parseArray(examScore.getReviewResult()));  // 阅卷结果
            dto.setAnswers(JSONObject.parseObject(examScore.getAnswers()));
            dto.setQuestions(JSONArray.parseArray(examScore.getQuestions()));

            return dto;
        }).toList();

        return AjaxResult.success("操作成功", examineeListDto) ;
    }

    @Override
    public IExamPagerService getFeign() {
        return this.service;
    }
}