package com.alinesno.infra.smart.assistant.role;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.role.context.RoleChainContext;
import com.alinesno.infra.smart.assistant.role.utils.YamlUtils;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 软文目录大纲设计专家
 */
@Slf4j
@Component
public class ProductArticleChapterSpecialist extends PlatformExpert {

    private static final String promptId = "35lxXaDe" ;
    private static final String STEP_01 = "PRODUCT_ARTICLE_CHAPTER_STEP_01" ;

    // 内容容器
    private List<String> resultMap ;

    @LiteflowComponent(value = "TEST_PAC_STEP_01", name="生成目录大纲结构")
    public class TEST_PAC_STEP_01 extends NodeComponent {

        @SneakyThrows
        @Override
        public void process() {
            resultMap = new ArrayList<>() ;

            // 获取上下文
            RoleChainContext roleContext = this.getContextBean(RoleChainContext.class) ;
            roleContext.setStartTime(System.currentTimeMillis());

            // 通过上下文传入
            String businessId = roleContext.getBusinessId() ;

            // 获取到参数
            Map<String , Object> params = this.getRequestData();
            brainRemoteService.chatTask(params , businessId , promptId);

            log.debug("params = {}" , params);
            pushTaskInfo("提交任务" , JSONObject.toJSONString(params));

            // >>>>>>>>>>>>>>>>>>>>>>> 获取结果并解析 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.
            int retryCount = 0 ;
            while (retryCount <= MAX_RETRY_COUNT) {

                Thread.sleep(DEFAULT_SLEEP_TIME);

                TaskContentDto content = brainRemoteService.chatContent(businessId);
                log.debug("promptId = {} , content = {}" , STEP_01 , content);

                if(content.getTaskStatus() == 2){
                    String yamlContent = content.getCodeContent().get(0).getContent() ;
                    log.debug("yamlContent = {}" , yamlContent);

                    pushTaskInfo("生效获取["+businessId+"]获取结果完成" , yamlContent);
                    resultMap.add(yamlContent) ;
                    break ;
                }

                retryCount ++ ;
                log.debug("生效获取业务[{}]次数:{}" , businessId , retryCount);
                pushTaskInfo("生效获取["+businessId+"]业务次数:" + retryCount +" , 重新获取.", JSONObject.toJSONString(params));
            }

            roleContext.setUserContent(params.get("label1").toString());
        }
    }

    // 根据情况，进行并行处理
    @LiteflowComponent(value = "TEST_PAC_STEP_02", name="保存目录大纲结构")
    public class TEST_PAC_STEP_02 extends NodeComponent {

        @SneakyThrows
        @Override
        public void process() {

            RoleChainContext roleContext = this.getContextBean(RoleChainContext.class) ;
            String businessId = roleContext.getBusinessId() ; // 获取到业务Id

            log.debug("YamlUtils.mergedYamlList(resultMap) = \r\n{}" , YamlUtils.mergedYamlList(resultMap));

            // 将聚合生成的内容保存到内容数据库中
            saveToBusinessResult(businessId , YamlUtils.mergedYamlList(resultMap)) ;

        }
    }

}