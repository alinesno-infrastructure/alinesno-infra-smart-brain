package com.alinesno.infra.smart.brain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

public class YamlToJsonConverter {

    public static String convertYamlToJson(String yamlString) {
        Yaml yaml = new Yaml();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 解析 YAML 字符串为 Java 对象
            Object yamlObject = yaml.load(yamlString);

            // 将 Java 对象转换为 JSON 字符串
            return objectMapper.writeValueAsString(yamlObject);
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String yamlContent = "- title: 什么是微服务架构的特点？\n  desc: 考察对微服务架构的理解和特点的掌握程度\n  type: single_choice\n  score: 10\n  answers: \n    - A: 高度自治\n    - B: 异构性\n    - C: 松耦合\n    - D: 高可用性\n  rightAnswer: A|B|C\n\n- title: 微服务架构中常用的服务注册与发现组件是什么？\n  desc: 考察对微服务架构中服务注册与发现的了解\n  type: single_choice\n  score: 10\n  answers: \n    - A: Apache Kafka\n    - B: Apache ZooKeeper\n    - C: Nginx\n    - D: RabbitMQ\n  rightAnswer: B\n\n- title: 微服务架构中的服务之间通信常用的协议是什么？\n  desc: 考察对微服务架构中服务通信协议的了解\n  type: single_choice\n  score: 10\n  answers: \n    - A: REST\n    - B: SOAP\n    - C: gRPC\n    - D: GraphQL\n  rightAnswer: A|C\n\n- title: 微服务架构中的服务拆分原则是什么？\n  desc: 考察对微服务架构中服务拆分原则的了解\n  type: single_choice\n  score: 10\n  answers: \n    - A: 单一职责原则\n    - B: 开闭原则\n    - C: 接口隔离原则\n    - D: 服务自治原则\n  rightAnswer: A|B|C\n\n- title: 微服务架构中的服务网关的作用是什么？\n  desc: 考察对微服务架构中服务网关的理解\n  type: single_choice\n  score: 10\n  answers: \n    - A: 负载均衡\n    - B: 安全认证\n    - C: 服务路由\n    - D: 服务监控\n  rightAnswer: A|B|C\n\n- title: 微服务架构中的服务间通信方式有哪些？\n  desc: 考察对微服务架构中服务通信方式的了解\n  type: multiple_choice\n  score: 10\n  answers: \n    - A: 同步通信\n    - B: 异步通信\n    - C: 消息队列\n    - D: 数据库连接\n  rightAnswer: A|B|C\n\n- title: 微服务架构中的服务监控常用的工具有哪些？\n  desc: 考察对微服务架构中服务监控工具的了解\n  type: multiple_choice\n  score: 10\n  answers: \n    - A: Prometheus\n    - B: Grafana\n    - C: ELK Stack\n    - D: Zipkin\n  rightAnswer: A|B|C|D\n\n- title: 微服务架构中的服务容错机制有哪些？\n  desc: 考察对微服务架构中服务容错机制的了解\n  type: fill_in_the_blank\n  score: 10\n  answers: 服务熔断|服务降级|服务限流\n\n- title: 微服务架构中的服务部署方式有哪些？\n  desc: 考察对微服务架构中服务部署方式的了解\n  type: fill_in_the_blank\n  score: 10\n  answers: Docker|Kubernetes\n\n- title: 微服务架构中的服务注册与发现的作用是什么？\n  desc: 考察对微服务架构中服务注册与发现的理解\n  type: fill_in_the_blank\n  score: 10\n  answers: 服务的自动发现和负载均衡" ;
        String jsonResult = convertYamlToJson(yamlContent);

        if (jsonResult != null) {
            System.out.println("YAML to JSON conversion result:");
            System.out.println(jsonResult);
        } else {
            System.out.println("Failed to convert YAML to JSON.");
        }
    }
}
