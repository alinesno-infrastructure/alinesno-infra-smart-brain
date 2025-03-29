package com.alinesno.infra.smart.scene.enums;

import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.dto.ScreenAgent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum SceneEnum {

    LONG_TEXT(new SceneInfoDto(10000000L, "长内容编写场景", "longText", "主要用于论文、合同、法律、招投标书编写等长文本场景", "已集成", "1", "", "fa-solid fa-signature",
            Arrays.asList(
                    new ScreenAgent(10000101L, "内容规划助手", "chapterEditor", "协助规划长文本的整体内容结构，确定各章节的主题和要点。"),
                    new ScreenAgent(10000102L, "大纲编辑助手", "chapterEditor", "对长文本的大纲进行编辑和优化，使其逻辑更加清晰。"),
                    new ScreenAgent(10000103L, "内容编写助手", "contentEditor", "根据规划和大纲，生成具体的文本内容。"),
                    new ScreenAgent(10000104L, "图片生成助手", "imageEditor", "根据文本内容生成相关的图片，增强文本的可视化效果。")
            ))),
    DOCUMENT_REVIEW(new SceneInfoDto(40000000L, "文档审核场景", "documentReview", "上传一份文档进行文档审核和修改建议，给出审核的意见和内容", "已集成", "0", "", "fa-solid fa-file-pen",
            Arrays.asList(
                    new ScreenAgent(20000201L, "格式审核助手", "formatReviewer", "检查文档的格式是否符合规范，如字体、字号、段落间距等。"),
                    new ScreenAgent(20000202L, "内容逻辑审核助手", "logicReviewer", "审核文档内容的逻辑是否合理，有无矛盾或不合理之处。"),
                    new ScreenAgent(20000203L, "语法拼写审核助手", "grammarReviewer", "检查文档中的语法错误和拼写错误，并给出修改建议。")
            ))),
    DOCUMENT_READING(new SceneInfoDto(50000000L, "文档阅读场景", "documentReading", "针对于大文档类型的阅读，归纳总结等，查询内容，或者提供对应的建议等 ", "已集成", "0", "", "fa-solid fa-book-open",
            Arrays.asList(
                    new ScreenAgent(30000301L, "内容总结助手", "summaryAgent", "对大文档的内容进行归纳总结，提取关键信息。"),
                    new ScreenAgent(30000302L, "内容查询助手", "queryAgent", "根据用户输入的关键词，在文档中查询相关内容。"),
                    new ScreenAgent(30000303L, "建议提供助手", "suggestionAgent", "根据文档内容和用户需求，提供相关的建议和意见。")
            ))),
    EXAM_PAPER_GENERATION(new SceneInfoDto(60000000L, "生成试卷场景", "examPaperGeneration", "根据培训情况，自动生成试卷内容，并导出 Word 内容", "已集成", "0", "", "fa-solid fa-file-invoice",
            Arrays.asList(
                    new ScreenAgent(40000401L, "题目生成助手", "questionGenerator", "根据培训内容和要求，自动生成试卷题目。"),
                    new ScreenAgent(40000402L, "Word 导出助手", "wordExporter", "将生成的试卷内容导出为 Word 文档。")
            ))),
    COPYWRITING(new SceneInfoDto(70000000L, "文案生成场景", "copywriting", "可以根据所提供的模板，自动生成文案信息，比较年度报告、合作协议等复杂文件初稿【待定】", "待定", "0", "", "fa-solid fa-file-word",
            Arrays.asList(
                    new ScreenAgent(50000501L, "模板匹配助手", "templateMatcher", "根据用户需求，匹配合适的文案模板。"),
                    new ScreenAgent(50000502L, "文案内容生成助手", "contentGenerator", "依据匹配的模板，生成具体的文案内容。"),
                    new ScreenAgent(50000503L, "初稿优化助手", "draftOptimizer", "对生成的文案初稿进行优化，提升文案质量。")
            ))),
    MEETING_MINUTES(new SceneInfoDto(80000000L, "会议纪要场景", "meetingMinutes", "自动生成会议纪要，生成专业报告并优化排版，根据上传的模板生成会议纪要", "已集成", "0", "", "fa-solid fa-calendar-check",
            Arrays.asList(
                    new ScreenAgent(60000601L, "语音识别助手", "reportFormatter", "将会议中的语音内容转换为文字信息。"),
                    new ScreenAgent(60000602L, "纪要生成助手", "minutesGenerator", "根据语音识别的文字内容，生成会议纪要。"),
                    new ScreenAgent(60000603L, "模板应用助手", "templateApplier", "将生成的会议纪要应用到指定的模板中，优化排版。")
            ))),
    VIDEO_GENERATION(new SceneInfoDto(100000000L, "视频生成场景", "videoGeneration", "根据提供的文本内容，自动生成视频内容，并导出为 MP4 文件", "已集成", "0", "", "fa-solid fa-video",
            Arrays.asList(
                    new ScreenAgent(70000701L, "文本内容解析助手", "textParser", "对提供的文本内容进行解析，提取关键信息。"),
                    new ScreenAgent(70000702L, "视频内容生成助手", "videoGenerator", "根据解析的文本内容，生成视频内容。"),
                    new ScreenAgent(70000703L, "视频导出助手", "videoExporter", "将生成的视频内容导出为 MP4 文件。")
            ))),
    PRODUCT_RESEARCH(new SceneInfoDto(90000000L, "产品进度汇总", "productResearch", "自动接入 Git 或者禅道等，获取到进度更新情况，并进行汇总归纳", "已集成", "0", "", "fa-solid fa-chart-line",
            Arrays.asList(
                    new ScreenAgent(80000801L, "禅道进度提取助手", "progressExtractor", "从禅道系统中提取产品的进度信息。"),
                    new ScreenAgent(80000802L, "Git 进度提取助手", "progressExtractor", "从 Git 仓库中提取产品的开发进度信息。"),
                    new ScreenAgent(80000803L, "进度咨询助手", "summarizer", "对提取的进度信息进行分析和咨询，给出相关的见解。"),
                    new ScreenAgent(80000804L, "报表生成助手", "summarizer", "根据进度信息生成产品进度报表。")
            )));

    private final SceneInfoDto sceneInfo;

    // 获取返回的列表
    public static List<SceneInfoDto> getList() {
        return Arrays.stream(values())
                .map(SceneEnum::getSceneInfo)
                .collect(Collectors.toList());
    }

    /**
     * 通过code获取到SceneInfo
     * @param code
     * @return
     */
    public static SceneInfoDto getSceneInfoByCode(String code) {
        return getByCode(code)
                .map(SceneEnum::getSceneInfo)
                .orElse(null);
    }

    // 根据 code 获取返回的对象
    public static Optional<SceneEnum> getByCode(String code) {
        return Arrays.stream(values())
                .filter(scene -> scene.getSceneInfo().getCode().equals(code))
                .findFirst();
    }

    /**
     * 通过给的code，判断是否有这个场景代码
     * @param code
     * @return
     */
    public static boolean hasCode(String code) {
        return Arrays.stream(values())
                .anyMatch(scene -> scene.getSceneInfo().getCode().equals(code));
    }

    // 根据 code 获取它的 ScreenAgent 列表
    public static List<ScreenAgent> getAgentsByCode(String code) {
        return getByCode(code)
                .map(scene -> scene.getSceneInfo().getAgents())
                .orElse(List.of());
    }

    /**
     * 通过 agent 的 id 获取到 agent 的信息
     * @param agentId agent 的 id
     * @return 对应的 ScreenAgent 对象，如果未找到则返回 null
     */
    public static ScreenAgent getAgentById(long agentId) {
        return Arrays.stream(values())
                .flatMap(scene -> scene.getSceneInfo().getAgents().stream())
                .filter(agent -> agent.getId() == agentId)
                .findFirst()
                .orElse(null);
    }

    /**
     * 通过 sceneInfo 的 id 获取到 SceneInfoDto 的信息
     * @param sceneInfoId sceneInfo 的 id
     * @return 对应的 SceneInfoDto 对象，如果未找到则返回 null
     */
    public static SceneInfoDto getSceneInfoById(long sceneInfoId) {
        return Arrays.stream(values())
                .map(SceneEnum::getSceneInfo)
                .filter(info -> info.getId() == sceneInfoId)
                .findFirst()
                .orElse(null);
    }
}