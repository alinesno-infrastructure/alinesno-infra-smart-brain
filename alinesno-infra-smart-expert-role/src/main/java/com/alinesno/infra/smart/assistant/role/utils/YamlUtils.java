package com.alinesno.infra.smart.assistant.role.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 合并yaml
 *
 */
public final class YamlUtils {
    /**
     * 行内注释
     */
    private static final String YAML_SUFFIX_COMMENT = " #";

    /**
     * 数组前缀
     */
    private static final String YAML_ARRAY_SUFFIX = "- ";

    /**
     * yaml可以分割
     */
    private static final String YAML_KEY_SPLIT = ":";

    /**
     * 换行
     */
    private static final String REGEX_ENTER = "\n";

    /**
     * 匹配值只包含制表符和空格
     */
    private static final String REGEX_TABLE_SPACE = ".*\\S.*";

    /**
     * 整行注释
     */
    private static final String REGEX_ROW_COMMENT = "^\s*#";

    /**
     * 数组注释
     */
    private static final String REGEX_ROW_ARRAY_COMMENT = "^\s*-";

    /**
     * YamlKey
     */
    private static final String REGEX_YAML_KEY = "(\\w+):";

    /**
     * YamlKey级别
     */
    private static final String REGEX_YAML_KEY_LEVEL = "(\s*)(\\S+)";

    /**
     * 合并文件,，并保留注释
     *
     * @param yamls yaml文件
     * @return 返回合并后的yaml
     */
    public static String mergedYaml(String... yamls) {
        Map<String, Object> merged = new LinkedHashMap<>();
        Map<CommentKey, List<CommentValue>> commentMap = new LinkedHashMap<>();
        for (String yaml : yamls) {
            commentMap.putAll(readYaml(yaml));
            merged.putAll(new Yaml().load(yaml));
        }

        // 设置输出格式选项，包括空格和注释
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        options.setWidth(120);
        options.setProcessComments(true);
        String dump = new Yaml(options).dump(merged);
        return mergedComment(commentMap, dump);
    }

    /**
     * 合并yaml和注释
     *
     * @param commentMap  注释map
     * @param yamlContext yaml内容
     * @return 返回合并后的yuml
     */
    private static String mergedComment(Map<CommentKey, List<CommentValue>> commentMap, String yamlContext) {
        // 源数据
        StringBuilder sourceData = new StringBuilder();
        String[] yamlArray = yamlContext.split(REGEX_ENTER);

        String allKey = "";
        for (String rowYaml : yamlArray) {
            // 正则前面有多少空格,也就是key层级
            Pattern r = Pattern.compile(REGEX_YAML_KEY_LEVEL);
            Matcher m = r.matcher(rowYaml);
            int level = 0;
            if (m.find()) {
                level = (m.group(1).length() + 1) / 2;
            }

            if (level == 0) {
                allKey = "";
            }

            // 获取yaml的key
            Pattern pattern = Pattern.compile(REGEX_YAML_KEY);
            Matcher matcher = pattern.matcher(rowYaml);

            String rowKey = "";
            // 判断是否是普通key还是数组元素
            if (matcher.find()) {
                rowKey = matcher.group(1);
                if (StringUtils.isNotBlank(allKey)) {
                    allKey = allKey + YAML_KEY_SPLIT + rowKey;
                } else {
                    allKey = rowKey;
                }
                rowKey = allKey;
            }

            // 获取yaml的数组key
            Pattern pattern2 = Pattern.compile(REGEX_ROW_ARRAY_COMMENT);
            Matcher matcher2 = pattern2.matcher(rowYaml);
            if (matcher2.find()) {
                rowKey = allKey + YAML_KEY_SPLIT + rowYaml.substring(rowYaml.indexOf(YAML_ARRAY_SUFFIX) + 2);
            }
            List<CommentValue> commentValues = new ArrayList<>();
            for (CommentKey commentKey : commentMap.keySet()) {
                if (commentKey.allKey.equals(rowKey)) {
                    commentValues.addAll(commentMap.get(commentKey));
                }
            }

            // 上
            StringBuilder up = new StringBuilder();
            // 后
            StringBuilder suffix = new StringBuilder();
            // 下
            StringBuilder sourceDown = new StringBuilder();
            for (CommentValue commentValue : commentValues) {
                if (commentValue.comment.isEmpty()) {
                    continue;
                }
                if (LocationEnum.UP.equals(commentValue.getLocation())) {
                    up.append(commentValue.comment).append("\n");
                }
                if (LocationEnum.SUFFIX.equals(commentValue.getLocation())) {
                    suffix.append(commentValue.comment);
                }
                if (LocationEnum.DOWN.equals(commentValue.getLocation())) {
                    sourceDown.append(commentValue.comment).append("\n");
                }
            }
            if (!up.isEmpty()) {
                sourceData.append(up);
            }
            sourceData.append(rowYaml);
            if (!suffix.isEmpty()) {
                sourceData.append(suffix);
            }
            sourceData.append("\n");
            if (!sourceDown.isEmpty()) {
                sourceData.append(sourceDown);
            }
        }
        return sourceData.toString();
    }

