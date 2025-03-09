package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * HTTP 接口调用的节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpApiNodeData extends NodeData {

    /**
     * 请求参数列表，每个参数包含键值对
     */
    private List<Param> params;

    /**
     * 请求头列表，每个头信息包含键值对
     */
    private List<Param> headers;

    /**
     * HTTP 请求方法，如 POST、GET 等
     */
    private String method;

    /**
     * 请求体类型，如 text、json 等
     */
    private String bodyType;

    /**
     * 请求的 URL
     */
    private String url;

    /**
     * 内部类，用于表示键值对参数
     */
    @Data
    public static class Param {
        /**
         * 参数的键
         */
        private String key;
        /**
         * 参数的值
         */
        private String value;
    }
}