//package com.alinesno.infra.base.search.job;
//
//import cn.hutool.core.thread.ExecutorBuilder;
//import com.alinesno.infra.base.search.api.DataProcessingDto;
//import com.alinesno.infra.base.search.entity.DatasetKnowledgeEntity;
//import com.alinesno.infra.base.search.entity.DatasetParseLogEntity;
//import com.alinesno.infra.base.search.enums.AutoImportStatusEnums;
//import com.alinesno.infra.base.search.enums.DatasetParseStatusEnum;
//import com.alinesno.infra.base.search.enums.DocumentStatusEnums;
//import com.alinesno.infra.base.search.service.IDatasetKnowledgeService;
//import com.alinesno.infra.base.search.service.IDatasetParseLogService;
//import com.alinesno.infra.common.core.cache.RedisUtils;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.*;
//
///**
// * 数据集任务解析类。
// * 该类负责在Spring Boot应用启动完成后，每30秒从IDatasetKnowledgeService搜索未导入的数据，
// * 并使用Hutool线程框架进行多线程导入，通过调用dataUploadToVectorDataset方法将数据集导入向量化，
// * 同时在任务执行过程中记录详细的日志信息到数据库中，便于后续对任务执行情况进行监控和分析。
// */
//@Slf4j
//@Component
//@EnableScheduling
//public class DatasetParseJob implements ApplicationListener<ApplicationReadyEvent> {
//
//    public static final String CUSTOM_SPLIT_SYMBOL = "\n;======;==SPLIT==";
//    public static final int IDEAL_CHUNK_LENGTH = 512;
//    public static final String PROCESSING_METHOD = "direct_segmentation";
//    public static final String PROCESSING_PARAM = "自动";
//    public static final String LOCK_KEY = "dataset_parse_batch_lock";
//
//    private static final int CORE_POOL_SIZE = 3;
//    private static final int MAX_POOL_SIZE = 3;
//    private static final long KEEP_ALIVE_TIME = 45*1000L;
//    private static final int QUEUE_CAPACITY = 200; // 队列容量
//
//    private final IDatasetKnowledgeService datasetKnowledgeService;
//    private final IDatasetParseLogService datasetParseLogService;
//    @Autowired
//    private TaskScheduler taskScheduler;
//    private ExecutorService executorService;
//
//    // 创建线程池
//    ThreadPoolExecutor executor = new ThreadPoolExecutor(
//            CORE_POOL_SIZE,
//            MAX_POOL_SIZE,
//            KEEP_ALIVE_TIME,
//            TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<>(QUEUE_CAPACITY)
//    );
//
//
//    /**
//     * 构造函数，注入数据集知识服务和数据集解析日志服务。
//     *
//     * @param datasetKnowledgeService 数据集知识服务，用于搜索未导入的数据和进行数据导入操作
//     * @param datasetParseLogService  数据集解析日志服务，用于操作数据集解析日志记录
//     */
//    public DatasetParseJob(IDatasetKnowledgeService datasetKnowledgeService, IDatasetParseLogService datasetParseLogService) {
//        this.datasetKnowledgeService = datasetKnowledgeService;
//        this.datasetParseLogService = datasetParseLogService;
//    }
//
//    /**
//     * 初始化线程池的方法。
//     * 使用Hutool的ExecutorBuilder来创建线程池。
//     */
//    @PostConstruct
//    public void init() {
//        executorService = ExecutorBuilder.create()
//                .setCorePoolSize(3)
//                .setMaxPoolSize(5)
//                .build();
//    }
//
//    @Override
//    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
//        log.debug("搜索导入定时任务启动.");
//        long initialDelay = 0;
//        long period = 60; // 每隔x秒执行一次
//        taskScheduler.scheduleAtFixedRate(this::parseDataset, Instant.now().plusMillis(initialDelay), Duration.ofSeconds(period));
//    }
//
//    /**
//     * 定时调度数据集导入任务的方法。
//     * 该方法会每隔30秒被调用一次，从IDatasetKnowledgeService搜索出状态为未导入的数据，
//     * 并使用Hutool线程框架进行多线程导入，通过调用dataUploadToVectorDataset方法将数据集导入向量化。
//     */
//    public void parseDataset() {
//        log.debug("正在搜索需要自动导入但是未导入的数据集...");
//
//        // 尝试获取Redis锁
//        boolean locked = RedisUtils.setObjectIfAbsent(LOCK_KEY, "locked", Duration.ofSeconds(60));
//        if (!locked) {
//            log.info("上一个批次还未处理完成，当前批次等待中...");
//            return;
//        }
//
//            LambdaQueryWrapper<DatasetKnowledgeEntity> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(DatasetKnowledgeEntity::getStatus, DocumentStatusEnums.IMPORT_NOT_COMPLETED.getCode());
//            queryWrapper.eq(DatasetKnowledgeEntity::getHasAutoImport, AutoImportStatusEnums.AUTO_IMPORT.getCode());
//
//            queryWrapper.last("LIMIT 100"); // 限制最大每次100条
//            List<DatasetKnowledgeEntity> unimportedData = datasetKnowledgeService.list(queryWrapper);
//
//            log.debug("搜索到未导入的数据集数量：{}", unimportedData.size());
//
//            // 创建 CompletableFuture 列表
//            List<CompletableFuture<Void>> futures = unimportedData.stream()
//                    .map(data -> CompletableFuture.runAsync(() -> processData(data), executor))
//                    .toList();
//
//            // 当所有任务完成时触发通知
//            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            allFutures.thenRun(() -> {
//                log.info("所有未导入的数据集处理完成");
//                RedisUtils.deleteObject(LOCK_KEY);
//            });
//
//    }
//
//    private void processData(DatasetKnowledgeEntity data) {
//        DataProcessingDto dto = new DataProcessingDto();
//        dto.setDatasetId(data.getDatasetId());
//
//        dto.setProcessingMethod(StringUtils.isNotBlank(data.getProcessingMethod()) ? data.getProcessingMethod() : PROCESSING_METHOD);
//        dto.setProcessingParam(StringUtils.isNotBlank(data.getProcessingParam()) ? data.getProcessingParam() : PROCESSING_PARAM);
//        dto.setIdealChunkLength(StringUtils.isNotBlank(data.getIdealChunk()) ? data.getIdealChunk() : String.valueOf(IDEAL_CHUNK_LENGTH));
//        dto.setCustomSplitSymbol(StringUtils.isNotBlank(data.getCustomSeparator()) ? data.getCustomSeparator() : CUSTOM_SPLIT_SYMBOL);
//
//        DatasetParseLogEntity logEntity = startLogging();
//        try {
//            log.info("正在线程 {} 上导入数据集: {}", Thread.currentThread().getName(), data.getDocumentName());
//            datasetKnowledgeService.dataUploadToVectorDataset(dto);
//            updateLogging(logEntity, DatasetParseStatusEnum.COMPLETED);
//        } catch (Exception e) {
//            log.error("导入数据集时发生错误: {}", data.getDocumentName(), e);
//            updateLogging(logEntity, DatasetParseStatusEnum.FAILED, e.getMessage());
//        }
//    }
//
//    /**
//     * 开始记录任务日志的方法。
//     * 该方法会创建一个新的数据集解析日志记录，设置初始状态为已开始，
//     * 并将其保存到数据库中，最后返回该日志记录对象。
//     *
//     * @return 新创建的数据集解析日志记录对象
//     */
//    private DatasetParseLogEntity startLogging() {
//        DatasetParseLogEntity logEntity = new DatasetParseLogEntity();
//        logEntity.setThreadName(Thread.currentThread().getName());
//        logEntity.setStartTime(new Date());
//        logEntity.setStatus(DatasetParseStatusEnum.STARTED.getCode());
//        datasetParseLogService.save(logEntity);
//        return logEntity;
//    }
//
//    /**
//     * 更新任务日志状态（无错误信息）的方法。
//     * 该方法会更新指定的数据集解析日志记录的结束时间和状态，
//     * 并将更新后的记录保存到数据库中。
//     *
//     * @param logEntity 要更新的数据集解析日志记录对象
//     * @param status    要更新的状态枚举
//     */
//    private void updateLogging(DatasetParseLogEntity logEntity, DatasetParseStatusEnum status) {
//        updateLogging(logEntity, status, null);
//    }
//
//    /**
//     * 更新任务日志状态（包含错误信息）的方法。
//     * 该方法会更新指定的数据集解析日志记录的结束时间、状态和错误信息，
//     * 并将更新后的记录保存到数据库中。
//     *
//     * @param logEntity  要更新的数据集解析日志记录对象
//     * @param status     要更新的状态枚举
//     * @param errorMessage 错误信息，如果没有错误则为 null
//     */
//    private void updateLogging(DatasetParseLogEntity logEntity, DatasetParseStatusEnum status, String errorMessage) {
//        logEntity.setEndTime(new Date());
//        logEntity.setStatus(status.getCode());
//        logEntity.setErrorMessage(errorMessage);
//        datasetParseLogService.updateById(logEntity);
//    }
//}