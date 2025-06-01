package com.alinesno.infra.smart.assistant.scene.scene.productResearch.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;

/**
 * Prompt
 */
public class PromptHandle {

    /**
     * 根据gitCommit获取对应的Prompt
     * @param gitCommit
     * @return
     */
     public static String getPrompt(GitCommitInfoDto gitCommit) {

         return String.format(Prompt.COLLECT_ANALYSIS_PROMPT ,
                 gitCommit.getCommittedFiles(),
                 gitCommit.getCommitContent() ,
                 gitCommit.getCommitDiff() ,
                 gitCommit.getChangeType()) ;
    }

}
