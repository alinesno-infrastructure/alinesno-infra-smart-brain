package com.alinesno.infra.smart.assistant.scene.common.examPaper.tools;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 试卷生成word工具类
 * ExamPaperGenerator
 */
@Slf4j
@Component
public class ExamPaperGenerator {

    @Autowired
    private IExamPagerService examPagerService;

    @Autowired
    private IExamQuestionService examQuestionService;

//    // 临时调试方法
//    public String debugGenerateToFile(Long pagerId, boolean showAnswer) {
//        ExamPagerEntity examPager = examPagerService.getById(pagerId);
//        XWPFDocument document = new XWPFDocument();
//        generateWordPaper(document, examPager, showAnswer);
//
//        // 生成保存本地的临时路径
//        String outputPath = "/tmp/debug_paper_" + IdUtil.nanoId() + ".docx";
//        log.info("调试文档已生成到: {}", outputPath);
//
//        try (FileOutputStream out = new FileOutputStream(outputPath)) {
//            document.write(out);
//            log.info("调试文档已生成到: {}" , outputPath);
//        } catch (IOException e) {
//            log.error("生成调试文档失败", e);
//        }
//        // 返回上传的路径
//        return outputPath ;
//    }

    public String debugGenerateToFile(Long pagerId, boolean showAnswer) {
        ExamPagerEntity examPager = examPagerService.getById(pagerId);
        XWPFDocument document = new XWPFDocument();
        generateWordPaper(document, examPager, showAnswer);

        try {
            // 创建临时文件
            Path tempFile = Files.createTempFile("debug_paper_", ".docx");
            String outputPath = tempFile.toString();

            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
                log.info("调试文档已生成到: {}", outputPath);
            }
            return outputPath;
        } catch (IOException e) {
            log.error("生成调试文档失败", e);
            throw new RuntimeException("生成调试文档失败", e);
        }
    }

    /**
     * 生成Word文档到给定的XWPFDocument对象
     * @param document 空白的Word文档对象
     * @param examPager 试卷实体
     * @param showAnswer 是否显示答案
     */
    public void generateWordPaper(XWPFDocument document, ExamPagerEntity examPager, boolean showAnswer) {
        try {
            // 1. 查询试卷下的所有题目
            LambdaQueryWrapper<ExamQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExamQuestionEntity::getPagerId, examPager.getId())
                    .orderByAsc(ExamQuestionEntity::getSortOrder);
            List<ExamQuestionEntity> questions = examQuestionService.list(queryWrapper);

            // 2. 填充文档内容
            // 添加试卷标题
            addTitle(document, examPager.getTitle());

            // 添加试卷描述
            if (examPager.getDescription() != null && !examPager.getDescription().isEmpty()) {
                addSubtitle(document, examPager.getDescription());
            }

            // 添加试卷基本信息
            addPaperInfo(document, examPager);

            // 添加所有题目
            for (int i = 0; i < questions.size(); i++) {
                ExamQuestionEntity question = questions.get(i);
                addQuestion(document, question, i + 1, showAnswer);
            }

        } catch (Exception e) {
            log.error("生成试卷失败", e);
            throw new RuntimeException("生成试卷失败", e);
        }
    }

    /**
     * 根据试卷ID生成Word文档
     * @param pagerId 试卷ID
     * @param outputPath 输出文件路径
     * @param showAnswer 是否显示答案
     */
    public void generateWordPaper(Long pagerId, String outputPath, boolean showAnswer) {
        // 1. 查询试卷基本信息
        ExamPagerEntity examPager = examPagerService.getById(pagerId);
        if (examPager == null) {
            throw new RuntimeException("试卷不存在，ID: " + pagerId);
        }

        // 2. 查询试卷下的所有题目
        QueryWrapper<ExamQuestionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pager_id", pagerId)
                   .orderByAsc("sort_order");
        List<ExamQuestionEntity> questions = examQuestionService.list(queryWrapper);

        // 3. 创建Word文档
        try (XWPFDocument document = new XWPFDocument()) {
            // 设置文档属性
            document.createParagraph().createRun().setText("");

            // 添加试卷标题
            addTitle(document, examPager.getTitle());
            
            // 添加试卷描述
            if (examPager.getDescription() != null && !examPager.getDescription().isEmpty()) {
                addSubtitle(document, examPager.getDescription());
            }
            
            // 添加试卷基本信息
            addPaperInfo(document, examPager);

            // 添加所有题目
            for (int i = 0; i < questions.size(); i++) {
                ExamQuestionEntity question = questions.get(i);
                addQuestion(document, question, i + 1, showAnswer);
            }

            // 保存Word文档
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
                log.info("试卷导出成功，路径: {}", outputPath);
            }
        } catch (IOException e) {
            log.error("导出试卷失败", e);
            throw new RuntimeException("导出试卷失败", e);
        }
    }

    private void addTitle(XWPFDocument document, String title) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setFontFamily("宋体");
    }

    private void addSubtitle(XWPFDocument document, String subtitle) {
        XWPFParagraph subTitlePara = document.createParagraph();
        subTitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subTitleRun = subTitlePara.createRun();
        subTitleRun.setText(subtitle);
        subTitleRun.setFontSize(14);
        subTitleRun.setFontFamily("宋体");
        document.createParagraph().createRun().setText("");
    }

    private void addPaperInfo(XWPFDocument document, ExamPagerEntity examPager) {
        XWPFParagraph infoPara = document.createParagraph();
        infoPara.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun infoRun = infoPara.createRun();
        
        StringBuilder infoText = new StringBuilder();
        infoText.append("试卷难度: ").append(getDifficultyText(examPager.getDifficulty())).append("\n");
        infoText.append("考试时长: ").append(examPager.getDuration()).append("分钟\n");
        if (examPager.getStartTime() != null) {
            infoText.append("开始时间: ").append(examPager.getStartTime()).append("\n");
        }
        if (examPager.getEndTime() != null) {
            infoText.append("结束时间: ").append(examPager.getEndTime()).append("\n");
        }
        
        infoRun.setText(infoText.toString());
        infoRun.setFontSize(12);
        infoRun.setFontFamily("宋体");
        
        document.createParagraph().createRun().setText("");
    }

    private String getDifficultyText(String difficulty) {
        return switch (difficulty) {
            case "easy" -> "简单";
            case "medium" -> "中等";
            case "hard" -> "困难";
            default -> difficulty;
        };
    }

