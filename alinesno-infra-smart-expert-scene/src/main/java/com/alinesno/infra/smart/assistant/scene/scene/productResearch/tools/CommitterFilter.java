//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.CommitterInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Predicate;
//
///**
// * 提交者信息过滤器
// */
//public class CommitterFilter {
//    private static final List<Predicate<String>> emailFilters = new ArrayList<>();
//    private static final List<Predicate<String>> nameFilters =  new ArrayList<>();
//
//    static {
//        initDefaultEmailFilters();
//        initDefaultNameFilters();
//    }
//
//    private static void initDefaultEmailFilters() {
//        // GitHub相关无回复邮箱
//        addEmailFilter(email -> email.endsWith("@users.noreply.github.com"));
//        addEmailFilter(email -> email.endsWith("@noreply.github.com"));
//        addEmailFilter("noreply@github.com"::equalsIgnoreCase);
//
//        // 通用无回复邮箱模式
//        addEmailFilter(email -> email.contains("noreply@"));
//        addEmailFilter(email -> email.contains("no-reply@"));
//
//        // CI/CD和自动化工具
//        addEmailFilter(email -> email.endsWith("@githubactions.com"));
//        addEmailFilter(email -> email.endsWith("@actions.github.com"));
//        addEmailFilter(email -> email.endsWith("@jenkins.local"));
//
//        // 机器人账户
//        addEmailFilter(email -> email.contains("[bot]@"));
//        addEmailFilter(email -> email.contains("-bot@"));
//        addEmailFilter(email -> email.contains("_bot@"));
//        addEmailFilter(email -> email.endsWith("@bot.com"));
//        addEmailFilter(email -> email.startsWith("bot@"));
//
//        // 本地或测试邮箱
//        addEmailFilter(email -> email.contains("@local"));
//        addEmailFilter(email -> email.contains("@example.com"));
//        addEmailFilter(email -> email.endsWith(".local"));
//        addEmailFilter(email -> email.startsWith("local@"));
//        addEmailFilter(email -> email.startsWith("test@"));
//
//        // 无效邮箱格式
//        addEmailFilter(email -> !email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$"));
//    }
//
//    private static void initDefaultNameFilters() {
//        // 过滤系统用户
//        addNameFilter(name -> name.equalsIgnoreCase("root"));
//        addNameFilter(name -> name.equalsIgnoreCase("admin"));
//        addNameFilter(name -> name.equalsIgnoreCase("system"));
//        addNameFilter(name -> name.equalsIgnoreCase("git"));
//
//        // 过滤机器人账户
//        addNameFilter(name -> name.contains("[bot]"));
//        addNameFilter(name -> name.endsWith("_bot"));
//        addNameFilter(name -> name.endsWith("-bot"));
//        addNameFilter(name -> name.startsWith("bot-"));
//
//        // 过滤测试账户
//        addNameFilter(name -> name.equalsIgnoreCase("test"));
//        addNameFilter(name -> name.equalsIgnoreCase("demo"));
//    }
//
//    public static void addEmailFilter(Predicate<String> filter) {
//        emailFilters.add(filter);
//    }
//
//    public static void addNameFilter(Predicate<String> filter) {
//        nameFilters.add(filter);
//    }
//
//    public static boolean isValidEmail(String email) {
//        if (email == null || email.trim().isEmpty()) {
//            return false;
//        }
//
//        String lowerEmail = email.toLowerCase();
//        return emailFilters.stream().noneMatch(f -> f.test(lowerEmail));
//    }
//
//    public static boolean isValidName(String name) {
//        if (name == null || name.trim().isEmpty()) {
//            return false;
//        }
//
//        return nameFilters.stream().noneMatch(f -> f.test(name));
//    }
//
//    /**
//     * 验证提交者信息是否有效
//     * @param committerInfo 提交者信息
//     * @return 是否有效
//     */
//    public static boolean isValidCommitter(CommitterInfo committerInfo) {
//        return committerInfo != null
//                && isValidName(committerInfo.getName())
//                && isValidEmail(committerInfo.getEmail());
//    }
//
//}
//