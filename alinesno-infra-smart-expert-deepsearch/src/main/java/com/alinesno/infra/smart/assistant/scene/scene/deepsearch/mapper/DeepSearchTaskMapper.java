package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 数据分析规划 Mapper 接口
 */
@Mapper
public interface DeepSearchTaskMapper extends IBaseMapper<DeepSearchTaskEntity> {

    /**
     * 更新任务名称和描述
     * @param title
     * @param desc
     * @param id
     */
    @Update("UPDATE `deep_search_task` " +
            "SET `task_name` = #{title}, `task_description` = #{desc} " +
            "WHERE `id` = #{id}")
    void updateTitleAndDescById(@Param("title") String title,
                                @Param("desc") String desc,
                                @Param("id") Long id);

}