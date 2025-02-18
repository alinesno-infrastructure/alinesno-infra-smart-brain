package com.alinesno.infra.base.search.api;

import lombok.Data;

import java.io.Serializable;

/**
 * 搜索请求的数据传输对象
 *
 * 此类用于API层的搜索请求，包含了进行搜索所需的基本参数
 */
@Data
public class SearchRequestDto implements Serializable {

    /**
     * 索引基础名称
     *
     * 用于指定搜索操作所属的索引基础
     */
    private String indexBase;

    /**
     * 字段名称
     */
    private String fieldName ;

    /**
     * 索引类型
     *
     * 用于指定搜索操作的具体类型
     */
    private String indexType;

    /**
     * 查询文本
     *
     * 用户输入的搜索关键词或查询文本
     */
    private String queryText;

    /**
     * 当前页码
     *
     * 用于分页显示搜索结果，表示用户正在查看的页码
     */
    private int currentPage;

    /**
     * 页面大小
     *
     * 每页显示的搜索结果数量
     */
    private int pageSize;

}
