package com.alinesno.infra.smart.scene.enums;

import com.alinesno.infra.smart.scene.dto.SceneAgent;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum SceneEnum {

    // 文章写作场景(ArticleWriting)
    ARTICLE_WRITING(new SceneInfoDto(160000000L, "文章写作", "articleWriting", "专业的文章生成、编辑和优化场景，支持多类型文章创作", "已集成", "1", "", "fa-solid fa-feather", true,
            List.of(
                    new SceneAgent(160000001L, "写作专员", "articleWriter", "负责文章内容的创作、编辑和优化，确保逻辑连贯、表达流畅" , true , true),
                    new SceneAgent(160000002L, "排版专员", "articleLayoutDesigner", "负责文章的格式排版、视觉优化和多媒体元素整合")
            )
    )),
    // 深度搜索场景(DeepSearch)
    DEEP_SEARCH(new SceneInfoDto(120000000L, "深度搜索", "deepSearch", "深度搜索场景，聊天窗口的升级版本，用于智能搜索和知识库问答", "已集成", "1", "", "fa-solid fa-magnifying-glass", true,
            List.of(
                    new SceneAgent(120000001L, "深度检索专员", "searchPlanner", "规划专员负责规划并确定问题的优先级，并确定问题的解决方式。")
            )
    )),
    // 通用智能体
    GENERAL_AGENT(new SceneInfoDto(110000000L, "通用场景", "generalAgent", "多行业的通用业务处理和结果展示场景，数据分析、行业分析、报告编写等", "已集成", "1", "", "fa-solid fa-cogs",
            List.of(
                    new SceneAgent(110000001L, "解决方案助手", "businessProcessor", "业务处理助手负责处理通用业务流程中的各类任务，为后续的结果展示提供准确的数据支持。" , true),
                    new SceneAgent(110000002L, "业务执行助手", "businessExecute", "针对用于处理业务并执行业务指定的内容场景，包括分析业务内容。"),
                    new SceneAgent(110000003L, "业务展示助手", "dataViewer", "业务展示助手专注于将业务处理助手处理后的数据和结果，以清晰、直观且用户友好的方式呈现出来。")
            )
    )),
    // 考试培训
    EXAM_AGENT(new SceneInfoDto(130000000L, "考试培训", "examPager", "可针对于课件AI生成试卷和生成题库，在线考试和考试培训测试等场景", "已集成", "1", "", "fa-solid fa-graduation-cap",
            List.of(
                    // 出题专员、阅卷专员、组卷专员
                    new SceneAgent(130000000L, "出题专员", "questionGenerator", "根据问题的类型和难度，自动生成试题。" , true) ,
                    new SceneAgent(130000001L, "阅卷专员", "answerChecker", "对学生的答案进行阅卷，给出正确与否的判断，并给出解析答案")
            )
    )),
    // 长文场景
    LONG_TEXT(new SceneInfoDto(10000000L, "长文档编写", "longText", "主要用于论文、合同、方案、招投标书编写等超长文本场景", "已集成", "1", "", "fa-solid fa-signature",
            List.of(
                    new SceneAgent(10000102L, "大纲编辑助手", "chapterEditor", "对长文本的大纲进行编辑和优化，使其逻辑更加清晰。" , true),
                    new SceneAgent(10000103L, "内容编写助手", "contentEditor", "根据规划和大纲，生成具体的文本内容。")
    ))),
    // 文档审核
    DOCUMENT_REVIEW(new SceneInfoDto(40000000L, "文档审核", "documentReview", "上传一份文档进行文档审核和修改建议，给出审核的意见和内容", "已集成", "1", "", "fa-solid fa-file-pen",
            List.of(
                    new SceneAgent(20000101L, "文档分析助手", "analysisAgent", "对合同类文档的审核，审核清单生成，还有分析合同目录内容等。" , true),
                    new SceneAgent(20000202L, "文档审核助手", "logicReviewer", "审核文档内容的逻辑是否合理，有无矛盾或不合理之处。")
    ))),
//    // 视频生成
//    VIDEO_GENERATION(new SceneInfoDto(100000000L, "短视频生成", "videoGeneration", "根据提供的文本内容，自动生成视频内容，并导出为 MP4 文件", "已集成", "0", "", "fa-solid fa-video",
//            List.of(
//                    new SceneAgent(70000701L, "视频脚本生成", "textParser", "对提供的文本内容进行解析，提取关键信息。"),
//                    new SceneAgent(70000702L, "视频内容生成", "videoGenerator", "根据解析的文本内容，生成视频内容。")
//    ))),
    // 文档阅读
    DOCUMENT_READER(new SceneInfoDto(50000000L, "文档阅读", "documentReader", "针对于大文档类型的阅读，归纳总结等，查询内容，或者提供对应的建议等 ", "已集成", "1", "", "fa-solid fa-book-open-reader",
            List.of(
                    new SceneAgent(30000301L, "内容分析助手", "summaryAgent", "对大文档的内容进行归纳总结，提取关键信息。"),
                    new SceneAgent(30000302L, "案例查询助手", "caseQueryAgent", "根据用户输入的关键词，在文档中查询相关案例内容。")
    ))),
    // 内容排版
    CONTENT_FORMATTER(new SceneInfoDto(70000000L, "智能文档", "contentFormatter", "AI智能文档管理，文档编写，文档审核和内容排版等功能", "已集成", "1", "", "fa-solid fa-file-lines",
            List.of(
                    new SceneAgent(40000401L, "内容提取助手", "templateExtractor", "从给定的内容中提取出模板信息。") ,
                    new SceneAgent(40000402L, "内容检查助手", "contentReviewer", "对提取的模块内容进行检查，确保模板的完整性和正确性。" , true)
            )
    )),
    // 项目检索
    PRODUCT_RESEARCH(new SceneInfoDto(90000000L, "知识库检索", "productResearch", "接入企业不同知识库进行内容深度检索，文档报告分析，预测规划等", "已集成", "1", "", "fa-solid fa-chart-line",
            Arrays.asList(
                    new SceneAgent(80000801L, "进度采集助手", "progressCollector", "从版本管理系统中提取产品的进度信息。"),
                    new SceneAgent(80000802L, "项目情况分析助手", "progressAnalyzer", "从版本仓库中提取产品的开发进度信息，并提供数据分析")
            )))
//    // 原型设计
//    PROTOTYPE_DESIGN(new SceneInfoDto(170000000L, "原型设计", "prototypeDesign", "根据需求文档进行产品原型设计，完成交互逻辑与界面框架规划", "进行中", "1", "", "fa-solid fa-pencil-ruler",
//                 Arrays.asList(
//                     new SceneAgent(170000001L, "需求分析专员", "requirementAnalyzer", "负责解析需求文档，提炼核心功能点与用户交互逻辑"),
//                     new SceneAgent(170000002L, "原型设计专员", "prototypeDesigner", "基于需求分析结果，使用专业工具完成高保真原型设计")
//        )))
    ;

    private final SceneInfoDto sceneInfo;

    // 获取返回的列表
    public static List<SceneInfoDto> getList() {
        return Arrays.stream(values())
                .map(SceneEnum::getSceneInfo)
                .filter(SceneInfoDto::isShow)
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

    // 根据 code 获取它的 SceneAgent 列表
    public static List<SceneAgent> getAgentsByCode(String code) {
        return getByCode(code)
                .map(scene -> scene.getSceneInfo().getAgents())
                .orElse(List.of());
    }

    /**
     * 通过 agent 的 id 获取到 agent 的信息
     * @param agentId agent 的 id
     * @return 对应的 SceneAgent 对象，如果未找到则返回 null
     */
    public static SceneAgent getAgentById(long agentId) {
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