package com.alinesno.infra.smart.assistant.role.yaml;

import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

public class TableDefinitionBeanExample {

    @SneakyThrows
    public static void main(String[] args) {
        // 创建TableDefinition实例
        TableDefinitionBean tableDefinition = new TableDefinitionBean();

        // 创建customer_login表的详细信息
        TableDefinitionBean.TableDetail customerLoginDetail = new TableDefinitionBean.TableDetail();
        customerLoginDetail.setName("用户登陆信息记录");
        customerLoginDetail.setDesc("主要包括用户名、密码、登陆时间、登陆日期等字段");

        // 创建customer_info表的详细信息
        TableDefinitionBean.TableDetail customerInfoDetail = new TableDefinitionBean.TableDetail();
        customerInfoDetail.setName("用户信息表");
        customerInfoDetail.setDesc("主要用于记录用户的基础信息和后期用户管理使用");

        // 将表详细信息添加到tables字段中
        tableDefinition.setTables(Map.of(
                "customer_login", List.of(customerLoginDetail),
                "customer_info", List.of(customerInfoDetail)
        ));

        // 打印表详细信息
        Map<String, List<TableDefinitionBean.TableDetail>> tables = tableDefinition.getTables();
        System.out.println(YAMLMapper.toYAML(tableDefinition));

        String yamlContent = "tables:\n" +
                "  customer_login:\n" +
                "  - name: \"用户登陆信息记录\"\n" +
                "    desc: \"主要包括用户名、密码、登陆时间、登陆日期等字段\"\n" +
                "  customer_info:\n" +
                "  - name: \"用户信息表\"\n" +
                "    desc: \"主要用于记录用户的基础信息和后期用户管理使用\"" ;

        TableDefinitionBean t1 = YAMLMapper.fromYAML(yamlContent , TableDefinitionBean.class)  ;
        System.out.println(t1);

    }
}