    /**
     * 读取Yaml
     *
     * @param yamlContext Yaml内容
     * @return 返回封装
     */
    private static Map<CommentKey, List<CommentValue>> readYaml(String yamlContext) {
        // 注释key信息
        List<CommentKey> commentKeys = new ArrayList<>();
        // 注释列表
        List<CommentValue> comments = new ArrayList<>();
        // 注释Map
        Map<CommentKey, List<CommentValue>> commentMap = new LinkedHashMap<>();

        // 按换行符拆分
        String[] yamlArray = yamlContext.split(REGEX_ENTER);
        for (String rowYaml : yamlArray) {
            // 过滤空行
            // 正则表达式检查是否有非空格字符
            if (!rowYaml.matches(REGEX_TABLE_SPACE)) {
                continue;
            }
            Pattern rowCommentPattern = Pattern.compile(REGEX_ROW_COMMENT);
            Matcher rowCommentMatcher = rowCommentPattern.matcher(rowYaml);
            // 匹配是否是一整行注释
            if (rowCommentMatcher.find()) {
                comments.add(CommentValue.buildUp(rowYaml));
            } else {
                // 如果不是整行注释，判断是否有行内注释
                // TODO 可以优化成把前面所有的空格都分割下来
                if (rowYaml.contains(YAML_SUFFIX_COMMENT)) {
                    comments.add(CommentValue.buildSuffix(rowYaml.substring(rowYaml.indexOf(YAML_SUFFIX_COMMENT))));
                }

                // 正则前面有多少空格,也就是key层级
                Pattern r = Pattern.compile(REGEX_YAML_KEY_LEVEL);
                Matcher m = r.matcher(rowYaml);
                int level = 0;
                if (m.find()) {
                    level = (m.group(1).length() + 1) / 2;
                }

                // 获取yaml的key
                Pattern pattern = Pattern.compile(REGEX_YAML_KEY);
                Matcher matcher = pattern.matcher(rowYaml);

                // 判断是否是普通key还是数组元素
                if (matcher.find()) {
                    String rowKey = matcher.group(1);
                    CommentKey commentKey = CommentKey.build(level, rowKey, commentKeys);
                    commentKeys.add(commentKey);
                    commentMap.put(commentKey, comments);
                }
                // 获取yaml的数组key
                Pattern pattern2 = Pattern.compile(REGEX_ROW_ARRAY_COMMENT);
                Matcher matcher2 = pattern2.matcher(rowYaml);
                if (matcher2.find()) {
                    // 获取key
                    String rowKey = rowYaml.substring(rowYaml.indexOf(YAML_ARRAY_SUFFIX) + 2, rowYaml.indexOf(YAML_SUFFIX_COMMENT));
                    CommentKey commentKey = CommentKey.build(level, rowKey, commentKeys);
                    commentKeys.add(commentKey);
                    commentMap.put(commentKey, comments);
                }
                comments = new ArrayList<>();
            }
        }
        return commentMap;
    }

    /**
     * List to string
     * @param resultMap
     * @return
     */
    @SneakyThrows
    public static String mergedYamlList(List<String> resultMap) {

        System.out.println("---->>>>>>>>> \r\n" + YAMLMapper.toYAML(resultMap));

        StringBuilder content = new StringBuilder() ;

        if(!resultMap.isEmpty()){
            int size = resultMap.size() ;
            for(int i = 0 ; i < resultMap.size() ; i ++){
               String str = resultMap.get(i) ;
               content.append(str) ;

               if(size > 1 && i < size - 1){
                   content.append("\r\n");
                   content.append("----") ;
                   content.append("\r\n");
               }
            }
        }

        return content.toString() ;
    }

    /**
     * 注释key
     */
    @Data
    private static class CommentKey {
        /**
         * 层级级别
         */
        private int level;
        /**
         * 当前key
         */
        private String key;
        /**
         * 全key
         */
        private String allKey;

        private CommentKey() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CommentKey that = (CommentKey) o;
            return level == that.level && Objects.equals(key, that.key) && Objects.equals(allKey, that.allKey);
        }

        /**
         * 这里重新Hashcode，原因是，lombok的@data注解，会把两个不一样的对象，但是内部值一样的对象的hashcode计算成一样的
         *
         * @return 返回hashCode
         */
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public static CommentKey build(int level, String key, List<CommentKey> commentKeys) {
            CommentKey commentKey = new CommentKey();
            commentKey.level = level;
            commentKey.key = key;
            commentKey.allKey = key;

            // 如果是一级结束
            if (commentKey.level == 0) {
                return commentKey;
            }
            // 如果list是空，结束
            if (commentKeys.isEmpty()) {
                return commentKey;
            }
            for (int i = commentKeys.size() - 1; i >= 0; i--) {
                CommentKey lastKey = commentKeys.get(i);
                if (lastKey.level == commentKey.level - 1) {
                    commentKey.allKey = lastKey.allKey + YAML_KEY_SPLIT + commentKey.allKey;
                    break;
                }
            }
            return commentKey;
        }
    }

    /**
     * 注释值
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CommentValue {
        /**
         * 位置
         */
        private LocationEnum location;
        /**
         * 注释
         */
        private String comment;

        public static CommentValue buildUp(String comment) {
            return new CommentValue(LocationEnum.UP, comment);
        }

        public static CommentValue buildSuffix(String comment) {
            return new CommentValue(LocationEnum.SUFFIX, comment);
        }

        public static CommentValue buildDown(String comment) {
            return new CommentValue(LocationEnum.DOWN, comment);
        }
    }

    /**
     * 方位
     */
    private enum LocationEnum {
        /**
         * 上
         */
        UP,
        /**
         * 后追加
         */
        SUFFIX,
        /**
         * 下
         */
        DOWN,
    }
}
