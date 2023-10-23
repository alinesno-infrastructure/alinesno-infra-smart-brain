package com.alinesno.infra.smart.brain.interp.core;

import com.alinesno.infra.smart.brain.interp.exception.InterpreterException;
import com.alinesno.infra.smart.brain.interp.utils.UserInfoUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SystemMessageGenerator {
    /**
     * 动态生成系统消息。
     *
     * @param interpreter 解释器实例
     * @return 系统消息字符串
     */
    public static String generateSystemMessage(Interpreter interpreter) {
        // 开始静态系统消息
        String systemMessage = interpreter.getSystemMessage();

        // 添加动态组件，如用户的操作系统、用户名等
        systemMessage += "\n" + UserInfoUtils.getUserInfoString();
        try {
            systemMessage += "\n" + interpreter.getRelevantProceduresString();
        } catch (InterpreterException e) {
            if (interpreter.isDebugMode()) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                systemMessage += "\n" + sw.toString();
            }
            // 如果有些人无法安装嵌入模型（我不确定是否会发生这种情况）
        }

        return systemMessage;
    }

    public static void main(String[] args) {
        // 示例用法
        Interpreter interpreter = new Interpreter();
        // 设置和初始化解释器...

        String systemMessage = generateSystemMessage(interpreter);
        System.out.println(systemMessage);
    }
}
