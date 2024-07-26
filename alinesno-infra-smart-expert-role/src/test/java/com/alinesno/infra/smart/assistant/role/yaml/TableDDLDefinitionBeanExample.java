package com.alinesno.infra.smart.assistant.role.yaml;

import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class TableDDLDefinitionBeanExample {

    @SneakyThrows
    public static void main(String[] args) {
        // 创建学生表定义实例
        TableDDLDefinitionBean studentTable = new TableDDLDefinitionBean();
        studentTable.setTableName("student");
        studentTable.setPrimaryKey("pk_student_id(student_id)");
        studentTable.setEngine("innodb");
        studentTable.setComment("主要用于记录学生的基础信息和后期学生管理使用的学生信息表");

        // 创建学生表列定义实例列表
        List<TableDDLDefinitionBean.ColumnDefinition> studentColumns = new ArrayList<>();
        studentColumns.add(createColumn("student_id", "INT UNSIGNED", true, false, "学生ID"));
        studentColumns.add(createColumn("student_name", "VARCHAR(50)", false, false, "学生姓名"));
        studentColumns.add(createColumn("gender", "ENUM('男', '女')", false, false, "学生性别"));
        studentColumns.add(createColumn("birthday", "DATE", false, false, "学生生日"));
        studentColumns.add(createColumn("address", "VARCHAR(100)", false, true, "学生地址"));
        studentColumns.add(createColumn("phone", "VARCHAR(20)", false, true, "学生电话"));

        // 设置学生表列定义列表
        studentTable.setColumns(studentColumns);

        // 打印学生表定义信息
        System.out.println("学生表定义:");
        System.out.println(YAMLMapper.toYAML(studentTable));

        // 创建课程表定义实例
        TableDDLDefinitionBean courseTable = new TableDDLDefinitionBean();
        courseTable.setTableName("course");
        courseTable.setPrimaryKey("pk_courseid(course_id)");
        courseTable.setEngine("innodb");
        courseTable.setComment("主要用于记录课程的基础信息和后期课程管理使用");

        // 创建课程表列定义实例列表
        List<TableDDLDefinitionBean.ColumnDefinition> courseColumns = new ArrayList<>();
        courseColumns.add(createColumn("course_id", "INT UNSIGNED", true, false, "课程ID"));
        courseColumns.add(createColumn("course_name", "VARCHAR(50)", false, false, "课程名称"));
        courseColumns.add(createColumn("course_desc", "TEXT", true, true, "课程描述"));
        courseColumns.add(createColumn("course_teacher", "VARCHAR(50)", false, false, "授课教师"));
        courseColumns.add(createColumn("course_credit", "DECIMAL(3, 1)", false, false, "学分"));
        courseColumns.add(createColumn("created_time", "TIMESTAMP", false, false, "创建时间"));

        // 设置课程表列定义列表
        courseTable.setColumns(courseColumns);

        // 打印课程表定义信息
        System.out.println("\n课程表定义:");
        System.out.println(YAMLMapper.toYAML(courseTable));
//        printTableDDLDefinition(courseTable);

        String yamlContent = "# 表名称，用于记录学生选课的信息\n" +
                "tableName: \"course_selection\"\n" +
                "\n" +
                "# 列定义\n" +
                "columns:\n" +
                "  - name: \"student_id\"  # 字段名称，学生ID\n" +
                "    type: \"INT UNSIGNED\"  # 字段类型，无符号整数\n" +
                "    autoIncrement: false  # 是否自增，当前设置为不自增\n" +
                "    comment: \"学生ID，用于关联学生信息\"\n" +
                "    defaultVal: null  # 默认值，当前设置为null\n" +
                "    \"null\": false  # 是否允许为null，当前设置为不允许\n" +
                "\n" +
                "  - name: \"course_id\"  # 字段名称，课程ID\n" +
                "    type: \"INT UNSIGNED\"  # 字段类型，无符号整数\n" +
                "    autoIncrement: false  # 是否自增，当前设置为不自增\n" +
                "    comment: \"课程ID，用于关联课程信息\"\n" +
                "    defaultVal: null  # 默认值，当前设置为null\n" +
                "    \"null\": false  # 是否允许为null，当前设置为不允许\n" +
                "\n" +
                "  - name: \"selection_time\"  # 字段名称，选课时间\n" +
                "    type: \"DATETIME\"  # 字段类型，日期时间类型\n" +
                "    autoIncrement: false  # 是否自增，当前设置为不自增\n" +
                "    comment: \"选课时间，记录学生选课的时间\"\n" +
                "    defaultVal: null  # 默认值，当前设置为null\n" +
                "    \"null\": false  # 是否允许为null，当前设置为不允许\n" +
                "\n" +
                "# 主键定义\n" +
                "primaryKey: \"pk_student_course(student_id, course_id)\"\n" +
                "\n" +
                "# 外键定义\n" +
                "foreignKeys:\n" +
                "  - name: \"fk_student_id\"  # 外键名称，学生ID\n" +
                "    columns: \"student_id\"  # 外键关联的字段，学生ID\n" +
                "    referenceTable: \"student\"  # 外键关联的表，学生信息表\n" +
                "    referenceColumns: \"student_id\"  # 外键关联的字段，学生ID\n" +
                "\n" +
                "  - name: \"fk_course_id\"  # 外键名称，课程ID\n" +
                "    columns: \"course_id\"  # 外键关联的字段，课程ID\n" +
                "    referenceTable: \"course\"  # 外键关联的表，课程信息表\n" +
                "    referenceColumns: \"course_id\"  # 外键关联的字段，课程ID\n" +
                "\n" +
                "# 表引擎类型\n" +
                "engine: \"innodb\"\n" +
                "\n" +
                "# 表注释，描述表的用途\n" +
                "comment: \"主要用于记录学生选课的信息，包括学生ID、课程ID、选课时间等字段\"" ;

        TableDDLDefinitionBean tables = YAMLMapper.fromYAML(yamlContent , TableDDLDefinitionBean.class) ;
        System.out.println(tables);
    }

    // 辅助方法，创建列定义实例
    private static TableDDLDefinitionBean.ColumnDefinition createColumn(
            String name, String type, boolean autoIncrement, boolean isNull, String comment) {
        TableDDLDefinitionBean.ColumnDefinition column = new TableDDLDefinitionBean.ColumnDefinition();
        column.setName(name);
        column.setType(type);
        column.setAutoIncrement(autoIncrement);
        column.setNull(isNull);
        column.setComment(comment);
        return column;
    }

    // 辅助方法，打印表定义信息
    private static void printTableDDLDefinition(TableDDLDefinitionBean table) {
        System.out.println("表名: " + table.getTableName());
        System.out.println("主键: " + table.getPrimaryKey());
        System.out.println("存储引擎: " + table.getEngine());
        System.out.println("注释: " + table.getComment());

        // 打印列定义信息
        List<TableDDLDefinitionBean.ColumnDefinition> columns = table.getColumns();
        for (TableDDLDefinitionBean.ColumnDefinition column : columns) {
            System.out.println("  列名: " + column.getName());
            System.out.println("    类型: " + column.getType());
            System.out.println("    自增: " + column.isAutoIncrement());
            System.out.println("    允许为空: " + column.isNull());
            System.out.println("    注释: " + column.getComment());
        }
    }
}
