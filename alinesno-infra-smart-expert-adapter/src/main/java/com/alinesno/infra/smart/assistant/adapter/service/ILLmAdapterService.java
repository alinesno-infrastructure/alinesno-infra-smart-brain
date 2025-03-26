package com.alinesno.infra.smart.assistant.adapter.service;

import com.agentsflex.core.image.ImageConfig;
import com.agentsflex.core.image.ImageModel;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.ocr.OcrConfig;
import com.agentsflex.core.ocr.OcrModel;
import com.agentsflex.core.ocr.OcrResponse;
import com.agentsflex.core.reranker.ReRanker;
import com.agentsflex.core.reranker.ReRankerConfig;
import com.agentsflex.core.speech.SpeechConfig;
import com.agentsflex.core.speech.SpeechModel;
import com.agentsflex.core.vision.BaseVision;

/**
 * LLmAdapter 大模型适配器
 */
public interface ILLmAdapterService {

    /**
     * 根据不同的类返回对应的Llm对象
     *
     * @param type
     * @return
     */
    Llm getLlm(String type, LlmConfig config);

    /**
     * 根据不同的类返回对应的ImageModel对象
     *
     * @param type
     * @param config
     * @return
     */
    ImageModel getImageModel(String type, ImageConfig config);

    /**
     * 重排序模型
     *
     * @param config
     * @return
     */
    ReRanker reranker(String type , ReRankerConfig config);

    /**
     * 语音模型
     * @param type
     * @param config
     * @return
     */
    SpeechModel speechModel(String type ,  SpeechConfig config);

    /**
     * 获取OCR模型
     */
    OcrModel ocrModel(String type , OcrConfig config);

    /**
     * 获取视觉模型
     */
    Llm visionModel(String type , LlmConfig config)  ;

}
