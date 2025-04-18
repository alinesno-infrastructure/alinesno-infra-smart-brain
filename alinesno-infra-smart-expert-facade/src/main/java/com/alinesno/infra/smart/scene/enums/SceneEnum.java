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

    LONG_TEXT(new SceneInfoDto(10000000L, "文档编写", "longText", "主要用于论文、合同、法律、招投标书编写等长文本场景", "已集成", "1", "", "fa-solid fa-signature",
            List.of(
                    new SceneAgent(10000102L, "大纲编辑助手", "chapterEditor", "对长文本的大纲进行编辑和优化，使其逻辑更加清晰。" , true),
                    new SceneAgent(10000103L, "内容编写助手", "contentEditor", "根据规划和大纲，生成具体的文本内容。")
            ))),
    DOCUMENT_REVIEW(new SceneInfoDto(40000000L, "文档审核", "documentReview", "上传一份文档进行文档审核和修改建议，给出审核的意见和内容", "已集成", "1", "", "fa-solid fa-file-pen",
            List.of(
                    new SceneAgent(20000101L, "文档分析助手", "analysisAgent", "对合同类文档的审核，审核清单生成，还有分析合同目录内容等。" , true),
                    new SceneAgent(20000202L, "文档审核助手", "logicReviewer", "审核文档内容的逻辑是否合理，有无矛盾或不合理之处。")
            ))),
    VIDEO_GENERATION(new SceneInfoDto(100000000L, "短视频生成", "videoGeneration", "根据提供的文本内容，自动生成视频内容，并导出为 MP4 文件", "已集成", "0", "", "fa-solid fa-video",
            List.of(
                    new SceneAgent(70000701L, "视频脚本生成助手", "textParser", "对提供的文本内容进行解析，提取关键信息。"),
                    new SceneAgent(70000702L, "视频内容生成助手", "videoGenerator", "根据解析的文本内容，生成视频内容。")
            ))),
    // 增加数据分析场景
    DATA_ANALYSIS(new SceneInfoDto(60000000L, "数据分析", "dataAnalysis", "根据企业数据的深度挖掘场景，自动生成数据报告分析和数据图表，并进行分析", "已集成", "0", "", "fa-solid fa-chart-bar",
            List.of(
                    new SceneAgent(50000501L, "数据分析助手", "textParser", "精准解析输入的文本内容，提取关键数据和分析目标相关的信息，为后续分析提供基础" , true),
                    new SceneAgent(50000502L, "数据挖掘助手", "dataMiner", "运用专业算法和技术，从大量企业相关数据中挖掘有价值的信息和潜在模式")
            )
    )) ,
    DOCUMENT_READER(new SceneInfoDto(50000000L, "文档阅读", "documentReader", "针对于大文档类型的阅读，归纳总结等，查询内容，或者提供对应的建议等 ", "已集成", "0", "", "fa-solid fa-book-open-reader",
            List.of(
                    new SceneAgent(30000301L, "内容分析助手", "summaryAgent", "对大文档的内容进行归纳总结，提取关键信息。"),
                    new SceneAgent(30000302L, "案例查询助手", "caseQueryAgent", "根据用户输入的关键词，在文档中查询相关案例内容。")
            ))),
//    MEETING_MINUTES(new SceneInfoDto(80000000L, "会议纪要", "meetingMinutes", "自动生成会议纪要，生成专业报告并优化排版，根据上传的模板生成会议纪要", "已集成", "0", "", "fa-solid fa-calendar-check",
//            Arrays.asList(
//                    new SceneAgent(60000601L, "语音识别助手", "reportFormatter", "将会议中的语音内容转换为文字信息。"),
//                    new SceneAgent(60000602L, "纪要生成助手", "minutesGenerator", "根据语音识别的文字内容，生成会议纪要。"),
//                    new SceneAgent(60000603L, "模板应用助手", "templateApplier", "将生成的会议纪要应用到指定的模板中，优化排版。")
//            ))),
    // 增加内容格式化场景，输入不规则内容，针对给定的模板生成结果内容
    CONTENT_FORMATTER(new SceneInfoDto(70000000L, "内容排版", "contentFormatter", "根据给定的模板，自动生成内容，并优化排版", "已集成", "1", "", "fa-solid fa-file-lines",
            List.of(
                    new SceneAgent(40000401L, "内容提取助手", "templateExtractor", "从给定的内容中提取出模板信息。") ,
                    new SceneAgent(40000402L, "内容检查助手", "contentReviewer", "对提取的模块内容进行检查，确保模板的完整性和正确性。" , true)
            )
    )),
    PRODUCT_RESEARCH(new SceneInfoDto(90000000L, "研发进度", "productResearch", "自动接入 Git 或者禅道等，获取到进度更新情况，并进行汇总归纳", "已集成", "0", "", "fa-solid fa-chart-line",
            Arrays.asList(
                    new SceneAgent(80000801L, "禅道进度提取助手", "progressExtractor", "从禅道系统中提取产品的进度信息。"),
                    new SceneAgent(80000802L, "Git进度提取助手", "progressExtractor", "从 Git 仓库中提取产品的开发进度信息。"),
                    new SceneAgent(80000803L, "进度咨询助手", "summarizer", "对提取的进度信息进行分析和咨询，给出相关的见解。"),
                    new SceneAgent(80000804L, "报表生成助手", "summarizer", "根据进度信息生成产品进度报表。")
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