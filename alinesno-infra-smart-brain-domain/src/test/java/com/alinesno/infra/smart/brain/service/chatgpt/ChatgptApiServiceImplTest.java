package com.alinesno.infra.smart.brain.service.chatgpt;

import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.brain.api.dto.PromptMessage;
import com.alinesno.infra.smart.brain.api.dto.RequestData;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.event.TaskEvent;
import com.google.gson.Gson;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatChoice;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import dev.ai4j.openai4j.Model;
import lombok.SneakyThrows;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ChatgptApiServiceImplTest {

    @Test
    void getClient() {
    }

    @Test
    void session() {
    }

    @Test
    void chat() {
    }

    @Test
    void config() {
    }

    @Test
    void chatProcess() {
    }

    @Test
    void verify() {
    }

}