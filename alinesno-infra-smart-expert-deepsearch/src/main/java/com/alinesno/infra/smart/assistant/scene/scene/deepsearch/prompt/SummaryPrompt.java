package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt;

/**
 * 总结的Prompt命令
 */
public class SummaryPrompt {

    /**
     * 内容总结
     */
    public static final String DEFAULT_SUMMARY_PROMPT = """
            ##问题
            用户提供了一个复杂问题：${complex_task!""}
            ##计划
            问题被拆解成了以下的计划并被执行完毕：${planning_detail!""}
            ###执行结果
            ${answer_output!""}
            请根据的上述计划执行的结果，进行总结性的回复
            """;

    /**
     * html格式化设计
     */
    public static final String HTML_FORMAT_PROMPT = """
            你是一名网页编写和设计专家，专门针对于网页内容编写，你会收到一个目标和针对这个目标的内容，然后根据任务和完成的内容来进行网页设计，用于合理展示内容。
            
            下面是网站的基础模板：
            ```html
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title><!-- 标题 --> </title>
                <script src="https://cdn.tailwindcss.com"></script>
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" rel="stylesheet">
                   <script>
                    tailwind.config = {
                        theme: {
                            extend: {
                                colors: {
                                    primary: '#3b5998',
                                    secondary: '#00B42A',
                                    accent: '#722ED1',
                                    dark: '#1D2129',
                                    light: '#F2F3F5',
                                    'light-bg': '#F2F3F5'
                                },
                                fontFamily: {
                                    inter: ['Inter', 'system-ui', 'sans-serif'],
                                },
                                boxShadow: {
                                    'card': '0 10px 30px -5px rgba(0, 0, 0, 0.1)',
                                    'hover': '0 20px 40px -5px rgba(22, 93, 255, 0.15)',
                                }
                            },
                        }
                    }
                </script>
            </head>
            <body>
            </body>
            </html>
            ```
                        
            请根据模板和内容，编写出对应的网页界面排版，要求：
            1. 界面是单界面显示，界面显示要排版布局美观合理，符合主题内容，单界面显示不需要导航栏
            2. 如果内容有iframe，则需要直接嵌入网页显示
            3. 直接返回html内容，不要返回不相关内容，返回格式为
               ```html
               ```
               
            目标要求:${complex_task!""}
            目标内容:${summary_content!""}
            """;
}