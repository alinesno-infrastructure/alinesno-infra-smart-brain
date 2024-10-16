package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.ProcessInstanceEntity;
import com.alinesno.infra.smart.assistant.mapper.ProcessInstanceMapper;
import com.alinesno.infra.smart.assistant.service.IProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessInstanceServiceImpl extends IBaseServiceImpl<ProcessInstanceEntity, ProcessInstanceMapper> implements IProcessInstanceService {

//    /**
//     * 工作空间路径
//     */
//    @Value("${alinesno.data.scheduler.workspacePath:#{systemProperties['java.io.tmpdir']}}")
//    private String workspacePath;
//
//    /**
//     * 读取日志文件最大长度，单位为字节，默认为2048KB
//     */
//    private static final int MAX_READ_LENGTH = 2048 * 1024; // 2048KB in bytes
//
//    /**
//     * 读取日志文件，并返回日志文本以及当前读取的下标
//     * @param processInstanceId 流程实例ID
//     * @param start 起始偏移量（可选）
//     * @return 返回一个包含日志文本、当前读取的下标和是否还有更多日志内容的对象
//     */
//    @SneakyThrows
//    @Override
//    public LogReadResultDto readLog(long processInstanceId, String start) {
//        ProcessInstanceEntity entity = this.getById(processInstanceId);
//        String workspace = workspacePath + File.separator + entity.getWorkspace();
//
//        File logFile = new File(workspace, PipeConstants.RUNNING_LOGGER);
//        String logText = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
//
//        // 解析请求参数，计算文本起始偏移量
//        long offset = StringUtils.isBlank(start) ? 0 : Long.parseLong(start);
//
//        // 计算本次读取的最大长度
//        int maxLength = Math.min(MAX_READ_LENGTH, logText.length() - (int) offset);
//
//        // 截取出相应部分的日志文本
//        String logSnippet = logText.substring((int) offset, (int) offset + maxLength);
//
//        // 判断是否还有更多的日志内容
//        boolean hasMoreLog = offset < logText.length()  ;
//
//        // 创建并返回结果对象
//        return new LogReadResultDto(logSnippet, (int) (offset + maxLength), hasMoreLog);
//    }

}
