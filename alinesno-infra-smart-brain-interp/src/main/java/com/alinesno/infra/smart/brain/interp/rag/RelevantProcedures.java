//package com.alinesno.infra.smart.brain.interp.rag;
//
//import com.alinesno.infra.smart.brain.interp.core.Interpreter;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class RelevantProcedures {
//
//    public static String getRelevantProceduresString(Interpreter interpreter) {
//        // 获取相关的过程字符串
//
//        // 如果downloadOpenProcedures为true且interpreter.procedures为null，
//        // 则下载过程库
////        if (interpreter.procedures == null && interpreter.downloadOpenProcedures && !interpreter.local) {
////            // 从Github获取Open Procedures
////            String url = "https://raw.githubusercontent.com/KillianLucas/open-procedures/main/procedures_db.json";
////            String response = requests.get(url);
////            interpreter.proceduresDb = response.json();
////            interpreter.procedures = interpreter.proceduresDb.keySet();
////        }
//
//        // 更新过程库以反映interpreter.procedures的任何更改
////        if (!interpreter.proceduresDb.keySet().equals(interpreter.procedures)) {
////            Map<String, Object> updatedProceduresDb = new HashMap<>();
////            for (String key : interpreter.procedures) {
////                if (interpreter.proceduresDb.containsKey(key)) {
////                    updatedProceduresDb.put(key, interpreter.proceduresDb.get(key));
////                } else {
////                    updatedProceduresDb.put(key, interpreter.embedFunction(key));
////                }
////            }
////            interpreter.proceduresDb = updatedProceduresDb;
////        }
//
//        // 组装过程查询字符串，最后两条消息
//        String queryString = "";
//        List<Object> messages = interpreter.getMessages();
//        int startIndex = Math.max(0, messages.size() - 2);
//        for (int i = startIndex; i < messages.size(); i++) {
//            Map<String, Object> message = (Map<String, Object>) messages.get(i);
//            if (message.containsKey("content")) {
//                queryString += "\n" + message.get("content");
//            }
//            if (message.containsKey("code")) {
//                queryString += "\n" + message.get("code");
//            }
//            if (message.containsKey("output")) {
//                queryString += "\n" + message.get("output");
//            }
//        }
//        queryString = queryString.substring(Math.max(0, queryString.length() - 3000)).trim();
//
//        int numResults = interpreter.numProcedures;
//
//        List<Object> relevantProcedures = search(queryString, interpreter.proceduresDb, interpreter::embedFunction, numResults);
//
//        // 这部分可以改进。有些过程应该是“固定”的...
//        String relevantProceduresString = "[Recommended Procedures]\n" + String.join("\n---\n", relevantProcedures) + "\nIn your plan, include steps and, if present, **EXACT CODE SNIPPETS** (especially for deprecation notices, **WRITE THEM INTO YOUR PLAN -- underneath each numbered step** as they will VANISH once you execute your first line of code, so WRITE THEM DOWN NOW if you need them) from the above procedures if they are relevant to the task. Again, include **VERBATIM CODE SNIPPETS** from the procedures above if they are relevent to the task **directly in your plan.**";
//
//        if (interpreter.debugMode) {
//            System.out.println("Generated relevantProceduresString: " + relevantProceduresString);
//        }
//
//        return relevantProceduresString;
//    }
//
//    public static List<Object> search(String query, Map<String, Object> db, EmbedFunction embedFunction, int numResults) {
//        // 查找与查询最相似的嵌入值
//
//        // 将查询转换为嵌入值
//        Object queryEmbedding = embedFunction.embed(query);
//
//        // 计算查询嵌入值与数据库中每个嵌入值之间的余弦距离
//        Map<Object, Double> distances = new HashMap<>();
//        for (Map.Entry<String, Object> entry : db.entrySet()) {
//            Object value = entry.getKey();
//            Object embedding = entry.getValue();
//            double distance = cosine(queryEmbedding, embedding);
//            distances.put(value, distance);
//        }
//
//        // 按距离排序值，并选择前numResults个
//        List<Object> mostSimilarValues = distances.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue())
//                .limit(numResults)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        // 返回最相似的值
//        return mostSimilarValues;
//    }
//
//    private static double cosine(Object embedding1, Object embedding2) {
//        // 计算余弦距离
//        // 在这里添加计算逻辑
//        return 0.0;
//    }
//
//    private interface EmbedFunction {
//        Object embed(String text);
//    }
//
////    private static class Interpreter {
////        private List<Object> messages;
////        private boolean downloadOpenProcedures;
////        private boolean local;
////        private Object procedures;
////        private Object proceduresDb;
////        private int numProcedures;
////        private boolean debugMode;
////
////        public Interpreter(List<Object> messages, boolean downloadOpenProcedures, boolean local, Object procedures, Object proceduresDb, int numProcedures, boolean debugMode) {
////            this.messages = messages;
////            this.downloadOpenProcedures = downloadOpenProcedures;
////            this.local = local;
////            this.procedures = procedures;
////            this.proceduresDb = proceduresDb;
////            this.numProcedures = numProcedures;
////            this.debugMode = debugMode;
////        }
////
////        public List<Object> getMessages() {
////            return messages;
////        }
////
////        public Object embedFunction(String text) {
////            // 嵌入函数的实现
////            // 在这里添加嵌入逻辑
////            return null;
////        }
////    }
//}
