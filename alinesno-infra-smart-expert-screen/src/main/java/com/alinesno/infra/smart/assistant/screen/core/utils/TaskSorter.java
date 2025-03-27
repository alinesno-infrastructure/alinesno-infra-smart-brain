//package com.alinesno.infra.smart.assistant.screen.utils;
//
//import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
//
//import java.util.*;
//
//public class TaskSorter {
//
//    public static List<RoleTaskDto> sortTasks(List<RoleTaskDto> tasks) throws Exception {
//        // 分离任务
//        List<RoleTaskDto> withPreRole = tasks.stream().filter(task -> task.getPreRoleId() != null).toList();
//        List<RoleTaskDto> withoutPreRole = tasks.stream().filter(task -> task.getPreRoleId() == null).toList();
//
//        // 构建任务依赖图
//        Map<String, List<RoleTaskDto>> dependencyGraph = new HashMap<>();
//        for (RoleTaskDto task : withPreRole) {
//            dependencyGraph.computeIfAbsent(task.getPreRoleId(), k -> new ArrayList<>()).add(task);
//        }
//
//        // 拓扑排序
//        List<RoleTaskDto> sortedTasks = new ArrayList<>();
//        Set<String> visited = new HashSet<>();
//        Set<String> visiting = new HashSet<>(); // 用于检测循环依赖
//
//        for (RoleTaskDto task : withPreRole) {
//            if (!visited.contains(task.getWorkerRoleId())) {
//                if (!topologicalSort(task, visited, visiting, dependencyGraph, sortedTasks)) {
//                    throw new Exception("Detected cycle in tasks.");
//                }
//            }
//        }
//
//        // 合并结果
//        List<RoleTaskDto> result = new ArrayList<>(sortedTasks);
//        result.addAll(withoutPreRole);
//        return result;
//    }
//
//    private static boolean topologicalSort(
//            RoleTaskDto task,
//            Set<String> visited,
//            Set<String> visiting,
//            Map<String, List<RoleTaskDto>> graph,
//            List<RoleTaskDto> result
//    ) {
//        if (visiting.contains(task.getWorkerRoleId())) {
//            // 发现循环依赖
//            return false;
//        }
//
//        if (visited.contains(task.getWorkerRoleId())) {
//            return true;
//        }
//
//        visiting.add(String.valueOf(task.getWorkerRoleId()));
//
//        if (graph.containsKey(task.getWorkerRoleId())) {
//            for (RoleTaskDto child : graph.get(task.getWorkerRoleId())) {
//                if (!topologicalSort(child, visited, visiting, graph, result)) {
//                    return false;
//                }
//            }
//        }
//
//        visiting.remove(task.getWorkerRoleId());
//        visited.add(String.valueOf(task.getWorkerRoleId()));
//        result.add(task);
//
//        return true;
//    }
//}