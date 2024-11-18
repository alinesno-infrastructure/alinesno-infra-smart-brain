package com.alinesno.infra.smart.assistant.plugin.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SingleThreadAsyncLoopExample {

    public static void main(String[] args) {
        // 创建一个单线程的线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        // 用于存储Future对象，以收集异步任务的结果
        List<Future<String>> futures = new ArrayList<>();

        // 外部同步循环
        for (int i = 0; i < 10; i++) {
            final int index = i;
            // 提交异步任务
            Future<String> future = executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    // 模拟耗时操作
                    Thread.sleep(1000);
                    return "Task " + index + " completed";
                }
            });
            // 将Future对象添加到列表中
            futures.add(future);
        }

        // 关闭线程池，不再接受新的任务
        executorService.shutdown();

        // 遍历Future列表，获取所有任务的结果
        for (Future<String> future : futures) {
            try {
                // 获取任务结果，如果任务还没有完成，则会阻塞直到任务完成
                String result = future.get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}