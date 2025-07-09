package com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.AiMessage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.llm.ModelAdapterLLM;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.enums.ExamineeExamEnums;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.prompt.ExamPagerPromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamScoreService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.scene.entity.ExamScoreEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ExamPagerFormatMessageTool {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IExamScoreService examScoreService;

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private ModelAdapterLLM modelAdapterLLM ;

    @Autowired
    private IAgentSceneService agentSceneService ;

    @SneakyThrows
    public void handleChapterMessage(WorkflowExecutionDto genContent, MessageTaskInfo taskInfo) {

        taskInfo.setTraceBusId(IdUtil.getSnowflakeNextId());

        String formatContent = """
        ##你是一名专业的题目处理专家，针对需求对题目及类型进行JSON格式化。
                  
        ### **JSON格式规范描述**
        #### **1. 基础结构**
        - 整体为**数组**，每个元素代表一个**题目对象**
        - 每个题目对象包含以下**核心字段**：
          ```json
          {
            "id": "number",          // 题目唯一标识（必填）
            "type": "string",        // 题型标识（必填）
            "question": "string",    // 问题文本（必填）
            "isRequired": "boolean", // 是否必答（必填）
            "score": "number",        // 题目分值（可选）
            "assessmentContent" : "string" , // 考核内容(必填)
            "answerAnalysis": "string" // 答案分析
          }
          ```
                 
        #### **2. 题型扩展字段**
        根据`type`不同，需补充特定字段：
                 
        | 题型(type)       | 特有字段                                                                 |
        |-------------------|--------------------------------------------------------------------------|
        | `radio`           | `answers: [{label, content, isCorrect, value}]`                         |
        | `checkbox`        | `answers: [{label, content, isCorrect, value}]`                         |
        | `dropdown`        | `answers: [{label, content, isCorrect, value}]`（首项常为"请选择"占位） |
        | `image-radio`     | `answers: [{label, content, img, isCorrect, value}]`                   |
        | `image-checkbox`  | `answers: [{label, content, img, isCorrect, value}]`                   |
        | `single-line`     | `placeholder: "string"`, `correctAnswer: "string"`                     |
        | `multi-fill`      | `blanks: [{index, correctAnswer}]`                                     |
        | `multi-line`      | `placeholder: "string"`, `wordLimit: number`                           |
        | `image-file`      | `fileTypes: "string"`, `maxSize: "string"`                             |
        | `datetime`        | 无额外字段                                                             |
        | `location`        | `answers: [{label, content, isCorrect, value}]`                        |
        | `description`     | `content: "html-string"`（富文本描述）                                 |
                 
        #### **3. 通用可选字段**
        - `mediaFiles`: 数组，包含媒体文件信息（图片/视频等）
          ```json
          {
            "name": "string",
            "type": "string",
            "url": "string",
            "previewUrl": "string"
          }
          ```
        - `order`: 题目排序序号（number）
        - `placeholder`: 输入框提示文本（填空/文本题型）
                 
        #### **4. 答案结构规范**
        - 选择题答案项：
          ```json
          {
            "label": "string",    // 选项标签（如A/B/C/D）
            "content": "string",  // 选项文本
            "isCorrect": "boolean",
            "value": "string"     // 提交时的值
          }
          ```
                 
        #### **5. 示例生成规则**
        ```json
        {
          "id": "按顺序生成",
          "type": "从上述题型中选择",
          "question": "自定义问题文本",
          "isRequired": "随机true/false",
          // 根据题型补充对应字段...
        }
        ```
        ### **使用说明**
        1. 根据`type`匹配对应的字段结构
        2. 媒体文件(`mediaFiles`)和`order`字段可省略
        3. 选择题必须包含`answers`数组，填空题需有`correctAnswer`或`blanks`
        4. 描述类题目(`description`)用HTML格式的`content`
                       
        ##生成示例json：
        比如某医学数据挖掘与实践(R或Python)》1学期考试试卷A卷题目。
        ```json
        [{"id":1,"type":"radio","question":"假设有100个学生，其中90个学生回答了问卷调查，另外10个学生没有回答问卷调查，那么有效回复率是？","assessmentContent":"问卷调查回复率计算","answerAnalysis":"有效回复率计算公式为：回答问卷的学生数 ÷ 总样本数 × 100%，本题中为90 ÷ 100 × 100% = 90%","mediaFiles":[{"name":"5fd34deecbe349ed91bd8cde2af9cfd6~tplv-13w3uml6bg-resize_800_320.png","type":"image/png","url":"https://picsum.photos/id/242/200/100","previewUrl":"blob:http://localhost:8080/f213353c-b84d-4331-ba9a-3f031b94a136"},{"name":"897c06742e8f445796b6be8729af0a84~tplv-13w3uml6bg-resize_800_320.jpg","type":"image/jpeg","url":"https://picsum.photos/id/242/200/100","previewUrl":"blob:http://localhost:8080/13c8d335-20ae-4f14-9c95-8cb254fd4f09"}],"isRequired":true,"score":1,"order":1,"answers":[{"label":"A","content":"90%","isCorrect":true,"value":"90%"},{"label":"B","content":"10%","isCorrect":false,"value":"10%"},{"label":"C","content":"80%","isCorrect":false,"value":"80%"},{"label":"D","content":"100%","isCorrect":false,"value":"100%"}]},{"id":2,"type":"checkbox","question":"以下哪些属于Python数据挖掘常用库？","assessmentContent":"Python数据挖掘工具库识别","answerAnalysis":"NumPy用于数值计算，Pandas用于数据处理，Matplotlib用于数据可视化，这三个是数据挖掘基础库。TensorFlow主要用于深度学习，不属于常规数据挖掘工具库。","mediaFiles":[{"name":"5fd34deecbe349ed91bd8cde2af9cfd6~tplv-13w3uml6bg-resize_800_320.png","type":"image/png","url":"https://picsum.photos/id/242/200/100","previewUrl":"blob:http://localhost:8080/f213353c-b84d-4331-ba9a-3f031b94a136"},{"name":"897c06742e8f445796b6be8729af0a84~tplv-13w3uml6bg-resize_800_320.jpg","type":"image/jpeg","url":"https://picsum.photos/id/242/200/100","previewUrl":"blob:http://localhost:8080/13c8d335-20ae-4f14-9c95-8cb254fd4f09"}],"isRequired":false,"score":2,"order":2,"answers":[{"label":"A","content":"NumPy","isCorrect":true,"value":"numpy"},{"label":"B","content":"Pandas","isCorrect":true,"value":"pandas"},{"label":"C","content":"Matplotlib","isCorrect":true,"value":"matplotlib"},{"label":"D","content":"TensorFlow","isCorrect":false,"value":"tensorflow"}]},{"id":3,"type":"radio","question":"假设有100个学生，其中90个学生回答了问卷调查，另外10个学生没有回答问卷调查，那么有效回复率是？","assessmentContent":"问卷调查回复率计算","answerAnalysis":"有效回复率计算公式为：回答问卷的学生数 ÷ 总样本数 × 100%，本题中为90 ÷ 100 × 100% = 90%","isRequired":true,"score":1,"order":3,"answers":[{"label":"A","content":"90%","isCorrect":true,"value":"90%"},{"label":"B","content":"10%","isCorrect":false,"value":"10%"},{"label":"C","content":"80%","isCorrect":false,"value":"80%"},{"label":"D","content":"100%","isCorrect":false,"value":"100%"}]},{"id":4,"type":"checkbox","question":"以下哪些属于Python数据挖掘常用库？","assessmentContent":"Python数据挖掘工具库识别","answerAnalysis":"NumPy用于数值计算，Pandas用于数据处理，Matplotlib用于数据可视化，这三个是数据挖掘基础库。TensorFlow主要用于深度学习，不属于常规数据挖掘工具库。","isRequired":true,"score":2,"order":4,"answers":[{"label":"A","content":"NumPy","isCorrect":true,"value":"numpy"},{"label":"B","content":"Pandas","isCorrect":true,"value":"pandas"},{"label":"C","content":"Matplotlib","isCorrect":true,"value":"matplotlib"},{"label":"D","content":"TensorFlow","isCorrect":false,"value":"tensorflow"}]},{"id":5,"type":"dropdown","question":"在Python中，用于处理缺失值的常用方法是？","assessmentContent":"Pandas缺失值处理方法","answerAnalysis":"dropna()用于直接删除包含缺失值的行或列，fillna()用于填充缺失值，replace()用于替换特定值。题目问的是处理缺失值的常用方法，应选择dropna()。","isRequired":true,"score":1,"order":5,"answers":[{"label":"","content":"请选择","isCorrect":false,"value":""},{"label":"","content":"dropna()","isCorrect":true,"value":"dropna"},{"label":"","content":"fillna()","isCorrect":false,"value":"fillna"},{"label":"","content":"replace()","isCorrect":false,"value":"replace"},{"label":"","content":"以上都是","isCorrect":false,"value":"all"}]},{"id":6,"type":"image-radio","question":"请选择表示决策树的图形：","assessmentContent":"机器学习算法图形识别","answerAnalysis":"决策树的图形特征是具有明确的树状分支结构，从根节点开始通过特征判断分裂到叶节点，与其他算法图示有显著区别。","isRequired":true,"score":1,"order":6,"answers":[{"label":"A","content":"决策树：一种树状结构模型，通过节点分裂进行特征判断，最终到达叶节点形成分类/回归结果","img":"https://picsum.photos/id/237/200/100","isCorrect":true,"value":"tree"},{"label":"B","content":"神经网络：模仿生物神经网络的层级结构，包含输入层、隐藏层和输出层，适合处理复杂非线性关系","img":"https://picsum.photos/id/238/200/100","isCorrect":false,"value":"neural"},{"label":"C","content":"聚类分析：无监督学习算法，将相似数据点分组，图中展示的是多维数据在二维平面的投影分布","img":"https://picsum.photos/id/239/200/100","isCorrect":false,"value":"cluster"},{"label":"D","content":"回归分析：展示自变量与因变量的线性/非线性关系，图中包含散点图和拟合的趋势线","img":"https://picsum.photos/id/240/200/100","isCorrect":false,"value":"regression"}]},{"id":7,"type":"image-checkbox","question":"请选择所有属于监督学习算法的图示：","assessmentContent":"监督学习算法识别","answerAnalysis":"决策树、SVM和随机森林都是典型的监督学习算法，需要有标注数据训练。K-means是无监督聚类算法，不需要标注数据。","isRequired":true,"score":2,"order":7,"answers":[{"label":"A","content":"决策树：经典监督学习算法，图示展示树形结构及'if-then'规则的分支过程","img":"https://picsum.photos/id/241/200/150","isCorrect":true,"value":"tree"},{"label":"B","content":"K-means：典型无监督聚类算法，图示展示迭代过程中质心移动和簇形成","img":"https://picsum.photos/id/242/200/150","isCorrect":false,"value":"kmeans"},{"label":"C","content":"SVM（支持向量机）：监督学习分类器，图示展示最优超平面及边缘支持向量","img":"https://picsum.photos/id/243/200/150","isCorrect":true,"value":"svm"},{"label":"D","content":"随机森林：集成学习算法，图示展示多棵决策树的并行训练及投票机制","img":"https://picsum.photos/id/244/200/150","isCorrect":true,"value":"randomforest"}]},{"id":8,"type":"single-line","question":"请写出Python中用于计算相关系数的函数名称：","assessmentContent":"Pandas数据分析函数掌握","answerAnalysis":"在Pandas中，DataFrame.corr()方法用于计算列与列之间的相关系数矩阵，是数据分析中常用的统计方法。","isRequired":true,"score":1,"order":8,"placeholder":"输入函数名称","correctAnswer":"corr()"},{"id":9,"type":"multi-fill","question":"完成以下Python代码，实现读取CSV文件并显示前5行","assessmentContent":"Pandas基础操作能力","answerAnalysis":"pd.read_csv()是Pandas读取CSV文件的标准方法，而head()方法默认显示DataFrame的前5行数据，这两个函数组合是数据探索的常用操作。","isRequired":true,"score":2,"order":9,"blanks":[{"index":1,"correctAnswer":"read_csv"},{"index":2,"correctAnswer":"head"}]},{"id":10,"type":"multi-line","question":"简要描述K近邻(KNN)算法的基本原理和适用场景：","assessmentContent":"机器学习算法理解","answerAnalysis":"KNN算法的核心思想是'物以类聚'，通过计算待分类样本与训练样本的距离，选取最近的K个邻居，根据这些邻居的类别投票决定分类结果。适用于样本分布均匀、特征维度不高且需要解释性的场景。","isRequired":true,"score":3,"order":10,"placeholder":"请输入至少50字的回答","wordLimit":200},{"id":11,"type":"image-file","question":"请上传你完成的数据分析可视化图表：","assessmentContent":"数据可视化能力考核","answerAnalysis":"优秀的数据可视化应包含清晰的标题、坐标轴标签、图例说明，并能有效传达数据洞察。常见的可视化类型包括折线图、柱状图、散点图和热力图等。","isRequired":false,"score":2,"order":11,"fileTypes":"image/*","maxSize":"5MB"},{"id":12,"type":"datetime","question":"请输入你预计完成数据分析项目的日期：","assessmentContent":"项目时间规划能力","answerAnalysis":"合理的时间预估应考虑数据清洗、分析、可视化和报告撰写等各个环节所需时间，同时预留缓冲时间应对意外情况。","isRequired":true,"score":1,"order":12},{"id":13,"type":"location","question":"假设你需要收集患者地理位置数据用于疾病传播分析，你会使用哪种地理坐标系统？","assessmentContent":"地理信息系统基础知识","answerAnalysis":"WGS84是国际通用的地理坐标系统，GPS设备默认使用此系统，具有全球一致性。GCJ-02和BD-09是中国特有的加密坐标系，UTM是局部投影坐标系。","isRequired":true,"score":1,"order":13,"answers":[{"label":"A","content":"WGS 84","isCorrect":true,"value":"wgs84"},{"label":"B","content":"GCJ-02","isCorrect":false,"value":"gcj02"},{"label":"C","content":"BD-09","isCorrect":false,"value":"bd09"},{"label":"D","content":"UTM","isCorrect":false,"value":"utm"}]},{"id":19,"type":"description","question":"数据挖掘在医学领域的应用：","assessmentContent":"数据挖掘应用场景理解","answerAnalysis":"医学数据挖掘可帮助发现疾病模式、优化治疗方案、预测流行病趋势，但需特别注意患者隐私保护和数据伦理问题。","content":"<p>数据挖掘技术在医学领域有着广泛的应用前景，包括但不限于以下几个方面：</p><ul><li>疾病预测与诊断辅助</li><li>医疗质量评估与改进</li><li>药物疗效分析与个性化医疗</li><li>医疗资源优化配置</li><li>医学影像分析与识别</li></ul><p>请选择其中一个方向，简要阐述数据挖掘技术在该领域的具体应用方式和价值。</p>","isRequired":false,"score":0,"order":14}]
        ```
        
        ##规则：
          1-最终请你下面的格式进行直接输出，不要输出其他信息，格式：
           ```json
           ```
        """ + "##需求=" + taskInfo.getText() + "\n" +
            " ##需要格式化的题目="  + taskInfo.getFullContent()
                ;

        taskInfo.setText(formatContent) ;
        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(taskInfo.getRoleId(), SceneEnum.EXAM_AGENT.getSceneInfo().getId()) ;

        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getLlmModelId()) ;

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey()) ;
        llmConfig.setApiSecret(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode() ;

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        IndustryRoleEntity role = roleService.getById(taskInfo.getRoleId());
        long messageId = IdUtil.getSnowflakeNextId() ;
        CompletableFuture<AiMessage> future = modelAdapterLLM.getSingleAiChatResultAsync(llm, role, taskInfo.getText() , taskInfo , messageId) ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());

        genContent.setGenContent(message.getFullContent());
        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(message.getFullContent()));
    }

    /**
     * 成绩结果评分，主要分两步:
     * 1. 先AI角色阅卷
     * @param examScore
     */
    @Async
    public void markExamScore(ExamScoreEntity examScore, IndustryRoleEntity industryRole, ExamPagerSceneEntity examPagerScene) {

        // 更新状态为阅卷中
        examScore.setExamStatus(ExamineeExamEnums.REVIEW.getCode());
        examScoreService.updateById(examScore);

        try {

            JSONArray questions = JSONArray.parseArray(examScore.getQuestions()); // 传入题目JSON数组
            JSONObject answers = JSONObject.parseObject(examScore.getAnswers());   // 传入答案JSON对象

            String markdownAnswer = QuestionAnswerFormatter.formatToMarkdown(questions , answers) ;
            String markdownAnswerPrompt = ExamPagerPromptHandle.generatorMarkPrompt(markdownAnswer) ;

            Long taskId = examScore.getId();
            Long channelStreamId = examScore.getId();

            // 构建任务信息
            MessageTaskInfo taskInfo = new MessageTaskInfo() ;

            taskInfo.setRoleId(industryRole.getId());
            taskInfo.setChannelStreamId(String.valueOf(channelStreamId)) ;
            taskInfo.setChannelId(examScore.getSceneId());
            taskInfo.setSceneId(examScore.getSceneId());
            taskInfo.setText(markdownAnswerPrompt);

            // 调用角色服务生成内容
            WorkflowExecutionDto genContent = roleService.runRoleAgent(taskInfo);
            log.info("角色服务调用完成，taskId: {}", taskId);
            log.info("角色服务输出内容：{}", taskInfo.getFullContent());

            List<CodeContent> codeContent = CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()) ;

            // 随机生成分数 (实际应该根据评分逻辑计算)
            long score = (long) (Math.random() * 100);
            boolean isPass = score >= 60;

            // 更新成绩信息
            examScore.setScore(score);
            examScore.setIsPass(isPass ? 1 : 0);
            examScore.setReviewTime(LocalDateTime.now());
            examScore.setReviewerId(1L); // 假设系统自动阅卷
            examScore.setReviewerName("系统自动阅卷");
            examScore.setAnalysisResult(codeContent.get(0).getContent());
            examScore.setExamStatus(ExamineeExamEnums.REVIEW_END.getCode());

            examScoreService.updateById(examScore);

        } catch (Exception e) {
            log.error("阅卷出错", e);
            // 出错时更新状态
            examScore.setExamStatus(ExamineeExamEnums.CANCELED.getCode());
            examScoreService.updateById(examScore);
        }
    }

}
