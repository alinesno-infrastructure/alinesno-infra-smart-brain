package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.role.context.RoleChainContext;
import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 软文目录大纲设计专家
 */
@Slf4j
@Component
public class ProductArticleChapterContentSpecialist extends PlatformExpert {

    private static final String promptId = "OyezG1Hk" ;
    private static final String STEP_01 = "PRODUCT_ARTICLE_CHAPTER_CONTENT_STEP_01" ;

    // 内容容器
    private List<String> resultMap ;

    @LiteflowComponent(value = "TEST_PACC_STEP_01", name="生成目录大纲结构")
    public class TEST_PACC_STEP_01 extends NodeComponent {

        @SneakyThrows
        @Override
        public void process() {
            resultMap = new ArrayList<>() ;

            // 获取上下文
            RoleChainContext roleContext = this.getContextBean(RoleChainContext.class) ;
            roleContext.setStartTime(System.currentTimeMillis());

            // 通过上下文传入
            String businessId = roleContext.getBusinessId() ;

            List<String> businessIdList = new ArrayList<>() ;

            // 获取到解析内容
            ArticleBean articleBean = getChapterArticleBean(roleContext.getAssistantYamlContent()) ;

            for(ArticleBean.Article article : articleBean.getArticle()){
                List<ArticleBean.Chapter> chapters = article.getChapters() ;

                for(ArticleBean.Chapter chapter : chapters){
                    String bId = generatorId() ;

                    Map<String , Object> paramsLabel = this.getRequestData();
                    paramsLabel.put("label1" , chapter.getChapter()) ;
                    paramsLabel.put("label2" , roleContext.getAssistantYamlContent()) ;

                    brainRemoteService.chatTask(paramsLabel , bId , promptId);
                    log.debug("params = {}" , paramsLabel);

                    if(chapter.getSubChapters() != null && !chapter.getSubChapters().isEmpty()){

                        for(ArticleBean.SubChapter subChapter : chapter.getSubChapters()){

                            String subBId = generatorId() ;
                            Map<String , Object> subParamsLabel = this.getRequestData();
                            subParamsLabel.put("label1" , subChapter.getSub()) ;
                            subParamsLabel.put("label2" , roleContext.getAssistantYamlContent()) ;

                            brainRemoteService.chatTask(subParamsLabel , subBId , promptId);
                            log.debug("params = {}" , paramsLabel);

                            businessIdList.add(subBId) ;
                        }
                    }

                    businessIdList.add(bId) ;
                }

            }

            // >>>>>>>>>>>>>>>>>>>>>>> 获取结果并解析 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.
            int retryCount = 0 ;
            while (retryCount <= MAX_RETRY_COUNT) {

                Thread.sleep(DEFAULT_SLEEP_TIME);
                boolean isFinish = true ;

                for(String bId : businessIdList){
                    TaskContentDto content = brainRemoteService.chatContent(bId);
                    if(content.getTaskStatus() != 2){
                        isFinish = false ;
                        break ;
                    }
                }

                if(isFinish){
                    for(String bId : businessIdList) {
                        TaskContentDto content = brainRemoteService.chatContent(bId);
                        String yamlContent = content.getCodeContent().get(0).getContent();
                        resultMap.add(yamlContent) ;
                    }
                    break ;
                }

                retryCount ++ ;
                log.debug("生效获取业务[{}]次数:{}" , businessId , retryCount);
            }

        }
    }

    // 根据情况，进行并行处理
    @LiteflowComponent(value = "TEST_PACC_STEP_02", name="保存目录大纲结构")
    public class TEST_PACC_STEP_02 extends NodeComponent {

        @SneakyThrows
        @Override
        public void process() {

            RoleChainContext roleContext = this.getContextBean(RoleChainContext.class) ;
            String businessId = roleContext.getBusinessId() ; // 获取到业务Id

            StringBuilder content = new StringBuilder() ;

            if(!resultMap.isEmpty()){
                int size = resultMap.size() ;
                for(int i = 0 ; i < resultMap.size() ; i ++){
                    String str = resultMap.get(i) ;
                    content.append(str) ;

                    if(size > 1 && i < size - 1){
                        content.append("\r\n");
                        content.append("\r\n");
                    }
                }
            }

            log.debug("YamlUtils.mergedYamlList(resultMap) = \r\n{}" , content.toString());

            // 将聚合生成的内容保存到内容数据库中
            saveToBusinessResult(businessId , content.toString()) ;
        }
    }

    @SneakyThrows
    @NotNull
    private static ArticleBean getChapterArticleBean(String article) {
        return YAMLMapper.fromYAML(article, ArticleBean.class);
    }

    @Data
    public static class ArticleBean {
        private List<Article> article;

        @Data
        public static class Article {
            private String title;
            private String summary;
            private List<Chapter> chapters;
        }

        @Data
        public static class Chapter {
            private String chapter;
            private List<SubChapter> subChapters;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubChapter {
            private String sub;
        }
    }

}