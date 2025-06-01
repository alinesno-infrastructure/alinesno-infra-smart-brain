package com.alinesno.infra.smart.assistant.scene.scene.productResearch.prompt;

/**
 * Prompt
 */
public interface Prompt {

    /**
     * 采集分析内容的提示
     */
    String COLLECT_ANALYSIS_PROMPT = """
            你是一名Git内容分析专家，针对于提交的git commit代码片段分析，请总结出commit信息，用于提交信息到git仓库，会有参考信息，但是不够详细
            文件路径:%s
            参考信息:%s
            类型:%s
            提交代码: %s
                        
            ##规范
            用于说明git commit的类别，只允许使用下面的标识。
            feat：新功能（feature）。
            fix/to：修复bug，可以是QA发现的BUG，也可以是研发自己发现的BUG。
            fix：产生diff并自动修复此问题。适合于一次提交直接修复问题
            to：只产生diff不自动修复此问题。适合于多次提交。最终修复问题提交时使用fix
            docs：文档（documentation）。
            style：格式（不影响代码运行的变动）。
            refactor：重构（即不是新增功能，也不是修改bug的代码变动）。
            perf：优化相关，比如提升性能、体验。
            test：增加测试。
            chore：构建过程或辅助工具的变动。
            revert：回滚到上一个版本。
            merge：代码合并。
            sync：同步主线或分支的Bug。
                        
            ##限制:
            - 请直接返回commit信息(中文)，并且按规范内容，比如以下返回内容
            """;

}
