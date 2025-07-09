package com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 考题与答案格式化工具类
 */
public class QuestionAnswerFormatter {

    /**
     * 将考题和答案格式化为Markdown格式
     * @param questions 题目列表
     * @param answers 答案对象
     * @return Markdown格式的字符串
     */
    public static String formatToMarkdown(JSONArray questions, JSONObject answers) {
        StringBuilder markdown = new StringBuilder();

        // 添加标题
        markdown.append("# 题目与答案\n\n");

        // 遍历所有题目
        for (int i = 0; i < questions.size(); i++) {
            JSONObject question = questions.getJSONObject(i);
            String questionId = question.getString("id");
            String questionType = question.getString("type");

            // 题目基本信息
            markdown.append("## 题目").append(i + 1).append("\n\n");
            markdown.append("**题型**: ").append(getQuestionTypeName(questionType)).append("\n");
            markdown.append("**考核内容**: ").append(question.getString("assessmentContent")).append("\n\n");
            markdown.append("**问题**: ").append(question.getString("question")).append("\n\n");
            markdown.append("**分值**: ").append(question.getInteger("score")).append("分\n\n");
            markdown.append("**是否必答**: ").append(question.getBooleanValue("isRequired") ? "是" : "否").append("\n\n");

            // 根据题型处理答案
            switch (questionType) {
                case "radio":
                case "checkbox":
                case "image-radio":
                case "image-checkbox":
                case "dropdown":
                case "location":
                    handleChoiceQuestion(markdown, question, answers, questionId);
                    break;
                case "single-line":
                case "multi-line":
                    handleTextQuestion(markdown, question, answers, questionId);
                    break;
                case "multi-fill":
                    handleFillBlankQuestion(markdown, question, answers, questionId);
                    break;
                case "image-file":
                case "datetime":
                case "description":
                default:
                    handleOtherQuestion(markdown, question, answers, questionId);
                    break;
            }

            // 添加答案解析
            if (question.containsKey("answerAnalysis") && question.get("answerAnalysis") != null) {
                markdown.append("**答案解析**: \n").append(question.getString("answerAnalysis")).append("\n\n");
            }

            markdown.append("---\n\n");
        }

        return markdown.toString();
    }

    /**
     * 处理选择题型（单选、多选、下拉等）
     */
    private static void handleChoiceQuestion(StringBuilder markdown, JSONObject question,
                                             JSONObject answers, String questionId) {
        if (question.containsKey("answers") && question.get("answers") != null) {
            JSONArray options = question.getJSONArray("answers");
            markdown.append("**选项**: \n");

            for (int j = 0; j < options.size(); j++) {
                JSONObject option = options.getJSONObject(j);
                String label = option.getString("label");
                String content = option.getString("content");

                // 获取用户选择的答案
                boolean isSelected = answers.containsKey(questionId) &&
                        answers.getJSONArray(questionId).contains(label);

                // 标记用户选择和正确答案
                String selectionMark = isSelected ? "✓" : " ";
                String correctMark = option.containsKey("isCorrect") &&
                        option.getBooleanValue("isCorrect") ? "(正确答案)" : "";

                markdown.append("- [").append(selectionMark).append("] ")
                        .append(label).append(". ").append(content).append(" ").append(correctMark).append("\n");

                // 如果有图片，添加图片标记
                if (option.containsKey("img")) {
                    String imgUrl = option.getString("img");
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        markdown.append("  ![图片](").append(imgUrl).append(")\n");
                    }
                }
            }
            markdown.append("\n");
        }
    }

    /**
     * 处理填空题型
     */
    private static void handleFillBlankQuestion(StringBuilder markdown, JSONObject question,
                                                JSONObject answers, String questionId) {
        if (question.containsKey("blanks") && question.get("blanks") != null) {
            JSONArray blanks = question.getJSONArray("blanks");
            JSONArray userAnswers = answers.containsKey(questionId) ?
                    answers.getJSONArray(questionId) : new JSONArray();

            markdown.append("**填空答案**: \n");
            for (int j = 0; j < blanks.size(); j++) {
                JSONObject blank = blanks.getJSONObject(j);
                int index = blank.getInteger("index");
                String correctAnswer = blank.getString("correctAnswer");
                String userAnswer = j < userAnswers.size() ? userAnswers.getString(j) : "未填写";

                markdown.append(index).append(". 正确答案: ").append(correctAnswer)
                        .append(" | 你的答案: ").append(userAnswer).append("\n");
            }
            markdown.append("\n");
        }
    }

    /**
     * 处理文本题型（单行、多行文本）
     */
    private static void handleTextQuestion(StringBuilder markdown, JSONObject question,
                                           JSONObject answers, String questionId) {
        if (answers.containsKey(questionId)) {
            String userAnswer = answers.getJSONArray(questionId).getString(0);
            String correctAnswer = question.containsKey("correctAnswer") ?
                    question.getString("correctAnswer") : "无标准答案";

            markdown.append("**你的答案**: \n").append(userAnswer).append("\n\n");
            markdown.append("**参考答案**: \n").append(correctAnswer).append("\n\n");
        } else {
            markdown.append("**未作答**\n\n");
        }
    }

    /**
     * 处理其他题型
     */
    private static void handleOtherQuestion(StringBuilder markdown, JSONObject question,
                                            JSONObject answers, String questionId) {
        if (answers.containsKey(questionId)) {
            markdown.append("**你的答案**: \n").append(answers.get(questionId)).append("\n\n");
        } else {
            markdown.append("**未作答**\n\n");
        }
    }

    /**
     * 获取题型名称
     */
    private static String getQuestionTypeName(String type) {
        return switch (type) {
            case "radio" -> "单选题";
            case "checkbox" -> "多选题";
            case "dropdown" -> "下拉题";
            case "image-radio" -> "图片单选题";
            case "image-checkbox" -> "图片多选题";
            case "single-line" -> "单行填空题";
            case "multi-line" -> "多行填空题";
            case "multi-fill" -> "多项填空题";
            case "image-file" -> "图片上传题";
            case "datetime" -> "日期时间题";
            case "location" -> "地理位置题";
            case "description" -> "描述题";
            default -> "未知题型";
        };
    }
}