//    private void addQuestion(XWPFDocument document, ExamQuestionEntity question, int index, boolean showAnswer) {
//        // 题目序号和内容
//        XWPFParagraph questionPara = document.createParagraph();
//        XWPFRun questionRun = questionPara.createRun();
//        questionRun.setText(index + ". " + question.getQuestion());
//        questionRun.setBold(true);
//        questionRun.setFontSize(14);
//        questionRun.setFontFamily("宋体");
//
//        // 如果是选择题，添加选项
//        if (question.getType().equals("radio") || question.getType().equals("checkbox")) {
//            addOptions(document, question);
//        }
//
//        // 如果是填空题，添加填空提示
//        if (question.getType().equals("fill")) {
//            addFillBlanks(document, question);
//        }
//
//        // 如果需要显示答案
//        if (showAnswer) {
//            addAnswerAnalysis(document, question);
//        }
//
//        // 添加空行分隔
//        document.createParagraph().createRun().setText("");
//    }

//    private void addOptions(XWPFDocument document, ExamQuestionEntity question) {
//        // 这里需要解析question.getAnswers()，假设它是JSON格式存储的选项
//        // 实际实现需要根据您的数据存储格式进行调整
//        List<AnswerOption> options = parseAnswerOptions(question.getAnswers());
//
//        for (AnswerOption option : options) {
//            XWPFParagraph optionPara = document.createParagraph();
//            XWPFRun optionRun = optionPara.createRun();
//            optionRun.setText("    " + option.getLabel() + ". " + option.getContent());
//            optionRun.setFontSize(12);
//            optionRun.setFontFamily("宋体");
//        }
//    }

    private void addFillBlanks(XWPFDocument document, ExamQuestionEntity question) {
        // 这里需要解析question.getBlanks()，假设它是JSON格式存储的填空位置
        // 实际实现需要根据您的数据存储格式进行调整
        XWPFParagraph blankPara = document.createParagraph();
        XWPFRun blankRun = blankPara.createRun();
        blankRun.setText("    填空位置: ____________________");
        blankRun.setFontSize(12);
        blankRun.setFontFamily("宋体");
    }

