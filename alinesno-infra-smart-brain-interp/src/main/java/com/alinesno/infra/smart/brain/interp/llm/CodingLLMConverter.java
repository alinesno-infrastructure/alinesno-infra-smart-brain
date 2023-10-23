package com.alinesno.infra.smart.brain.interp.llm;

import java.util.ArrayList;
import java.util.List;

public class CodingLLMConverter {
    
    public static List<Object> convertToCodingLLM(TextLLM textLLM, boolean debugMode) {
        /*
         * 将textLLM转换为OI Coding LLM（一个使用OI消息并流式传输带有`message`、`language`和`code`的增量的生成器）。
         */

        List<Object> codingLLM = new ArrayList<>();
        List<Object> messages = convertToOpenAIMessages(textLLM.getMessages(), false);

        boolean insideCodeBlock = false;
        StringBuilder accumulatedBlock = new StringBuilder();
        String language = null;

        for (Object chunk : textLLM.getChunks()) {

            if (debugMode) {
                System.out.println("Chunk in codingLLM: " + chunk);
            }

            if (!((MessageChunk) chunk).hasChoices()) {
                // 这有时会发生
                continue;
            }

            String content = ((MessageChunk) chunk).getChoices().get(0).getDelta().getContent();

            accumulatedBlock.append(content);

            if (accumulatedBlock.toString().endsWith("`")) {
                // 我们可能正在逐个标记地写入"```"
                continue;
            }

            // 我们刚进入一个代码块吗？
            if (accumulatedBlock.toString().contains("```") && !insideCodeBlock) {
                insideCodeBlock = true;
                accumulatedBlock = new StringBuilder(accumulatedBlock.toString().split("```")[1]);
            }

            // 我们刚退出一个代码块吗？
            if (insideCodeBlock && accumulatedBlock.toString().contains("```")) {
                return codingLLM;
            }

            // 如果我们在代码块中
            if (insideCodeBlock) {

                // 如果没有`language`，找到它
                if (language == null && accumulatedBlock.toString().contains("\n")) {
                    language = accumulatedBlock.toString().split("\n")[0];

                    // 如果没有指定，默认为python
                    if (language.equals("")) {
                        language = "python";
                    }

                    OutputObject output = new OutputObject();
                    output.setLanguage(language);

                    // 如果在这个块中收到的不仅仅是语言，则发送它
                    if (content.split("\n")[1] != null) {
                        output.setCode(content.split("\n")[1]);
                    }

                    codingLLM.add(output);

                // 如果有`language`，将输出作为代码发送
                } else if (language != null) {
                    OutputObject output = new OutputObject();
                    output.setCode(content);
                    codingLLM.add(output);
                }
            }

            // 如果不在代码块中，将输出作为消息发送
            if (!insideCodeBlock) {
                MessageObject message = new MessageObject();
                message.setMessage(content);
                codingLLM.add(message);
            }
        }

        return codingLLM;
    }

    private static List<Object> convertToOpenAIMessages(List<Object> messages, boolean functionCalling) {
        // 将消息转换为OpenAI消息的辅助函数
        // 在这里添加转换逻辑
        return new ArrayList<>();
    }

    private static class TextLLM {
        private List<Object> messages;
        private List<Object> chunks;

        public TextLLM(List<Object> messages, List<Object> chunks) {
            this.messages = messages;
            this.chunks = chunks;
        }

        public List<Object> getMessages() {
            return messages;
        }

        public List<Object> getChunks() {
            return chunks;
        }
    }

    private static class MessageChunk {
        private List<Choice> choices;

        public MessageChunk(List<Choice> choices) {
            this.choices = choices;
        }

        public boolean hasChoices() {
            return choices != null && !choices.isEmpty();
        }

        public List<Choice> getChoices() {
            return choices;
        }
    }

    private static class Choice {
        private Delta delta;

        public Choice(Delta delta) {
            this.delta = delta;
        }

        public Delta getDelta() {
            return delta;
        }
    }

    private static class Delta {
        private String content;

        public Delta(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }


    private static class OutputObject {
        private String language;
        private String code;

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    private static class MessageObject {
        private String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
