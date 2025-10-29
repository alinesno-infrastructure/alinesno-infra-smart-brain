//package com.alinesno.infra.smart.assistant.role.llm;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.dashscope.aigc.generation.Generation;
//import com.alibaba.dashscope.aigc.generation.GenerationParam;
//import com.alibaba.dashscope.aigc.generation.GenerationResult;
//import com.alibaba.dashscope.common.Message;
//import com.alibaba.dashscope.common.ResultCallback;
//import com.alibaba.dashscope.common.Role;
//import com.alibaba.dashscope.exception.ApiException;
//import com.alibaba.dashscope.exception.InputRequiredException;
//import com.alibaba.dashscope.exception.NoApiKeyException;
//import io.reactivex.Flowable;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//
///**
// * 千问新的API接口封装，用于ReAct特定模式
// */
//@Slf4j
//@Service
//public class QianWenNewApiLLM {
//
//    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
//    private String qianWenKey;
//
//    public GenerationParam createGenerationParam(List<Message> messages) {
//        return GenerationParam.builder()
//                .apiKey(qianWenKey)
//                .model(Generation.Models.QWEN_TURBO)
//                .messages(messages)
//                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
//                .topP(0.8)
//                .build();
//    }
//
//    public GenerationParam getReasoningGen(List<Message> messages , String model) {
//        return GenerationParam.builder()
//                .apiKey(qianWenKey)
//                .model(StringUtils.hasLength(model)?model: Generation.Models.QWEN_MAX)
//                .messages(messages)
//                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
//                .topP(0.8)
//                .build();
//    }
//
//
//    public GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
//        Generation gen = new Generation();
//
//        return gen.call(param);
//    }
//
//    @SneakyThrows
//    public GenerationResult chat(List<Message> messages){
//        log.debug("messages:\r\n{}" , JSONUtil.toJsonPrettyStr(messages));
//        GenerationParam param = createGenerationParam(messages);
//        return callGenerationWithMessages(param);
//    }
//
//    public Message createMessage(Role role, String content) {
//        return Message.builder().role(role.getValue()).content(content).build();
//    }
//
//    /**
//     * 流式调用
//     * @param messages
//     * @return
//     */
//    @SneakyThrows
//    public void streamCall(List<Message> messages , ResultCallback<GenerationResult> callback) {
//        log.debug("messages:\r\n{}" , JSONUtil.toJsonPrettyStr(messages));
//        GenerationParam param = createGenerationParam(messages);
//        Generation gen = new Generation();
//        gen.streamCall(param , callback);
//    }
//
//    @SneakyThrows
//    public Flowable<GenerationResult> streamCall(List<Message> messages) {
//        log.debug("messages:\r\n{}" , JSONUtil.toJsonPrettyStr(messages));
//        GenerationParam param = createGenerationParam(messages);
//        Generation gen = new Generation();
//        return gen.streamCall(param);
//    }
//
//    @SneakyThrows
//    public Flowable<GenerationResult> streamReasoningCall(List<Message> messages) {
//        return streamReasoningCall(messages , null);
//    }
//
//    @SneakyThrows
//    public Flowable<GenerationResult> streamReasoningCall(List<Message> messages , String model) {
//        log.debug("messages:\r\n{}" , JSONUtil.toJsonPrettyStr(messages));
//        GenerationParam param = getReasoningGen(messages , model);
//        Generation gen = new Generation();
//        return gen.streamCall(param);
//    }
//}