//    private void addAnswerAnalysis(XWPFDocument document, ExamQuestionEntity question) {
//        XWPFParagraph analysisPara = document.createParagraph();
//        XWPFRun analysisRun = analysisPara.createRun();
//        analysisRun.setText("【答案】" + getCorrectAnswer(question) + "  【解析】" + question.getAnswerAnalysis());
//        analysisRun.setFontSize(12);
//        analysisRun.setFontFamily("宋体");
//        analysisRun.setColor("FF0000");
//    }

//    private String getCorrectAnswer(ExamQuestionEntity question) {
//        // 这里需要根据题目类型和答案格式返回正确答案
//        // 实际实现需要根据您的数据存储格式进行调整
//        if (question.getType().equals("radio") || question.getType().equals("checkbox")) {
//            List<AnswerOption> options = parseAnswerOptions(question.getAnswers());
//            StringBuilder correctAnswers = new StringBuilder();
//            for (AnswerOption option : options) {
//                if (option.isCorrect()) {
//                    correctAnswers.append(option.getLabel()).append(" ");
//                }
//            }
//            return correctAnswers.toString().trim();
//        } else {
//            return "略"; // 对于非选择题，返回"略"或实际答案
//        }
//    }

//    // 辅助类，用于解析答案选项
//    @Data
//    private static class AnswerOption {
//        private String label;
//        private String content;
//        private boolean isCorrect;
//    }

//    // 解析答案选项的JSON字符串
//    private List<AnswerOption> parseAnswerOptions(String answersJson) {
//        // 这里需要实现JSON解析逻辑
//        // 可以使用Jackson或Gson等库
//        // 示例代码省略具体实现
//        return List.of(); // 实际应返回解析后的选项列表
//    }

    private void addQuestion(XWPFDocument document, ExamQuestionEntity question, int index, boolean showAnswer) {
        // Question number and content
        XWPFParagraph questionPara = document.createParagraph();
        XWPFRun questionRun = questionPara.createRun();
        questionRun.setText(index + ". " + question.getQuestion());
        questionRun.setBold(true);
        questionRun.setFontSize(14);
        questionRun.setFontFamily("宋体");

        // Handle different question types
        switch (question.getType()) {
            case "radio":
            case "checkbox":
            case "image-radio":
            case "image-checkbox":
            case "dropdown":
            case "location":
                addOptions(document, question);
                break;
            case "fill":
            case "multi-fill":
                addFillBlanks(document, question);
                break;
            case "single-line":
                addSingleLineAnswer(document, question);
                break;
            case "multi-line":
                addMultiLineAnswer(document, question);
                break;
            case "description":
                addDescriptionContent(document, question);
                break;
            case "datetime":
                addDateTimeField(document, question);
                break;
            case "image-file":
                addImageUploadField(document, question);
                break;
            default:
                log.warn("Unknown question type: {}", question.getType());
        }

        // Add answer and analysis if needed
        if (showAnswer) {
            addAnswerAnalysis(document, question);
        }

        // Add empty line separator
        document.createParagraph().createRun().setText("");
    }

    private void addOptions(XWPFDocument document, ExamQuestionEntity question) {
        List<AnswerOption> options = parseAnswerOptions(question.getAnswers());

        for (AnswerOption option : options) {
            XWPFParagraph optionPara = document.createParagraph();
            XWPFRun optionRun = optionPara.createRun();

            // Handle image options
            if ((question.getType().equals("image-radio") || question.getType().equals("image-checkbox"))
                    && option.getImg() != null && !option.getImg().isEmpty()) {
                optionRun.setText("    " + option.getLabel() + ". " + option.getContent());
                optionRun.addBreak();

                try {
                    // This would need actual implementation to add image from URL
                    // For now, we'll just add the image URL as text
                    optionRun.setText("    [Image: " + option.getImg() + "]");
                } catch (Exception e) {
                    log.error("Failed to add image for question {}", question.getId(), e);
                    optionRun.setText("    [Image load failed]");
                }
            } else {
                // Regular text options
                optionRun.setText("    " + option.getLabel() + ". " + option.getContent());
            }

            optionRun.setFontSize(12);
            optionRun.setFontFamily("宋体");
        }
    }

