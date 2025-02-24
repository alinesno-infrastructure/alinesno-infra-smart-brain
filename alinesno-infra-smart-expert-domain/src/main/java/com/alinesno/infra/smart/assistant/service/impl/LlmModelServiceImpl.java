package com.alinesno.infra.smart.assistant.service.impl;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.VectorData;
import com.agentsflex.llm.deepseek.DeepseekLlm;
import com.agentsflex.llm.deepseek.DeepseekLlmConfig;
import com.agentsflex.llm.doubao.DoubaoLlm;
import com.agentsflex.llm.doubao.DoubaoLlmConfig;
import com.agentsflex.llm.openai.OpenAiLlm;
import com.agentsflex.llm.openai.OpenAiLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import com.alinesno.infra.smart.assistant.mapper.LlmModelMapper;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class LlmModelServiceImpl extends IBaseServiceImpl<LlmModelEntity, LlmModelMapper> implements ILlmModelService {

    @Override
    public String testLlmModel(TestLlmModelDto dto) {

        String modelType = dto.getModelType() ;
        String modelProvider = dto.getProviderCode() ; // .getModelProvider() ;
        String url = dto.getApiUrl() ; // .getUrl() ;
        String apiKey = dto.getApiKey() ;
        String modelName = dto.getModel() ;// .getModelName() ;

        if(modelType.equals(ModelTypeEnums.LARGE_LANGUAGE_MODEL.getCode())){  // 大语言模型

            if(modelProvider.equals(LlmModelProviderEnums.TONGYI_QIYWEN.getCode())){  // 千问
                QwenLlmConfig config = new QwenLlmConfig();

                config.setEndpoint(url);
                config.setApiKey(apiKey) ;
                config.setModel(modelName);

                Llm llm = new QwenLlm(config);
                String response = llm.chat("你是谁") ;

                log.debug("response: {}" , response);

                return response ;
            }else if(modelProvider.equals(LlmModelProviderEnums.DEEDSEEK.getCode())){  // Deepseek
                DeepseekLlmConfig config = new DeepseekLlmConfig();

                config.setEndpoint(url);
                config.setApiKey(apiKey) ;
                config.setModel(modelName);

                Llm llm = new DeepseekLlm(config);
                String response = llm.chat("你是谁") ;

                log.debug("response: {}" , response);

                return response ;
            }else if(modelProvider.equals(LlmModelProviderEnums.CHATGPT.getCode())) {  // chatGPT
                OpenAiLlmConfig config = new OpenAiLlmConfig();

                config.setEndpoint(url);
                config.setApiKey(apiKey) ;
                config.setModel(modelName);

                Llm llm = new OpenAiLlm(config);
                String response = llm.chat("你是谁") ;

                log.debug("response: {}" , response);

                return response ;
            }else if(modelProvider.equals(LlmModelProviderEnums.DOUBAO.getCode())) {  // Doubao
                DoubaoLlmConfig config = new DoubaoLlmConfig();

                config.setApiKey(apiKey) ;
                config.setModel(modelName) ;

                Llm llm = new DoubaoLlm(config);
                String response = llm.chat("你是谁") ;

                log.debug("response: {}" , response);

                return response ;
            }
        }else if(modelType.equals(ModelTypeEnums.VECTOR_MODEL.getCode())) {  // 向量模型
            if(modelProvider.equals(LlmModelProviderEnums.TONGYI_QIYWEN.getCode())){  // 通义千问
                QwenLlmConfig config = new QwenLlmConfig();

                config.setApiKey(apiKey) ;
                config.setModel(modelName) ;

                Llm llm = new QwenLlm(config);
                VectorData vectorData = llm.embed(Document.of("我是谁"));

                return vectorData.toString() ;
            }else if(modelProvider.equals(LlmModelProviderEnums.DOUBAO.getCode())){  // 豆包
                DoubaoLlmConfig config = new DoubaoLlmConfig();

                config.setApiKey(apiKey) ;
                config.setModel(modelName) ;

                Llm llm = new DoubaoLlm(config);
                VectorData vectorData = llm.embed(Document.of("我是谁"));

                return vectorData.toString() ;
            }else if(modelProvider.equals(LlmModelProviderEnums.CHATGPT.getCode())){  // ChatGPT
                OpenAiLlmConfig config = new OpenAiLlmConfig();

                config.setApiKey(apiKey) ;
                config.setModel(modelName) ;

                Llm llm = new OpenAiLlm(config);
                VectorData vectorData = llm.embed(Document.of("我是谁"));

                return vectorData.toString() ;
            }
        } else if(modelType.equals(ModelTypeEnums.RE_RANKING_MODEL.getCode())){  // 重排模型

        }else if(modelType.equals(ModelTypeEnums.SPEECH_RECOGNITION.getCode())){  // 语音识别

        }else if(modelType.equals(ModelTypeEnums.SPEECH_SYNTHESIS.getCode())){  // 语音合成

        }else if(modelType.equals(ModelTypeEnums.IMAGE_GENERATION.getCode())){ // 图片生成

        }

        return null;
    }
}