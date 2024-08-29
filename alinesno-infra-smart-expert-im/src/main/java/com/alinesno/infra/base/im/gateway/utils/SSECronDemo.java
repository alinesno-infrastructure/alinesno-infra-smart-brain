package com.alinesno.infra.base.im.gateway.utils;//package com.alinesno.infra.base.im.gateway.utils;
//
//import com.alinesno.infra.base.im.service.ITaskService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * 示例数据
// */
//@Slf4j
//@Component
//public class SSECronDemo {
//
//    @Autowired
//    private ITaskService taskService ;
//
//    @Autowired
//    private RedisStreamPushUtils redisStreamPushUtils ;
//
//    private final static ObjectMapper mapper = new ObjectMapper();
//
////    /**
////     * 消息定时写回前端项目配置
////     */
////    @SneakyThrows
////    @Scheduled(cron ="*/2 * * * * ?")
////    public void getNoticeMessage() {
////        List<ChatMessageDto> chatMessageDtos = taskService.getTaskMessage() ;
////        redisStreamPushUtils.pushMessage(mapper.writeValueAsString(chatMessageDtos));
////    }
//
////    /**
////     * 任务运行的示例
////     */
////    @SneakyThrows
////    @Scheduled(cron ="*/5 * * * * ?")
////    public void getTaskNoticeMessage() {
////
////        String [] chineseContentArr = {
////                "这是随机生成的测试数据内容，用于模拟新的任务信息。详细的分析包括各种指标和数据的可视化呈现。我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈,我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈",
////                "业务拓展战略规划遇到了一些意料之外的挑战，详细的分析包括各种指标和数据的可视化呈",
////                "在分析财务数据时，我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈,我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈",
////                "业务拓展战略规划遇到了一些意料之外的挑战，我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈呈需要全面重新评估市场情况、资源配置和风险缓解策略。",
////                "在分析财务数据时，我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈,我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈我们发现了一些有价值的趋势和见解，这将对决策过程产生重要影响。详细的分析包括各种指标和数据的可视化呈现。"
////        };
////
////        List<String> chineseContent = Lists.newArrayList(chineseContentArr) ;
////
////        String[] statusOptions = {"pending", "completed", "failed"};
////        String[] taskType = {"1", "4", "5"};
////        TableItem tableItem = getTableItem(chineseContent, statusOptions, taskType);
////
////        redisStreamPushUtils.pushTask(mapper.writeValueAsString(tableItem));
////    }
////
////    @NotNull
////    private static TableItem getTableItem(List<String> chineseContent, String[] statusOptions, String[] taskType) {
////        String[] taskNameOptions = {"财务报告数据分析", "业务拓展战略规划", "产品研发进度跟踪", "市场营销活动策划", "人力资源招聘计划"};
////
////        Random random = new Random();
////        int randomIndex = random.nextInt(chineseContent.size());
////        return new TableItem(
////                statusOptions[random.nextInt(statusOptions.length)],
////                "B00" + (randomIndex + 1),
////                taskType[random.nextInt(taskType.length)],
////                taskNameOptions[random.nextInt(taskNameOptions.length)],
////                chineseContent.get(random.nextInt(chineseContent.size())),
////                (random.nextInt(10) + 1) + "秒"
////        );
////    }
////
////    @ToString
////    @Data
////    private static class TableItem {
////        private String taskStatus;
////        private String businessId;
////        private String taskType;
////        private String taskName;
////        private String assistantContent;
////        private String usageTime;
////
////        public TableItem(String taskStatus, String businessId, String taskType, String taskName, String assistantContent, String usageTime) {
////            this.taskStatus = taskStatus;
////            this.businessId = businessId;
////            this.taskType = taskType;
////            this.taskName = taskName;
////            this.assistantContent = assistantContent;
////            this.usageTime = usageTime;
////        }
////    }
//
//}