//    private void addFillBlanks(XWPFDocument document, ExamQuestionEntity question) {
//        if (question.getType().equals("fill")) {
//            XWPFParagraph blankPara = document.createParagraph();
//            XWPFRun blankRun = blankPara.createRun();
//            blankRun.setText("    Answer: ____________________");
//            blankRun.setFontSize(12);
//            blankRun.setFontFamily("宋体");
//        } else if (question.getType().equals("multi-fill")) {
//            // For multi-fill questions, show all blanks
//            List<Blank> blanks = parseBlanks(question.getBlanks());
//            for (Blank blank : blanks) {
//                XWPFParagraph blankPara = document.createParagraph();
//                XWPFRun blankRun = blankPara.createRun();
//                blankRun.setText("    Blank " + blank.getIndex() + ": ____________________");
//                blankRun.setFontSize(12);
//                blankRun.setFontFamily("宋体");
//            }
//        }
//    }

    private void addSingleLineAnswer(XWPFDocument document, ExamQuestionEntity question) {
        XWPFParagraph answerPara = document.createParagraph();
        XWPFRun answerRun = answerPara.createRun();
        answerRun.setText("    回答: ____________________");
        answerRun.setFontSize(12);
        answerRun.setFontFamily("宋体");
    }

    private void addMultiLineAnswer(XWPFDocument document, ExamQuestionEntity question) {
        XWPFParagraph answerPara = document.createParagraph();
        XWPFRun answerRun = answerPara.createRun();
        answerRun.setText("    回答: ");
        answerRun.addBreak();
        answerRun.setText("    ________________________________________________________");
        answerRun.addBreak();
        answerRun.setText("    ________________________________________________________");
        answerRun.setFontSize(12);
        answerRun.setFontFamily("宋体");

//        if (question.getWordLimit() != null && question.getWordLimit() > 0) {
//            XWPFParagraph limitPara = document.createParagraph();
//            XWPFRun limitRun = limitPara.createRun();
//            limitRun.setText("    (Word limit: " + question.getWordLimit() + ")");
//            limitRun.setFontSize(10);
//            limitRun.setFontFamily("宋体");
//            limitRun.setColor("888888");
//        }
    }

    private void addDescriptionContent(XWPFDocument document, ExamQuestionEntity question) {
//        if (question.getContent() != null && !question.getContent().isEmpty()) {
//            XWPFParagraph contentPara = document.createParagraph();
//            XWPFRun contentRun = contentPara.createRun();
//
//            // Simple HTML stripping - for better results consider using a proper HTML to text converter
//            String plainText = question.getContent()
//                    .replaceAll("<[^>]+>", "")
//                    .replaceAll("&nbsp;", " ")
//                    .trim();
//
//            contentRun.setText("    " + plainText);
//            contentRun.setFontSize(12);
//            contentRun.setFontFamily("宋体");
//            contentRun.setItalic(true);
//        }
    }

    private void addDateTimeField(XWPFDocument document, ExamQuestionEntity question) {
        XWPFParagraph datePara = document.createParagraph();
        XWPFRun dateRun = datePara.createRun();
        dateRun.setText("    Date: ____________________");
        dateRun.setFontSize(12);
        dateRun.setFontFamily("宋体");
    }

    private void addImageUploadField(XWPFDocument document, ExamQuestionEntity question) {
        XWPFParagraph uploadPara = document.createParagraph();
        XWPFRun uploadRun = uploadPara.createRun();
        uploadRun.setText("    [Image upload field]");
        uploadRun.setFontSize(12);
        uploadRun.setFontFamily("宋体");

//        if (question.getFileTypes() != null) {
//            XWPFParagraph typePara = document.createParagraph();
//            XWPFRun typeRun = typePara.createRun();
//            typeRun.setText("    Accepted file types: " + question.getFileTypes());
//            typeRun.setFontSize(10);
//            typeRun.setFontFamily("宋体");
//            typeRun.setColor("888888");
//        }
//
//        if (question.getMaxSize() != null) {
//            XWPFParagraph sizePara = document.createParagraph();
//            XWPFRun sizeRun = sizePara.createRun();
//            sizeRun.setText("    Maximum file size: " + question.getMaxSize());
//            sizeRun.setFontSize(10);
//            sizeRun.setFontFamily("宋体");
//            sizeRun.setColor("888888");
//        }
    }

    private void addAnswerAnalysis(XWPFDocument document, ExamQuestionEntity question) {
        XWPFParagraph analysisPara = document.createParagraph();
        XWPFRun analysisRun = analysisPara.createRun();

        String answer = getCorrectAnswer(question);
        String analysis = question.getAnswerAnalysis() != null ? question.getAnswerAnalysis() : "";

        analysisRun.setText("【答案】" + answer + (analysis.isEmpty() ? "" : "  【解析】" + analysis));
        analysisRun.setFontSize(12);
        analysisRun.setFontFamily("宋体");
        analysisRun.setColor("FF0000");
    }

    private String getCorrectAnswer(ExamQuestionEntity question) {
        switch (question.getType()) {
            case "radio":
            case "checkbox":
            case "image-radio":
            case "image-checkbox":
            case "location":
                List<AnswerOption> options = parseAnswerOptions(question.getAnswers());
                StringBuilder correctAnswers = new StringBuilder();
                for (AnswerOption option : options) {
                    if (option.isCorrect()) {
                        if (!correctAnswers.isEmpty()) {
                            correctAnswers.append(", ");
                        }
                        correctAnswers.append(option.getLabel());
                    }
                }
                return correctAnswers.toString();

            case "dropdown":
                List<AnswerOption> dropdownOptions = parseAnswerOptions(question.getAnswers());
                for (AnswerOption option : dropdownOptions) {
                    if (option.isCorrect()) {
                        return option.getContent();
                    }
                }
                return "";

            case "single-line":
                return question.getCorrectAnswer() != null ? question.getCorrectAnswer() : "略";

            case "fill":
            case "multi-fill":
                if (question.getType().equals("fill")) {
                    return question.getCorrectAnswer() != null ? question.getCorrectAnswer() : "略";
                } else {
                    List<Blank> blanks = parseBlanks(question.getBlanks());
                    StringBuilder fillAnswers = new StringBuilder();
                    for (Blank blank : blanks) {
                        if (!fillAnswers.isEmpty()) {
                            fillAnswers.append("; ");
                        }
                        fillAnswers.append("填空").append(blank.getIndex()).append(": ").append(blank.getCorrectAnswer());
                        // 加一个换行
                        fillAnswers.append("\n");
                    }
                    return fillAnswers.toString();
                }

            case "multi-line":
            case "description":
            case "datetime":
            case "image-file":
                return "略"; // These types typically don't have simple correct answers

            default:
                return "略";
        }
    }

    // Helper classes and methods
    @Data
    private static class AnswerOption {
        private String label;
        private String content;
        private String img;
        private boolean isCorrect;
        private String value;
    }

    @Data
    private static class Blank {
        private int index;
        private String correctAnswer;
    }

    private List<AnswerOption> parseAnswerOptions(String answersJson) {
        try {
            JSONArray array = JSONUtil.parseArray(answersJson);
            return JSONUtil.toList(array, AnswerOption.class);
        } catch (Exception e) {
            log.error("Failed to parse answer options: {}", answersJson, e);
            return List.of();
        }
    }

    private List<Blank> parseBlanks(String blanksJson) {
        try {
            JSONArray array = JSONUtil.parseArray(blanksJson);
            return JSONUtil.toList(array, Blank.class);
        } catch (Exception e) {
            log.error("Failed to parse blanks: {}", blanksJson, e);
            return List.of();
        }
    }
}