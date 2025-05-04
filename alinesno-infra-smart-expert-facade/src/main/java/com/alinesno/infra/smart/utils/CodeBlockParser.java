package com.alinesno.infra.smart.utils;

import com.alinesno.infra.smart.assistant.api.CodeContent;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析代码块的工具类。
 */
public class CodeBlockParser {

    /**
     * 从给定内容中解析代码块。 // TODO 处理计算机语言识别不是特别准确的问题
     *
     * @param content 要提取代码块的内容。
     * @return 包含解析后代码块的 TaskContentDto.CodeContent 对象列表。
     */
    public static List<CodeContent> parseCodeBlocks(String content) {

        List<CodeContent> codeBlocks = new ArrayList<>();

        // 先判断内容是否是以```json开头的，如果是的话则直接截取前后字符即可
        if(StringUtils.isNotBlank(content) && content.startsWith("```json")){
            int endIndex = content.trim().length() ;
            String block = content.substring(3, endIndex-3);
            int langIndex = block.indexOf("\n");
            if (langIndex != -1) {
                String lang = block.substring(0, langIndex).trim();
                String code = block.substring(langIndex).trim();
                codeBlocks.add(new CodeContent(lang, code));
            }

            return codeBlocks;
        }

        if(StringUtils.isNotBlank(content)) {

            String regex = "```(.*?)```"; // 用于匹配代码块的正则表达式
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String block = matcher.group(1).trim();
                int langIndex = block.indexOf("\n");
                if (langIndex != -1) {
                    String lang = block.substring(0, langIndex).trim();
                    String code = block.substring(langIndex).trim();
                    codeBlocks.add(new CodeContent(lang, code));
                }
            }

        }

        if(codeBlocks.isEmpty()){
            FileType fileType = LanguageIdentifier.identifyLanguage(content) ;

            if(!fileType.isUnknown()){  // 针对于内容结果为yaml或者sql直接返回的场景
                codeBlocks.add(new CodeContent(fileType.getValue(), content));
            }
        }

        return codeBlocks;
    }

    public static void main(String[] args) {
        String jsonBlock = """
                ```json
                {
                    "thought": "根据已生成的图表数据，需结合MBA球星分析框架（技术领导力、团队协作、关键问题解决等维度）进行结构化输出。通过整合代码提交趋势、技术领域分布、角色贡献占比三组可视化数据，匹配技术球星评估模型完成多维度能力量化分析，并输出完整图文报告。",
                    "tools": [],
                    "finalAnswer": "### 李伟个人能力分析报告（MBA球星分析法）\\n\\n#### 一、技术稳定性分析\\n<iframe src='http://training-static.linesno.com/public/2025-05-04/6816e784aed21eaabce64a42.html' class='role-chat-iframe-inner'></iframe>\\n- **数据解读**：2023年提交量呈阶梯式增长（Q4较Q1增长150%），12月达峰值20次\\n- **球星类比**：类似NBA球员的赛季得分稳定性，展现出持续输出能力\\n\\n#### 二、技术全面性评估\\n<iframe src='http://training-static.linesno.com/public/2025-05-04/6816e785aed21eaabce64a43.html' class='role-chat-iframe-inner'></iframe>\\n- **领域分布**：后端开发占比42%（核心能力），DevOps占25%（突出运维能力）\\n- **球星定位**：类似全能型控卫（代码开发+系统运维双修）\\n\\n#### 三、团队角色画像\\n<iframe src='http://training-static.linesno.com/public/2025-05-04/6816e785aed21eaabce64a44.html' class='role-chat-iframe-inner'></iframe>\\n- **核心贡献**：45%核心模块开发（主攻手）+30%代码评审（防守型角色）\\n- **类比分析**：类似球队队长（LeBron式技术领导力）\\n\\n#### 四、提升建议\\n1. **技术深度**：后端占比42%但缺乏架构级提交（需提升复杂系统设计能力）\\n2. **领域扩展**：测试领域仅占10%（需加强质量保障体系贡献）\\n3. **协作效率**：代码评审30%但缺少文档类提交（需提升知识沉淀能力）\\n\\n#### 五、培养方案\\n```mermaid\\ngraph TD\\n    A[技术球星培养路径] --> B[季度目标]\\n    B --> C{能力矩阵}\\n    C -->|架构能力| D[主导微服务改造项目]\\n    C -->|质量保障| E[建立自动化测试体系]\\n    C -->|知识管理| F[输出3篇技术方案文档]\\n```\\n\\n*注：数据周期2023.01-2023.12，基于128次有效提交分析*"
                }
                ```
                """;

        List<CodeContent> codes = CodeBlockParser.parseCodeBlocks(jsonBlock);
        for (CodeContent code : codes) {
            System.out.println("Language: " + code.getLanguage());
            System.out.println("Code: " + code.getContent());
            System.out.println("----------------------------------------");
        }
    }

}
