package com.alinesno.infra.smart.assistant.screen.core.utils;

import com.alinesno.infra.smart.assistant.screen.core.dto.RoleTaskDto;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色任务工具类
 */
public class RoleTaskUtils {

    private static final Map<String, Pair<List<RoleTaskDto>, LocalDateTime>> roleTaskMap = new ConcurrentHashMap<>();

    public static List<RoleTaskDto> getPreTaskList(String uId) {
        // 获取当前时间和任务列表
        LocalDateTime now = LocalDateTime.now();
        Pair<List<RoleTaskDto>, LocalDateTime> pair = roleTaskMap.get(uId);

        if (pair != null) {
            // 检查任务列表是否过期
            Duration duration = Duration.between(pair.getValue(), now);
            long days = duration.toDays();

            if (days >= 1) {
                // 如果超过1天，则从map中移除
                roleTaskMap.remove(uId);
                return new ArrayList<>(); // 或者返回空列表
            } else {
                return pair.getKey();
            }
        }

        return new ArrayList<>(); // 如果没有找到任务列表，或者任务列表已过期
    }

    public static void add(RoleTaskDto task, String uId) {
        roleTaskMap.compute(uId, (key, oldValue) -> {
            List<RoleTaskDto> tasks = (oldValue == null) ? new java.util.ArrayList<>() : oldValue.getKey();

            // 检查任务是否已经存在
            Optional<RoleTaskDto> existingTask = tasks.stream()
                    .filter(t -> t.getId().equals(task.getId()))
                    .findFirst();

            if (existingTask.isEmpty()) {
                tasks.add(task);
            }

            return new Pair<>(tasks, LocalDateTime.now());
        });
    }

    // 帮助类用于存储任务列表及其最后更新时间
    @Data
    private static class Pair<T, U> {
        private final T key;
        private final U value;

        public Pair(T key, U value) {
            this.key = key;
            this.value = value;
        }
    }
}