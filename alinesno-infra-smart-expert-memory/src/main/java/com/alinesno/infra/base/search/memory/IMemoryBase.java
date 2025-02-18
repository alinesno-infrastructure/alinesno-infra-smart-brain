package com.alinesno.infra.base.search.memory;

import java.util.List;
import java.util.Map;

/**
 * 定义了记忆操作的基本接口。
 */
public interface IMemoryBase {

    /**
     * 根据ID检索记忆。
     *
     * @param memoryId 记忆的唯一标识符。
     * @return 返回检索到的记忆对象。
     */
    Map<String, Object> get(String memoryId);

    /**
     * 列出所有记忆。
     *
     * @return 包含所有记忆对象的列表。
     */
    List<Map<String, Object>> getAll();

    /**
     * 根据ID更新记忆。
     *
     * @param memoryId 需要更新的记忆的唯一标识符。
     * @param data     包含更新信息的数据映射。
     * @return 返回更新后的记忆对象。
     */
    Map<String, Object> update(String memoryId, Map<String, Object> data);

    /**
     * 根据ID删除记忆。
     *
     * @param memoryId 需要删除的记忆的唯一标识符。
     */
    void delete(String memoryId);

    /**
     * 获取指定记忆的历史变更记录。
     *
     * @param memoryId 需要获取历史的记忆的唯一标识符。
     * @return 包含该记忆所有变更记录的列表。
     */
    List<Map<String, Object>> history(String memoryId);

}