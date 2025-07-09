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
     * @param userAnswers 用户答案对象
     * @return Markdown格式的字符串
     */
    public static String formatToMarkdown(JSONArray questions, JSONObject userAnswers) {
        if (questions == null || userAnswers == null) {
            return "# 无题目或答案数据\n";
        }

        StringBuilder markdown = new StringBuilder();
        markdown.append("# 题目与答案\n\n");
        markdown.append("> **说明**: \n")
                .append("> - ✓ 表示用户选择的答案\n")
                .append("> - ★ 表示正确答案\n\n");

        for (int i = 0; i < questions.size(); i++) {
            try {
                JSONObject question = questions.getJSONObject(i);
                String questionId = question.getString("id");
                String questionType = question.getString("type");

                markdown.append("## 题目").append(i + 1).append(" (ID: ").append(questionId).append(")\n\n")
                        .append("**题型**: ").append(getQuestionTypeName(questionType)).append("\n")
                        .append("**考核内容**: ").append(question.getString("assessmentContent")).append("\n\n")
                        .append("**问题**: ").append(question.getString("question")).append("\n\n")
                        .append("**分值**: ").append(question.getInteger("score")).append("分\n\n")
                        .append("**是否必答**: ").append(question.getBooleanValue("isRequired") ? "是" : "否").append("\n\n");

                // 根据题型处理答案
                switch (questionType) {
                    case "radio":
                    case "checkbox":
                    case "image-radio":
                    case "image-checkbox":
                    case "dropdown":
                    case "location":
                        handleChoiceQuestion(markdown, question, userAnswers, questionId);
                        break;
                    case "single-line":
                    case "multi-line":
                        handleTextQuestion(markdown, question, userAnswers, questionId);
                        break;
                    case "multi-fill":
                        handleFillBlankQuestion(markdown, question, userAnswers, questionId);
                        break;
                    default:
                        handleOtherQuestion(markdown, question, userAnswers, questionId);
                        break;
                }

                if (question.containsKey("answerAnalysis") && question.get("answerAnalysis") != null) {
                    markdown.append("**答案解析**: \n").append(question.getString("answerAnalysis")).append("\n\n");
                }

                markdown.append("---\n\n");
            } catch (Exception e) {
                markdown.append("**处理题目时出错**: ").append(e.getMessage()).append("\n\n");
                continue;
            }
        }

        return markdown.toString();
    }

    private static void handleChoiceQuestion(StringBuilder markdown, JSONObject question,
                                             JSONObject userAnswers, String questionId) {
        if (!question.containsKey("answers") || question.get("answers") == null) {
            markdown.append("**无选项数据**\n\n");
            return;
        }

        JSONArray options = question.getJSONArray("answers");
        markdown.append("**选项**: \n");

        // 先找出所有正确答案
        JSONArray correctAnswers = new JSONArray();
        for (int j = 0; j < options.size(); j++) {
            JSONObject option = options.getJSONObject(j);
            if (option.containsKey("isCorrect") && option.getBooleanValue("isCorrect")) {
                correctAnswers.add(option.getString("label"));
            }
        }

        for (int j = 0; j < options.size(); j++) {
            JSONObject option = options.getJSONObject(j);
            String label = option.getString("label");
            String content = option.getString("content");

            boolean isUserSelected = userAnswers.containsKey(questionId) &&
                    isAnswerSelected(userAnswers.get(questionId), label);
            boolean isCorrectAnswer = correctAnswers.contains(label);

            String userMark = isUserSelected ? "✓" : " ";
            String correctMark = isCorrectAnswer ? "★" : " ";

            markdown.append("- [").append(userMark).append("][").append(correctMark).append("] ")
                    .append(label).append(". ").append(content).append("\n");

            if (option.containsKey("img")) {
                String imgUrl = option.getString("img");
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    markdown.append("  ![图片](").append(imgUrl).append(")\n");
                }
            }
        }
        markdown.append("\n");
    }

    private static void handleFillBlankQuestion(StringBuilder markdown, JSONObject question,
                                                JSONObject userAnswers, String questionId) {
        if (!question.containsKey("blanks") || question.get("blanks") == null) {
            markdown.append("**无填空数据**\n\n");
            return;
        }

        JSONArray blanks = question.getJSONArray("blanks");
        Object answerValue = userAnswers.get(questionId);
        JSONArray userAnswerArray = new JSONArray();

        if (answerValue != null) {
            if (answerValue instanceof JSONArray) {
                userAnswerArray = (JSONArray) answerValue;
            } else if (answerValue instanceof String) {
                userAnswerArray.add(answerValue);
            }
        }

        markdown.append("**填空答案**: \n");
        for (int j = 0; j < blanks.size(); j++) {
            JSONObject blank = blanks.getJSONObject(j);
            int index = blank.getInteger("index");
            String correctAnswer = blank.getString("correctAnswer");
            String userAnswer = j < userAnswerArray.size() ? userAnswerArray.getString(j) : "未填写";

            markdown.append(index).append(". 正确答案: ★ ").append(correctAnswer)
                    .append(" | 你的答案: ✓ ").append(userAnswer).append("\n");
        }
        markdown.append("\n");
    }

    private static void handleTextQuestion(StringBuilder markdown, JSONObject question,
                                           JSONObject userAnswers, String questionId) {
        if (!userAnswers.containsKey(questionId)) {
            markdown.append("**未作答**\n\n");
            return;
        }

        Object answerValue = userAnswers.get(questionId);
        String userAnswer;

        if (answerValue instanceof JSONArray) {
            JSONArray answerArray = (JSONArray) answerValue;
            userAnswer = answerArray.isEmpty() ? "无答案" : answerArray.getString(0);
        } else {
            userAnswer = answerValue.toString();
        }

        String correctAnswer = question.containsKey("correctAnswer") ?
                question.getString("correctAnswer") : "无标准答案";

        markdown.append("**你的答案**: ✓ ").append(userAnswer).append("\n\n")
                .append("**参考答案**: ★ ").append(correctAnswer).append("\n\n");
    }

    private static void handleOtherQuestion(StringBuilder markdown, JSONObject question,
                                            JSONObject userAnswers, String questionId) {
        if (userAnswers.containsKey(questionId)) {
            markdown.append("**你的答案**: ✓ ").append(userAnswers.get(questionId)).append("\n\n");
        } else {
            markdown.append("**未作答**\n\n");
        }
    }

    private static boolean isAnswerSelected(Object answerValue, String label) {
        if (answerValue instanceof JSONArray) {
            return ((JSONArray) answerValue).contains(label);
        } else if (answerValue instanceof String) {
            return ((String) answerValue).equals(label);
        }
        return false;
    }

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