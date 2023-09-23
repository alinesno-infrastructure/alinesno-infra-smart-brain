package com.alinesno.infra.smart.brain.api.provider;

import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class BrainTaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IGenerateTaskService generateTaskService;

    @BeforeEach
    public void setup() {
        // 初始化Mockito注解
        MockitoAnnotations.openMocks(this);
        // 初始化MockMvc对象
        mockMvc = MockMvcBuilders.standaloneSetup(new BrainTaskController(generateTaskService)).build();
    }

    @ParameterizedTest
    @MethodSource("provideBrainTaskDto")
    @DisplayName("提交任务 - 成功")
    public void testCommitTask_Success(BrainTaskDto dto) throws Exception {

        log.debug(asJsonString(dto));

        // 发起POST请求并断言状态码为200
        mockMvc.perform(MockMvcRequestBuilders.post("/commitTask")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // 验证generateTaskService的commitTask方法被调用1次
        verify(generateTaskService, times(1)).commitTask(dto);
    }

    @ParameterizedTest
    @MethodSource("provideBrainTaskDto")
    @DisplayName("提交任务并保存到CMS - 成功")
    public void testCommitTaskToCms_Success(BrainTaskDto dto) throws Exception {
        // 发起POST请求并断言状态码为200
        mockMvc.perform(MockMvcRequestBuilders.post("/commitTaskToCms")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // 验证generateTaskService的commitTaskToCms方法被调用1次
        verify(generateTaskService, times(1)).commitTaskToCms(dto);
    }

    // 提供测试数据
    private static Stream<BrainTaskDto> provideBrainTaskDto() {
        return Stream.of(
                new BrainTaskDto("123456", "789", "系统设定内容1", "用户信息内容1"), // 所有字段都不为空
                new BrainTaskDto("234567", "890", "系统设定内容2", "用户信息内容2"), // 所有字段都不为空
                new BrainTaskDto(null, "789", "系统设定内容3", "用户信息内容3"), // 业务ID为空
                new BrainTaskDto("345678", null, "系统设定内容4", "用户信息内容4"), // GPT角色ID为空，但系统设定内容和用户信息内容都不为空
                new BrainTaskDto("456789", null, null, null) // GPT角色ID、系统设定内容和用户信息内容都为空
        );
    }

    // 辅助方法，将对象转换为JSON字符串
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
