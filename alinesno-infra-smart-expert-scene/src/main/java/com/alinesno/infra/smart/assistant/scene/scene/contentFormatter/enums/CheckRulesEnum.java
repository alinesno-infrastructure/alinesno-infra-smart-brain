package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 定义检查规则的枚举类
@Getter
public enum CheckRulesEnum {

    TYPO(1L, "错别字", "检查文字的拼写是否正确，有无形似字、同音字误用等情况。", "high"),
    PUNCTUATION(2L, "标点符号", "查看标点符号的使用是否准确，是否存在标点缺失、多余或使用不当的问题，如该用句号的地方用了逗号，引号的使用是否规范等。", "medium"),
    SENTENCE_FLUENCY(3L, "语句通顺", "确保句子表达流畅，没有语病，如主谓宾搭配不当、成分残缺、语序失调、句式杂糅等问题。", "high"),
    EXPRESSION_RIGOR(4L, "表达严谨", "检查内容的表述是否逻辑清晰、严密，避免出现前后矛盾、概念模糊、以偏概全、偷换概念等问题。", "high"),
    GRAMMAR_STANDARD(5L, "语法规范", "确认文章符合相应语言的语法规则，包括词性使用、句子结构、时态语态等方面的正确性。", "high"),
    WORD_USAGE_APPROPRIATENESS(6L, "用词恰当", "检查用词是否精准、合适，是否存在用词不当、词义重复、词语搭配不合理等问题，同时要注意词语的感情色彩和语体色彩是否符合文章的风格和语境。", "medium"),
    CONTENT_COMPLETENESS(7L, "内容完整", "查看文章是否涵盖了需要表达的所有要点，有无重要信息遗漏，开头、中间和结尾是否完整，是否存在虎头蛇尾或结构松散的情况。", "high"),
    FORMAT_STANDARD(8L, "格式规范", "如果有特定的格式要求，如字体、字号、行距、段落缩进、页码设置等，要检查是否符合规范。", "low"),
    CITATION_STANDARD(9L, "引用规范", "若文章中有引用他人的观点、数据、研究成果等，要检查引用格式是否正确，是否注明了出处，有无抄袭、剽窃等学术不端行为。", "high"),
    STYLE_UNITY(10L, "风格统一", "检查文章整体的语言风格是否一致，如正式与非正式、书面语与口语等风格不能混杂，以免造成阅读上的突兀感。", "medium"),
    DATA_ACCURACY(11L, "数据准确性", "对于包含数据的文章，要核实数据的真实性和准确性，确保数据来源可靠，引用无误，计算正确。", "high"),
    IDIOM_CHECK(12L, "成语检查", "检查文章中成语的使用是否正确，包括成语的书写、含义、语境搭配等方面。", "medium"),
    SCENE_STYLE_CHECK(13L, "场景风格检查", "查看文章的风格是否与所描述的场景相契合，避免出现风格与场景不符的情况。", "medium");

    private final Long id;
    private final String checkItem;
    private final String checkDetails;
    private final String riskLevel;

    // 枚举类的构造函数
    CheckRulesEnum(Long id, String checkItem, String checkDetails, String riskLevel) {
        this.id = id;
        this.checkItem = checkItem;
        this.checkDetails = checkDetails;
        this.riskLevel = riskLevel;
    }

    // 根据 ID 获取对应的枚举实例
    public static Optional<CheckRulesEnum> getById(Long id) {
        for (CheckRulesEnum rule : values()) {
            if (rule.id.equals(id)) {
                return Optional.of(rule);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据多个 ID 获取对应的枚举实例列表
     * @param ids
     * @return
     */
    public static List<CheckRulesEnum> getByMultipleIds(List<Long> ids) {
        List<CheckRulesEnum> rules = new ArrayList<>();
        for (Long id : ids) {
            getById(id).ifPresent(rules::add);
        }
        return rules;
    }

    /**
     * 获取所有规则
     * @param ids
     * @return
     */
    public static String getAllRules(List<Long> ids) {
        StringBuilder sb = new StringBuilder();

        List<CheckRulesEnum> rules = getByMultipleIds(ids);
        for (CheckRulesEnum rule : rules) {
            sb.append("检查项:").append(rule.getCheckItem()).append(",");
            sb.append("检查内容:").append(rule.getCheckDetails()).append(",");
            sb.append("风险级别:").append(rule.getRiskLevel()).append(";");
            sb.append("\r\n");
        }

        return sb.toString();
    }

    /**
     * 获取所有规则
     * @return
     */
    public static List<Map<String , Object>> getAllRule() {
        List<Map<String , Object>> list = new ArrayList<>();

        for (CheckRulesEnum rule : values()) {
            Map<String , Object> map = Map.of(
                    "id" , rule.getId() ,
                    "checkItem" , rule.getCheckItem() ,
                    "checkDetails" , rule.getCheckDetails() ,
                    "riskLevel" , rule.getRiskLevel()
            );
            list.add(map);
        }

        return list;
    }

    public String getRules() {

        return "检查项:" + getCheckItem() + "," +
                "检查内容:" + getCheckDetails() + "," +
                "风险级别:" + getRiskLevel() + ";" +
                "\r\n";
    }
}