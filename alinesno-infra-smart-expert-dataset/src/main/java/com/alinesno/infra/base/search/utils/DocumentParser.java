package com.alinesno.infra.base.search.utils;//package com.alinesno.infra.base.search.utils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 文档解析器工具类，用于对文档内容进行分段处理
// */
//@Deprecated
//public class DocumentParser {
//
//    /**
//     * 解析文档内容并将其分割为指定长度的段落
//     *
//     * @param documentContent   待分割的文档内容
//     * @param maxSegmentLength  每个段落的最大长度
//     * @return 分割后的段落列表
//     */
//    public static List<String> parseDocument(String documentContent, int maxSegmentLength) {
//        List<String> segments = new ArrayList<>();
//        splitDocument(documentContent.trim(), segments, maxSegmentLength); // 去掉文档内容前后的空格
//        return segments;
//    }
//
//    /**
//     * 将文档内容分割为指定长度的段落
//     *
//     * @param documentContent   待分割的文档内容
//     * @param segments          分割后的段落列表
//     * @param maxSegmentLength  每个段落的最大长度
//     */
//    private static void splitDocument(String documentContent, List<String> segments, int maxSegmentLength) {
//        if (documentContent.length() <= maxSegmentLength) {
//            segments.add(documentContent.trim()); // 去掉段落前后的空格
//            return;
//        }
//
//        String segment = documentContent.substring(0, maxSegmentLength);
//        int lastSplitIndex = findLastSplitIndex(segment);
//
//        if (lastSplitIndex != -1) {
//            segments.add(segment.substring(0, lastSplitIndex).trim()); // 去掉段落前后的空格
//            splitDocument(documentContent.substring(lastSplitIndex), segments, maxSegmentLength);
//        } else {
//            segments.add(segment.trim()); // 去掉段落前后的空格
//            splitDocument(documentContent.substring(maxSegmentLength), segments, maxSegmentLength);
//        }
//    }
//
//    /**
//     * 查找文本中的最后一个分割点索引
//     *
//     * @param segment  待查找的文本段落
//     * @return 最后一个分割点的索引
//     */
//    private static int findLastSplitIndex(String segment) {
//        // 定义中文分割符号，根据需要添加更多中文分隔符号
//        String[] splitSymbols = {"。", "！", "？", "；", "……", "，", "："};
//
//        for (String symbol : splitSymbols) {
//            int index = segment.lastIndexOf(symbol);
//            if (index != -1) {
//                return index + symbol.length();
//            }
//        }
//        // 如果没有中文分割符号，则返回最后一个空格的索引
//        return segment.lastIndexOf(" ");
//    }
//}
