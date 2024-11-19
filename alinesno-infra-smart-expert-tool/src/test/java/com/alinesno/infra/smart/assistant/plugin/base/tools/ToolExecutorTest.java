package com.alinesno.infra.smart.assistant.plugin.base.tools;

import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ToolExecutorTest {

    @Test
    public void testScript() throws Exception {
        String groovyScript = """
                import com.alinesno.infra.smart.assistant.plugin.base.Tool
                import com.alinesno.infra.smart.assistant.annotation.ParamInfo
                import com.alinesno.infra.smart.assistant.annotation.ToolInfo
                import cn.hutool.core.date.DateUtil
                                
                /**
                 * 获取当前时间(示例工具类)
                 */
                @ToolInfo(description = "获取当前时间")
                class GetTimeTools extends Tool {
                                
                    @ParamInfo(name = "dateStr", description = "日期字符串")
                    String dateStr
                                
                    @Override
                    String execute() {
                        // 格式化为 YYYY-MM-DD HH:mm:ss
                        String formattedTime = DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm:ss")
                                
                        return "当前时间是：" + formattedTime
                    }
                }
                """;

        Map<String, Object> params = new HashMap<>();
        // 如果有参数，添加到params中
        // params.put("dateStr", "2023-01-01");

        Object result = ToolExecutor.executeGroovyScript(groovyScript, params);
        System.out.println(result);
    }

}
